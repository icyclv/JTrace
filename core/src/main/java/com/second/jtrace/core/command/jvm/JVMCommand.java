package com.second.jtrace.core.command.jvm;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.jvm.response.JVMResponse;
import com.second.jtrace.core.command.jvm.vo.JVMInfoVO;
import com.second.jtrace.core.protocol.GsonSerializer;
import com.second.jtrace.core.protocol.MessageTypeMapper;
import com.second.jtrace.core.response.IResponse;

import java.lang.management.*;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;


public class JVMCommand extends AbstractCommand {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public IResponse executeForResponse(IClient client) {
        JVMResponse jvmResponse = new JVMResponse();
        JVMInfoVO jvmInfoVO = new JVMInfoVO();

        // Use method references and stream-like approach for adding information
        addJvmInformation(jvmInfoVO);

        jvmResponse.setJvmInfo(jvmInfoVO);
        return jvmResponse;
    }
    private void addJvmInformation(JVMInfoVO jvmInfoVO) {
        addRuntimeInfo(jvmInfoVO.getRuntimeInfo());
        addClassLoading(jvmInfoVO.getClassLoadingInfo());
        addCompilation(jvmInfoVO.getCompilationInfo());
        addGarbageCollectors(jvmInfoVO.getGarbageCollectorInfo());
        addMemoryManagers(jvmInfoVO.getMemoryManagerInfo());
        addMemory(jvmInfoVO.getMemoryInfo());
        addOperatingSystem(jvmInfoVO.getOperatingSystemInfo());
        addThread(jvmInfoVO.getThreadInfo());
        addFileDescriptor(jvmInfoVO.getFileDescriptorInfo());
    }

    /**
     * Safe method to invoke file descriptor related methods with reflection
     */
    private long safeInvokeMethod(OperatingSystemMXBean os, String methodName) {
        try {
            Method method = os.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            return (Long) method.invoke(os);
        } catch (Exception e) {
            // Log error or handle more explicitly if needed
            return -1;
        }
    }

    private void addFileDescriptor(Map<String, String> fileDescriptorInfo) {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        fileDescriptorInfo.put("MAX-FILE-DESCRIPTOR-COUNT",
                String.valueOf(safeInvokeMethod(osBean, "getMaxFileDescriptorCount")));
        fileDescriptorInfo.put("OPEN-FILE-DESCRIPTOR-COUNT",
                String.valueOf(safeInvokeMethod(osBean, "getOpenFileDescriptorCount")));
    }

    private void addRuntimeInfo(Map<String, String> runtimeInfo) {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        String bootClassPath = Optional.ofNullable(safeGetBootClassPath(runtimeMXBean))
                .orElse("Not Available");

        runtimeInfo.put("MACHINE-NAME", runtimeMXBean.getName());
        runtimeInfo.put("JVM-START-TIME", DATE_FORMAT.format(new Date(runtimeMXBean.getStartTime())));
        runtimeInfo.put("MANAGEMENT-SPEC-VERSION", runtimeMXBean.getManagementSpecVersion());
        runtimeInfo.put("SPEC-NAME", runtimeMXBean.getSpecName());
        runtimeInfo.put("SPEC-VENDOR", runtimeMXBean.getSpecVendor());
        runtimeInfo.put("SPEC-VERSION", runtimeMXBean.getSpecVersion());
        runtimeInfo.put("VM-NAME", runtimeMXBean.getVmName());
        runtimeInfo.put("VM-VENDOR", runtimeMXBean.getVmVendor());
        runtimeInfo.put("VM-VERSION", runtimeMXBean.getVmVersion());
        runtimeInfo.put("INPUT-ARGUMENTS", GsonSerializer.toJson(runtimeMXBean.getInputArguments()));
        runtimeInfo.put("CLASS-PATH", runtimeMXBean.getClassPath());
        runtimeInfo.put("BOOT-CLASS-PATH", bootClassPath);
        runtimeInfo.put("LIBRARY-PATH", runtimeMXBean.getLibraryPath());
    }

    /**
     * Safe method to get boot class path with exception handling
     */
    private String safeGetBootClassPath(RuntimeMXBean runtimeMXBean) {
        try {
            return runtimeMXBean.getBootClassPath();
        } catch (UnsupportedOperationException e) {
            // Log or handle the exception if needed
            return null;
        }
    }

    private void addClassLoading(Map<String, String> classLoadingInfo) {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        classLoadingInfo.put("LOADED-CLASS-COUNT",
                String.valueOf(classLoadingMXBean.getLoadedClassCount()));
        classLoadingInfo.put("TOTAL-LOADED-CLASS-COUNT",
                String.valueOf(classLoadingMXBean.getTotalLoadedClassCount()));
        classLoadingInfo.put("UNLOADED-CLASS-COUNT",
                String.valueOf(classLoadingMXBean.getUnloadedClassCount()));
        classLoadingInfo.put("IS-VERBOSE",
                String.valueOf(classLoadingMXBean.isVerbose()));
    }

