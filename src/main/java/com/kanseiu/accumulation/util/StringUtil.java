package com.kanseiu.accumulation.util;

public class StringUtil {
    /**
     * 默认空字符串
     */
    public static final String Empty = "";

    /**
     * 判断是否null或者空字符串
     * @param srcStr
     * @return
     */
    public static boolean isNullOrEmpty(String srcStr) {
        return srcStr == null || srcStr.length() <= 0;
    }
}
