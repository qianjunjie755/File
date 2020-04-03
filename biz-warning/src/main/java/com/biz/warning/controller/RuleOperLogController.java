package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.RuleOperLog;
import com.biz.warning.service.IRuleOperLogService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RestController
public class RuleOperLogController {

    @Autowired
    private IRuleOperLogService ruleOperLogService;

    /**
     * 新增规则操作日志接口
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/addRuleOperLog", method = RequestMethod.POST)
    @ResponseBody
    public long addRuleOperLog(@RequestBody RuleOperLog ruleOperLog) {
        ruleOperLog.setOperTime(new Timestamp(new Date().getTime()));
        long ret = ruleOperLogService.addRuleOperLog(ruleOperLog);
        return ret;
    }

    /**
     * 查询规则操作日志接口
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/findRuleOperLog/{ruleId}", method = RequestMethod.GET)
    @ResponseBody
    public RespEntity findRuleOperLog(@PathVariable("ruleId")Long ruleId,
                                      @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                      @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize) {
        RespEntity entity = new RespEntity();
        Page<RuleOperLog> page= PageHelper.startPage(pageNo, pageSize);
        List<RuleOperLog> ruleOperLog = ruleOperLogService.findRuleOperLog(ruleId);
        JSONObject jo = new JSONObject();
        jo.put("total",page.getTotal());
        jo.put("rows",ruleOperLog);
        entity.changeRespEntity(RespCode.SUCCESS,jo);
        return entity;
    }



}
