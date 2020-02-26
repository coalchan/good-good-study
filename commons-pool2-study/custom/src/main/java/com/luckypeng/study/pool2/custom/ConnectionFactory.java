package com.luckypeng.study.pool2.custom;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public interface ConnectionFactory<T extends OperationCloseable<V>, V> {
    PooledConnection<T, V> makeConnection() throws Exception;

    void destroyObject(PooledConnection<T, V> p) throws Exception;
}
