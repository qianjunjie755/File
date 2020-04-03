package com.biz.credit.utils;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalCache {

    private static ThreadLocal<Map<String, Object>> cache=new ThreadLocal<Map<String,Object>>();

    /**
     * 从ThreadLocal里获取缓存的值
     * @param key 要获取的数据的KEY
     * @return 要获取的值
     */
    public static Object getCache(String key) {
        Map<String, Object> map = cache.get();
        if(isCaheIsNull())
            return null;
        if(map.containsKey(key)){
            return map.get(key);
        }else {
            return null;
        }
    }

    /**
     * 向ThreadLocal缓存值
     * @param key 要缓存的KEY
     * @param value 要缓存的VALUE
     */
    public static void set(String key,Object value)
    {
        if(!isCaheIsNull()){
            cache.get().put(key, value);
        }
        else{
            Map<String, Object> vmap = new HashMap<String, Object>();
            vmap.put(key, value);
            cache.set(vmap);
        }
    }

    /**
     * 根据KEY移除缓存里的数据
     * @param key
     */
    public static void removeByKey(String key){
        if(isCaheIsNull())
            return;
        else{
            cache.get().remove(key);
        }

    }

    /**
     * 移除当前线程缓存
     * 用于释放当前线程threadlocal资源
     */
    public static void remove(){
        cache.remove();
    }

    private static boolean isCaheIsNull(){
        return  cache.get()==null;
    }
}
