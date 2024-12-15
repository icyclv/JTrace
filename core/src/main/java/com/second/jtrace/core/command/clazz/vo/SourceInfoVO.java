package com.second.jtrace.core.command.clazz.vo;

import lombok.Data;


@Data
public class SourceInfoVO {
    /**
     * 源码
     */
    private String source;

    /**
     * class信息
     */
    private ClassInfoVO classInfo;
}
