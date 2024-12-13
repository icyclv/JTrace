package com.second.jtrace.core.command.enhance.vo;


import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.core.command.enhance.WatchCommand;
import com.second.jtrace.core.enhance.EnhanceAdvice;
import com.second.jtrace.core.util.ObjectUtils;
import lombok.Data;

import java.util.Date;


@Data
public class WatchInfoVO {
    private Date ts;
    private double cost;
    private Integer expand;
    private Integer sizeLimit;
    private String className;
    private String methodName;
    private String accessPoint;

    private String targetInfo;
    private String paramsInfo;
    private String returnObjInfo;
    private String exceptionInfo;

    public WatchInfoVO() {

    }

    public WatchInfoVO(EnhanceAdvice advice, WatchCommand watchCommand, double cost) {
        setTs(new Date());
        setCost(cost);
        setExpand(watchCommand.getExpand());
        setSizeLimit(watchCommand.getSizeLimit());
        setClassName(advice.getClazz().getName());
        setMethodName(advice.getMethodName());
        if (advice.isAtBefore()) {
            setAccessPoint(JTraceConstants.ACCESS_POINT_BEFORE);
        } else if (advice.isAtAfter()) {
            setAccessPoint(JTraceConstants.ACCESS_POINT_AFTER);
        } else if (advice.isAtException()) {
            setAccessPoint(JTraceConstants.ACCESS_POINT_EXCEPTION);
        }
        if (watchCommand.isShowTarget()) {
            setTargetInfo(ObjectUtils.getObjectInfo(advice.getTarget()
                    , watchCommand.getSizeLimit(), watchCommand.getExpand()));
        }
        if (watchCommand.isShowParams()) {
            setParamsInfo(ObjectUtils.getObjectInfo(advice.getParams()
                    , watchCommand.getSizeLimit(), watchCommand.getExpand(), watchCommand.isShowWithJson()));
        }
        if (watchCommand.isShowReturnObj()) {
            setReturnObjInfo(ObjectUtils.getObjectInfo(advice.getReturnObj()
                    , watchCommand.getSizeLimit(), watchCommand.getExpand(), watchCommand.isShowWithJson()));
        }
        if (watchCommand.isShowException()) {
            setExceptionInfo(ObjectUtils.getObjectInfo(advice.getException()
                    , watchCommand.getSizeLimit(), watchCommand.getExpand()));
        }
    }
}
