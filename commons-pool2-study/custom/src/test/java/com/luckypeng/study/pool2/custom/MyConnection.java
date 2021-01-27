package com.luckypeng.study.pool2.custom;

import lombok.Getter;
import org.apache.commons.pool2.impl.Operationable;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class MyConnection implements Operationable<String> {
    @Getter
    private String id;

    private final AtomicInteger operationCount = new AtomicInteger(0);

    public MyConnection() {
        id = UUID.randomUUID().toString();
        System.out.println("connection inited: " + id);
    }

    public void close() {
        System.out.println("connection closed: " + id);
    }

    public String execute() {
        sleep(1000);
        String operation = UUID.randomUUID().toString();
        System.out.println("executed! operation=" + operation);
        return operation;
    }

    public String executeStatus(String operation) {
        sleep(500);
        return operation + ":RUNNING";
    }

    public void addOperation() {
        operationCount.incrementAndGet();
    }

    public void closeOperation(String operation) {
        sleep(500);
        operationCount.decrementAndGet();
    }

    @Override
    public boolean hasOperations() {
        return operationCount.get() != 0;
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
