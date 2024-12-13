package com.second.jtrace.core.command.enhance;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.EnhanceCommand;
import com.second.jtrace.core.command.enhance.response.WatchResponse;
import com.second.jtrace.core.command.enhance.vo.EnhanceInfoVO;
import com.second.jtrace.core.enhance.EnhancerAffect;
import com.second.jtrace.core.listener.AdviceListenerAdapter;
import com.second.jtrace.core.listener.WatchEnhanceListener;
import com.second.jtrace.core.response.IResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class WatchCommand extends EnhanceCommand {
    /**
     * 展开层次
     */
    private Integer expand = 1;
    /**
     * obj转换为string的长度
     */
    private Integer sizeLimit = 1024 * 1024;
    /**
     * watch方法调用前，主要用于观察参数会再方法里面修改的情况
     */
    private boolean atBefore = false;
    /**
     * watch方法正常结束后，主要用于观察返回结果
     */
    private boolean atAfter = false;
    /**
     * watch方法抛出的异常，主要用于观察异常结果
     */
    private boolean atException = false;
    /**
     * watch方法终结的时候（atAfter或者atException）
     * atBefore、atAfter、atException都为false的情况下，相当于atFinish为true
     */
    private boolean atFinish = false;
    /**
     * 是否返回target
     */
    private boolean showTarget = true;
    /**
     * 是否返回params
     */
    private boolean showParams = true;

    /**
     * 是否返回returnObj
     */
    private boolean showReturnObj = true;

    /**
     * 是否返回exception
     */
    private boolean showException = true;

    /**
     * 是否以JSON展示（针对params和targetObject）
     */
    private boolean showWithJson = false;

    @Override
    public IResponse getNormalResponse(EnhancerAffect enhancerAffect) {
        WatchResponse response = new WatchResponse();
        EnhanceInfoVO enhanceInfoVO = new EnhanceInfoVO(enhancerAffect);
        response.setEnhanceInfo(enhanceInfoVO);
        return response;
    }

    @Override
    public AdviceListenerAdapter getAdviceListener(IClient client) {
        return new WatchEnhanceListener(client, this);
    }

    @Override
    public boolean isTracing() {
        return false;
    }

    @Override
    public boolean isSkipJDKTrace() {
        return false;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return WatchResponse.class;
    }
}
