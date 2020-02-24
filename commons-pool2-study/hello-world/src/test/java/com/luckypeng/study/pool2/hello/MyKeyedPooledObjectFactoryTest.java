package com.luckypeng.study.pool2.hello;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.junit.Test;

import static org.junit.Assert.*;

public class MyKeyedPooledObjectFactoryTest {
    @Test
    public void test() throws Exception {
        MyKeyedPooledObjectFactory myKeyedPooledObjectFactory = new MyKeyedPooledObjectFactory();
        GenericKeyedObjectPool<String, MyConnection> pool = new GenericKeyedObjectPool(myKeyedPooledObjectFactory);
        pool.setMaxTotal(2);

        MyConnection connection = pool.borrowObject("a");
        System.out.println("borrowed connection: " + connection.getConnectionId());
        connection.request("world");

        pool.returnObject("a", connection);

//        if return illegal key, will throw java.lang.IllegalStateException: No keyed pool found under the given key.
//        pool.returnObject("b", connection);

        MyConnection connection1 = pool.borrowObject("a");
        System.out.println("borrowed connection: " + connection1.getConnectionId());

        MyConnection connection2 = pool.borrowObject("b");
        System.out.println("borrowed connection: " + connection2.getConnectionId());
    }
}