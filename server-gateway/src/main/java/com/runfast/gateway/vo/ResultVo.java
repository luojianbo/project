package com.runfast.gateway.vo;


import com.runfast.gateway.enums.ResultCodeEnum;

public final class ResultVo<D> {
    private boolean success;
    private String msg;
    private ResultCodeEnum errorCode;
    private String errorMsg;
    private D data;

    public ResultVo() {
    }

    private ResultVo(boolean success, String msg, ResultCodeEnum errorCode, String errorMsg, D data) {
        this.success = success;
        this.msg = msg;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }


    public static ResultVo ok(String msg) {
        return ok(msg, null);
    }


    public static <D> ResultVo<D> ok(String msg, D data) {
        return newInstance(true, msg, null, null, data);
    }

    public static <D> ResultVo<D> ok(ResultCodeEnum successCode, String msg, D data) {
        return newInstance(true, null, successCode, msg, data);
    }

    public static ResultVo fail(ResultCodeEnum errorCode) {
        return fail(errorCode, errorCode.getDescription());
    }

    public static ResultVo fail(ResultCodeEnum errorCode, String errorMsg) {
        return newInstance(false, null, errorCode, errorMsg, null);
    }

    public static <D> ResultVo<D> fail(ResultCodeEnum errorCode, String errorMsg, D data) {
        return newInstance(false, null, errorCode, errorMsg, data);
    }

    private static <D> ResultVo<D> newInstance(boolean success, String msg, ResultCodeEnum errorCode, String errorMsg, D data) {
        return new ResultVo(success, msg, errorCode, errorMsg, data);
    }


    public boolean isSuccess() {
        return success;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getMsg() {
        return msg;
    }

    public ResultCodeEnum getErrorCode() {
        return errorCode;
    }

    public D getData() {
        return data;
    }
}

