package com.luckypeng.study.pool2.custom.impl;

import com.luckypeng.study.pool2.custom.ConnectionFactory;
import com.luckypeng.study.pool2.custom.PooledConnection;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class MyConnectionFactory implements ConnectionFactory<Connection, String> {
    @Override
    public PooledConnection<Connection, String> makeConnection() throws Exception {
        Connection connection = new Connection();
        return new PooledConnection<>(connection);
    }

    @Override
    public void destroyObject(PooledConnection<Connection, String> p) throws Exception {

    }
}
