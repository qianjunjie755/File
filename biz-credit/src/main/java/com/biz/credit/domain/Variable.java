package com.biz.credit.domain;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Data
public class Variable implements Serializable {

    private Long variableId;
    private Long variableCode;
    private Long userId;
    private String apiCode;
    private Long isTemplate;
    private Long ruleId;
    private Long variableTypeCode;
    private Long frequencyCode;
    private String threshold;
    private Long period;
    private String apiProdCode;
    private String apiVersion;
    private String description;
    private String lastUpdateTime;
    private String createTime;
    private  Long modified;
    private String periodUnit = "m";
    private Integer weight;


    public void setPeriodUnit(String periodUnit) {
        if(StringUtils.isNotEmpty(periodUnit))
            this.periodUnit = periodUnit;
    }
}
