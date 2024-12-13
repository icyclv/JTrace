package com.second.jtrace.core.command.enhance.response;


import com.second.jtrace.core.command.enhance.vo.EnhanceInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class BaseEnhanceResponse extends BaseResponse {
    protected EnhanceInfoVO enhanceInfo;
}
