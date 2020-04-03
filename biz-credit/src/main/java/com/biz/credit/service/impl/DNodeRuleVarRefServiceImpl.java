package com.biz.credit.service.impl;

import com.biz.credit.dao.DNodeRuleVarDAO;
import com.biz.credit.dao.DNodeRuleVarRefDAO;
import com.biz.credit.domain.DNodeRuleVarRef;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDNodeRuleVarRefService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.DNodeRuleVarRefVO;
import com.biz.credit.vo.DNodeRuleVarVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class DNodeRuleVarRefServiceImpl implements IDNodeRuleVarRefService {

    @Autowired
    private DNodeRuleVarDAO nodeRuleVarDAO;

    @Autowired
    private DNodeRuleVarRefDAO nodeRuleVarRefDAO;

    @Override
    public RespEntity updateNodeRuleVarRef(DNodeRuleVarVO nodeRuleVarVO) {
        nodeRuleVarRefDAO.updateStatusByNodeRuleVarId(nodeRuleVarVO.getVarId(), Constants.COMMON_STATUS_INVALID);
        List<DNodeRuleVarRefVO> refRuleVarList = nodeRuleVarVO.getRefRuleVarList();
        List<DNodeRuleVarRef> refVarToBeInsert = new ArrayList<>();
        for (DNodeRuleVarRefVO refVar : refRuleVarList) {
            DNodeRuleVarRef nodeRuleVarRef = new DNodeRuleVarRef();
            nodeRuleVarRef.setSrcVarId(refVar.getVarId());
            nodeRuleVarRef.setVarThreshold(refVar.getVarThreshold());
            nodeRuleVarRef.setVarPeriod(refVar.getVarPeriod());
            nodeRuleVarRef.setPeriodUnit(refVar.getPeriodUnit());
            nodeRuleVarRef.setNodeVarId(nodeRuleVarVO.getVarId());
            nodeRuleVarRef.setStatus(Constants.COMMON_STATUS_VALID);
            refVarToBeInsert.add(nodeRuleVarRef);
        }
        if(!CollectionUtils.isEmpty(refVarToBeInsert)){
            nodeRuleVarRefDAO.insertList(refVarToBeInsert);
        }
        return RespEntity.success();
    }

    @Override
    public List<DNodeRuleVarRefVO> getNodeRuleVarRef(DNodeRuleVarVO nodeRuleVarVO) {
        return nodeRuleVarDAO.queryRefVars(nodeRuleVarVO.getVarId(), nodeRuleVarVO.getSrcVarId());
    }
}
