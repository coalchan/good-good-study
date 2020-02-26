package com.luckypeng.study.pool2.custom;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class MyConnectionFactory implements ConnectionFactory<MyConnection, String> {
    @Override
    public PooledConnection<MyConnection, String> makeConnection() throws Exception {
        MyConnection myConnection = new MyConnection();
        return new PooledConnection<>(myConnection);
    }

    @Override
    public void destroyObject(PooledConnection<MyConnection, String> p) throws Exception {
        p.getConnection().close();
    }
}
