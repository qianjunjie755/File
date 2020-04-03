package com.biz.credit.controller;

import com.biz.credit.controller.validator.DScoreLevelValidator;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.ICompanyCreditService;
import com.biz.credit.service.IDScoreLevelService;
import com.biz.credit.vo.DScoreLevelVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Api(tags = "征信评分等级管理API")
@RestController
@RequestMapping("/scoreLevel")
public class DScoreLevelController {

    @Autowired
    private IDScoreLevelService scoreLevelService;

    @Autowired
    private ICompanyCreditService companyCreditService;

    @ApiOperation(value = "新增/更新评分等级")
    @PostMapping
    public RespEntity saveScoreLevel(@RequestBody DScoreLevelVO scoreLevel,
                                   HttpSession session){
        String apiCode = (String) session.getAttribute("apiCode");
        scoreLevel.setApiCode(apiCode);
        RespEntity respEntity = DScoreLevelValidator.saveValidate(scoreLevel);
        if (!respEntity.isSuccess()){
            return respEntity;
        }
        if (companyCreditService.findModelByTypeAndCode(scoreLevel.getModelCode(), scoreLevel.getModelType()) == null){
            return RespEntity.error().setMsg("该模型Code和模型类型不存在");
        }
        scoreLevelService.saveScoreLevel(scoreLevel);
        return respEntity;
    }

    @ApiOperation(value = "查询模型评分等级列表", notes = "根据apicode，模型类型，模型code查询模型评分等级列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelCode", value = "模型code-Integer", required = true),
            @ApiImplicitParam(name = "modelType", value = "模型类型-Integer", required = true)
    })
    @GetMapping("list")
    public RespEntity<DScoreLevelVO> getScoreLevel(@RequestParam(name = "modelCode") Integer modelCode,
                                                   @RequestParam(name = "modelType") Integer modelType,
                                                   HttpSession session){
        String apiCode = (String) session.getAttribute("apiCode");
        RespEntity respEntity = DScoreLevelValidator.queryValidate(apiCode, modelCode, modelType);
        if (!respEntity.isSuccess()){
            return respEntity;
        }
        return respEntity.setData(scoreLevelService.findByCondition(apiCode, modelCode, modelType));
    }


}
