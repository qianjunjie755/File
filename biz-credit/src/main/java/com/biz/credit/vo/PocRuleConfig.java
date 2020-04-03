package com.biz.credit.vo;

import com.biz.credit.domain.DNodeThreshold;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PocRuleConfig {



    private List<PocRuleVO> nodeRuleList;
}
