package com.biz.credit.service.impl;

import com.biz.credit.dao.*;
import com.biz.credit.domain.*;
import com.biz.credit.service.ITaskService;
import com.biz.credit.utils.Constants;
import com.biz.credit.utils.RedisUtil;
import com.biz.credit.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class TaskServiceImpl implements ITaskService {
    @Autowired
    private StrategyDAO strategyDAO;
    @Autowired
    private RuleSetDAO ruleSetDAO;
    @Autowired
    private RuleDAO ruleDAO;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private VariableDAO variableDAO;
    @Autowired
    private ModuleTypeDAO moduleTypeDAO;
    @Autowired
    private ModuleTypeApiDAO moduleTypeApiDAO;
    @Autowired
    private IndustryInfoApiDAO industryInfoApiDAO;
    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    public long copyStrategyTemplate(StrategyVO source, StrategyVO target) throws Exception {
        long count = 0;
        StrategyVO retSource = strategyDAO.findStrategy(source.getStrategyId(),source.getUserId(),source.getApiCode());
        List<RuleSetVO> retSourceRuleSetList =  strategyDAO.findRuleSetByStrategy(retSource);
        retSourceRuleSetList.forEach(ruleSetVO -> {
            List<RuleVO> retSourceRuleVOList = ruleSetDAO.findRuleListByRuleSet(ruleSetVO);
            retSourceRuleVOList.forEach(ruleVO -> {
                List<VariableVO> retSourceVariableList = variableDAO.findVariableByRule(ruleVO);
                ruleVO.setVariableList(retSourceVariableList);
            });
            ruleSetVO.setRuleVOList(retSourceRuleVOList);
        });
        retSource.setRuleSetVOList(retSourceRuleSetList);
        retSource.setStrategyId(null);
        retSource.setStrategyCode(redisUtil.generateCodeNo("RSC"));
        retSource.setBusinessCode(retSource.getStrategyCode().replaceAll("RSC","RSBC"));
        retSource.setIsTemplate(target.getIsTemplate());
        retSource.setUserId(target.getUserId());
        retSource.setApiCode(target.getApiCode());
        if(target.getIndustryId()>0){
            retSource.setIndustryId(target.getIndustryId());
        }
        count = count + strategyDAO.addStrategy(retSource);
        target.setStrategyId(retSource.getStrategyId());
        List<StrategyRuleSet> strategyRuleSetList = new ArrayList<>();
        List<RulesetRule> rulesetRuleList = new ArrayList<>();
        List<VariableVO> variableVOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(retSource.getRuleSetVOList())){
            retSource.getRuleSetVOList().forEach(ruleSetVO -> {
                ruleSetVO.setRuleSetId(null);
                ruleSetVO.setIsTemplate(target.getIsTemplate());
                ruleSetVO.setUserId(target.getUserId());
                ruleSetVO.setApiCode(target.getApiCode());
                ruleSetDAO.addRuleSet(ruleSetVO);
                strategyRuleSetList.add(new StrategyRuleSet(retSource.getStrategyId(),ruleSetVO.getRuleSetId()));
                if(!CollectionUtils.isEmpty(ruleSetVO.getRuleVOList())){
                    List<String> ruleCodeList = redisUtil.generateCodeNos("RRC",ruleSetVO.getRuleVOList().size());
                    Iterator<String> it = ruleCodeList.iterator();
                    ruleSetVO.getRuleVOList().forEach(ruleVO -> {
                        ruleVO.setRuleId(null);
                        ruleVO.setIsTemplate(target.getIsTemplate());
                        ruleVO.setRuleCode(it.next());
                        ruleVO.setBusinessCode(ruleVO.getRuleCode().replaceAll("RRC","RRBC"));
                        ruleVO.setUserId(target.getUserId());
                        ruleVO.setApiCode(target.getApiCode());
                        ruleDAO.addRule(ruleVO);
                        rulesetRuleList.add(new RulesetRule(ruleSetVO.getRuleSetId(),ruleVO.getRuleId()));
                        if(!CollectionUtils.isEmpty(ruleVO.getVariableList())){
                            ruleVO.getVariableList().forEach(variableVO -> {
                                variableVO.setVariableId(null);
                                variableVO.setUserId(target.getUserId());
                                variableVO.setApiCode(target.getApiCode());
                                variableVO.setRuleId(ruleVO.getRuleId());
                                variableVO.setIsTemplate(target.getIsTemplate());
                                variableVOList.add(variableVO);
                            });
                        }
                    });
                }
            });
        }
        if(!CollectionUtils.isEmpty(strategyRuleSetList))
            count = count + strategyDAO.relateRuleSetList(strategyRuleSetList);
        if(!CollectionUtils.isEmpty(rulesetRuleList))
            count = count + ruleSetDAO.relateRuleList(rulesetRuleList);
        if(!CollectionUtils.isEmpty(variableVOList))
            count = count + variableDAO.addVariableList(variableVOList);
        return count;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    public long updateSrcIdForCopyStrategyTemplate(StrategyVO target) throws Exception {
        long count = strategyDAO.updateStrategyForCopyStrategy(target);
        RuleSetVO ruleSetVO =  new RuleSetVO(target.getUserId(),target.getApiCode());
        ruleSetVO.setStrategyId(target.getStrategyId());
        ruleSetVO.setIsTemplate(target.getIsTemplate());
        count = count + ruleSetDAO.updateRuleSetForCopyStrategy(ruleSetVO);
        RuleVO ruleVO =  new RuleVO(target.getUserId(),target.getApiCode());
        ruleVO.setIsTemplate(target.getIsTemplate());
        ruleVO.setStrategyId(target.getStrategyId());
        count = count + ruleDAO.updateRuleForCopyStrategy(ruleVO);
        return count;
    }
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    @Override
    public long copyModuleTypeByStrategyId(StrategyVO source, StrategyVO target) throws Exception {
        int count = 0;
        ModuleTypeVO moduleTypeVO = new ModuleTypeVO();
        moduleTypeVO.setStrategyId(source.getStrategyId());
        moduleTypeVO.setNewStrategyId(target.getStrategyId());
        moduleTypeVO.setApiCode(target.getApiCode());
        //moduleTypeVO.setDescription(moduleTypeVO.getModuleTypeName().concat(target.getApiCode()));
        count = count + moduleTypeDAO.addModuleTypeByStrategyId(moduleTypeVO);
        ModuleTypeVO sourceModuleTypeVO = moduleTypeDAO.findModuleTypeTemplateByStrategyId(source.getStrategyId().intValue());
        ModuleTypeApi moduleTypeApi = new ModuleTypeApi();
        moduleTypeApi.setModuleTypeId(moduleTypeVO.getModuleTypeId());
        moduleTypeApi.setValidEnd("3000-12-01 00:00:00");
        moduleTypeApi.setReportType(sourceModuleTypeVO.getReportType());
        moduleTypeApi.setApiCode(target.getApiCode());
        moduleTypeApi.setStatus(Constants.MODULE_TYPE_API_STATUS_VALID);
        count = count + moduleTypeApiDAO.addModuleTypeApi(moduleTypeApi);
        //if(2!=target.getIndustryId()){
            IndustryInfoApi industryInfoApi = new IndustryInfoApi();
            industryInfoApi.setIndustryId(target.getIndustryId());
            industryInfoApi.setModuleTypeId(moduleTypeVO.getModuleTypeId());
            industryInfoApi.setApiCode(target.getApiCode());
            count = count + industryInfoApiDAO.addIndustryInfoApi(industryInfoApi);
        //}
        return count;
    }
}
