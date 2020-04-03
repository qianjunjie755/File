package com.biz.credit.service;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.DNodeRuleVarRefVO;
import com.biz.credit.vo.DNodeRuleVarVO;

import java.util.List;

public interface IDNodeRuleVarRefService {

    RespEntity updateNodeRuleVarRef(DNodeRuleVarVO nodeRuleVarVO);

    List<DNodeRuleVarRefVO> getNodeRuleVarRef(DNodeRuleVarVO nodeRuleVarVO);
}
