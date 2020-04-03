package com.biz.credit.service;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.DFlowVO;

import java.util.List;

public interface IDFlowService {
    List<DFlowVO> findDFlowVOList(DFlowVO dFlowVO);

    RespEntity saveFlow(DFlowVO flowVO);

    RespEntity publishFlow(DFlowVO flowVO);
    RespEntity cancelFlow(DFlowVO flowVO);

    List<DFlowVO> getFlowList(DFlowVO flowVO);

    DFlowVO getFlowDetailById(Long flowId, String apiCode, String userId);



}
