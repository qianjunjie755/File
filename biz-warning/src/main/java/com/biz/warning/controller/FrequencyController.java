package com.biz.warning.controller;

import com.biz.warning.domain.FrequencyPool;
import com.biz.warning.service.IFrequencyService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FrequencyController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IFrequencyService frequencyService;

    /**
     * 查询频次接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/frequency")
    @ResponseBody
    public RespEntity findAllFrequency() {
        RespEntity entity = new RespEntity();

        try {
            List<FrequencyPool> frequencyPoolList = frequencyService.findAllFrequency();
            entity.changeRespEntity(RespCode.SUCCESS,frequencyPoolList);
            return entity;
        }catch (Exception e){
            logger.info("查询频次接口信息失败");
            return entity;
        }
    }


}
