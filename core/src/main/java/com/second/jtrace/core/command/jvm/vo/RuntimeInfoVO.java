package com.second.jtrace.core.command.jvm.vo;

import lombok.Data;


@Data
public class RuntimeInfoVO {
    private String osName;
    private String osVersion;
    private String javaVersion;
    private String javaHome;
    private double systemLoadAverage;
    private int processors;
    private long uptime;
    private long timestamp;
}
