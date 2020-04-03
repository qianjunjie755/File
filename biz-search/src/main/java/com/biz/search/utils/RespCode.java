package com.biz.search.utils;

public enum RespCode {

    SUCCESS("00", "success"),
    NO_RESULT("01","no data found"),
    UPLOAD_WRONG("02","invalid"),
    REQ_VALID("401","invalid"),
    LIMIT_WRONG("04","invalid"),
    TOTAL_LIMIT_WRONG("05","invalid"),
    SYS_LIMIT_WRONG("06","invalid"),
    TEMPLATE_NAME_WRONG("07","invalid"),
    MODULE_TYPE_REPEAT("10","invalid"),
    WARN("500", "error");

    private String code;
    private String msg;

    RespCode(String code, String msg) {
        this.code=code;this.msg = msg;
    }


    public String getCode() {
        return code;
    }
    public String getMsg() {
        return msg;
    }
}
