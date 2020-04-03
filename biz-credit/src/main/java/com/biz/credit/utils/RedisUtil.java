package com.biz.credit.utils;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 根据前缀获取编号 如：Task --> Task20180608000001
     * @param prefix 前缀
     * @return
     */
    public String generateCodeNo(String prefix){
        String codeNo = StringUtils.EMPTY;
        if(StringUtils.isNotEmpty(prefix)){
            DateTime date = DateTime.now();
            String dateStr = date.toString("yyyyMMdd");
            String key = "{biz_credit:report:codeNo}:".concat(prefix).concat(":").concat(dateStr);
            Double id = stringRedisTemplate.opsForZSet().incrementScore(key,dateStr,1);
            codeNo=prefix.concat(dateStr).concat(String.format("%06d",id.intValue()));
            stringRedisTemplate.expire(key,24*3600, TimeUnit.SECONDS);
        }
        return codeNo;
    }

    public List<String> generateCodeNos(String prefix, Integer count){
        List<String> codeNos = new ArrayList<>();
        if(StringUtils.isNotEmpty(prefix)){
            DateTime date = DateTime.now();
            String dateStr = date.toString("yyyyMMdd");
            String key = "{biz_credit:report:codeNo}:".concat(prefix).concat(":").concat(dateStr);
            Double id = stringRedisTemplate.opsForZSet().incrementScore(key,dateStr,count);
            int start =  id.intValue() - count + 1;
            for(int i=start;i<=id;i = i + 1){
                String codeNo=prefix.concat(dateStr).concat(String.format("%06d",i));
                codeNos.add(codeNo);
            }
            stringRedisTemplate.expire(key,24*3600, TimeUnit.SECONDS);
        }
        return codeNos;
    }

    public String getCodeNo(String prefix){
        String codeNo = StringUtils.EMPTY;
        if(StringUtils.isNotEmpty(prefix)){
            DateTime date = DateTime.now();
            String dateStr = date.toString("yyyyMMdd");
            String key = "{biz:report:codeNo}:".concat(prefix).concat(":").concat(dateStr);
            Double id = stringRedisTemplate.opsForZSet().score(key,dateStr);
            id = id==null||id<=0?1:id;
            codeNo=prefix.concat(dateStr).concat(String.format("%06d",id.intValue()));

        }
        return codeNo;
    }

    public Long getApiReportId(String idName){
        if(StringUtils.isNotEmpty(idName)) {
            return stringRedisTemplate.opsForZSet().incrementScore("bi_credit:apiReport", idName, -1).longValue();
        }
        return null;
    }
}
