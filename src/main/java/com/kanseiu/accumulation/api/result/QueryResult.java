package com.kanseiu.accumulation.api.result;

import lombok.Data;

@Data
public class QueryResult<T> extends ActionResult {

    /** 结果数据 */
    private T data;

    public static <T> QueryResult<T> createQueryResult(boolean succeed, T data, int code, String message){
        QueryResult<T> result = new QueryResult<>();
        result.setSuccess(succeed);
        result.setData(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <T> QueryResult<T> fail(T data, String msg){
        return createQueryResult(false, data, ResultCodes.FAIL, msg);
    }

    public static <T> QueryResult<T> ok(T data){
        return createQueryResult(true, data, ResultCodes.SUCCESS, ResultCodes.SUCCESS_FLAG);
    }

    public static <T> QueryResult<T> ok(T data, String msg){
        return createQueryResult(true, data, ResultCodes.SUCCESS, msg);
    }
}
