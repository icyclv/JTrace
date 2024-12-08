package com.second.jtrace.attach;

import com.second.JTrace.utils.AnsiLog;
import com.second.JTrace.utils.IOUtils;
import com.second.JTrace.utils.JavaVersionUtils;

import java.io.*;
import java.util.*;

public class BootUtils {
    private static String FOUND_JAVA_HOME = null;


    public static void parseConfig(String[] args, Map<String, String> config) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--") && i + 1 < args.length) {
                String key = args[i].substring(2);
                String value = args[i + 1];
                config.put(key, value);
                i++; // 跳过下一个参数
            }
        }
    }

    public static long readPid(Set<Long> pidSet) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String input = reader.readLine();
        long pid = Long.parseLong(input);
        if (!pidSet.contains(pid)) {
            throw new Exception("Invalid PID. Please enter a valid PID.");
        }
        return pid;
    }

    public static boolean isValidJarPath(String agentJar, String coreJar) {


        if (!agentJar.endsWith(".jar") || !coreJar.endsWith(".jar")) {
            return false;
        }

        File agentJarFile = new File(agentJar);
        File coreJarFile = new File(coreJar);

        return agentJarFile.exists() && coreJarFile.exists() && agentJarFile.isFile() && coreJarFile.isFile();

    }

    public static String findJavaHome() {
        if (FOUND_JAVA_HOME != null) {
            return FOUND_JAVA_HOME;
        }

        String javaHome = System.getProperty("java.home");

        if (JavaVersionUtils.isLessThanJava9()) {
            File toolsJar = new File(javaHome, "lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // maybe jre
                toolsJar = new File(javaHome, "../../lib/tools.jar");
            }

            if (toolsJar.exists()) {
                FOUND_JAVA_HOME = javaHome;
                return FOUND_JAVA_HOME;
            }

            if (!toolsJar.exists()) {
                AnsiLog.debug("Can not find tools.jar under java.home: " + javaHome);
                String javaHomeEnv = System.getenv("JAVA_HOME");
                if (javaHomeEnv != null && !javaHomeEnv.isEmpty()) {
                    AnsiLog.debug("Try to find tools.jar in System Env JAVA_HOME: " + javaHomeEnv);
                    // $JAVA_HOME/lib/tools.jar
                    toolsJar = new File(javaHomeEnv, "lib/tools.jar");
                    if (!toolsJar.exists()) {
                        // maybe jre
                        toolsJar = new File(javaHomeEnv, "../lib/tools.jar");
                    }
                }

                if (toolsJar.exists()) {
                    AnsiLog.info("Found java home from System Env JAVA_HOME: " + javaHomeEnv);
                    FOUND_JAVA_HOME = javaHomeEnv;
                    return FOUND_JAVA_HOME;
                }

                throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome
                        + ", please try to start jtrace-boot with full path java. Such as /opt/jdk/bin/java -jar jtrace-boot.jar");
            }
        } else {
            FOUND_JAVA_HOME = javaHome;
        }
        return FOUND_JAVA_HOME;
    }

    public static File findToolsJar() {
        String javaHome = findJavaHome();
        if (JavaVersionUtils.isLessThanJava9()) {
            File toolsJar = new File(javaHome, "lib/tools.jar");
            if (!toolsJar.exists()) {
                toolsJar = new File(javaHome, "../lib/tools.jar");
            }
            if (!toolsJar.exists()) {
                // maybe jre
                toolsJar = new File(javaHome, "../../lib/tools.jar");
            }

            if (toolsJar.exists()) {
                return toolsJar;
            }

            throw new IllegalArgumentException("Can not find tools.jar under java home: " + javaHome
                    + ", please try to start jtrace-boot with full path java. Such as /opt/jdk/bin/java -jar jtrace-boot.jar");
        } else {
            return null;
        }
    }

    public static File findJavaExecutable() {
        String javaHome = findJavaHome();
        String[] paths = {"bin/java", "bin/java.exe", "../bin/java", "../bin/java.exe"};

        List<File> javaList = new ArrayList<File>();
        for (String path : paths) {
            File javaFile = new File(javaHome, path);
            if (javaFile.exists()) {
                AnsiLog.debug("Found java: " + javaFile.getAbsolutePath());
                javaList.add(javaFile);
            }
        }

        if (javaList.isEmpty()) {
            AnsiLog.debug("Can not find java/java.exe under current java home: " + javaHome);
            return null;
        }

        // find the shortest path, jre path longer than jdk path
        if (javaList.size() > 1) {
            Collections.sort(javaList, new Comparator<File>() {
                @Override
                public int compare(File file1, File file2) {
                    try {
                        return file1.getCanonicalPath().length() - file2.getCanonicalPath().length();
                    } catch (IOException e) {
                        // ignore
                    }
                    return -1;
                }
            });
        }
        return javaList.get(0);
    }

    public static void startAttachAgent(long pid, String agentJar, String coreJar, String server, String ip, String name) {
        try {
            File java = findJavaExecutable();
            if (java == null) {
                AnsiLog.error("Can not find java executable");
                return;
            }

            File toolsJar = findToolsJar();
            if (JavaVersionUtils.isLessThanJava9()) {
                if (toolsJar == null || !toolsJar.exists()) {
                    throw new IllegalArgumentException("Can not find tools.jar under java home: " + findJavaHome());
                }
            }

            List<String> command = new ArrayList<>();
            command.add(java.getAbsolutePath());
            if (toolsJar != null && toolsJar.exists()) {
                command.add("-Xbootclasspath/a:" + toolsJar.getAbsolutePath());
            }
            command.add("-jar");
            command.add(agentJar);
            command.add("--pid");
            command.add(String.valueOf(pid));
            command.add("--server");
            command.add(server);
            command.add("--ip");
            command.add(ip);
            command.add("--name");
            command.add(name);
            command.add("--core");
            command.add(coreJar);
            ProcessBuilder pb = new ProcessBuilder(command);
            // clear JAVA_TOOL_OPTIONS to avoid conflict with other tools
            pb.environment().put("JAVA_TOOL_OPTIONS", "");

            pb.redirectErrorStream(true);
            try {
                final Process proc = pb.start();
                Thread redirectStdout = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InputStream inputStream = proc.getInputStream();
                        try {
                            IOUtils.copy(inputStream, System.out);
                        } catch (IOException e) {
                            IOUtils.close(inputStream);
                        }

                    }
                });

                Thread redirectStderr = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        InputStream inputStream = proc.getErrorStream();
                        try {
                            IOUtils.copy(inputStream, System.err);
                        } catch (IOException e) {
                            IOUtils.close(inputStream);
                        }

                    }
                });
                redirectStdout.start();
                redirectStderr.start();
                redirectStdout.join();
                redirectStderr.join();

                int exitValue = proc.exitValue();
                if (exitValue != 0) {
                    AnsiLog.error("attach fail, targetPid: " + pid);
                    System.exit(1);
                }
            } catch (Throwable e) {
                // ignore
            }
        } catch (Exception e) {
            AnsiLog.error("Error attaching agent: " + e.getMessage());
        }
    }

}
