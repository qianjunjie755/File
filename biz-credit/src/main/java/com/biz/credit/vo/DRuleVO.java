package com.biz.credit.vo;

import com.biz.credit.domain.DRule;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Setter
@Getter
public class DRuleVO extends DRule{

    private String keyword;

    private Integer instance;

    private String apiCode;

    private Map<String,List<DRuleVO>> ruleVersionListCodeMap;

    private List<DRuleVarVO> dRuleVarVOList;
}
