package com.luckypeng.study.avatica.hello;


import org.apache.calcite.avatica.remote.LocalService;
import org.apache.calcite.avatica.server.AvaticaJsonHandler;
import org.apache.calcite.avatica.server.HttpServer;

/**
 * @author coalchan
 */
public class Server {
    public static void main(String[] args) {
        AvaticaJsonHandler handler = new AvaticaJsonHandler(new LocalService(new MyMeta()));

        HttpServer server = new HttpServer(8282, handler);
        System.out.println("Started Avatica server on port: " + server.getPort());
        server.start();
        try {
            server.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
