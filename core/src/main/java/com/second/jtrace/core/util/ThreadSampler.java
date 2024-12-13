package com.second.jtrace.core.util;

import com.second.jtrace.core.command.thread.vo.ThreadInfo;
import sun.management.HotspotThreadMBean;
import sun.management.ManagementFactoryHelper;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;


public class ThreadSampler {
    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static HotspotThreadMBean hotspotThreadMBean;
    private static boolean hotspotThreadMBeanEnable = true;

    private Map<ThreadInfo, Long> lastCpuTimes = new HashMap<ThreadInfo, Long>();

    private long lastSampleTimeNanos;

    public List<ThreadInfo> sample(Collection<ThreadInfo> originThreads) {
        List<ThreadInfo> threads = new ArrayList<ThreadInfo>();
        for (ThreadInfo threadInfo : originThreads) {
            threads.add(threadInfo);
        }

        // Sample CPU
        if (lastCpuTimes.isEmpty()) {
            lastSampleTimeNanos = System.nanoTime();
            for (ThreadInfo thread : threads) {
                if (thread.getId() > 0) {
                    long cpu = threadMXBean.getThreadCpuTime(thread.getId());
                    lastCpuTimes.put(thread, cpu);
                    thread.setTime(cpu / 1000000);
                }
            }

            // add internal threads
            Map<String, Long> internalThreadCpuTimes = getInternalThreadCpuTimes();
            if (internalThreadCpuTimes != null) {
                for (Map.Entry<String, Long> entry : internalThreadCpuTimes.entrySet()) {
                    String key = entry.getKey();
                    ThreadInfo thread = createThreadSampleInfoVO(key);
                    thread.setTime(entry.getValue() / 1000000);
                    threads.add(thread);
                    lastCpuTimes.put(thread, entry.getValue());
                }
            }

            //sort by time
            Collections.sort(threads, new Comparator<ThreadInfo>() {
                @Override
                public int compare(ThreadInfo o1, ThreadInfo o2) {
                    long l1 = o1.getTime();
                    long l2 = o2.getTime();
                    if (l1 < l2) {
                        return 1;
                    } else if (l1 > l2) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            return threads;
        }

        // Resample
        long newSampleTimeNanos = System.nanoTime();
        Map<ThreadInfo, Long> newCpuTimes = new HashMap<ThreadInfo, Long>(threads.size());
        for (ThreadInfo thread : threads) {
            if (thread.getId() > 0) {
                long cpu = threadMXBean.getThreadCpuTime(thread.getId());
                newCpuTimes.put(thread, cpu);
            }
        }
        // internal threads
        Map<String, Long> newInternalThreadCpuTimes = getInternalThreadCpuTimes();
        if (newInternalThreadCpuTimes != null) {
            for (Map.Entry<String, Long> entry : newInternalThreadCpuTimes.entrySet()) {
                ThreadInfo ThreadInfo = createThreadSampleInfoVO(entry.getKey());
                threads.add(ThreadInfo);
                newCpuTimes.put(ThreadInfo, entry.getValue());
            }
        }

        // Compute delta time
        final Map<ThreadInfo, Long> deltas = new HashMap<ThreadInfo, Long>(threads.size());
        for (ThreadInfo thread : newCpuTimes.keySet()) {
            Long t = lastCpuTimes.get(thread);
            if (t == null) {
                t = 0L;
            }
            long time1 = t;
            long time2 = newCpuTimes.get(thread);
            if (time1 == -1) {
                time1 = time2;
            } else if (time2 == -1) {
                time2 = time1;
            }
            long delta = time2 - time1;
            deltas.put(thread, delta);
        }

        long sampleIntervalNanos = newSampleTimeNanos - lastSampleTimeNanos;

        // Compute cpu usage
        final HashMap<ThreadInfo, Double> cpuUsages = new HashMap<ThreadInfo, Double>(threads.size());
        for (ThreadInfo thread : threads) {
            double cpu = sampleIntervalNanos == 0 ? 0 : (Math.rint(deltas.get(thread) * 10000.0 / sampleIntervalNanos) / 100.0);
            cpuUsages.put(thread, cpu);
        }

        // Sort by CPU time : should be a rendering hint...
        Collections.sort(threads, new Comparator<ThreadInfo>() {
            public int compare(ThreadInfo o1, ThreadInfo o2) {
                long l1 = deltas.get(o1);
                long l2 = deltas.get(o2);
                if (l1 < l2) {
                    return 1;
                } else if (l1 > l2) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        for (ThreadInfo thread : threads) {
            //nanos to mills
            long timeMills = newCpuTimes.get(thread) / 1000000;
            long deltaTime = deltas.get(thread) / 1000000;
            double cpu = cpuUsages.get(thread);

            thread.setCpu(cpu);
            thread.setTime(timeMills);
            thread.setDeltaTime(deltaTime);
        }
        lastCpuTimes = newCpuTimes;
        lastSampleTimeNanos = newSampleTimeNanos;

        return threads;
    }

    private Map<String, Long> getInternalThreadCpuTimes() {
        if (hotspotThreadMBeanEnable) {
            try {
                if (hotspotThreadMBean == null) {
                    hotspotThreadMBean = ManagementFactoryHelper.getHotspotThreadMBean();
                }
                return hotspotThreadMBean.getInternalThreadCpuTimes();
            } catch (Throwable e) {
                //ignore ex
                hotspotThreadMBeanEnable = false;
            }
        }
        return null;
    }

    private ThreadInfo createThreadSampleInfoVO(String name) {
        ThreadInfo ThreadInfo = new ThreadInfo();
        ThreadInfo.setId(-1);
        ThreadInfo.setName(name);
        ThreadInfo.setPriority(-1);
        ThreadInfo.setDaemon(true);
        ThreadInfo.setInterrupted(false);
        return ThreadInfo;
    }

    public void pause(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
