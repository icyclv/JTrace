package com.second.jtrace.core.command.clazz;


import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.clazz.response.JadResponse;
import com.second.jtrace.core.command.clazz.vo.ClassInfoVO;
import com.second.jtrace.core.command.clazz.vo.SourceInfoVO;
import com.second.jtrace.core.enhance.ClassDumpTransformer;
import com.second.jtrace.core.protocol.GsonSerializer;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.ClassLoaderUtils;
import com.second.jtrace.core.util.DecompilerUtils;
import com.second.jtrace.core.util.InstrumentationUtils;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;


@Data
public class JadCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(JadCommand.class);

    private static Pattern pattern = Pattern.compile("(?m)^/\\*\\s*\\*/\\s*$" + System.getProperty("line.separator"));

    /**
     * 类名(精确匹配)
     */
    private String className;
    /**
     * 方法名(可选，精确匹配)
     */
    private String methodName;
    /**
     * 类加载器hashcode(可选，精确匹配)
     */
    private String classLoaderHash;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return JadResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {

        Set<Class<?>> clazzSet = ClassLoaderUtils.findClassesOnly(client.getInstrumentation()
                , className, classLoaderHash);

        if (clazzSet.size() == 0) {
            return createExceptionResponse("not find any classes:" + GsonSerializer.toJson(this));
        } else if (clazzSet.size() > 1) {
            return createExceptionResponse("find more than one class:" + clazzSet.size());
        } else {
            Class<?> clazz = clazzSet.iterator().next();
            SourceInfoVO sourceInfo = new SourceInfoVO();
            // 找内部类列表
            Set<Class<?>> withInnerClasses = ClassLoaderUtils.findClasses(client.getInstrumentation()
                    , clazz.getName() + "$", classLoaderHash);

            // 聚合需要反编译的类集合
            Set<Class<?>> sourceInfoSet = new HashSet<Class<?>>(withInnerClasses);
            sourceInfoSet.add(clazz);

            // 增强类：将class文件dump到文件夹
            ClassDumpTransformer transformer = new ClassDumpTransformer(sourceInfoSet);

            // 执行一次增强转换，把class文件导出到目录
            InstrumentationUtils.reTransformClasses(client.getInstrumentation(), transformer, sourceInfoSet);

            // 获取导出的class文件列表
            Map<Class<?>, File> classFiles = transformer.getDumpResult();
            File classFile = classFiles.get(clazz);

            if (classFile == null) {
                return createExceptionResponse("no file found:" + clazz.getName());
            }

            // 反编译class文件，获取源码
            String source = DecompilerUtils
                    .decompile(classFile.getAbsolutePath(), methodName);
            if (source != null) {
                source = pattern.matcher(source).replaceAll("");
            } else {
                source = JTraceConstants.UNKNOWN;
            }
            JadResponse jadResponse = new JadResponse();
            sourceInfo.setSource(source);
            sourceInfo.setClassInfo(new ClassInfoVO(clazz));
            jadResponse.setSourceInfo(sourceInfo);
            return jadResponse;
        }
    }
}
