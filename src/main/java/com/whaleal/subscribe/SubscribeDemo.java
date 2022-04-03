package com.whaleal.subscribe;

import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;

/**
 * @author wh
 *
 *
 * subscribe();
 *
 * subscribe(Consumer<? super T> consumer);
 *
 * subscribe(Consumer<? super T> consumer,
 *           Consumer<? super Throwable> errorConsumer);
 *
 * subscribe(Consumer<? super T> consumer,
 *           Consumer<? super Throwable> errorConsumer,
 *           Runnable completeConsumer);
 *
 * subscribe(Consumer<? super T> consumer,
 *           Consumer<? super Throwable> errorConsumer,
 *           Runnable completeConsumer,
 *           Consumer<? super Subscription> subscriptionConsumer);
 *
 * 在订阅（subscribe）的时候，Flux 和 Mono 使用 Java 8 lambda 表达式。 .subscribe() 方法有多种不同的方法签名，你可以传入各种不同的 lambda 形式的参数来定义回调。如下所示：
 *
 * 基于 lambda 的对 Flux 的订阅（subscribe）
 *
 *
 *
 * 订阅并触发序列。
 *  	对每一个生成的元素进行消费。
 *  	对正常元素进行消费，也对错误进行响应。
 *  	对正常元素和错误均有响应，还定义了序列正常完成后的回调。
 *  	对正常元素、错误和完成信号均有响应， 同时也定义了对该 subscribe 方法返回的 Subscription 执行的回调。
 *  	以上方法会返回一个 Subscription 的引用，如果不再需要更多元素你可以通过它来取消订阅。 取消订阅时， 源头会停止生成新的数据，并清理相关资源。取消和清理的操作在 Reactor 中是在 接口 Disposable中定义的。
 */
public class SubscribeDemo {

    public static void main( String[] args ) {

        SubscribeDemo.demo1();
        System.out.println("---------1");
        SubscribeDemo.demo2();
        System.out.println("---------2");
        SubscribeDemo.demo3();
        System.out.println("---------3");
        SubscribeDemo.demo4();
        System.out.println("---------4");
        SubscribeDemo.demo5();
        System.out.println("---------5");

    }


    /**
     * 如下是一个无参的基本方法的使用：
     * 	配置一个在订阅时会产生3个值的 Flux。
     *  最简单的订阅方式
     */
    public static void demo1(){
        Flux<Integer> ints = Flux.range(1, 3);
        ints.subscribe();
    }


    /**
     * Flux 产生了3个值。如果我们传入一个 lambda， 我们就可以看到这几个值，如下一个列子：
     * 配置一个在订阅时会产生3个值的 Flux。
     *  	订阅它并打印值。
     */
    public static void demo2(){
        Flux<Integer> ints = Flux.range(1, 3);
        ints.subscribe(i -> System.out.println(i));
    }


    /**
     * 为了演示下一个方法签名，我们故意引入一个错误，如下所示：
     *
     * 配置一个在订阅时会产生4个值的 Flux。
     *  	为了对元素进行处理，我们需要一个 map 操作。
     *  	对于多数元素，返回值本身。
     *  	对其中一个元素抛出错误。
     *  	订阅的时候定义如何进行错误处理。
     *
     *
     *  现在我们有两个 lambda 表达式：一个是用来处理正常数据，一个用来处理错误。 刚才的代码输出如下：
     *
     * 1
     * 2
     * 3
     * Error: java.lang.RuntimeException: Got to 4
     */
    public static void demo3(){
        Flux<Integer> ints = Flux.range(1, 4)
                .map(i -> {
                    if (i <= 3) return i;
                    throw new RuntimeException("Got to 4");
                });
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error: " + error));
    }


