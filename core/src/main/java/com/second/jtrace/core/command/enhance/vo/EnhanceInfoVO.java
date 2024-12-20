package com.second.jtrace.core.command.enhance.vo;

import com.second.jtrace.core.enhance.EnhancerAffect;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Data
public class EnhanceInfoVO {
    private long cost;
    private int classCnt;
    private int methodCnt;
    private String stackErrorTrace;
    private List<String> classDumpPaths;
    private List<String> methods;

    public EnhanceInfoVO() {

    }

    public EnhanceInfoVO(EnhancerAffect enhancerAffect) {
        this.cost = enhancerAffect.cost();
        this.classCnt = enhancerAffect.classCnt();
        this.methodCnt = enhancerAffect.methodCnt();
        if (enhancerAffect.getThrowable() != null) {
            this.stackErrorTrace = enhancerAffect.getThrowable().toString();
        }
        this.classDumpPaths = new ArrayList<String>();
        if (enhancerAffect.getClassDumpFiles() != null) {
            for (File f : enhancerAffect.getClassDumpFiles()) {
                this.classDumpPaths.add(f.getAbsolutePath());
            }
        }
        this.methods = enhancerAffect.getMethods();
    }
}
