package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.vo.DNodeRuleVarRefVO;
import com.biz.credit.vo.DRuleVO;

import java.util.List;

public interface IDRuleService {
    JSONObject getRuleListByKeyword(String keyword);

    List<DRuleVO> getCompanyRuleListByKeyword(DRuleVO dRuleVO);

    List<DRuleVO> getPersonRuleListByKeyword(DRuleVO dRuleVO);

    List<DRuleVO> getSrcRuleList(DRuleVO dRuleVO);

    List<DNodeRuleVarRefVO> queryAllSrcRefVarsByVarId(String varId);

    JSONObject getPocRuleList(String s, String userId, String apiCode);
}