    /**
     * 下一个 subscribe 方法的签名既有错误处理，还有一个完成后的处理，如下
     * 配置一个在订阅时会产生4个值的 Flux。
     *  	订阅时定义错误和完成信号的处理。
     * 错误和完成信号都是终止信号，并且二者只会出现其中之一。为了能够最终全部正常完成，你必须处理错误信号。
     *
     * 用于处理完成信号的 lambda 是一对空的括号，因为它实际上匹配的是 Runnalbe 接口中的 run 方法， 不接受参数。刚才的代码输出如下：
     *
     * 1
     * 2
     * 3
     * 4
     * Done
     */
    public static void demo4(){
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> {System.out.println("Done");});
    }

    /**
     * 自定义的 subscriber
     */
    public static void demo5(){

        SampleSubscriber<Integer> ss = new SampleSubscriber<Integer>();
        Flux<Integer> ints = Flux.range(1, 4);
        ints.subscribe(i -> System.out.println(i),
                error -> System.err.println("Error " + error),
                () -> {System.out.println("Done");},
                s -> ss.request(10));
        ints.subscribe(ss);
    }
}


/**
 * SampleSubscriber 类继承自 BaseSubscriber，在 Reactor 中, 推荐用户扩展它来实现自定义的 Subscriber。这个类提供了一些 hook 方法，我们可以通过重写它们来调整 subscriber 的行为。 默认情况下，它会触发一个无限个数的请求，但是当你想自定义请求元素的个数的时候，扩展 BaseSubscriber 就很方便了。
 *
 * 扩展的时候通常至少要覆盖 hookOnSubscribe(Subscription subscription) 和 hookOnNext(T value) 这两个方法。这个例子中， hookOnSubscribe 方法打印一段话到标准输出，然后进行第一次请求。 然后 hookOnNext 同样进行了打印，同时逐个处理剩余请求。
 * Subscribed
 * 1
 * 2
 * 3
 * 4
 * 建议你同时重写 hookOnError、hookOnCancel，以及 hookOnComplete 方法。
 * 你最好也重写 hookFinally 方法。
 * SampleSubscribe 确实是一个最简单的实现了 请求有限个数元素的 Subscriber。
 *
 *
 * 本文档后边还会再讨论 BaseSubscriber。
 *
 * 响应式流规范定义了另一个 subscribe 方法的签名，它只接收一个自定义的 Subscriber， 没有其他的参数，如下所示：
 *
 * subscribe(Subscriber<? super T> subscriber);
 * 如果你已经有一个 Subscriber，那么这个方法签名还是挺有用的。况且，你可能还会用到它 来做一些订阅相关（subscription-related）的回调。比如，你想要自定义“背压（backpressure）” 并且自己来触发请求。
 *
 * 在这种情况下，使用 BaseSubscriber 抽象类就很方便，因为它提供了很好的配置“背压” 的方法。
 *
 * 使用 BaseSubscriber 来配置“背压”
 *
 *
 *  	BaseSubscriber 是一个抽象类，所以我们创建一个匿名内部类。
 *  	BaseSubscriber 定义了多种用于处理不同信号的 hook。它还定义了一些捕获 Subscription 对象的现成方法，这些方法可以用在 hook 中。
 *  	request(n) 就是这样一个方法。它能够在任何 hook 中，通过 subscription 向上游传递 背压请求。这里我们在开始这个流的时候请求1个元素值。
 *  	随着接收到新的值，我们继续以每次请求一个元素的节奏从源头请求值。
 *  	其他 hooks 有 hookOnComplete, hookOnError, hookOnCancel, and hookFinally （它会在流终止的时候被调用，传入一个 SignalType 作为参数）。
 *  	当你修改请求操作的时候，你必须注意让 subscriber 向上提出足够的需求， 否则上游的 Flux 可能会被“卡住”。所以 BaseSubscriber 在进行扩展的时候要覆盖 hookOnSubscribe 和 onNext，这样你至少会调用 request 一次。
 * BaseSubscriber 还提供了 requestUnbounded() 方法来切换到“无限”模式（等同于 request(Long.MAX_VALUE)）。
 *
 *
 */
class SampleSubscriber<T> extends BaseSubscriber<T> {

    public void hookOnSubscribe( Subscription subscription) {
        System.out.println("Subscribed");
        request(1);
    }

    public void hookOnNext(T value) {
        System.out.println(value);
        request(1);
    }
}