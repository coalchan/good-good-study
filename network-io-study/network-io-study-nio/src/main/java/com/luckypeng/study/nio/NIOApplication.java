package com.luckypeng.study.nio;

import com.luckypeng.study.nio.client.NIOClient;
import com.luckypeng.study.nio.server.NIOServer;

import java.util.Scanner;

/**
 * @author coalchan
 * created at: 2018/2/24 17:46
 * Description:
 */
public class NIOApplication {
    public static void main(String[] args) throws Exception{
        //运行服务器
        NIOServer.start();
        //避免客户端先于服务器启动前执行代码
        Thread.sleep(100);
        //运行客户端
        NIOClient.start();
        while(NIOClient.sendMsg(new Scanner(System.in).nextLine())); // 终端输入算式, 如 4*3-2 然后回车
    }
}
