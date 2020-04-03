package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.model.radar.chart.v10.ParamConst;
import com.biz.model.radar.chart.v10.RadarChartCore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class RadarController {

    @PostMapping("/radar/get_result")
    public RespEntity getResult(@RequestParam(value = "apiCode")String apiCode,
                                @RequestParam(value = "radarCode")String radarCode,
                                @RequestParam(value = "jsonData")String jsonData){
        RespEntity entity = new RespEntity(RespCode.WARN,null);
        log.info("apiCode:{}",apiCode);
        log.info("radarCode:{}",radarCode);
        log.info("jsonData:{}",jsonData);
        if(StringUtils.isNotEmpty(jsonData)){
            JSONObject tmp = new JSONObject();
            JSONObject jsonDataObj  = JSONObject.parseObject(jsonData);
            Map<String,String> paramMap = new HashMap<>();
            ParamConst.PARAM_ITEM_GROUP.forEach((k, v)->{
                if(!jsonDataObj.keySet().contains(k)&&!ParamConst.COMPANY_PERSON.equals(v)){
                    tmp.put("paramError",1);
                    entity.changeRespEntity(RespCode.REQ_VALID,tmp);
                    paramMap.put(k,StringUtils.EMPTY);
                }else{
                    paramMap.put(k,jsonDataObj.getString(k));
                }
            });
            if(!tmp.containsKey("paramError")){
                Map<String, Double> result = new RadarChartCore().score(paramMap);
                log.info("result:{}",result);
                entity.changeRespEntity(RespCode.SUCCESS,result);
            }
        }
        return entity;
    }
}

