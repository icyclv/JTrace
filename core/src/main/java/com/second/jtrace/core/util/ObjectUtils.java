package com.second.jtrace.core.util;


import com.second.jtrace.core.protocol.GsonSerializer;

public final class ObjectUtils {
    private ObjectUtils() {

    }

    /**
     * 获取obj的描述
     *
     * @param obj
     * @param deep
     * @param sizeLimit
     * @param isJson
     * @return
     */
    public static String getObjectInfo(Object obj, int sizeLimit, int deep, boolean isJson) {
        String info = "";
        if (isJson) {
            try {
                info = GsonSerializer.toJson(obj);
                return info;
            } catch (Exception ex) {
                info = "parse json error: " + ex.getMessage() + ".\n";
            }
        }
        info = info + getObjectInfo(obj, sizeLimit, deep);
        return info;
    }

    /**
     * 获取obj的描述
     *
     * @param obj
     * @param sizeLimit
     * @param deep
     * @return
     */
    public static String getObjectInfo(Object obj, int sizeLimit, int deep) {
        ObjectRender objectRender = new ObjectRender(sizeLimit, deep);
        return objectRender.getObjectInfo(obj);
    }
}
