package com.luckypeng.study.aio;

import com.luckypeng.study.aio.client.AIOClient;
import com.luckypeng.study.aio.server.AIOServer;

import java.util.Scanner;

/**
 * @author coalchan
 * created at: 2018/2/26 10:08
 * Description:
 */
public class AIOApplication {
    public static void main(String[] args) throws InterruptedException {
        AIOServer.start();
        Thread.sleep(100);
        AIOClient.start();
        System.out.println("请输入请求消息：");
        Scanner scanner = new Scanner(System.in);
        while(AIOClient.sendMsg(scanner.nextLine()));
    }
}
