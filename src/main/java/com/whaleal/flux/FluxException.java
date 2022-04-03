package com.whaleal.flux;

/**
 * @author wh
 *
 * 源端异常的几种处理方式
 *
 * 0.在 源 端进行处理，发送之前处理好
 * 1.onErrorReturn  doOnError
 * 2。subscribe
 * 3.onErrorResume
 *
 *  	如果想了解有哪些可用于错误处理的操作符，请参考 the relevant operator decision tree。
 * 在响应式流中，错误（error）是终止（terminal）事件。当有错误发生时，它会导致流序列停止， 并且错误信号会沿着操作链条向下传递，直至遇到你定义的 Subscriber 及其 onError 方法。
 *
 * 这样的错误还是应该在应用层面解决的。比如，你可能会将错误信息显示在用户界面，或者通过某个 REST 端点（endpoint）发出。因此，订阅者（subscriber）的 onError 方法是应该定义的。
 *
 *  	如果没有定义，onError 会抛出 UnsupportedOperationException。你可以接下来再 检测错误，并通过 Exceptions.isErrorCallbackNotImplemented 方法捕获和处理它。
 * Reactor 还提供了其他的用于在链中处理错误的方法，即错误处理操作（error-handling operators）。
 *
 *  	在你了解错误处理操作符之前，你必须牢记 响应式流中的任何错误都是一个终止事件。
 *  	即使用了错误处理操作符，也不会让源头流序列继续。而是将 onError 信号转化为一个 新的 序列 的开始。换句话说，它代替了被终结的 上游 流序列。
 * 现在我们来逐个看看错误处理的方法。需要的时候我们会同时用到命令式编程风格的 try 代码块来作比较。
 *
 *
 * 订阅的时候，位于链结尾的 onError 回调方法和 catch 块类似，一旦有异常，执行过程会跳入到 catch：
 *
 */
public class FluxException {
}
