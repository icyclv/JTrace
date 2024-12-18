package com.second.jtrace.core.command.logger.response;

import com.second.jtrace.core.command.logger.vo.LoggerInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

import java.util.List;


@Data
public class LoggerInfoResponse extends BaseResponse {
    private List<LoggerInfoVO> loggerInfos;
}
