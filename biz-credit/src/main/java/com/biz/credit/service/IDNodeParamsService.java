package com.biz.credit.service;

import com.biz.credit.vo.DFlowVO;
import com.biz.credit.vo.DTaskParamVO;
import com.biz.credit.vo.DTaskVO;

import java.util.List;

public interface IDNodeParamsService {
    DTaskVO findDTaskVOByDFlowVO(DFlowVO dFlowVO);
    void findDTaskParamVOList(DFlowVO dFlowVO, List<DTaskParamVO> paramVOList, List<DTaskParamVO> relatedParamVOList);
}
