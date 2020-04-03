package com.biz.warning.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.strategy.BizTask;
import com.biz.strategy.entity.Task;
import com.biz.strategy.entity.TaskInput;
import com.biz.utils.DateUtils;
import com.biz.warning.dao.*;
import com.biz.warning.domain.Entity;
import com.biz.warning.domain.RulesetRule;
import com.biz.warning.domain.StrategyRuleSet;
import com.biz.warning.domain.VariableParamPool;
import com.biz.warning.service.IEntityService;
import com.biz.warning.service.IStrategyService;
import com.biz.warning.service.ITaskService;
import com.biz.warning.util.RedisUtil;
import com.biz.warning.util.RuleSetVORuleSetCodeComparator;
import com.biz.warning.util.SysDict;
import com.biz.warning.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TaskServiceImpl  implements ITaskService {

    @Autowired
    private TaskDAO taskDAO;
    @Autowired
    private VariableParamPoolDAO variableParamPoolDAO;
    @Autowired
    private IStrategyService strategyService;
    @Autowired
    private StrategyDAO strategyDAO;
    @Autowired
    private RuleSetDAO ruleSetDAO;
    @Autowired
    private RuleDAO ruleDAO;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private EntityDAO entityDAO;
    @Autowired
    private VariableDAO variableDAO;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IEntityService entityService;
    @Autowired
    private BizTask bizTask;

    private String apiInfoKeyPrefix = "{biz_credit:strategy_engine:apiInfo}:";
    private String strategyPrefix = "{biz_credit:risk_warning:task:strategy:template:}:";

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    public long addTask(TaskVO task) throws Exception {
        /*long repeatError = checkRepeat(task);
        if(repeatError<0){
            return repeatError;
        }*/
        Long templateStrategyId = task.getStrategyVO().getStrategyId();
        StrategyVO templateStrategyVO = strategyDAO.findStrategy(task.getStrategyVO().getStrategyId(),null,null);
        task.getStrategyVO().setDescription(task.getStrategyDescription());
        task.getStrategyVO().setSourceStrategyId(task.getStrategyVO().getStrategyId());
        task.getStrategyVO().setApiCode(task.getApiCode());
        task.getStrategyVO().setUserId(task.getUserId());
        task.getStrategyVO().setStrategyId(null);
        task.getStrategyVO().setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
        task.getStrategyVO().setStrategyCode(redisUtil.generateCodeNo("SGY"));
        task.getStrategyVO().setExecInterval(templateStrategyVO.getExecInterval());
        strategyDAO.addStrategy(task.getStrategyVO());
        changeEntityTemplateName(task);
        task.setStrategyId(task.getStrategyVO().getStrategyId());
        long count = taskDAO.addTask(task);

        StrategyVO strategy = new StrategyVO();
        strategy.setStrategyId(task.getStrategyVO().getSourceStrategyId());
        List<RuleSetVO> templateRuleSetList =  strategyDAO.findRuleSetByStrategy(strategy);
        List<RuleSetVO> ruleSetList = new ArrayList<>();
        List<RuleVO> ruleListFinal = new ArrayList<>();
        List<VariableVO> variableListFinal = new ArrayList<>();
        Map<Long,RuleSetVO> ruleSetTemplateMap = new HashMap<>();
        Map<Long,List<RuleVO>> ruleSetRuleMap = new HashMap<>();
        Map<Long,RuleVO> ruleTemplateMap = new HashMap<>();
        Map<Long,List<VariableVO>> variableTemplateMap = new HashMap<>();
        Map<Long,VariableVO> variableMap = new HashMap<>();
        templateRuleSetList.forEach(ruleSet -> {
            ruleSetTemplateMap.put(ruleSet.getRuleSetId(),ruleSet);
            RuleSetVO tmp = new RuleSetVO();
            BeanUtils.copyProperties(ruleSet,tmp);
        });
        for(RuleSetVO ruleSetVO:task.getStrategyVO().getRuleSetVOList()){
            RuleSetVO ruleSet = ruleSetTemplateMap.get(ruleSetVO.getRuleSetId());
            List<RuleVO> ruleTemplateList = ruleSetDAO.findRulesByRuleSet(ruleSetVO);
            ruleTemplateList.forEach(ruleVO -> {
                ruleTemplateMap.put(ruleVO.getRuleId(),ruleVO);

            });
            ruleSetRuleMap.put(ruleSetVO.getSrcRuleSetId(),ruleSetVO.getRuleVOList());
            if(null!=ruleSet){
                ruleSet.setRuleSetId(null);
                ruleSet.setUserId(task.getUserId());
                ruleSet.setApiCode(task.getApiCode());
                ruleSet.setIsTemplate(0l);
                ruleSet.setCalcLogic(ruleSetVO.getCalcLogic());
                ruleSet.setPriority(ruleSetVO.getPriority());
                ruleSetList.add(ruleSet);
            }
            for(RuleVO ruleVO:ruleSetVO.getRuleVOList()){
                variableTemplateMap.put(ruleVO.getRuleId(),variableDAO.findVariableByRule(new RuleVO(ruleVO.getRuleId())));
                for(VariableVO variableVO:ruleVO.getVariableList()){
                    variableMap.put(variableVO.getVariableId(),variableVO);
                }
            }
        }
        /*String strategyRedisKey = strategyPrefix.concat(task.getTaskId().toString());
        ValueOperations<String,StrategyVO> strategyOps = redisTemplate.opsForValue();
        strategyOps.set(strategyRedisKey,redisStrategyVO,1*24*3600,TimeUnit.SECONDS);*/
        ruleSetDAO.addRuleSetList(ruleSetList);
        List<StrategyRuleSet> strategyRuleSetList = new ArrayList<>();
        Map<Long,List<RuleVO>> newRuleSetRuleMap = new HashMap<>();
        ruleSetList.forEach(ruleSet -> {
            strategyRuleSetList.add(new StrategyRuleSet(task.getStrategyVO().getStrategyId(),ruleSet.getRuleSetId()));
            List<RuleVO> list = ruleSetRuleMap.get(ruleSet.getSrcRuleSetId());
            if(!CollectionUtils.isEmpty(list)){
                newRuleSetRuleMap.put(ruleSet.getRuleSetId(),list);
            }
        });
        strategyDAO.relateRuleSetList(strategyRuleSetList);
        Map<Long,List<RuleVO>> newRuleMap = new HashMap<>();
        newRuleSetRuleMap.forEach((ruleSetId,rules) -> {
            if(!CollectionUtils.isEmpty(rules)){
                List<String> ruleCodeList = redisUtil.generateCodeNos("RULE",rules.size());
                Iterator<String> it = ruleCodeList.iterator();
                rules.forEach(ruleVO -> {
                    RuleVO ruleTemp = ruleTemplateMap.get(ruleVO.getSrcRuleId());
                    if(null!=ruleTemp){
                        ruleTemp.setRuleId(null);
                        ruleTemp.setIsTemplate(0l);
                        ruleTemp.setRuleCode(it.next());
                        ruleTemp.setBusinessCode(ruleTemp.getRuleCode().replaceAll("RULE","RULEBC"));
                        ruleTemp.setCalcLogic(ruleVO.getCalcLogic());
                        ruleTemp.setUserId(task.getUserId());
                        ruleTemp.setApiCode(task.getApiCode());
                        ruleListFinal.add(ruleTemp);
                        if(null==newRuleMap.get(ruleSetId)){
                            newRuleMap.put(ruleSetId,new ArrayList<>());
                        }
                        newRuleMap.get(ruleSetId).add(ruleTemp);
                    }
                });
            }
        });
        ruleDAO.addRuleList(ruleListFinal);
        List<RulesetRule> rsrList = new ArrayList<>();
        newRuleMap.forEach((ruleSetId,ruleList)->{
            ruleList.forEach(rule->{
                rsrList.add(new RulesetRule(ruleSetId,rule.getRuleId()));
                List<VariableVO> variableVOList = variableTemplateMap.get(rule.getSrcRuleId());
                if(!CollectionUtils.isEmpty(variableVOList)){
                    variableVOList.forEach(variableVO -> {
                        Long variableId = variableVO.getVariableId();
                        variableVO.setVariableId(null);
                        variableVO.setRuleId(rule.getRuleId());
                        variableVO.setApiCode(task.getApiCode());
                        variableVO.setUserId(task.getUserId());
                        variableVO.setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
                        VariableVO variable = variableMap.get(variableId);
                        if(null!=variable){
                            variableVO.setThreshold(variable.getThreshold());
                        }
                    });
                    variableListFinal.addAll(variableVOList);
                }
            });
        });
        ruleSetDAO.relateRuleList(rsrList);
        variableDAO.addVariableList(variableListFinal);

        return count;
    }
    public void changeEntityTemplateName(TaskVO task) throws Exception{
        List<RuleSetVO> ruleSetList = task.getStrategyVO().getRuleSetVOList();
        Set<String> apiInfoSet = new HashSet<>();
        ruleSetList.forEach(ruleSet->{
            apiInfoSet.add(ruleSet.getApiProdCode().concat(":").concat(ruleSet.getApiVersion().toString()));
        });
        Set<String> paramSet = new HashSet<>();
        paramSet.add("expire_time");paramSet.add("application_date");
        apiInfoSet.forEach(apiInfo->{
            JSONObject apiInfoJson = JSONObject.parseObject(JSONObject.toJSONString(stringRedisTemplate.opsForHash().entries(apiInfoKeyPrefix.concat(apiInfo))));
            if(null!=apiInfoJson&&apiInfoJson.size()>0){
                JSONObject queryParams = null;
                try{
                    queryParams = apiInfoJson.getJSONObject("varsetQueryJsonData").getJSONObject("source").getJSONObject("params");
                }catch (NullPointerException e){
                    log.error(e.getMessage(),e);
                    log.info("apiInfoSet_Exception:".concat(apiInfo));
                }
                queryParams.keySet().forEach(key->{
                        paramSet.add(key.trim());
                });
                JSONObject resultParams = apiInfoJson.getJSONObject("varsetResultJsonData").getJSONObject("params");
                resultParams.keySet().forEach(prodCode->{
                    JSONObject params = resultParams.getJSONObject(prodCode);
                    params.keySet().forEach(key->{
                        paramSet.add(key.trim());
                    });
                });
            }
        });
        List<VariableParamPool> variableParamPoolList = variableParamPoolDAO.findListByNameList(new ArrayList<>(paramSet));
        StringBuffer paramSB = new StringBuffer();
        Set<String> repeatCheckSet = new HashSet<>();
        for(VariableParamPool variableParamPool:variableParamPoolList){
            if(!repeatCheckSet.contains(variableParamPool.getDescription())){
                paramSB.append(variableParamPool.getDescription()).append("_");
                repeatCheckSet.add(variableParamPool.getDescription());
            }
        }
        task.setEntityTemplateName(paramSB.substring(0,paramSB.length()-1));
    }

    @Override
    public String getHeadListByTaskId(Integer taskId) throws Exception {
        return taskDAO.getHeadListByTaskId(taskId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    public long updateTask(TaskVO task) throws Exception {
        /*long repeatError = checkRepeat(task);
        if(repeatError<0){
            return repeatError;
        }*/
        long count = 0;
        changeEntityTemplateName(task);
        StrategyVO strategyVO = strategyDAO.findStrategy(task.getStrategyVO().getStrategyId(),null,task.getApiCode());
        strategyVO.setCalcLogic(task.getStrategyVO().getCalcLogic());
        strategyVO.setDescription(task.getStrategyDescription());

        if(SysDict.IS_TEMPLATE_FALSE==strategyVO.getIsTemplate()){
            strategyDAO.updateStrategy(strategyVO);
        }else{
            Long templateStrategyId = strategyVO.getStrategyId();
            String strategyRedisKey = strategyPrefix.concat(task.getTaskId().toString());
            ValueOperations<String,StrategyVO> strategyOps = redisTemplate.opsForValue();
            strategyOps.set(strategyRedisKey,strategyVO,365*24*3600,TimeUnit.SECONDS);
            strategyVO.setStrategyId(null);
            strategyVO.setCalcLogic(task.getStrategyVO().getCalcLogic());
            strategyVO.setStrategyCode(redisUtil.generateCodeNo("SGY"));
            strategyVO.setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
            strategyVO.setUserId(task.getUserId());
            strategyVO.setApiCode(task.getApiCode());
            strategyDAO.addStrategy(strategyVO);
            task.setStrategyId(strategyVO.getStrategyId());
            task.getStrategyVO().setStrategyId(strategyVO.getStrategyId());
            TaskVO taskTmp = taskDAO.findByTask(task);
            if(null!=taskTmp){
                strategyDAO.deleteStrategy(taskTmp.getStrategyId());
            }
        }

        List<RuleSetVO> oldRuleSetVOList =  strategyDAO.findRuleSetByStrategy(strategyVO);
        Map<Long,RuleSetVO> oldRuleSetVOMap = new HashMap<>();
        oldRuleSetVOList.forEach(ruleSetVO -> {
            oldRuleSetVOMap.put(ruleSetVO.getRuleSetId(),ruleSetVO);
        });
        Map<Long,RuleSetVO> updateRuleSetVOMap = new HashMap<>();
        Map<Long,RuleSetVO> newRuleSetVOTemplateMap = new HashMap<>();
        task.getStrategyVO().getRuleSetVOList().forEach(ruleSetVO -> {
            if(SysDict.IS_TEMPLATE_TRUE==ruleSetVO.getIsTemplate()){
                newRuleSetVOTemplateMap.put(ruleSetVO.getRuleSetId(),ruleSetVO);
            }else{
                updateRuleSetVOMap.put(ruleSetVO.getRuleSetId(),ruleSetVO);
            }
        });
        oldRuleSetVOMap.keySet().forEach(ruleSetId->{
            if(!updateRuleSetVOMap.containsKey(ruleSetId)){
                strategyDAO.deleteRelateRuleSet(new StrategyRuleSet(strategyVO.getStrategyId(),ruleSetId));
            }
        });
        strategyDAO.deleteRuleSetByStrategy(task.getStrategyVO());
        //处理添加
        List<RuleSetVO> ruleSetVOListToAdd = new ArrayList<>();
        Map<Long,List<RuleVO>> ruleVOMapToAdd = new HashMap<>();
        Map<Long,List<VariableVO>> variableVOMapToAdd = new HashMap<>();
        List<VariableVO> variableVOListToAdd = new ArrayList<>();
        List<StrategyRuleSet> strategyRuleSetListToAdd = new ArrayList<>();
        List<RulesetRule> ruleSetRuleListToAdd = new ArrayList<>();
        for(Long ruleSetId:newRuleSetVOTemplateMap.keySet()){
            RuleSetVO ruleSetVO = ruleSetDAO.findRuleSetByRuleSet(newRuleSetVOTemplateMap.get(ruleSetId));
            List<RuleVO> ruleVOList = newRuleSetVOTemplateMap.get(ruleSetId).getRuleVOList();

            if(!CollectionUtils.isEmpty(ruleVOList)){
                List<String> codes = redisUtil.generateCodeNos("RULE",ruleVOList.size());
                Iterator<String> it = codes.iterator();
                for(RuleVO ruleVO:ruleVOList){
                    List<VariableVO> variableVOList = ruleVO.getVariableList();
                    if(CollectionUtils.isEmpty(variableVOList))
                        variableVOList = variableDAO.findVariableByRule(ruleVO);
                    if(!CollectionUtils.isEmpty(variableVOList)){
                        variableVOMapToAdd.put(ruleVO.getRuleId(),variableVOList);
                    }
                    ruleVO.setRuleId(null);
                    ruleVO.setRuleCode(it.next());
                    ruleVO.setBusinessCode(ruleVO.getRuleCode().replaceAll("RULE","RULEBC"));
                    ruleVO.setUserId(task.getUserId());
                    ruleVO.setApiCode(task.getApiCode());
                    ruleVO.setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
                }
            }
            ruleVOMapToAdd.put(newRuleSetVOTemplateMap.get(ruleSetId).getSrcRuleSetId(),ruleVOList);
            ruleSetVO.setRuleSetId(null);
            ruleSetVO.setCalcLogic(newRuleSetVOTemplateMap.get(ruleSetId).getCalcLogic());
            ruleSetVO.setUserId(task.getUserId());
            ruleSetVO.setApiCode(task.getApiCode());
            ruleSetVO.setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
            ruleSetVOListToAdd.add(ruleSetVO);
        }
        if(!CollectionUtils.isEmpty(ruleSetVOListToAdd)){
            ruleSetDAO.addRuleSetList(ruleSetVOListToAdd);
        }
        for(RuleSetVO ruleSetVO: ruleSetVOListToAdd){
            strategyRuleSetListToAdd.add(new StrategyRuleSet(strategyVO.getStrategyId(),ruleSetVO.getRuleSetId()));
            List<RuleVO> ruleVOList = ruleVOMapToAdd.get(ruleSetVO.getSrcRuleSetId());
            if(!CollectionUtils.isEmpty(ruleVOList)){
                ruleDAO.addRuleList(ruleVOList);
                ruleVOList.forEach(ruleVO -> {
                    ruleSetRuleListToAdd.add(new RulesetRule(ruleSetVO.getRuleSetId(),ruleVO.getRuleId()));
                    List<VariableVO> variableVOList = variableVOMapToAdd.get(ruleVO.getSrcRuleId());
                    if(!CollectionUtils.isEmpty(variableVOList)){
                        variableVOList.forEach(variableVO -> {
                            variableVO.setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
                            variableVO.setVariableId(null);
                            variableVO.setRuleId(ruleVO.getRuleId());
                            variableVO.setUserId(task.getUserId());
                            variableVO.setApiCode(task.getApiCode());
                            variableVOListToAdd.add(variableVO);
                        });
                    }
                });
            }
        }

        //处理更新
        Map<Long,RuleVO> newRuleVOMap = new HashMap<>();
        Map<Long,RuleVO> updateRuleVOMap = new HashMap<>();
        for(Map.Entry<Long,RuleSetVO> entry:updateRuleSetVOMap.entrySet()){
            ruleSetDAO.deleteRulesByRuleSet(entry.getValue());
            RuleSetVO ruleSetVO = ruleSetDAO.findRuleSetByRuleSet(entry.getValue());
            if(SysDict.IS_TEMPLATE_FALSE==ruleSetVO.getIsTemplate()){
                ruleSetVO.setCalcLogic(entry.getValue().getCalcLogic());
                ruleSetDAO.updateRuleSet(ruleSetVO);
            }

            if(!CollectionUtils.isEmpty(entry.getValue().getRuleVOList())){
                for(RuleVO ruleVO:entry.getValue().getRuleVOList()){
                    if(SysDict.IS_TEMPLATE_TRUE==ruleVO.getIsTemplate()){
                        RuleVO tempRule = ruleDAO.findRuleById(ruleVO.getRuleId(),null,null);
                        tempRule.setRuleId(null);
                        tempRule.setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
                        tempRule.setCalcLogic(ruleVO.getCalcLogic());
                        tempRule.setUserId(task.getUserId());
                        tempRule.setApiCode(task.getApiCode());
                        ruleDAO.addRule(tempRule);
                        ruleSetRuleListToAdd.add(new RulesetRule(entry.getValue().getRuleSetId(),tempRule.getRuleId()));
                        newRuleVOMap.put(tempRule.getRuleId(),ruleVO);
                    }else{
                        ruleSetRuleListToAdd.add(new RulesetRule(entry.getValue().getRuleSetId(),ruleVO.getRuleId()));
                        updateRuleVOMap.put(ruleVO.getRuleId(),ruleVO);
                    }
                }
            }
            strategyRuleSetListToAdd.add(new StrategyRuleSet(task.getStrategyVO().getStrategyId(),entry.getKey()));
        }
        newRuleVOMap.forEach((ruleId,ruleVO)->{
            if(!CollectionUtils.isEmpty(ruleVO.getVariableList())){
                ruleVO.getVariableList().forEach(variableVO -> {
                    variableVO.setVariableId(null);
                    variableVO.setIsTemplate(SysDict.IS_TEMPLATE_FALSE);
                    variableVO.setRuleId(ruleId);
                    variableVO.setUserId(task.getUserId());
                    variableVO.setApiCode(task.getApiCode());
                    variableVOListToAdd.add(variableVO);
                });
            }
        });
        for(Map.Entry<Long,RuleVO> entry: updateRuleVOMap.entrySet()){
            ruleDAO.updateRuleCalcLogic(entry.getValue());
            if(!CollectionUtils.isEmpty(entry.getValue().getVariableList())){
                for(VariableVO variableVO:entry.getValue().getVariableList()){
                    variableVO.setFrequencyCode(1l);
                    variableDAO.updateVariableValue(variableVO);
                }
            }
        }

        if(!CollectionUtils.isEmpty(strategyRuleSetListToAdd))
            strategyDAO.relateRuleSetList(strategyRuleSetListToAdd);
        if(!CollectionUtils.isEmpty(ruleSetRuleListToAdd))
            ruleSetDAO.relateRuleList(ruleSetRuleListToAdd);
        if(!CollectionUtils.isEmpty(variableVOListToAdd))
            variableDAO.addVariableList(variableVOListToAdd);
        count = taskDAO.updateTaskNameBCDescriptionStrategyId(task);
        return count;
    }

    @Override
    public long updateTaskStatus(TaskVO taskVO) throws Exception {
        long count = taskDAO.updateTaskStatus(taskVO);
        return count;
    }

    @Override
    public List<TaskVO> findTaskVOListByPage(TaskVO taskVO) throws Exception {
        /*if(StringUtils.isNotEmpty(taskVO.getTask().getCreateTime())){
            DateTime dateTime = DateTime.parse(taskVO.getTask().getCreateTime().split(" ")[0]);
            taskVO.setStartDateStr(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
            DateTime dateTimeEnd = dateTime.plusDays(1);
            taskVO.setEndDateStr(dateTimeEnd.toString("yyyy-MM-dd HH:mm:ss"));
        }*/
        List<TaskVO> list = taskDAO.findListByTaskVO(taskVO);
        return list;
    }

    @Override
    public List<RuleSetVO> findRuleSetListByTask(TaskVO task) throws Exception {
        List<RuleSetVO> list = new ArrayList<>();
        Map<String,RuleSetVO> ruleSetNameMap = new LinkedHashMap<>();
        List<RuleSetVO> ruleSetList =  taskDAO.findRuleSetListByTask(task);
        ruleSetList.forEach(ruleSet -> {
            if(!ruleSetNameMap.containsKey(ruleSet.getRuleSetName())){
                ruleSetNameMap.put(ruleSet.getRuleSetName(),ruleSet);
            }else{
                Integer priority = ruleSet.getPriority();
                Integer oldPriority = ruleSetNameMap.get(ruleSet.getRuleSetName()).getPriority();
                if(priority<oldPriority){
                    ruleSetNameMap.put(ruleSet.getRuleSetName(),ruleSet);
                }
            }
        });
        ruleSetNameMap.values().forEach(ruleSet -> {
            list.add(ruleSet);
        });
        return list;
    }

    @Override
    public TaskVO findTaskVOByTaskVO(TaskVO taskVO) throws Exception {
        //TaskVO returnTaskVO = new TaskVO();
        TaskVO task = taskDAO.findByTask(taskVO);
        if(null!=task){
            //BeanUtils.copyProperties(taskVO,returnTaskVO);
            //returnTaskVO.setTask(task);
            if(null==taskVO.getWithStrategyInfo()||StringUtils.equals("1",taskVO.getWithStrategyInfo().toString())){
                RuleSetVORuleSetCodeComparator codeComparator = new RuleSetVORuleSetCodeComparator();
                StrategyVO returnStrategyVO = strategyService.findStrategy(task.getStrategyId(),task.getUserId(),taskVO.getApiCode());
                if(null!=returnStrategyVO&&!CollectionUtils.isEmpty(returnStrategyVO.getRuleSetVOList())){
                    Collections.sort(returnStrategyVO.getRuleSetVOList(),codeComparator);
                }
                task.setStrategyVO(returnStrategyVO);
                int count = SysDict.TASK_STATUS_ACTIVE!=task.getTaskStatus()?0:entityDAO.findCountByTaskId(task.getTaskId().intValue());
                task.setEntityCount(count);
            }
        }
        return task;
    }

    @Override
    public TaskVO findTaskByTaskIdAndUserId(Integer taskId,Integer userId) throws Exception {
        TaskVO query = new TaskVO();
        query.setTaskId(taskId.longValue());
        if(null!=userId)
            query.setUserId(userId.longValue());
        TaskVO task = taskDAO.findByTask(query);
        return task;
    }

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
        retSource.setStrategyCode(redisUtil.generateCodeNo("SGY"));
        retSource.setBusinessCode(retSource.getStrategyCode().replaceAll("SGY","SGYBC"));
        retSource.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
        retSource.setUserId(target.getUserId());
        retSource.setApiCode(target.getApiCode());
        count = count + strategyDAO.addStrategy(retSource);
        //retSource.setSourceStrategyId(retSource.getStrategyId());
        List<StrategyRuleSet> strategyRuleSetList = new ArrayList<>();
        List<RulesetRule> rulesetRuleList = new ArrayList<>();
        List<VariableVO> variableVOList = new ArrayList<>();
        if(!CollectionUtils.isEmpty(retSource.getRuleSetVOList())){
            retSource.getRuleSetVOList().forEach(ruleSetVO -> {
                ruleSetVO.setRuleSetId(null);
                ruleSetVO.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
                ruleSetVO.setUserId(target.getUserId());
                ruleSetVO.setApiCode(target.getApiCode());
                ruleSetVO.setOrigin(0);
                ruleSetDAO.addRuleSet(ruleSetVO);
                strategyRuleSetList.add(new StrategyRuleSet(retSource.getStrategyId(),ruleSetVO.getRuleSetId()));
                if(!CollectionUtils.isEmpty(ruleSetVO.getRuleVOList())){
                    List<String> ruleCodeList = redisUtil.generateCodeNos("RULE",ruleSetVO.getRuleVOList().size());
                    Iterator<String> it = ruleCodeList.iterator();
                    ruleSetVO.getRuleVOList().forEach(ruleVO -> {
                        ruleVO.setRuleId(null);
                        ruleVO.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
                        ruleVO.setRuleCode(it.next());
                        ruleVO.setBusinessCode(ruleVO.getRuleCode().replaceAll("RULE","RULEBC"));
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
                                variableVO.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
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
        count = count + ruleSetDAO.updateRuleSetForCopyStrategy(new RuleSetVO(target.getUserId(),target.getApiCode()));
        count = count + ruleDAO.updateRuleForCopyStrategy(new RuleVO(target.getUserId(),target.getApiCode()));
        return count;
    }

    @Override
    public JSONObject getTasks(String apiCode) {
        JSONObject tasks = new JSONObject();
        List<ParamVO> vos = taskDAO.queryTasksByApiCode(apiCode);
        if (!CollectionUtils.isEmpty(vos)) {
            JSONArray data = new JSONArray();
            Integer taskId = null;
            JSONArray params = null;
            for (ParamVO vo : vos) {
                if (!Objects.equals(taskId, vo.getTaskId())) {
                    taskId = vo.getTaskId();
                    JSONObject task = new JSONObject();
                    task.put("taskId", taskId);
                    task.put("taskName", vo.getTaskName());
                    params = new JSONArray();
                    task.put("taskParams", params);
                    data.add(task);
                }
                JSONObject param = new JSONObject();
                param.put("code", vo.getCode());
                param.put("name", vo.getName());
                param.put("type", vo.getType());
                param.put("required", vo.getRequired());
                params.add(param);
            }
            tasks.put("taskList", data);
        }
        return tasks;
    }

    @Override
    @Transactional
    public JSONObject startMonitor(Map<String, Object> request) throws Exception {
        JSONObject object = new JSONObject();
        object.put("code", "03");
        object.put("msg", "Params error!");
        //
        String jsonData = JSON.toJSONString(request);
        log.info("参数信息: {}", jsonData);
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String apiCode = jsonObject.getString("apiCode");
        Long taskId = jsonObject.getLong("taskId");
        JSONObject params = jsonObject.getJSONObject("params");
        //参数校验
        if (StringUtils.isBlank(apiCode) || taskId == null || params == null) {
            log.error("apiCode|taskId|进件参数信息不能为空!!");
            return object;
        }
        String companyName = params.getString(SysDict.COMPANY_NAME);
        String creditCode = params.getString(SysDict.CREDIT_CODE);
        String name = params.getString(SysDict.NAME);
        String idNo = params.getString(SysDict.ID_NO);
        String telNo = params.getString(SysDict.CELL);
        String bankId = params.getString(SysDict.BANK_ID);
        String homeAddr = params.getString(SysDict.HOME_ADDR);
        String workAddr = params.getString(SysDict.WORK_ADDR);
        String _applyDate = params.getString(SysDict.APPLY_DATE);
        if (StringUtils.isBlank(_applyDate)) {
            _applyDate = LocalDate.now().toString();
        }
        //到期日期默认值
        String _expireDate = "9999-12-31";

        LocalDate applyDate = DateUtils.parse(_applyDate);
        LocalDate expireDate = DateUtils.parse(_expireDate);
        if (StringUtils.isBlank(companyName)) {
            log.error("企业名称不能为空!!");
            object.put("msg", "[" + SysDict.COMPANY_NAME + "]不能为空!!");
            return object;
        }
        Integer count = taskDAO.queryEntityCount(apiCode, companyName);
        if (count > 0) {
            log.warn("企业[{}]正处于监控中, 无需重复提交!!", companyName);
            object.put("code", "00");
            object.put("msg", "Success");
            return object;
        }
        //获取任务信息
        TaskVO param = new TaskVO(taskId);
        param.setApiCode(apiCode);
        TaskVO taskVO = findTaskVOByTaskVO(param);
        if (taskVO == null) {
            log.error("客户[{}]任务[{}]信息未获取到!!", apiCode, taskId);
            return object;
        }
        int userId = -1;
        //保存进件
        Entity entity = new Entity();
        entity.setEntityName(companyName);
        entity.setCompanyName(companyName);
        entity.setCreditCode(creditCode);
        entity.setPersonId(idNo);
        entity.setLegalPerson(name);
        entity.setCell(telNo);
        entity.setBankId(bankId);
        entity.setHomeAddr(homeAddr);
        entity.setBizAddr(workAddr);
        entity.setApplicationDate(applyDate.toString());
        entity.setExpireTime(expireDate.toString());
        entity.setApiCode(apiCode);
        entity.setParams(params.toJSONString());
        entity.setUserId(Long.valueOf(userId));
        entity.setUploadTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
        List<Entity> entities = new ArrayList<>();
        entities.add(entity);
        entityService.saveEntityList(entities);
        //
        Task task = new Task();
        task.setApiCode(apiCode);
        task.setUserId(userId);
        task.setTaskName(taskVO.getTaskName());
        task.setStrategyId(taskVO.getStrategyId().intValue());
        task.setTaskId(taskId.intValue());
        TaskInput taskInput = new TaskInput();
        taskInput.setInputId(entity.getEntityId().intValue());
        taskInput.setCompanyName(entity.getCompanyName());
        taskInput.setExpireTime(entity.getExpireTime());
        taskInput.setLegalPerson(entity.getLegalPerson());
        taskInput.setLegalPersonID(entity.getPersonId());
        taskInput.setLegalPersonPhone(entity.getCell());
        taskInput.setInputDate(entity.getUploadTime().split(" ")[0]);
        taskInput.setLegalPersonHomeAddr(entity.getHomeAddr());
        taskInput.setLegalPersonWorkAddr(entity.getBizAddr());
        taskInput.setCompanyBankId(entity.getBankId());
        taskInput.setCreditCode(entity.getCreditCode());
        taskInput.setAppId(entity.getParentAppId());
        taskInput.setApplyDate(entity.getApplicationDate());
        taskInput.setParams(params);
        task.addTaskInput(taskInput);
        bizTask.issue(task);
        //
        object.put("code", "00");
        object.put("msg", "Success");
        return object;
    }
}
