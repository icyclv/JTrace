package com.second.jtrace.core.util;

import com.second.jtrace.core.command.thread.vo.ThreadInfoVO;
import sun.management.HotspotThreadMBean;
import sun.management.ManagementFactoryHelper;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.*;


public class ThreadSampler {
    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static HotspotThreadMBean hotspotThreadMBean;
    private static boolean hotspotThreadMBeanEnable = true;

    private Map<ThreadInfoVO, Long> lastCpuTimes = new HashMap<ThreadInfoVO, Long>();

    private long lastSampleTimeNanos;

    public List<ThreadInfoVO> sample(Collection<ThreadInfoVO> originThreads) {
        List<ThreadInfoVO> threads = new ArrayList<ThreadInfoVO>();
        for (ThreadInfoVO threadInfoVO : originThreads) {
            threads.add(threadInfoVO);
        }

        // Sample CPU
        if (lastCpuTimes.isEmpty()) {
            lastSampleTimeNanos = System.nanoTime();
            for (ThreadInfoVO thread : threads) {
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
                    ThreadInfoVO thread = createThreadSampleInfoVO(key);
                    thread.setTime(entry.getValue() / 1000000);
                    threads.add(thread);
                    lastCpuTimes.put(thread, entry.getValue());
                }
            }

            //sort by time
            Collections.sort(threads, new Comparator<ThreadInfoVO>() {
                @Override
                public int compare(ThreadInfoVO o1, ThreadInfoVO o2) {
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
        Map<ThreadInfoVO, Long> newCpuTimes = new HashMap<ThreadInfoVO, Long>(threads.size());
        for (ThreadInfoVO thread : threads) {
            if (thread.getId() > 0) {
                long cpu = threadMXBean.getThreadCpuTime(thread.getId());
                newCpuTimes.put(thread, cpu);
            }
        }
        // internal threads
        Map<String, Long> newInternalThreadCpuTimes = getInternalThreadCpuTimes();
        if (newInternalThreadCpuTimes != null) {
            for (Map.Entry<String, Long> entry : newInternalThreadCpuTimes.entrySet()) {
                ThreadInfoVO ThreadInfoVO = createThreadSampleInfoVO(entry.getKey());
                threads.add(ThreadInfoVO);
                newCpuTimes.put(ThreadInfoVO, entry.getValue());
            }
        }

        // Compute delta time
        final Map<ThreadInfoVO, Long> deltas = new HashMap<ThreadInfoVO, Long>(threads.size());
        for (ThreadInfoVO thread : newCpuTimes.keySet()) {
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
        final HashMap<ThreadInfoVO, Double> cpuUsages = new HashMap<ThreadInfoVO, Double>(threads.size());
        for (ThreadInfoVO thread : threads) {
            double cpu = sampleIntervalNanos == 0 ? 0 : (Math.rint(deltas.get(thread) * 10000.0 / sampleIntervalNanos) / 100.0);
            cpuUsages.put(thread, cpu);
        }

        // Sort by CPU time : should be a rendering hint...
        Collections.sort(threads, new Comparator<ThreadInfoVO>() {
            public int compare(ThreadInfoVO o1, ThreadInfoVO o2) {
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

        for (ThreadInfoVO thread : threads) {
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

    private ThreadInfoVO createThreadSampleInfoVO(String name) {
        ThreadInfoVO ThreadInfoVO = new ThreadInfoVO();
        ThreadInfoVO.setId(-1);
        ThreadInfoVO.setName(name);
        ThreadInfoVO.setPriority(-1);
        ThreadInfoVO.setDaemon(true);
        ThreadInfoVO.setInterrupted(false);
        return ThreadInfoVO;
    }

    public void pause(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
