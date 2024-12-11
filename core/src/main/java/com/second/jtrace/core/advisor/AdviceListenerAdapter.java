package com.second.jtrace.core.advisor;


import com.second.jtrace.core.client.IClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * @author hengyunabc 2020-05-20
 *
 */
public abstract class AdviceListenerAdapter implements AdviceListener {
    private static final Logger logger = LoggerFactory.getLogger(AbstractEnhanceAdviceListener.class);

    protected IClient client;
    protected EnhancerTransformer enhancerTransformer;
    protected EnhanceCommand enhanceCommand;
    protected final ThreadLocalWatch threadLocalWatch = new ThreadLocalWatch();

    public AbstractEnhanceAdviceListener(IClient client, EnhanceCommand enhanceCommand) {
        this.client = client;
        this.enhanceCommand = enhanceCommand;
    }

    /**
     * 关闭侦听
     */
    protected void close() {
        EnhanceManager.removeTransformer(this.getEnhancerTransformer());
        AdviceListenerManager.removeAdviceListener(this);
    }

    /**
     * 是否超过了上限，超过之后，停止输出
     *
     * @param limit        命令执行上限
     * @param currentTimes 当前执行次数
     * @return true 如果超过或者达到了上限
     */
    protected boolean isLimitExceeded(int limit, int currentTimes) {
        return currentTimes >= limit;
    }

    /**
     * 判断条件是否满足，满足的情况下需要输出结果
     *
     * @param conditionExpress 条件表达式
     * @param enhanceAdvice    当前的advice对象
     * @param cost             本次执行的耗时
     * @return true 如果条件表达式满足
     */
    protected boolean isConditionMatch(String conditionExpress, EnhanceAdvice enhanceAdvice, double cost) {
        return StringUtils.isBlank(conditionExpress)
                || ExpressUtils.check(conditionExpress, enhanceAdvice, cost);
    }

    public EnhancerTransformer getEnhancerTransformer() {
        return enhancerTransformer;
    }

    public void setEnhancerTransformer(EnhancerTransformer enhancerTransformer) {
        this.enhancerTransformer = enhancerTransformer;
    }

    public String getSessionId() {
        return enhanceCommand.getSessionId();
    }

    public String getCommandId() {
        return enhanceCommand.getCommandId();
    }
}
