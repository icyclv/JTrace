package com.second.jtrace.core.command.thread.vo;

import com.second.jtrace.core.util.StringUtils;
import lombok.Data;

import java.lang.management.LockInfo;
import java.lang.management.MonitorInfo;


@Data
public class ThreadInfo {
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
    protected LockInfo lockInfo;
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

    public ThreadInfo() {

    }

    public void initThreadInfo(java.lang.management.ThreadInfo threadInfo) {
        if (threadInfo == null) {
            return;
        }
        this.setId(threadInfo.getThreadId());
        this.setName(threadInfo.getThreadName());
        this.setState(threadInfo.getThreadState());

        this.setLockInfo(threadInfo.getLockInfo());
        MonitorInfo[] threadInfoLockedMonitors = threadInfo.getLockedMonitors();
        if (threadInfoLockedMonitors != null) {
            MonitorInfoVO[] monitorInfoVOS = new MonitorInfoVO[threadInfoLockedMonitors.length];
            for (int i = 0; i < threadInfoLockedMonitors.length; i++) {
                MonitorInfo monitorInfo = threadInfoLockedMonitors[i];
                MonitorInfoVO monitorInfoVO = new MonitorInfoVO();
                monitorInfoVO.setClassName(monitorInfo.getClassName());
                monitorInfoVO.setIdentityHashCode(monitorInfo.getIdentityHashCode());
                monitorInfoVO.setStackDepth(monitorInfo.getLockedStackDepth());
                monitorInfoVO.setStackFrame(monitorInfo.getLockedStackFrame());
                monitorInfoVOS[i] = monitorInfoVO;
            }
            this.setLockedMonitors(monitorInfoVOS);
        }
        LockInfo[] synchronizers = threadInfo.getLockedSynchronizers();
        if (synchronizers != null) {
            LockInfoVO[] lockInfoVOS = new LockInfoVO[synchronizers.length];
            for (int i = 0; i < synchronizers.length; i++) {
                LockInfo lockInfo = synchronizers[i];
                LockInfoVO lockInfoVO = new LockInfoVO();
                lockInfoVO.setClassName(lockInfo.getClassName());
                lockInfoVO.setIdentityHashCode(lockInfo.getIdentityHashCode());
                lockInfoVOS[i] = lockInfoVO;
            }
            this.setLockedSynchronizers(lockInfoVOS);
        }
        this.setLockName(threadInfo.getLockName());
        this.setLockOwnerId(threadInfo.getLockOwnerId());
        this.setLockOwnerName(threadInfo.getLockOwnerName());
        StackTraceElement[] trace = threadInfo.getStackTrace();
        if (trace != null) {
            StackTraceElementVO[] stackTraceElementVOS = new StackTraceElementVO[trace.length];
            for (int i = 0; i < trace.length; i++) {
                StackTraceElement stackTraceElement = trace[i];
                StackTraceElementVO stackTraceElementVO = new StackTraceElementVO();
                stackTraceElementVO.setDeclaringClass(stackTraceElement.getClassName());
                stackTraceElementVO.setFileName(stackTraceElement.getFileName());
                stackTraceElementVO.setLineNumber(stackTraceElement.getLineNumber());
                stackTraceElementVO.setMethodName(stackTraceElement.getMethodName());
                stackTraceElementVO.setNativeMethod(stackTraceElement.isNativeMethod());
                stackTraceElementVOS[i] = stackTraceElementVO;
            }
            this.setStackTraces(stackTraceElementVOS);
        }

        this.setStackTrace(StringUtils.getStackTraceString(trace));
        this.setBlockedCount(threadInfo.getBlockedCount());
        this.setBlockedTime(threadInfo.getBlockedTime());
        this.setNativeFlag(threadInfo.isInNative());
        this.setSuspended(threadInfo.isSuspended());
        this.setWaitedCount(threadInfo.getWaitedCount());
        this.setWaitedTime(threadInfo.getWaitedTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ThreadInfo threadVO = (ThreadInfo) o;

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
