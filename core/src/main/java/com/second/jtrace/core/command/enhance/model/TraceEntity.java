package com.second.jtrace.core.command.enhance.model;

import com.second.jtrace.core.util.ThreadUtils;
import lombok.Data;


@Data
public class TraceEntity {
    protected TraceTree tree;
    protected int deep;

    public TraceEntity() {
        this.tree = createTraceTree();
        this.deep = 0;
    }

    private TraceTree createTraceTree() {
        return new TraceTree(ThreadUtils.getThreadNode(Thread.currentThread()));
    }

    public void addDeep() {
        deep++;
    }

    public void subDeep() {
        deep--;
    }
}
