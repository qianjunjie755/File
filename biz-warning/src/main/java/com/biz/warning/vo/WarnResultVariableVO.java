package com.biz.warning.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.WarnResultVariable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class WarnResultVariableVO extends WarnResultVariable implements Serializable {

  private String ruleName;
  private String ruleSetName;
  private String strategyName;
  private String variableName;
  private String thresholdValue;
  private String triggerThresholdCH;
  private String taskName;
  private String entityName;
  private String periodCH;
  private String judge;
  private String startDate;
  private String endDate;
  private Integer variableSourceId;
  private List<Integer> variableSourceIds;
  private String variableSourceName;
  private Integer userId;
  private String apiCode;
  private String userIds;
  private List<Integer> userIdList;
  private List<Integer> taskIdList;
  private String taskIdListStr;
  private Integer entityStatus;
  private String warnStatus;

  private List<Integer> hitTypeList;
  private String hitTypeStr;


  public String getRuleSetName(){
    if(null!=ruleSetName){
      ruleSetName = ruleSetName.trim();
    }
    return ruleSetName;
  }

  public String getWarnStatus() {
    if(null!=entityStatus&&1==entityStatus){
      warnStatus="当前监控";
    }else{
      warnStatus="历史监控";
    }
    return warnStatus;
  }

  public String getRuleName(){
    if(null!=ruleName){
      ruleName = ruleName.trim();
    }
    return ruleName;
  }

  public String getVariableName(){
    if(null!=variableName){
      variableName = variableName.trim();
    }
    return variableName;
  }

  public String getTriggerThresholdCH() {
    String value = getTriggerThreshold();
    if (null != value && StringUtils.isNotEmpty(getThreshold())) {
      try {
        JSONObject thresholdJson = JSONObject.parseObject(getThreshold());
        String type = thresholdJson.getString("type");
        if (value.indexOf('.') > 0) {
          triggerThresholdCH = value.replaceAll("0+?$", "").replaceAll("[.]$", "");
        } else {
          triggerThresholdCH = value;
        }
        if (StringUtils.equals("equal", type)) {
          if (StringUtils.equals("true", triggerThresholdCH)) {
            triggerThresholdCH = "是";
          } else if (StringUtils.equals("false", triggerThresholdCH)) {
            triggerThresholdCH = "否";
          }
        }
        if (StringUtils.isNotEmpty(JSONObject.parseObject(getThreshold()).getString("unit"))) {
          triggerThresholdCH = triggerThresholdCH.concat(JSONObject.parseObject(getThreshold()).getString("unit"));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return triggerThresholdCH;
  }

  public String getThresholdValue() {
    thresholdValue = StringUtils.EMPTY;
    if(StringUtils.isNotEmpty(getThreshold())){
      try{
        JSONObject thresholdJson = JSONObject.parseObject(getThreshold());
        String type = thresholdJson.getString("type");
        if(StringUtils.equals("equal",type)){
          if(StringUtils.equals("true",thresholdJson.getString("eq"))){
            thresholdValue = "是";
          }else if(StringUtils.equals("false",thresholdJson.getString("eq"))){
            thresholdValue = "否";
          }else {
            thresholdValue = thresholdJson.getString("eq");
          }
        }else if(StringUtils.equals("compare",type)){
          thresholdValue = StringUtils.isNotEmpty(thresholdJson.getString("eq"))?thresholdJson.getString("eq"):(StringUtils.isNotEmpty(thresholdJson.getString("gt"))?thresholdJson.getString("gt"):thresholdJson.getString("lt"));
        }
        if(StringUtils.isNotEmpty(thresholdJson.getString("unit"))){
          thresholdValue = thresholdValue.concat(thresholdJson.getString("unit"));
        }
      }catch (Exception e){
        e.printStackTrace();
      }
    }
    return thresholdValue;
  }


  public String getPeriodCH(){
    if(null!=getPeriod()){
      if(StringUtils.equals("-1",getPeriod().toString())){
        periodCH = "无时间限制";
      }else{
        periodCH = "近".concat(getPeriod().toString()).concat(StringUtils.equals("d",getPeriodUnit())?"天":(StringUtils.equals("m",getPeriodUnit())?"个月":"年"));
      }

    }
    return periodCH;
  }

  public String getJudge(){
    judge = StringUtils.EMPTY;
    if(StringUtils.isNotEmpty(getThreshold())){
      try{
        judge = JSON.parseObject(getThreshold()).getString("judge");
      }catch (Exception e){
        e.printStackTrace();
      }
    }
    return judge;
  }

}
