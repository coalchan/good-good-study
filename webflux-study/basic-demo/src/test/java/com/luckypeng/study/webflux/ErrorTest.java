package com.luckypeng.study.webflux;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author chenzhipeng
 * @date 2018/5/10 9:37
 * 错误处理
 */
public class ErrorTest {
    /**
     * 错误处处理: 书写错误处理函数
     * 遇到错误即退出
     */
    @Test
    public void testErrorHandling() {
        Flux.range(1, 6)
                .map(i -> 10/(i-3))
                .map(i -> i*i)
                .subscribe(System.out::println, j -> System.err.println("出错: " + j));
    }

    /**
     * 错误处理: 用新的数据流代替错误数据，这里也可以是包装为业务相关的异常后继续抛出
     * 还有其他处理方法，请参考 https://blog.csdn.net/get_set/article/details/79480172
     * 代替后依然退出，但是不会走到错误处理errorConsumer
     */
    @Test
    public void testErrorResume() {
        Flux.range(1, 6)
                .map(i -> 10/(i-3))
                .onErrorResume(e -> Mono.just(6)) // 提供新的数据流
                .map(i -> i*i)
                .subscribe(System.out::println, System.err::println);
    }
}
