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
import com.second.jtrace.core.util.ProcFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.*;
import java.util.*;

public class CpuAndMemoryProfiler implements Profiler {
    private static final String PROFILER_NAME = "CpuAndMemory";

    private static final Logger logger = LoggerFactory.getLogger(CpuAndMemoryProfiler.class);
    private static final String ATTRIBUTE_NAME_ProcessCpuLoad = "ProcessCpuLoad";
    private static final int ATTRIBUTE_INDEX_ProcessCpuLoad = 0;

    private static final String ATTRIBUTE_NAME_SystemCpuLoad = "SystemCpuLoad";
    private static final int ATTRIBUTE_INDEX_SystemCpuLoad = 1;

    private static final String ATTRIBUTE_NAME_ProcessCpuTime = "ProcessCpuTime";
    private static final int ATTRIBUTE_INDEX_ProcessCpuTime = 2;

    private long intervalMillis = JTraceConstants.DEFAULT_METRIC_INTERVAL;

    private MBeanServer platformMBeanServer;
    private ObjectName operatingSystemObjectName;

    private MemoryMXBean memoryMXBean;
    private Reporter reporter;
    public CpuAndMemoryProfiler(Reporter reporter) {
        setReporter(reporter);
        init();


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
        Double processCpuLoad = null;
        Double systemCpuLoad = null;
        Long processCpuTime = null;

        AttributeList cpuAttributes = getCpuAttributes();
        if (cpuAttributes != null && cpuAttributes.size() > 0) {
            Attribute att = (Attribute) cpuAttributes.get(ATTRIBUTE_INDEX_ProcessCpuLoad);
            processCpuLoad = (Double) att.getValue();
            if (processCpuLoad == Double.NaN) {
                processCpuLoad = null;
            }

            att = (Attribute) cpuAttributes.get(ATTRIBUTE_INDEX_SystemCpuLoad);
            systemCpuLoad = (Double) att.getValue();
            if (systemCpuLoad == Double.NaN) {
                systemCpuLoad = null;
            }

            att = (Attribute) cpuAttributes.get(ATTRIBUTE_INDEX_ProcessCpuTime);
            processCpuTime = (Long) att.getValue();
        }

        Double heapMemoryTotalUsed = null;
        Double heapMemoryCommitted = null;
        Double heapMemoryMax = null;

        Double nonHeapMemoryTotalUsed = null;
        Double nonHeapMemoryCommitted = null;
        Double nonHeapMemoryMax = null;
        
        if (memoryMXBean != null) {
            MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
            heapMemoryTotalUsed = new Double(memoryUsage.getUsed());
            heapMemoryCommitted = new Double(memoryUsage.getCommitted());
            heapMemoryMax =  new Double(memoryUsage.getMax());

            memoryUsage = memoryMXBean.getNonHeapMemoryUsage();
            nonHeapMemoryTotalUsed = new Double(memoryUsage.getUsed());
            nonHeapMemoryCommitted = new Double(memoryUsage.getCommitted());
            nonHeapMemoryMax =  new Double(memoryUsage.getMax());

        }

        List<Map<String, Object>> gcMetrics = new ArrayList<>();

        List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        
        if (gcMXBeans != null) {
            for (GarbageCollectorMXBean gcMXBean : gcMXBeans) {
                Map<String, Object> gcMap = new HashMap<>();
                gcMap.put("name", gcMXBean.getName());
                gcMap.put("collectionCount", new Long(gcMXBean.getCollectionCount()));
                gcMap.put("collectionTime", new Long(gcMXBean.getCollectionTime()));

                gcMetrics.add(gcMap);
            }
        }

        List<Map<String, Object>> memoryPoolsMetrics = new ArrayList<>();

        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            Map<String, Object> memoryPoolMap = new HashMap<>();

            memoryPoolMap.put("name", pool.getName());
            memoryPoolMap.put("type", pool.getType().toString());
            memoryPoolMap.put("usageCommitted", pool.getUsage().getCommitted());
            memoryPoolMap.put("usageMax", pool.getUsage().getMax());
            memoryPoolMap.put("usageUsed", pool.getUsage().getUsed());
            memoryPoolMap.put("peakUsageCommitted", pool.getPeakUsage().getCommitted());
            memoryPoolMap.put("peakUsageMax", pool.getPeakUsage().getMax());
            memoryPoolMap.put("peakUsageUsed", pool.getPeakUsage().getUsed());

            memoryPoolsMetrics.add(memoryPoolMap);
        }

