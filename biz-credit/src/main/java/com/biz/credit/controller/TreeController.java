package com.biz.credit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.*;
import com.biz.credit.service.ITreeService;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.DNodeVO;
import com.biz.credit.vo.DTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/tree")
public class TreeController {

    @Autowired
    private ITreeService iTreeService;

    @PostMapping("/saveTree")
    public RespEntity saveTree(@RequestBody DTreeVO dTreeVO,HttpSession session){
        log.info("/saveTree,请求参数：jsonObject={}", JSON.toJSONString(dTreeVO));
        String apiCode=session.getAttribute("apiCode").toString();
        String userId=session.getAttribute("userId").toString();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            return RespEntity.error().setMsg("您的登陆信息已过期，请重新登陆");
        }
        dTreeVO.setApiCode(apiCode);
        dTreeVO.setUserId(Long.parseLong(userId));
        try {
            return iTreeService.saveTree(dTreeVO);
        }catch (Exception e){
            log.error("保存决策树失败",e.getMessage(),e);
            return RespEntity.error().setMsg("保存决策树失败:"+e.getMessage());
        }

    }

    @GetMapping("/treeMessage")
    public RespEntity getTreeById(@RequestParam(value = "treeId",required = true )Long treeId) {
        if(treeId==null){
            return RespEntity.error().setMsg("请求参数为空"+"treeId="+treeId);
        }
        DTreeVO jsonObject = iTreeService.getTreeById(treeId);
        return RespEntity.success().setData(jsonObject);
    }
    @GetMapping("/deleteTree")
    public RespEntity getDeleteTreeByTreeId(@RequestParam(value = "treeId",required = true )Long treeId) {
        if(treeId==null){
            return RespEntity.error().setMsg("请求参数为空，treeId="+treeId);
        }
        try {
            return iTreeService.deleteTreeByTreeId(treeId);
        }catch (Exception e){
            log.error("删除决策树信息失败,treeId={}",treeId,e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("删除决策树信息失败");
    }
    }

    @PostMapping("/publishTree")
    public RespEntity publishTree(@RequestBody DTreeVO dTreeVO,HttpSession session){
        log.info("/publishTree,请求参数：jsonObject={}", JSON.toJSONString(dTreeVO));
        String apiCode=session.getAttribute("apiCode").toString();
        String userId=session.getAttribute("userId").toString();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            return RespEntity.error().setMsg("您的登陆信息已过期，请重新登陆");
        }
        dTreeVO.setApiCode(apiCode);
        dTreeVO.setUserId(Long.parseLong(userId));
        try {
            return iTreeService.publishTree(dTreeVO);
        }catch (Exception e){
            log.error("发布决策树失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("发布决策树失败:"+e.getMessage());
        }
    }

    @GetMapping("/versionList")
    public RespEntity getVersionListByTreeName(@RequestParam(value = "projectId",required = true)Long projectId,
            @RequestParam(value = "treeName",required = true) String treeName){
        if(projectId==null||StringUtils.isEmpty(treeName)){
            return RespEntity.error().setMsg("请求参数为空,treeName="+treeName+",projectId="+projectId);
        }
        List<DTree> versionList = iTreeService.getVersionListByTreeName(projectId,treeName);
        return RespEntity.success().setData(versionList);
    }
    @GetMapping("/treeList")
    public RespEntity getTreeListByuserId(@RequestParam(value = "projectId",required = true)Long projectId,HttpSession session) {
        String apiCode=session.getAttribute("apiCode").toString();
        if(projectId==null){
            return RespEntity.error().setMsg("请求参数为空,projectId="+projectId);
        }
        List<DTree> treeList = iTreeService.getTreeList(projectId,apiCode);
        return RespEntity.success().setData(treeList);
    }
    @GetMapping("/maxVersion")
    public RespEntity getMaxVersionByTreeName(@RequestParam(value = "treeName",required = true) String treeName,
                                              @RequestParam(value = "projectId",required = true)Long projectId){
        if(projectId==null||StringUtils.isEmpty(treeName)){
            return RespEntity.error().setMsg("请求参数为空,treeName="+treeName+",projectId="+projectId);
        }
        Double maxVersion = iTreeService.getMaxVersionByTreeName(treeName,projectId);
        return RespEntity.success().setData(maxVersion);
    }

    @GetMapping("/existTreeName")
    public RespEntity existTreeName(@RequestParam(value = "treeName",required = true) String treeName,
                                    @RequestParam(value = "projectId",required = true)Long projectId){
        if(projectId==null||StringUtils.isEmpty(treeName)){
            return RespEntity.error().setMsg("请求参数为空,treeName="+treeName+",projectId="+projectId);
        }
        boolean exist = iTreeService.existTreeName(treeName,projectId);
        return RespEntity.success().setData(exist);
    }

    @GetMapping("/queryTreeConfig")
    public RespEntity queryTreeConfig(@RequestParam(value = "nodeId",required = false )Long nodeId,HttpSession session){
        String apiCode=session.getAttribute("apiCode").toString();
        if (StringUtils.isEmpty(apiCode)) {
            return RespEntity.error().setMsg("您的登陆信息已过期，请重新登陆");
        }
        DNodeConfigVO nodeConfigVO=new DNodeConfigVO();
        nodeConfigVO.setApiCode(apiCode);
        nodeConfigVO.setNodeId(nodeId);
        List<JSONObject> list = iTreeService.queryTreeConfig(nodeConfigVO);
        return RespEntity.success().setData(list);
    }
    @PostMapping("/saveTreeConfig")
    public RespEntity saveTreeConfig(@RequestBody DNodeVO dNodeVO){
        RespEntity list = iTreeService.saveTreeConfig(dNodeVO.getNodeId(),dNodeVO.getNodeTreeConfig());
        return RespEntity.success().setData(list);
    }
}
