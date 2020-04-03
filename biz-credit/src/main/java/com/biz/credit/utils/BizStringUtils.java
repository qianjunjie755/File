package com.biz.credit.utils;

import org.springframework.stereotype.Component;

/**
 * 字符串处理工具类
 */
@Component
public class BizStringUtils {
    public static String unicodeToCn(String unicode) {
        String[] strings = unicode.split("\\\\u");
        String returnStr = "";
        for (int i = 1; i < strings.length; i++) {
            returnStr += (char) Integer.valueOf(strings[i], 16).intValue();
        }
        return returnStr;
    }
}
