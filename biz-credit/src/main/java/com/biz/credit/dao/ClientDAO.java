package com.biz.credit.dao;

import com.biz.credit.domain.DNodeModel;
import com.biz.credit.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientDAO {
    int addDFlow(@Param("dFlowVO") DFlowVO dFlowVO);
    int addDNode(@Param("dNodeVO") DNodeVO dNodeVO);
    int addDNodeModel(@Param("dNodeModel") DNodeModel dNodeModel);
    int addDNodeApiVOList(List<DNodeApiVO> dNodeApiVOList);
    int addDNodeRuleList(List<DNodeRuleVO> dNodeRuleVOList);
    int addDNodeRuleVarList(List<DNodeRuleVarVO> dNodeRuleVarVOList);
    int addDNodeParamsVOList(List<DNodeParamsVO> dNodeParamsVOList);
    List<DNodeParamsVO> queryDNodeParamsVOListByNodeIdList(List<Long> nodeIdList);
    DFlowVO queryFlowVOByFlowId(@Param("flowId") Long flowId);
    List<DNodeVO> queryListByFlowId(@Param("flowId") Long flowId);
    DNodeModel queryNodeModel(@Param("nodeId") Long nodeId);
    List<DNodeRuleVO> queryDNodeRuleVOListByModelId(@Param("modelId") Long modelId);
    List<DNodeRuleVarVO> queryDNodeRuleVarVOListByRuleIdList(List<Long> ruleIdList);
}
