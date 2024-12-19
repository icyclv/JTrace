package com.second.jtrace.core.command.sampling;

import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.sampling.response.SamplingInfoResponse;
import com.second.jtrace.core.command.sampling.vo.ProfilerInfo;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.sampling.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SamplingInfoCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        List<ProfilerInfo> profilerInfoList = new ArrayList<>();
        ConcurrentHashMap<String, Profiler> profilers = client.getProfilers();
        for (String key : profilers.keySet()) {
            Profiler profiler = profilers.get(key);
            ProfilerInfo profilerInfo = new ProfilerInfo();
            profilerInfo.setProfilerName(key);
            profilerInfo.setIntervalMillis(profiler.getIntervalMillis());
            profilerInfoList.add(profilerInfo);
        }
        SamplingInfoResponse sampingInfoResponse = new SamplingInfoResponse();
        sampingInfoResponse.setProfilerInfos(profilerInfoList);
        return sampingInfoResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return SamplingInfoResponse.class;
    }
}
