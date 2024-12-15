package com.second.jtrace.core.command.jvm.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GcInfoVO {
    private String name;
    private long collectionCount;
    private long collectionTime;
}
