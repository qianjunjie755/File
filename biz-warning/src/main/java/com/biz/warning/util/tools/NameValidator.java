package com.biz.warning.util.tools;

import org.apache.commons.lang.StringUtils;

public class NameValidator implements IValidator {
    @Override
    public boolean validate(String string) throws Exception {
        return StringUtils.isNotEmpty(string);
    }

    @Override
    public String errMsg() {
        return "法人姓名不能为空";
    }
}
