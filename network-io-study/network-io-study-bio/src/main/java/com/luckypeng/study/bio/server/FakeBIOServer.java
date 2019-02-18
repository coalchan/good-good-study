package com.luckypeng.study.bio.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author coalchan
 * created at: 2018/2/24 15:19
 * Description: 伪异步I/O 服务端
 */
public class FakeBIOServer {
    private static int DEFAULT_PORT = 12345; //默认的端口号
    private static ServerSocket server;
    private static ExecutorService executorService = Executors.newFixedThreadPool(60); // 构造大小固定的线程池

    public static void start() throws IOException {
        start(DEFAULT_PORT);
    }

    //这个方法不会被大量并发访问，不太需要考虑效率，直接进行方法同步就行了
    public synchronized static void start(int port) throws IOException{
        if(server != null) return;
        try{
            server = new ServerSocket(port);
            System.out.println("服务器已启动，端口号：" + port);
            Socket socket;
            while(true){
                socket = server.accept();
                executorService.execute(new BIOServerHandler(socket));
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
