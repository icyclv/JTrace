package com.second.jtrace.core.command.jvm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MemoryInfoVO {
    private String type;
    private String name;
    private long used;
    private long total;
    private long max;
}