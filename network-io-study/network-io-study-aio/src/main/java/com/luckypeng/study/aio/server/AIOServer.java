package com.luckypeng.study.aio.server;

/**
 * @author coalchan
 * created at: 2018/2/26 9:57
 * Description:
 */
public class AIOServer {
    private static int DEFAULT_PORT = 12345;
    private static AIOServerHandler serverHandle;
    public volatile static long clientCount = 0;
    public static void start(){
        start(DEFAULT_PORT);
    }
    public static synchronized void start(int port){
        if(serverHandle == null) {
            serverHandle = new AIOServerHandler(port);
            new Thread(serverHandle, "Server").start();
        }
    }
}
