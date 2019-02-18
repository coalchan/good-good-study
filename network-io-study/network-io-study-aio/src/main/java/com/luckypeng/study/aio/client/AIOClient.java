package com.luckypeng.study.aio.client;

/**
 * @author coalchan
 * created at: 2018/2/26 10:07
 * Description:
 */
public class AIOClient {
    private static String DEFAULT_HOST = "127.0.0.1";
    private static int DEFAULT_PORT = 12345;
    private static AIOClientHandler clientHandle;
    public static void start(){
        start(DEFAULT_HOST,DEFAULT_PORT);
    }
    public static synchronized void start(String ip,int port){
        if(clientHandle!=null)
            return;
        clientHandle = new AIOClientHandler(ip,port);
        new Thread(clientHandle,"Client").start();
    }
    //向服务器发送消息
    public static boolean sendMsg(String msg) {
        if(msg.equals("q")) {
            return false;
        }
        clientHandle.sendMsg(msg);
        return true;
    }

}
