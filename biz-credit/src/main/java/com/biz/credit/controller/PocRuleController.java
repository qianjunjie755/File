package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.DRuleVar;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDRuleService;
import com.biz.credit.service.IDRuleVarService;
import com.biz.credit.vo.DNodeRuleVarRefVO;
import com.biz.credit.vo.DRuleVO;
import com.biz.credit.vo.DRuleVarVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/pocRule")
public class PocRuleController {

    @Autowired
    private IDRuleService dRuleService;

    @Autowired
    private IDRuleVarService idRuleVarService;



    //规则及变量
    @GetMapping("/ruleList")
    public RespEntity getRuleList(String keyword,HttpSession session,@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize){
        String userId = session.getAttribute("userId").toString();
        String apiCode= session.getAttribute("apiCode").toString();
        DRuleVO dRuleVO = new DRuleVO();
        dRuleVO.setKeyword(keyword);
        dRuleVO.setUserId(Integer.parseInt(userId));
        dRuleVO.setApiCode(apiCode);
        Page<DRuleVO> page = PageHelper.startPage(pageNo, pageSize);//分页处理
        List<DRuleVO> ruleList = dRuleService.getSrcRuleList(dRuleVO);
        JSONObject jo = new JSONObject();
        jo.put("total", page.getTotal());
        jo.put("rows", page.getResult());
        return RespEntity.success().setData(jo);
    }
    //引用变量
    @GetMapping("/refVarList")
    public RespEntity getRefVarList(String varPId,HttpSession session){
        String userId = session.getAttribute("userId").toString();
        String apiCode= session.getAttribute("apiCode").toString();

        List<DNodeRuleVarRefVO> varRefVOList = dRuleService.queryAllSrcRefVarsByVarId(varPId);


        return RespEntity.success().setData(varRefVOList);
    }

    @GetMapping("/pocRuleList")
    public RespEntity getPocRuleList(String keyword,HttpSession session){
        String userId = session.getAttribute("userId").toString();
        String apiCode= session.getAttribute("apiCode").toString();
        JSONObject res = dRuleService.getPocRuleList("",userId,apiCode);
        return RespEntity.success().setData(res);
    }

    @PostMapping(value = "/updateRuleVar")
    public RespEntity updateVarInfo(@RequestBody List<DRuleVar> dDRuleVar, HttpSession session){
        String userId = session.getAttribute("userId").toString();
        String apiCode= session.getAttribute("apiCode").toString();
        JSONObject res = idRuleVarService.updateVarLsit(dDRuleVar);
        return RespEntity.success();
    }

    @PostMapping(value ="/updateRefVar")
    public RespEntity updateRefVarInfo(@RequestBody List<DRuleVar> dDRuleVar, HttpSession session){
        String userId = session.getAttribute("userId").toString();
        String apiCode= session.getAttribute("apiCode").toString();
        JSONObject res = idRuleVarService.updateRefVarList(dDRuleVar);
        return RespEntity.success();
    }

}
