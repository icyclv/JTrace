package com.second.jtrace.core.command.clazz.response;

import com.second.jtrace.core.command.clazz.vo.ClassInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import lombok.Data;

import java.util.List;


@Data
public class ClassResponse extends BaseResponse {
    /**
     * 类信息列表
     */
    private List<ClassInfoVO> classInfos;

}
