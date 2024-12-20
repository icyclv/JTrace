package com.second.jtrace.core.util;


import com.second.jtrace.common.JTraceConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * @author hengyunabc 2019-02-05
 */
public class ClassLoaderUtils {
    private static Logger logger = LoggerFactory.getLogger(ClassLoaderUtils.class);

    public static Set<ClassLoader> getAllClassLoader(Instrumentation inst) {
        Set<ClassLoader> classLoaderSet = new HashSet<ClassLoader>();

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                classLoaderSet.add(classLoader);
            }
        }
        return classLoaderSet;
    }


    public static ClassLoader getClassLoader(Instrumentation inst, String hashCode) {
        if (hashCode == null || hashCode.isEmpty()) {
            return null;
        }

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                if (Integer.toHexString(classLoader.hashCode()).equals(hashCode)) {
                    return classLoader;
                }
            }
        }
        return null;
    }

    /**
     * 通过类名查找classloader
     *
     * @param inst
     * @param classLoaderClassName
     * @return
     */
    public static List<ClassLoader> getClassLoaderByClassName(Instrumentation inst, String classLoaderClassName) {
        if (classLoaderClassName == null || classLoaderClassName.isEmpty()) {
            return null;
        }
        Set<ClassLoader> classLoaderSet = getAllClassLoader(inst);
        List<ClassLoader> matchClassLoaders = new ArrayList<ClassLoader>();
        for (ClassLoader classLoader : classLoaderSet) {
            if (classLoader.getClass().getName().equals(classLoaderClassName)) {
                matchClassLoaders.add(classLoader);
            }
        }
        return matchClassLoaders;
    }

    public static String classLoaderHash(ClassLoader classLoader) {
        int hashCode = 0;
        if (classLoader == null) {
            hashCode = System.identityHashCode(classLoader);
        } else {
            hashCode = classLoader.hashCode();
        }
        if (hashCode <= 0) {
            hashCode = System.identityHashCode(classLoader);
            if (hashCode < 0) {
                hashCode = hashCode & Integer.MAX_VALUE;
            }
        }
        return Integer.toHexString(hashCode);
    }

    /**
     * Find List<ClassLoader> by the class name of ClassLoader or the return value of ClassLoader#toString().
     *
     * @param inst
     * @param classLoaderClassName
     * @param classLoaderToString
     * @return
     */
    public static List<ClassLoader> getClassLoader(Instrumentation inst, String classLoaderClassName, String classLoaderToString) {
        List<ClassLoader> matchClassLoaders = new ArrayList<ClassLoader>();
        if (StringUtils.isEmpty(classLoaderClassName) && StringUtils.isEmpty(classLoaderToString)) {
            return matchClassLoaders;
        }
        Set<ClassLoader> classLoaderSet = getAllClassLoader(inst);
        List<ClassLoader> matchedByClassLoaderToStr = new ArrayList<ClassLoader>();
        for (ClassLoader classLoader : classLoaderSet) {
            // only classLoaderClassName
            if (!StringUtils.isEmpty(classLoaderClassName) && StringUtils.isEmpty(classLoaderToString)) {
                if (classLoader.getClass().getName().equals(classLoaderClassName)) {
                    matchClassLoaders.add(classLoader);
                }
            }
            // only classLoaderToString
            else if (!StringUtils.isEmpty(classLoaderToString) && StringUtils.isEmpty(classLoaderClassName)) {
                if (classLoader.toString().equals(classLoaderToString)) {
                    matchClassLoaders.add(classLoader);
                }
            }
            // classLoaderClassName and classLoaderToString
            else {
                if (classLoader.getClass().getName().equals(classLoaderClassName)) {
                    matchClassLoaders.add(classLoader);
                }
                if (classLoader.toString().equals(classLoaderToString)) {
                    matchedByClassLoaderToStr.add(classLoader);
                }
            }
        }
        // classLoaderClassName and classLoaderToString
        if (!StringUtils.isEmpty(classLoaderClassName) && !StringUtils.isEmpty(classLoaderToString)) {
            matchClassLoaders.retainAll(matchedByClassLoaderToStr);
        }
        return matchClassLoaders;
    }

    @SuppressWarnings({"unchecked", "restriction"})
    public static URL[] getUrls(ClassLoader classLoader) {
        if (classLoader instanceof URLClassLoader) {
            try {
                return ((URLClassLoader) classLoader).getURLs();
            } catch (Throwable e) {
                logger.error("classLoader: {} getUrls error", classLoader, e);
            }
        }

        // jdk9
        if (classLoader.getClass().getName().startsWith("jdk.internal.loader.ClassLoaders$")) {
            try {
                Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                sun.misc.Unsafe unsafe = (sun.misc.Unsafe) field.get(null);

                Class<?> ucpOwner = classLoader.getClass();
                Field ucpField = null;

                // jdk 9~15: jdk.internal.loader.ClassLoaders$AppClassLoader.ucp
                // jdk 16~17: jdk.internal.loader.BuiltinClassLoader.ucp
                while (ucpField == null && !ucpOwner.getName().equals("java.lang.Object")) {
                    try {
                        ucpField = ucpOwner.getDeclaredField("ucp");
                    } catch (NoSuchFieldException ex) {
                        ucpOwner = ucpOwner.getSuperclass();
                    }
                }
                if (ucpField == null) {
                    return null;
                }

                long ucpFieldOffset = unsafe.objectFieldOffset(ucpField);
                Object ucpObject = unsafe.getObject(classLoader, ucpFieldOffset);
                if (ucpObject == null) {
                    return null;
                }

                // jdk.internal.loader.URLClassPath.path
                Field pathField = ucpField.getType().getDeclaredField("path");
                if (pathField == null) {
                    return null;
                }
                long pathFieldOffset = unsafe.objectFieldOffset(pathField);
                ArrayList<URL> path = (ArrayList<URL>) unsafe.getObject(ucpObject, pathFieldOffset);

                return path.toArray(new URL[path.size()]);
            } catch (Throwable e) {
                // ignore
                return null;
            }
        }
        return null;
    }


    /**
     * 获取classLoader的hashCode
     *
     * @param classLoader
     * @return
     */
    public static String getClassLoaderHash(ClassLoader classLoader) {
        if (classLoader == null) {
            return JTraceConstants.NONE;
        } else {
            return Integer.toHexString(classLoader.hashCode());
        }
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
                String hash = ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader());
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
            String hash = ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader());
            if (clazz.getName().equals(className)
                    && (StringUtils.isBlank(classLoaderHash) || hash.equals(classLoaderHash))) {
                clazzList.add(clazz);
            }
        }
        return clazzList;
    }


    /**
     * 搜索classloader
     *
     * @param inst
     * @param name
     * @return
     */
    public static ClassLoader getClassLoaderByName(Instrumentation inst, String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            ClassLoader classLoader = clazz.getClassLoader();
            if (classLoader != null) {
                if (classLoader.getClass().getName().equals(name)) {
                    return classLoader;
                }
            }
        }
        return null;
    }
}
