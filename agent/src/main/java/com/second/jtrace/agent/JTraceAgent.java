package com.second.jtrace.agent;


import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class JTraceAgent {
    private static final String JTRACE_CLIENT = "com.second.jtrace.client.JTraceClient";
    private static volatile ClassLoader jtraceClassLoader;

    public static void resetClassLoader() {
        jtraceClassLoader = null;
    }

    public static void premain(String args, Instrumentation inst) {
        attach(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        attach(args, inst);
    }

    public static ClassLoader getClassLoader(File arthasClientJarFile) throws MalformedURLException {
        if (jtraceClassLoader == null) {
            jtraceClassLoader = new JTraceClassloader(new URL[]{arthasClientJarFile.toURI().toURL()});
        }
        return jtraceClassLoader;
    }

    private static synchronized void attach(String args, final Instrumentation inst) {
        try {
            System.out.println("JTraceAgent attach args: " + args);
            String[] configs = args.split(";");
            if (configs.length != 4) {
                throw new IllegalArgumentException("Invalid agent arguments! args format: appName host port clientPath");
            }
            String appName = configs[0];
            String ip = configs[1];
            String port = configs[2];
            String clientJar = configs[3];

            File clientJarFile = new File(clientJar);
            if (!clientJarFile.exists() || !clientJarFile.isFile()) {
                throw new IllegalArgumentException("Invalid client jar path: " + clientJar);
            }
            final ClassLoader agentLoader = getClassLoader(clientJarFile);
            Class<?> clientClass = agentLoader.loadClass(JTRACE_CLIENT);
            // new JTraceClient(inst,appName, ip, port);
            clientClass.getConstructor(Instrumentation.class, String.class, String.class, int.class).newInstance(inst, appName, ip, Integer.parseInt(port));


        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }


    public static void main(String[] args) {
        try {
            HashMap<String, String> config = new HashMap<>();
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("--") && i + 1 < args.length) {
                    String key = args[i].substring(2);
                    String value = args[i + 1];
                    config.put(key, value);
                    i++;
                }
            }

            String pid = config.get("pid");
            String appName = config.get("clientName");
            String ip = config.get("serverIP");
            String port = config.get("serverPort");
            String agentPath = config.get("agentJar");
            String clientJar = config.get("clientJar");

            if (appName.contains(";")) {
                throw new IllegalArgumentException("Name cannot contain ';'");
            }

            VirtualMachine virtualMachine = VirtualMachine.attach(pid);
            virtualMachine.loadAgent(agentPath, appName + ";" + ip + ";" + port + ";" + clientJar);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
