package com.second.jtrace.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Set;


public class InstrumentationUtils {
    private static final Logger logger = LoggerFactory.getLogger(InstrumentationUtils.class);

    private InstrumentationUtils() {

    }

    /**
     * 类增强执行（1次执行，添加->执行->卸载）
     *
     * @param inst
     * @param transformer
     * @param classSet
     */
    public static void reTransformClasses(Instrumentation inst,
                                          ClassFileTransformer transformer,
                                          Set<Class<?>> classSet) {
        try {
            inst.addTransformer(transformer, true);
            for (Class<?> clazz : classSet) {
                if (ClassUtils.isLambdaClass(clazz)) {
                    continue;
                }
                try {
                    inst.retransformClasses(clazz);
                } catch (Throwable e) {
                    logger.warn("reTransformClasses class error, name: {}", clazz.getName(), e);
                }
            }
        } finally {
            inst.removeTransformer(transformer);
        }
    }
}
