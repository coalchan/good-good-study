package com.luckypeng.study.thrift.hello;

import com.luckypeng.study.thrift.hello.generated.HelloWorldService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;

/**
 * @author coalchan
 * @date 2020/02/18
 */
public class Server {
    public static final int SERVER_PORT = 8080;

    public void startServer() {
        try {
            TServerSocket serverTransport = new TServerSocket(SERVER_PORT);

            TServer.Args tArgs = new TServer.Args(serverTransport)
                    .processor(new HelloWorldService.Processor(new HelloWorldImpl()))
                    .protocolFactory(new TBinaryProtocol.Factory());
            TServer server = new TSimpleServer(tArgs);

            System.out.println("Start server on port: " + SERVER_PORT);
            server.serve();
        } catch (Exception e) {
            System.out.println("Server happened error!!!");
            e.printStackTrace();
        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
