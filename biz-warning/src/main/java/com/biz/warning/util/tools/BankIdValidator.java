package com.biz.warning.util.tools;

import org.apache.commons.lang.StringUtils;

public class BankIdValidator implements IValidator {
    @Override
    public boolean validate(String string) throws Exception {
       //boolean isTrue=StringUtils.isNotEmpty(string);
        return StringUtils.isNotEmpty(string);
    }

    @Override
    public String errMsg() {
        return "企业开户账号不能为空";
    }
}