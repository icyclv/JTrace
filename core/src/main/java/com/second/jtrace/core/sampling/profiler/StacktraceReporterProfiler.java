/*
 * Copyright (c) 2018 Uber Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.second.jtrace.core.sampling.profiler;



import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.core.sampling.Reporter;
import com.second.jtrace.core.sampling.bean.ClassAndMethod;
import com.second.jtrace.core.sampling.bean.Stacktrace;
import com.second.jtrace.core.sampling.bean.StacktraceMetricBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This class reads the stacktraces from the given buffer and send out via given reporter.
 */
public class StacktraceReporterProfiler  implements Profiler {
    public static final String PROFILER_NAME = "Stacktrace";

    private StacktraceMetricBuffer buffer;

    private Reporter reporter;

    private long intervalMillis = JTraceConstants.DEFAULT_METRIC_INTERVAL;

    public StacktraceReporterProfiler(StacktraceMetricBuffer buffer, Reporter reporter) {
        this.buffer = buffer;
        this.reporter = reporter;
    }

    @Override
    public long getIntervalMillis() {
        return intervalMillis;
    }

    public void setIntervalMillis(long intervalMillis) {
        this.intervalMillis = intervalMillis;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public void profile() {
        Map<String,Object> resultMap = new HashMap<>();
        if (buffer == null) {
            return;
        }

        if (reporter == null) {
            return;
        }

        long startEpoch = buffer.getLastResetMillis();
        
        Map<Stacktrace, AtomicLong> metrics = buffer.reset();

        long endEpoch = buffer.getLastResetMillis();

        resultMap.put("startEpoch", startEpoch);
        resultMap.put("endEpoch", endEpoch);
        List<Map<String,Object>> stacktraces = new ArrayList<>();
        for (Map.Entry<Stacktrace, AtomicLong> entry : metrics.entrySet()) {
            Map<String, Object> map = new HashMap<>();


            Stacktrace stacktrace = entry.getKey();
            
            map.put("threadName", stacktrace.getThreadName());
            map.put("threadState", stacktrace.getThreadState());

            ClassAndMethod[] classAndMethodArray = stacktrace.getStack();
            if (classAndMethodArray!= null) {
                List<String> stackArray = new ArrayList<>(classAndMethodArray.length);
                for (ClassAndMethod classAndMethod : classAndMethodArray) {
                    stackArray.add(classAndMethod.getClassName() + "." + classAndMethod.getMethodName());
                }
                map.put("stacktrace", stackArray);
            }
            
            map.put("count", entry.getValue().get());
            stacktraces.add(map);
        }
        resultMap.put("stacktraces", stacktraces);
        reporter.report(PROFILER_NAME, resultMap);

    }
}
