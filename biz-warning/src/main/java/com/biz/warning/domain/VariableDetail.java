package com.biz.warning.domain;

import lombok.Data;

@Data
public class VariableDetail {
    private Long warnId;
    private Long variableId;
    private Long entityId;
    private String hitDate;
    private String varDetail;
    private String companyName;
}
