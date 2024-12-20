package com.second.jtrace.common;

import java.io.File;

public class JTraceConstants {
    public static final byte[] VERSION = new byte[]{1, 0, 0};
    public static final String JTRACE_HOME;
    public static final String JTRACE_AGENT_JAR = "jtrace-agent.jar";
    public static final String JTRACE_AGENT_CLASS = "com.second.jtrace.agent.JTraceAgent";
    public static final String JTRACE_AGENT_CLASSLOADER_CLASS = "com.second.jtrace.agent.JTraceClassloader";
    public static final int MAGIC_NUMBER = 0xACEACEAC;
    public static final int TIME_OUT = 20;
    public static final int MAX_RETRY = 10;
    public static final String NONE = "none";
    public static final String TMP_DIR;
    public static final String EMPTY_STRING = "";
    public static final String TYPE_HEAP = "heap";
    public static final String TYPE_NON_HEAP = "non_heap";
    public static final String TYPE_BUFFER_POOL = "buffer_pool";
    public static final String UNKNOWN = "Unknown";
    public static final String ACCESS_POINT_BEFORE = "before";
    public static final String ACCESS_POINT_AFTER = "after";
    public static final String ACCESS_POINT_EXCEPTION = "exception";
    public static final String OUTPUT_DIR;
    public static final long DEFAULT_METRIC_INTERVAL = 60000;
    public static final int MAX_STRING_LENGTH = 800000;
    public static final String LIB_DIR;

    static {
        JTRACE_HOME = getJTraceHome();
        TMP_DIR = getTempDir();
        OUTPUT_DIR = getOutputDir();
        LIB_DIR = getLibDir();
    }


    public static String getJTraceHome() {
        String path = "";
        String dirName = ".jtrace";
        path = System.getProperty("user.home") + File.separator + dirName;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getTempDir() {
        String path = JTRACE_HOME + File.separator + "tmp";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public static String getLibDir() {
        String path = JTRACE_HOME + File.separator + "lib";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }


    public static String getOutputDir() {
        String path = JTRACE_HOME + File.separator + "output" + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path;
    }


}
