package com.biz.credit.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.biz.credit.vo.Threshold;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Objects;

@Getter
@Setter
public class DRuleVar {
    private Long varId;
    private Long varPId;
    private String varDesc;
    private Integer varWeight;
    private String varThreshold;
    private Integer varPeriod;
    private String periodUnit;
    private Long ruleId;
    private Long userId;
    private Integer status;


    private String thresholdValue;
    private String judge;
    /**
     * 判断符号
     * @return
     */
    public String getJudge() {
        Threshold thd = parse();
        if(thd != null){
            String type = thd.getType();
            if(Objects.equals(type,"equal")){
                return "=";
            }
            else if(Objects.equals(type,"notEqual")){
                return "!=";
            }
            else if(Objects.equals(type,"compare")){
                String res = "";
                String lt = thd.getLt();
                String gt = thd.getGt();
                String eq = thd.getEq();
                if(!StringUtils.isEmpty(lt)){
                    res += "<";
                }
                if(!StringUtils.isEmpty(gt)){
                    res += ">";
                }
                if(!StringUtils.isEmpty(eq)){
                    res += "=";
                }
                return res;
            }
        }
        return "";
    }


    public String getThresholdValue(){
        Threshold thd = parse();
        if(thd == null){
            return "";
        }
        if("equal".equals(thd.getType()) || "notEqual".equals(thd.getType())){
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

    public void buildThresholdValue() {
        if(StringUtils.isNotEmpty(thresholdValue)){
            String ts = getVarThreshold();
            Threshold thd = JSON.parseObject(ts,Threshold.class);
            String value = StringUtils.EMPTY;
            if("equal".equals(thd.getType()) || "notEqual".equals(thd.getType())){
                switch (thresholdValue){
                    case "是":
                        value="true";
                        break;
                    case "否":
                        value="false";
                        break;
                    default:
                        value = thresholdValue;
                }
            }else{
                value = thresholdValue;
            }
            JSONObject json = JSONObject.parseObject(getVarThreshold());
            if(StringUtils.isNotEmpty(thd.getEq())){
                json.put("eq",value);
            }
            if(StringUtils.isNotEmpty(thd.getLt())){
                json.put("lt",value);
            }
            if(StringUtils.isNotEmpty(thd.getGt())){
                json.put("gt",value);
            }
            setVarThreshold(json.toJSONString());
        }
    }

    public void buildPocThresholdValue() {
        if(StringUtils.isNotEmpty(thresholdValue)){
            String ts = getVarThreshold();
            Threshold thd = JSONArray.parseObject(ts,Threshold.class);
            String s1=StringEscapeUtils.unescapeJava(ts);
            //Threshold thd = JSONObject.parseObject(s1,Threshold.class);
            String value = StringUtils.EMPTY;
            if("equal".equals(thd.getType()) || "notEqual".equals(thd.getType())){
                switch (thresholdValue){
                    case "是":
                        value="true";
                        break;
                    case "否":
                        value="false";
                        break;
                    default:
                        value = thresholdValue;
                }
            }else{
                value = thresholdValue;
            }
            JSONObject json = JSONObject.parseObject(s1);
            if(StringUtils.isNotEmpty(thd.getEq())){
                json.put("eq",value);
            }
            if(StringUtils.isNotEmpty(thd.getLt())){
                json.put("lt",value);
            }
            if(StringUtils.isNotEmpty(thd.getGt())){
                json.put("gt",value);
            }
            if(StringUtils.isNotEmpty(thd.getDescribe())){
                json.put("describe",thresholdValue);
            }
           String jsonString= JSONObject.toJSONString(json, SerializerFeature.WriteMapNullValue);
            setVarThreshold(jsonString);
        }
    }

    private Threshold parse() {
        Threshold thd = null;
        String ts = getVarThreshold();
        try {
            thd = JSON.parseObject(ts, Threshold.class);
        } catch (Exception e) {
            thd = null;
        }
        return thd;
    }

    /**
     *显示时间范围
     * @return
     */

    public String  getTimeframe() {
        if(getVarPeriod() == null){
            return  "";
        }
        String timef="";
        if(getVarPeriod()<0){
            timef="无限制";
        }else{
            if(StringUtils.equals("d",getPeriodUnit())){
                timef="近"+getVarPeriod()+"天";
            }else{
                timef="近"+getVarPeriod()+"个月";
            }

        }
        return timef;
    }



}
