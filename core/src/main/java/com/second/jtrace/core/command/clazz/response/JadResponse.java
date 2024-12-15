package com.second.jtrace.core.command.clazz.response;


import com.second.jtrace.core.command.clazz.vo.SourceInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;


@Data
public class JadResponse extends BaseResponse {
    /**
     * 类源码信息
     */
    private SourceInfoVO sourceInfo;
}
