package com.biz.warning.service.impl;

import com.biz.warning.dao.*;
import com.biz.warning.domain.RulesetRule;
import com.biz.warning.domain.StrategyRuleSet;
import com.biz.warning.service.IStrategyService;
import com.biz.warning.util.RedisUtil;
import com.biz.warning.util.SysDict;
import com.biz.warning.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StrategyServiceImpl implements IStrategyService {

    @Autowired
    StrategyDAO sDao;
    @Autowired
    RuleSetDAO rsDao;
    @Autowired
    RuleDAO rDao;
    @Autowired
    VariableDAO vDao;
    @Autowired
    TaskDAO tDao;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate redisTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 新增策略
     * @param strategy
     * @return
     */
    @Override   
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    public long addStrategy(StrategyVO strategy,String key) {
        logger.info("Strategy init start..");
        if(StringUtils.isEmpty(strategy.getStrategyCode())){
            strategy.setStrategyCode(redisUtil.generateCodeNo("SGY"));
        }
        strategy.setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
        strategy.setStrategyStatus(SysDict.STRATEGY_STATUS_ACTIVE);
        sDao.addStrategy(strategy);
        saveStrategyRelationData(strategy,key,new HashMap<>());
        return strategy.getStrategyId();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    public long updateStrategy(StrategyVO strategy,String key) {
        if(isRelationByOther(strategy)){
            //被其他任务引用了
            return -1;
        }
        sDao.updateStrategy(strategy);
        List<RuleSetVO> lRuleSet = sDao.findRuleSetByStrategy(strategy);
        Map<Long,List<VariableVO>> ruleVariableMap = new HashMap<>();
        for(RuleSetVO ruleSet : lRuleSet){
            if(ruleSet.getOrigin() == SysDict.IS_ORIGIN_FALSE){
                List<RuleVO> lRule = rsDao.findRulesByRuleSet(ruleSet);
                for(RuleVO rule : lRule){
                    rDao.deleteRule(rule);
                    ruleVariableMap.put(rule.getRuleId(),vDao.findVariableByRule(rule));
                    vDao.deleteVariableByRuleId(rule);
                }
                rsDao.deleteRuleSet(ruleSet);
                rsDao.deleteRulesByRuleSet(ruleSet);
            }
        }
        sDao.deleteRuleSetByStrategy(strategy);
        saveStrategyRelationData(strategy,key,ruleVariableMap);
        return strategy.getStrategyId();
    }

    /**
     * 添加和策略的关联数据
     * @param strategy
     */
    private void saveStrategyRelationData(StrategyVO strategy,String key,Map<Long,List<VariableVO>> ruleVariableList) {
        List<RuleSetVO> totalRuleSetList = new ArrayList<>();
        List<RuleSetVO> ruleSetVOList = strategy.getRuleSetVOList();
        HashOperations<String,String,RuleVO> opsForHash = redisTemplate.opsForHash();
        Map<String,RuleVO> ruleVOMap = opsForHash.entries(key);
        boolean isTemplate = strategy.getIsTemplate() == SysDict.IS_TEMPLATE_TRUE;
        //新增规则集
        for (RuleSetVO ruleSetVO : ruleSetVOList) {
            List<RuleVO> totalRuleList = new ArrayList<>();
            RuleSetVO originRuleSet = rsDao.findOriginRuleSet(ruleSetVO);
            //Long srcRuleSetId = ruleSetVO.getSrcRuleSetId();
            //RuleSetVO ruleSetTmp = new RuleSetVO(srcRuleSetId);
            //RuleSetVO existRuleSet = rsDao.findRuleSetByRuleSet(ruleSetTmp);
            RuleSetVO existRuleSet = rsDao.findRuleSetById(ruleSetVO.getOriginId());
            existRuleSet.setUserId(strategy.getUserId());
            existRuleSet.setApiCode(strategy.getApiCode());
            existRuleSet.setIsTemplate(strategy.getIsTemplate());
            existRuleSet.setSrcRuleSetId(existRuleSet.getRuleSetId());
            existRuleSet.setRuleSetId(0L);
            existRuleSet.setCalcLogic(ruleSetVO.getCalcLogic());
            //新增规则
            for (RuleVO ruleVO : ruleSetVO.getRuleVOList()) {
                if(ruleVOMap.containsKey(ruleVO.getRuleId().toString())){
                    ruleVO.setVariableList(ruleVOMap.get(ruleVO.getRuleId().toString()).getVariableList());
                    opsForHash.delete(key,ruleVO.getRuleId().toString());
                }
                ruleVO.setUserId(strategy.getUserId());
                ruleVO.setApiCode(strategy.getApiCode());
                ruleVO.setIsTemplate(strategy.getIsTemplate());
                ruleVO.setSrcRuleId(ruleVO.getSrcRuleId());
                ruleVO.setRuleId(0L);
                ruleVO.setRuleCode(redisUtil.generateCodeNo("RULE"));
                totalRuleList.add(ruleVO);
            }
            if(!CollectionUtils.isEmpty(totalRuleList)){
                rDao.addRuleList(totalRuleList);
            }
            if(isTemplate){
                for (RuleVO ruleVO : totalRuleList) {
                    ruleVO.setSrcRuleId(ruleVO.getRuleId());
                    rDao.updateRule(ruleVO);
                }
            }
            existRuleSet.setRuleVOList(totalRuleList);
            totalRuleSetList.add(existRuleSet);
        }
        rsDao.addRuleSetList(totalRuleSetList);
        if(isTemplate){
            for (RuleSetVO ruleSetVO : totalRuleSetList) {
                ruleSetVO.setSrcRuleSetId(ruleSetVO.getRuleSetId());
                rsDao.updateRuleSet(ruleSetVO);
            }
        }
        List<StrategyRuleSet> strategyRuleSetList = new ArrayList<>();
        List<RulesetRule> ruleSetRuleList = new ArrayList<>();
        List<VariableVO> totalVariableList = new ArrayList<>();
        //新增关联关系数据
        for (RuleSetVO ruleSetVO : totalRuleSetList) {
            //策略和规则集
            StrategyRuleSet strategyRuleSet = new StrategyRuleSet();
            strategyRuleSet.setStrategyId(strategy.getStrategyId());
            strategyRuleSet.setRuleSetId(ruleSetVO.getRuleSetId());
            strategyRuleSetList.add(strategyRuleSet);
            for (RuleVO ruleVO : ruleSetVO.getRuleVOList()) {
                //规则集和规则
                RulesetRule rulesetRule = new RulesetRule();
                rulesetRule.setRuleSetId(ruleSetVO.getRuleSetId());
                rulesetRule.setRuleId(ruleVO.getRuleId());
                ruleSetRuleList.add(rulesetRule);
                //新增变量
                for (VariableVO variableVO : ruleVO.getVariableList()) {
                    variableVO.setUserId(strategy.getUserId());
                    variableVO.setApiCode(strategy.getApiCode());
                    variableVO.setIsTemplate(strategy.getIsTemplate());
                    variableVO.setRuleId(ruleVO.getRuleId());
                    variableVO.setVariableId(0L);
                    totalVariableList.add(variableVO);
                }
            }
        }
        sDao.relateRuleSetList(strategyRuleSetList);
        rsDao.relateRuleList(ruleSetRuleList);
        if(!CollectionUtils.isEmpty(totalVariableList)){
            vDao.addVariableList(totalVariableList);
        }
    }


    @Override
    public long updateStrategyState(Long strategyId, Long strategyStatus) {
        return sDao.updateStrategyState(strategyId,strategyStatus);
    }

    @Override
    public StrategyVO findStrategy(Long strategyId,Long userId,String apiCode) {
        List<RuleSetVO> ruleSetRuleVOList = new ArrayList<>();
        StrategyVO s = sDao.findStrategy(strategyId,userId,apiCode);
        List<RuleSetVO> ruleSetList = sDao.findRuleSetByStrategy(s);
        for(RuleSetVO rs : ruleSetList){
            rs.setUserId(userId);
            rs.setApiCode(apiCode);
            RuleSetVO originRuleSet = rsDao.findOriginRuleSet(rs);
            if(originRuleSet != null) {
                rs.setOriginId(originRuleSet.getRuleSetId());
            }
            List<RuleVO> ruleList = rsDao.findRulesByRuleSet(rs);
            List<RuleVO> ruleVOList = new ArrayList<>();
            for(RuleVO rule:ruleList){
                List<VariableVO> variableList = vDao.findVariableByRule(rule);
                rule.setVariableList(variableList);
                //查找该规则下各个版本
                List<RuleVO> ruleVersionList = rDao.findRuleVersion(null, apiCode, rule.getSrcRuleCode(), null);
                rule.setVersionList(ruleVersionList);
                ruleVOList.add(rule);
            }
            rs.setRuleVOList(ruleVOList);
            ruleSetRuleVOList.add(rs);
        }
        s.setRuleSetVOList(ruleSetRuleVOList);
        StrategyVO strategyVO = new StrategyVO(strategyId);
        strategyVO.setApiCode(apiCode);
        s.setIsRelated(isRelationByOther(strategyVO));
        return s;
    }


    @Override
    public StrategyVO findRuleSetsByStrategy(Long strategyId, Long userId, String apiCode) {
        List<RuleSetVO> ruleSetRuleVOList = new ArrayList<>();
        StrategyVO s = sDao.findStrategy(strategyId,null,apiCode);
        Map<String,RuleSetVO> tmpMap = new HashMap<>();
        List<RuleSetVO> ruleSetListFinal = new ArrayList<>();
        List<RuleSetVO> ruleSetList = sDao.findRuleSetByStrategy(s);
        int instCount = CollectionUtils.isEmpty(ruleSetList)?0:ruleSetList.size();
        List<RuleSetVO> ruleSetListTemplate = sDao.findRuleSetByStrategy(new StrategyVO(s.getSourceStrategyId()));
        int tempCount = CollectionUtils.isEmpty(ruleSetListTemplate)?0:ruleSetListTemplate.size();
        if(ruleSetListTemplate.size()==ruleSetList.size()){
            ruleSetList.forEach(ruleSet->{
                ruleSetListFinal.add(ruleSet);
            });
        }else{
            ruleSetList.forEach(ruleSet -> {
                if(ruleSet.getSrcRuleSetId().longValue()!=ruleSet.getRuleSetId().longValue()){
                    tmpMap.put(ruleSet.getSrcRuleSetId().toString(),ruleSet);
                    ruleSetListFinal.add(ruleSet);
                }
            });
            ruleSetListTemplate.forEach(ruleSet -> {
                if(null==tmpMap.get(ruleSet.getRuleSetId().toString())){
                    ruleSetListFinal.add(ruleSet);
                }
            });
        }
        for(RuleSetVO rs : ruleSetListFinal){
            //RuleSetVO rsVo = new RuleSetRuleVO();
            rs.setInstRuleSetCount(instCount);
            rs.setTempRuleSetCount(tempCount);
            rs.setApiCode(apiCode);
            rs.setUserId(userId);
            List<RuleVO> ruleList = rsDao.findRulesByRuleSet(rs);
            List<RuleVO> ruleVOList = new ArrayList<>();
            for(RuleVO rule:ruleList){
                List<VariableVO> variableList = vDao.findVariableByRule(rule);
                //查询该规则所有的版本列表
                List<RuleVO> listRule = rDao.findRuleVersion(rule.getSrcRuleId(),apiCode,rule.getSrcRuleCode(),null);
                rule.setVersionList(listRule);

                //rule.setRule(rule);
                rule.setVariableList(variableList);
                ruleVOList.add(rule);
            }
            rs.setRuleVOList(ruleVOList);
            ruleSetRuleVOList.add(rs);
        }
        s.setRuleSetVOList(ruleSetRuleVOList);
        return s;
    }

    @Override
    public List<StrategyVO> findAllStrategy(StrategyVO Strategy) {
        return sDao.findAllStrategy(Strategy);
    }

    @Override
    @Transactional
    public long updateStrategyTemplateState(Long strategyId, Long strategyTemplateStatus) {
        long result = sDao.updateStrategyTemplateState(strategyId,strategyTemplateStatus);
        if(strategyTemplateStatus == SysDict.IS_TEMPLATE_TRUE){
            StrategyVO strategyVO = new StrategyVO();
            strategyVO.setStrategyId(strategyId);
            strategyVO.setSourceStrategyId(strategyId);
            sDao.updateStrategy(strategyVO);
        }
        StrategyVO strategy = new StrategyVO();
        strategy.setStrategyId(strategyId);
        List<RuleSetVO> lRuleSet = sDao.findRuleSetByStrategy(strategy);
        for(RuleSetVO ruleSet : lRuleSet){
            List<RuleVO> lRule = rsDao.findRulesByRuleSet(ruleSet);
            for(RuleVO rule : lRule) {
                rule.setIsTemplate(strategyTemplateStatus);
                if(strategyTemplateStatus == SysDict.IS_TEMPLATE_TRUE){
                    rule.setSrcRuleId(rule.getRuleId());
                }
                rDao.updateRule(rule);
                List<VariableVO> variableList = vDao.findVariableByRule(rule);
                for (VariableVO variableVO : variableList) {
                    variableVO.setIsTemplate(strategyTemplateStatus);
                    vDao.updateVariable(variableVO);
                }
            }
            ruleSet.setIsTemplate(strategyTemplateStatus);
            if(strategyTemplateStatus == SysDict.IS_TEMPLATE_TRUE){
                ruleSet.setSrcRuleSetId(ruleSet.getRuleSetId());
            }
            rsDao.updateRuleSet(ruleSet);
        }
        return result;
    }

    @Override
    public List<RuleSetVO> findRuleSetListByStrategyId(Long strategyId) {
        StrategyVO strategy = new StrategyVO();
        strategy.setStrategyId(strategyId);
        return sDao.findRuleSetByStrategy(strategy);
    }

    @Override
    public List<StrategyVO> findStrategyByParam(StrategyVO strategy, String param) {
        return sDao.findStrategyByParam(strategy,param);
    }


    @Override
    public List<StrategyVO> findAllStrategyNotTask(StrategyVO strategy) {
        return sDao.findAllStrategyNotTask(strategy);
    }

    @Override
    public boolean isRelationByOther(StrategyVO strategyVO) {
        List<TaskVO> taskVOList = sDao.findTaskBySrcStrategy(strategyVO);
        return !CollectionUtils.isEmpty(taskVOList);
    }

}
