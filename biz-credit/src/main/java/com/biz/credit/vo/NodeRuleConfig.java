package com.biz.credit.vo;

import com.biz.credit.domain.DNodeThreshold;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NodeRuleConfig {

    private List<DNodeThreshold> nodeThresholdList;

    private List<DNodeRuleVO> nodeRuleList;
}
