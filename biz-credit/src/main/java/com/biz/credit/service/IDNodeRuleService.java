package com.biz.credit.service;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.DNodeRuleVO;
import com.biz.credit.vo.NodeRuleConfig;

import java.util.List;

public interface IDNodeRuleService {

    List<DNodeRuleVO> getNodeRuleList(DNodeRuleVO dNodeRuleVO);

    RespEntity updateNodeRuleVar(DNodeRuleVO nodeRuleVO);

    RespEntity saveCompanyNodeRule(Long nodeId, NodeRuleConfig nodeRuleConfig, String userId, boolean isNew);

    RespEntity savePersonalNodeRule(Long nodeId, NodeRuleConfig nodeRuleConfig, String userId, boolean isNew);

    NodeRuleConfig getNodeRuleConfig(DNodeRuleVO dNodeRuleVO);
}
