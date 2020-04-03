package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.ICompanySliceService;
import com.biz.credit.service.IStrategyService;
import com.biz.credit.utils.comparators.VarThresholdVOComparator;
import com.biz.credit.vo.VarLogicVO;
import com.biz.credit.vo.VarSetVO;
import com.biz.credit.vo.VarThresholdVO;
import com.biz.credit.vo.VarVO;
import com.biz.decision.entity.Api;
import com.biz.decision.entity.ApiInfo;
import com.biz.decision.entity.EntityBasic;
import com.biz.decision.entity.Flow;
import com.biz.decision.enums.EModel;
import com.biz.decision.enums.EVarType;
import com.biz.decision.model.RuleModel;
import com.biz.decision.service.IDecideService;
import com.biz.decision.service.IInvokeService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
@io.swagger.annotations.Api( tags = "决策流相关业务调用")
@Slf4j
@RestController
@RequestMapping("strategy")
public class StrategyController {

    @Value("${biz.decision.flow-key}")
    private String decisionFlowKey;

    @Autowired
    private IStrategyService service;
    @Autowired
    private IDecideService decideService;
    @Autowired
    private IInvokeService invokeService;
    @Autowired
    private ICompanySliceService sliceService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "查询风控决策流阈值",notes = "决策引擎 <- 大数据平台")
    @ApiImplicitParams({
            @ApiImplicitParam(name="setId",value = "决策流编号",example = "22",required = true,paramType = "query")
    })
    @GetMapping(value = "/varThreshold/list")
    public RespEntity<VarSetVO> queryFlowVarThreshold(@RequestParam(name = "setId") String flowId){
        RespEntity<VarSetVO>  jsonObject = new RespEntity<>();
        String key = decisionFlowKey.replaceAll("decision","varThreshold");
        HashOperations<String,String, VarSetVO> varThresholdOps = redisTemplate.opsForHash();
        VarSetVO varSetVO = varThresholdOps.get(key,flowId);
        if(Objects.isNull(varSetVO)){
            Set<Integer> hashSet = new HashSet<>();
            VarSetVO varSetVO2 = new VarSetVO();
            Flow flow = decideService.getFlow(Integer.parseInt(flowId));
            if(flow==null){
                jsonObject.setCode("01");
                jsonObject.setMsg("No result");
            }else{
                VarThresholdVOComparator varThresholdVOComparator = new VarThresholdVOComparator();
                varSetVO2.setName(flow.getFlowName());varSetVO2.setSetId(flow.getFlowId());
                flow.getNodes().forEach(node->{
                    node.getModels().forEach(model -> {
                        if(model.getType()== EModel.D_E_RULE_MODEL||model.getType()==EModel.D_P_RULE_MODEL){
                            RuleModel ruleModel = (RuleModel) model;
                            ruleModel.getRules().forEach(rule -> {
                                ApiInfo api = invokeService.getApiInfo(new Api(rule.getApiProdCode(), rule.getApiVersion()));
                                rule.getVars().forEach(ruleVar -> {
                                    VarVO varVO = new VarVO();
                                    if(ruleVar.getVarType()== EVarType.LOGIC_VAR){
                                        Map<Integer,List<VarThresholdVO>> varThresholdVOMap = new LinkedHashMap<>();
                                        List<VarThresholdVO> noGroupVarList = new ArrayList<>();
                                        ruleVar.getRefVars().forEach(varRef->{
                                            VarThresholdVO varThresholdVO = new VarThresholdVO(
                                                    varRef.getVarCode(),varRef.getVarVersion(),varRef.getVarName(),
                                                    api.getApiVarset(),api.getApiVarsetVersion(),
                                                    varRef.getJudgeExpr().getType(),varRef.getJudgeExpr().getLt(),
                                                    varRef.getJudgeExpr().getGt(),
                                                    varRef.getJudgeExpr().getEq(),
                                                    varRef.getVarThreshold(),varRef.getVarTime(),
                                                    varRef.getVarMatch(),rule.getSrcRuleId(), rule.getRuleName());
                                            varThresholdVO.makeLogicSign();
                                            if(varRef.getVarGroup()!=null){
                                                if(varThresholdVOMap.get(varRef.getVarGroup())==null){
                                                    varThresholdVOMap.put(varRef.getVarGroup(),new ArrayList<>());
                                                }
                                                varThresholdVOMap.get(varRef.getVarGroup()).add(varThresholdVO);
                                            }else{
                                                noGroupVarList.add(varThresholdVO);
                                            }
                                        });
                                        if(!CollectionUtils.isEmpty(noGroupVarList)){
                                            VarLogicVO varLogicVO = new VarLogicVO();
                                            noGroupVarList.forEach(varThresholdVO->{
                                                varLogicVO.getVarThresholdVOList().add(varThresholdVO);
                                            });
                                            varVO.getVarLogicVOList().add(varLogicVO);
                                        }
                                        if(!CollectionUtils.isEmpty(varThresholdVOMap)){
                                            varThresholdVOMap.keySet().forEach(groupId->{
                                                VarLogicVO varLogicVO = new VarLogicVO();
                                                varLogicVO.setLogic(groupId>0?0:1);
                                                if(varThresholdVOMap.get(groupId).size()==2&&varLogicVO.getLogic()==0&&Objects.equals("compare",varThresholdVOMap.get(groupId).get(0).getType())&&Objects.equals("compare",varThresholdVOMap.get(groupId).get(1).getType())&&Objects.equals(varThresholdVOMap.get(groupId).get(0).getProdCode(),varThresholdVOMap.get(groupId).get(1).getProdCode())){
                                                    VarThresholdVO groupThresholdVO = new VarThresholdVO();
                                                    Collections.sort(varThresholdVOMap.get(groupId),varThresholdVOComparator);
                                                    StringBuffer th = new StringBuffer();
                                                    List<String> small = new ArrayList<>();
                                                    List<String> large = new ArrayList<>();
                                                    varThresholdVOMap.get(groupId).forEach(varThresholdVO -> {
                                                        th.append(varThresholdVO.getThreshold()).append(",");
                                                        if(Objects.nonNull(varThresholdVO.getLt())){
                                                            small.add(varThresholdVO.getThreshold());
                                                            if(Objects.nonNull(varThresholdVO.getEq())){
                                                                small.add(varThresholdVO.getThreshold());
                                                            }
                                                        }else  if(Objects.nonNull(varThresholdVO.getGt())){
                                                            large.add(varThresholdVO.getThreshold());
                                                            if(Objects.nonNull(varThresholdVO.getEq())){
                                                                large.add(varThresholdVO.getThreshold());
                                                            }
                                                        }
                                                    });
                                                    groupThresholdVO.setThreshold(th.substring(0,th.length()-1));
                                                    groupThresholdVO.setType("compare");
                                                    String logicSignSmall = small.size()==1?"gt":"ge";
                                                    String logicSignLarge = large.size()==1?"lt":"le";
                                                    VarThresholdVO varThresholdVO = varThresholdVOMap.get(groupId).get(0);
                                                    groupThresholdVO.setLogicSign(logicSignSmall.concat(",").concat(logicSignLarge));
                                                    groupThresholdVO.setRuleId(rule.getSrcRuleId());
                                                    groupThresholdVO.setRuleName(rule.getRuleName());
                                                    groupThresholdVO.setVarsetVersion(varThresholdVO.getVarsetVersion());
                                                    groupThresholdVO.setVarset(varThresholdVO.getVarset());
                                                    groupThresholdVO.setProdCode(varThresholdVO.getProdCode());
                                                    groupThresholdVO.setVersion(varThresholdVO.getVersion());
                                                    groupThresholdVO.setVarName(varThresholdVO.getVarName());
                                                    varLogicVO.getVarThresholdVOList().add(groupThresholdVO);
                                                }else{
                                                    varThresholdVOMap.get(groupId).forEach(varThresholdVO -> {
                                                        varLogicVO.getVarThresholdVOList().add(varThresholdVO);
                                                    });
                                                }
                                                varVO.getVarLogicVOList().add(varLogicVO);
                                                varVO.setLogic(varLogicVO.getLogic()>0?0:1);
                                            });
                                        }
                                    }else{
                                        VarLogicVO varLogicVO = new VarLogicVO();
                                        VarThresholdVO varThresholdVO = new VarThresholdVO(
                                                ruleVar.getVarCode(),ruleVar.getVarVersion(),ruleVar.getVarName(),
                                                api.getApiVarset(),api.getApiVarsetVersion(),
                                                ruleVar.getJudgeExpr().getType(),ruleVar.getJudgeExpr().getLt(),
                                                ruleVar.getJudgeExpr().getGt(),
                                                ruleVar.getJudgeExpr().getEq(),
                                                ruleVar.getVarThreshold(),ruleVar.getVarTime(),
                                                ruleVar.getVarMatch(),rule.getSrcRuleId(), rule.getRuleName());
                                        varThresholdVO.makeLogicSign();
                                        varLogicVO.getVarThresholdVOList().add(varThresholdVO);
                                        varVO.getVarLogicVOList().add(varLogicVO);
                                        varVO.setLogic(1);
                                    }
                                    Integer hashCode = JSONObject.toJSONString(varVO).hashCode();
                                    if(!hashSet.contains(hashCode)){
                                        hashSet.add(hashCode);
                                        varSetVO2.getVarVOList().add(varVO);
                                    }
                                });
                            });
                        }
                    });
                });
                jsonObject.setCode("00");
                jsonObject.setMsg("Success");
                jsonObject.setData(varSetVO2);
                redisTemplate.opsForHash().put(key,flowId,varSetVO2);
                redisTemplate.expire(key, NumberUtils.INTEGER_ONE,TimeUnit.DAYS);
            }
            return jsonObject;
        }
        jsonObject.setCode("00");
        jsonObject.setMsg("Success");
        jsonObject.setData(varSetVO);
        redisTemplate.expire(key, 1,TimeUnit.DAYS);
        return jsonObject;
    }



    /**
     * 获取客户决策流列表
     * @param apiCode
     * @return
     */
    @GetMapping(value = "/flow/list")
    public JSONObject queryDecideFlows(@RequestParam(name = "apiCode") String apiCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject data = service.getDecideFlows(apiCode);
            if (CollectionUtils.isEmpty(data)) {
                jsonObject.put("code", "01");// 没有结果
                jsonObject.put("msg", "No result");
            } else {
                jsonObject.put("code", "00");
                jsonObject.put("msg", "Success");
                jsonObject.put("data", data);
            }
        } catch (Exception e) {
            jsonObject.put("code", "03");// 03 默认生成失败
            jsonObject.put("msg", "Failed");
            log.error("获取客户[" + apiCode + "]决策流信息失败: " + e.getMessage(), e);
        }
        return jsonObject;
    }

    /**
     * 开启决策流
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/flow/start")
    public JSONObject startDecideFlow(@RequestBody Map<String, Object> request) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = service.startDecideFlow(request);
        } catch (Exception e) {
            jsonObject.put("code", "03");
            jsonObject.put("msg", "Failed!");
            log.error("开启决策流失败: " + e.getMessage(), e);
        }
        return jsonObject;
    }

    @GetMapping(value = "/basic/info")
    public JSONObject getBasicInfo(@RequestParam(name = "companyName") String companyName) {
        JSONObject jsonObject = new JSONObject();

        try {
            EntityBasic basicInfo = service.getBasicInfo(companyName);
            if (basicInfo == null) {
                jsonObject.put("code", "01");
                jsonObject.put("msg", "No result");
            } else {
                jsonObject.put("code", "00");
                jsonObject.put("msg", "Success");
                jsonObject.put("data", basicInfo);
            }
        } catch (Exception e) {
            jsonObject.put("code", "03");
            jsonObject.put("msg", "Failed!");
            log.error("开启决策流失败: " + e.getMessage(), e);
        }
        return jsonObject;
    }

    /**
     * 获取企业分层信息
     *
     * @param companyName
     * @return
     */
    @GetMapping(value = "/company/slice")
    public JSONObject getCompanySlice(@RequestParam(name = "companyName") String companyName) {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject = sliceService.getCompanySlice(companyName);
        } catch (Exception e) {
            jsonObject.put("code", "03");
            jsonObject.put("msg", "Failed!");
            log.error("获取企业分层信息失败: " + e.getMessage(), e);
        }
        return jsonObject;
    }
}
