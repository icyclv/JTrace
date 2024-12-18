package com.second.jtrace.core.command.logger.vo;

import com.second.jtrace.core.command.logger.AbstractLoggerCommand;
import lombok.Data;

import java.util.List;

@Data
public class LoggerInfoVO {
    private AbstractLoggerCommand.LoggerType loggerType;
    private String className;
    private String classLoader;
    private String classLoaderHash;
    private String codeSource;

    // logger info
    private String level;
    private String effectiveLevel;

    // log4j2 only
    private String config;

    // type boolean
    private String additivity;
    private List<AppenderInfoVO> appenders;
}
