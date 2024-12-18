

package com.second.jtrace.core.sampling.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * StacktraceMetricBuffer is a buffer to store metrics. It is thread safe for appendValue.
 * The reset method will create a new empty internal buffer and return the old one.
 */
public class StacktraceMetricBuffer {
    private AtomicLong lastResetMillis = new AtomicLong(System.currentTimeMillis());
    
    private volatile ConcurrentHashMap<Stacktrace, AtomicLong> metrics = new ConcurrentHashMap<>();

    public void appendValue(Stacktrace stacktrace) {
        AtomicLong counter = metrics.computeIfAbsent(stacktrace, key -> new AtomicLong(0));
        counter.incrementAndGet();
    }

    public long getLastResetMillis() {
        return lastResetMillis.get();
    }
    
    public Map<Stacktrace, AtomicLong> reset() {
        ConcurrentHashMap<Stacktrace, AtomicLong> oldCopy = metrics;
        metrics = new ConcurrentHashMap<>();
        
        lastResetMillis.set(System.currentTimeMillis());
        
        return oldCopy;
    }
}
