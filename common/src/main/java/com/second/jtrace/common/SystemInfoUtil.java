package com.second.jtrace.common;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SystemInfoUtil {

    // 本机IP地址
    public static final String LOCAL_IP_ADDRESS;
    // 当前Java进程的PID
    public static final String PROCESS_ID;
    // 主机名称
    public static final String HOST_NAME;

    public static final String JVM_NAME;
    // 静态代码块初始化常量
    static {
        LOCAL_IP_ADDRESS = initLocalIpAddress();
        PROCESS_ID = initProcessId();
        HOST_NAME = initHostName();
        JVM_NAME = initJvmName();
    }

    /**
     * 初始化本机IP地址
     */
    private static String initLocalIpAddress() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown IP";
        }
    }

    /**
     * 初始化当前Java进程的PID
     */
    private static String initProcessId() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    private static String initJvmName() {
        return ManagementFactory.getRuntimeMXBean().getName();
    }
    /**
     * 初始化主机名称
     */
    private static String initHostName() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            return inetAddress.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "Unknown Host";
        }
    }

    public static void main(String[] args) {
        System.out.println("本机IP地址: " + LOCAL_IP_ADDRESS);
        System.out.println("当前进程PID: " + PROCESS_ID);
        System.out.println("主机名称: " + HOST_NAME);
    }
}
