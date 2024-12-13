package com.second.jtrace.core.listener;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.enhance.StackCommand;
import com.second.jtrace.core.command.enhance.response.StackAsyncResponse;
import com.second.jtrace.core.command.enhance.vo.StackInfoVO;
import com.second.jtrace.core.enhance.EnhanceAdvice;
import com.second.jtrace.core.response.IAsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class StackEnhanceListener extends TimesAdviceListener {
    private static final Logger logger = LoggerFactory.getLogger(StackEnhanceListener.class);

    public StackEnhanceListener(IClient client, StackCommand command) {
        super(client, command);
    }

    @Override
    public void before(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        // 开始计算本次方法调用耗时
        threadLocalWatch.start();
    }

    @Override
    public void afterReturning(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {
        final EnhanceAdvice advice = EnhanceAdvice.newForAfterReturning(clazz, methodName, methodDesc, target, args, returnObject);
        finish(advice);
    }

    @Override
    public void afterThrowing(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        final EnhanceAdvice advice = EnhanceAdvice.newForAfterThrowing(clazz, methodName, methodDesc, target, args, throwable);
        finish(advice);
    }

    @Override
    protected IAsyncResponse getAsyncResponse(EnhanceAdvice enhanceAdvice, double cost) {
        StackAsyncResponse stackAsyncResponse = new StackAsyncResponse();
        StackInfoVO stackInfo = new StackInfoVO(cost);
        stackAsyncResponse.setStackInfo(stackInfo);
        return stackAsyncResponse;
    }
}
