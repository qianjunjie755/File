package com.biz.credit.utils.tools;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class Regex_CreditCode implements IValidator {

     public  boolean validate(String businessCode) throws Exception {
        /*if (StringUtils.isEmpty(businessCode) || businessCode.length() != 18) {
            return false;
        }
        String baseCode = "0123456789ABCDEFGHJKLMNPQRTUWXY";
        char[] baseCodeArray = baseCode.toCharArray();
        Map<Character, Integer> codes = new HashMap<Character, Integer>();
        for (int i = 0; i < baseCode.length(); i++) {
            codes.put(baseCodeArray[i], i);
        }
        char[] businessCodeArray = businessCode.toCharArray();
        Character check = businessCodeArray[17];
        if (baseCode.indexOf(check) == -1) {
            return false;
        }
        int[] wi = { 1, 3, 9, 27, 19, 26, 16, 17, 20, 29, 25, 13, 8, 24, 10, 30, 28 };
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            Character key = businessCodeArray[i];
            if (baseCode.indexOf(key) == -1) {
                return false;
            }
            sum += (codes.get(key) * wi[i]);
        }
        int value = 31 - sum % 31;
        return value == codes.get(check);*/
        return true;
    }

    @Override
    public String errMsg() {
        return "统一社会信用代码不合法";
    }



}
