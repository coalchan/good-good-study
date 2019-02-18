package com.luckypeng.study.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author coalchan
 * created at: 2018/2/26 9:58
 * Description:
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AIOServerHandler> {

    @Override
    public void completed(AsynchronousSocketChannel channel, AIOServerHandler serverHandler) {
        //继续接受其他客户端的请求
        AIOServer.clientCount++;
        System.out.println("连接的客户端数：" + AIOServer.clientCount);
        serverHandler.channel.accept(serverHandler, this);
        //创建新的Buffer
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //异步读  第三个参数为接收消息回调的业务Handler
        channel.read(buffer, buffer, new ReadHandler(channel));
    }

    @Override
    public void failed(Throwable exc, AIOServerHandler attachment) {

    }
}
