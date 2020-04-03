package com.biz.credit.controller;

import com.alibaba.fastjson.JSON;
import com.biz.credit.domain.DFlowLink;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDFlowPropsService;
import com.biz.credit.service.IDFlowService;
import com.biz.credit.service.IDNodeRuleService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.DFlowBizVO;
import com.biz.credit.vo.DFlowLinkVO;
import com.biz.credit.vo.DFlowPlatformVO;
import com.biz.credit.vo.DFlowVO;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpHeaders.Values.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Api( tags = "决策流相关操作")

@RestController
@RequestMapping(value = "/flow", consumes = APPLICATION_JSON,produces = APPLICATION_JSON)
@Slf4j
public class DFlowController {

    @Autowired
    private IDFlowService idFlowService;
    @Autowired
    private IDFlowPropsService dFlowPropsService;

    @Autowired
    private IDNodeRuleService nodeRuleService;

    @GetMapping("/listForInput")
    public RespEntity getFlowList(HttpServletRequest request){
        RespEntity respEntity = new RespEntity(RespCode.WARN,null);
        String apiCode = request.getSession().getAttribute(Constants.API_CODE).toString();
        List<DFlowVO> dFlowVOList = idFlowService.findDFlowVOList(new DFlowVO(apiCode));
        respEntity.changeRespEntity(RespCode.SUCCESS,dFlowVOList);
        return respEntity;
    }

    @PostMapping
    public RespEntity saveFlow(@RequestBody DFlowVO flowVO,@RequestParam(value = "groupId")Integer groupId,
            HttpSession session){
        log.info("保存决策流，入参:{}", JSON.toJSONString(flowVO));
        String apiCode = (String)session.getAttribute("apiCode");
        String userId = (String)session.getAttribute("userId");

        if(groupId ==null){
            return RespEntity.error().setMsg("平台类型id不能为空");
        }

        flowVO.setApiCode(apiCode);
        flowVO.setUserId(Long.parseLong(userId));
        flowVO.setPlatformId(groupId);
        try {
            return idFlowService.saveFlow(flowVO);
        }
        catch (Exception e){
            log.error("决策流保存失败",e.getMessage());
            log.error(e.getMessage(),e);
            return RespEntity.error().setMsg(e.getMessage());
        }
    }

    @PostMapping("/publish")
    public RespEntity publishFlow(@RequestBody DFlowVO flowVO, HttpSession session){
        String apiCode = (String) session.getAttribute("apiCode");
        String userId = (String)session.getAttribute("userId");
        flowVO.setApiCode(apiCode);
        flowVO.setUserId(Long.parseLong(userId));
        try {
            return idFlowService.publishFlow(flowVO);
        }catch (Exception e){
            log.error("决策流发布失败",e.getMessage());
            e.printStackTrace();
            return RespEntity.error().setMsg(e.getMessage());
        }
    }

    @GetMapping("/list")
    public RespEntity getFlowList(DFlowVO flowVO,HttpSession session){
        String apiCode = (String) session.getAttribute("apiCode");
        String userId = (String)session.getAttribute("userId");
        flowVO.setApiCode(apiCode);
        flowVO.setUserId(Long.parseLong(userId));
        List<DFlowVO> flowVOList = idFlowService.getFlowList(flowVO);
        return RespEntity.success().setData(flowVOList);
    }

