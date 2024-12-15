package com.second.jtrace.core.command.jvm.response;

import com.second.jtrace.core.protocol.MessageTypeMapper;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;


@Data
public class HeapDumpResponse extends BaseResponse {
    private String filePath;

    private byte[] fileContent;


}
