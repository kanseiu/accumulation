package com.kanseiu.accumulation.constant;

public enum ExceptionMessageEnum implements MessageEnum {

    COMMON_EXCEPTION("未知错误");

    private String message;

    ExceptionMessageEnum(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
