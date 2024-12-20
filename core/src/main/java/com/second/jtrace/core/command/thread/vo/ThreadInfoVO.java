package com.second.jtrace.core.command.thread.vo;

import lombok.Data;


@Data
public class ThreadInfoVO {
    protected long id;
    protected String name;
    protected Thread.State state;

    // Thread info
    protected String group;
    protected int priority;
    protected boolean interrupted;
    protected boolean daemon;

    // ThreadInfo info
    protected long blockedTime;
    protected long blockedCount;
    protected long waitedTime;
    protected long waitedCount;
    protected String lockName;
    protected long lockOwnerId;
    protected String lockOwnerName;
    protected boolean nativeFlag;
    protected boolean suspended;
    protected String stackTrace;
    protected MonitorInfoVO[] lockedMonitors;
    protected LockInfoVO[] lockedSynchronizers;
    protected StackTraceElementVO[] stackTraces;

    // Sampler info
    protected double cpu;
    protected long deltaTime;
    protected long time;

    public ThreadInfoVO() {

    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadInfoVO threadVO = (ThreadInfoVO) o;

        if (id != threadVO.id) return false;
        return name != null ? name.equals(threadVO.name) : threadVO.name == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
