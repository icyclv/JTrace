package com.second.jtrace.core.command.sampling;

import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.sampling.response.SamplingResponse;
import com.second.jtrace.core.response.IResponse;
import lombok.Data;

@Data
public class DisableSamplingCommand extends AbstractCommand {
    String profilerName;

    @Override
    public IResponse executeForResponse(IClient client) {
        SamplingResponse response = new SamplingResponse();
        boolean result = client.disableSampling(profilerName);
        if (!result) {
            response.setStatus(SamplingResponse.STATUS_FAIL);
            response.setMsg("disable sampling fail");
        } else {
            response.setStatus(SamplingResponse.STATUS_OK);
            response.setMsg("disable sampling success");
        }
        return response;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return SamplingResponse.class;
    }
}
