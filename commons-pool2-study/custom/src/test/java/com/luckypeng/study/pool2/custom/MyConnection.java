package com.luckypeng.study.pool2.custom;

import lombok.Getter;

import java.util.UUID;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class MyConnection implements OperationCloseable<String> {
    @Getter
    private String id;

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

    @Override
    public void closeOperation(String operation) throws Exception {
        sleep(500);
        System.out.println("close operation: " + operation);
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
