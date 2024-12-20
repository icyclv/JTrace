package com.second.jtrace.core.sampling.bean;

import com.second.jtrace.core.protocol.IMessage;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class SamplingMessage implements IMessage {
    String profilerName;
    Map<String, Object> result;
}
