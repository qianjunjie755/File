package com.biz.credit.schedule;

import com.biz.credit.dao.UploadRecordDAO;
import com.biz.credit.domain.UploadRecord;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UploadRecordSchedule {
    @Resource
    private UploadRecordDAO uploadRecordDAO;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Value("${schedule_run}")
    private Integer scheduleRun;
    @Scheduled(fixedDelay = 10000)
    public void storeToDB(){
        if(0==scheduleRun){
            return;
        }
        DateTime origin = DateTime.now();
        DateTime dateTime = origin.minusDays(1);
        String hKey = dateTime.toString("yyyyMMdd");
        int originYear = origin.getYear();
        int originMonth = origin.getMonthOfYear();
        int originDay = origin.getDayOfMonth();
        Set<String> keys = stringRedisTemplate.keys("biz_credit:report:uploadLimit:user::*");
        List<UploadRecord> list = new ArrayList<>();
        Set<String> apiCodes = new HashSet<>();
        for(String key : keys){
            String apiCode = key.substring("biz_credit:report:uploadLimit:user::".length(),key.indexOf(":","biz_credit:report:uploadLimit:user::".length()));
            apiCodes.add(apiCode);
            String userId = key.substring(key.lastIndexOf(":")+1);
            Double countTmp = stringRedisTemplate.opsForZSet().score(key,hKey);
            if(null!=countTmp&&0<countTmp){
                UploadRecord record = new UploadRecord();
                record.setApiCode(apiCode);
                record.setUserId(Integer.parseInt(userId));
                record.setDatetime(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
                record.setUploadCount(countTmp.intValue());
                list.add(record);
            }
        }
        if(!CollectionUtils.isEmpty(list)){
            int count = 0;
            try {
             count =  uploadRecordDAO.saveUploadRecord(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(count>0){
                for(String key : keys){
                    stringRedisTemplate.opsForZSet().remove(key,hKey);
                }
                for(String apiCode:apiCodes){
                    String key = "biz_credit:report:uploadLimit:apiCode:".concat(apiCode);
                    stringRedisTemplate.opsForZSet().remove(key,dateTime.toString(hKey));
                    if(1==originMonth&&3>=originDay){
                        DateTime time = DateTime.parse(String.valueOf(originYear-1));
                        for(int i=0;i<12;i++){
                            DateTime time2 = time.plusMonths(i);
                            stringRedisTemplate.opsForZSet().remove(key,time2.toString("yyyyMM"));
                        }
                    }
                }
            }
        }

    }

    public static void main(String[] args) {
        DateTime dateTime = DateTime.now();
        DateTime dateTime2 = dateTime.minusDays(1);
        System.out.println(dateTime2.toString("yyyy-MM-dd HH:mm:ss"));


        String key = "biz_credit:report:uploadLimit:user::4000159:1";
        System.out.println(key.substring("biz_credit:report:uploadLimit:user::".length(),key.indexOf(":","biz_credit:report:uploadLimit:user::".length())));
        System.out.println(key.substring(key.lastIndexOf(":")+1));
        DateTime t = DateTime.parse("2017");
        System.out.println(t.toString());
    }
}
