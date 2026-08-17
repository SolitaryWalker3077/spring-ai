package com.jinan.ai;



import reactor.core.publisher.Flux;

import java.time.Duration;

public class FluxDemoTest {

    public static void main(String[] args) throws InterruptedException {
        Flux<String> flux = Flux.just("apple","banana","pear").delayElements(Duration.ofSeconds(1));
        flux.map(String::toUpperCase).map(s -> s+"-1").subscribe(System.out::println);
        Thread.sleep(5000);
    }
}
