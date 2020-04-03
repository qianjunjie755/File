package com.biz.credit.utils;

import com.google.common.collect.Maps;
import org.springframework.cglib.beans.BeanMap;

import java.util.Map;

public class MapTransformUtils {
    /**
     * 将对象装换为map * * @param bean * @return
     */
    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }


}
