package com.second.jtrace.server.configuration;

import com.second.jtrace.server.netty.JTraceServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyServerConfiguration {
    @Value("${jtrace.netty.port:4090}")
    private int port;

    @Bean
    public JTraceServer jTraceServer() {
        JTraceServer jTraceServer = new JTraceServer(port);
        jTraceServer.start();
        return jTraceServer;
    }

}
