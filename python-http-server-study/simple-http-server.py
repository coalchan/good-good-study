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
    def translate_path(self, path):
        """
        所有路由都重定向到当前目录，即无论 path 如何，都返回 index.html 内容
        :param path:
        :return:
        """
        return './'


httpd = SocketServer.TCPServer(("", PORT), MyHandle)

print "serving at port", PORT
httpd.serve_forever()
