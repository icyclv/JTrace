package com.second.jtrace.agent;


import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


public class JTraceAgent {
    private static final String JTRACE_CLIENT = "com.second.jtrace.core.client.JTraceClient";
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

    public static ClassLoader getClassLoader(File arthasCoreJarFile) throws MalformedURLException {
        if (jtraceClassLoader == null) {
            jtraceClassLoader = new JTraceClassloader(new URL[]{arthasCoreJarFile.toURI().toURL()});
        }
        return jtraceClassLoader;
    }

    private static synchronized void attach(String args, final Instrumentation inst) {
        try {
            String[] configs = args.split(",");
            if (configs.length != 4) {
                throw new IllegalArgumentException("Invalid agent arguments! args format: appName host port corePath");
            }
            String appName = configs[0];
            String ip = configs[1];
            String port = configs[2];
            String corePath = configs[3];

            File coreJarFile = new File(corePath);
            if (!coreJarFile.exists() || !coreJarFile.isFile()) {
                throw new IllegalArgumentException("Invalid core jar path: " + corePath);
            }
            final ClassLoader agentLoader = getClassLoader(coreJarFile);
            Class<?> clientClass = agentLoader.loadClass(JTRACE_CLIENT);
            // new JTraceClient(appName, ip, port, inst);
            clientClass.getConstructor(String.class, String.class, int.class, Instrumentation.class).newInstance(appName, ip, Integer.parseInt(port), inst);


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
            String appName = config.get("name");
            String ip = config.get("ip");
            String port = config.get("port");
            String agentPath = config.get("agentJar");
            String corePath = config.get("coreJar");

            if (appName.contains(";")) {
                throw new IllegalArgumentException("Name cannot contain ';'");
            }

            VirtualMachine virtualMachine = VirtualMachine.attach(pid);
            virtualMachine.loadAgent(agentPath, appName + ";" + ip + ";" + port + ";" + corePath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
