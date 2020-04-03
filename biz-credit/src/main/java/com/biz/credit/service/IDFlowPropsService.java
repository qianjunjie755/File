package com.biz.credit.service;

import com.biz.credit.vo.DFlowBizVO;
import com.biz.credit.vo.DFlowLinkVO;
import com.biz.credit.vo.DFlowPlatformVO;

import java.util.List;

public interface IDFlowPropsService {
    List<DFlowPlatformVO> findPlatformList(Integer platformId);
    List<DFlowBizVO> findBizList(Integer platformId, Integer bizId);
    List<DFlowLinkVO> findLinkList(Integer bizId, Integer linkId);
}
