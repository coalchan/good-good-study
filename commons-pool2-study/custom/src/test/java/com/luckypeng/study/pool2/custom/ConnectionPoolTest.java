package com.luckypeng.study.pool2.custom;

import org.apache.commons.pool2.impl.ConnectionPool;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ConnectionPoolTest {
    private static ConnectionPool<MyConnection> pool;

    /**
     * 存储操作与连接关系
     * @author coalchan
     */
    private final static Map<String, MyConnection> reservedMap = new ConcurrentHashMap<>();
    private static final AtomicLong reservedUseCount = new AtomicLong(0);

    @Before
    public void before() {
        pool = new ConnectionPool(new MyConnectionFactory());
        pool.setMaxTotal(30);
        pool.setMaxIdle(10);
//        pool.setMinIdle(3);

        pool.setMinEvictableIdleTimeMillis(10000);
        pool.setTimeBetweenEvictionRunsMillis(10000);
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
        for (int i = 0; i < 50; i++) {
            new Thread(new MyThread(i % 5 == 0)).start();
        }
        while (true) {
            Thread.sleep(1000);
        }
    }

    public static void statPool(String prefix) {
        System.out.print(prefix);
        System.out.println(String.format(": active=%s,idle=%s,reserverd=%s, " +
                        "reservedUseCount=%s,borrowCnt=%s,returnCnt=%s,destroyCnt=%s",
                pool.getNumActive(), pool.getNumIdle(), new HashSet<>(reservedMap.values()).size(),
                reservedUseCount.get(), pool.getBorrowedCount(), pool.getReturnedCount(), pool.getDestroyedCount()));
    }

    public static String execute() throws Exception {
        MyConnection conn = null;
        boolean isReserved = false;
        conn = pool.borrowObject(2000);
        if (conn == null) {
            throw new RuntimeException("borrow null");
        }
        String operation = conn.execute();
        conn.addOperation();
        reservedMap.put(operation, conn);
        if (!isReserved) {
            pool.returnObject(conn);
        }
        return operation;
    }

    public static void executeStatus(String operation) throws Exception {
        MyConnection conn = reservedMap.get(operation);
        reservedUseCount.incrementAndGet();
        String status = conn.executeStatus(operation);
    }

    public static void closeExecute(String operation) throws Exception {
        MyConnection conn = reservedMap.get(operation);
        reservedUseCount.incrementAndGet();
        reservedMap.remove(operation);
        conn.closeOperation(operation);
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
                if (!needBack) {
                    executeStatus(operation);
                    closeExecute(operation);
                }
                System.out.println("------------------------------ended");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}