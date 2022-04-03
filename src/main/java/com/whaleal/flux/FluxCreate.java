package com.whaleal.flux;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 *
 * 作为一个更高级的创建 Flux 的方式， create 方法的生成方式既可以是同步， 也可以是异步的，并且还可以每次发出多个元素。
 *
 * 该方法用到了 FluxSink，后者同样提供 next，error 和 complete 等方法。
 * 与 generate 不同的是，create 不需要状态值，另一方面，它可以在回调中触发 多个事件（即使是在未来的某个时间）。
 *
 * @author wh
 *
 * 这个可以同步可以异步
 *
 */
public class FluxCreate {
    public static void main( String[] args ) {

    }


    /**
     * 使用 create 方法将其转化为响应式类型 Flux<T>：
     *
     * 桥接 MyEventListener。
     *  	每一个 chunk 的数据转化为 Flux 中的一个元素。
     *  	processComplete 事件转换为 onComplete。
     *  	所有这些都是在 myEventProcessor 执行时异步执行的。
     *
     * 此外，既然 create 可以是异步地，并且能够控制背压，你可以通过提供一个 OverflowStrategy 来定义背压行为。
     *
     * IGNORE： 完全忽略下游背压请求，这可能会在下游队列积满的时候导致 IllegalStateException。
     *
     * ERROR： 当下游跟不上节奏的时候发出一个 IllegalStateException 的错误信号。
     *
     * DROP：当下游没有准备好接收新的元素的时候抛弃这个元素。
     *
     * LATEST：让下游只得到上游最新的元素。
     *
     * BUFFER：（默认的）缓存所有下游没有来得及处理的元素（这个不限大小的缓存可能导致 OutOfMemoryError）。
     *
     */
    public static void demo1(){
        /*Flux<String> bridge = Flux.create(sink -> {
            myEventProcessor.register(
                    new MyEventListener<String>() {

                        public void onDataChunk( List<String> chunk) {
                            for(String s : chunk) {
                                sink.next(s);
                            }
                        }

                        public void processComplete() {
                            sink.complete();
                        }
                    });
        });*/
    }


    /**
     * 推送（push）模式
     *
     * create 的一个变体是 push，适合生成事件流。与 create`类似，`push 也可以是异步地， 并且能够使用以上各种溢出策略（overflow strategies）管理背压。
     * 每次只有一个生成线程可以调用 next，complete 或 error。
     *
     * 桥接 SingleThreadEventListener API。
     *  	在监听器所在线程中，事件通过调用 next 被推送到 sink。
     *  	complete 事件也在同一个线程中。
     *  	error 事件也在同一个线程中。
     */
    public static void demo2(){
        /*Flux<String> bridge = Flux.push(sink -> {
            myEventProcessor.register(
                    new SingleThreadEventListener<String>() {

                        public void onDataChunk(List<String> chunk) {
                            for(String s : chunk) {
                                sink.next(s);
                            }
                        }

                        public void processComplete() {
                            sink.complete();
                        }

                        public void processError(Throwable e) {
                            sink.error(e);
                        }
                    });
        });*/

    }


    /**
     * 不像 push，create 可以用于 push 或 pull 模式，因此适合桥接监听器的 的 API，因为事件消息会随时异步地到来。
     * 回调方法 onRequest 可以被注册到 FluxSink 以便跟踪请求。
     * 这个回调可以被用于从源头请求更多数据，或者通过在下游请求到来 的时候传递数据给 sink 以实现背压管理。
     * 这是一种推送/拉取混合的模式， 因为下游可以从上游拉取已经就绪的数据，上游也可以在数据就绪的时候将其推送到下游。
     *
     * 	当有请求的时候取出一个 message。
     *  	如果有就绪的 message，就发送到 sink。
     *  	后续异步到达的 message 也会被发送给 sink。
     */
    public static void  demo3(){
        /*Flux<String> bridge = Flux.create(sink -> {
            myMessageProcessor.register(
                    new MyMessageListener<String>() {

                        public void onMessage(List<String> messages) {
                            for(String s : messages) {
                                sink.next(s);
                            }
                        }
                    });
            sink.onRequest(n -> {
                List<String> messages = myMessageProcessor.request(n);
                for(String s : message) {
                    sink.next(s);
                }
            });
    });*/
    }

    /**
     *
     * onDispose 和 onCancel 这两个回调用于在被取消和终止后进行清理工作。
     * onDispose 可用于在 Flux 完成，有错误出现或被取消的时候执行清理。
     * onCancel 只用于针对“取消”信号执行相关操作，会先于 onDispose 执行。
     *
     *  	onCancel 在取消时被调用。
     *  	onDispose 在有完成、错误和取消时被调用。
     */
    public static void demo4(){
          /*  Flux<String> bridge = Flux.create(sink -> {
                sink.onRequest(n -> channel.poll(n))
                        .onCancel(() -> channel.cancel())
                        .onDispose(() -> channel.close());
            });*/
    }
}

interface MyEventListener<T> {
    void onDataChunk(List<T> chunk);
    void processComplete();
}
