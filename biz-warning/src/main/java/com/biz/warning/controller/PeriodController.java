package com.biz.warning.controller;

import com.biz.warning.domain.Period;
import com.biz.warning.service.IPeriodService;
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
public class PeriodController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IPeriodService periodService;

    /**
     * 查询时间范围
     *
     * @param
     * @return
     */
    @GetMapping(value = "/period")
    @ResponseBody
    public RespEntity findAllFrequency() {
        RespEntity entity = new RespEntity();
        try {
            List<Period> periodList = periodService.findAllPeriod();
            /*
            List<Map<String,Object>> periodList = new ArrayList<>();
            periodList.add(new HashMap(){{
                put("periodCode",1);
                put("periodName","最近3个月");
            }});
            periodList.add(new HashMap(){{
                put("periodCode",2);
                put("periodName","最近6个月");
            }});*/
            entity.changeRespEntity(RespCode.SUCCESS,periodList);
            return entity;
        }catch (Exception e){
            logger.info("查询时间范围接口信息失败");
            return entity;
        }
    }


}
