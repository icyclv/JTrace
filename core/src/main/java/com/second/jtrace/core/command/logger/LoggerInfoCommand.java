package com.second.jtrace.core.command.logger;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.logger.helper.Log4j2Helper;
import com.second.jtrace.core.command.logger.helper.Log4jHelper;
import com.second.jtrace.core.command.logger.helper.LogbackHelper;
import com.second.jtrace.core.command.logger.helper.LoggerHelper;
import com.second.jtrace.core.command.logger.response.LoggerInfoResponse;
import com.second.jtrace.core.command.logger.vo.AppenderInfoVO;
import com.second.jtrace.core.command.logger.vo.LoggerInfoVO;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.ClassLoaderUtils;
import com.second.jtrace.core.util.ClassUtils;
import com.second.jtrace.core.util.StringUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.*;

@Data
public class LoggerInfoCommand extends AbstractLoggerCommand {

    private static final Logger logger = LoggerFactory.getLogger(LoggerInfoCommand.class);

    /**
     * Logger name
     * 为空的时候，只能查询存在appender的logger，日志配置文件里面配置的，比如root
     * 不为空的时候，可以用类的全路径进行查询，比如LoggerInfoCommand
     */
    private String name;
    /**
     * 类加载器hashcode
     */
    private String classLoaderHash;

    private boolean includeNoAppender;


    @Override
    public Class<? extends IResponse> getResponseClass() {
        return LoggerInfoResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        try{
        LoggerInfoResponse loggerInfoResponse = new LoggerInfoResponse();
        List<LoggerInfoVO> loggerInfos = new ArrayList<>();
        initLoggerInfos(client.getInstrumentation(), loggerInfos);
        loggerInfoResponse.setLoggerInfos(loggerInfos);
        if (loggerInfos.isEmpty()) {
            return createExceptionResponse("Can not find any logger info");
        }
            return loggerInfoResponse;

        }catch (Exception e){
            logger.error("LoggerInfoCommand executeForResponse error, message:{}", e.getMessage(), e);
            return createExceptionResponse("The command execution failed. If the JDK version is greater than 9, please check whether --add-opens java.base/java.lang=ALL-UNNAMED is enabled.");
        }
    }

    public void initLoggerInfos(Instrumentation instrumentation, List<LoggerInfoVO> loggerInfos) throws Exception {
        Map<ClassLoader, LoggerTypes> classLoaderLoggerMap = new LinkedHashMap<ClassLoader, LoggerTypes>();
        for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {
            String className = clazz.getName();
            ClassLoader classLoader = clazz.getClassLoader();
            if (!StringUtils.isBlank(this.classLoaderHash)
                    && !this.classLoaderHash.equals(ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader()))) {
                continue;
            }
            if (classLoader != null) {
                LoggerTypes loggerTypes = classLoaderLoggerMap.get(classLoader);
                if (loggerTypes == null) {
                    loggerTypes = new LoggerTypes();
                    classLoaderLoggerMap.put(classLoader, loggerTypes);
                }
                updateLoggerType(loggerTypes, classLoader, className);
            }
        }


