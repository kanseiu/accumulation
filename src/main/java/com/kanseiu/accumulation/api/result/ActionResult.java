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

    public static ActionResult createActionResult(boolean succeed, int code, String message){
        ActionResult result = new ActionResult();
        result.setSuccess(succeed);
        result.setCode(code);
        result.setMessage(message);
        return result;
    }

    public static ActionResult ok(){
        return createActionResult(true, ResultCodes.SUCCESS, ResultCodes.SUCCESS_FLAG);
    }

    public static ActionResult fail(String msg){
        return createActionResult(false, ResultCodes.FAIL, msg);
    }

    public static ActionResult exception(Exception e) {
        return createActionResult(false, ResultCodes.FAIL, String.format("%s:%s", ResultCodes.FAIL_FLAG, e.getMessage()));
    }
}
