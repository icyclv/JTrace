package com.second.jtrace.core.listener;


public interface InvokeListener {
    /**
     * 调用之前跟踪
     *
     * @param tracingClassName  调用类名
     * @param tracingMethodName 调用方法名
     * @param tracingMethodDesc 调用方法描述
     * @param tracingLineNumber 执行调用行数
     * @ 通知过程出错
     */
    void invokeBeforeTracing(
            ClassLoader classLoader,
            String tracingClassName,
            String tracingMethodName,
            String tracingMethodDesc,
            int tracingLineNumber);

    /**
     * 抛异常后跟踪
     *
     * @param tracingClassName  调用类名
     * @param tracingMethodName 调用方法名
     * @param tracingMethodDesc 调用方法描述
     * @param tracingLineNumber 执行调用行数
     * @ 通知过程出错
     */
    void invokeThrowTracing(
            ClassLoader classLoader,
            String tracingClassName,
            String tracingMethodName,
            String tracingMethodDesc,
            int tracingLineNumber);


    /**
     * 调用之后跟踪
     *
     * @param tracingClassName  调用类名
     * @param tracingMethodName 调用方法名
     * @param tracingMethodDesc 调用方法描述
     * @param tracingLineNumber 执行调用行数
     * @ 通知过程出错
     */
    void invokeAfterTracing(
            ClassLoader classLoader,
            String tracingClassName,
            String tracingMethodName,
            String tracingMethodDesc,
            int tracingLineNumber);
}
