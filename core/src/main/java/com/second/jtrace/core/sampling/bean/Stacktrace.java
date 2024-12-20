package com.second.jtrace.core.sampling.bean;

import java.util.Arrays;

public class Stacktrace {
    private String threadName;
    private String threadState;
    private ClassAndMethod[] stack = new ClassAndMethod[0];

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public String getThreadState() {
        return threadState;
    }

    public void setThreadState(String threadState) {
        this.threadState = threadState;
    }

    public ClassAndMethod[] getStack() {
        return stack;
    }

    public void setStack(ClassAndMethod[] stack) {
        if (stack == null) {
            this.stack = new ClassAndMethod[0];
        } else {
            this.stack = stack;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stacktrace that = (Stacktrace) o;

        if (threadName != null ? !threadName.equals(that.threadName) : that.threadName != null) return false;
        if (threadState != null ? !threadState.equals(that.threadState) : that.threadState != null) return false;

        return Arrays.equals(stack, that.stack);
    }

    @Override
    public int hashCode() {
        int result = threadName != null ? threadName.hashCode() : 0;
        result = 31 * result + (threadState != null ? threadState.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(stack);
        return result;
    }
}
