package com.luckypeng.study.pool2.custom;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class PooledConnection<T, V> implements Comparable<PooledConnection<T, V>> {
    @Getter
    private T connection;
    private Set<V> operations;
    private final long createTime = System.currentTimeMillis();
    private volatile long lastBorrowTime = createTime;
    private volatile long lastUseTime = createTime;
    private volatile long lastReturnTime = createTime;

    public PooledConnection(T connection) {
        this.connection = connection;
        this.operations = new HashSet<>();
    }


    @Override
    public int compareTo(PooledConnection<T, V> other) {
        return this.operations.size() - other.operations.size();
    }

    public void addOperation(V operation) {
        operations.add(operation);
    }
}
