package com.biz.credit.service.impl;

import com.biz.credit.dao.DFlowPropsDAO;
import com.biz.credit.service.IDFlowPropsService;
import com.biz.credit.vo.DFlowBizVO;
import com.biz.credit.vo.DFlowLinkVO;
import com.biz.credit.vo.DFlowPlatformVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DFlowPropsServiceImpl implements IDFlowPropsService {

    @Autowired
    private DFlowPropsDAO dFlowPropsDAO;

    @Override
    public List<DFlowPlatformVO> findPlatformList(Integer platformId) {
        return dFlowPropsDAO.findPlatformList(platformId);
    }

    @Override
    public List<DFlowBizVO> findBizList(Integer platformId, Integer bizId) {
        return dFlowPropsDAO.findBizList(platformId,bizId);
    }

    @Override
    public List<DFlowLinkVO> findLinkList(Integer bizId, Integer linkId) {
        return dFlowPropsDAO.findLinkList(bizId,linkId);
    }
}
