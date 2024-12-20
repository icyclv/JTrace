package com.second.jtrace.core.command.jvm.response;

import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;


@Data
public class SysPropResponse extends BaseResponse {
    private Map<String, String> info = new LinkedHashMap<String, String>();


}
