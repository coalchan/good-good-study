package com.luckypeng.study.hadoop.hdfs;

import com.luckypeng.study.hadoop.hdfs.service.MyProtocol;
import com.luckypeng.study.hadoop.hdfs.service.MyProtocolImpl;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.ipc.RPC;

import java.io.IOException;

/**
 * @author coalchan
 */
public class Server {
    public static void main(String[] args) throws IOException {
        RPC.Server server = new RPC.Builder(new Configuration())
                .setBindAddress("localhost")
                .setPort(6666)
                .setProtocol(MyProtocol.class)
                .setInstance(new MyProtocolImpl())
                .build();

        server.start();
    }
}
