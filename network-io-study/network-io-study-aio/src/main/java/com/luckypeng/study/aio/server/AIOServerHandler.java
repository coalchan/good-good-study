package com.luckypeng.study.aio.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @author coalchan
 * created at: 2018/2/26 9:57
 * Description:
 */
public class AIOServerHandler implements Runnable{
    public CountDownLatch latch;
    public AsynchronousServerSocketChannel channel;
    public AIOServerHandler(int port) {
        try {
            channel = AsynchronousServerSocketChannel.open(); //创建服务端通道
            channel.bind(new InetSocketAddress(port));
            System.out.println("服务器已启动，端口号：" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        latch = new CountDownLatch(1);
        channel.accept(this,new AcceptHandler()); //接收客户端的连接
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
