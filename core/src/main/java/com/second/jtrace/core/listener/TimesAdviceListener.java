package com.second.jtrace.core.listener;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.EnhanceCommand;
import com.second.jtrace.core.command.enhance.model.TraceEntity;
import com.second.jtrace.core.enhance.EnhanceAdvice;
import com.second.jtrace.core.response.IAsyncResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;


public abstract class TimesAdviceListener extends AdviceListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(TimesAdviceListener.class);
    protected final ThreadLocal<TraceEntity> threadLocalTraceEntity = new ThreadLocal<TraceEntity>();
    protected AtomicInteger times = new AtomicInteger(0);

    public TimesAdviceListener(IClient client, EnhanceCommand enhanceCommand) {
        super(client, enhanceCommand);
    }

    /**
     * 获取threadLocal里面的traceEntity
     *
     * @return
     */
    protected TraceEntity getThreadLocalTraceEntity() {
        TraceEntity traceEntity = threadLocalTraceEntity.get();
        if (traceEntity == null) {
            traceEntity = new TraceEntity();
            threadLocalTraceEntity.set(traceEntity);
        }
        return traceEntity;
    }

    /**
     * 结束
     */
    protected void finish(EnhanceAdvice enhanceAdvice) {
        double cost = threadLocalWatch.costInMillis();
        try {
            boolean conditionResult = isConditionMatch(enhanceCommand.getConditionExpress(), enhanceAdvice, cost);
            if (conditionResult) {
                // 是否到达数量限制
                if (isLimitExceeded(enhanceCommand.getNumberOfLimit(), times.get())) {
                    close();
                } else {
                    IAsyncResponse response = getAsyncResponse(enhanceAdvice, cost);
                    client.write(enhanceCommand, response);
                }
                // 满足输出条件
                times.getAndIncrement();
            }
        } catch (Exception e) {
            logger.warn("finish failed.", e);
        } finally {
            threadLocalTraceEntity.remove();
        }
    }

    protected abstract IAsyncResponse getAsyncResponse(EnhanceAdvice enhanceAdvice, double cost);
}
