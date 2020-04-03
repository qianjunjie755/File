package com.biz.credit.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleVariableVO implements Serializable {
    private Integer ruleIdInst;
    private Integer ruleIdTemp;
}
