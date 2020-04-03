package com.biz.warning.controller;

import com.biz.warning.domain.VariableSource;
import com.biz.warning.service.IVariableSourceService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
public class VariableSourceController {

    @Autowired
    private IVariableSourceService variableSourceService;

    @GetMapping("/variableSources")
    public RespEntity findAllVariableSource(
            @RequestParam(value = "requireAll",required = false,defaultValue = "1") Integer requireAll
    ){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        try {
            List<VariableSource> variableSourceList = variableSourceService.findAllVariableSource();
            if(CollectionUtils.isEmpty(variableSourceList)){
                variableSourceList = new ArrayList<>();
                if(StringUtils.equals("1",requireAll.toString())){
                    variableSourceList.add(new VariableSource(0,"全部"));
                }

            }else{
                if(StringUtils.equals("1",requireAll.toString()))
                    variableSourceList.add(0,new VariableSource(0,"全部"));
            }
            respEntity.changeRespEntity(RespCode.SUCCESS,variableSourceList);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }
}
