package com.biz.credit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.DNodeParam;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.*;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/node")
@Slf4j
public class DNodeController {
    @Autowired
    private IDNodeApiService nodeApiService;

    @Autowired
    private IApiService apiService;

    @Autowired
    private IDNodeRuleService nodeRuleService;

    @Autowired
    private IDNodeService nodeService;

    @Autowired
    private IDAntiFraudService antiFraudService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping("/syncApiInfo")
    public RespEntity syncApiInfo(@RequestParam("publicKey")String publicKey){
        RespEntity respEntity = new RespEntity(RespCode.SUCCESS,null);
        String redisPublicKey = stringRedisTemplate.opsForValue().get("biz_credit:publicKey");
        if(StringUtils.equals(publicKey,redisPublicKey)){
            apiService.syncApiInfo();
        }
        return respEntity;
    }

    @GetMapping("/api/list")
    public RespEntity getApiList(DNodeApiVO nodeApiVO,HttpSession session){
        log.info("/api/list,请求参数：nodeApiVO={}", JSON.toJSONString(nodeApiVO));
        String apiCode= session.getAttribute("apiCode").toString();
        nodeApiVO.setApiCode(apiCode);
        try {
            List<DNodeApiVO> nodeApiList = nodeApiService.getDNodeApiList(nodeApiVO);
            return RespEntity.success().setData(nodeApiList);
        } catch (Exception e) {
            log.info("查询api信息失败",e.getMessage());
            return RespEntity.error().setMsg("查询api信息失败");
        }
    }

    @GetMapping("/api/sourceCount")
    public RespEntity getApiSourceCount(HttpSession session){
        String apiCode= session.getAttribute("apiCode").toString();
        List<Map<String,Object>> sourceCount = apiService.getSourceCount(apiCode);
        return RespEntity.success().setData(sourceCount);
    }

    @GetMapping("/api")
    public RespEntity getApiDetail(ApiVO apiVO){
        log.info("/api/{apiId},请求参数：apiId={}", JSON.toJSONString(apiVO));
        ApiVO res = apiService.getApiDetail(apiVO);
        return RespEntity.success().setData(res);
    }

    @GetMapping("/nodeRule/list")
    public RespEntity getNodeRuleList(DNodeRuleVO dNodeRuleVO, HttpSession session){
        log.info("/nodeRule,请求参数：dNodeRuleVO={}", JSON.toJSONString(dNodeRuleVO));
        String apiCode= session.getAttribute("apiCode").toString();
        if(dNodeRuleVO.getRuleType() == null){
            return RespEntity.error().setMsg("规则类型不能为空");
        }
        if(dNodeRuleVO.getRuleType() != 1 && dNodeRuleVO.getRuleType() != 2){
            return RespEntity.error().setMsg("规则类型有误");
        }
        Integer modelType = dNodeRuleVO.getRuleType() == 1 ? Constants.ENTERPRISE_RULE : Constants.NATURAL_PERSON_RULE;
        try {
            dNodeRuleVO.setApiCode(apiCode);
            dNodeRuleVO.setModelType(modelType);
            List<DNodeRuleVO> nodeRuleList = nodeRuleService.getNodeRuleList(dNodeRuleVO);
            return RespEntity.success().setData(nodeRuleList);
        } catch (Exception e) {
            log.info("查询规则信息失败",e.getMessage());
            return RespEntity.error().setMsg("查询规则信息失败");
        }
    }

    @PostMapping("/nodeRule")
    public RespEntity saveRuleVar(@RequestBody DNodeRuleVO nodeRuleVO){
        log.info("/nodeRule,请求参数：nodeRuleVO={}", JSON.toJSONString(nodeRuleVO));
        nodeRuleVO.buildVarThreadValue();
        return nodeRuleService.updateNodeRuleVar(nodeRuleVO);
    }

    @GetMapping("/antiFraud/list")
    public RespEntity getAntiFraudList(DAntiFraudVO antiFraudVO,HttpSession session){
        String apiCode= session.getAttribute("apiCode").toString();
        antiFraudVO.setApiCode(apiCode);
        List<DAntiFraudVO> antiFraudList = antiFraudService.getAntiFraudList(antiFraudVO);
        return RespEntity.success().setData(antiFraudList);
    }

    @PostMapping
    public RespEntity saveNode(@RequestBody DNodeVO nodeVO,HttpSession session){
        log.info("保存节点，入参:{}",JSON.toJSONString(nodeVO));
        String userId = session.getAttribute("userId").toString();
        nodeVO.setUserId(userId);
        try {
            return nodeService.saveNode(nodeVO);
        }catch (Exception e){
            log.error("保存节点失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg(e.getMessage());
        }
    }

    @GetMapping("/configScore")
    public RespEntity getConfigScoreByNodeId(@RequestParam(value = "nodeId",required = false) Long nodeId,

                                             HttpSession session){
        String userId = session.getAttribute("userId").toString();
        String apiCode= session.getAttribute("apiCode").toString();
        JSONObject res = nodeService.getNodeDetail(nodeId,userId,apiCode);
        return RespEntity.success().setData(res);
    }
    /*
    @GetMapping("/queryInPrarams")
    public RespEntity queryInPrarams(@RequestParam(value = "nodeId",required = true)Long nodeId){
        List<DNodeParam> list = nodeService.queryInPararms(nodeId);
        return RespEntity.success().setData(list);
    }/*
    @PostMapping("/queryNodePrarams")
    public RespEntity queryNodePrarams(@RequestBody FlowParamConfigVO flowParamConfigVO){
        log.info("/queryNodePrarams,切换tab配置入参请求参数：flowParamConfigVO={}", JSON.toJSONString(flowParamConfigVO));
        Set<DNodeParam> list=nodeService.queryNodePrarams(flowParamConfigVO);
        return RespEntity.success().setData(list);
    }*/
    @PostMapping("/getPrarams")
    public RespEntity getPrarams(@RequestBody FlowParamConfigVO flowParamConfigVO){
        log.info("/getPrarams,配置入参请求参数：flowParamConfigVO={}", JSON.toJSONString(flowParamConfigVO));
        Set<DNodeParam> list=nodeService.getPrarams(flowParamConfigVO);
        return RespEntity.success().setData(list);
    }

    @PostMapping("/saveInPrarams")
    public RespEntity saveInPrarams(@RequestBody DParamsVO dParamsVO){
        log.info("/saveInPrarams,保存入参请求参数：dParamsVO={}", JSON.toJSONString(dParamsVO));
        List<DNodeParam> list=dParamsVO.getNodeParamList();
        return nodeService.saveInPrarams(dParamsVO.getNodeId(),list);
    }

    @DeleteMapping("/{nodeId}")
    public RespEntity deleteNode(@PathVariable("nodeId")Long nodeId){
        return nodeService.deleteNode(nodeId);
    }

}
