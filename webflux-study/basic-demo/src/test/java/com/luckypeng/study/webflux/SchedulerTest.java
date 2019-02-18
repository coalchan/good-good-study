package com.luckypeng.study.webflux;

import org.junit.Test;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author chenzhipeng
 * @date 2018/5/10 9:18
 * 调度器测试
 */
public class SchedulerTest {
    /**
     * 模拟同步方法
     * @return
     */
    private String getStringSync() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello, Reactor!";
    }

    /**
     * subscribeOn: 将同步方法放入到{@code Schedulers}内置的弹性线程池执行，弹性线程池会为Callable的执行任务分配一个单独的线程
     * @throws InterruptedException
     */
    @Test
    public void testSyncToAsync() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Mono.fromCallable(() -> getStringSync())
                .subscribeOn(Schedulers.elastic())
                .subscribe(System.out::println, System.err::println, countDownLatch::countDown);
        System.out.println("这里不会阻塞");
        countDownLatch.await(10, TimeUnit.SECONDS);
    }
}
