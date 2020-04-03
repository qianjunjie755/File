package com.biz.warning.domain;


import com.biz.warning.util.SysDict;
import com.biz.warning.util.tools.ApplicationDateValidator;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.joda.time.DateTime;

import java.io.Serializable;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Entity implements Serializable {

  private Long entityId;
  private String entityName;
  private String uploadTime;
  private Long userId;
  private String apiCode;
  private Long taskId;
  private String expireTime;
  private String applicationDate;
  private String description;
  private String lastUpdateTime;
  private Integer entityStatus = SysDict.ENTITY_STATUS_VALID;
  private String createTime;
  private String companyName;
  private String legalPerson;
  private String personId;
  private String cell;
  private String creditCode;
  private String bankId;
  private String homeAddr;
  private String bizAddr;
  private String params;
  private String parentAppId;
  private String notifyJsonData;
  private String execDate;
  private Long hitCount;


  public String getApiCode() {
    return apiCode;
  }

  public void setApiCode(String apiCode) {
    this.apiCode = apiCode;
  }

  public void setEntityStatus(Integer entityStatus){
    if(null!=entityStatus)
      this.entityStatus = entityStatus;
  }

  public void setParamValue(String paramName,String value){
    int date = Integer.parseInt(DateTime.now().toString("yyyyMMdd"));
    if(SysDict.companyName.equals(paramName)){
      setCompanyName(value);
      setEntityName(value);
    }else if(SysDict.idNumber.equals(paramName)){
      setPersonId(value);
    }else if(SysDict.legalPerson.equals(paramName)){
      setLegalPerson(value);
    }else if(SysDict.cellPhone.equals(paramName)){
      setCell(value);
    }else if(SysDict.creditCode.equals(paramName)){
      setCreditCode(value);
    }else if(SysDict.bankId.equals(paramName)){
      setBankId(value);
    }else if(SysDict.ENTITY_EXPIRE_TIME_NAME.equals(paramName)){
      DateTime expiredTimeDatetime =  ApplicationDateValidator.parseFromString(value);
      int expiredTime = 0;
      if(null!=expiredTimeDatetime){
        expiredTime = Integer.parseInt(expiredTimeDatetime.toString("yyyyMMdd"));
      }
      if(expiredTime<date){
        setEntityStatus(2);
      }
      setExpireTime(value.split(" ")[0]);
    }else if(SysDict.ENTITY_APPLICATION_DATE.equals(paramName)){
      setApplicationDate(value.split(" ")[0]);
    }else if (SysDict.homeAddress.equals(paramName)){
      setHomeAddr(value);
    }else if (SysDict.bizAddress.equals(paramName)){
      setBizAddr(value);
    }
  }
}
