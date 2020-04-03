package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.Rule;
import com.biz.warning.service.IVariableTypeService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class VariableTypeController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IVariableTypeService variableTypeService;


    /**
     * 查找变量一级类型接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/firstVariableType")
    @ResponseBody
    public RespEntity findFirstVariableType( @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                             @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize) {
        Page<Rule> page= PageHelper.startPage(pageNo, pageSize);
        RespEntity entity = new RespEntity();
        try {
            variableTypeService.findFirstVariableType();
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("查询变量一级类型接口信息失败");
            return entity;
        }
    }

    /**
     * 测试接口
     *
     * @param
     * @return
     */
    @PostMapping(value = "/findFirstVariableTypeTest")
    @ResponseBody
    public RespEntity findFirstVariableTypeTest() {
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.setData(variableTypeService.findFirstVariableType());
        return entity;
    }

    /**
     * 查找变量二级类型接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/secondVariableType/{firstTypeName}")
    @ResponseBody
    public RespEntity findSecondVariableType(@PathVariable("firstTypeName")String firstTypeName,
                                             @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                             @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize) {
        Page<Rule> page= PageHelper.startPage(pageNo, pageSize);
        RespEntity entity = new RespEntity();
        try {
            variableTypeService.findSecondVariableType(firstTypeName);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("查询变量二级类型接口信息失败");
            return entity;
        }
    }

}
