package com.biz.warning.controller;

import com.biz.warning.domain.VariablePool;
import com.biz.warning.service.IVariablePoolService;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class VariablePoolController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IVariablePoolService variablePoolService;

    /**
     * 按数据源查找
     *
     * @param
     * @return
     */
    @GetMapping(value = "/variablePool/apiProdCode/{apiProdCode}")
    @ResponseBody
    public RespEntity findVersionByVariableName(@PathVariable("apiProdCode")String apiProdCode) {
        RespEntity entity = new RespEntity();
        try {
            List<VariablePool> variableList = variablePoolService.findByApiProdCode(apiProdCode);
            entity.changeRespEntity(RespCode.SUCCESS,variableList);
            return entity;
        }catch (Exception e){
            e.printStackTrace();
            logger.info("按数据源查找变量信息失败");
            return entity;
        }
    }

}
