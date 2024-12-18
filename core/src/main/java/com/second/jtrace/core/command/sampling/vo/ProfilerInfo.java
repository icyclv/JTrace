package com.second.jtrace.core.command.sampling.vo;

import lombok.Data;

@Data
public class ProfilerInfo {
    String profilerName;
    long intervalMillis;
}
