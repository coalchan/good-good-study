package com.luckypeng.study.pool2.custom;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class ConnectionPool<T extends OperationCloseable<V>, V> {
    private ConnectionFactory<T, V> factory;

    /**
     * 空闲连接
     */
    private LinkedBlockingDeque<PooledConnection<T, V>> idles;

    /**
     * 所有连接
     */
    private final Map<IdentityWrapper<T>, PooledConnection<T, V>> all =
            new ConcurrentHashMap<>();

    /**
     * 暂存连接，有未完成的操作
     */
    private PriorityBlockingQueue<PooledConnection<T, V>> reserved;

    /**
     * 暂存连接，有未完成的操作，Key 为具体操作实例
     */
    private Map<V, PooledConnection<T, V>> reservedConnections = new ConcurrentHashMap<>();

    private final Object makeObjectCountLock = new Object();
    private final AtomicLong createCount = new AtomicLong(0);

    private long makeObjectCount = 0;

    private int maxTotal = 10;
    private int maxIdle = 4;
    private int minIdle = 0;

    private long maxWaitTimeMillis = 3000;

    /**
     * 单个连接最大保存操作数
     */
    private int maxReserveOperations = 10;

    /**
     * The size of the caches used to store historical data for some attributes
     * so that rolling means may be calculated.
     */
    public static final int MEAN_TIMING_STATS_CACHE_SIZE = 100;

    // Monitoring attributes
    private final AtomicLong borrowedCount = new AtomicLong(0);
    private final AtomicLong backedCount = new AtomicLong(0);
    final AtomicLong createdCount = new AtomicLong(0);
    final AtomicLong destroyedCount = new AtomicLong(0);
    private final StatsStore activeTimes = new StatsStore(MEAN_TIMING_STATS_CACHE_SIZE);
    private final StatsStore idleTimes = new StatsStore(MEAN_TIMING_STATS_CACHE_SIZE);
    private final StatsStore waitTimes = new StatsStore(MEAN_TIMING_STATS_CACHE_SIZE);
    private final AtomicLong maxBorrowWaitTimeMillis = new AtomicLong(0L);

    public ConnectionPool(ConnectionFactory<T, V> factory) {
        this.factory = factory;
        idles = new LinkedBlockingDeque<>(maxTotal);
        reserved = new PriorityBlockingQueue<>(maxTotal);
    }

    public int getNumActive() {
        return all.size() - idles.size() - reserved.size();
    }

    public int getNumIdle() {
        return idles.size();
    }

    public int getNumReserved() {
        return reserved.size();
    }

    /***
     * 创建连接
     * @return
     * @throws Exception
     */
    private PooledConnection<T, V> create() throws Exception {
        final long localStartTimeMillis = System.currentTimeMillis();
        final long localMaxWaitTimeMillis = maxWaitTimeMillis;

        // Flag that indicates if create should:
        // - TRUE:  call the factory to create an object
        // - FALSE: return null
        // - null:  loop and re-test the condition that determines whether to
        //          call the factory
        Boolean create = null;
        while (create == null) {
            synchronized (makeObjectCountLock) {
                final long newCreateCount = createCount.incrementAndGet();
                if (newCreateCount > maxTotal) {
                    // The pool is currently at capacity or in the process of
                    // making enough new objects to take it to capacity.
                    createCount.decrementAndGet();
                    if (makeObjectCount == 0) {
                        // There are no makeObject() calls in progress so the
                        // pool is at capacity. Do not attempt to create a new
                        // object. Return and wait for an object to be returned
                        create = Boolean.FALSE;
                    } else {
                        // There are makeObject() calls in progress that might
                        // bring the pool to capacity. Those calls might also
                        // fail so wait until they complete and then re-test if
                        // the pool is at capacity or not.
                        makeObjectCountLock.wait(localMaxWaitTimeMillis);
                    }
                } else {
                    // The pool is not at capacity. Create a new object.
                    makeObjectCount++;
                    create = Boolean.TRUE;
                }
            }

            // Do not block more if maxWaitTimeMillis is set.
            if (create == null &&
                    (localMaxWaitTimeMillis > 0 &&
                            System.currentTimeMillis() - localStartTimeMillis >= localMaxWaitTimeMillis)) {
                create = Boolean.FALSE;
            }
        }

        if (!create.booleanValue()) {
            return null;
        }

        final PooledConnection<T, V> p;
        try {
            p = factory.makeConnection();
        } catch (final Throwable e) {
            createCount.decrementAndGet();
            throw e;
        } finally {
            synchronized (makeObjectCountLock) {
                makeObjectCount--;
                makeObjectCountLock.notifyAll();
            }
        }

        createdCount.incrementAndGet();
        all.put(new IdentityWrapper<>(p.getConnection()), p);
        return p;
    }

    /**
     * Destroys a wrapped pooled object.
     *
     * @param toDestroy The wrapped pooled object to destroy
     *
     * @throws Exception If the factory fails to destroy the pooled object
     *                   cleanly
     */
    private void destroy(final PooledConnection<T, V> toDestroy) throws Exception {
        idles.remove(toDestroy);
        all.remove(new IdentityWrapper<>(toDestroy.getConnection()));
        try {
            factory.destroyObject(toDestroy);
        } finally {
            destroyedCount.incrementAndGet();
            createCount.decrementAndGet();
        }
    }

    /**
     * 获取连接，先从空闲获取，没有则创建，创建不成则从保存操作的连接中获取
     * @return
     * @throws Exception
     */
    public T borrow() throws Exception {
        final long waitTime = System.currentTimeMillis();

        PooledConnection<T, V> p = idles.poll();

        testP(p, "first poll");

        if (p == null) {
            p = create();
        }

        testP(p, "create");

        if (p == null) {
            // 如果空闲连接不够用，可以从 reserved 中获取，从而提高利用率
            p = reserved.poll(maxWaitTimeMillis / 2, TimeUnit.MILLISECONDS);
            if (p != null && p.getNumOperations() >= maxReserveOperations) {
                reserved.put(p);
                p = null;
            }
        }

        testP(p, "from reserved");

        if (p == null) {
            p = idles.poll(maxWaitTimeMillis / 2, TimeUnit.MILLISECONDS);
        }

        testP(p, "second poll");

        if (p == null) {
            return null;
        }

        updateStatsBorrow(p, System.currentTimeMillis() - waitTime);
        return p.getConnection();
    }

    public void testP(PooledConnection p, String info) {
        if(p!=null) {
            System.out.println(info + " PooledConnection: " + p.hashCode());
        }
    }

    /**
     * 获取指定操作对应连接
     * @param operation
     * @return
     * @throws Exception
     */
    public T borrow(V operation) throws Exception {
        // question
        PooledConnection<T, V> p = reservedConnections.get(operation);
        if (p == null) {
            return null;
        }
        reserved.remove(p);
        return p.getConnection();
    }

    public void back(T connection) throws Exception {
        PooledConnection<T, V> p = all.get(new IdentityWrapper<>(connection));
        final long activeTime = p.getActiveTimeMillis();
        if (idles.size() >= maxIdle) {
            destroy(p);
        } else {
            idles.put(p);
        }
        updateStatsBack(activeTime);
    }

    public void back(T connection, V operation) throws Exception {
        PooledConnection<T, V> p = all.get(new IdentityWrapper<>(connection));
        final long activeTime = p.getActiveTimeMillis();
        p.addOperation(operation);
        reservedConnections.put(operation, p);
        System.out.println("1reserved: " + p.hashCode());
        updateStatsBack(activeTime);
    }

    public void backAndClose(T connection, V operation) throws Exception {
        PooledConnection<T, V> p = all.get(new IdentityWrapper<>(connection));
        final long activeTime = p.getActiveTimeMillis();
        p.closeOperation(operation);
        reservedConnections.remove(operation);
        if (p.getNumOperations() > 0) {
            reserved.put(p);
            System.out.println("2reserved: " + p.hashCode());
        } else if (idles.size() >= maxIdle) {
            destroy(p);
        } else {
            idles.put(p);
        }
        updateStatsBack(activeTime);
    }

    /**
     * Updates statistics after an object is borrowed from the pool.
     * @param p object borrowed from the pool
     * @param waitTime time (in milliseconds) that the borrowing thread had to wait
     */
    final void updateStatsBorrow(final PooledConnection<T, V> p, final long waitTime) {
        borrowedCount.incrementAndGet();
        idleTimes.add(p.getIdleTimeMillis());
        waitTimes.add(waitTime);

        // lock-free optimistic-locking maximum
        long currentMax;
        do {
            currentMax = maxBorrowWaitTimeMillis.get();
            if (currentMax >= waitTime) {
                break;
            }
        } while (!maxBorrowWaitTimeMillis.compareAndSet(currentMax, waitTime));
    }

    /**
     * Updates statistics after an object is returned to the pool.
     * @param activeTime the amount of time (in milliseconds) that the returning
     * object was checked out
     */
    final void updateStatsBack(final long activeTime) {
        backedCount.incrementAndGet();
        activeTimes.add(activeTime);
    }

    static class IdentityWrapper<T> {
        /** Wrapped object */
        private final T instance;

        /**
         * Create a wrapper for an instance.
         *
         * @param instance object to wrap
         */
        public IdentityWrapper(final T instance) {
            this.instance = instance;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(instance);
        }

        @Override
        public boolean equals(final Object other) {
            return  other instanceof IdentityWrapper &&
                    ((IdentityWrapper) other).instance == instance;
        }

        /**
         * @return the wrapped object
         */
        public T getConnection() {
            return instance;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("IdentityWrapper [instance=");
            builder.append(instance);
            builder.append("]");
            return builder.toString();
        }
    }

    /**
     * Maintains a cache of values for a single metric and reports
     * statistics on the cached values.
     */
    private class StatsStore {

        private final AtomicLong values[];
        private final int size;
        private int index;

        /**
         * Create a StatsStore with the given cache size.
         *
         * @param size number of values to maintain in the cache.
         */
        public StatsStore(final int size) {
            this.size = size;
            values = new AtomicLong[size];
            for (int i = 0; i < size; i++) {
                values[i] = new AtomicLong(-1);
            }
        }

        /**
         * Adds a value to the cache.  If the cache is full, one of the
         * existing values is replaced by the new value.
         *
         * @param value new value to add to the cache.
         */
        public synchronized void add(final long value) {
            values[index].set(value);
            index++;
            if (index == size) {
                index = 0;
            }
        }

        /**
         * Returns the mean of the cached values.
         *
         * @return the mean of the cache, truncated to long
         */
        public long getMean() {
            double result = 0;
            int counter = 0;
            for (int i = 0; i < size; i++) {
                final long value = values[i].get();
                if (value != -1) {
                    counter++;
                    result = result * ((counter - 1) / (double) counter) +
                            value/(double) counter;
                }
            }
            return (long) result;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder();
            builder.append("StatsStore [values=");
            builder.append(Arrays.toString(values));
            builder.append(", size=");
            builder.append(size);
            builder.append(", index=");
            builder.append(index);
            builder.append("]");
            return builder.toString();
        }
    }
}
