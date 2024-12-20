package com.second.jtrace.core.command.logger;

import com.alibaba.bytekit.utils.ReflectUtils;
import com.second.jtrace.common.IOUtils;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.logger.helper.Log4j2Helper;
import com.second.jtrace.core.command.logger.helper.Log4jHelper;
import com.second.jtrace.core.command.logger.helper.LogbackHelper;
import com.second.jtrace.core.command.logger.helper.LoggerHelper;
import com.second.jtrace.core.util.AsmRenameUtil;
import com.second.jtrace.core.util.ClassLoaderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class AbstractLoggerCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(AbstractLoggerCommand.class);

    private static byte[] LoggerHelperBytes;
    private static byte[] Log4jHelperBytes;
    private static byte[] LogbackHelperBytes;
    private static byte[] Log4j2HelperBytes;

    private static Map<Class<?>, byte[]> classToBytesMap = new HashMap<Class<?>, byte[]>();

    private static String jtraceClassLoaderHash = ClassLoaderUtils
            .classLoaderHash(AbstractLoggerCommand.class.getClassLoader());

    static {
        LoggerHelperBytes = loadClassBytes(LoggerHelper.class);
        Log4jHelperBytes = loadClassBytes(Log4jHelper.class);
        LogbackHelperBytes = loadClassBytes(LogbackHelper.class);
        Log4j2HelperBytes = loadClassBytes(Log4j2Helper.class);

        classToBytesMap.put(LoggerHelper.class, LoggerHelperBytes);
        classToBytesMap.put(Log4jHelper.class, Log4jHelperBytes);
        classToBytesMap.put(LogbackHelper.class, LogbackHelperBytes);
        classToBytesMap.put(Log4j2Helper.class, Log4j2HelperBytes);
    }

    private static byte[] loadClassBytes(Class<?> clazz) {
        try {
            InputStream stream = AbstractLoggerCommand.class.getClassLoader()
                    .getResourceAsStream(clazz.getName().replace('.', '/') + ".class");

            return IOUtils.getBytes(stream);
        } catch (IOException e) {
            // ignore
            return null;
        }
    }

    static Class<?> helperClassNameWithClassLoader(ClassLoader classLoader, Class<?> helperClass) throws Exception {
        String classLoaderHash = ClassLoaderUtils.classLoaderHash(classLoader);
        String className = helperClass.getName();
        // if want to debug, change to return className
        String helperClassName = className + jtraceClassLoaderHash + classLoaderHash;

        try {
            return classLoader.loadClass(helperClassName);
        } catch (ClassNotFoundException e) {
            try {
                byte[] helperClassBytes = AsmRenameUtil.renameClass(classToBytesMap.get(helperClass),
                        helperClass.getName(), helperClassName);
                return ReflectUtils.defineClass(helperClassName, helperClassBytes, classLoader);
            } catch (Throwable e1) {
                logger.error("loggger command try to define helper class error: " + helperClassName,
                        e1);
                throw new Exception(e1);
            }
        }

    }

    void updateLoggerType(LoggerTypes loggerTypes, ClassLoader classLoader, String className) {
        if ("org.apache.log4j.Logger".equals(className)) {
            // 判断 org.apache.log4j.AsyncAppender 是否存在，如果存在则是 log4j，不是slf4j-over-log4j
            try {
                if (classLoader.getResource("org/apache/log4j/AsyncAppender.class") != null) {
                    loggerTypes.addType(LoggerType.LOG4J);
                }
            } catch (Throwable e) {
                // ignore
            }
        } else if ("ch.qos.logback.classic.Logger".equals(className)) {
            try {
                if (classLoader.getResource("ch/qos/logback/core/Appender.class") != null) {
                    loggerTypes.addType(LoggerType.LOGBACK);
                }
            } catch (Throwable e) {
                // ignore
            }
        } else if ("org.apache.logging.log4j.Logger".equals(className)) {
            try {
                if (classLoader.getResource("org/apache/logging/log4j/core/LoggerContext.class") != null) {
                    loggerTypes.addType(LoggerType.LOG4J2);
                }
            } catch (Throwable e) {
                // ignore
            }
        }
    }

    public static enum LoggerType {
        LOG4J, LOGBACK, LOG4J2
    }

    static class LoggerTypes {
        Set<LoggerType> types = new HashSet<LoggerType>();

        public Collection<LoggerType> types() {
            return types;
        }

        public void addType(LoggerType type) {
            types.add(type);
        }

        public boolean contains(LoggerType type) {
            return types.contains(type);
        }
    }

}
