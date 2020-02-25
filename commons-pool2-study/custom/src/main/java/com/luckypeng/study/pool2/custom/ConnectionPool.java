package com.luckypeng.study.pool2.custom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author coalchan
 * @date 2020/02/25
 */
public class ConnectionPool<T, V> {
    private ConnectionFactory<T, V> factory;
    private PriorityBlockingQueue<PooledConnection<T, V>> idles;
    private final Map<IdentityWrapper<T>, PooledConnection<T, V>> allConnections =
            new ConcurrentHashMap<>();
    private Map<V, T> reservedConnctions = new ConcurrentHashMap<>();;

    private int maxTotal = 8;
    private int maxIdle = 4;
    private int minIdle = 0;

    public ConnectionPool(ConnectionFactory<T, V> factory) {
        this.factory = factory;
        idles = new PriorityBlockingQueue<>(maxTotal);
    }

    private PooledConnection<T, V> create() throws Exception {
        PooledConnection<T, V> p = factory.makeConnection();
        allConnections.put(new IdentityWrapper<>(p.getConnection()), p);
        return p;
    }

    public T borrow() throws Exception {
        return idles.take().getConnection();
    }

    public T borrow(V operation) throws Exception {
        return reservedConnctions.get(operation);
    }

    public void back(T connection) {
        PooledConnection<T, V> p = allConnections.get(new IdentityWrapper<>(connection));
        idles.put(p);
    }

    public void back(T connection, V operation) {
        PooledConnection<T, V> p = allConnections.get(new IdentityWrapper<>(connection));
        p.addOperation(operation);
        reservedConnctions.put(operation, connection);
        idles.put(p);
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
        @SuppressWarnings("rawtypes")
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
}
