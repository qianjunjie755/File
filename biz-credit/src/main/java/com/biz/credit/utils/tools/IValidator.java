package com.biz.credit.utils.tools;

public interface IValidator {
    public  boolean validate(String string) throws Exception;
    public String errMsg();
}
