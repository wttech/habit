# -*- coding: utf-8 -*-

import gzip
import json
import os
import requests
import shutil
import socket
import ssl
import sys
import threading
import uuid
import zlib
from http.server import HTTPServer, BaseHTTPRequestHandler
from io import StringIO
from socketserver import ThreadingMixIn
from threading import Thread

ROOT_REQUEST_ID_HEADER = 'X-Habit-Root-Request-Id'

LOGS_ROOT_FOLDER = "/opt/request-persister/logs"

def with_color(c, s):
    return "\x1b[%dm%s\x1b[0m" % (c, s)


def join_with_script_dir(path):
    return os.path.join(os.path.dirname(os.path.abspath(__file__)), path)


class ThreadingHTTPServer(ThreadingMixIn, HTTPServer):
    address_family = socket.AF_INET6
    daemon_threads = True

    def handle_error(self, request, client_address):
        # surpress socket/ssl related errors
        cls, e = sys.exc_info()[:2]
        if cls is socket.error or cls is ssl.SSLError:
            pass
        else:
            return HTTPServer.handle_error(self, request, client_address)


class ProxyRequestHandler(BaseHTTPRequestHandler):
    timeout = 5
    lock = threading.Lock()

    def __init__(self, *args, **kwargs):
        BaseHTTPRequestHandler.__init__(self, *args, **kwargs)

    def log_error(self, format, *args):
        # surpress "Request timed out: timeout('timed out',)"
        if isinstance(args[0], socket.timeout):
            return
        self.log_message(format, *args)

    def do_GET(self):
        if self.path.startswith("/api/healthcheck"):
            self.send_response(200)
            self.send_header('Connection', 'close')
            self.end_headers()
        else:
            self.send_response(404)
            self.send_header('Connection', 'close')
            self.end_headers()

    def do_POST(self):
        if self.path == '/api/requests' or self.path == '/api/requests/':
            self.handle_test_request()
        else:
            self.send_response(404)
            self.send_header('Connection', 'close')
            self.end_headers()

    def handle_test_request(self):
        if self.is_loop():
            self.send_response(508)
            self.send_header('Connection', 'close')
            self.end_headers()
            return

        self.create_request_graph()

    def is_loop(self):
        return ROOT_REQUEST_ID_HEADER in self.headers

    def create_request_graph(self):
        req = self
        content_length = int(req.headers.get('Content-Length', 0))
        req_body = self.rfile.read(content_length) if content_length else None
        request_json = req_body.decode("utf-8")

        requestData = json.loads(request_json)
        method = requestData['method']
        protocol = requestData['protocol']
        host = requestData['host']
        port = ':' + str(requestData['port']) if 'port' in requestData else ''
        path = requestData['path'] if 'path' in requestData else ''
        headers = requestData['headers'] if 'headers' in requestData else {}
        queryStringParameters = requestData['queryStringParameters'] if 'queryStringParameters' in requestData else []
        body = requestData['body'] if 'body' in requestData else None
        queryString = self.calculateQueryString(queryStringParameters)

        # Set global identifier of the whole request
        # Will be set on all redirects as well
        root_request_id = str(uuid.uuid4())
        headers[ROOT_REQUEST_ID_HEADER] = root_request_id

        # Execute the test request
        try:
            proxy_request = requests.request(method,
                                         protocol + '://' + host + port + path + queryString,
                                         headers=headers,
                                         data=body, verify=False)
            # Recombine request parts into a full graph
            flat_data = self.recreateGraph(root_request_id)

            # remove the folder with data
            shutil.rmtree(LOGS_ROOT_FOLDER + "/" + root_request_id)

            # Send the graph as response
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.send_header('Connection', 'close')
            self.end_headers()

            self.wfile.write(bytes(json.dumps(flat_data[root_request_id]), 'utf-8'))
        except requests.exceptions.ConnectionError as e:
            data = {
                'error': {
                    'message': str(e),
                    'code': 222
                }
            }
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.send_header('Connection', 'close')
            self.end_headers()

            self.wfile.write(bytes(json.dumps(data), 'utf-8'))


    def calculateQueryString(self, queryStringParameters):
        accumulator = ''
        for param in queryStringParameters:
            name = param['name']
            value = param['value']
            if name:
                paramToAppend = None
                if value:
                    paramToAppend = name + '=' + value
                else:
                    paramToAppend = name
                if accumulator:
                    accumulator += '&'
                accumulator += paramToAppend
        return '?' + accumulator if accumulator else ''

    def recreateGraph(self, root_request_id):
        flat_data = {}

        flat_data[root_request_id] = {
            'subrequests': []
        }

        request_dir = LOGS_ROOT_FOLDER + "/" + root_request_id
        request_log_files = [request_dir + '/' + i for i in self.getfiles(request_dir)]
        for request_log_filename in request_log_files:
            with open(request_log_filename, 'r') as request_log_file:
                json_content = json.load(request_log_file)
                request_id = json_content['requestId']
                if request_id not in flat_data:
                    flat_data[request_id] = {
                        'request': None,
                        'response': None,
                        'error': None,
                        'subrequests': []
                    }
                if '.request.' in request_log_filename:
                    flat_data[request_id]['request'] = json_content
                    parent_id = json_content['parentRequestId']
                    flat_data[parent_id]['subrequests'].append(flat_data[request_id])
                elif '.response.' in request_log_filename:
                    flat_data[request_id]['response'] = json_content
                elif '.error.' in request_log_filename:
                    flat_data[request_id]['error'] = json_content

        return flat_data

    def getfiles(self, dirpath):
        a = [s for s in os.listdir(dirpath)
             if os.path.isfile(os.path.join(dirpath, s))]
        a.sort(key=lambda s: os.path.getctime(os.path.join(dirpath, s)))
        return a

    def encode_content_body(self, text, encoding):
        if encoding == 'identity':
            data = text
        elif encoding in ('gzip', 'x-gzip'):
            io = StringIO()
            with gzip.GzipFile(fileobj=io, mode='wb') as f:
                f.write(text)
            data = io.getvalue()
        elif encoding == 'deflate':
            data = zlib.compress(text)
        else:
            raise Exception("Unknown Content-Encoding: %s" % encoding)
        return data

    def decode_content_body(self, data, encoding):
        if encoding == 'identity':
            text = data
        elif encoding in ('gzip', 'x-gzip'):
            io = StringIO(data)
            with gzip.GzipFile(fileobj=io) as f:
                text = f.read()
        elif encoding == 'deflate':
            try:
                text = zlib.decompress(data)
            except zlib.error:
                text = zlib.decompress(data, -zlib.MAX_WBITS)
        else:
            raise Exception("Unknown Content-Encoding: %s" % encoding)
        return text

    def request_handler(self, req, req_body):
        pass

    def response_handler(self, req, req_body, res, res_body):
        pass

    def save_handler(self, req, req_body, res, res_body):
        pass


def serve_on_port(port=8080):
    server_address = ('', port)
    ProxyRequestHandler.protocol_version = "HTTP/1.1"
    httpd = ThreadingHTTPServer(server_address, ProxyRequestHandler)
    print("Serving HTTP Proxy on", 'localhost', "port", port, "...")
    httpd.serve_forever()


def run(ports):
    threads = []

    # Create servers for all ports
    for port in ports:
        threads.append(Thread(target=serve_on_port, args=port, daemon=True))

    # Start all threads
    for thread in threads:
        thread.start()

    # Wait for all of them to finish
    for thread in threads:
        thread.join()


if __name__ == "__main__":
    portArray = [(8080,)]
    run(ports=portArray)
