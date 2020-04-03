package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.ICreditModelService;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.DNodeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RestController
public class ReportCreditModelController {
    @Autowired
    private ICreditModelService iCreditModelService;
    @GetMapping("/queryCreditModelConfig")
    public RespEntity queryCreditModelConfig(@RequestParam(value = "nodeId",required = false )Long nodeId, HttpSession session){
        String apiCode=session.getAttribute("apiCode").toString();
        if (StringUtils.isEmpty(apiCode)) {
            return RespEntity.error().setData("您的登陆信息已过期，请重新登陆");
        }
        DNodeConfigVO nodeConfigVO=new DNodeConfigVO();
        nodeConfigVO.setApiCode(apiCode);
        nodeConfigVO.setNodeId(nodeId);
        List<JSONObject> list = iCreditModelService.queryCreditModelConfig(nodeConfigVO);
        return RespEntity.success().setData(list);
    }
    @PostMapping("/saveCreditConfig")
    public RespEntity saveTreeConfig(@RequestBody DNodeVO dNodeVO){
        RespEntity list = iCreditModelService.saveCreditModelConfig(dNodeVO.getNodeId() , dNodeVO.getNodeCreditModelConfig() , true );
        return RespEntity.success().setData(list);
    }
}
