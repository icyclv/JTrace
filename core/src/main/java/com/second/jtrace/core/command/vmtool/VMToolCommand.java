package com.second.jtrace.core.command.vmtool;


import arthas.VmTool;
import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.vmtool.response.VMToolResponse;
import com.second.jtrace.core.protocol.GsonSerializer;
import com.second.jtrace.core.response.BaseResponse;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.ClassLoaderUtils;
import com.second.jtrace.core.util.ExpressUtils;
import com.second.jtrace.core.util.StringUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;


@Data
public class VMToolCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(VMToolCommand.class);

    /**
     * 类名(精确匹配)
     */
    protected String className;
    /**
     * 类加载器hashcode(可选，精确匹配)
     */
    protected String classLoaderHash;
    /**
     * 类加载器名称(可选，精确匹配)
     */
    protected String classLoaderName;
    /**
     * 表达式(可选，格式：xxx)
     */
    protected String express;

    @Override
    public IResponse executeForResponse(IClient client) {
        if (StringUtils.isBlank(classLoaderHash) && !StringUtils.isBlank(classLoaderName)) {
            ClassLoader classLoader = ClassLoaderUtils.getClassLoaderByName(client.getInstrumentation(), classLoaderName);
            classLoaderHash = ClassLoaderUtils.getClassLoaderHash(classLoader);
        }

        Set<Class<?>> classSet = ClassLoaderUtils.findClassesOnly(client.getInstrumentation(), className, classLoaderHash);


        if (classSet.size() == 0) {
            return BaseResponse.fail("Can not find" + className, VMToolResponse.class);
        }
        if (classSet.size() > 1) {
            return BaseResponse.fail("Find more than one class:" + classSet.size(), VMToolResponse.class);
        }
        Class<?> clazz = classSet.iterator().next();
        VmTool vmTool = VmTool.getInstance();
        Object[] instances = vmTool.getInstances(clazz, 10);
        if (instances.length == 0) {
            return BaseResponse.fail("Can not find any instances: "+className, VMToolResponse.class);
        }
        VMToolResponse vmToolResponse = new VMToolResponse();
        Object object = ExpressUtils.get(express, clazz.getClassLoader(), new InstancesWrapper(instances));
        vmToolResponse.setReturnObj(GsonSerializer.toJson(object));
        return vmToolResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return VMToolResponse.class;
    }

    static class InstancesWrapper {
        Object instances;

        public InstancesWrapper(Object instances) {
            this.instances = instances;
        }

        public Object getInstances() {
            return instances;
        }

        public void setInstances(Object instances) {
            this.instances = instances;
        }
    }
}
