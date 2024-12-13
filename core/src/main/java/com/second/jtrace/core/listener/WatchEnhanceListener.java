package com.second.jtrace.core.listener;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.enhance.WatchCommand;
import com.second.jtrace.core.command.enhance.response.WatchAsyncResponse;
import com.second.jtrace.core.command.enhance.vo.WatchInfoVO;
import com.second.jtrace.core.enhance.EnhanceAdvice;
import com.second.jtrace.core.response.IAsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class WatchEnhanceListener extends TimesAdviceListener {
    private static final Logger logger = LoggerFactory.getLogger(WatchEnhanceListener.class);

    private WatchCommand watchCommand;

    public WatchEnhanceListener(IClient client, WatchCommand watchCommand) {
        super(client, watchCommand);
        this.watchCommand = watchCommand;
    }

    @Override
    public void before(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        // 开始计算本次方法调用耗时
        threadLocalWatch.start();
        if (watchCommand.isAtBefore()) {
            EnhanceAdvice advice = EnhanceAdvice.newForBefore(clazz, methodName, methodDesc, target, args);
            finish(advice);
        }
    }

    @Override
    public void afterReturning(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {
        EnhanceAdvice advice = EnhanceAdvice.newForAfterReturning(clazz, methodName, methodDesc, target, args, returnObject);
        if (watchCommand.isAtAfter()) {
            finish(advice);
        }
        atFinish(advice);
    }


    @Override
    public void afterThrowing(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        EnhanceAdvice advice = EnhanceAdvice.newForAfterThrowing(clazz, methodName, methodDesc, target, args, throwable);
        if (watchCommand.isAtException()) {
            finish(advice);
        }
        atFinish(advice);
    }

    /**
     * 检测atFinish的通知
     *
     * @param advice
     */
    private void atFinish(EnhanceAdvice advice) {
        if ((watchCommand.isAtFinish())
                || (!watchCommand.isAtBefore() && !watchCommand.isAtAfter() && !watchCommand.isAtException())) {
            finish(advice);
        }
    }

    @Override
    protected IAsyncResponse getAsyncResponse(EnhanceAdvice enhanceAdvice, double cost) {
        WatchAsyncResponse asyncResponse = new WatchAsyncResponse();
        WatchInfoVO watchInfoVO = new WatchInfoVO(enhanceAdvice, watchCommand, cost);
        asyncResponse.setWatchInfo(watchInfoVO);
        return asyncResponse;
    }
}
