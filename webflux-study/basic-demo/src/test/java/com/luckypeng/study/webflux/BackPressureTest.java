package com.luckypeng.study.webflux;

import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;

/**
 * 背压测试
 * @author chenzhipeng
 * @date 2018/5/10 10:26
 */
public class BackPressureTest {
    /**
     * 1. Flux.range是一个快的Publisher；
     * 2. 在每次request的时候打印request个数；
     * 3. 通过重写BaseSubscriber的方法来自定义Subscriber；
     * 4. hookOnSubscribe定义在订阅的时候执行的操作；
     * 5. 订阅时首先向上游请求1个元素；
     * 6. hookOnNext定义每次在收到一个元素的时候的操作；
     * 7. sleep 1秒钟来模拟慢的Subscriber；
     * 8. 打印收到的元素；
     * 9. 每次处理完1个元素后再请求1个。
     */
    @Test
    public void test() {
        Flux.range(1, 6)    // 1
                .doOnRequest(n -> System.out.println("Request " + n + " values..."))    // 2
                .subscribe(new BaseSubscriber<Integer>() {  // 3
                    @Override
                    protected void hookOnSubscribe(Subscription subscription) { // 4
                        System.out.println("Subscribed and make a request...");
                        request(1); // 5
                    }

                    @Override
                    protected void hookOnNext(Integer value) {  // 6
                        try {
                            TimeUnit.SECONDS.sleep(1);  // 7
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Get value [" + value + "]");    // 8
                        request(1); // 9
                    }
                });
    }
}
