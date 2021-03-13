package com.luckypeng.study.hadoop.hdfs;

import com.luckypeng.study.hadoop.hdfs.service.MyProtocol;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author coalchan
 */
public class Client {
    public static void main(String[] args) throws IOException {
        MyProtocol myProtocol = RPC.getProxy(MyProtocol.class,
                MyProtocol.versionID,
                new InetSocketAddress("localhost", 6666),
                new Configuration());

        String response = myProtocol.sayHello("zhangsan");
        System.out.println("客户端收到: " + response);
    }
}
