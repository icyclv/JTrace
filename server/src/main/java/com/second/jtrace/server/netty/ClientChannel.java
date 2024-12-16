package com.second.jtrace.server.netty;


import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.command.client.vo.ClientInfoVO;
import com.second.jtrace.core.response.BaseResponse;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.StringUtils;
import io.netty.channel.Channel;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Data
public class ClientChannel {
    private static final Logger logger = LoggerFactory.getLogger(ClientChannel.class);


    public static int SECONDS_OF_WAIT_TIME = 15;

    private final Map<String, CountDownLatch> countDownLatchMap = new ConcurrentHashMap<>();


    private final Map<String, IResponse> responseMap = new ConcurrentHashMap<>();


    private Channel channel;


    private ClientInfoVO clientInfoVO;


    private JTraceServer server;


    public IResponse sendCommand(ICommand command) {
        String commandId = null;
        try {
            commandId = StringUtils.UUID(true);
            command.setCommandId(commandId);

            CountDownLatch countDownLatch = new CountDownLatch(1);
            countDownLatchMap.put(commandId, countDownLatch);

            channel.writeAndFlush(command);

            boolean timeout = countDownLatch.await(SECONDS_OF_WAIT_TIME, TimeUnit.SECONDS);

            // TimeOut
            if (!timeout) {
                return BaseResponse.fail("The command execution timeout", command.getResponseClass());
            }

            return responseMap.get(commandId);
        } catch (Exception e) {
            logger.error("sendCommand error:" + e.getMessage(), e);
            return BaseResponse.fail("The command execution error", command.getResponseClass());
        } finally {
            if (!StringUtils.isBlank(commandId)) {
                countDownLatchMap.remove(commandId);
            }
            responseMap.remove(commandId);
        }
    }


    public void handleResponse(IResponse response) {
        if (countDownLatchMap.containsKey(response.getCommandId())) {
            responseMap.put(response.getCommandId(), response);
            CountDownLatch latch = countDownLatchMap.get(response.getCommandId());
            if (latch != null) {
                latch.countDown();
            }
        } else {
            logger.warn("ignore response:{}", response);
        }
    }
}
