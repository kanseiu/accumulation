package com.kanseiu.accumulation.api.result;

import com.spire.ms.System.Exception;
import lombok.Data;

@Data
public class ActionResult {

    /** 是否成功 */
    private boolean success;

    /** 结果编码 */
    private int code;

    /** 结果具体信息 */
    private String message;

    public static ActionResult CreateActionResult(boolean succeed, int code, String message){
        ActionResult result = new ActionResult();
        result.setSuccess(succeed);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static ActionResult ok(){
        return CreateActionResult(true, ResultCodes.SUCCESS, ResultCodes.SuccessFlag);
    }

    public static ActionResult fail(String msg){
        return CreateActionResult(false, ResultCodes.FAIL, msg);
    }

    public static ActionResult exception(Exception e) {
        return CreateActionResult(false, ResultCodes.FAIL, String.format("%s:%s", ResultCodes.FailFlag, e.getMessage()));
    }
}
