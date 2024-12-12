package com.second.jtrace.common;

import java.io.File;

public class JTraceConstants {
    public static final byte[] VERSION =[1,0,0];
    public static final String JTRACE_HOME = ".jtrace";
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

    static{
        TMP_DIR = getTempDir();

    }




    public static String getTempDir(){
        String path="";
        if(OSUtils.isWindows()){
            path = System.getenv("LOCALAPPDATA") + File.separator +JTraceConstants.JTRACE_HOME;
        }else {
            path = System.getProperty("user.home") + File.separator + JTraceConstants.JTRACE_HOME;
        }
        path = path + File.separator + "tmp"+File.separator;
        File file = new File(path);
        if(!file.exists()){
            file.mkdirs();
        }
        return path;
    }


}
