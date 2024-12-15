package com.second.jtrace.core.command.client.response;


import com.second.jtrace.core.command.client.vo.ClientInfoVO;
import com.second.jtrace.core.protocol.MessageTypeMapper;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;



@Data
public class ClientInfoResponse extends BaseResponse {
    /**
     * 客户端信息
     */
    private ClientInfoVO clientInfo;


}
