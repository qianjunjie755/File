package com.biz.credit.controller;

import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.domain.VariablePeriod;
import com.biz.credit.service.IVariablePeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/nodeModel")
public class DNodeModelController{

    @Autowired
    private IVariablePeriodService variablePeriodService;

    @GetMapping("periods")
    public RespEntity getPeriods(){
        List<VariablePeriod> list = variablePeriodService.getVariablePeriodList();
        return new RespEntity(RespCode.SUCCESS,list);
    }
}
