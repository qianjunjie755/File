package com.biz.chart.utils;

import java.util.HashMap;
import java.util.Map;

public enum RespCode {

    SUCCESS("00", "success"),
    NO_RESULT("01","success"),
    INVALID("02","invalid"),
    REQ_VALID("401","invalid"),
    ERROR("500", "error");
    private String code;
    private String message;

    RespCode(String code, String message) {
        this.code=code;this.message = message;
    }


    public String getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }


    public static Map<String,RespCode> CODE_MAP = new HashMap<String,RespCode>(){
        {
            put("00",SUCCESS);
            put("0",SUCCESS);
            put("01",NO_RESULT);
            put("1",NO_RESULT);
            put("02",INVALID);
            put("2",INVALID);
            put("500",ERROR);
        }
    };
}
