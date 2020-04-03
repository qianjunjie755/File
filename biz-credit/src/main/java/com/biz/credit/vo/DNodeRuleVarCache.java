package com.biz.credit.vo;

import lombok.Data;

import java.util.List;

@Data
public class DNodeRuleVarCache {
    List<DNodeRuleVarRefVO> allSrcRefVarList;
    List<DNodeRuleVarRefVO> allInstanceRefVarList;
    List<DNodeRuleVarVO> allSrcRuleVarList;
    List<DNodeRuleVarVO> allInstanceRuleVarList;
}
