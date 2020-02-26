package com.luckypeng.study.pool2.custom;

public interface OperationCloseable<V> {
    void closeOperation(V operation) throws Exception;
}
