package com.biz.credit.controller;

import cn.hutool.core.lang.UUID;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespCommonData;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IFixedPriceModelService;
import com.biz.credit.service.IQuotaModelService;
import com.biz.credit.vo.FixedPriceModelVO;
import com.biz.credit.vo.FixedPriceModelVarVO;
import com.biz.credit.vo.QuotaModelVO;
import com.biz.credit.vo.QuotaModelVarVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Api( tags = "定价模型相关业务调用")
@Slf4j
@RestController
@RequestMapping("/fixPriceModel")
public class FixPriceModelController {
    @Autowired
    private IFixedPriceModelService fixedPriceModelService;

    @ApiOperation(value = "查询定价模型列表",notes = "决策引擎 <- 操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId",value = "空-查询全部 有值-查询对应的额度模型",paramType = "query")
    })
    @GetMapping
    public RespEntity<List<FixedPriceModelVO>> findList(
            @RequestParam(value = "modelId",required = false)Integer modelId,
            @RequestParam(value = "modelName",required = false)String modelName
    ){
        RespEntity<List<FixedPriceModelVO>> respEntity = new RespEntity<>(RespCode.WARN,null);
        List<FixedPriceModelVO> list =  fixedPriceModelService.findModelList(modelId,modelName);
        respEntity.changeRespEntity(RespCode.SUCCESS,list);
        return respEntity;
    }
    @ApiOperation(value = "查询单个定价模型下的变量列表",notes = "决策引擎 <- 操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId",required = true,value = "查询模型编号对应的额度模型",paramType = "query")
    })
    @GetMapping("/var")
    public RespEntity<List<FixedPriceModelVarVO>> findVarList(
            @RequestParam(value = "modelId")Integer modelId
    ){
        RespEntity<List<FixedPriceModelVarVO>> respEntity = new RespEntity<>(RespCode.WARN,null);
        List<FixedPriceModelVarVO> list =  fixedPriceModelService.findModelVarList(modelId);
        respEntity.changeRespEntity(RespCode.SUCCESS,list);
        return respEntity;
    }

    @ApiOperation(value = "添加定价模型",notes = "决策引擎 <- 操作员")
    @PostMapping
    public RespEntity<RespCommonData> addModel(@RequestBody FixedPriceModelVO fixedPriceModelVO,
                                               HttpSession session){
        RespEntity<RespCommonData> respEntity = new RespEntity<>(RespCode.WARN,null);
        Object fileName = session.getAttribute("fixedPriceModelFileName");
        if(Objects.isNull(fileName)){
            respEntity.changeRespEntity(RespCode.UPLOAD_WRONG,new RespCommonData(0));
            return respEntity;
        }

        return respEntity;
    }

    @ApiOperation(value = "上传定价模型脚本",notes = "决策引擎 <- 操作员")
    @PostMapping("/upload")
    public RespEntity<RespCommonData> uploadInputFile(@RequestParam("file") MultipartFile file,HttpSession session){
        RespEntity<RespCommonData> respEntity = new RespEntity<>(RespCode.WARN);
        String dirName = "/data/fixedPriceModel/";
        File dir = new File(dirName);
        if(!dir.exists()){
            dir.mkdir();
        }
        String fileName = dirName.concat(UUID.fastUUID().toString()).concat(".").concat(file.getName());
        File serverFile = new File(fileName);
        try {
            FileUtils.writeByteArrayToFile(serverFile,file.getBytes());
            respEntity.changeRespEntity(RespCode.SUCCESS,new RespCommonData(1));
            session.setAttribute("fixedPriceModelFileName",fileName);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }
}
