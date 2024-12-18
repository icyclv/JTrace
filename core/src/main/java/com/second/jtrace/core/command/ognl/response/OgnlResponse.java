package com.second.jtrace.core.command.ognl.response;

import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;


@Data
public class OgnlResponse extends BaseResponse {
    private String result;
}
