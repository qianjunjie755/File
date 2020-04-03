package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.RuleOperLog;
import com.biz.warning.domain.Variable;
import com.biz.warning.service.IRuleOperLogService;
import com.biz.warning.service.IRuleService;
import com.biz.warning.service.IRuleSetService;
import com.biz.warning.service.IVariableService;
import com.biz.warning.util.RedisUtil;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.biz.warning.util.SysDict;
import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.RuleVO;
import com.biz.warning.vo.VariableVO;
import com.biz.controller.BaseController;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class RuleController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IRuleService ruleService;
    @Autowired
    private IRuleSetService ruleSetService;
    @Autowired
    private IVariableService variableService;
    @Autowired
    private IRuleOperLogService ruleOperLogService;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 新增规则接口
     *
     * @param
     * @return
     */
    @PostMapping(value = "/rule")
    @ResponseBody
    public RespEntity addRule(@RequestBody RuleVO ruleVO) {
        Long userId = getUserId();
        String apiCode = getApiCode();
        String useName  = getUserName();
        ruleVO.setUserName(useName);
        ruleVO.setApiCode(apiCode);
        ruleVO.setUserId(userId);
        ruleVO.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);   //规则模块新增的都是模板
        ruleVO.setRuleState(SysDict.RULE_STATUS_DRAFT);   //新增的规则为草稿状态
        for(VariableVO variable : ruleVO.getVariableList()){
            String threshold = variableService.findThresholdByVariableCode(variable.getVariableCode());
            variable.setThreshold(threshold);
            variable.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
            variable.setUserId(userId);
            variable.setApiCode(apiCode);
        }
        return ruleService.addRule(ruleVO);
    }

    /**
     * 查找规则接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rule/{ruleId}")
    @ResponseBody
    public RespEntity findRule(@PathVariable("ruleId")Long ruleId,HttpSession session) {
        String apiCode = getApiCode();
        RuleVO ret = ruleService.findRule(ruleId,null,apiCode);
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.setData(ret);
        return entity;
    }

    /**
     * 查找变量详情
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rule/variable/{ruleId}")
    @ResponseBody
    public RespEntity findVariable(@PathVariable("ruleId")Long ruleId,HttpSession session) {
        List<VariableVO> variableVOList = ruleService.findVariableByRuleId(ruleId);
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.setData(variableVOList);
        return entity;
    }

    /**
     * 更新规则接口
     *
     * @param
     * @return
     */
    @PutMapping(value = "/rule")
    @ResponseBody
    public RespEntity updateRule(@RequestBody RuleVO ruleVO, HttpSession session) {
        Long userId = getUserId();
        String userName  = getUserName();
        String apiCode = getApiCode();
        ruleVO.setUserName(userName);
        ruleVO.setApiCode(apiCode);
        ruleVO.setUserId(userId);
        for(VariableVO variable : ruleVO.getVariableList()){
            String threshold = variableService.findThresholdByVariableCode(variable.getVariableCode());
            variable.setThreshold(threshold);
            variable.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
            variable.setUserId(userId);
            variable.setApiCode(apiCode);
        }
        return ruleService.updateRule(ruleVO);
    }


    /**
     * 规则生效
     *
     * @param
     * @return
     */
    @PutMapping(value = "/rule/effective")
    @ResponseBody
    public RespEntity updateRuleState(@RequestBody RuleVO ruleVO, HttpSession session) {
        Long userId = getUserId();
        String apiCode = getApiCode();
        String useName  = getUserName();
        ruleVO.setUserName(useName);
        ruleVO.setApiCode(apiCode);
        ruleVO.setUserId(userId);
        for(Variable variable : ruleVO.getVariableList()){
            String threshold = variableService.findThresholdByVariableCode(variable.getVariableCode());
            variable.setThreshold(threshold);
            variable.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
            variable.setUserId(userId);
            variable.setApiCode(apiCode);
        }
        long ruleId = ruleService.effectiveRule(ruleVO);

        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.setData(ruleId);
        return entity;
    }

    /**
     * 查找规则版本接口(按ruleId)
     *
     * @param
     * @return
     */
    @GetMapping(value = "/ruleVersion/ruleId/{ruleId}")
    @ResponseBody
    public RespEntity findRuleVersionByRuleId(@PathVariable("ruleId")Long ruleId,HttpSession session) {
        Long userId = getUserId();
        String apiCode = getApiCode();
        RespEntity entity = new RespEntity();
        RuleVO ruleVO = new RuleVO(ruleId);
        ruleVO.setUserId(userId);
        ruleVO.setApiCode(apiCode);
        try {
            List<RuleVO> ruleList = ruleService.findRuleVersionByRuleId(ruleVO);
            entity.changeRespEntity(RespCode.SUCCESS,ruleList);
            return entity;
        }catch (Exception e){
            logger.info("查找规则版本接口信息失败",e);
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 查找规则版本接口(按ruleCode)
     *
     * @param
     * @return
     */
    @GetMapping(value = "/ruleVersion/ruleCode/{ruleCode}")
    @ResponseBody
    public RespEntity findRuleVersionByRuleCode(@PathVariable("ruleCode")String ruleCode,HttpSession session) {
        RespEntity entity = new RespEntity();
        String apiCode = getApiCode();
        try {
            List<RuleVO> ruleList = ruleService.findRuleVersionByRuleCode(ruleCode,apiCode);
            entity.changeRespEntity(RespCode.SUCCESS,ruleList);
            return entity;
        }catch (Exception e){
            logger.info("查找规则版本接口(按ruleCode)",e);
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 查找规则版本接口(按ruleCode)
     *
     * @param
     * @return
     */
    @GetMapping(value = "/allRuleVersion")
    @ResponseBody
    public RespEntity findAllRuleVersion() {
        RespEntity entity = new RespEntity();
        try {
            Map<String,List<RuleVO>> ruleList = ruleService.findAllRuleVersion();
            entity.changeRespEntity(RespCode.SUCCESS,ruleList);
            return entity;
        }catch (Exception e){
            logger.info("查找规则版本接口(按ruleCode)",e);
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 规则推广/取消推广
     *
     * @param
     * @return
     */
    @PutMapping(value = "/rule/extension/{ruleId}/{ruleState}")
    @ResponseBody
    public RespEntity extensionRule(@PathVariable("ruleId")Long ruleId,@PathVariable("ruleState") Long ruleState,HttpSession session) {
        Long userId = getUserId();
        String apiCode = getApiCode();
        String userName  = getUserName();
        RespEntity entity = new RespEntity();
        try {
            Long res = ruleService.updateRuleState(ruleId,ruleState);
            //写入规则操作日志
            RuleOperLog log = new RuleOperLog();
            log.setOperContent(ruleState==2?SysDict.RULE_OPER_LOG_CANCEL_EXTENSION:SysDict.RULE_OPER_LOG_EXTENSION);
            log.setRuleId(ruleId);
            log.setUserId(userId);
            log.setUserName(userName);
            log.setApiCode(apiCode);
            ruleOperLogService.addRuleOperLog(log);
            entity.changeRespEntity(RespCode.SUCCESS,res);
            return entity;
        }catch (Exception e){
            logger.info("查找规则版本接口信息失败",e);
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 查询最大版本号
     *
     * @param
     * @return
     */
    @GetMapping(value = "/maxRuleVersion/ruleCode/{ruleCode}")
    @ResponseBody
    public RespEntity queryMaxRuleVersion(@PathVariable("ruleCode") String ruleCode) {
        RespEntity entity = new RespEntity();
        try {
            Float res = ruleService.findMaxRuleVersion(ruleCode);
            entity.changeRespEntity(RespCode.SUCCESS,res);
            return entity;
        }catch (Exception e){
            logger.info("查找规则版本接口信息失败",e);
            e.printStackTrace();
            return entity;
        }
    }


    /**
     * 查找所有规则（不分版本）
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rules")
    @ResponseBody
    public RespEntity findRules(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                HttpSession session) {
        String apiCode = getApiCode();
        Page<RuleVO> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        try {
            RuleVO rule = new RuleVO();
            rule.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
            rule.setApiCode(apiCode);
            ruleService.findRules(rule);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            logger.info("查询所有任务信息失败");
            e.printStackTrace();
            return entity;
        }

    }

    /**
     * 查找所有规则（全部版本）
     *
     * @param
     * @return
     */
    @GetMapping(value = "/allRule")
    @ResponseBody
    public RespEntity findAllRule(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                  @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                  HttpSession session) {
        String apiCode = getApiCode();
        Page<RuleVO> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        try {
            RuleVO rule = new RuleVO();
            rule.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
            rule.setApiCode(apiCode);
            ruleService.findAllRule(rule);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            logger.info("查找所有规则（全部版本）信息失败");
            e.printStackTrace();
            return entity;
        }

    }

    /**
     * 按规则集查找规则
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rule/ruleSet/{ruleSetId}")
    @ResponseBody
    private RespEntity findRulesByRuleSet(@PathVariable("ruleSetId")Long ruleSetId,
                                          @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                          @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                          @RequestParam(value = "requireAll",required = false,defaultValue = "1") String requireAll,
                                          RuleSetVO ruleSetVO,
                                          HttpSession session) {
        String apiCode = getApiCode();
        Page<RuleVO> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        ruleSetVO.setRuleSetId(ruleSetId);
        ruleSetVO.setApiCode(apiCode);
        try {
            ruleSetService.findRulesByRuleSet(ruleSetVO);
            JSONObject jo = new JSONObject();
            RuleVO rule = new RuleVO();
            rule.setRuleId(0L);
            rule.setRuleName("全部");
            if(page.getTotal()>0){
                if(StringUtils.equals(NumberUtils.INTEGER_ONE.toString(),requireAll)) {
                    page.getResult().add(0,rule);
                }
                jo.put("total",page.getTotal());
                jo.put("rows",page.getResult());
            }else{
                List<RuleVO> ruleList = new ArrayList<>();
                if(StringUtils.equals(NumberUtils.INTEGER_ONE.toString(),requireAll)) {
                    ruleList.add(rule);
                }
                jo.put("total",page.getTotal());
                jo.put("rows",ruleList);
            }
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("按规则集查找规则信息失败");
            return entity;
        }
    }

    /**
     * 获取规则编号
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rule/ruleCode")
    @ResponseBody
    private RespEntity getRuleCode() {
        RespEntity entity = new RespEntity();
        try {
            String ruleCode = redisUtil.generateCodeNo("RULE");
            entity.changeRespEntity(RespCode.SUCCESS,ruleCode);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("获取规则编号错误");
            return entity;
        }

    }

    /**
     * 获取规则编号
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rule/ruleCodeAndVersion/{ruleCode}/{version}")
    @ResponseBody
    private RespEntity getRuleByRuleIdAndVersion(@PathVariable("ruleCode") String ruleCode,@PathVariable("version") Double version,HttpSession session) {
        String apiCode = getApiCode();
        RespEntity entity = new RespEntity();
        try {
            RuleVO rule = ruleService.findRuleByRuleCodeAndVersion(ruleCode,version,apiCode);
            entity.changeRespEntity(RespCode.SUCCESS,rule);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("获取规则编号错误");
            return entity;
        }

    }

    /**
     * 校验规则集下规则名称是否重复
     *
     * @param
     * @return
     */
    @GetMapping(value = "/rule/checkRuleName/{ruleSetId}/{ruleCode}/{ruleName}")
    @ResponseBody
    private RespEntity checkRuleNameByRuleSetId(@PathVariable("ruleSetId") Integer ruleSetId,@PathVariable("ruleCode") String ruleCode,@PathVariable("ruleName") String ruleName) {
        RespEntity entity = new RespEntity();
        try {
            boolean result = ruleService.checkRuleNameByRuleSetId(ruleSetId,ruleCode,ruleName);
            entity.changeRespEntity(RespCode.SUCCESS,result);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("校验规则集下规则名称是否重复");
            return entity;
        }

    }

}


