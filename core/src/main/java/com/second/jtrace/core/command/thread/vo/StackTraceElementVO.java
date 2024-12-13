package com.second.jtrace.core.command.thread.vo;

import lombok.Data;

@Data
public class StackTraceElementVO {
    private String declaringClass;
    private String methodName;
    private String fileName;
    private int    lineNumber;
    private boolean nativeMethod;
}
