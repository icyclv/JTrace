package com.second.jtrace.core.command.logger.vo;

import lombok.Data;


@Data
public class AppenderInfoVO {
    private String className;
    private String classLoader;
    private String classLoaderHash;
    // appender info
    private String name;
    private String file;
    private String blocking;
    // type List<String>
    private String appenderRef;
    private String target;
}
