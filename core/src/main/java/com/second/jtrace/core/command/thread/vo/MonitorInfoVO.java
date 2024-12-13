package com.second.jtrace.core.command.thread.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MonitorInfoVO extends LockInfoVO {
    private int    stackDepth;
    private StackTraceElement stackFrame;

}
