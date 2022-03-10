package com.kanseiu.accumulation.api.result;

import lombok.Data;

@Data
public class QueryResult<T> extends ActionResult {

    /** 结果数据 */
    private T data;

    public static <TData> QueryResult<TData> CreateQueryResult(boolean succeed, TData data, int code, String message){
        QueryResult<TData> result = new QueryResult<>();
        result.setSuccess(succeed);
        result.setData(data);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static <TData> QueryResult<TData> fail(TData data, String msg){
        return CreateQueryResult(false, data, ResultCodes.FAIL, msg);
    }

    public static <TData> QueryResult<TData> ok(TData data){
        return CreateQueryResult(true, data, ResultCodes.SUCCESS, ResultCodes.SuccessFlag);
    }

    public static <TData> QueryResult<TData> ok(TData data, String msg){
        return CreateQueryResult(true, data, ResultCodes.SUCCESS, msg);
    }
}
