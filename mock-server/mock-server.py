#!/usr/bin/env python
"""
Very simple HTTP server in python.
Usage::
    ./mock-server.py
Send a GET request::
    curl http://localhost
Send a HEAD request::
    curl -I http://localhost
Send a POST request::
    curl -d "foo=bar&bin=baz" http://localhost
"""
import json
import os
import sys
from http.server import BaseHTTPRequestHandler, HTTPServer

MOCK_PORT = 80


class Response:
    def __init__(self, method=None, line=None, path=None, version=None,
        headers=None, body=''):
        self.host = os.environ.get('MOCK_HOSTNAME')
        self.port = MOCK_PORT
        self.method = method
        self.path = path
        self.query = {}
        queryCharacterIndex = path.find('?')
        if queryCharacterIndex > -1:
            self.basePath = self.path[:queryCharacterIndex]
            queryParameters = self.path[queryCharacterIndex + 1:].split('&')
            for queryParam in queryParameters:
                split = queryParam.split('=')
                self.query[split[0]] = split[1]
        else:
            self.basePath = self.path
        self.version = version
        self.headers = {key: value for (key, value) in headers.items()}
        self.body = body


class MockServerHandler(BaseHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.send_header('X-Httpd-Tester', '')
        self.end_headers()

    def _create_response(self):
        return Response(self.command, self.requestline, self.path,
                        self.request_version, self.headers)

    def _write_response(self, response):
        self.wfile.write(bytes(json.dumps(response.__dict__), 'utf-8'))

    def _read_content(self):
        content_length = int(self.headers['Content-Length'])
        return self.rfile.read(content_length).decode('utf-8')

    def _do_method_without_body(self):
        self._do_simple_alternative()
        # self._set_headers()
        # response = self._create_response()
        # self._write_response(response)

    def _do_method_with_body(self):
        self._do_simple_alternative()
        # self._set_headers()
        # response = self._create_response()
        # response.body = self._read_content()
        # self._write_response(response)

    def _do_simple_alternative(self):
        html_content = '<!doctype html><html><head></head><body><p>Request arrived to {0}:{1}</p></body></html>'.format(os.environ.get('MOCK_HOSTNAME'),
                               MOCK_PORT)
        byte_response = bytes(html_content, 'utf-8')

        self.send_response(200)
        self.send_header('Content-Type', 'text/html; charset=utf-8')
        self.end_headers()

        self.wfile.write(byte_response)
        self.wfile.close()

    def do_DELETE(self):
        self._do_method_with_body()

    def do_GET(self):
        self._do_method_without_body()

    def do_HEAD(self):
        self._do_method_without_body()

    def do_OPTIONS(self):
        self._do_method_with_body()

    def do_PATCH(self):
        self._do_method_with_body()

    def do_POST(self):
        self._do_method_with_body()

    def do_PUT(self):
        self._do_method_with_body()


def run(server_class=HTTPServer, handler_class=MockServerHandler):
    server_address = ('', MOCK_PORT)
    httpd = server_class(server_address, handler_class)
    print('Starting httpd...')
    httpd.serve_forever()


if __name__ == "__main__":
    MOCK_PORT = int(sys.argv[1])
    run()
