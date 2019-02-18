package com.luckypeng.study.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author coalchan
 * created at: 2018/2/24 14:19
 * Description: 服务端
 */
public class BIOServer {
    private static int DEFAULT_PORT = 12345; //默认的端口号

    private static ServerSocket server; //单例的ServerSocket

    public static void start() throws IOException {
        start(DEFAULT_PORT);
    }

    //这个方法不会被大量并发访问，不太需要考虑效率，直接进行方法同步就行了
    public synchronized static void start(int port) throws IOException{
        if (server != null) {
            return;
        }
        try{
            server = new ServerSocket(port);
            System.out.println("服务器已启动，端口号：" + port);
            while(true){
                Socket socket = server.accept();
                new Thread(new BIOServerHandler(socket)).start(); // 服务端启动一个专门针对客户端请求的线程来应答
            }
        }finally{
            if(server != null){
                System.out.println("服务器已关闭。");
                server.close();
                server = null;
            }
        }
    }
}
