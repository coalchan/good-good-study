package com.luckypeng.study.pool2.custom;

import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class MyConnectionFactory implements PooledObjectFactory<MyConnection> {
    @Override
    public PooledObject<MyConnection> makeObject() throws Exception {
        MyConnection myConnection = new MyConnection();
        return new DefaultPooledObject<>(myConnection);
    }

    @Override
    public void destroyObject(PooledObject<MyConnection> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<MyConnection> p) {
        return false;
    }

    @Override
    public void activateObject(PooledObject<MyConnection> p) throws Exception {

    }

    @Override
    public void passivateObject(PooledObject<MyConnection> p) throws Exception {

    }
}
