package com.biz.warning.util;

import java.util.HashMap;
import java.util.Map;

public enum RespCode {

    SUCCESS("00", "success"),
    NO_RESULT("01","success"),
    INVALID("02","invalid"),
    REQ_VALID("401","invalid"),
    ERROR("500", "error"),
    UPLOAD_WRONG("03","invalid"),           //进件部分内容错错误
    TEMPLATE_NAME_WRONG("04","invalid"),  //进件入参不正确
    COMPANY_NAME_REPEAT("05","进件公司重复"),
    RULE_SET_REPEAT("-1","规则集重复"),
    RULE_REPEAT("-2","规则重复");
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
            put("500",ERROR);;
            put("03",UPLOAD_WRONG);
            put("3",UPLOAD_WRONG);
            put("04",TEMPLATE_NAME_WRONG);
            put("4",TEMPLATE_NAME_WRONG);
            put("-1",RULE_SET_REPEAT);
            put("-2",RULE_REPEAT);
        }
    };
}
