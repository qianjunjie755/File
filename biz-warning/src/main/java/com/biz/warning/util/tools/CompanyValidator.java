package com.biz.warning.util.tools;

import org.apache.commons.lang.StringUtils;

public class CompanyValidator implements IValidator {
    @Override
    public boolean validate(String string) throws Exception {
        return StringUtils.isNotEmpty(string);
    }

    @Override
    public String errMsg() {
        return "公司名不能为空";
    }
}
