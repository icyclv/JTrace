package com.second.jtrace.core.command.vmtool.response;

import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

@Data
public class VMToolResponse extends BaseResponse {
    private String returnObj;
}