        for (Map.Entry<ClassLoader, LoggerTypes> entry : classLoaderLoggerMap.entrySet()) {
            ClassLoader classLoader = entry.getKey();
            LoggerTypes loggerTypes = entry.getValue();

            if (loggerTypes.contains(LoggerType.LOG4J)) {
                Map<String, Map<String, Object>> loggerInfoMap =loggerInfo(classLoader, Log4jHelper.class);
                loggerInfos.addAll(resetLoggerInfoWithClassLoader(loggerInfoMap, LoggerType.LOG4J));
            }

            if (loggerTypes.contains(LoggerType.LOGBACK)) {
                Map<String, Map<String, Object>> loggerInfoMap = loggerInfo(classLoader, LogbackHelper.class);
                loggerInfos.addAll(resetLoggerInfoWithClassLoader(loggerInfoMap, LoggerType.LOGBACK));
            }

            if (loggerTypes.contains(LoggerType.LOG4J2)) {
                Map<String, Map<String, Object>> loggerInfoMap = loggerInfo(classLoader, Log4j2Helper.class);
                loggerInfos.addAll(resetLoggerInfoWithClassLoader(loggerInfoMap, LoggerType.LOG4J2));
            }
        }
    }
    private Map<String, Map<String, Object>> loggerInfo(ClassLoader classLoader, Class<?> helperClass) throws Exception {
        Map<String, Map<String, Object>> loggers = Collections.emptyMap();
        try {
            Class<?> clazz = helperClassNameWithClassLoader(classLoader, helperClass);
            Method getLoggersMethod = clazz.getMethod("getLoggers", new Class<?>[]{String.class, boolean.class});
            loggers = (Map<String, Map<String, Object>>) getLoggersMethod.invoke(null,
                    new Object[]{name, includeNoAppender});
        } catch (Throwable e) {
            logger.error("getLoggers error, classLoader:{}, helperClass:{}, name:{}, includeNoAppender:{}",
                    classLoader, helperClass, name, includeNoAppender, e);
            throw new Exception(e);
        }
        return loggers;

    }

    private List<LoggerInfoVO> resetLoggerInfoWithClassLoader(Map<String, Map<String, Object>> loggers, LoggerType loggerType) {
        List<LoggerInfoVO> loggerInfos = new ArrayList<LoggerInfoVO>();
        //expose attributes to json: classloader, classloaderHash
        for (Map<String, Object> loggerInfo : loggers.values()) {
            LoggerInfoVO loggerInfoVO = new LoggerInfoVO();
            Class clazz = (Class) loggerInfo.get(LoggerHelper.clazz);

            loggerInfoVO.setLoggerType(loggerType);

            loggerInfoVO.setClassLoader(getClassLoaderName(clazz.getClassLoader()));
            loggerInfoVO.setClassLoaderHash(ClassLoaderUtils.getClassLoaderHash(clazz.getClassLoader()));
            loggerInfoVO.setClassName(ClassUtils.getClassName(clazz));
            loggerInfoVO.setAdditivity(loggerInfo.get(LoggerHelper.additivity) + "");
            loggerInfoVO.setConfig(loggerInfo.get(LoggerHelper.config) + "");
            loggerInfoVO.setCodeSource(loggerInfo.get(LoggerHelper.codeSource) + "");
            loggerInfoVO.setLevel(loggerInfo.get(LoggerHelper.level) + "");
            loggerInfoVO.setEffectiveLevel(loggerInfo.get(LoggerHelper.effectiveLevel) + "");

            List<Map<String, Object>> appenders = (List<Map<String, Object>>) loggerInfo.get(LoggerHelper.appenders);
            List<AppenderInfoVO> appenderInfoList = new ArrayList<AppenderInfoVO>();
            for (Map<String, Object> appenderInfo : appenders) {
                Class appenderClass = (Class) appenderInfo.get(LoggerHelper.clazz);
                if (appenderClass != null) {
                    AppenderInfoVO appenderInfoVO = new AppenderInfoVO();
                    appenderInfoVO.setClassLoader(getClassLoaderName(appenderClass.getClassLoader()));
                    appenderInfoVO.setClassLoaderHash(ClassLoaderUtils.getClassLoaderHash(appenderClass.getClassLoader()));
                    appenderInfoVO.setClassName(ClassUtils.getClassName(appenderClass));

                    appenderInfoVO.setAppenderRef(appenderInfo.get(LoggerHelper.appenders) + "");
                    appenderInfoVO.setBlocking(appenderInfo.get(LoggerHelper.blocking) + "");
                    appenderInfoVO.setFile(appenderInfo.get(LoggerHelper.file) + "");
                    appenderInfoVO.setTarget(appenderInfo.get(LoggerHelper.target) + "");
                    appenderInfoVO.setName(appenderInfo.get(LoggerHelper.name) + "");
                    appenderInfoList.add(appenderInfoVO);
                }
            }
            loggerInfoVO.setAppenders(appenderInfoList);
            loggerInfos.add(loggerInfoVO);
        }
        return loggerInfos;
    }

    private String getClassLoaderName(ClassLoader classLoader) {
        return classLoader == null ? null : classLoader.toString();
    }

}
