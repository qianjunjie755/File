package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Slf4j
public class DTreeVar implements Serializable {
    private Long varId;
    private Long treeId;
    private Integer srcVarId;
    private String varName;
    private String type="variable";
    private Integer condType;
    private String outValue;
    private Integer status;
    private String updateTime;
    private String createTime;
    private List<DTreeCond> children;
    private Integer periodId;
    private Integer varPeriod = -1;
    private String periodUnit = "m";
    private String periodCnt;
    private String apiProdCode;
    private Integer varType;

    public Integer getVarType() {
        if(StringUtils.isNotEmpty(apiProdCode)){
            if(StringUtils.startsWith(apiProdCode,"Biz")){
                varType = 1;
            }else{
                varType = 2;
            }
        }
        //log.info("apiProdCode:{},varType:{}",apiProdCode,varType);
        return varType;
    }

    public void setVarPeriod(Integer varPeriod) {
        if(null!= varPeriod)
            this.varPeriod = varPeriod;
    }

    public void setPeriodUnit(String periodUnit) {
        if(StringUtils.isNotEmpty(periodUnit))
            this.periodUnit = periodUnit;
    }

    public Integer getPeriodId() {
        /*if(StringUtils.startsWith(apiProdCode,"Biz")){
            periodId = 7;
        }*/
        return periodId;
    }

    public Integer getVarPeriod() {
        /*if(StringUtils.startsWith(apiProdCode,"Biz")){
            varPeriod = -1;
        }*/
        return varPeriod;
    }

    public String getPeriodUnit() {
        /*if(StringUtils.startsWith(apiProdCode,"Biz")){
            periodUnit = "m";
        }*/
        return periodUnit;
    }
}
