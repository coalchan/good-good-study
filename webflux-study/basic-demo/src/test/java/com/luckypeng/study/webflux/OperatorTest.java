package com.luckypeng.study.webflux;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 操作符测试
 * @author coalchan
 * @date 2018/5/9 9:50
 */
@Slf4j
public class OperatorTest {

    /**
     * map: 把元素转换成新元素
     */
    @Test
    public void testMap() {
        Flux.range(1, 6).map(i -> i * i).subscribe(System.out::println);
    }

    /**
     * 把流中的元素转换成新的流
     * @throws InterruptedException
     */
    @Test
    public void testFlatMap() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.just("flat", "mp3")
                .flatMap(s -> Flux.fromArray(s.split("\\s*"))
                        .delayElements(Duration.ofMillis(100)))
                .subscribe(System.out::println);

        countDownLatch.await(2, TimeUnit.SECONDS);

        StepVerifier.create(Flux.just("flux", "mono")
                .flatMap(s -> Flux.fromArray(s.split("\\s*"))
                        .delayElements(Duration.ofMillis(100)))
                .doOnNext(System.out::print))
                .expectNextCount(8)
                .verifyComplete();
    }

    private Flux<String> getZipDescFlux() {
        String desc = "Zip two sources together, that is to say wait for all the sources to emit one element and combine these elements once into a Tuple2.";
        return Flux.fromArray(desc.split("\\s+"));
    }

    /**
     * 拉链
     * @throws InterruptedException
     */
    @Test
    public void testZip() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Flux.zip(
                getZipDescFlux(),
                Flux.interval(Duration.ofMillis(200)))  // 3
                .subscribe(t -> System.out.println(t.getT1() + ": " + t.getT2()));    // 4
        countDownLatch.await(10, TimeUnit.SECONDS);
    }
}
