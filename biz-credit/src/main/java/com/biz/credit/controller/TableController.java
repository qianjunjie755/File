package com.biz.credit.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.DTable;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.ITableService;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.DNodeVO;
import com.biz.credit.vo.DTableVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/table")
public class TableController {
    @Autowired
    private ITableService iTableService;

    @PostMapping("/saveTable")
    public RespEntity saveTable(@RequestBody DTableVO dTableVO,HttpSession session){
        log.info("/saveTable,请求参数：dTableVO={}", JSON.toJSONString(dTableVO));
        String apiCode=session.getAttribute("apiCode").toString();
        String userId=session.getAttribute("userId").toString();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            log.info("您的登陆信息已过期，请重新登陆");
            return RespEntity.error().setMsg("您的登陆信息已过期，请重新登陆");
        }
        dTableVO.setApiCode(apiCode);
        dTableVO.setUserId(Long.parseLong(userId));
        try {
            return iTableService.saveTable(dTableVO);
        }catch (Exception e){
            log.error("保存决策表失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("保存决策表失败:"+e.getMessage());
        }
    }


    @GetMapping("/tableMessage")
    public RespEntity getTableByTableId(@RequestParam(value = "tableId",required = true )Long tableId) {
        try {
            DTableVO dTableVO = iTableService.getTableByTableId(tableId);
            return RespEntity.success().setData(dTableVO);
        }catch (Exception e){
            log.error("获取决策表信息失败,tableId={}",tableId,e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("获取决策表信息失败");
        }
    }

    @GetMapping("/deleteTable")
    public RespEntity getDeleteTableByTableId(@RequestParam(value = "tableId",required = true )Long tableId) {
        try {
            return iTableService.deleteTableByTableId(tableId);
        }catch (Exception e){
            log.error("删除决策表信息失败,tableId={}",tableId,e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("删除决策表信息失败");
        }
    }
    @PostMapping("/publishTable")
    public RespEntity publishTable(@RequestBody DTableVO dTableVO,HttpSession session){
        log.info("/publishTable,请求参数：dTableVO={}", JSON.toJSONString(dTableVO));
        String apiCode=session.getAttribute("apiCode").toString();
        String userId=session.getAttribute("userId").toString();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            return RespEntity.error().setMsg("您的登陆信息已过期，请重新登陆");
        }
        dTableVO.setApiCode(apiCode);
        dTableVO.setUserId(Long.parseLong(userId));
        try {
            return iTableService.publishTable(dTableVO);
        }catch (Exception e){
            log.error("发布决策表失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("发布决策表失败:"+e.getMessage());
        }
    }

    @GetMapping("/tableVersionList")
    public RespEntity getVersionListByTableName(@RequestParam(value = "projectId",required = true)Long projectId,
                                                @RequestParam(value = "tableName",required = true) String tableName){
        try{
            List<DTableVO> versionList = iTableService.getVersionListByTableName(projectId,tableName);
            return RespEntity.success().setData(versionList);
        }catch (Exception e){
            log.error("获取决策表名称获取版本列表失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("获取决策表名称获取版本列表失败");
        }

    }
    @GetMapping("/tableList")
    public RespEntity getTableListByProjectId(@RequestParam(value = "projectId",required = true) Long projectId,HttpSession session) {
        String apiCode=session.getAttribute("apiCode").toString();
        if (StringUtils.isEmpty(apiCode)) {
            return RespEntity.error().setMsg("您的登陆信息已过期，请重新登陆");
        }
        try{
            List<DTable> tableList = iTableService.getTableList(projectId,apiCode);
            return RespEntity.success().setData(tableList);
        }catch (Exception e){
            log.error("获取决策表列表失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("获取决策表列表失败");
        }
    }
    @GetMapping("/maxVersion")
    public RespEntity getMaxVersionByTableName(@RequestParam(value = "tableName",required = true) String tableName){
        try{
            String maxVersion = iTableService.getMaxVersionByTableName(tableName);
            return RespEntity.success().setData(maxVersion);
        }catch (Exception e){
            log.error("获取决策表最大版本失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("获取决策表最大版本失败");
        }
    }

    @GetMapping("/existTableName")
    public RespEntity existTableName(@RequestParam(value = "tableName",required = true) String tableName,
                                     @RequestParam(value = "projectId",required = true) Long projectId ){
        try{
            boolean exist = iTableService.existTableName(tableName,projectId);
            return RespEntity.success().setData(exist);
        }catch (Exception e){
            log.error("判断决策表名称是否存在接口失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg("判断决策表名称是否存在接口失败");
        }
    }
    @GetMapping("/queryTableConfig")
    public RespEntity queryTableConfig(@RequestParam(value = "nodeId",required = false )Long nodeId,HttpSession session){
        String apiCode=session.getAttribute("apiCode").toString();
        DNodeConfigVO nodeConfigVO=new DNodeConfigVO();
        nodeConfigVO.setApiCode(apiCode);
        nodeConfigVO.setNodeId(nodeId);
        List<JSONObject> list = iTableService.queryTableConfig(nodeConfigVO);
        return RespEntity.success().setData(list);
    }
    @PostMapping("/saveTableConfig")
    public RespEntity saveTreeConfig(@RequestBody DNodeVO dNodeVO){
        RespEntity list = iTableService.saveTableConfig(dNodeVO.getNodeId(),dNodeVO.getNodeTableConfig());
        return RespEntity.success().setData(list);
    }
}
