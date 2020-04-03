package com.biz.credit.domain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.vo.Threshold;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@NoArgsConstructor
public class DNodeRuleVar implements Serializable {

  private Long varId;
  private Long srcVarId;
  private Integer varPeriod;
  private String periodUnit;
  private String varThreshold;
  private Integer varWeight;
  private Long ruleId;
  private Integer status;
  private String updateTime;
  private String createTime;

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
