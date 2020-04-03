package com.biz.warning.util.tools;

import org.apache.commons.lang.math.NumberUtils;

public class PhoneValidator implements IValidator {
    @Override
    public boolean validate(String string) throws Exception {
        return NumberUtils.isNumber(string);
    }

    @Override
    public String errMsg() {
        return "法人联系电话不合法";
    }
}
