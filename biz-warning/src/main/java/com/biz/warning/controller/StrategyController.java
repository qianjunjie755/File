package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.controller.BaseController;
import com.biz.strategy.entity.TaskApi;
import com.biz.strategy.entity.TaskRules;
import com.biz.strategy.entity.TaskStrategy;
import com.biz.strategy.service.IInitService;
import com.biz.warning.domain.Rule;
import com.biz.warning.domain.Strategy;
import com.biz.warning.service.IRuleSetService;
import com.biz.warning.service.IStrategyService;
import com.biz.warning.service.ITaskService;
import com.biz.warning.util.RedisUtil;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.biz.warning.util.SysDict;
import com.biz.warning.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
public class StrategyController extends BaseController {

    @Autowired
    private IStrategyService strategyService;

    @Autowired
    private IRuleSetService ruleSetService;

    private String ruleVORedisKey = "{biz:warning:hash}:";

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private IInitService initService;

    @Autowired
    private ITaskService taskService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Value("{biz.task.all-strategy-key}")
    private String varThresholdKey;


    @GetMapping(value = "/strategy/varThreshold/list")
    public JSONObject queryFlowVarThreshold(@RequestParam(name = "setId") String strategyId){
        JSONObject jsonObject = new JSONObject();

        String key = varThresholdKey.replaceAll("warning","varThreshold");
        HashOperations<String,String,VarSetVO> hashOps = redisTemplate.opsForHash();
        VarSetVO varSetRedisVO = hashOps.get(key,strategyId);
        if(null==varSetRedisVO){
            TaskStrategy strategy = initService.getStrategy(Integer.parseInt(strategyId));

            if(null!=strategy){
                List<TaskApi> apis = initService.getUserRuleApis(strategy);
                Map<String,String> apiMap = new HashMap<>();
                apis.forEach(api->{
                    apiMap.put(api.getApiProdCode(),api.getApiVarset());
                });
                Set<Integer> hashSet = new HashSet<>();
                VarSetVO varSetVO = new VarSetVO();
                varSetVO.setName(strategy.getStrategyName());
                varSetVO.setSetId(strategy.getStrategyId());
                List<TaskRules> strategyRules = strategy.getStrategyRules();
                if(!CollectionUtils.isEmpty(strategyRules)){
                    strategyRules.forEach(taskRules -> {
                        if(!CollectionUtils.isEmpty(taskRules.getRulesRule())){
                            taskRules.getRulesRule().forEach(rule->{
                                if(!CollectionUtils.isEmpty(rule.getRuleVars())){
                                    rule.getRuleVars().forEach(ruleVar->{
                                        VarVO varVO = new VarVO();
                                        VarLogicVO varLogicVO = new VarLogicVO();
                                        ruleVar.getVarThreshold();
                                        VarThresholdVO varThresholdVO = new VarThresholdVO(
                                                ruleVar.getVarCode(),ruleVar.getVarVersion(),apiMap.get(taskRules.getApiProdCode()),taskRules.getApiVersion(),
                                                ruleVar.getThd().getType(),
                                                ruleVar.getThd().getLt(),
                                                ruleVar.getThd().getGt(),
                                                ruleVar.getThd().getEq(),
                                                ruleVar.getVarThreshold(),ruleVar.getVarInterval(),
                                                null,null);
                                        varThresholdVO.makeLogicSign();
                                        varLogicVO.getVarThresholdVOList().add(varThresholdVO);
                                        varVO.getVarLogicVOList().add(varLogicVO);
                                        varVO.setLogic(1);
                                        Integer hashCode = (JSONObject.toJSONString(varVO)).hashCode();
                                        if(!hashSet.contains(hashCode)){
                                            hashSet.add(hashCode);
                                            varSetVO.getVarVOList().add(varVO);
                                        }
                                    });
                                }
                            });
                        }

                    });
                }
                jsonObject.put("code", "00");
                jsonObject.put("msg", "Success");
                jsonObject.put("data", varSetVO);
                redisTemplate.opsForHash().put(key,strategyId,varSetVO);
                redisTemplate.expire(key, 1, TimeUnit.DAYS);
            }else{
                jsonObject.put("code", "01");// 没有结果
                jsonObject.put("msg", "No result");
            }
            return jsonObject;
        }
        jsonObject.put("code", "00");
        jsonObject.put("msg", "Success");
        jsonObject.put("data", varSetRedisVO);
        redisTemplate.expire(key, 1, TimeUnit.DAYS);
        return jsonObject;
    }



