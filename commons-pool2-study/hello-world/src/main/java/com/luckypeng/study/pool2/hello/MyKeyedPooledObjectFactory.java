package com.luckypeng.study.pool2.hello;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class MyKeyedPooledObjectFactory implements KeyedPooledObjectFactory<String, MyConnection> {

    @Override
    public PooledObject<MyConnection> makeObject(String key) throws Exception {
        MyConnection myConnection = new MyConnection();
        return new DefaultPooledObject(myConnection);
    }

    @Override
    public void destroyObject(String key, PooledObject<MyConnection> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(String key, PooledObject<MyConnection> p) {
        return p.getObject().isOpen();
    }

    @Override
    public void activateObject(String key, PooledObject<MyConnection> p) throws Exception {

    }

    @Override
    public void passivateObject(String key, PooledObject<MyConnection> p) throws Exception {

    }
}
