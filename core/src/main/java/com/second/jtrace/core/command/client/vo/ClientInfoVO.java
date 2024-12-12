package com.second.jtrace.core.command.client.vo;

import lombok.Data;

@Data
public class ClientInfoVO {
    /**
     * 客户端ID
     */
    private String clientId;
    /**
     * 客户端名称
     */
    private String clientName;
    /**
     * 客户端服务器名称
     */
    private String host;
    /**
     * 客户端服务器IP
     */
    private String ip;
    /**
     * 版本
     */
    private String version;
}
