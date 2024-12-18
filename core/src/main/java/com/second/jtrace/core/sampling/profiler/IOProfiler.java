package com.second.jtrace.core.sampling.profiler;


import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.common.OSUtils;
import com.second.jtrace.core.sampling.Reporter;
import com.second.jtrace.core.util.ProcFileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IOProfiler  implements Profiler {
    public final static String PROFILER_NAME = "IO";

    private long intervalMillis = JTraceConstants.DEFAULT_METRIC_INTERVAL;
    public static boolean available;
    static {
        available = OSUtils.isLinux();
    }
    private Reporter reporter;
    public IOProfiler(Reporter reporter) {
      setReporter(reporter);

    }
    @Override
    public long getIntervalMillis() {
        return intervalMillis;
    }

    @Override
    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    @Override
    public synchronized void profile() {
        if (!available) {
            return;
        }
        // See http://man7.org/linux/man-pages/man5/proc.5.html for details about /proc/[pid]/io
        Map<String, String> procMap = ProcFileUtils.getProcIO();
        Long rchar = ProcFileUtils.getBytesValue(procMap, "rchar");
        Long wchar = ProcFileUtils.getBytesValue(procMap, "wchar");
        Long read_bytes = ProcFileUtils.getBytesValue(procMap, "read_bytes");
        Long write_bytes = ProcFileUtils.getBytesValue(procMap, "write_bytes");

        List<Map<String, Object>> cpuTime = ProcFileUtils.getProcStatCpuTime();
        
        Map<String, Object> map = new HashMap<String, Object>();

        map.put("epochMillis", System.currentTimeMillis());

        Map<String, Object> selfMap = new HashMap<String, Object>();
        map.put("self", selfMap);
        
        Map<String, Object> ioMap = new HashMap<String, Object>();
        selfMap.put("io", ioMap);

        ioMap.put("rchar", rchar);
        ioMap.put("wchar", wchar);
        ioMap.put("read_bytes", read_bytes);
        ioMap.put("write_bytes", write_bytes);

        map.put("stat", cpuTime);
        this.reporter.report(PROFILER_NAME, map);
    }
}
