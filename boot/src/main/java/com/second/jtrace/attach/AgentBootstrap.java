package com.second.jtrace.attach;

import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class AgentBootstrap {

    public static void main(String[] args) {
        Map<String, String> defaultValues = new HashMap<>();
        defaultValues.put("server", "localhost");
        defaultValues.put("ip", "127.0.0.1");
        defaultValues.put("name", "default-agent");
        defaultValues.put("agentJar", "~/.jtrace/lib/agent.jar");
        defaultValues.put("coreJar", "~/.jtrace/lib/core.jar");

        Map<String, String> config = new HashMap<>(defaultValues);
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("--") && i + 1 < args.length) {
                    String key = args[i].substring(2);
                    String value = args[i + 1];
                    config.put(key, value);
                    i++; // 跳过下一个参数
                }
            }
        } catch (Exception e) {
            System.err.println("Error parsing arguments. Please use the format: --key value");
            return;
        }

        System.out.println("Current Configuration:");
        config.forEach((key, value) -> System.out.println(key + ": " + value));

        if (!isValidConfig(config)) {
            System.err.println("Invalid configuration. Please check your input.");
            return;
        }

        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        System.out.println("\nRunning JVM Processes:");
        for (VirtualMachineDescriptor vm : vms) {
            System.out.println("PID: " + vm.id() + " - Name: " + vm.displayName());
        }

        // 提示用户输入 PID
        System.out.println("\nEnter the PID of the target JVM to attach the agent:");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            String input = reader.readLine();
            int pid = Integer.parseInt(input);

            // 尝试附加到目标 JVM
            attachAgent(pid, config.get("agentJar"));
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean isValidConfig(Map<String, String> config) {
        // 这里可以添加更多验证逻辑
        return config.get("agentJar").endsWith(".jar") && config.get("coreJar").endsWith(".jar");
    }

    private static void attachAgent(int pid, String agentJar) {
        try {
            VirtualMachine vm = VirtualMachine.attach(String.valueOf(pid));
            vm.loadAgent(agentJar);
            vm.detach();
            System.out.println("Successfully attached agent to JVM with PID: " + pid);
        } catch (Exception e) {
            System.err.println("Failed to attach agent to JVM. Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
