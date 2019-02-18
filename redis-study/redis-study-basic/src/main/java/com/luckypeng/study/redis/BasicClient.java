package com.luckypeng.study.redis;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author coalchan
 * created at: 2018/2/27 14:34
 * Description: 基础用法
 */
public class BasicClient {
    private static final String REDIS_HOST = "cdhtest02";
    private static final Integer REDIS_PORT = 6379;
    private static final String REDIS_AUTH = "redis02";
    private static final String SUCCESS_RESULT = "OK";

    Jedis jedis;

    JedisPool pool;
    Jedis jedisPool;

    @Before
    public void init() {
        jedis = new Jedis(REDIS_HOST, REDIS_PORT);
        jedis.auth(REDIS_AUTH);

        JedisPoolConfig config = new JedisPoolConfig();
        pool = new JedisPool(config, REDIS_HOST, REDIS_PORT);
        jedisPool = pool.getResource();
        jedisPool.auth(REDIS_AUTH);
    }

    @After
    public void close() {
        jedis.close();
        jedisPool.close();
        pool.close();
    }

    /**
     * 常规使用
     * 单机1W 8.638s
     */
    @Test
    public void normal() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String result = jedis.set("n" + i, "n" + i);
            if(!SUCCESS_RESULT.equals(result)) {
                System.out.println(i + "'s result is: " + result);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Simple SET: " + ((end - start)/1000.0) + " seconds");
    }


    /**
     * 单机版的redis使用连接池管理连接
     * 单机1W 9.067s
     */
    @Test
    public void alonePool() {
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10000; i++) {
            String result = jedisPool.set("n" + i,"n" + i);
//            String result = jedisPool.setex("n" + i, 10,"n" + i); // 设置过期时间
            if(!SUCCESS_RESULT.equals(result)) {
                System.out.println(i + "'s result is: " + result);
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("Simple SET: " + ((end - start)/1000.0) + " seconds");
    }

    /**
     * 测试Hash类型的操作
     */
    @Test
    public void hash() {
        jedis.select(1);
        Long result = jedis.hset("key1", "k1", "v1");
        System.out.println("result: " + result);
        result = jedis.hset("key1", "k2", "v2");
        System.out.println("result: " + result);
        String value = jedis.hget("key", "k1");
        System.out.println("value: " + value);
    }
}
