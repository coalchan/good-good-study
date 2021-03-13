package com.luckypeng.study.hadoop.hdfs.service;

import org.apache.hadoop.ipc.ProtocolSignature;

import java.io.IOException;

/**
 * @author coalchan
 */
public class MyProtocolImpl implements MyProtocol {
    public String sayHello(String name) {
        System.out.println("服务端收到: " + name);
        return "Hello: " + name;
    }

    public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
        return MyProtocol.versionID;
    }

    public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
        return new ProtocolSignature(MyProtocol.versionID, null);
    }
}
