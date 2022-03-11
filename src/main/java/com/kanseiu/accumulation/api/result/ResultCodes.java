package com.kanseiu.accumulation.api.result;

public class ResultCodes {

    private ResultCodes(){
        throw new IllegalStateException();
    }

    /** 成功 */
    public static final int SUCCESS = 200;

    /** 成功标志字符串 */
    public static final String SUCCESS_FLAG = "OK";

    /** 失败 */
    public static final int FAIL = -1;

    /** 失败标志字符串 */
    public static final String FAIL_FLAG = "NG";
}
