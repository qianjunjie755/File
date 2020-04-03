package com.biz.warning.vo;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.Entity;
import com.biz.warning.util.SysDict;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class EntityVO extends Entity {
    private Long limitId;
    private Long strategyId;
    private JSONObject jsonData;
    private Integer taskStatus;
    private String startDate;
    private String endDate;
    private String columnHead;
    private String realName;
    private String monitorStatus;

    private String registNo;
    private String registCapital;
    private String companyStatus;
    private String createTime;
    private String legalPersonName;
    private String registAddress;
    private String bizScope;

    public String getMonitorStatus() {
        if(SysDict.ENTITY_STATUS_VALID.equals(getEntityStatus())){
            monitorStatus = SysDict.MONITOR_STATUS_VALID;
        }else if(SysDict.ENTITY_STATUS_EXPIRED.equals(getEntityStatus())){
            monitorStatus = SysDict.MONITOR_STATUS_EXPIRE;
        }else if(SysDict.ENTITY_STATUS_DELETED.equals(getEntityStatus())){
            monitorStatus = SysDict.MONITOR_STATUS_INVALID;
        }
        return monitorStatus;
    }

    public void generateJsonData(){
            jsonData = new JSONObject();
            if(StringUtils.isNotEmpty(getCompanyName())){
                SysDict.EntityNameMap.get("companyName").forEach(name->{
                    jsonData.put(name,getCompanyName());
                });
            }
            if(StringUtils.isNotEmpty(getLegalPerson())){
                SysDict.EntityNameMap.get("legalPerson").forEach(name->{
                    jsonData.put(name,getLegalPerson());
                });
            }
            if(StringUtils.isNotEmpty(getPersonId())){
                SysDict.EntityNameMap.get("personId").forEach(name->{
                    jsonData.put(name,getPersonId());
                });
            }
            if(StringUtils.isNotEmpty(getCell())){
                SysDict.EntityNameMap.get("cell").forEach(name->{
                    jsonData.put(name,getCell());
                });
            }
            if(StringUtils.isNotEmpty(getBankId())){
                SysDict.EntityNameMap.get("bankId").forEach(name->{
                    jsonData.put(name,getBankId());
                });
            }
            if(StringUtils.isNotEmpty(getCreditCode())){
                SysDict.EntityNameMap.get("creditCode").forEach(name->{
                    jsonData.put(name,getCreditCode());
                });
            }
    }
}
