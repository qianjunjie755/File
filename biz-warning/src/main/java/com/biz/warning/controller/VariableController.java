package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.Rule;
import com.biz.warning.service.IVariableService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

/**
 * 变量维护接口
 */
@RestController
public class VariableController {

    @Autowired
    RestTemplate restTemplate;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IVariableService variableService;


    /**
     * 按类型查找变量接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/variable/variableTypeCode/{variableTypeCode}")
    @ResponseBody
    public RespEntity findVariableByType(@PathVariable("variableTypeCode")Long variableTypeCode,
                                         @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                         @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize) {
        Page<Rule> page= PageHelper.startPage(pageNo, pageSize);
        RespEntity entity = new RespEntity();

        try {
            variableService.findVariableByType(variableTypeCode);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("按类型查找变量接口信息失败");
            return entity;
        }
    }

    /**
     * 按变量名查找版本
     *
     * @param
     * @return
     */
    @GetMapping(value = "/version/variableName/{variableName}")
    @ResponseBody
    public RespEntity findVersionByVariableName(@PathVariable("variableName")String variableName,
                                                @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                                @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize) {
        Page<Rule> page= PageHelper.startPage(pageNo, pageSize);
        RespEntity entity = new RespEntity();

        try {
            variableService.findVersionByVariableName(variableName);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("按变量名查找版本信息失败");
            return entity;
        }
    }
}
