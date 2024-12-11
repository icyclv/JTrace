package com.second.jtrace.core.command;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.response.IResponse;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.util.Set;


@Data
public abstract class EnhanceCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(EnhanceCommand.class);
    /**
     * 类名(精确匹配)
     */
    protected String className;
    /**
     * 方法名(可选，精确匹配)
     */
    protected String methodName;
    /**
     * 类加载器hashcode(可选，精确匹配)
     */
    protected String classLoaderHash;
    /**
     * 条件表达式(可选，格式：xxx)
     */
    protected String conditionExpress;
    /**
     * 执行次数限制
     */
    protected int numberOfLimit = 5;

    @Override
    public IResponse executeForResponse(IClient client) {
        try {
            Instrumentation instrumentation = client.getInstrumentation();
            Set<Class<?>> clazzSet = ClassLoaderUtils.findClassesOnly(instrumentation, className, classLoaderHash);

            EnhancerTransformer enhancerTransformer = new EnhancerTransformer(clazzSet
                    , methodName, getAdviceListener(client), isTracing(), isSkipJDKTrace());

            EnhancerAffect enhancerAffect = enhancerTransformer.enhance(instrumentation);
            return getNormalResponse(enhancerAffect);
        } catch (Exception ex) {
            logger.warn("enhance err {}", ex.getMessage(), ex);
            return createExceptionResponse("enhance error:" + ex.getMessage());
        }
    }

    /**
     * 获取同步返回结果
     *
     * @param enhancerAffect
     * @return
     */
    public abstract IResponse getNormalResponse(EnhancerAffect enhancerAffect);

    /**
     * 抽象方法：获取切面侦听器
     *
     * @param client
     * @return
     */
    public abstract AbstractEnhanceAdviceListener getAdviceListener(IClient client);

    /**
     * 是否需要针对方法里面的方法调用增加切面
     *
     * @return
     */
    public abstract boolean isTracing();

    /**
     * 是否忽略JDK方法增加切面
     *
     * @return
     */
    public abstract boolean isSkipJDKTrace();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getClassLoaderHash() {
        return classLoaderHash;
    }

    public void setClassLoaderHash(String classLoaderHash) {
        this.classLoaderHash = classLoaderHash;
    }

    public String getConditionExpress() {
        return conditionExpress;
    }

    public void setConditionExpress(String conditionExpress) {
        this.conditionExpress = conditionExpress;
    }

    public int getNumberOfLimit() {
        return numberOfLimit;
    }

    public void setNumberOfLimit(int numberOfLimit) {
        this.numberOfLimit = numberOfLimit;
    }
}
