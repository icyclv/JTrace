package com.second.jtrace.core.listener;


import com.alibaba.bytekit.utils.concurrent.ConcurrentWeakKeyHashMap;
import com.second.jtrace.core.util.ClassUtils;
import com.second.jtrace.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class AdviceListenerManager {

    private static final Logger logger = LoggerFactory.getLogger(AdviceListenerManager.class);

    /**
     * 普通方法切面集合
     * key：classLoader+className+methodName+methodDesc
     * value:AdviceListener列表，AdviceListener侦听方法的before、after、exception
     */
    public static ConcurrentWeakKeyHashMap<String, List<AdviceListenerAdapter>> adviceListenerMap = new ConcurrentWeakKeyHashMap<String, List<AdviceListenerAdapter>>();
    /**
     * 方法内的调用切面集合
     * key：classLoader+owner+className+methodName+methodDesc
     * value:InvokeListener列表，InvokeListener侦听方法内调用的beforeInvoke、afterInvoke、exception）
     */
    public static ConcurrentWeakKeyHashMap<String, List<AdviceListenerAdapter>> invokeListenerMap = new ConcurrentWeakKeyHashMap<String, List<AdviceListenerAdapter>>();

    private AdviceListenerManager() {

    }

    /**
     * 查询方法的切面列表
     *
     * @param classLoader
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    public static List<AdviceListenerAdapter> queryAdviceListeners(ClassLoader classLoader, String className
            , String methodName, String methodDesc) {
        return adviceListenerMap.get(getListenerKey(classLoader, className, null, methodName, methodDesc));
    }

    /**
     * 查询方法内调用的切面列表
     *
     * @param classLoader
     * @param className
     * @param owner
     * @param methodName
     * @param methodDesc
     * @return
     */
    public static List<AdviceListenerAdapter> queryInvokeListeners(ClassLoader classLoader, String className, String owner, String methodName, String methodDesc) {
        return invokeListenerMap.get(getListenerKey(classLoader, className, owner, methodName, methodDesc));
    }

    /**
     * 注册方法切面
     *
     * @param classLoader
     * @param className
     * @param methodName
     * @param methodDesc
     * @param listener
     */
    public static void registerAdviceListener(ClassLoader classLoader, String className
            , String methodName, String methodDesc, AdviceListenerAdapter listener) {
        String key = getListenerKey(classLoader, className, null, methodName, methodDesc);
        registerAdviceListener(key, listener, adviceListenerMap);
    }

    /**
     * 注册方法调用切面
     *
     * @param classLoader
     * @param className
     * @param owner
     * @param methodName
     * @param methodDesc
     * @param listener
     */
    public static void registerInvokeListener(ClassLoader classLoader, String className, String owner
            , String methodName, String methodDesc, AdviceListenerAdapter listener) {
        String key = getListenerKey(classLoader, className, owner, methodName, methodDesc);
        registerAdviceListener(key, listener, invokeListenerMap);
    }

    private static void registerAdviceListener(String key, AdviceListenerAdapter listener
            , ConcurrentWeakKeyHashMap<String, List<AdviceListenerAdapter>> adviceListenerMap) {
        List<AdviceListenerAdapter> list = adviceListenerMap.get(key);
        if (list == null) {
            // 重要：使用CopyOnWriteArrayList规避在循环通知Listener的过程中，移除Listener导致的遍历异常
            list = new CopyOnWriteArrayList<AdviceListenerAdapter>();
            adviceListenerMap.put(key, list);
        }
        if (!list.contains(listener)) {
            list.add(listener);
        }
    }

    /**
     * 移除Listener
     *
     * @param methodListener
     */
    public static void removeAdviceListener(AdviceListenerAdapter methodListener) {
        removeAdviceListener(adviceListenerMap, methodListener);
        removeAdviceListener(invokeListenerMap, methodListener);
    }

    /**
     * 移除Listener
     *
     * @param methodListener
     */
    private static void removeAdviceListener(ConcurrentWeakKeyHashMap<String, List<AdviceListenerAdapter>> invokeListenerMap
            , AdviceListenerAdapter methodListener) {
        for (List<AdviceListenerAdapter> listeners : invokeListenerMap.values()) {
            if (listeners.contains(methodListener)) {
                listeners.remove(methodListener);
                break;
            }
        }
    }

    /**
     * 获取key
     *
     * @param classLoader
     * @param className
     * @param owner
     * @param methodName
     * @param methodDesc
     * @return
     */
    private static String getListenerKey(ClassLoader classLoader, String className, String owner
            , String methodName, String methodDesc) {
        String classLoaderHash = ClassUtils.classLoaderHash(classLoader);
        className = className.replace('/', '.');
        if (StringUtils.isBlank(owner)) {
            // AdviceListener的key里面owner为空
            return classLoaderHash + "_" + className + "_" + methodName + "_" + methodDesc;
        } else {
            // InvokeListener的key里面owner不为空
            return classLoaderHash + "_" + className + "_" + owner + "_" + methodName + "_" + methodDesc;
        }
    }
}
