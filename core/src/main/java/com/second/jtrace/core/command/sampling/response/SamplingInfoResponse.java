package com.second.jtrace.core.command.sampling.response;

import com.second.jtrace.core.command.sampling.vo.ProfilerInfo;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

import java.util.List;

@Data
public class SamplingInfoResponse extends BaseResponse {

    private List<ProfilerInfo> profilerInfos;

}