    /**
     * 新增策略(包含：策略、规则集、规则)
     *
     * @param strategyVO
     * @return
     */
    @PostMapping(value = "/strategy")
    @ResponseBody
    public RespEntity addStrategy(@RequestBody StrategyVO strategyVO, HttpSession session){
        Long userId = getUserId();
        String apiCode = getApiCode();
        strategyVO.setUserId(userId);
        strategyVO.setApiCode(apiCode);
        String key = ruleVORedisKey.concat(apiCode).concat(":").concat(String.valueOf(userId)).concat(":").concat(session.getId());
        strategyService.addStrategy(strategyVO,key);
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        return entity;
    }

    /**
     * 修改策略(包含：策略、规则集、规则)
     * @param strategyVO
     * @return
     */
    @PutMapping(value = "/strategy")
    @ResponseBody
    public RespEntity updateStrategy(@RequestBody StrategyVO strategyVO, HttpSession session){
        Long userId = getUserId();
        String apiCode = getApiCode();
        strategyVO.setUserId(userId);
        strategyVO.setApiCode(apiCode);
        String key = ruleVORedisKey.concat(apiCode).concat(":").concat(String.valueOf(userId)).concat(":").concat(session.getId());
        long result = strategyService.updateStrategy(strategyVO,key);
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        if(result == -1){
            entity.setCode(RespCode.ERROR.getCode());
            entity.setMessage("该策略已被其他任务引用，无法修改");
        }
        return entity;
    }

    /**
     * 修改策略状态
     * @param strategyId
     * @param strategyStatus
     * @return
     */
    @PutMapping(value = "/strategy/{strategyId}/strategyStatus/{strategyStatus}")
    @ResponseBody
    public RespEntity updateStrategyState(@PathVariable("strategyId")Long strategyId,
                                          @PathVariable("strategyStatus")Long strategyStatus){
        strategyService.updateStrategyState(strategyId, strategyStatus);
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        return entity;
    }

    /**
     * 修改策略模板状态
     * @param strategyId
     * @param strategyTemplateStatus
     * @return
     */
    @PutMapping(value = "/strategy/{strategyId}/strategyTemplateStatus/{strategyTemplateStatus}")
    @ResponseBody
    public RespEntity updateStrategyTemplateState(@PathVariable("strategyId")Long strategyId,
                                          @PathVariable("strategyTemplateStatus")Long strategyTemplateStatus){
        strategyService.updateStrategyTemplateState(strategyId, strategyTemplateStatus);
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        return entity;
    }

    /**
     * 根据策略编号读取策略信息(返回策略表信息  单表)
     * @param strategyId
     * @return
     */
    @GetMapping(value = "/strategy/{strategyId}")
    @ResponseBody
    public RespEntity findStrategy(@PathVariable("strategyId")Long strategyId,HttpSession session){
        String apiCode = getApiCode();
        Long userId = getUserId();
        String sessionId = session.getId();
        HashOperations<String,String,RuleVO> opsForHash = redisTemplate.opsForHash();
        String key = ruleVORedisKey.concat(apiCode).concat(":").concat(userId.toString()).concat(":").concat(sessionId);
        StrategyVO vo = strategyService.findStrategy(strategyId,null,apiCode);
        //清除变量缓存
        for (RuleSetVO ruleSetVO : vo.getRuleSetVOList()) {
            for (RuleVO ruleVO : ruleSetVO.getRuleVOList()) {
                opsForHash.delete(key,ruleVO.getRuleId().toString());
            }
        }
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.setData(vo);
        return entity;
    }

    /**
     * 根据策略编号读取策略下的所有规则集(有模板也有实例)
     * @param strategyId
     * @return
     */
    @GetMapping(value = "/strategy/ruleSet/{strategyId}")
    @ResponseBody
    public RespEntity findRuleStrategy(@PathVariable("strategyId")Long strategyId){
        String apiCode = getApiCode();
        StrategyVO vo = strategyService.findRuleSetsByStrategy(strategyId,null,apiCode);
        RespEntity entity = new RespEntity(RespCode.ERROR,null);
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.changeRespEntity(RespCode.SUCCESS,vo.getRuleSetVOList());
        return entity;
    }


