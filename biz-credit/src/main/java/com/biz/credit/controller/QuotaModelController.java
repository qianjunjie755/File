package com.biz.credit.controller;

import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IQuotaModelService;
import com.biz.credit.vo.QuotaModelVO;
import com.biz.credit.vo.QuotaModelVarVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api( tags = "额度模型相关业务调用")
@RestController
@RequestMapping("/quotaModel")
public class QuotaModelController {

    @Autowired
    private IQuotaModelService quotaModelService;

    @ApiOperation(value = "查询额度模型列表",notes = "决策引擎 <- 操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId",value = "空-查询全部 有值-查询对应的额度模型",paramType = "query")
    })
    @GetMapping
    public RespEntity<List<QuotaModelVO>> findList(
            @RequestParam(value = "modelId",required = false)Integer modelId
    ){
        RespEntity<List<QuotaModelVO>> respEntity = new RespEntity<>(RespCode.WARN,null);
        List<QuotaModelVO> list =  quotaModelService.findModelList(modelId,null);
        respEntity.changeRespEntity(RespCode.SUCCESS,list);
        return respEntity;
    }


    @ApiOperation(value = "查询单个额度模型下的变量列表",notes = "决策引擎 <- 操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId",required = true,value = "查询模型编号对应的额度模型",paramType = "query")
    })
    @GetMapping("/var")
    public RespEntity<List<QuotaModelVarVO>> findVarList(
            @RequestParam(value = "modelId")Integer modelId
    ){
        RespEntity<List<QuotaModelVarVO>> respEntity = new RespEntity<>(RespCode.WARN,null);
        List<QuotaModelVarVO> list =  quotaModelService.findModelVarList(modelId);
        respEntity.changeRespEntity(RespCode.SUCCESS,list);
        return respEntity;
    }


}
