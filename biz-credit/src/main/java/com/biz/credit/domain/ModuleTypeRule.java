package com.biz.credit.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class ModuleTypeRule implements Serializable {
    private Integer moduleTypeId;
    private Integer ruleId;

    public ModuleTypeRule(Integer moduleTypeId,Integer ruleId){
        this.moduleTypeId = moduleTypeId;this.ruleId=ruleId;
    }
}
