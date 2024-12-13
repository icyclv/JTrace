package com.second.jtrace.core.command.thread.vo;

import lombok.Data;

@Data
public class LockInfoVO {
    private String className;
    private int    identityHashCode;
}
