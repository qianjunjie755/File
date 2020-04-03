package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.DRuleVarDAO;
import com.biz.credit.domain.DRuleVar;
import com.biz.credit.service.IDRuleVarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DRuleVarServiceImpl implements IDRuleVarService {

    @Autowired
    private DRuleVarDAO dRuleVarDAO;

    @Override
    public DRuleVar getById(Long varId) {
        return dRuleVarDAO.queryById(varId);
    }

    @Override
    public JSONObject updateVarInfo(DRuleVar dDRuleVar) {

        dDRuleVar.buildPocThresholdValue();
        dRuleVarDAO.updateRuleVar(dDRuleVar);
        return null;
    }

    @Override
    public JSONObject updateVarLsit(List<DRuleVar> dDRuleVar) {
        dDRuleVar.stream().forEach(DRuleVar->DRuleVar.buildPocThresholdValue());
        dRuleVarDAO.updateRuleVarList(dDRuleVar);
        return null;
    }

    @Override
    public JSONObject updateRefVar(DRuleVar dDRuleVar) {
        dDRuleVar.buildPocThresholdValue();
        dRuleVarDAO.updateRefVar(dDRuleVar);
        return null;
    }

    @Override
    public JSONObject updateRefVarList(List<DRuleVar> dDRuleVar) {
        dDRuleVar.stream().forEach(DRuleVar->DRuleVar.buildPocThresholdValue());
        dRuleVarDAO.updateRefVarList(dDRuleVar);
        return null;
    }

    public static void main(String[] args) {

        String str="{type:\"equal\",\"lt\":null,\"gt\":null,\"eq\":\"false\",\"describe\":\"<1年\",\"interval\":\"无\"}";
        System.out.println(str);
        /*DRuleVar dDRuleVar =new DRuleVar();
        dDRuleVar.setThresholdValue("1");
        dDRuleVar.setVarThreshold("{type:\"equal\",\"lt\":null,\"gt\":null,\"eq\":\"false\",\"describe\":\"<1年\",\"interval\":\"无\"}");
        dDRuleVar.buildPocThresholdValue();
        System.out.println(JSONObject.toJSON(dDRuleVar));
        */

    }
}
