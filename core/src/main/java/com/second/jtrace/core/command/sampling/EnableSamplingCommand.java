package com.second.jtrace.core.command.sampling;

import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.sampling.response.SamplingResponse;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.sampling.profiler.IOProfiler;
import lombok.Data;

@Data
public class EnableSamplingCommand extends AbstractCommand {
    String profilerName;
    Integer samplingInterval;
    Integer reportInterval;

    @Override
    public IResponse executeForResponse(IClient client) {
        if(profilerName == null) {
            return SamplingResponse.fail("profilerName cannot be null", getResponseClass());
        }
        if(profilerName.equals("IO") && IOProfiler.available==false) {
            return SamplingResponse.fail("IO profiler is only available on Linux platform", getResponseClass());
        }
        if(reportInterval == null) {
            return SamplingResponse.fail("reportInterval cannot be null", getResponseClass());
        }
        if("Stacktrace".equals(profilerName)   && samplingInterval == null) {
            return SamplingResponse.fail("samplingInterval cannot be null", getResponseClass());
        }
        if(reportInterval < 1000 || (samplingInterval != null && samplingInterval < 100)) {
            return SamplingResponse.fail("interval must be greater than 100", getResponseClass());
        }

        boolean result = client.enableSampling(profilerName,samplingInterval == null ? 0 : samplingInterval , reportInterval);
        SamplingResponse response = new SamplingResponse();
        response.setMsg(result ? "success" : "fail to enable sampling");
        response.setStatus(result ? 0 : -1);
        return  response;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return SamplingResponse.class;
    }
}
