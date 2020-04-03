package com.biz.warning.service.impl;

import com.biz.warning.dao.RuleDAO;
import com.biz.warning.dao.RuleOperLogDAO;
import com.biz.warning.dao.RuleSetDAO;
import com.biz.warning.dao.VariableDAO;
import com.biz.warning.domain.RuleOperLog;
import com.biz.warning.domain.RuleSet;
import com.biz.warning.domain.RulesetRule;
import com.biz.warning.service.IRuleService;
import com.biz.warning.util.*;
import com.biz.warning.vo.RuleVO;
import com.biz.warning.vo.VariableVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class RuleServiceImpl implements IRuleService {
    @Autowired
    private RuleDAO ruleDAO;
    @Autowired
    private RuleSetDAO ruleSetDAO;
    @Autowired
    private VariableDAO variableDAO;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RuleOperLogDAO ruleOperLogDAO;


    /**
     * 新增规则
     * @param rule
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    @Override
    public RespEntity addRule(RuleVO rule) {
        //Rule rule = ruleVO.getRule();
        if(StringUtils.isEmpty(rule.getRuleCode())){
            rule.setRuleCode(redisUtil.generateCodeNo("RULE"));
        }
        RuleSet ruleSet = rule.getRuleSet();
        rule.setApiProdCode(ruleSet.getApiProdCode());
        rule.setApiVersion(String.valueOf(ruleSet.getApiVersion()));
        rule.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);   //规则模块新增的都是模板
        rule.setRuleState(SysDict.RULE_STATUS_DRAFT);   //新增的规则为草稿状态
        RespEntity checkResult = checkParams(rule,true);
        if(!RespCode.SUCCESS.getCode().equals(checkResult.getCode())){
            return checkResult;
        }
        ruleDAO.addRule(rule);
        long ruleId = rule.getRuleId();
        rule.setSrcRuleId(ruleId);
        rule.setSrcRuleCode(rule.getRuleCode());
        ruleDAO.updateRule(rule);
        RulesetRule rulesetRule = new RulesetRule();
        rulesetRule.setRuleId(ruleId);
        rulesetRule.setRuleSetId(rule.getRuleSet().getRuleSetId());
        ruleSetDAO.relateRule(rulesetRule);

        for(VariableVO variable : rule.getVariableList()){
            variable.setRuleId(ruleId);
            variable.setApiProdCode(rule.getApiProdCode());
            variable.setApiVersion(rule.getApiVersion());
            String now = DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
            variable.setLastUpdateTime(now);
            variable.setCreateTime(now);
            variableDAO.addVariable(variable);
        }
        //写入规则操作日志
        RuleOperLog log = new RuleOperLog();
        log.setOperContent(SysDict.RULE_OPER_LOG_ADD);
        log.setRuleId(ruleId);
        log.setUserId(rule.getUserId());
        log.setUserName(rule.getUserName());
        log.setApiCode(rule.getApiCode());
        ruleOperLogDAO.addRuleOperLog(log);
        return RespEntity.success().setData(ruleId);
    }



    /**
     * 查找规则详细信息
     * @param ruleId
     * @return
     */
    @Override
    public RuleVO findRule(Long ruleId,Long userId,String apiCode) {

        RuleVO resultVO = ruleDAO.findRuleById(ruleId,userId,apiCode);
        VariableVO variable = new VariableVO();
        variable.setRuleId(ruleId);
        List<VariableVO> ruleVariableList = variableDAO.findVariable(variable);
        resultVO.setVariableList(ruleVariableList);
        //查询规则集
        RuleSet ruleSet = ruleSetDAO.findRuleSetByRuleId(ruleId,apiCode);
        resultVO.setRuleSet(ruleSet);
        return resultVO;
    }

    @Override
    public RuleVO findRuleForTask(RuleVO ruleVO) {
        RuleVO resultVO =  null!=ruleVO.getRuleId()?ruleDAO.findRuleById(ruleVO.getRuleId(),null,null):ruleDAO.findRuleForChangeVersion(ruleVO);
        VariableVO variable = new VariableVO();
        variable.setRuleId(resultVO.getRuleId());
        List<VariableVO> ruleVariableList = variableDAO.findVariable(variable);
        resultVO.setVariableList(ruleVariableList);
        //查询该规则所有的版本列表
        List<RuleVO> listRule = ruleDAO.findRuleVersion(null,resultVO.getApiCode(),resultVO.getSrcRuleCode(),null);
        /*listRule.forEach(r->{
            List<VariableVO> vList = variableDAO.findVariableByRule(r);
            r.setVariableList(vList);
        });*/
        resultVO.setVersionList(listRule);
        return resultVO;
    }

    /**
     * 修改规则
     * @param rule
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    @Override
    public RespEntity updateRule(RuleVO rule) {
        return updateRule(rule,false);
    }

    @Override
    public long updateRuleForTask(RuleVO ruleVO) {
        return ruleDAO.updateRule(ruleVO);
    }

    /**
     * 修改规则
     * @param rule
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    @Override
    public RespEntity updateRule(RuleVO rule,boolean cancelOperLog) {
        //RuleVO rule = ruleVO.getRule();
        RespEntity checkResult = checkParams(rule,false);
        if(!RespCode.SUCCESS.getCode().equals(checkResult.getCode())){
            return checkResult;
        }
        ruleDAO.updateRule(rule);
        long ruleId = rule.getRuleId();
        RuleSet ruleSet = rule.getRuleSet();
        rule.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);   //规则模块新增的都是模板
        rule.setRuleState(SysDict.RULE_STATUS_DRAFT);   //新增的规则为草稿状态
        ruleSetDAO.deleteRules(rule);
        RulesetRule rulesetRule = new RulesetRule();
        rulesetRule.setRuleId(ruleId);
        rulesetRule.setRuleSetId(rule.getRuleSet().getRuleSetId());
        ruleSetDAO.relateRule(rulesetRule);
        variableDAO.deleteVariableByRuleId(rule);
        for(VariableVO variable : rule.getVariableList()){
            variable.setRuleId(ruleId);
            variable.setApiProdCode(ruleSet.getApiProdCode());
            variable.setApiVersion(String.valueOf(ruleSet.getApiVersion()));
            String now = DateUtil.parseDateToStr(new Date(),DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
            variable.setLastUpdateTime(now);
            variable.setCreateTime(now);
            variableDAO.addVariable(variable);
        }
        //写入规则操作日志
        if(!cancelOperLog){
            RuleOperLog log = new RuleOperLog();
            log.setOperContent(SysDict.RULE_OPER_LOG_UPDATE);
            log.setRuleId(ruleId);
            log.setUserId(rule.getUserId());
            log.setUserName(rule.getUserName());
            log.setApiCode(rule.getApiCode());
            ruleOperLogDAO.addRuleOperLog(log);
        }
        return RespEntity.success();
    }

    @Override
    public long updateRuleState(Long ruleId, Long ruleState) {
        return ruleDAO.updateRuleState(ruleId, ruleState);
    }

    @Override
    public long updateRuleCalcLogic(RuleVO ruleVO) {
        return ruleDAO.updateRuleCalcLogic(ruleVO);
    }

    @Override
    public List<RuleVO> findRuleVersionByRuleCode(String ruleCode,String apiCode) {
        List<RuleVO> list = ruleDAO.findRuleVersionByRuleCode(ruleCode,apiCode);
        return list;
    }

    @Override
    public Map<String, List<RuleVO>> findAllRuleVersion() {
        List<RuleVO> ruleList = ruleDAO.findAllRuleVersion();
        Map<String,List<RuleVO>> result = new HashMap<>();
        for (RuleVO ruleVO : ruleList) {
            if(!result.containsKey(ruleVO.getRuleCode())){
                result.put(ruleVO.getRuleCode(),new ArrayList<>());
            }
            result.get(ruleVO.getRuleCode()).add(ruleVO);
        }
        return result;
    }

    @Override
    public RuleVO findRuleByRuleCodeAndVersion(String ruleCode, Double version,String apiCode) {
        RuleVO ruleVO = ruleDAO.findRuleByRuleCodeAndVersion(ruleCode,version,apiCode);
        List<VariableVO> variableVOList = variableDAO.findVariableByRule(ruleVO);
        ruleVO.setVariableList(variableVOList);
        List<RuleVO> listRule = ruleDAO.findRuleVersionByRuleCode(ruleCode,apiCode);
        ruleVO.setVersionList(listRule);
        return ruleVO;
    }

    @Override
    public boolean checkRuleNameByRuleSetId(Integer ruleSetId,String ruleCode, String ruleName) {
        RuleVO ruleVO = ruleDAO.findRuleByRuleSetAndRuleName(ruleSetId,ruleCode,ruleName);
        return ruleVO == null;
    }


    @Override
    public List<RuleVO> findRules(RuleVO rule) {
        List<RuleVO> list = ruleDAO.findRules(rule);
        return list;
    }

    @Override
    public List<RuleVO> findAllRule(RuleVO rule) {
        return ruleDAO.findAllRule(rule);
    }

    @Transactional(propagation = Propagation.REQUIRED,isolation = Isolation.DEFAULT,timeout = 3600,rollbackFor={RuntimeException.class, Exception.class})
    @Override
    public long effectiveRule(RuleVO ruleVO) {
        if(ruleVO.getRuleId() == null){
            addRule(ruleVO);
        }else{
            updateRule(ruleVO,true);
        }
        //Rule rule = ruleVO.getRule();
        updateRuleState(ruleVO.getRuleId(),SysDict.RULE_STATUS_INACTIVE);
        //写入规则操作日志
        RuleOperLog log = new RuleOperLog();
        log.setOperContent(SysDict.RULE_OPER_LOG_EFFECTIVE);
        log.setRuleId(ruleVO.getRuleId());
        log.setUserId(ruleVO.getUserId());
        log.setUserName(ruleVO.getUserName());
        log.setApiCode(ruleVO.getApiCode());
        ruleOperLogDAO.addRuleOperLog(log);
        return ruleVO.getRuleId();
    }



    @Override
    public List<VariableVO> findVariableByRuleId(Long ruleId) {
        return ruleDAO.findVariableByRuleId(ruleId);
    }

    @Override
    public List<RuleVO> findRuleVersionByRuleId(RuleVO ruleVO) {
        RuleVO rule = ruleDAO.findRuleById(ruleVO.getRuleId(), null, ruleVO.getApiCode());
        return ruleDAO.findRuleVersionByRuleCode(rule.getRuleCode(),ruleVO.getApiCode());
    }

    @Override
    public Float findMaxRuleVersion(String ruleCode) {
        return ruleDAO.findMaxRuleVersion(ruleCode);
    }

    private RespEntity checkParams(RuleVO rule,boolean isNew) {
        if(rule == null){
            return RespEntity.checkFail("参数错误");
        }
        if(StringUtils.isEmpty(rule.getRuleCode())){
            return RespEntity.checkFail("规则编码不能为空");
        }
        /*if(StringUtils.isEmpty(rule.getBusinessCode())){
            return RespEntity.checkFail("业务编码不能为空");
        }*/
        if(StringUtils.isEmpty(rule.getRuleName())){
            return RespEntity.checkFail("规则名称不能为空");
        }
        if(!checkRuleNameByRuleSetId(rule.getRuleSet().getRuleSetId().intValue(),rule.getRuleCode(),rule.getRuleName())){
            return RespEntity.checkFail("规则名称已存在，请更换");
        }
        if(StringUtils.isEmpty(rule.getVersion())){
            return RespEntity.checkFail("规则版本不能为空");
        }
        if(rule.getCalcLogic() == null){
            return RespEntity.checkFail("计算逻辑不能为空");
        }
        if(isNew){
            if(rule.getUserId() == null){
                return RespEntity.checkFail("规则所属人不能为空");
            }
            if(StringUtils.isEmpty(rule.getApiCode())){
                return RespEntity.checkFail("apiCode不能为空");
            }
            if(rule.getRuleState() == null){
                return RespEntity.checkFail("规则状态不能为空");
            }
            if(rule.getIsTemplate() == null){
                return RespEntity.checkFail("是否为模板字段不能为空");
            }
            if(StringUtils.isEmpty(rule.getApiProdCode())){
                return RespEntity.checkFail("数据源api编码不能为空");
            }
            if(StringUtils.isEmpty(rule.getApiVersion())){
                return RespEntity.checkFail("数据源api版本不能为空");
            }
        }
        return RespEntity.success();
    }
}
