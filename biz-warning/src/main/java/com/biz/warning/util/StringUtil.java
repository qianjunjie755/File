package com.biz.warning.util;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class StringUtil {

    public static List<Integer> changeStr2IntList(String str,String regex){
        if(StringUtils.isEmpty(str)){
            return null;
        }
        String[] strArr = str.split(regex);
        List<Integer> resultList = new ArrayList<>();
        for (String s : strArr) {
            resultList.add(Integer.parseInt(s));
        }
        return resultList;
    }
}
