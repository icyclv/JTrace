package com.second.jtrace.core.command.jvm.vo;

import com.sun.management.VMOption;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VMOptionVO {
    private String name;
    private String value;
    private boolean writeable;
    private String origin;



}
