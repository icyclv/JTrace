package com.second.jtrace.server.util;

import com.second.jtrace.core.command.ICommand;
import com.second.jtrace.core.response.IResponse;
import com.second.jtrace.core.util.StringUtils;
import com.second.jtrace.server.netty.JTraceServer;


public class CommandUtil {
    public static <T extends IResponse> T dealCommand(JTraceServer eyeServer, String clientId, ICommand command) {
        // 检查并补充commandId
        if (StringUtils.isBlank(command.getCommandId())) {
            String commandId = StringUtils.UUID(true);
            command.setCommandId(commandId);
        }
        // 检查并补充sessionId
        if (StringUtils.isBlank(command.getSessionId())) {
            String sessionId = StringUtils.UUID(true);
            command.setSessionId(sessionId);
        }
        // 发送命令并获取返回结果
        IResponse response = eyeServer.sendCommand(clientId, command);

        return (T) response;
    }
}
