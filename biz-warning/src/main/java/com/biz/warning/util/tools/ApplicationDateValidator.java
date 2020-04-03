package com.biz.warning.util.tools;

import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

@Slf4j
public class ApplicationDateValidator implements IValidator {
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
                    log.error("日期["+string+"]格式错误：" + e2.getMessage());
                }
            }
        }
        return false;
    }

    public static DateTime  parseFromString(String string)  {
        try{
            DateTime dateTime = DateTime.parse(string,DateTimeFormat.forPattern("yyyy/MM/dd"));
            if(null!= dateTime){
                return dateTime;
            }
        }catch (Exception e){
            try{
                DateTime dateTime = DateTime.parse(string,DateTimeFormat.forPattern("yyyy-MM-dd"));
                if(null!=dateTime){
                    return dateTime;
                }
            }catch (Exception e1){
                try{
                    DateTime dateTime = DateTime.parse(string,DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                    if(null!=dateTime){
                        return dateTime;
                    }
                }catch (Exception e2){
                    log.error(e2.getMessage(),e2);
                }
            }
        }
        return null;
    }



    @Override
    public String errMsg() {
        return "申请日期格式不正确";
    }

    public static void main(String[] args) {
        DateTime dateTime = DateTime.parse("2000/1/1",DateTimeFormat.forPattern("yyyy/MM/dd"));
        System.out.println(dateTime.toString());
    }
}