        List<Map<String, Object>> bufferPoolsMetrics = new ArrayList<>();

        List<BufferPoolMXBean> bufferPools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        if (bufferPools != null) {
            for (BufferPoolMXBean pool : bufferPools) {
                Map<String, Object> bufferPoolMap = new HashMap<>();

                bufferPoolMap.put("name", pool.getName());
                bufferPoolMap.put("count", new Long(pool.getCount()));
                bufferPoolMap.put("memoryUsed", new Long(pool.getMemoryUsed()));
                bufferPoolMap.put("totalCapacity", new Long(pool.getTotalCapacity()));

                bufferPoolsMetrics.add(bufferPoolMap);
            }
        }

        // See http://man7.org/linux/man-pages/man5/proc.5.html for details about proc status
        Map<String, String> procStatus = ProcFileUtils.getProcStatus();
        Long procStatusVmRSS = ProcFileUtils.getBytesValue(procStatus, "VmRSS");
        Long procStatusVmHWM = ProcFileUtils.getBytesValue(procStatus, "VmHWM");
        Long procStatusVmSize = ProcFileUtils.getBytesValue(procStatus, "VmSize");
        Long procStatusVmPeak = ProcFileUtils.getBytesValue(procStatus, "VmPeak");

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("epochMillis", System.currentTimeMillis());




        map.put("processCpuLoad", processCpuLoad);
        map.put("systemCpuLoad", systemCpuLoad);
        map.put("processCpuTime", processCpuTime);

        map.put("heapMemoryTotalUsed", heapMemoryTotalUsed);
        map.put("heapMemoryCommitted", heapMemoryCommitted);
        map.put("heapMemoryMax", heapMemoryMax);

        map.put("nonHeapMemoryTotalUsed", nonHeapMemoryTotalUsed);
        map.put("nonHeapMemoryCommitted", nonHeapMemoryCommitted);
        map.put("nonHeapMemoryMax", nonHeapMemoryMax);
        
        map.put("gc", gcMetrics);

        map.put("memoryPools", memoryPoolsMetrics);
        map.put("bufferPools", bufferPoolsMetrics);

        if (procStatusVmRSS != null) {
            map.put("vmRSS", procStatusVmRSS);
        }
        if (procStatusVmHWM != null) {
            map.put("vmHWM", procStatusVmHWM);
        }
        if (procStatusVmSize != null) {
            map.put("vmSize", procStatusVmSize);
        }
        if (procStatusVmPeak != null) {
            map.put("vmPeak", procStatusVmPeak);
        }
        this.reporter.report(PROFILER_NAME,map);
    }

    private void init() {
        try {
            platformMBeanServer = ManagementFactory.getPlatformMBeanServer();
            operatingSystemObjectName = ObjectName.getInstance("java.lang:type=OperatingSystem");
        } catch (Throwable ex) {
            logger.warn("Failed to get Operation System MBean", ex);
        }

        try {
            memoryMXBean = ManagementFactory.getMemoryMXBean();
        } catch (Throwable ex) {
            logger.warn("Failed to get Memory MBean", ex);
        }
    }

    private AttributeList getCpuAttributes() {
        try {
            String[] names = new String[]{ATTRIBUTE_NAME_ProcessCpuLoad, ATTRIBUTE_NAME_SystemCpuLoad, ATTRIBUTE_NAME_ProcessCpuTime};
            AttributeList list = platformMBeanServer.getAttributes(operatingSystemObjectName, names);
            if (list.size() != names.length) {
                logger.warn("Failed to get all attributes");
                return new AttributeList();
            }
            return list;
        } catch (Throwable ex) {
            logger.warn("Failed to get CPU MBean attributes", ex);
            return null;
        }
    }
}
