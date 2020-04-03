package com.biz.credit.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.Variable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class VariableVO extends Variable implements Serializable {
    //private Long variableId;
    private Threshold thd;

    private String timeFram;


    private String variable_name;

    private String variableName;
    private Long periodCode;
    private String periodName;
    private String frequencyName;
    private String description;
    private String thresholdValue;
    private String judge;
    /**
     * 判断符号
     * @return
     */
    public String getJudge() {
        thd = parse();
        return thd == null ? null : thd.getJudge();
    }

    private Threshold parse() {
        if (thd == null) {
            String ts = super.getThreshold();
            try {
                thd = JSON.parseObject(ts, Threshold.class);
            } catch (Exception e) {
                thd = null;
            }
        }
        return thd;
    }

    public String getThresholdValue(){
        thd = parse();
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

    public void setThresholdValue(String thresholdValue) {
        if(StringUtils.isNotEmpty(thresholdValue)){
            String value = StringUtils.EMPTY;
            thd = parse();
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
            JSONObject json = JSONObject.parseObject(super.getThreshold());
            if(StringUtils.isNotEmpty(thd.getEq())){
                json.put("eq",value);
            }
            if(StringUtils.isNotEmpty(thd.getLt())){
                json.put("lt",value);
            }
            if(StringUtils.isNotEmpty(thd.getGt())){
                json.put("gt",value);
            }
            super.setThreshold(json.toJSONString());
            thd = null;
        }
    }

    /**
     *阈值转json
     * @return
     */
    public String thresholdToJson() {
        return thd == null? null : JSON.toJSONString(thd);
    }

    /**
     *显示时间范围
     * @return
     */

    public String  getTimeframe() {
        if(super.getPeriod() == null){
            return  "";
        }
        String timef="";
        if(super.getPeriod()<0){
            timef="无";
        }else{
            if(StringUtils.equals("d",super.getPeriodUnit())){
                timef="近"+super.getPeriod()+"天";
            }else{
                timef="近"+super.getPeriod()+"个月";
            }

        }
        return timef;
    }

    /**
     * 显示阈值
     * @return
     */
    /*public String getThresholdValue() {
        thd = parse();
        if(thd!=null){
            if(thd.getLt()!=null){
                return  thd.getLt();
            }
            if(thd.getEq()!=null){
                return  thd.getLt();
            }
            if(thd.getGt()!=null){
                return  thd.getLt();
            }
        }
        return  null ;
    }*/

    /**
     * 取出时间数字
     * @return
     */
    public String timeFramToJson(){
        if(timeFram==null){
            return null;
        }
        String regEx="[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(timeFram);
        return m.replaceAll("").trim();
    }

/*
    public String getfrequencyList() {
        if (frequencyList == null) {
            frequencyList=new ArrayList();
            String ts = super.getFrequencyCode().toString();
            Frequency f1=new Frequency();
            f1.setId("1");
            f1.setName("每天");
            f1.setSelected("0");
            Frequency f2=new Frequency();
            f2.setId("2");
            f2.setName("每月");
            f2.setSelected("0");
            Frequency f3=new Frequency();
            f3.setId("3");
            f3.setName("每年");
            f3.setSelected("0");

            switch (ts){
                case "1":f1.setSelected("1");break;
                case "2":f2.setSelected("1");break;
                case "3":f3.setSelected("1");break;
                default:break;

            }
            frequencyList.add(f1);
            frequencyList.add(f2);
            frequencyList.add(f3);
        }
        return frequencyList==null?null:JSON.toJSONString(frequencyList);
    }


    public Long setFrequency(String json){
        if (json != null) {

            try {
                frequencyList = JSON.parseObject(json,new TypeReference<List<Frequency>>(){});
                for (Frequency f:frequencyList){
                    if(f.getSelected().equals("1")){
                        return Long.parseLong(f.getId());
                    }
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
*/

    @Override
    public String toString() {
        return "VariableVO{" +
                "thd=" + thd +
                ", timeFram='" + timeFram + '\'' +
                ", variable_name='" + variable_name + '\'' +
                ", variableName='" + variableName + '\'' +
                ", periodCode=" + periodCode +
                ", periodName='" + periodName + '\'' +
                ", frequencyName='" + frequencyName + '\'' +
                ", description='" + description + '\'' +
                ", thresholdValue='" + thresholdValue + '\'' +
                ", judge='" + judge + '\'' +
                ", variableId='" + super.getVariableId() + '\'' +
                ", variableCode='" + super.getVariableCode() + '\'' +
                ", isTemplate='" + super.getIsTemplate() + '\'' +
                '}';
    }
}
