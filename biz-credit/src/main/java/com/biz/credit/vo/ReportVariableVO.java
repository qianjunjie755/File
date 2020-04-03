package com.biz.credit.vo;

import com.biz.credit.domain.VariableTemplate;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReportVariableVO extends VariableTemplate implements Serializable {
    private String apiCode;
    private Integer moduleTypeId;
    private String htmlTemplateName;
    private Integer weight;
    private Integer ruleId;
    private String period;
    private String periodUnit;

}
