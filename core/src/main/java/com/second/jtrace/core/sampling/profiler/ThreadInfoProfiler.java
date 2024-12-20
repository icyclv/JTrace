package com.second.jtrace.core.sampling.profiler;


import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.core.sampling.Reporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

/**
 * ThreadInfoProfiler is used to Collects the Thread Related Metrics.
 */
public class ThreadInfoProfiler implements Profiler {
    public final static String PROFILER_NAME = "ThreadInfo";
    private final static Logger logger = LoggerFactory.getLogger(ThreadInfoProfiler.class);
    private long intervalMillis = JTraceConstants.DEFAULT_METRIC_INTERVAL;

    private ThreadMXBean threadMXBean;
    private long previousTotalStartedThreadCount = 0L; // to keep track of Total Thread.

    private Reporter reporter;

    private boolean available;

    public ThreadInfoProfiler(Reporter reporter) {
        setReporter(reporter);
        init();
    }

    private void init() {
        try {
            this.threadMXBean = ManagementFactory.getThreadMXBean();
            this.available = true;
        } catch (Throwable ex) {
            logger.warn("Failed to get Thread MXBean", ex);
            this.available = false;
        }

    }

    @Override
    public long getIntervalMillis() {
        return intervalMillis;
    }

    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    @Override
    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void profile() {
        if (!available) {
            return;
        }

        long totalStartedThreadCount = 0L; // total Thread created so far since JVm Launch.
        int liveThreadCount = 0; // Number of thread which are currently active.
        int peakThreadCount = 0; // the peak live thread count since the Java virtual machine started or peak was reset
        long newThreadCount = 0; // Number of new thread created since last time time the metrics was created.
        // This is a Derived metrics from previous data point.
        if (threadMXBean != null) {
            liveThreadCount = threadMXBean.getThreadCount();
            peakThreadCount = threadMXBean.getPeakThreadCount();
            totalStartedThreadCount = threadMXBean.getTotalStartedThreadCount();
            newThreadCount = totalStartedThreadCount - this.previousTotalStartedThreadCount;
            this.previousTotalStartedThreadCount = totalStartedThreadCount;
        }

        Map<String, Object> map = new HashMap<>();

        map.put("epochMillis", System.currentTimeMillis());


        map.put("totalStartedThreadCount", totalStartedThreadCount);
        map.put("newThreadCount", newThreadCount);
        map.put("liveThreadCount", liveThreadCount);
        map.put("peakThreadCount", peakThreadCount);

        if (reporter != null) {
            reporter.report(PROFILER_NAME, map);
        }
    }

}