    /**
     * 根据策略id读取策略下的所有规则集(用于策略管理左侧导航)
     * @param strategyId
     * @return
     */
    @GetMapping(value = "/strategy/ruleSetList/{strategyId}")
    @ResponseBody
    public RespEntity findRuleSetListStrategy(@PathVariable("strategyId")Long strategyId){
        List<RuleSetVO> ruleSetVOList = strategyService.findRuleSetListByStrategyId(strategyId);
        RespEntity entity = new RespEntity(RespCode.ERROR,null);
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.changeRespEntity(RespCode.SUCCESS,ruleSetVOList);
        return entity;
    }


    /**
     * 读取所有策略模板信息
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/strategies")
    @ResponseBody
    public RespEntity findAllStrategy(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "99999999") Integer pageSize,
                                      HttpSession session){
        String apiCode = getApiCode();
        Page<Rule> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        StrategyVO strategy = new StrategyVO();
        strategy.setApiCode(apiCode);
        strategy.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);   // 只查询模板
        try {
            strategyService.findAllStrategy(strategy);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            log.info("查找规则版本接口信息失败");
            return entity;
        }
    }


    /**
     * 读取所有策略模板信息(策略模块导航)
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/strategy/navlist")
    @ResponseBody
    public RespEntity findStrategyNavList(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "99999999") Integer pageSize,
                                      HttpSession session){
        String apiCode = getApiCode();
        Page<Rule> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        StrategyVO strategy = new StrategyVO();
        strategy.setApiCode(apiCode);
        try {
            strategyService.findAllStrategyNotTask(strategy);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            log.info("查找规则版本接口信息失败");
            return entity;
        }
    }

    /**
     * 根据参数查询策略（策略管理搜索）
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/strategy/findByParam")
    @ResponseBody
    public RespEntity findByParam(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                          @RequestParam(value = "pageSize",required = false,defaultValue = "99999999") Integer pageSize,
                                          @RequestParam(value = "param",required = false,defaultValue = "") String param,
                                          HttpSession session){
        String apiCode = getApiCode();
        Page<Rule> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        StrategyVO strategy = new StrategyVO();
        strategy.setApiCode(apiCode);
        try {
            strategyService.findStrategyByParam(strategy,param);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            log.info("根据参数查询策略信息失败");
            return entity;
        }
    }

    /**
     * 获取策略编号
     * @param
     * @return
     */
    @GetMapping(value = "/strategy/strategyCode")
    @ResponseBody
    private RespEntity getStrategyCode() {
        RespEntity entity = new RespEntity();
        try {
            String ruleCode = redisUtil.generateCodeNo("SGY");
            entity.changeRespEntity(RespCode.SUCCESS,ruleCode);
            return entity;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            log.info("获取策略编号错误");
            return entity;
        }
    }

    /**
     * 按规则集查找规则
     *
     * @param
     * @return
     */
    @GetMapping(value = "/strategy/rule/ruleSet/{ruleSetId}")
    @ResponseBody
    private RespEntity findRulesByRuleSet(@PathVariable("ruleSetId")Long ruleSetId,
                                          RuleSetVO ruleSetVO,
                                          HttpSession session) {
        String apiCode = getApiCode();
        RespEntity entity = new RespEntity();
        ruleSetVO.setRuleSetId(ruleSetId);
        ruleSetVO.setApiCode(apiCode);
        try {
            List<RuleVO> ruleVOList = ruleSetService.findActiveRuleByRuleSet(ruleSetVO);
            entity.changeRespEntity(RespCode.SUCCESS,ruleVOList);
            return entity;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            log.info("按规则集查找规则信息失败");
            return entity;
        }
    }



    @RequestMapping("/strategy/generate")
    public RespEntity generateClientStrategy(@RequestParam(value = "strategyId",required = true) Long strategyId,
                                             @RequestParam(value = "apiCode",required = true) String apiCode,
                                             @RequestParam(value = "userId",required = true) Long userId,
                                             HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String bairongApiCode = session.getAttribute(SysDict.API_CODE).toString();
        Long bairongUserId = Long.parseLong(session.getAttribute(SysDict.USER_ID).toString());
        StrategyVO source = new StrategyVO(strategyId,bairongApiCode,bairongUserId);
        StrategyVO target = new StrategyVO(null,apiCode,userId);
        try {
            long count = taskService.copyStrategyTemplate(source, target);
            count = count + taskService.updateSrcIdForCopyStrategyTemplate(target);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count",count);
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }
}
