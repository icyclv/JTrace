package com.second.jtrace.core.command.jvm;


import com.second.jtrace.common.JTraceConstants;
import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.jvm.response.MemoryResponse;
import com.second.jtrace.core.command.jvm.vo.GcInfoVO;
import com.second.jtrace.core.command.jvm.vo.MemoryInfoVO;
import com.second.jtrace.core.command.jvm.vo.RuntimeInfoVO;
import com.second.jtrace.core.protocol.MessageTypeMapper;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.StringUtils;

import java.lang.management.*;
import java.util.ArrayList;
import java.util.List;

public class MemoryCommand extends AbstractCommand {
    @Override
    public IResponse executeForResponse(IClient client) {
        MemoryResponse memoryResponse = new MemoryResponse();
        memoryResponse.setMemoryInfos(getMemoryInfos());
        return memoryResponse;
    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return MemoryResponse.class;
    }



    public List<MemoryInfoVO> getMemoryInfos() {
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        List<MemoryInfoVO> memoryInfos = new ArrayList<MemoryInfoVO>();

        // heap
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        memoryInfos.add(createMemoryInfoVO(JTraceConstants.TYPE_HEAP, JTraceConstants.TYPE_HEAP, heapMemoryUsage));
        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = getUsage(poolMXBean);
                if (usage != null) {
                    String poolName = StringUtils.beautifyName(poolMXBean.getName());
                    memoryInfos.add(createMemoryInfoVO(JTraceConstants.TYPE_HEAP, poolName, usage));
                }
            }
        }
        // non-heap
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        memoryInfos.add(createMemoryInfoVO(JTraceConstants.TYPE_NON_HEAP, JTraceConstants.TYPE_NON_HEAP, nonHeapMemoryUsage));
        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.NON_HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = getUsage(poolMXBean);
                if (usage != null) {
                    String poolName = StringUtils.beautifyName(poolMXBean.getName());
                    memoryInfos.add(createMemoryInfoVO(JTraceConstants.TYPE_NON_HEAP, poolName, usage));
                }
            }
        }
        addBufferPoolMemoryInfo(memoryInfos);
        return memoryInfos;
    }
    private MemoryUsage getUsage(MemoryPoolMXBean memoryPoolMXBean) {
        try {
            return memoryPoolMXBean.getUsage();
        } catch (InternalError e) {
            // Defensive for potential InternalError with some specific JVM options. Based on its Javadoc,
            // MemoryPoolMXBean.getUsage() should return null, not throwing InternalError, so it seems to be a JVM bug.
            return null;
        }
    }

    private void addBufferPoolMemoryInfo(List<MemoryInfoVO> memoryInfos) {
        try {
            @SuppressWarnings("rawtypes")
            Class bufferPoolMXBeanClass = Class.forName("java.lang.management.BufferPoolMXBean");
            @SuppressWarnings("unchecked")
            List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(bufferPoolMXBeanClass);
            for (BufferPoolMXBean mbean : bufferPoolMXBeans) {
                long used = mbean.getMemoryUsed();
                long total = mbean.getTotalCapacity();
                memoryInfos.add(new MemoryInfoVO(JTraceConstants.TYPE_BUFFER_POOL, mbean.getName(), used, total, Long.MIN_VALUE));
            }
        } catch (ClassNotFoundException e) {
            // ignore
        }
    }

    private MemoryInfoVO createMemoryInfoVO(String type, String name, MemoryUsage memoryUsage) {
        return new MemoryInfoVO(type, name, memoryUsage.getUsed(), memoryUsage.getCommitted(), memoryUsage.getMax());
    }

    public RuntimeInfoVO getRuntimeInfo() {
        RuntimeInfoVO runtimeInfo = new RuntimeInfoVO();
        runtimeInfo.setOsName(System.getProperty("os.name"));
        runtimeInfo.setOsVersion(System.getProperty("os.version"));
        runtimeInfo.setJavaVersion(System.getProperty("java.version"));
        runtimeInfo.setJavaHome(System.getProperty("java.home"));
        runtimeInfo.setSystemLoadAverage(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage());
        runtimeInfo.setProcessors(Runtime.getRuntime().availableProcessors());
        runtimeInfo.setUptime(ManagementFactory.getRuntimeMXBean().getUptime() / 1000);
        runtimeInfo.setTimestamp(System.currentTimeMillis());
        return runtimeInfo;
    }

    public List<GcInfoVO> getGcInfos() {
        List<GcInfoVO> gcInfos = new ArrayList<GcInfoVO>();
        List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean gcMXBean : garbageCollectorMxBeans) {
            String name = gcMXBean.getName();
            gcInfos.add(new GcInfoVO(StringUtils.beautifyName(name),
                    gcMXBean.getCollectionCount(), gcMXBean.getCollectionTime()));
        }
        return gcInfos;
    }
}
