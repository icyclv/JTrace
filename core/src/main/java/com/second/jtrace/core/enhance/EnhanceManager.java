package com.second.jtrace.core.enhance;


import com.second.jtrace.core.listener.AdviceListenerManager;
import com.second.jtrace.core.util.StringUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.System.arraycopy;


public class EnhanceManager {
    /**
     * 监控类转换器列表
     */
    private static List<EnhancerTransformer> watchTransformers = new CopyOnWriteArrayList<EnhancerTransformer>();
    /**
     * 跟踪类转换器列表
     */
    private static List<EnhancerTransformer> traceTransformers = new CopyOnWriteArrayList<EnhancerTransformer>();


    /**
     * 聚合转换器
     */
    private static ClassFileTransformer classFileTransformer;

    public static void init(Instrumentation instrumentation) {
        classFileTransformer = new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                for (ClassFileTransformer transformer : watchTransformers) {
                    byte[] transformResult = transformer.transform(loader, className, classBeingRedefined,
                            protectionDomain, classfileBuffer);
                    if (transformResult != null) {
                        classfileBuffer = transformResult;
                    }
                }

                for (ClassFileTransformer transformer : traceTransformers) {
                    byte[] transformResult = transformer.transform(loader, className, classBeingRedefined,
                            protectionDomain, classfileBuffer);
                    if (transformResult != null) {
                        classfileBuffer = transformResult;
                    }
                }

                return classfileBuffer;
            }
        };
        instrumentation.addTransformer(classFileTransformer, true);
    }

    /**
     * 添加转换器
     *
     * @param classFileTransformer
     * @param isTracing
     */
    public static void addTransformer(EnhancerTransformer classFileTransformer, boolean isTracing) {
        if (isTracing) {
            traceTransformers.add(classFileTransformer);
        } else {
            watchTransformers.add(classFileTransformer);
        }
    }

    /**
     * 移除转换器
     *
     * @param classFileTransformer
     */
    public static void removeTransformer(ClassFileTransformer classFileTransformer) {
        traceTransformers.remove(classFileTransformer);
        watchTransformers.remove(classFileTransformer);
    }

    /**
     * 移除转换器
     *
     * @param resetCommandId
     * @param sessionId
     * @return 返回被移除的转换器
     */
    public static Set<EnhancerTransformer> removeTransformers(String resetCommandId, String sessionId) {
        Set<EnhancerTransformer> enhancerTransformers = new HashSet<EnhancerTransformer>();
        enhancerTransformers.addAll(removeTransformers(watchTransformers, resetCommandId, sessionId));
        enhancerTransformers.addAll(removeTransformers(traceTransformers, resetCommandId, sessionId));
        return enhancerTransformers;
    }

    /**
     * 移除转换器
     *
     * @param list
     * @param resetCommandId
     * @param sessionId
     * @return 返回被移除的转换器
     */
    private static Set<EnhancerTransformer> removeTransformers(List<EnhancerTransformer> list
            , String resetCommandId, String sessionId) {
        Set<EnhancerTransformer> enhancerTransformers = new HashSet<EnhancerTransformer>();
        for (int i = 0; i < list.size(); i++) {
            EnhancerTransformer transformer = list.get(i);
            if (sessionId.equals(transformer.getSessionId())
                    && (StringUtils.isBlank(resetCommandId) || resetCommandId.equals(transformer.getCommandId()))) {
                enhancerTransformers.add(transformer);
                list.remove(i);
                i--;
            }
        }
        return enhancerTransformers;
    }


    /**
     * 注销重置
     *
     * @param instrumentation
     * @param resetCommandId
     * @param sessionId
     * @return
     */
    public static EnhancerAffect reset(Instrumentation instrumentation
            , String resetCommandId, String sessionId) {
        final EnhancerAffect affect = new EnhancerAffect();
        final Set<Class<?>> enhanceClassSet = new HashSet<Class<?>>();
        try {
            Set<EnhancerTransformer> enhancerTransformers = EnhanceManager.removeTransformers(resetCommandId, sessionId);
            for (EnhancerTransformer enhancerTransformer : enhancerTransformers) {
                enhanceClassSet.addAll(enhancerTransformer.getClassesToEnhance());
                AdviceListenerManager.removeAdviceListener(enhancerTransformer.getListener());
            }
            enhance(instrumentation, enhanceClassSet);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            for (Class<?> resetClass : enhanceClassSet) {
                EnhancerTransformer.classBytesCache.remove(resetClass);
                affect.classCnt(1);
            }
        }
        return affect;
    }

    /**
     * 重置所以的Class
     *
     * @param inst inst
     * @return 增强影响范围
     */
    public static synchronized EnhancerAffect reset(final Instrumentation inst) throws UnmodifiableClassException {

        final EnhancerAffect affect = new EnhancerAffect();

        final Set<Class<?>> enhanceClassSet = new HashSet<Class<?>>(EnhancerTransformer.classBytesCache.keySet());

        try {
            enhance(inst, enhanceClassSet);
        } finally {
            for (Class<?> resetClass : enhanceClassSet) {
                EnhancerTransformer.classBytesCache.remove(resetClass);
                affect.classCnt(1);
            }
        }

        return affect;
    }


    private static void enhance(Instrumentation inst, Set<Class<?>> classes)
            throws UnmodifiableClassException {
        int size = classes.size();
        Class<?>[] classArray = new Class<?>[size];
        arraycopy(classes.toArray(), 0, classArray, 0, size);
        if (classArray.length > 0) {
            inst.retransformClasses(classArray);
        }
    }

    public static void destroy(Instrumentation inst) {
        watchTransformers.clear();
        traceTransformers.clear();

        if (classFileTransformer != null) {
            inst.removeTransformer(classFileTransformer);
        }

    }
}
