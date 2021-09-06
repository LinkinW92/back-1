package com.skeqi.htd.common;

/**
 * 接口返回码
 *
 * @author qingwei
 */
public enum ResultCode {
    SUCCESS(0, "成功"),
    BIZ_EXCEPTION(-1, "业务异常"),
    ILLEGAL_ARGUMENTS(-2, "非法参数"),
    /**
     * 接口超时，服务未找到，对应TException
     */
    API_ERROR(-3, "接口异常"),
    LIMITED_TASK(-4, "今日已达任务上限，明日再试试"),
    DUPLICATE_INVITATION(-5, "不可重复助力哦"),
    ERR_SPAM(-6, "分控"),
    OVERFLOW_STOCK(-7, "库存不足");

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
