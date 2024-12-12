package com.second.jtrace.core.command.client.response;


import com.second.jtrace.core.command.client.vo.ClientInfoVO;
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
public class ClientInfoResponse extends BaseResponse {
    /**
     * 客户端信息
     */
    private ClientInfoVO clientInfo;
}
