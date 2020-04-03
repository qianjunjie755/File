package com.biz.credit.domain;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class DTableVar implements Serializable {
    private Long varId;
    private Long tableId;
    private Long srcVarId;
    private String varName;
    private Integer varOrder;
    private Integer condType;
    private Integer status;
    private String updateTime;
    private String createTime;
    private List<DTableCond> tableCondList;
    private Integer ruleType;
    private Integer varPeriod;
    private String periodUnit;
}
