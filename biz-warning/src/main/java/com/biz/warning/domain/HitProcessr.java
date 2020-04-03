package com.biz.warning.domain;

import lombok.Data;


@Data
public class HitProcessr {
    private String processrName;
    private Integer hitCount;
    private String variableCode;
    private String ruleId;
    private String ruleSetId;
}
