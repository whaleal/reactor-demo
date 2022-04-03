package com.whaleal.flux;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

/**
 * @author wh
 */
public class Fluxmerge {

    public static void main( String[] args ) throws InterruptedException {

        //Fluxmerge.wdemo();

        Fluxmerge.demo1();
    }


    /**
     * 这是个正确的的示例
     *
     * @throws InterruptedException
     */
    public static void demo1() throws InterruptedException {
        Flux< Integer > flux1 = Flux.< Integer >generate(emitter -> emitter.next(1))
                .subscribeOn(Schedulers.elastic());

        Flux< Integer > flux2 = Flux.< Integer >generate(emitter -> emitter.next(2))
                .subscribeOn(Schedulers.elastic());

        Flux< Integer > merged = flux1.mergeWith(flux2);
        merged.subscribe(s -> System.out.println(s));

        Thread.currentThread().join();
    }


    /**
     * 一个错误的示例
     * 这里只会打印1
     * <p>
     * 请注意，合并适用于异步源或有限源。
     * 在处理尚未在专用调度器上发布的无限源时，您必须将该源隔离在其自己的调度器中，否则合并会在订阅另一个源之前尝试将其耗尽。
     * <p>
     * Note that merge is tailored to work with asynchronous sources or finite sources.
     * When dealing with an infinite source that doesn't already publish on a dedicated Scheduler,
     * you must isolate that source in its own Scheduler,
     * as merge would otherwise attempt to drain it before subscribing to another source.
     * <p>
     * <p>
     * 换句话说 merge 的对象要么是有限源
     * 在有限源执行完毕之前 不会执行新的源
     * 单是加上调度器之后 他们之间就变得 并行了
     *
     * @throws InterruptedException
     */
    public static void wdemo() throws InterruptedException {

        Flux< Integer > flux1 = Flux.generate(emitter -> {
            emitter.next(1);
        });

        Flux< Integer > flux2 = Flux.generate(emitter -> {
            emitter.next(2);

        });

        Flux< Integer > merged = flux1.mergeWith(flux2);
        merged.subscribe(s -> System.out.println(s));

        Thread.currentThread().join();
    }

}
