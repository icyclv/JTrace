package com.second.jtrace.core.command.shutdown;

import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

@Data
public class ShutdownResponse extends BaseResponse {
    String message;

}
