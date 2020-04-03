package com.biz.credit.vo;

import com.biz.credit.domain.DRuleVar;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DRuleVarVO extends DRuleVar {


    private String prodCode;
    private String variableName;
    private Integer variableTypeCode;
    private String version;
    private String dataType;
    private String defaultThreshold;
    private String dataSource;
    private String apiProdCode;
    private String apiVersion;
    private String description;
    private String pocSrcVarId;
}
