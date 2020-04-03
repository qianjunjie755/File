package com.biz.warning.service.impl;

import com.biz.warning.dao.RuleDAO;
import com.biz.warning.dao.RuleSetDAO;
import com.biz.warning.dao.VariableDAO;
import com.biz.warning.service.IRuleSetService;
import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.RuleVO;
import com.biz.warning.vo.VariableVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class  RuleSetServiceImpl implements IRuleSetService {
    @Autowired
    private RuleSetDAO dao;

    @Autowired
    private VariableDAO vDao;
    @Autowired
    private RuleDAO rDao;
    @Override
    public long addRuleSet(RuleSetVO ruleSet) {
        return dao.addRuleSet(ruleSet);
    }

    @Override
    public List<RuleSetVO> findAllRuleSet(RuleSetVO ruleSet) {
        return dao.findAllRuleSet(ruleSet);
    }

    @Override
    public List<RuleVO> findRulesByRuleSet(RuleSetVO ruleSetVO) {
        List<RuleVO> ruleList = dao.findRuleListByRuleSet(ruleSetVO);
        for(RuleVO rule:ruleList){
            List<VariableVO> variableList = vDao.findVariableByRule(rule);
            //查询该规则所有的版本列表
            List<RuleVO> listRule = rDao.findActiveRuleVersionByRuleCode(rule.getSrcRuleCode(),ruleSetVO.getApiCode());
            rule.setVersionList(listRule);
            rule.setVariableList(variableList);
        }
        return ruleList;
    }

    @Override
    public List<RuleVO> findRulesByRuleSetForTask(RuleSetVO ruleSetVO) {
        RuleSetVO ruleSetRet = dao.findRuleSetByRuleSet(ruleSetVO);
        RuleSetVO srcRuleSetVO = new RuleSetVO();
        srcRuleSetVO.setRuleSetId(ruleSetRet.getSrcRuleSetId());
        List<RuleVO> ruleList = dao.findRulesByRuleSet(ruleSetVO);
        List<RuleVO> finalList = new ArrayList<>();
        Set<String> instSrcRuleIdSet = new HashSet<>();
        for(RuleVO rule:ruleList){
            List<VariableVO> variableList = vDao.findVariableByRule(rule);
            //查询该规则所有的版本列表
            List<RuleVO> listRule = rDao.findRuleVersion(rule.getSrcRuleId(),ruleSetVO.getApiCode(),rule.getSrcRuleCode(),null);
            listRule.forEach(r->{
                List<VariableVO> vList = vDao.findVariableByRule(rule);
                r.setVariableList(vList);
            });
            rule.setVersionList(listRule);
            //ruleVO.setRule(rule);
            rule.setVariableList(variableList);
            finalList.add(rule);
            instSrcRuleIdSet.add(rule.getSrcRuleId().toString());
        }
        if(0==ruleSetRet.getIsTemplate()){
            List<RuleVO> srcRuleList = dao.findRulesByRuleSet(srcRuleSetVO);
            for(RuleVO rule:srcRuleList){
                if(!instSrcRuleIdSet.contains(rule.getRuleId().toString())){
                    List<VariableVO> variableList = vDao.findVariableByRule(rule);
                    //查询该规则所有的版本列表
                    List<RuleVO> listRule = rDao.findRuleVersion(rule.getSrcRuleId(),ruleSetVO.getApiCode(),rule.getSrcRuleCode(),null);
                    listRule.forEach(r->{
                        List<VariableVO> vList = vDao.findVariableByRule(rule);
                        r.setVariableList(vList);
                    });
                    rule.setVersionList(listRule);
                    //ruleVO.setRule(rule);
                    rule.setVariableList(variableList);
                    finalList.add(rule);
                }
            }
        }

        return finalList;
    }

    @Override
    public RuleSetVO findRuleSetByRuleId(Long ruleId,String apiCode) {
        return dao.findRuleSetByRuleId(ruleId,apiCode);
    }

    @Override
    public List<RuleSetVO> findRuleSetByParam(RuleSetVO ruleSet) {
        return dao.findRuleSetByParam(ruleSet);
    }

    @Override
    public List<RuleVO> findActiveRuleByRuleSet(RuleSetVO ruleSetVO) {
        List<RuleVO> ruleList = dao.findActiveRuleByRuleSet(ruleSetVO);
        for(RuleVO rule:ruleList){
            List<VariableVO> variableList = vDao.findVariableByRule(rule);
            //查询该规则所有的版本列表
            List<RuleVO> listRule = rDao.findActiveRuleVersionByRuleCode(rule.getSrcRuleCode(),ruleSetVO.getApiCode());
            rule.setVersionList(listRule);
            rule.setVariableList(variableList);
        }
        return ruleList;
    }


}
