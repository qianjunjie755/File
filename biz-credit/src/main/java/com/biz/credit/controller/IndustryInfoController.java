package com.biz.credit.controller;

import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IIndustryInfoService;
import com.biz.credit.vo.IndustryInfoViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/industryInfoView")
public class IndustryInfoController {

    @Autowired
    private IIndustryInfoService industryInfoService;

    @GetMapping("/list")
    public RespEntity list(){
        RespEntity respEntity = new RespEntity(RespCode.WARN,null);
        try {
            List<IndustryInfoViewVO> list = industryInfoService.findAllIndustryInfoVOListView();
            respEntity.changeRespEntity(RespCode.SUCCESS,list);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }
    @GetMapping("")
    public RespEntity getById(@RequestParam("industryCode")String industryCode,@RequestParam(value = "version",defaultValue = "1.0",required = false)String version){
        RespEntity respEntity = new RespEntity(RespCode.WARN,null);
        try {
            IndustryInfoViewVO view = industryInfoService.findByIndustryCode(industryCode, version);
            respEntity.changeRespEntity(RespCode.SUCCESS,view);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }


}
