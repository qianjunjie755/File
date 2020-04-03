package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.DRuleVar;

import java.util.List;

public interface IDRuleVarService {

    DRuleVar getById(Long varId);

    JSONObject updateVarInfo(DRuleVar dDRuleVar);
    JSONObject updateVarLsit(List<DRuleVar> dDRuleVar);

    JSONObject updateRefVar(DRuleVar dDRuleVar);

    JSONObject updateRefVarList(List<DRuleVar> dDRuleVar);
}
