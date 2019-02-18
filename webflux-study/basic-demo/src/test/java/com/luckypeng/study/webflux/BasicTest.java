package com.luckypeng.study.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author coalchan
 * @date 2018/5/9 8:59
 */
@Slf4j
public class BasicTest {

    /**
     * 基础测试
     */
    @Test
    public void test() {
        log.info("只声明不订阅，不会有任何行为产生");
        Flux.just(1, 2, 3, 4, 5, 6);
        Mono.just(10);

        log.info("正常完成测试:");
        Flux.just(1, 2, 3)
                .subscribe(System.out::print, System.err::print, () -> System.out.println("完成"));

        log.info("异常测试:");
        Flux.just(new Exception("错误测试"))
                .subscribe(System.out::print, System.err::print, () -> System.out.println("完成"));
    }

    /**
     * 用于测试
     * @return
     */
    private Flux<Integer> generateFluxFrom1To6() {
        return Flux.just(1, 2, 3, 4, 5, 6);
    }

    /**
     * 用于测试
     * @return
     */
    private Mono<Integer> generateMonoWithError() {
        return Mono.error(new Exception("some error"));
    }

    /**
     * 单元测试框架StepVerifier
     */
    @Test
    public void testStepVerifier() {
        StepVerifier.create(generateFluxFrom1To6())
                .expectNext(1, 2, 3, 4, 5, 6)
                .expectComplete()
                .verify();

        StepVerifier.create(generateMonoWithError())
                .expectErrorMatches(i -> i instanceof Exception)
                .verify();
    }
}
