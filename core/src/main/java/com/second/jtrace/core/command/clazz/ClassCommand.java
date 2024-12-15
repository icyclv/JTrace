package com.second.jtrace.core.command.clazz;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.clazz.response.ClassResponse;
import com.second.jtrace.core.command.clazz.vo.ClassInfoVO;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.ClassLoaderUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
public class ClassCommand extends AbstractCommand {
    /**
     * 类名（模糊查找）
     */
    private String className;
    /**
     * 限制返回数
     */
    private int numberOfLimit = 100;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ClassResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        Set<Class<?>> classSet = ClassLoaderUtils.findClasses(client.getInstrumentation()
                , className);

        List<ClassInfoVO> classInfos = new ArrayList<ClassInfoVO>();
        for (Class<?> clazz : classSet) {
            classInfos.add(new ClassInfoVO(clazz));
            if (classInfos.size() >= numberOfLimit) {
                break;
            }
        }

        ClassResponse classResponse = new ClassResponse();
        classResponse.setClassInfos(classInfos);
        return classResponse;
    }
}
