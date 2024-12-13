package com.second.jtrace.core.command.enhance.response;


import com.second.jtrace.core.command.enhance.vo.WatchInfoVO;
import com.second.jtrace.core.response.AbstractAsyncResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class WatchAsyncResponse extends AbstractAsyncResponse {
    private WatchInfoVO watchInfo;
}
