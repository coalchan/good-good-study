package com.luckypeng.study.bio;

import com.luckypeng.study.bio.client.BIOClient;
import com.luckypeng.study.bio.server.BIOServer;
import com.luckypeng.study.bio.server.FakeBIOServer;

import java.io.IOException;
import java.util.Random;

/**
 * @author coalchan
 * created at: 2018/2/24 14:36
 * Description: 伪异步I/O编程，即在传统的BIO编程的基础上在服务端利用了线程池来应答客户端而已
 */
public class FakeBIOApplication {
    //测试主方法
    public static void main(String[] args) throws InterruptedException {
        //运行服务器
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FakeBIOServer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //避免客户端先于服务器启动前执行代码
        Thread.sleep(100);

        //运行客户端
        char operators[] = {'+','-','*','/'};
        Random random = new Random(System.currentTimeMillis());
        new Thread(new Runnable() {
            @SuppressWarnings("static-access")
            @Override
            public void run() {
                while(true){
                    //随机产生算术表达式
                    String expression = random.nextInt(10)+""+operators[random.nextInt(4)]+(random.nextInt(10)+1);
                    BIOClient.send(expression);
                    try {
                        Thread.currentThread().sleep(random.nextInt(1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}