package com.second.jtrace.core.command.jvm.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;


@Data
public class JVMInfoVO {
    private Map<String, String> fileDescriptorInfo = new LinkedHashMap<String, String>();
    private Map<String, String> runtimeInfo = new LinkedHashMap<String, String>();
    private Map<String, String> classLoadingInfo = new LinkedHashMap<String, String>();
    private Map<String, String> compilationInfo = new LinkedHashMap<String, String>();
    private Map<String, String> garbageCollectorInfo = new LinkedHashMap<String, String>();
    private Map<String, String> memoryManagerInfo = new LinkedHashMap<String, String>();
    private Map<String, String> memoryInfo = new LinkedHashMap<String, String>();
    private Map<String, String> operatingSystemInfo = new LinkedHashMap<String, String>();
    private Map<String, String> threadInfo = new LinkedHashMap<String, String>();
}
