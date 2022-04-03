package com.whaleal.flux;

import reactor.core.publisher.Flux;

import java.util.Arrays;

/**
 * @author wh
 */
public class MutiSubscribe {

    public static void main( String[] args ) {
        MutiSubscribe.demo1();
    }

    /**
     * blue
     * Subscriber 1 to Composed MapAndFilter :blue
     * green
     * Subscriber 1 to Composed MapAndFilter :green
     * orange
     * Subscriber 1 to Composed MapAndFilter :orange
     * purple
     * Subscriber 1 to Composed MapAndFilter :purple
     * blue
     * Subscriber 2 to Composed MapAndFilter: blue
     * green
     * Subscriber 2 to Composed MapAndFilter: green
     * orange
     * Subscriber 2 to Composed MapAndFilter: orange
     * purple
     * Subscriber 2 to Composed MapAndFilter: purple
     *
     * 示例中两个 subscribe  都会收到全量的消息
     */
    public static void demo1(){
        Flux<String> composedFlux =
                Flux.fromIterable(Arrays.asList("blue", "green", "orange", "purple"))
                        .doOnNext(System.out::println);

        composedFlux.subscribe(d -> System.out.println("Subscriber 1 to Composed MapAndFilter :"+d));
        composedFlux.subscribe(d -> System.out.println("Subscriber 2 to Composed MapAndFilter: "+d));
    }
}
