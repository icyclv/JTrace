package com.second.jtrace.core.response;

import com.second.jtrace.core.protocol.GsonSerializer;
import com.second.jtrace.core.protocol.MessageTypeMapper;
import lombok.Data;


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
        return GsonSerializer.fromJson(GsonSerializer.toJson(baseResponse), responseClass);
    }

    public static IResponse ok(String msg, Class<? extends IResponse> responseClass) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMsg(msg);
        baseResponse.setStatus(STATUS_OK);
        return GsonSerializer.fromJson(GsonSerializer.toJson(baseResponse), responseClass);
    }


}