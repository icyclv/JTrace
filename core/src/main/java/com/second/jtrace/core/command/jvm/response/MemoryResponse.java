package com.second.jtrace.core.command.jvm.response;


import com.second.jtrace.core.command.jvm.vo.MemoryInfoVO;
import com.second.jtrace.core.protocol.MessageTypeMapper;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

import java.util.List;


@Data
public class MemoryResponse extends BaseResponse {
    private List<MemoryInfoVO> memoryInfos;


}