    @GetMapping("/{flowId}")
    public RespEntity getFlowDetailById(@PathVariable("flowId")Long flowId,HttpSession session){
        String apiCode = (String) session.getAttribute("apiCode");
        String userId = (String)session.getAttribute("userId");
        long start =System.currentTimeMillis();
        //缓存变量数据
        DFlowVO flowVO = idFlowService.getFlowDetailById(flowId,apiCode,userId);
        log.info("查询决策流总耗时 cost:"+(System.currentTimeMillis()-start)+"ms");
        return RespEntity.success().setData(flowVO);
    }
    @GetMapping("/cancel/{flowId}")
    public RespEntity cancelFlow(@PathVariable("flowId")Long flowId,HttpSession session){
        String apiCode = session.getAttribute(Constants.API_CODE).toString();
        Long userId = Long.parseLong(session.getAttribute(Constants.USER_ID).toString());
        RespEntity retJson = idFlowService.cancelFlow(new DFlowVO(apiCode, flowId,userId));
        if(Objects.equals(retJson.getCode(),(RespCode.WARN.getCode()))){
            return RespEntity.error();
        }
        return RespEntity.success().setData(retJson.getData());
    }
    @ApiOperation(value = "查询决策流平台列表",notes = "决策引擎 <- 操作员")
    @ApiImplicitParam(name="addAll",value = "是否在列表头部添加“全部”选项 1添加 0不添加",example = "1",defaultValue = "1",paramType = "query")
    @GetMapping("/platformList")
    public RespEntity<List<DFlowPlatformVO>> getPlatformList(
            @RequestParam(value = "addAll",required = false,defaultValue = "1")Integer addAll
            ){
        RespEntity<List<DFlowPlatformVO>> retJson = new RespEntity(RespCode.WARN,null);
        List<DFlowPlatformVO> list = dFlowPropsService.findPlatformList(null);
        if(!CollectionUtils.isEmpty(list)&&Objects.equals(NumberUtils.INTEGER_ONE,addAll)){
            DFlowPlatformVO dFlowPlatformVO = new DFlowPlatformVO();
            dFlowPlatformVO.setStatus(Constants.COMMON_STATUS_VALID);
            dFlowPlatformVO.setPlatFormName("全部");
            dFlowPlatformVO.setId(NumberUtils.INTEGER_MINUS_ONE);
            list.add(0,dFlowPlatformVO);
        }
        retJson.changeRespEntity(RespCode.SUCCESS,list);
        return retJson;
    }
    @ApiOperation(value = "查询决策流业务列表",notes = "决策引擎 <- 操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addAll", value = "是否在列表头部添加“全部”选项 1添加 0不添加", example = "1", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "platformId", value = "业务所属平台编号", example = "1", defaultValue = "-1", paramType = "query")
    })
    @GetMapping("/bizList")
    public RespEntity<List<DFlowBizVO>> getBizList(
            @RequestParam(value = "addAll",required = false,defaultValue = "1")Integer addAll,
            @RequestParam(value = "platformId",required = false,defaultValue = "-1")Integer platformId
    ){
        RespEntity<List<DFlowBizVO>> retJson = new RespEntity(RespCode.WARN,null);
        List<DFlowBizVO> list = dFlowPropsService.findBizList(Objects.equals(NumberUtils.INTEGER_MINUS_ONE,platformId)?null:platformId,null);
        retJson.changeRespEntity(RespCode.SUCCESS,list);
        if(!CollectionUtils.isEmpty(list)&&Objects.equals(NumberUtils.INTEGER_ONE,addAll)){
            DFlowBizVO dFlowBizVO = new DFlowBizVO();
            dFlowBizVO.setStatus(Constants.COMMON_STATUS_VALID);
            dFlowBizVO.setBizName("全部");
            dFlowBizVO.setId(NumberUtils.INTEGER_MINUS_ONE);
            list.add(0,dFlowBizVO);
        }
        return retJson;
    }

    @ApiOperation(value = "查询决策流业务环节列表",notes = "决策引擎 <- 操作员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "addAll", value = "是否在列表头部添加“全部”选项 1添加 0不添加", example = "1", defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "bizId", value = "业务环节所属业务编号", example = "1", defaultValue = "-1", paramType = "query")
    })
    @GetMapping("/linkList")
    public RespEntity<List<DFlowLinkVO>> getLinkList(
            @RequestParam(value = "addAll",required = false,defaultValue = "1")Integer addAll,
            @RequestParam(value = "bizId",required = false,defaultValue = "-1")Integer bizId
    ){
        RespEntity<List<DFlowLinkVO>> retJson = new RespEntity(RespCode.WARN,null);
        List<DFlowLinkVO> list = dFlowPropsService.findLinkList(Objects.equals(NumberUtils.INTEGER_MINUS_ONE,bizId)?null:bizId,null);
        retJson.changeRespEntity(RespCode.SUCCESS,list);
        if(!CollectionUtils.isEmpty(list)&&Objects.equals(NumberUtils.INTEGER_ONE,addAll)){
            DFlowLinkVO vo = new DFlowLinkVO();
            vo.setStatus(Constants.COMMON_STATUS_VALID);
            vo.setLinkName("全部");
            vo.setId(NumberUtils.INTEGER_MINUS_ONE);
            list.add(0,vo);
        }
        return retJson;
    }
}
