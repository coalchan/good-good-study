package com.luckypeng.study.nio.server;

/**
 * @author coalchan
 * created at: 2018/2/24 17:34
 * Description:
 */
public class NIOServer {
    private static int DEFAULT_PORT = 12345;
    private static NIOServerHandler serverHandle;
    public static void start(){
        start(DEFAULT_PORT);
    }
    public static synchronized void start(int port){
        if(serverHandle!=null) {
            serverHandle.stop();
        }
        serverHandle = new NIOServerHandler(port);
        new Thread(serverHandle,"Server").start();
    }
}
