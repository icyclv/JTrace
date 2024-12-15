package com.second.jtrace.core.command.enhance.model;

import java.util.ArrayList;
import java.util.List;


public class TraceNode {
    /**
     * 父节点
     */
    protected TraceNode parent;

    /**
     * 子节点列表
     */
    protected List<TraceNode> children;

    /**
     * node type
     */
    private String type;
    /**
     * 备注信息
     */
    private String mark;

    public TraceNode(String type) {
        this.type = type;
    }

    public void addChild(TraceNode child) {
        if (children == null) {
            children = new ArrayList<TraceNode>();
        }
        this.children.add(child);
        child.setParent(this);
    }

    public List<TraceNode> getChildren() {
        return this.children;
    }

    public void setParent(TraceNode parent) {
        this.parent = parent;
    }

    public TraceNode parent() {
        return parent;
    }


    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getMark() {
        return mark;
    }

    public void begin() {
    }

    public void end() {
    }
}
