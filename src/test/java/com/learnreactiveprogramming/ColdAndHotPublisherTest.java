package com.learnreactiveprogramming;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static com.learnreactiveprogramming.util.CommonUtil.delay;

public class ColdAndHotPublisherTest {


    @Test
    void coldPublisherTest() {
        var flux = Flux.range(1, 10);

        flux.subscribe(i -> System.out.println("Subscriber 1: " + i));

        flux.subscribe(i -> System.out.println("Subscriber 2: " + i));
    }

    @Test
    void hotPublisherTest() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<Integer> connectibleFlux = flux.publish();
        connectibleFlux.connect();

        connectibleFlux.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(4000);
        connectibleFlux.subscribe(i -> System.out.println("Subscriber 2: " + i));
        delay(10000);
    }

    @Test
    void hotPublisherTestAutoConnect() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1));

        Flux<Integer> hotSource = flux.publish().autoConnect(2);

        hotSource.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(4000);
        hotSource.subscribe(i -> System.out.println("Subscriber 2: " + i));
        delay(4000);
        hotSource.subscribe(i -> System.out.println("Subscriber 3: " + i));
        delay(15000);
    }

    @Test
    void hotPublisherTestRefCount() {
        var flux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .doOnCancel(() -> {
                    System.out.println("Received Cancel Signal");
                });

        Flux<Integer> hotSource = flux.publish().refCount(2);

        var disposable1 = hotSource.subscribe(i -> System.out.println("Subscriber 1: " + i));
        delay(4000);
        var disposable2 = hotSource.subscribe(i -> System.out.println("Subscriber 2: " + i));
        delay(4000);
        disposable1.dispose();
        disposable2.dispose();
        hotSource.subscribe(i -> System.out.println("Subscriber 3: " + i));
        delay(2000);
        hotSource.subscribe(i -> System.out.println("Subscriber 4: " + i));
        delay(10000);
    }
}
