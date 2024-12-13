package com.second.jtrace.server;


import com.second.jtrace.server.netty.JTraceServerHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JTraceServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(JTraceServerApplication.class, args);

    }
}
