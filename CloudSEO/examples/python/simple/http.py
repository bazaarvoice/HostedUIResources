#!/usr/bin/python

import CGIHTTPServer
import BaseHTTPServer

class Handler(CGIHTTPServer.CGIHTTPRequestHandler):
    cgi_directories = ['/']

port = 8000
httpd = BaseHTTPServer.HTTPServer(("", port), Handler)

httpd.serve_forever()

print 'HTTP server started on %d...' % port