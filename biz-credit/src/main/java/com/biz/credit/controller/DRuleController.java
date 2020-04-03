package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDRuleService;
import com.biz.credit.vo.DNodeRuleVarRefVO;
import com.biz.credit.vo.DRuleVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/drule")
public class DRuleController {

    @Autowired
    private IDRuleService dRuleService;

    @GetMapping("/list")
    public RespEntity getAllRuleList(String keyword){
        JSONObject result = dRuleService.getRuleListByKeyword(keyword);
        return RespEntity.success().setData(result);
    }

    @GetMapping("/companyRuleList")
    public RespEntity getCompanyRuleList(String keyword, HttpSession session){
        String userId = session.getAttribute("userId").toString();
        String apiCode= session.getAttribute("apiCode").toString();
        DRuleVO dRuleVO = new DRuleVO();
        dRuleVO.setKeyword(keyword);
        dRuleVO.setUserId(Integer.parseInt(userId));
        dRuleVO.setApiCode(apiCode);
        List<DRuleVO> ruleList = dRuleService.getCompanyRuleListByKeyword(dRuleVO);
        return RespEntity.success().setData(ruleList);
    }

    @GetMapping("/personRuleList")
    public RespEntity getPersonRuleList(String keyword,HttpSession session){
        String userId = session.getAttribute("userId").toString();
        String apiCode= session.getAttribute("apiCode").toString();
        DRuleVO dRuleVO = new DRuleVO();
        dRuleVO.setKeyword(keyword);
        dRuleVO.setUserId(Integer.parseInt(userId));
        dRuleVO.setApiCode(apiCode);
        List<DRuleVO> ruleList = dRuleService.getPersonRuleListByKeyword(dRuleVO);
        return RespEntity.success().setData(ruleList);
    }

}
