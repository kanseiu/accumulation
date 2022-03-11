package com.kanseiu.accumulation.util;

public class MyStringUtil {

    private MyStringUtil(){
        throw new IllegalStateException();
    }

    /**
     * 默认空字符串
     */
    public static final String EMPTY = "";

    /**
     * 判断是否null或者空字符串
     * @param srcStr
     * @return
     */
    public static boolean isNullOrEmpty(String srcStr) {
        return srcStr == null || srcStr.trim().length() <= 0;
    }
}
