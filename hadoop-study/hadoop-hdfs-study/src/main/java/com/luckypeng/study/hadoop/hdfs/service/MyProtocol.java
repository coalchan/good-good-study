package com.luckypeng.study.hadoop.hdfs.service;

import org.apache.hadoop.ipc.VersionedProtocol;

/**
 * @author coalchan
 */
public interface MyProtocol extends VersionedProtocol {
    public static int versionID = 1;

    /**
     * say hello
     * @param name
     * @return
     */
    String sayHello(String name);
}
