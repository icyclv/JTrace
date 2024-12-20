package com.second.jtrace.core.listener;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.EnhanceCommand;
import com.second.jtrace.core.enhance.EnhanceAdvice;
import com.second.jtrace.core.enhance.EnhanceManager;
import com.second.jtrace.core.enhance.EnhancerTransformer;
import com.second.jtrace.core.util.ExpressUtils;
import com.second.jtrace.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author hengyunabc 2020-05-20
 */
public abstract class AdviceListenerAdapter implements AdviceListener {
    private static final Logger logger = LoggerFactory.getLogger(AdviceListenerAdapter.class);
    protected final ThreadLocalWatch threadLocalWatch = new ThreadLocalWatch();
    protected IClient client;
    protected EnhancerTransformer enhancerTransformer;
    protected EnhanceCommand enhanceCommand;

    public AdviceListenerAdapter(IClient client, EnhanceCommand enhanceCommand) {
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
     * 是否满足条件表达式
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
