package com.second.jtrace.core.command.enhance.vo;

import lombok.Data;

import java.util.Date;


@Data
public class NodeInfoVO {
    public static final String NODE_TYPE_METHOD = "method";
    public static final String NODE_TYPE_THREAD = "thread";
    public static final String NODE_TYPE_EXCEPTION = "exception";

    private int deep;
    private boolean last;
    private String mark;
    private String nodeType;
    private double cost;
    private double percentage;
    private long minCost;
    private long maxCost;
    private long totalCost;
    private long times;
    private boolean maxCostNode;
    private int lineNumber;

    // method info
    private String className;
    private String methodName;

    // thread info
    private Date ts;
    private String threadName;
    private long threadId;
    private boolean daemon;
    private int priority;
    private String classLoader;

    // exception info
    private String exception;
    private String message;
}
