package com.second.jtrace.core.command.clazz.response;


import com.second.jtrace.core.command.clazz.vo.ClassLoaderInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

import java.util.List;


@Data
public class ClassLoaderResponse extends BaseResponse {
    /**
     * 类加载器列表
     */
    private List<ClassLoaderInfoVO> classLoaderInfos;
}
