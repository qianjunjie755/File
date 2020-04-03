package com.biz.credit.domain;

import lombok.Data;

@Data
public class DNodeRuleVarRef extends DNodeRuleVar {

    private Long nodeVarId;

    private String prodCode;
    private String variableName;
    private Integer variableTypeCode;
    private String version;
    private String description;

}
