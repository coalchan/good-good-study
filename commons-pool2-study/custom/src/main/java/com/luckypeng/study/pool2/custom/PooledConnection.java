package com.luckypeng.study.pool2.custom;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class PooledConnection<T extends OperationCloseable<V>, V> implements Comparable<PooledConnection<T, V>> {
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
        if (!operations.contains(operation)) {
            operations.add(operation);
        }
    }

    public void removeOperation(V operation) {
        operations.remove(operation);
    }

    public int getNumOperations() {
        return operations.size();
    }

    public long getIdleTimeMillis() {
        final long elapsed = System.currentTimeMillis() - lastReturnTime;
        // elapsed may be negative if:
        // - another thread updates lastReturnTime during the calculation window
        // - System.currentTimeMillis() is not monotonic (e.g. system time is set back)
        return elapsed >= 0 ? elapsed : 0;
    }

    public long getActiveTimeMillis() {
        // Take copies to avoid threading issues
        final long rTime = lastReturnTime;
        final long bTime = lastBorrowTime;

        if (rTime > bTime) {
            return rTime - bTime;
        }
        return System.currentTimeMillis() - bTime;
    }

    /**
     * 关闭操作
     * @param operation
     * @throws Exception
     */
    public void closeOperation(V operation) throws Exception {
        connection.closeOperation(operation);
        operations.remove(operation);
    }
}
