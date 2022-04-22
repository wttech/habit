import json
import mitmproxy.http
import os
import re
import uuid
from datetime import datetime
from mitmproxy import ctx
from mitmproxy.connections import ServerConnection

# This regex extracts splits the host header into host and port.
# Handles the edge case of IPv6 addresses containing colons.
# https://bugzilla.mozilla.org/show_bug.cgi?id=45891
parse_host_header = re.compile(r"^(?P<host>[^:]+|\[.+\])(?::(?P<port>\d+))?$")

ROOT_REQUEST_ID_HEADER = 'X-Habit-Root-Request-Id'
REQUEST_ID_HEADER = "X-Habit-Request-Id"

ORIGIN_DOMAIN_ENV = 'ORIGIN_DOMAIN'
EXTERNAL_DOMAIN_ENV = 'EXTERNAL_DOMAIN'

LOGS_ROOT_FOLDER = "/opt/request-persister/logs"

REQUEST_FILE_SUFFIX = "request"
RESPONSE_FILE_SUFFIX = "response"
ERROR_FILE_SUFFIX = "error"

HEADERS_TO_REMOVE = [ROOT_REQUEST_ID_HEADER, REQUEST_ID_HEADER]


class RequestLogger:

    def __init__(self):
        ctx.log.info('Init')

    def load(self, loader):
        ctx.options.keep_host_header = True
        loader.add_option(
            name = "ssl",
            typespec = bool,
            default = False,
            help = "Is SSL",
        )

    def request(self, flow: mitmproxy.http.HTTPFlow):
        self.keep_port(flow.request)
        if ctx.options.ssl:
            ctx.log.info("SSL")
            self.keep_host_header(flow.request)
        if self.is_valid_request(flow.request):
            parent_id = self.get_parent_request_id(flow.request)
            self.update_request_id(flow.request)
            self.log_request(flow.request, parent_id)

    def response(self, flow: mitmproxy.http.HTTPFlow):
        if self.is_valid_request(flow.request):
            self.log_response(flow.request, flow.response)

    def error(self, flow: mitmproxy.http.HTTPFlow):
        self.log_error(flow.request, flow.error.msg)

    def serverconnect(self, connection: ServerConnection):
        origin_domain = os.environ.get(ORIGIN_DOMAIN_ENV)
        port = int(os.getenv("PORT"))
        new_address = (origin_domain, port)
        connection.address = new_address
        ctx.log.info(connection.address[0])
        ctx.log.info(str(connection.address[1]))

    def is_valid_request(self, request: mitmproxy.http.HTTPRequest):
        return ROOT_REQUEST_ID_HEADER in request.headers

    def get_parent_request_id(self, request: mitmproxy.http.HTTPRequest):
        parent_header_name = REQUEST_ID_HEADER if REQUEST_ID_HEADER in request.headers else ROOT_REQUEST_ID_HEADER
        return request.headers[parent_header_name]

    def update_request_id(self, request: mitmproxy.http.HTTPRequest):
        request.headers[REQUEST_ID_HEADER] = str(uuid.uuid4())

    def keep_host_header(self, request: mitmproxy.http.HTTPRequest):
        m = parse_host_header.match(request.host_header)
        if m:
            request.host = m.group("host").strip("[]")
            if m.group("port"):
                request.port = int(m.group("port"))
        request.host = request.host_header

    def keep_port(self, request: mitmproxy.http.HTTPRequest):
        ctx.log.info(request.host_header)
        m = parse_host_header.match(request.host_header)
        if m:
            if m.group("port"):
                request.port = int(m.group("port"))

    def log_request(self, request: mitmproxy.http.HTTPRequest,
        parent_request_id: str):
        query_part_index = request.path.find("?")
        base_path = request.path[:query_part_index] if query_part_index > -1 else request.path

        query_params = {}
        for key, value in request.query.items(True):
            if key not in query_params:
                query_params[key] = []
            query_params[key].append(value)

        headers = {}
        for k, v in request.headers.items():
            if k not in HEADERS_TO_REMOVE:
                headers[k] = v

        port = int(os.getenv("PORT"))

        to_log = {'rootRequestId': request.headers[ROOT_REQUEST_ID_HEADER],
                  'parentRequestId': parent_request_id,
                  'requestId': request.headers[REQUEST_ID_HEADER],
                  'timestamp': datetime.utcnow().isoformat() + 'Z',
                  'protocol': request.scheme,
                  'host': os.getenv(EXTERNAL_DOMAIN_ENV),
                  'port': port,
                  'method': request.method,
                  'fullPath': request.path,
                  'query': query_params,
                  'path': base_path,
                  'version': request.http_version,
                  'headers': headers,
                  'body': request.text,
                  }
        json_content = json.dumps(to_log)
        ctx.log.info(json_content)

        self.write_to_file(request.headers[ROOT_REQUEST_ID_HEADER],
                           request.headers[REQUEST_ID_HEADER], REQUEST_FILE_SUFFIX,
                           json_content)

    def log_response(self, request: mitmproxy.http.HTTPRequest,
        response: mitmproxy.http.HTTPResponse):
        headers = {}
        for k, v in response.headers.items():
            if k not in HEADERS_TO_REMOVE:
                headers[k] = v
        to_log = {'rootRequestId': request.headers[ROOT_REQUEST_ID_HEADER],
                  'requestId': request.headers[REQUEST_ID_HEADER],
                  'timestamp': datetime.today().isoformat() + 'Z',
                  'headers': headers,
                  'reason': response.reason,
                  'statusCode': response.status_code,
                  'body': response.text,
                  }
        json_content = json.dumps(to_log)
        # ctx.log.info(json_content)

        self.write_to_file(request.headers[ROOT_REQUEST_ID_HEADER],
                           request.headers[REQUEST_ID_HEADER], RESPONSE_FILE_SUFFIX,
                           json_content)

    def log_error(self, request: mitmproxy.http.HTTPRequest, message):
        to_log = {
            'rootRequestId': request.headers[ROOT_REQUEST_ID_HEADER],
            'requestId': request.headers[REQUEST_ID_HEADER],
            'code': 1,
            'message': message,
            'timestamp': datetime.today().isoformat() + 'Z'
        }
        json_content = json.dumps(to_log)
        self.write_to_file(request.headers[ROOT_REQUEST_ID_HEADER],
                           request.headers[REQUEST_ID_HEADER], ERROR_FILE_SUFFIX,
                           json_content)

    def write_to_file(self, root_request_id: str, request_id: str, suffix: str,
        content: str):
        folder = LOGS_ROOT_FOLDER + '/' + root_request_id
        file_name = folder + '/' + request_id + '.' + suffix + '.json'
        os.makedirs(folder, exist_ok=True)
        with open(file_name, "w") as file:
            print(content, file=file)


addons = [
    RequestLogger()
]
