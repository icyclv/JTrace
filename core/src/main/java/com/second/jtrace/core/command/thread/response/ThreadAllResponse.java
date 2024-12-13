package com.second.jtrace.core.command.thread.response;


import com.second.jtrace.core.command.thread.vo.ThreadInfo;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class ThreadAllResponse extends BaseResponse {
    private List<ThreadInfo> threadSampleInfos;
}
