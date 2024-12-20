package com.second.jtrace.core.util;

public class VersionUtil {

    /**
     * 比较两个版本号。
     *
     * @param v1 第一个版本号，表示为 byte[]
     * @param v2 第二个版本号，表示为 byte[]
     * @return -1 如果 v1 < v2，0 如果 v1 == v2，1 如果 v1 > v2
     */
    public static int compareVersion(byte[] v1, byte[] v2) {
        int maxLength = Math.max(v1.length, v2.length);

        for (int i = 0; i < maxLength; i++) {
            // 获取当前字节的值，不足的部分补 0
            int part1 = i < v1.length ? Byte.toUnsignedInt(v1[i]) : 0;
            int part2 = i < v2.length ? Byte.toUnsignedInt(v2[i]) : 0;

            // 比较当前部分
            if (part1 != part2) {
                return Integer.compare(part1, part2);
            }
        }

        // 所有部分都相等
        return 0;
    }
}
