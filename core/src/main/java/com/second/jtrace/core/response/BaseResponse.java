package com.second.jtrace.core.response;

import com.second.jtrace.core.protocol.GsonSerializer;
import lombok.Data;

/**
 * @author gy4j
 * 功能：
 * 日期：2022/11/26
 * 版本       开发者     描述
 * 1.0.0     gy4j     ...
 */
@Data
public class BaseResponse implements IResponse {
    public static final int STATUS_OK = 0;
    public static final int STATUS_FAIL = -1;

    protected String commandId;
    protected String clientId;
    protected int status;
    protected String msg;
    public static IResponse fail(String msg, Class<? extends IResponse> responseClass) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMsg(msg);
        baseResponse.setStatus(STATUS_FAIL);
        return responseClass.cast(baseResponse);
    }

    @Override
    public int getMessageTypeId() {
        return 0;
    }
}