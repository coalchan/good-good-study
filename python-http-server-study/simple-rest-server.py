# -*- coding: UTF-8 -*-
import json
import SimpleHTTPServer
import SocketServer
import sys
reload(sys)
sys.setdefaultencoding('utf-8')

if sys.argv[1:]:
    PORT = int(sys.argv[1])
else:
    PORT = 9999

Handler = SimpleHTTPServer.SimpleHTTPRequestHandler


class MyHandle(SimpleHTTPServer.SimpleHTTPRequestHandler):
    def _set_headers(self):
        self.send_response(200)
        self.send_header('Content-type', 'application/json')
        self.end_headers()
        
    def do_POST(self):
        print "POST"
	self.data_string = self.rfile.read(int(self.headers['Content-Length']))
        print self.data_string
        response = {
            'status':'SUCCESS',
            'data':'server got your post data'
        }
        self._set_headers()
	self.wfile.write(json.dumps(response))
	return

httpd = SocketServer.TCPServer(("", PORT), MyHandle)

print "serving at port", PORT
httpd.serve_forever()
