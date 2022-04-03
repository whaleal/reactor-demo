package com.whaleal.flux;

import reactor.core.publisher.Flux;

/**
 * @author wh
 *
 * handle 方法有些不同，它在 Mono 和 Flux 中都有。然而，它是一个实例方法 （instance method），意思就是它要链接在一个现有的源后使用（与其他操作符一样）。
 *
 * 它与 generate 比较类似，因为它也使用 SynchronousSink，并且只允许元素逐个发出。 然而，handle 可被用于基于现有数据源中的元素生成任意值，有可能还会跳过一些元素。 这样，可以把它当做 map 与 filter 的组合。handle 方法签名如下：
 *
 * handle(BiConsumer<T, SynchronousSink<R>>)
 * 举个例子，响应式流规范允许 null 这样的值出现在序列中。假如你想执行一个类似 map 的操作，你想利用一个现有的具有映射功能的方法，但是它会返回 null，这时候怎么办呢？
 *
 * 例如，下边的方法可以用于 Integer 序列，映射为字母或 null 。
 *
 *
 *
 */
public class FluxHandle {
    public static void main( String[] args ) {

    }


    /**
     *  	映射到字母。
     *  	如果返回的是 null …​
     *  	就不会调用 sink.next 从而过滤掉。
     * 输出如下：
     *
     * M
     * I
     * T
     */
    public static void demo1(){
        Flux<String> alphabet = Flux.just(-1, 30, 13, 9, 20)
                .handle((i, sink) -> {
                    String letter = alphabet(i);
                    if (letter != null)
                        sink.next(letter);
                });

        alphabet.subscribe(System.out::println);
    }


    public static String alphabet(int letterNumber) {
        if (letterNumber < 1 || letterNumber > 26) {
            return null;
        }
        int letterIndexAscii = 'A' + letterNumber - 1;
        return "" + (char) letterIndexAscii;
    }
}
