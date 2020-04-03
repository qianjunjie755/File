package com.biz.credit.service.impl;

import com.biz.credit.dao.DNodeModelDAO;
import com.biz.credit.dao.DNodeRuleDAO;
import com.biz.credit.dao.DNodeRuleVarDAO;
import com.biz.credit.dao.DNodeRuleVarRefDAO;
import com.biz.credit.domain.DNodeModel;
import com.biz.credit.domain.DNodeRuleVarRef;
import com.biz.credit.domain.DNodeThreshold;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDNodeRuleService;
import com.biz.credit.service.IDNodeThresholdService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.DNodeRuleVO;
import com.biz.credit.vo.DNodeRuleVarRefVO;
import com.biz.credit.vo.DNodeRuleVarVO;
import com.biz.credit.vo.NodeRuleConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DNodeRuleServiceImpl implements IDNodeRuleService{

    @Autowired
    private DNodeRuleDAO nodeRuleDAO;

    @Autowired
    private DNodeRuleVarDAO nodeRuleVarDAO;

    @Autowired
    private DNodeModelDAO nodeModelDAO;

    @Autowired
    private DNodeRuleVarRefDAO nodeRuleVarRefDAO;

    @Autowired
    private IDNodeThresholdService nodeThresholdService;

    @Override
    public List<DNodeRuleVO> getNodeRuleList(DNodeRuleVO dNodeRuleVO) {
        List<DNodeRuleVO> dNodeRuleVOList = nodeRuleDAO.queryNodeRuleList(dNodeRuleVO);
        List<DNodeRuleVarRefVO> allSrcRefVarList = nodeRuleVarRefDAO.queryAllSrcRefVars();
        List<DNodeRuleVarRefVO> allInstanceRefVarList =  nodeRuleVarRefDAO.queryInstanceRefVars(dNodeRuleVO);
        List<DNodeRuleVarVO> allSrcRuleVarList = nodeRuleVarDAO.queryAllSrcNodeRuleVarList();
        List<DNodeRuleVarVO> allInstanceRuleVarList = nodeRuleVarDAO.queryInstanceRuleVarList(dNodeRuleVO);
        for (DNodeRuleVO nodeRuleVO : dNodeRuleVOList) {
            List<DNodeRuleVarVO> nodeRuleVarList = new ArrayList<>();
            List<DNodeRuleVarVO> srcNodeRuleVarList = allSrcRuleVarList.stream().filter(ruleVar -> Objects.equals(nodeRuleVO.getSrcRuleId(),ruleVar.getSrcRuleId())).collect(Collectors.toList());
            if(nodeRuleVO.getRuleId() == null){
                nodeRuleVarList = srcNodeRuleVarList;
            }else{
                List<DNodeRuleVarVO> instanceNodeRuleVarList = allInstanceRuleVarList.stream().filter(ruleVar -> Objects.equals(nodeRuleVO.getSrcRuleId(),ruleVar.getSrcRuleId())).collect(Collectors.toList());
                Set<DNodeRuleVarVO> instanceNodeRuleVarSet = new HashSet<>();
                instanceNodeRuleVarSet.addAll(instanceNodeRuleVarList);
                instanceNodeRuleVarSet.addAll(srcNodeRuleVarList);
                nodeRuleVarList = new ArrayList<>(instanceNodeRuleVarSet);
            }
            nodeRuleVO.setNodeRuleVarVOList(nodeRuleVarList);
            for (DNodeRuleVarVO nodeRuleVarVO : nodeRuleVarList) {
                List<DNodeRuleVarRefVO> refVarList = new ArrayList<>();
                List<DNodeRuleVarRefVO> srcNodeRuleVarRefList = allSrcRefVarList.stream().filter(refVar -> Objects.equals(refVar.getVarPId(),nodeRuleVarVO.getVarPId())).collect(Collectors.toList());
                if(nodeRuleVarVO.getVarId() == null){
                    refVarList = srcNodeRuleVarRefList;
                }else{
                    List<DNodeRuleVarRefVO> instanceNodeRuleVarRefList = allInstanceRefVarList.stream().filter(refVar -> Objects.equals(refVar.getVarPId(),nodeRuleVarVO.getVarPId())).collect(Collectors.toList());
                    Set<DNodeRuleVarRefVO> instanceNodeRuleVarRefSet = new HashSet<>();
                    instanceNodeRuleVarRefSet.addAll(instanceNodeRuleVarRefList);
                    instanceNodeRuleVarRefSet.addAll(srcNodeRuleVarRefList);
                    refVarList = new ArrayList<>(instanceNodeRuleVarRefSet);
                }
                nodeRuleVarVO.setRefRuleVarList(refVarList);
            }
            nodeRuleVO.setNodeRuleVarVOList(nodeRuleVarList);
        }
        return dNodeRuleVOList;
    }

    @Override
    @Transactional
    public RespEntity updateNodeRuleVar(DNodeRuleVO nodeRuleVO) {
        if(nodeRuleVO.getRuleId() == null){
            return RespEntity.error().setMsg("规则id不能为空");
        }
        if(nodeRuleVO.getSrcRuleId() == null){
            return RespEntity.error().setMsg("源规则id不能为空");
        }
        List<DNodeRuleVarVO> nodeRuleVarVOList = nodeRuleVO.getNodeRuleVarVOList();
        //插入变量数据
        List<DNodeRuleVarRef> refVarList = new ArrayList<>();
        if(nodeRuleVarVOList != null){
            for (DNodeRuleVarVO dNodeRuleVarVO : nodeRuleVarVOList) {
                //dNodeRuleVarVO.setStatus(Constants.COMMON_STATUS_VALID);
                dNodeRuleVarVO.setStatus(dNodeRuleVarVO.getSelected());
            }
            if(!CollectionUtils.isEmpty(nodeRuleVarVOList)){
                nodeRuleVarDAO.updateList(nodeRuleVarVOList);
            }
            for (DNodeRuleVarVO nodeRuleVarVO : nodeRuleVarVOList) {
                if(!CollectionUtils.isEmpty(nodeRuleVarVO.getRefRuleVarList())){
                    for (DNodeRuleVarRefVO refVar : nodeRuleVarVO.getRefRuleVarList()) {
                        refVar.setNodeVarId(nodeRuleVarVO.getVarId());
                        refVar.setStatus(Constants.COMMON_STATUS_VALID);
                        refVarList.add(refVar);
                    }
                }
            }
            if(!CollectionUtils.isEmpty(refVarList)){
                nodeRuleVarRefDAO.updateList(refVarList);
            }
        }
        return RespEntity.success();
    }

    @Transactional
    @Override
    public RespEntity saveCompanyNodeRule(Long nodeId, NodeRuleConfig nodeRuleConfig, String userId, boolean isNew) {
        return saveNodeRule(nodeId,nodeRuleConfig,userId,isNew,Constants.ENTERPRISE_RULE);
    }

    @Transactional
    @Override
    public RespEntity savePersonalNodeRule(Long nodeId,NodeRuleConfig nodeRuleConfig,String userId,boolean isNew) {
        return saveNodeRule(nodeId,nodeRuleConfig,userId,isNew,Constants.NATURAL_PERSON_RULE);
    }

    @Override
    public NodeRuleConfig getNodeRuleConfig(DNodeRuleVO dNodeRuleVO) {
        List<DNodeRuleVO> dNodeRuleVOList = getNodeRuleList(dNodeRuleVO);
        Integer modelType = dNodeRuleVO.getModelType();
        //阈值查询
        List<DNodeThreshold> nodeThresholdList = nodeThresholdService.getListByNodeIdAndType(dNodeRuleVO.getNodeId(),modelType);
        NodeRuleConfig nodeRuleConfig = new NodeRuleConfig();
        nodeRuleConfig.setNodeRuleList(dNodeRuleVOList);
        nodeRuleConfig.setNodeThresholdList(nodeThresholdList);
        return nodeRuleConfig;
    }

    @Transactional
    public RespEntity saveNodeRule(Long nodeId,NodeRuleConfig nodeRuleConfig,String userId,boolean isNew,int modelType) {
        List<DNodeRuleVO> nodeRuleVOList = nodeRuleConfig.getNodeRuleList();
        RespEntity checkNodeRuleResp = checkNodeRuleParams(nodeId,nodeRuleConfig);
        if(!checkNodeRuleResp.isSuccess()){
            return checkNodeRuleResp;
        }
        DNodeModel nodeModel = new DNodeModel();
        nodeModel.setModelType(modelType);
        nodeModel.setNodeId(nodeId);
        if(isNew){
            //新建model
            nodeModel.setStatus(Constants.COMMON_STATUS_VALID);
            nodeModelDAO.insert(nodeModel);
        }else{
            DNodeModel existNodeModel = nodeModelDAO.queryNodeModel(nodeModel);
            if(existNodeModel == null){
                //nodeModel不存在
                nodeModel.setStatus(Constants.COMMON_STATUS_VALID);
                nodeModelDAO.insert(nodeModel);
            }else{
                nodeModel = existNodeModel;
            }
            //失效
            nodeRuleDAO.updateStatusByModelId(nodeModel.getModelId(), Constants.COMMON_STATUS_INVALID);
        }
        //保存数据
        for (DNodeRuleVO dNodeRuleVO : nodeRuleVOList) {
            List<DNodeRuleVarVO> insertNodeRuleVarList = new ArrayList<>();
            List<DNodeRuleVarVO> updateNodeRuleVarList = new ArrayList<>();
            List<DNodeRuleVarVO> nodeRuleVarList = new ArrayList<>();
            List<DNodeRuleVarRef> insertRefVarList = new ArrayList<>();
            List<DNodeRuleVarRef> updateRefVarList = new ArrayList<>();
            if (dNodeRuleVO.getChoose()) {
                dNodeRuleVO.buildVarThreadValue();
                dNodeRuleVO.setModelId(nodeModel.getModelId());
                dNodeRuleVO.setStatus(Constants.COMMON_STATUS_VALID);
                dNodeRuleVO.setCalcLogic(Constants.COMMON_STATUS_VALID);
                nodeRuleDAO.insertDNodeRule(dNodeRuleVO);
                if (!CollectionUtils.isEmpty(dNodeRuleVO.getNodeRuleVarVOList())) {
                    for (DNodeRuleVarVO nodeRuleVarVO : dNodeRuleVO.getNodeRuleVarVOList()) {
                        nodeRuleVarVO.setRuleId(dNodeRuleVO.getRuleId());
                        nodeRuleVarVO.setStatus(Constants.COMMON_STATUS_VALID);
                        nodeRuleVarList.add(nodeRuleVarVO);
                        if(nodeRuleVarVO.getVarId() == null){
                            insertNodeRuleVarList.add(nodeRuleVarVO);
                        }else{
                            updateNodeRuleVarList.add(nodeRuleVarVO);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(insertNodeRuleVarList)) {
                nodeRuleVarDAO.insertList(insertNodeRuleVarList);
            }
            if (!CollectionUtils.isEmpty(updateNodeRuleVarList)) {
                nodeRuleVarDAO.updateList(updateNodeRuleVarList);
            }
            for (DNodeRuleVarVO dNodeRuleVar : nodeRuleVarList) {
                if (!CollectionUtils.isEmpty(dNodeRuleVar.getRefRuleVarList())) {
                    for (DNodeRuleVarRefVO refVar : dNodeRuleVar.getRefRuleVarList()) {
                        refVar.setStatus(Constants.COMMON_STATUS_VALID);
                        refVar.setNodeVarId(dNodeRuleVar.getVarId());
                        if(refVar.getVarId() == null){
                            insertRefVarList.add(refVar);
                        }else{
                            updateRefVarList.add(refVar);
                        }
                    }
                }
            }
            if (!CollectionUtils.isEmpty(insertRefVarList)) {
                nodeRuleVarRefDAO.insertList(insertRefVarList);
            }
            if (!CollectionUtils.isEmpty(updateRefVarList)) {
                nodeRuleVarRefDAO.updateList(updateRefVarList);
            }
        }
        //保存阈值
        return nodeThresholdService.saveNodeThresholdList(nodeModel.getModelId(),nodeRuleConfig.getNodeThresholdList());
    }

    private RespEntity checkNodeRuleParams (Long nodeId, NodeRuleConfig nodeRuleConfig){
        if (nodeId == null) {
            return RespEntity.error().setMsg("nodeId不能为空");
        }
        List<DNodeRuleVO> nodeRuleVOList = nodeRuleConfig.getNodeRuleList();
        if (!CollectionUtils.isEmpty(nodeRuleVOList)) {
            for (DNodeRuleVO dNodeRuleVO : nodeRuleVOList) {
                if (dNodeRuleVO.getSrcRuleId() == null) {
                    return RespEntity.error().setMsg("源规则id不能为空");
                }
                if (dNodeRuleVO.getRuleWeight() == null) {
                    return RespEntity.error().setMsg("规则权重不能为空");
                }
                if (dNodeRuleVO.getChoose() == null) {
                    return RespEntity.error().setMsg("是否选择字段不能为空");
                }
                if (dNodeRuleVO.getInstance() == null) {
                    return RespEntity.error().setMsg("instance字段不能为空");
                }
                if (dNodeRuleVO.getInstance() != 0 && dNodeRuleVO.getInstance() != 1) {
                    return RespEntity.error().setMsg("instance字段有误");
                }
            }
        }
        return RespEntity.success();
    }

}

