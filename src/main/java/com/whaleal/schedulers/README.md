4.5. 调度器（Schedulers）
Reactor， 就像 RxJava，也可以被认为是 并发无关（concurrency agnostic） 的。意思就是， 它并不强制要求任何并发模型。更进一步，它将选择权交给开发者。不过，它还是提供了一些方便 进行并发执行的库。

在 Reactor 中，执行模式以及执行过程取决于所使用的 Scheduler。 Scheduler 是一个拥有广泛实现类的抽象接口。Schedulers 类提供的静态方法用于达成如下的执行环境：

当前线程（Schedulers.immediate()）

可重用的单线程（Schedulers.single()）。注意，这个方法对所有调用者都提供同一个线程来使用， 直到该调度器（Scheduler）被废弃。如果你想使用专一的线程，就对每一个调用使用 Schedulers.newSingle()。

弹性线程池（Schedulers.elastic()。它根据需要创建一个线程池，重用空闲线程。线程池如果空闲时间过长 （默认为 60s）就会被废弃。对于 I/O 阻塞的场景比较适用。 Schedulers.elastic() 能够方便地给一个阻塞 的任务分配它自己的线程，从而不会妨碍其他任务和资源，见 如何包装一个同步阻塞的调用？。

固定大小线程池（Schedulers.parallel()）。所创建线程池的大小与 CPU 个数等同。

此外，你还可以使用 Schedulers.fromExecutorService(ExecutorService) 基于现有的 ExecutorService 创建 Scheduler。（虽然不太建议，不过你也可以使用 Executor 来创建）。你也可以使用 newXXX 方法来创建不同的调度器。比如 Schedulers.newElastic(yourScheduleName) 创建一个新的名为 yourScheduleName 的弹性调度器。

 	操作符基于非阻塞算法实现，从而可以利用到某些调度器的工作窃取（work stealing） 特性的好处。
一些操作符默认会使用一个指定的调度器（通常也允许开发者调整为其他调度器）例如， 通过工厂方法 Flux.interval(Duration.ofMillis(300)) 生成的每 300ms 打点一次的 Flux<Long>， 默认情况下使用的是 Schedulers.parallel()，下边的代码演示了如何将其装换为 Schedulers.single()：

Flux.interval(Duration.ofMillis(300), Schedulers.newSingle("test"))
Reactor 提供了两种在响应式链中调整调度器 Scheduler 的方法：publishOn 和 subscribeOn。 它们都接受一个 Scheduler 作为参数，从而可以改变调度器。但是 publishOn 在链中出现的位置 是有讲究的，而 subscribeOn 则无所谓。要理解它们的不同，你首先要理解 nothing happens until you subscribe()。

在 Reactor 中，当你在操作链上添加操作符的时候，你可以根据需要在 Flux 和 Mono 的实现中包装其他的 Flux 和 Mono。一旦你订阅（subscribe）了它，一个 Subscriber 的链 就被创建了，一直向上到第一个 publisher 。这些对开发者是不可见的，开发者所能看到的是最外一层的 Flux （或 Mono）和 Subscription，但是具体的任务是在中间这些跟操作符相关的 subscriber 上处理的。

基于此，我们仔细研究一下 publishOn 和 subscribeOn 这两个操作符：

publishOn 的用法和处于订阅链（subscriber chain）中的其他操作符一样。它将上游 信号传给下游，同时执行指定的调度器 Scheduler 的某个工作线程上的回调。 它会 改变后续的操作符的执行所在线程 （直到下一个 publishOn 出现在这个链上）。

subscribeOn 用于订阅（subscription）过程，作用于那个向上的订阅链（发布者在被订阅 时才激活，订阅的传递方向是向上游的）。所以，无论你把 subscribeOn 至于操作链的什么位置， 它都会影响到源头的线程执行环境（context）。 但是，它不会影响到后续的 publishOn，后者仍能够切换其后操作符的线程执行环境。

 	只有操作链中最早的 subscribeOn 调用才算数。