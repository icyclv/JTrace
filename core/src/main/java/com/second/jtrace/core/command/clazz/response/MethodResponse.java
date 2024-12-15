package com.second.jtrace.core.command.clazz.response;


import com.second.jtrace.core.command.clazz.vo.MethodInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

import java.util.List;


@Data
public class MethodResponse extends BaseResponse {
    private List<MethodInfoVO> methodInfos;
}
