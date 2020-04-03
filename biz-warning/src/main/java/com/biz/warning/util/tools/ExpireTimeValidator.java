package com.biz.warning.util.tools;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

@Slf4j
public class ExpireTimeValidator implements IValidator {
    @Override
    public boolean validate(String string) throws Exception {
        try{
            DateTime dateTime = DateTime.parse(string,DateTimeFormat.forPattern("yyyy/MM/dd"));
            if(null!= dateTime){
                return true;
            }
        }catch (Exception e){
            try{
                DateTime dateTime = DateTime.parse(string,DateTimeFormat.forPattern("yyyy-MM-dd"));
                if(null!=dateTime){
                    return true;
                }
            }catch (Exception e1){
                try{
                    DateTime dateTime = DateTime.parse(string,DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                    if(null!=dateTime){
                        return true;
                    }
                }catch (Exception e2){
                    log.error(e2.getMessage(),e2);
                }
            }
        }
        return false;
    }
    public static String formatDatetime(String dateStr,String formatter){
        return DateTime.parse(dateStr.replaceAll("/","-")).toString(formatter);
    }

    @Override
    public String errMsg() {
        return "截止日期格式不正确";
    }
}
