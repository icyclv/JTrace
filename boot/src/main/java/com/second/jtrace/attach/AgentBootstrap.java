package com.second.jtrace.attach;

import com.second.jtrace.common.AnsiLog;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;

import java.util.*;

import static com.second.jtrace.attach.BootUtils.isValidJarPath;
import static com.second.jtrace.attach.BootUtils.readPid;

public class AgentBootstrap {

    public static void main(String[] args) {
        Map<String, String> defaultValues = new HashMap<>();
        defaultValues.put("server", "localhost");
        defaultValues.put("ip", "127.0.0.1");
        defaultValues.put("name", "default-agent");
        defaultValues.put("agentJar", "~/.jtrace/lib/agent.jar");
        defaultValues.put("coreJar", "~/.jtrace/lib/core.jar");

        Map<String, String> config = new HashMap<>(defaultValues);
        BootUtils.parseConfig(args, config);

        System.out.println("Current Configuration:");
        config.forEach((key, value) -> System.out.println(key + ": " + value));
        // 提示是否需要修改配置或继续
        System.out.println("\nDo you want to continue with the current configuration? (Y/N)");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        if (!input.equalsIgnoreCase("Y")) {
            System.out.println("Please enter the new configuration( --key value), or press Enter to use the default value:");
            String[] newArgs = scanner.nextLine().split(" ");
            BootUtils.parseConfig(newArgs, config);
        }

        if (!isValidJarPath(config.get("agentJar"), config.get("coreJar"))) {
            System.err.println("Invalid path. Please check your input.");
            return;
        }

        List<VirtualMachineDescriptor> vms = VirtualMachine.list();
        Set<Long> pidSet = new HashSet<>();

        System.out.println("\nRunning JVM Processes:");
        for (VirtualMachineDescriptor vm : vms) {
            if (vm.displayName().contains("com.com.second.jtrace.attach.AgentBootstrap")) {
                continue;
            }
            System.out.println("PID: " + vm.id() + " - Name: " + vm.displayName());
            pidSet.add(Long.parseLong(vm.id()));
        }

        System.out.println("\nEnter the PID of the target JVM to attach the agent:");
        long pid = 0;
        while (true) {
            try {
                pid = readPid(pidSet);
                break;
            } catch (Exception e) {
                AnsiLog.error("Invalid PID. The PID must be one of the listed PIDs.");
            }
        }

        BootUtils.startAttachAgent(pid, config.get("agentJar"), config.get("coreJar"), config.get("server"), config.get("ip"), config.get("name"));


    }


}
