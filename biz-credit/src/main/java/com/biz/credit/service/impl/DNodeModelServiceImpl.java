package com.biz.credit.service.impl;

import com.biz.credit.dao.DNodeModelDAO;
import com.biz.credit.service.IDNodeModelService;
import com.biz.credit.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DNodeModelServiceImpl implements IDNodeModelService {

    @Autowired
    private DNodeModelDAO nodeModelDAO;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invalidByNodeId(Long nodeId) {
        //nodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.ENTERPRISE_RULE, Constants.COMMON_STATUS_INVALID);
        //nodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.NATURAL_PERSON_RULE,Constants.COMMON_STATUS_INVALID);
        nodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.ANTI_FRAUD_SCORE,Constants.COMMON_STATUS_INVALID);
        nodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.CREDIT_SCORE_MODEL,Constants.COMMON_STATUS_INVALID);
        nodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.SCORE_CARD,Constants.COMMON_STATUS_INVALID);
        nodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.DECISION_TABLE,Constants.COMMON_STATUS_INVALID);
        nodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.DECISION_TREE,Constants.COMMON_STATUS_INVALID);
    }
}
