package com.second.jtrace.core.command.ognl;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.ognl.response.OgnlResponse;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.ClassLoaderUtils;
import com.second.jtrace.core.util.ExpressUtils;
import com.second.jtrace.core.util.ObjectUtils;
import lombok.Data;

import java.lang.instrument.Instrumentation;


@Data
public class OgnlCommand extends AbstractCommand {
    /**
     * ognl表达式
     */
    private String express;
    /**
     * 类加载器hash
     */
    private String classLoaderHash;
    /**
     * 展开层次
     */
    private int expand = 1;
    /**
     * 返回内容阈值
     */
    private int sizeLimit = 5 * 1024 * 1024;
    /**
     * 是否json格式返回
     */
    private boolean showWithJson;

    @Override
    public IResponse executeForResponse(IClient client) {
        Instrumentation inst = client.getInstrumentation();
        ClassLoader classLoader = ClassLoaderUtils.getClassLoader(inst, classLoaderHash);
        Object value = ExpressUtils.get(express, classLoader, null);
        OgnlResponse ognlResponse = new OgnlResponse();
        ognlResponse.setResult(ObjectUtils.getObjectInfo(value, sizeLimit, expand, isShowWithJson()));
        return ognlResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return OgnlResponse.class;
    }
}