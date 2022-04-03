package com.whaleal.flux;

import reactor.core.publisher.Flux;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 最简单的创建 Flux 的方式就是使用 generate 方法。
 *
 * 这是一种 同步地， 逐个地 产生值的方法，意味着 sink 是一个 SynchronousSink 而且其 next() 方法在每次回调的时候最多只能被调用一次。你也可以调用 error(Throwable) 或者 complete()，不过是可选的。
 *
 * 最有用的一种方式就是同时能够记录一个状态值（state），从而在使用 sink 发出下一个元素的时候能够 基于这个状态值去产生元素。此时生成器（generator）方法就是一个 BiFunction<S, SynchronousSink<T>, S>， 其中 <S> 是状态对象的类型。你需要提供一个 Supplier<S> 来初始化状态值，而生成器需要 在每一“回合”生成元素后返回新的状态值（供下一回合使用）。
 *
 * 例如我们使用一个 int 作为状态值。
 *
 * 基于状态值的 generate 示例
 *
 * @author wh
 *
 * 这个只能同步 的模式
 */
public class FluxGenerate {


    public static void main( String[] args ) {

    }


    /**
     * 最有用的一种方式就是同时能够记录一个状态值（state），从而在使用 sink 发出下一个元素的时候能够 基于这个状态值去产生元素。
     * 此时生成器（generator）方法就是一个 BiFunction<S, SynchronousSink<T>, S>， 其中 <S> 是状态对象的类型。
     * 你需要提供一个 Supplier<S> 来初始化状态值，而生成器需要 在每一“回合”生成元素后返回新的状态值（供下一回合使用）。
     *
     * 例如我们使用一个 int 作为状态值。
     *
     * 基于状态值的 generate 示例
     *
     *
     *  	初始化状态值（state）为0。
     *  	我们基于状态值 state 来生成下一个值（state 乘以 3）。
     *  	我们也可以用状态值来决定什么时候终止序列。
     *  	返回一个新的状态值 state，用于下一次调用。
     * 上面的代码生成了“3 x”的乘法表：
     *
     * 3 x 0 = 0
     * 3 x 1 = 3
     * 3 x 2 = 6
     * 3 x 3 = 9
     * 3 x 4 = 12
     * 3 x 5 = 15
     * 3 x 6 = 18
     * 3 x 7 = 21
     * 3 x 8 = 24
     * 3 x 9 = 27
     * 3 x 10 = 30
     *
     */
    public static void demo1(){
        Flux<String> flux = Flux.generate(
                () -> 0,
                (state, sink) -> {
                    sink.next("3 x " + state + " = " + 3*state);
                    if (state == 10) sink.complete();
                    return state + 1;
                });
    }


    /**
     * 我们也可以使用可变（mutable）类型（译者注：如上例，原生类型及其包装类，以及String等属于不可变类型） 的 <S>。
     * 上边的例子也可以用 AtomicLong 作为状态值，在每次生成后改变它的值。
     *
     * 可变类型的状态变量
     *
     * 这次我们初始化一个可变类型的状态值。
     *  	改变状态值。
     *  	返回 同一个 实例作为新的状态值。
     *  	如果状态对象需要清理资源，可以使用 generate(Supplier<S>, BiFunction, Consumer<S>) 这个签名方法来清理状态对象（译者注：Comsumer 在序列终止才被调用）。
     */
    public static void demo2(){
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3*i);
                    if (i == 10) sink.complete();
                    return state;
                });
    }


    /**
     * 在 generate 方法中增加 Consumer 的例子：
     *
     * 如果 state 使用了数据库连接或者其他需要最终进行清理的资源，
     * 这个 Consumer lambda 可以用来在最后关闭连接或完成相关的其他清理任务。
     */
    public static void demo3(){
        Flux<String> flux = Flux.generate(
                AtomicLong::new,
                (state, sink) -> {
                    long i = state.getAndIncrement();
                    sink.next("3 x " + i + " = " + 3*i);
                    if (i == 10) sink.complete();
                    return state;
                }, (state) -> System.out.println("state: " + state));
    }

}
