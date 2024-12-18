package com.second.jtrace.core.command.logger;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.logger.helper.Log4j2Helper;
import com.second.jtrace.core.command.logger.helper.Log4jHelper;
import com.second.jtrace.core.command.logger.helper.LogbackHelper;
import com.second.jtrace.core.command.logger.response.LoggerLevelResponse;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.ClassLoaderUtils;
import com.second.jtrace.core.util.StringUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;


@Data
public class LoggerLevelCommand extends AbstractLoggerCommand {
    private static final Logger logger = LoggerFactory.getLogger(LoggerLevelCommand.class);

    /**
     * Logger name
     * 可以为存在appender的logger：比如root
     * 可以为类名的全路径：比如LoggerInfoCommand
     */
    private String name;
    /**
     * 日志级别
     */
    private String level;

    private String hashCode;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return LoggerLevelResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        LoggerLevelResponse response = new LoggerLevelResponse();
        if (StringUtils.isBlank(name) || StringUtils.isBlank(level)) {
            return createExceptionResponse("name and level cannot be null!");
        }
        if (!checkLevel(level)) {
            return createExceptionResponse("level 不合法!");
        }
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        Instrumentation inst = client.getInstrumentation();
        if (hashCode != null) {
            classLoader = ClassLoaderUtils.getClassLoader(inst, hashCode);
            if (classLoader == null) {
                return createExceptionResponse("classLoader not found!");
            }
        }
        LoggerTypes loggerTypes = findLoggerTypes(inst, classLoader);

        boolean result = false;
        if(loggerTypes.contains(LoggerType.LOG4J)) {
            try {
                Boolean updateResult = updateLevel(inst, classLoader, Log4jHelper.class);
                if (Boolean.TRUE.equals(updateResult)) {
                    result = true;
                }
            } catch (Throwable e) {
                logger.warn("logger command update log4j level error", e);
            }
        }
        if(loggerTypes.contains(LoggerType.LOGBACK)) {
            try {
                Boolean updateResult = updateLevel(inst, classLoader, LogbackHelper.class);
                if (Boolean.TRUE.equals(updateResult)) {
                    result = true;
                }
            } catch (Throwable e) {
                logger.warn("logger command update logback level error", e);
            }
        }
        if(loggerTypes.contains(LoggerType.LOG4J2)) {
            try {
                Boolean updateResult = updateLevel(inst, classLoader, Log4j2Helper.class);
                if (Boolean.TRUE.equals(updateResult)) {
                    result = true;
                }
            } catch (Throwable e) {
                logger.warn("logger command update log4j2 level error", e);
            }
        }

        if (result) {
            response.setMsg("update successful.");
            return response;
        } else {
            return createExceptionResponse("update failed.");
        }
    }

    /**
     * 检查参数level的合法性
     *
     * @param level
     * @return
     */
    private boolean checkLevel(String level) {
        String s = level.toUpperCase();
        if (s.equals("ALL")) {
            return true;
        } else if (s.equals("DEBUG")) {
            return true;
        } else if (s.equals("INFO")) {
            return true;
        } else if (s.equals("WARN")) {
            return true;
        } else if (s.equals("ERROR")) {
            return true;
        } else if (s.equals("FATAL")) {
            return true;
        } else if (s.equals("OFF")) {
            return true;
        } else if (s.equals("TRACE")) {
            return true;
        } else {
            return false;
        }
    }


    private LoggerTypes findLoggerTypes(Instrumentation inst, ClassLoader classLoader) {
        LoggerTypes loggerTypes = new LoggerTypes();
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            if(classLoader == clazz.getClassLoader()) {
                updateLoggerType(loggerTypes, classLoader, clazz.getName());
            }
        }
        return loggerTypes;
    }


    private Boolean updateLevel(Instrumentation inst, ClassLoader classLoader, Class<?> helperClass) throws Exception {
        Class<?> clazz = helperClassNameWithClassLoader(classLoader, helperClass);
        Method updateLevelMethod = clazz.getMethod("updateLevel", new Class<?>[]{String.class, String.class});
        return (Boolean) updateLevelMethod.invoke(null, new Object[]{this.name, this.level});
    }
}
