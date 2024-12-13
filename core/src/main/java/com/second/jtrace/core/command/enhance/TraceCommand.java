package com.second.jtrace.core.command.enhance;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.EnhanceCommand;
import com.second.jtrace.core.command.enhance.response.TraceResponse;
import com.second.jtrace.core.command.enhance.vo.EnhanceInfoVO;
import com.second.jtrace.core.enhance.EnhancerAffect;
import com.second.jtrace.core.listener.AdviceListenerAdapter;
import com.second.jtrace.core.listener.TraceEnhanceListener;
import com.second.jtrace.core.response.IResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Data
@EqualsAndHashCode(callSuper = true)
public class TraceCommand extends EnhanceCommand {
    private static final Logger logger = LoggerFactory.getLogger(TraceCommand.class);

    /**
     * 是否加强jdk方法
     */
    protected boolean skipJDKTrace = false;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return TraceResponse.class;
    }

    @Override
    public IResponse getNormalResponse(EnhancerAffect enhancerAffect) {
        TraceResponse response = new TraceResponse();
        EnhanceInfoVO enhanceInfoVO = new EnhanceInfoVO(enhancerAffect);
        response.setEnhanceInfo(enhanceInfoVO);
        return response;
    }

    @Override
    public AdviceListenerAdapter getAdviceListener(IClient client) {
        return new TraceEnhanceListener(client, this);
    }

    @Override
    public boolean isTracing() {
        return true;
    }
}
