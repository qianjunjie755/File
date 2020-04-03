package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.Rule;
import com.biz.warning.domain.RuleSet;
import com.biz.warning.service.IRuleSetService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.biz.warning.util.SysDict;
import com.biz.warning.vo.RuleSetVO;
import com.biz.controller.BaseController;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RestController
public class RuleSetController extends BaseController {

    @Resource
    private IRuleSetService ruleSetService;
    @Resource
    private RestTemplate restTemplate;

    /**
     * 新增规则集接口
     *
     * @param
     * @return
     */
    @PostMapping(value = "/ruleSet")
    @ResponseBody
    public RespEntity addRuleSet(@RequestBody RuleSetVO ruleSet) {
        Long userId = getUserId();
        String apiCode = getApiCode();
        ruleSet.setUserId(userId);
        ruleSet.setApiCode(apiCode);
        ruleSet.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
        ruleSetService.addRuleSet(ruleSet);
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        return entity;
    }

    /**
     * 分页查找规则集列表接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/ruleSetsByPaging")
    @ResponseBody
    private RespEntity findRuleSetsByPaging(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                    @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                    HttpSession session) {
        String apiCode = getApiCode();
        Page<Rule> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        RuleSetVO ruleSet = new RuleSetVO();
        ruleSet.setApiCode(apiCode);
        ruleSet.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
        try {
            ruleSetService.findAllRuleSet(ruleSet);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            log.info("查找所有规则接口信息失败", e);
            return entity;
        }
    }

    /**
     * 查找所有规则集接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/ruleSets")
    @ResponseBody
    private RespEntity findRuleSets() {
        String apiCode = getApiCode();
        RespEntity entity = new RespEntity();
        RuleSetVO ruleSet = new RuleSetVO();
        ruleSet.setApiCode(apiCode);
        ruleSet.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
        ruleSet.setOrigin(SysDict.IS_ORIGIN_TRUE);
        try {
            List<RuleSetVO> ruleSetList = ruleSetService.findAllRuleSet(ruleSet);
            entity.changeRespEntity(RespCode.SUCCESS,ruleSetList);
            return entity;
        }catch (Exception e){
            log.info("查找所有规则集接口信息失败", e);
            return entity;
        }
    }

    /**
     * 按条件查找规则集接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/ruleSetList")
    @ResponseBody
    private RespEntity findRuleSets(RuleSetVO ruleSet,HttpSession session) {
        String apiCode = getApiCode();
        RespEntity entity = new RespEntity();
        ruleSet.setApiCode(apiCode);
        ruleSet.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
        ruleSet.setOrigin(SysDict.IS_ORIGIN_TRUE);
        try {
            List<RuleSetVO> ruleSetList = ruleSetService.findRuleSetByParam(ruleSet);
            entity.changeRespEntity(RespCode.SUCCESS,ruleSetList);
            return entity;
        }catch (Exception e){
            log.info("按条件查找规则集接口信息失败", e);
            return entity;
        }
    }

    /**
     * 数据源测试接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/apisTest")
    @ResponseBody
    public RespEntity apisTest(HttpSession session) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type","application/json;charset=utf-8");

        JSONObject jsonData = new JSONObject();
        jsonData.put("limitCount",100);

        MultiValueMap<String,String> map = new LinkedMultiValueMap<>();
        map.add("jsonData",jsonData.toJSONString());

        JSONObject jsonObject = restTemplate.getForEntity("http://API-ADMIN-SERVICE/api_admin/apis?prodCode=BizExecutor",  JSONObject.class).getBody();
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.setData(jsonObject);

        return entity;
    }

    /**
     * 根据规则查找规则集
     *
     * @param
     * @return
     */
    @GetMapping(value = "/ruleSet/ruleId/{ruleId}")
    @ResponseBody
    private RespEntity findRuleSetByRuleId(@PathVariable("ruleId") Long ruleId) {
        String apiCode = getApiCode();
        RespEntity entity = new RespEntity();
        try {
            RuleSet ruleSet = ruleSetService.findRuleSetByRuleId(ruleId,apiCode);
            entity.changeRespEntity(RespCode.SUCCESS,ruleSet);
            return entity;
        }catch (Exception e){
            log.info("根据规则查找规则集", e);
            return entity;
        }
    }

}
