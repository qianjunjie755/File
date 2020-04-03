package com.biz.warning.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.io.Serializable;

@Data
public class VarReqVO implements Serializable {
    private Long ruleId;
    private Long strategyId;
    private Long ruleSetId;
    private Long variableId;
    private Long variableCode;
    private String var;
    private JSONObject platForm;
    private String queryUrl;
    private String resultUrl;
    private String queryParam;
    private String resultParam;
    private String apiCode;
    private String searchKey;
    private Double version;
    private String keyNo;
    private String companyName;
    private String legalPerson;
    private String personId;
    private String bankId;
    private String cell;
    private String creditCode;
    private Long taskId;
    private Long limitTaskId;
    private String threshold;
    private String time;
    private Integer intervalCode;

    private Integer ruleSetCalcLogic;
    private Integer ruleCalcLogic;
    private Integer varCalcLogic;
    private Long srcRuleSetId;
    private Long srcRuleId;
}
