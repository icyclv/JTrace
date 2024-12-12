package com.second.jtrace.core.command.jvm.response;


import com.second.jtrace.core.command.jvm.vo.JVMInfoVO;
import com.second.jtrace.core.protocol.MessageTypeMapper;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class JVMResponse extends BaseResponse {
    private JVMInfoVO jvmInfo;

    @Override
    public int getMessageTypeId() {
        return MessageTypeMapper.TypeList.JVMResponse.ordinal();
    }
}
