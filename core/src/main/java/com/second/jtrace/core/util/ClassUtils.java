package com.second.jtrace.core.util;

import com.alibaba.deps.org.objectweb.asm.Type;
import com.second.jtrace.common.JTraceConstants;

import java.lang.annotation.Annotation;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.CodeSource;
import java.util.*;

/**
 *
 * @author hengyunabc 2018-10-18
 *
 */
public class ClassUtils {

    public static String getCodeSource(final CodeSource cs) {
        if (null == cs || null == cs.getLocation() || null == cs.getLocation().getFile()) {
            return JTraceConstants.NONE;
        }

        return cs.getLocation().getFile();
    }

    public static boolean isLambdaClass(Class<?> clazz) {
        return clazz.getName().contains("$$Lambda$");
    }


    /**
     * 获取类的加载文件来源
     *
     * @param clazz
     * @return
     */
    public static String getCodeSource(final Class<?> clazz) {
        CodeSource cs = clazz.getProtectionDomain().getCodeSource();
        if (null == cs || null == cs.getLocation() || null == cs.getLocation().getFile()) {
            return JTraceConstants.UNKNOWN;
        }
        return cs.getLocation().getFile();
    }

    /**
     * 获取类的类加载器列表
     *
     * @param clazz
     * @return
     */
    public static String[] getClassLoaders(Class<?> clazz) {
        if (clazz.getClassLoader() == null) {
            return null;
        }
        List<String> result = new ArrayList<String>();
        ClassLoader classLoader = clazz.getClassLoader();
        while (classLoader != null) {
            result.add(0, classLoader.toString());
            classLoader = classLoader.getParent();
        }
        return result.toArray(new String[]{});
    }

    /**
     * 获取类的类加载器
     *
     * @param clazz
     * @return
     */
    public static String getClassLoader(Class<?> clazz) {
        if (clazz.getClassLoader() == null) {
            return JTraceConstants.NONE;
        }
        return clazz.getClassLoader().toString();
    }

    public static String[] getClassNameList(Class[] classes) {
        List<String> list = new ArrayList<String>();
        for (Class anInterface : classes) {
            list.add(StringUtils.classname(anInterface));
        }
        return list.toArray(new String[0]);
    }

    public static String classLoaderHash(Class<?> clazz) {
        if (clazz == null || clazz.getClassLoader() == null) {
            return "null";
        }
        return Integer.toHexString(clazz.getClassLoader().hashCode());
    }

    public static String classLoaderHash(ClassLoader classLoader) {
        if (classLoader == null ) {
            return "null";
        }
        return Integer.toHexString(classLoader.hashCode());
    }

    /**
     * 根据类名模糊搜索类列表
     *
     * @param instrumentation
     * @param className
     * @return
     */
    public static Set<Class<?>> findClasses(Instrumentation instrumentation
            , String className) {
        if (StringUtils.isBlank(className)) {
            return Collections.emptySet();
        }
        return findClasses(instrumentation, className, null);
    }

    /**
     * 根据类名模糊搜索类列表
     *
     * @param instrumentation
     * @param className
     * @param classLoaderHash
     * @return
     */
    public static Set<Class<?>> findClasses(Instrumentation instrumentation
            , String className, String classLoaderHash) {
        Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
        Set<Class<?>> clazzSet = new LinkedHashSet<>();
        for (Class<?> clazz : loadedClasses) {
            // 检查累加器是否匹配
            if (classLoaderHash != null) {
                String hash = classLoaderHash(clazz.getClassLoader());
                if (!classLoaderHash.equals(hash)) {
                    continue;
                }
            }

            // 检查类名是否匹配
            if (clazz.getName().indexOf(className) != -1) {
                clazzSet.add(clazz);
            }
        }
        return clazzSet;
    }

    /**
     * 根据类名和累加载器hash精确匹配
     *
     * @param instrumentation
     * @param className
     * @param classLoaderHash
     * @return
     */
    public static Set<Class<?>> findClassesOnly(Instrumentation instrumentation
            , String className, String classLoaderHash) {
        Class<?>[] loadedClasses = instrumentation.getAllLoadedClasses();
        Set<Class<?>> clazzList = new LinkedHashSet<>();
        for (Class<?> clazz : loadedClasses) {
            String hash = classLoaderHash(clazz.getClassLoader());
            if (clazz.getName().equals(className)
                    && (StringUtils.isBlank(classLoaderHash) || hash.equals(classLoaderHash))) {
                clazzList.add(clazz);
            }
        }
        return clazzList;
    }


    /**
     * 翻译注解名
     *
     * @param annotations
     * @return
     */
    public static String[] getAnnotations(Annotation[] annotations) {
        List<String> list = new ArrayList<String>();
        if (annotations != null && annotations.length > 0) {
            for (Annotation annotation : annotations) {
                list.add(getClassName(annotation.annotationType()));
            }
        }
        return list.toArray(new String[0]);
    }

    /**
     * 翻译类名称
     *
     * @param clazz
     * @return
     */
    public static String getClassName(Class<?> clazz) {
        if (clazz.isArray()) {
            StringBuilder sb = new StringBuilder(clazz.getName());
            sb.delete(0, 2);
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ';') {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("[]");
            return sb.toString();
        } else {
            return clazz.getName();
        }
    }

    /**
     * 翻译Modifier名
     *
     * @param mod
     * @param splitter
     * @return
     */
    public static String modifier(int mod, char splitter) {
        StringBuilder sb = new StringBuilder();
        if (Modifier.isAbstract(mod)) {
            sb.append("abstract").append(splitter);
        }
        if (Modifier.isFinal(mod)) {
            sb.append("final").append(splitter);
        }
        if (Modifier.isInterface(mod)) {
            sb.append("interface").append(splitter);
        }
        if (Modifier.isNative(mod)) {
            sb.append("native").append(splitter);
        }
        if (Modifier.isPrivate(mod)) {
            sb.append("private").append(splitter);
        }
        if (Modifier.isProtected(mod)) {
            sb.append("protected").append(splitter);
        }
        if (Modifier.isPublic(mod)) {
            sb.append("public").append(splitter);
        }
        if (Modifier.isStatic(mod)) {
            sb.append("static").append(splitter);
        }
        if (Modifier.isStrict(mod)) {
            sb.append("strict").append(splitter);
        }
        if (Modifier.isSynchronized(mod)) {
            sb.append("synchronized").append(splitter);
        }
        if (Modifier.isTransient(mod)) {
            sb.append("transient").append(splitter);
        }
        if (Modifier.isVolatile(mod)) {
            sb.append("volatile").append(splitter);
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

}
