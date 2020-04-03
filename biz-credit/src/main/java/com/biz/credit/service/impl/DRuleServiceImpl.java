package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.DNodeRuleVarRefDAO;
import com.biz.credit.dao.DRuleDAO;
import com.biz.credit.domain.DNodeThreshold;
import com.biz.credit.service.IDRuleService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DRuleServiceImpl implements IDRuleService{

    @Autowired
    private DRuleDAO dRuleDAO;
    @Autowired
    private DNodeRuleVarRefDAO nodeRuleVarRefDAO;

    @Override
    public JSONObject getRuleListByKeyword(String keyword) {
        DRuleVO dRuleVO = new DRuleVO();
        dRuleVO.setKeyword(keyword);
        dRuleVO.setRuleType(Constants.DRULE_TYPE_CPMPANY);
        List<DRuleVO> companyRuleList = dRuleDAO.queryListByKeyword(dRuleVO);
        dRuleVO.setRuleType(Constants.DRULE_TYPE_PERSON);
        List<DRuleVO> personRuleList = dRuleDAO.queryListByKeyword(dRuleVO);
        JSONObject res = new JSONObject();
        res.put("companyRuleList",companyRuleList);
        res.put("personRuleList",personRuleList);
        return res;
    }

    @Override
    public List<DRuleVO> getCompanyRuleListByKeyword(DRuleVO dRuleVO) {
        dRuleVO.setRuleType(Constants.DRULE_TYPE_CPMPANY);
        return dRuleDAO.queryListByKeyword(dRuleVO);
    }

    @Override
    public List<DRuleVO> getPersonRuleListByKeyword(DRuleVO dRuleVO) {
        dRuleVO.setRuleType(Constants.DRULE_TYPE_PERSON);
        return dRuleDAO.queryListByKeyword(dRuleVO);
    }

    @Override
    public List<DRuleVO> getSrcRuleList(DRuleVO dRuleVO) {
        return dRuleDAO.querySrcRuleList(dRuleVO);
    }

    @Override
    public List<DNodeRuleVarRefVO> queryAllSrcRefVarsByVarId(String refPId) {
        List<DNodeRuleVarRefVO> allSrcRefVarList = nodeRuleVarRefDAO.querySrcRefVars(refPId);
        return allSrcRefVarList;
    }

    @Override
    public JSONObject getPocRuleList(String s, String userId, String apiCode) {
        //规则配置
        DNodeRuleVO nodeRuleVO = new DNodeRuleVO();
        nodeRuleVO.setModelType(Constants.ENTERPRISE_RULE);
        nodeRuleVO.setApiCode(apiCode);
        nodeRuleVO.setRuleType(Constants.ENTERPRISE_RULE);
        long start =System.currentTimeMillis();
        PocRuleConfig companyNodeRuleConfig = getNodeRuleConfig(nodeRuleVO);
        log.info("companyNodeRuleConfig  cost:"+(System.currentTimeMillis()-start)+"ms");

        JSONObject res = new JSONObject();
        res.put("companyNodeRuleConfig",companyNodeRuleConfig);
        return res;
    }

    public PocRuleConfig getNodeRuleConfig(DNodeRuleVO dNodeRuleVO) {
        List<PocRuleVO> dNodeRuleVOList = getNodeRuleList(dNodeRuleVO);
        Integer modelType = dNodeRuleVO.getModelType();

        PocRuleConfig nodeRuleConfig = new PocRuleConfig();
        nodeRuleConfig.setNodeRuleList(dNodeRuleVOList);

        return nodeRuleConfig;
    }

    private List<PocRuleVO> getNodeRuleList(DNodeRuleVO dNodeRuleVO) {

          List<PocRuleVO> dNodeRuleVOList =null;
            return dNodeRuleVOList;
        }

}
