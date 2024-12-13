package com.second.jtrace.core.command.enhance.model.node;


import com.second.jtrace.core.command.enhance.model.TraceNode;

public class ThrowNode extends TraceNode {
    private String exception;
    private String message;
    private int lineNumber;

    public ThrowNode() {
        super("throw");
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
