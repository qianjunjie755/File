package com.biz.credit.service.impl;

import com.biz.credit.dao.DNodeThresholdDAO;
import com.biz.credit.domain.DNodeThreshold;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDNodeThresholdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DNodeThresholdServiceImpl implements IDNodeThresholdService{

    @Autowired
    private DNodeThresholdDAO nodeThresholdDAO;

    @Override
    @Transactional
    public RespEntity saveNodeThresholdList(Long modelId, List<DNodeThreshold> nodeThresholdList) {
        for (DNodeThreshold dNodeThreshold : nodeThresholdList) {
            dNodeThreshold.setModelId(modelId);
        }
        nodeThresholdDAO.deleteByModelId(modelId);
        nodeThresholdDAO.insertList(nodeThresholdList);
        return RespEntity.success();
    }

    @Override
    public List<DNodeThreshold> getListByNodeIdAndType(Long nodeId,int modelType) {
        if(nodeId == null){
            return new ArrayList<>();
        }
        return nodeThresholdDAO.queryListByNodeIdAndType(nodeId,modelType);
    }
}
