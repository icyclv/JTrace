package com.second.jtrace.core.command.enhance;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.EnhanceCommand;
import com.second.jtrace.core.command.enhance.response.StackResponse;
import com.second.jtrace.core.command.enhance.vo.EnhanceInfoVO;
import com.second.jtrace.core.enhance.EnhancerAffect;
import com.second.jtrace.core.listener.AdviceListenerAdapter;
import com.second.jtrace.core.listener.StackEnhanceListener;
import com.second.jtrace.core.response.IResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class StackCommand extends EnhanceCommand {
    /**
     * 是否加强jdk方法
     */
    protected boolean skipJDKTrace = false;

    @Override
    public IResponse getNormalResponse(EnhancerAffect enhancerAffect) {
        StackResponse response = new StackResponse();
        EnhanceInfoVO enhanceInfoVO = new EnhanceInfoVO(enhancerAffect);
        response.setEnhanceInfo(enhanceInfoVO);
        return response;
    }

    @Override
    public AdviceListenerAdapter getAdviceListener(IClient client) {
        return new StackEnhanceListener(client, this);
    }

    @Override
    public boolean isTracing() {
        return false;
    }

    @Override
    public boolean isSkipJDKTrace() {
        return skipJDKTrace;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return StackResponse.class;
    }
}
