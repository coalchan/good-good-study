package com.luckypeng.study.pool2.custom;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConnectionPoolTest {
    private static ConnectionPool<MyConnection, String> pool;

    @Before
    public void before() {
        pool = new ConnectionPool(new MyConnectionFactory());
    }

    @Test
    @Ignore
    public void testSync() throws Exception {
        statPool("1");
        String operation = execute();
        statPool("2");
        executeStatus(operation);
        statPool("3");
        closeExecute(operation);
        statPool("4");
    }

    @Test
    public void test() throws Exception {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(
                ()->statPool("stat"), 0, 1, TimeUnit.SECONDS);
        for (int i = 0; i < 20; i++) {
            new Thread(new MyThread(i % 3 == 0)).start();
        }
        while (true) {
            Thread.sleep(1000);
        }
    }

    public static void statPool(String prefix) {
        System.out.print(prefix);
        System.out.println(String.format(": active=%s,idle=%s,reserverd=%s",
                pool.getNumActive(), pool.getNumIdle(), pool.getNumReserved()));
    }

    public static String execute() throws Exception {
        MyConnection conn = pool.borrow();
        if (conn == null) {
            throw new RuntimeException("borrow null");
        }
//        System.out.println("borrowed connection: " + conn.getId());
        String operation = conn.execute();
        pool.back(conn, operation);
        return operation;
    }

    public static void executeStatus(String operation) throws Exception {
        MyConnection conn = pool.borrow(operation);
//        System.out.println("borrowed connection: " + conn.getId());
        String status = conn.executeStatus(operation);
        System.out.println("get status: " + status + " of connection:" + conn.getId());
        pool.back(conn, operation);
    }

    public static void closeExecute(String operation) throws Exception {
        MyConnection conn = pool.borrow(operation);
        System.out.println("borrowed connection: " + conn.getId());
        pool.backAndClose(conn, operation);
    }

    public static class MyThread implements Runnable {
        private boolean needBack;

        public MyThread(boolean needBack) {
            this.needBack = needBack;
        }

        @Override
        public void run() {
            try {
                String operation = execute();
                if (needBack) {
                    executeStatus(operation);
                    closeExecute(operation);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void work() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}