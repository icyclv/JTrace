package com.second.jtrace.core.command.jvm.response;


import com.second.jtrace.core.command.jvm.vo.JVMInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;


@Data
public class JVMResponse extends BaseResponse {
    private JVMInfoVO jvmInfo;

}
