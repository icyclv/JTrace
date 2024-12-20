package com.second.jtrace.core.sampling.profiler;


import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.core.sampling.Reporter;
import com.second.jtrace.core.sampling.bean.ClassAndMethod;
import com.second.jtrace.core.sampling.bean.Stacktrace;
import com.second.jtrace.core.sampling.bean.StacktraceMetricBuffer;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;

/**
 * This class collects stacktraces by getting thread dump via JMX, and stores the stacktraces into the given buffer.
 */
public class StacktraceCollectorProfiler implements Profiler {
    private long intervalMillis;
    private StacktraceMetricBuffer buffer;
    private String ignoreThreadNamePrefix = "";
    private int maxStringLength = JTraceConstants.MAX_STRING_LENGTH;
    private ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public StacktraceCollectorProfiler(StacktraceMetricBuffer buffer, String ignoreThreadNamePrefix) {
        this(buffer, ignoreThreadNamePrefix, JTraceConstants.MAX_STRING_LENGTH);
    }

    public StacktraceCollectorProfiler(StacktraceMetricBuffer buffer, String ignoreThreadNamePrefix, int maxStringLength) {
        this.buffer = buffer;
        this.ignoreThreadNamePrefix = ignoreThreadNamePrefix == null ? "" : ignoreThreadNamePrefix;
        this.maxStringLength = maxStringLength;
    }

    @Override
    public long getIntervalMillis() {
        return this.intervalMillis;
    }

    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void setReporter(Reporter reporter) {
        // Do nothing
    }

    @Override
    public void profile() {
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(false, false);
        if (threadInfos == null) {
            return;
        }

        for (ThreadInfo threadInfo : threadInfos) {
            String threadName = threadInfo.getThreadName();
            if (threadName == null) {
                threadName = "";
            }

            if (!ignoreThreadNamePrefix.isEmpty()
                    && threadName.startsWith(ignoreThreadNamePrefix)) {
                continue;
            }

            StackTraceElement[] stackTraceElements = threadInfo.getStackTrace();

            Stacktrace stacktrace = new Stacktrace();
            stacktrace.setThreadName(threadName);
            stacktrace.setThreadState(String.valueOf(threadInfo.getThreadState()));

            // Start from bottom of the stacktrace so we could trim top method (most nested method) if the size is too large
            int totalLength = 0;
            List<ClassAndMethod> stack = new ArrayList<>(stackTraceElements.length);
            for (int i = stackTraceElements.length - 1; i >= 0; i--) {
                StackTraceElement stackTraceElement = stackTraceElements[i];
                String className = String.valueOf(stackTraceElement.getClassName());
                String methodName = String.valueOf(stackTraceElement.getMethodName());
                stack.add(new ClassAndMethod(className, methodName));

                totalLength += className.length() + methodName.length();

                if (totalLength >= maxStringLength) {
                    stack.add(new ClassAndMethod("_stack_", "_trimmed_"));
                    break;
                }
            }

            // Reverse the stack so the top method (most nested method) is the first element of the array
            ClassAndMethod[] classAndMethodArray = new ClassAndMethod[stack.size()];
            for (int i = 0; i < stack.size(); i++) {
                classAndMethodArray[classAndMethodArray.length - 1 - i] = stack.get(i);
            }

            stacktrace.setStack(classAndMethodArray);

            buffer.appendValue(stacktrace);
        }
    }
}
