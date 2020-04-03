package com.biz.warning.util;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yangjun
 */
public class BeanUtil {

    public static Map<String, Object> getBeanMap(Object obj) {
        return JSON.parseObject(JSON.toJSONString(obj));
    }

    public static <T> List<Map<String, Object>> getBeanMapList(List<T> beanList) {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Object obj : beanList) {
            resultList.add(getBeanMap(obj));
        }
        return resultList;
    }
}
