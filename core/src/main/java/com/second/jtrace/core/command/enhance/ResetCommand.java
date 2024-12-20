package com.second.jtrace.core.command.enhance;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.enhance.response.ResetResponse;
import com.second.jtrace.core.command.enhance.vo.EnhanceInfoVO;
import com.second.jtrace.core.enhance.EnhanceManager;
import com.second.jtrace.core.enhance.EnhancerAffect;
import com.second.jtrace.core.response.IResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class ResetCommand extends AbstractCommand {
    /**
     * 需要清理的命令ID，为空时清理session所有的commandId
     */
    private String resetCommandId;

    @Override
    public IResponse executeForResponse(IClient client) {
        EnhancerAffect enhancerAffect = EnhanceManager.reset(client.getInstrumentation()
                , resetCommandId, getSessionId());
        ResetResponse response = new ResetResponse();
        EnhanceInfoVO enhanceInfoVO = new EnhanceInfoVO(enhancerAffect);
        response.setEnhanceInfo(enhanceInfoVO);
        return response;
    }


    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ResetResponse.class;
    }
}
