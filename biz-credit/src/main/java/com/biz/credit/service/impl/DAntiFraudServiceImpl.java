package com.biz.credit.service.impl;

import com.biz.credit.dao.DAntiFraudDAO;
import com.biz.credit.dao.DNodeModelDAO;
import com.biz.credit.domain.DNodeModel;
import com.biz.credit.domain.DNodeThreshold;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDAntiFraudService;
import com.biz.credit.service.IDNodeThresholdService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.DAntiFraudVO;
import com.biz.credit.vo.DNodeAntiFraudConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class DAntiFraudServiceImpl implements IDAntiFraudService{

    @Autowired
    private DNodeModelDAO nodeModelDAO;

    @Autowired
    private IDNodeThresholdService nodeThresholdService;

    @Autowired
    private DAntiFraudDAO antiFraudDAO;

    @Override
    @Transactional
    public RespEntity saveAntiFraudConfig(Long nodeId,DNodeAntiFraudConfig nodeAntiFraudConfig){
        if(nodeId == null){
            return RespEntity.error().setMsg("nodeId不能为空");
        }
        if(nodeAntiFraudConfig.getAntiFraud() == null || CollectionUtils.isEmpty(nodeAntiFraudConfig.getAntiFraud()) || nodeAntiFraudConfig.getAntiFraud().get(0).getId() == null){
            return RespEntity.error().setMsg("反欺诈数据有误");
        }
        //失效
        nodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.ANTI_FRAUD_SCORE,Constants.COMMON_STATUS_INVALID);
        //新增
        DNodeModel nodeModel = new DNodeModel();
        nodeModel.setNodeId(nodeId);
        nodeModel.setModelType(Constants.ANTI_FRAUD_SCORE);
        nodeModel.setModelCode(nodeAntiFraudConfig.getAntiFraud().get(0).getId());
        nodeModel.setStatus(Constants.COMMON_STATUS_VALID);
        nodeModelDAO.insert(nodeModel);
        //保存阈值
        return nodeThresholdService.saveNodeThresholdList(nodeModel.getModelId(),nodeAntiFraudConfig.getNodeThresholdList());
    }

    @Override
    public List<DAntiFraudVO> getAntiFraudList(DAntiFraudVO antiFraudVO) {
        antiFraudVO.setModelType(Constants.ANTI_FRAUD_SCORE);
        return antiFraudDAO.queryList(antiFraudVO);
    }

    @Override
    public DNodeAntiFraudConfig getAntiFraudConfig(DAntiFraudVO antiFraudVO) {
        List<DAntiFraudVO> antiFraudList = getAntiFraudList(antiFraudVO);
        //阈值查询
        List<DNodeThreshold> nodeThresholdList = nodeThresholdService.getListByNodeIdAndType(antiFraudVO.getNodeId(),Constants.ANTI_FRAUD_SCORE);
        DNodeAntiFraudConfig antiFraudConfig = new DNodeAntiFraudConfig();
        antiFraudConfig.setAntiFraudVOList(antiFraudList);
        antiFraudConfig.setNodeThresholdList(nodeThresholdList);
        return antiFraudConfig;
    }
}
