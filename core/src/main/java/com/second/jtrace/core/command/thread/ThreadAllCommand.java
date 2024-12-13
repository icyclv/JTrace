package com.second.jtrace.core.command.thread;


import com.second.jtrace.core.client.IClient;
import com.second.jtrace.core.command.AbstractCommand;
import com.second.jtrace.core.command.thread.response.ThreadAllResponse;
import com.second.jtrace.core.command.thread.vo.ThreadInfo;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.ThreadUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
public class ThreadAllCommand extends AbstractCommand {
    private static final Logger logger = LoggerFactory.getLogger(ThreadAllCommand.class);
    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    /**
     * 采样间隔，毫秒
     */
    private int sampleInterval = 200;
    /**
     * if true, retrieves all locked monitors
     */
    private boolean lockedMonitors = false;
    /**
     * if true, retrieves all locked ownable synchronizers.
     */
    private boolean lockedSynchronizers = false;

    @Override
    public Class<? extends IResponse> getResponseClass() {
        return ThreadAllResponse.class;
    }

    @Override
    public IResponse executeForResponse(IClient client) {
        ThreadAllResponse threadResponse = new ThreadAllResponse();
        List<ThreadInfo> threadInfoList = ThreadUtils.findAllThreadInfos(sampleInterval, lockedMonitors, lockedSynchronizers);
        threadResponse.setThreadSampleInfos(threadInfoList);
        return threadResponse;
    }
}