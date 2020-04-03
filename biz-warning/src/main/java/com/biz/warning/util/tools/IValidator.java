package com.biz.warning.util.tools;

public interface IValidator {
    boolean validate(String string) throws Exception;
    String errMsg();
}
