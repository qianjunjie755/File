package com.biz.warning.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.warning.vo.Threshold;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

@Slf4j
public class ThresholdUtil {

    public static int  checkRule(Object result,String threshold){
        try{
            if(StringUtils.isNotEmpty(threshold)&&null!=result&&StringUtils.isNotEmpty(result.toString())){
                JSONObject thresholdJson = JSONObject.parseObject(threshold);
                String type = thresholdJson.getString("type");
                if(StringUtils.equals("compare",type)){
                    Object eqValue = thresholdJson.get("eq");
                    Object gtValue = thresholdJson.get("gt");
                    Object ltValue = thresholdJson.get("lt");
                    if(null!=eqValue&&customCompare(eqValue,result)==0){
                        return 1;
                    }
                    if(null!=gtValue&&customCompare(gtValue,result)>0){
                        return 1;
                    }
                    if(null!=ltValue&&customCompare(result,ltValue)>0){
                        return 1;
                    }
                }else if(StringUtils.equals("equal",type)){
                    Object eqValue = thresholdJson.get("eq");
                    if(null!=eqValue&&customCompare(result,eqValue)==0){
                        return 1;
                    }
                }
            }else{
                return -1;
            }
            return 0;
        }catch (Exception e){
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * 获取判断条件描述
     * @param threshold
     * @return
     */
    public static String getJudge(String threshold) {
        Threshold thd = parse(threshold);
        return thd == null ? null : thd.getJudge();
    }

    /**
     * 获取阈值
     * @param threshold
     * @return
     */
    public static String getThresholdValue(String threshold){
        Threshold thd = parse(threshold);
        if("equal".equals(thd.getType())){
            switch (thd.getEq()){
                case "true":
                    return "是";
                case "false":
                    return "否";
                default:
                    return thd.getEq();
            }
        }
        return StringUtils.isEmpty(thd.getEq())?(StringUtils.isEmpty(thd.getLt())?thd.getGt():thd.getLt()):thd.getEq();
    }

    /**
     * 获取时间范围
     * @param period
     * @param periodUnit
     * @return
     */
    public static String  getTimeframe(int period,String periodUnit) {
        String timef=StringUtils.EMPTY;
        if(period<0){
            timef="无";
        }else{
            timef="近"+period;
            if(StringUtils.equals("d",periodUnit)){
                timef = timef + "天";
            }else{
                timef = timef + "个月";
            }
        }
        return timef;
    }

    /**
     * 将阈值字符串转换成Threshold类
     * @param threshold
     * @return
     */
    private static Threshold parse(String threshold) {
        try {
            return JSON.parseObject(threshold, Threshold.class);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }




    public static int customCompare(Object low,Object high){
    if(NumberUtils.isNumber(low.toString())){
        Double lowDouble = Double.parseDouble(low.toString());
        Double highDouble = Double.parseDouble(high.toString());
        return highDouble.compareTo(lowDouble);
    }
    return high.toString().compareTo(low.toString());
    }

    public static void main(String[] args) {
        /*JSONObject jo =  new JSONObject();
        jo.put("dd","1.02");
        System.out.println("-333.33333".compareTo("-333.33332"));
        Object d = 1;
        System.out.println(jo.get("dd") instanceof Double);*/
/*        System.out.println(NumberUtils.isNumber("300"));
        System.out.println(new ThresholdUtil().checkRule("F","{type:\"compare\",\"lt\":null,\"gt\":\"C\",\"eq\":\"C\"}"));
        System.out.println(new ThresholdUtil().checkRule("49","{type:\"compare\",\"lt\":null,\"gt\":\"50\",\"eq\":\"50\"}"));
        System.out.println(new ThresholdUtil().checkRule("3000.00","{type:\"compare\",\"lt\":\"50\",\"gt\":null,\"eq\":null}"));*/
        System.out.println(new ThresholdUtil().checkRule("true","{type:\"equal\",\"lt\":null,\"gt\":null,\"eq\":\"false\"}"));
    }
}