    private void addCompilation(Map<String, String> compilationInfo) {
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        if (compilationMXBean == null) return;

        compilationInfo.put("NAME", compilationMXBean.getName());
        if (compilationMXBean.isCompilationTimeMonitoringSupported()) {
            compilationInfo.put("TOTAL-COMPILE-TIME",
                    String.valueOf(compilationMXBean.getTotalCompilationTime()));
        }
    }

    private void addGarbageCollectors(Map<String, String> garbageCollectorInfo) {
        Collection<GarbageCollectorMXBean> garbageCollectorMXBeans =
                ManagementFactory.getGarbageCollectorMXBeans();

        garbageCollectorMXBeans.forEach(gcMXBean -> {
            Map<String, Object> gcInfo = new LinkedHashMap<>();
            gcInfo.put("name", gcMXBean.getName());
            gcInfo.put("collectionCount", gcMXBean.getCollectionCount());
            gcInfo.put("collectionTime", gcMXBean.getCollectionTime());

            garbageCollectorInfo.put(
                    gcMXBean.getName(),
                    GsonSerializer.toJson(gcInfo)
            );
        });
    }

    private void addMemoryManagers(Map<String, String> memoryManagerInfo) {
        Collection<MemoryManagerMXBean> memoryManagerMXBeans =
                ManagementFactory.getMemoryManagerMXBeans();

        memoryManagerMXBeans.stream()
                .filter(MemoryManagerMXBean::isValid)
                .forEach(memoryManagerMXBean -> {
                    String name = memoryManagerMXBean.getName();
                    memoryManagerInfo.put(
                            name,
                            GsonSerializer.toJson(memoryManagerMXBean.getMemoryPoolNames())
                    );
                });
    }

    private void addMemory(Map<String, String> memoryInfo) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

        memoryInfo.put("HEAP-MEMORY-USAGE",
                GsonSerializer.toJson(getMemoryUsageInfo("heap", memoryMXBean.getHeapMemoryUsage())));

        memoryInfo.put("NO-HEAP-MEMORY-USAGE",
                GsonSerializer.toJson(getMemoryUsageInfo("non_heap", memoryMXBean.getNonHeapMemoryUsage())));

        memoryInfo.put("PENDING-FINALIZE-COUNT",
                String.valueOf(memoryMXBean.getObjectPendingFinalizationCount()));
    }

    private Map<String, Object> getMemoryUsageInfo(String name, MemoryUsage memoryUsage) {
        Map<String, Object> memoryInfo = new LinkedHashMap<>();
        memoryInfo.put("name", name);
        memoryInfo.put("init", memoryUsage.getInit());
        memoryInfo.put("used", memoryUsage.getUsed());
        memoryInfo.put("committed", memoryUsage.getCommitted());
        memoryInfo.put("max", memoryUsage.getMax());
        return memoryInfo;
    }

    private void addOperatingSystem(Map<String, String> operatingSystemInfo) {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();

        operatingSystemInfo.put("OS", osBean.getName());
        operatingSystemInfo.put("ARCH", osBean.getArch());
        operatingSystemInfo.put("PROCESSORS-COUNT",
                String.valueOf(osBean.getAvailableProcessors()));
        operatingSystemInfo.put("LOAD-AVERAGE",
                String.valueOf(osBean.getSystemLoadAverage()));
        operatingSystemInfo.put("VERSION", osBean.getVersion());
    }

    private void addThread(Map<String, String> threadInfo) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        threadInfo.put("COUNT", String.valueOf(threadMXBean.getThreadCount()));
        threadInfo.put("DAEMON-COUNT", String.valueOf(threadMXBean.getDaemonThreadCount()));
        threadInfo.put("PEAK-COUNT", String.valueOf(threadMXBean.getPeakThreadCount()));
        threadInfo.put("STARTED-COUNT", String.valueOf(threadMXBean.getTotalStartedThreadCount()));
        threadInfo.put("DEADLOCK-COUNT", String.valueOf(getDeadlockedThreadsCount(threadMXBean)));
    }

    private int getDeadlockedThreadsCount(ThreadMXBean threads) {
        long[] ids = threads.findDeadlockedThreads();
        return ids == null ? 0 : ids.length;
    }

    @Override
    public int getMessageTypeId() {
        return MessageTypeMapper.TypeList.JVMCommand.ordinal();

    }

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return JVMResponse.class;
    }
}
