package com.luckypeng.study.pool2.hello;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyPooledObjectFactoryTest {
    @Test
    public void test() throws Exception {
        MyPooledObjectFactory myPooledObjectFactory = new MyPooledObjectFactory();
        GenericObjectPool<MyConnection> pool = new GenericObjectPool(myPooledObjectFactory);
        pool.setMaxTotal(2);

        System.out.println("1. active=" + pool.getNumActive() + ", idle=" + pool.getNumIdle());

        MyConnection connection = pool.borrowObject();
        System.out.println("2. active=" + pool.getNumActive() + ", idle=" + pool.getNumIdle());
        connection.request("world");
        pool.returnObject(connection);
        System.out.println("3. active=" + pool.getNumActive() + ", idle=" + pool.getNumIdle());
        System.out.println("connection.isOpen():" + connection.isOpen());


        pool.close();
    }
}