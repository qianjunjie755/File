package com.biz.warning.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.domain.Rule;
import com.biz.warning.service.IWarnResultService;
import com.biz.warning.util.*;
import com.biz.warning.vo.HitedVariableOverviewVO;
import com.biz.warning.vo.WarnResultVariableVO;
import com.biz.controller.BaseController;
import com.biz.service.IAuthService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@RestController
public class WarnResultController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(WarnResultController.class);

    @Autowired
    private IAuthService authService;
    @Autowired
    private IWarnResultService warnResultService;

    /**
     * 按任务查找预警结果接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/warnResultVariable/task/{taskId}")
    @ResponseBody
    public RespEntity findWarnResultVariableByTask(@PathVariable("taskId")Long taskId,
                                                   @RequestParam(value = "entityName",required = false,defaultValue = StringUtils.EMPTY) String entityName,
                                                   @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                                   @RequestParam(value = "startDate",required = false, defaultValue = "") String startDate,
                                                   @RequestParam(value = "endDate",required = false, defaultValue = "") String endDate,
                                                   @RequestParam(value = "ruleId",required = false, defaultValue = "0") Long ruleId,
                                                   @RequestParam(value = "variableSourceId",required = false,defaultValue = "0") Integer variableSource,
                                                   @RequestParam(value = "warnStatus",defaultValue = "0")Integer warnStatus,
                                                   @RequestParam(value = "ruleSetId",required = false, defaultValue = "0") Long ruleSetId,HttpSession session) {
        Page<WarnResultVariableVO> page= PageHelper.startPage(pageNo, pageSize);
        RespEntity entity = new RespEntity();

        try {
            //warnResultService.findWarnResultVariable(taskId,null,"");
            if(StringUtils.isNotEmpty(startDate)){
                startDate = startDate.concat(" 00:00:00");
            }
            if(StringUtils.isNotEmpty(endDate)){
                endDate = endDate.concat(" 23:59:59");
            }
            WarnResultVariableVO warnResultVariableVO = new WarnResultVariableVO();
            warnResultVariableVO.setEntityName(entityName);
            warnResultVariableVO.setTaskId(taskId);
            warnResultVariableVO.setStartDate(startDate);
            warnResultVariableVO.setEndDate(endDate);
            warnResultVariableVO.setRuleId(ruleId);
            warnResultVariableVO.setRuleSetId(ruleSetId);
            warnResultVariableVO.setEntityStatus(warnStatus);
            /*Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
            warnResultVariableVO.setUserId(userId);*/
            warnResultService.findWarnResultVariableByTask(warnResultVariableVO);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());

            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            logger.info("按任务查找预警结果接口信息失败");
            e.printStackTrace();
            return entity;
        }

    }

    @GetMapping(value = "/warnResultVariables")
    public RespEntity findWarnResultVariables(@RequestParam(value = "entityName", required = false, defaultValue = StringUtils.EMPTY) String entityName,
                                              @RequestParam(value = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                              @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                                              @RequestParam(value = "startDate", required = false, defaultValue = "") String startDate,
                                              @RequestParam(value = "endDate", required = false, defaultValue = "") String endDate,
                                              @RequestParam(value = "ruleId", required = false, defaultValue = "0") Long ruleId,
                                              @RequestParam(value = "variableSourceId", required = false, defaultValue = "0") String variableSourceId,
                                              @RequestParam(value = "warnStatus", defaultValue = "0") Integer warnStatus,
                                              @RequestParam(value = "ruleSetId", required = false, defaultValue = "0") Long ruleSetId,
                                              @RequestParam(value = "variableName", required = false, defaultValue = StringUtils.EMPTY) String variableName,
                                              @RequestParam(value = "ruleName", required = false, defaultValue = StringUtils.EMPTY) String ruleName,
                                              @RequestParam(value = "ruleSetName", required = false, defaultValue = StringUtils.EMPTY) String ruleSetName,
                                              HttpServletRequest request) {
        Page<WarnResultVariableVO> page = PageHelper.startPage(pageNo, pageSize);
        RespEntity entity = new RespEntity();

        try {
            //warnResultService.findWarnResultVariable(taskId,null,"");
            if (StringUtils.isNotEmpty(startDate)) {
                startDate = startDate.concat(" 00:00:00");
            }
            if (StringUtils.isNotEmpty(endDate)) {
                endDate = endDate.concat(" 23:59:59");
            }
            WarnResultVariableVO warnResultVariableVO = new WarnResultVariableVO();
            warnResultVariableVO.setEntityName(entityName);
            warnResultVariableVO.setStartDate(startDate);
            warnResultVariableVO.setEndDate(endDate);
            warnResultVariableVO.setRuleId(ruleId);
            warnResultVariableVO.setRuleSetId(ruleSetId);
            warnResultVariableVO.setVariableName(variableName);
            warnResultVariableVO.setRuleName(ruleName);
            warnResultVariableVO.setRuleSetName(ruleSetName);
            List<Integer> vIds = new ArrayList<>();
            Arrays.asList(variableSourceId.trim().split(",")).forEach(vid -> {
                if (StringUtils.equals("0", vid.trim())) {
                    warnResultVariableVO.setVariableSourceId(0);
                }
                vIds.add(Integer.parseInt(vid.trim()));
            });
            warnResultVariableVO.setVariableSourceIds(vIds);
            warnResultVariableVO.setEntityStatus(warnStatus);

            String userTypeStr = request.getSession().getAttribute(SysDict.USER_TYPE).toString();
            if (StringUtils.equals(SysDict.USER_TYPE_SUPER_ADMIN, userTypeStr)) {
                String apiCode = request.getSession().getAttribute(SysDict.API_CODE).toString();
                warnResultVariableVO.setApiCode(apiCode);
            } else if (StringUtils.equals(SysDict.USER_TYPE_ADMIN, userTypeStr)) {
                List<Integer> users = authService.getAdminUserIds(request.getHeader("cookie"));
                if (!CollectionUtils.isEmpty(users)) {
                    String userIds = users.toString().replaceAll("\u0020|\\[|\\]", StringUtils.EMPTY);
                    warnResultVariableVO.setUserIdList(users);
                    warnResultVariableVO.setUserIds(userIds);
                }
            } else {
                Integer userId = Integer.parseInt(request.getSession().getAttribute(SysDict.USER_ID).toString());
                warnResultVariableVO.setUserId(userId);
            }
            warnResultService.findWarnResultVariableByTask(warnResultVariableVO);
            JSONObject jo = new JSONObject();
            jo.put("total", page.getTotal());
            jo.put("rows", page.getResult());

            entity.changeRespEntity(RespCode.SUCCESS, jo);
            return entity;
        } catch (Exception e) {
            logger.info("按任务查找预警结果接口信息失败");
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 下载风险命中详情
     * @param entityName
     * @param startDate
     * @param endDate
     * @param ruleId
     * @param variableSourceId
     * @param warnStatus
     * @param ruleSetId
     * @param response
     */
    @GetMapping(value = "/exportWarnResultVariables")
    public void exportWarnResultVariables(@RequestParam(value = "entityName", defaultValue = StringUtils.EMPTY) String entityName,
                                          @RequestParam(value = "startDate", defaultValue = "") String startDate,
                                          @RequestParam(value = "endDate", defaultValue = "") String endDate,
                                          @RequestParam(value = "ruleId", defaultValue = "0") Long ruleId,
                                          @RequestParam(value = "variableSourceId", defaultValue = "0") String variableSourceId,
                                          @RequestParam(value = "warnStatus", defaultValue = "0")Integer warnStatus,
                                          @RequestParam(value = "ruleSetId", defaultValue = "0") Long ruleSetId,
                                          @RequestParam(value = "variableName", defaultValue = StringUtils.EMPTY) String variableName,
                                          @RequestParam(value = "ruleName", defaultValue = StringUtils.EMPTY) String ruleName,
                                          @RequestParam(value = "ruleSetName", defaultValue = StringUtils.EMPTY) String ruleSetName,
                                          HttpServletResponse response) {
        try {
            if(StringUtils.isNotEmpty(startDate)){
                startDate = startDate.concat(" 00:00:00");
            }
            if(StringUtils.isNotEmpty(endDate)){
                endDate = endDate.concat(" 23:59:59");
            }
            WarnResultVariableVO warnResultVariableVO = new WarnResultVariableVO();
            warnResultVariableVO.setEntityName(entityName);
            warnResultVariableVO.setStartDate(startDate);
            warnResultVariableVO.setEndDate(endDate);
            warnResultVariableVO.setRuleId(ruleId);
            warnResultVariableVO.setRuleSetId(ruleSetId);
            warnResultVariableVO.setVariableName(variableName);
            warnResultVariableVO.setRuleName(ruleName);
            warnResultVariableVO.setRuleSetName(ruleSetName);
            List<Integer> vIds = new ArrayList<>();
            Arrays.asList(variableSourceId.trim().split(",")).forEach(vid->{
                if(StringUtils.equals("0",vid.trim())){
                    warnResultVariableVO.setVariableSourceId(0);
                }
                vIds.add(Integer.parseInt(vid.trim()));
            });
            warnResultVariableVO.setVariableSourceIds(vIds);
            warnResultVariableVO.setEntityStatus(warnStatus);

            String userTypeStr = getUserType();
            if(StringUtils.equals(SysDict.USER_TYPE_SUPER_ADMIN,userTypeStr)){
                warnResultVariableVO.setApiCode(getApiCode());
            }else if(StringUtils.equals(SysDict.USER_TYPE_ADMIN,userTypeStr)){
                List<Integer> users = getUserIdList();
                if(!CollectionUtils.isEmpty(users)){
                    String userIds = users.toString().replaceAll("\u0020|\\[|\\]",StringUtils.EMPTY);
                    warnResultVariableVO.setUserIdList(users);
                    warnResultVariableVO.setUserIds(userIds);
                }
            }else{
                Integer userId = Integer.parseInt(getUserId().toString());
                warnResultVariableVO.setUserId(userId);
            }
            List<WarnResultVariableVO> warnResultList = warnResultService.findWarnResultVariableByTask(warnResultVariableVO);

            Map<String,String> rowMapper = new LinkedHashMap<>();
            rowMapper.put("entityName","企业名称");
            rowMapper.put("variableSourceName","风险来源");
            rowMapper.put("ruleName","规则名称");
            rowMapper.put("variableName","变量");
            rowMapper.put("periodCH","时间粒度");
            rowMapper.put("judge","判断条件");
            rowMapper.put("thresholdValue","阈值");
            rowMapper.put("triggerThresholdCH","命中值");
            rowMapper.put("hitTime","预警时间");

            String fileName = "风险命中详情.xlsx";
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.flushBuffer();
            XSSFWorkbook workbook = ExcelUtil.getWorkbook(BeanUtil.getBeanMapList(warnResultList), rowMapper);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            logger.error("导出预警结果接口信息失败:" + e.getMessage(), e);
        }
    }

    /**
     * 按进件查找预警结果接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/warnResultVariable/entity/{entityId}")
    @ResponseBody
    public RespEntity findWarnResultVariableByEntity(@PathVariable("entityId")Long entityId,
                                                     @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                                     @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                                     @RequestParam(value = "period",required = false, defaultValue = "") String period) {

        Page<Rule> page= PageHelper.startPage(pageNo, pageSize);
        RespEntity entity = new RespEntity();

        try {
            warnResultService.findWarnResultVariable(null,entityId,period);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            logger.info("按进件查找预警结果接口信息失败");
            e.printStackTrace();
            return entity;
        }

    }

    /**
     * 按任务查找预警规则结果接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/warnResultRule")
    @ResponseBody
    public RespEntity findWarnResultRule(@RequestParam(value = "taskId",required = false,defaultValue = "") Long taskId,
                                               @RequestParam(value = "entityId",required = false,defaultValue = "") Long entityId,
                                               @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                               @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                               @RequestParam(value = "period",required = false, defaultValue = "") String period) {

        Page<Rule> page= PageHelper.startPage(pageNo, pageSize);
        RespEntity entity = new RespEntity();

        try {
            warnResultService.findWarnResultRule(taskId,entityId,period);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            logger.info("任务查找预警规则结果接口信息失败");
            e.printStackTrace();
            return entity;
        }

    }

    /**
     * 按任务查找变量预警结果概览接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/hitedVariableOverview")
    @ResponseBody
    public RespEntity findHitedVariableOverview(@RequestParam(value = "taskId",required = false,defaultValue = "") Long taskId,
                                                @RequestParam(value = "entityId",required = false,defaultValue = "") Long entityId,
                                                @RequestParam(value = "period",required = false, defaultValue = "") String period) {
        List<HitedVariableOverviewVO> list =  warnResultService.findHitedVariableOverview(taskId,entityId,period);
        RespEntity entity = new RespEntity();
        entity.setCode(RespCode.SUCCESS.getCode());
        entity.setMessage(RespCode.SUCCESS.getMessage());
        entity.setData(list);
        return entity;

    }

    /**
     * 按进件名称查找变量预警结果详情接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/warnResultVariableList")
    @ResponseBody
    public RespEntity findWarnResultVariableByEntity(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                                     @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                                     WarnResultVariableVO warnResultVariableVO) {
        String apiCode = getApiCode();
        warnResultVariableVO.setApiCode(apiCode);
        List<Integer> userIdList = getUserIdList();
        Page<WarnResultVariableVO> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        try {
            warnResultService.findWarnResultVariableByEntity(warnResultVariableVO,userIdList);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
        } catch (Exception e) {
            log.info("按进件名称查找变量预警结果详情失败");
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 按进件名称查找变量预警结果详情接口
     *
     * @param
     * @return
     */
    @GetMapping(value = "/riskSourceCountByEntity")
    @ResponseBody
    public RespEntity findWarnResultVariableByEntity(WarnResultVariableVO warnResultVariableVO) {
        String apiCode = getApiCode();
        warnResultVariableVO.setApiCode(apiCode);
        List<Integer> userIdList = getUserIdList();
        RespEntity entity = new RespEntity();
        try {
            List<Map<String, Object>> riskSourceCount = warnResultService.findRiskSourceCount(warnResultVariableVO, userIdList);
            entity.changeRespEntity(RespCode.SUCCESS,riskSourceCount);
        } catch (Exception e) {
            log.info("按进件名称查找变量预警结果详情失败");
            e.printStackTrace();
        }
        return entity;
    }


    /**
     * 导出变量预警结果详情
     *
     * @param
     * @return
     */
    @GetMapping(value = "/exportWarnResultVariableList")
    @ResponseBody
    public void exportWarnResultVariableList(WarnResultVariableVO warnResultVariableVO, HttpServletResponse response) {
        String apiCode = getApiCode();
        warnResultVariableVO.setApiCode(apiCode);
        List<Integer> userIdList = getUserIdList();
        try {
            List<WarnResultVariableVO> warnResultList = warnResultService.findWarnResultVariableByEntity(warnResultVariableVO, userIdList);
            Map<String,String> rowMapper = new LinkedHashMap<>();
            rowMapper.put("hitTime","命中时间");
            rowMapper.put("ruleName","规则名称");
            rowMapper.put("variableName","变量");
            rowMapper.put("judge","判断条件");
            rowMapper.put("thresholdValue","触发阈值");
            rowMapper.put("triggerThresholdCH","命中值");
            rowMapper.put("taskName","关联任务名称");
            XSSFWorkbook workbook = ExcelUtil.getWorkbook(BeanUtil.getBeanMapList(warnResultList), rowMapper);
            try {
                workbook.write(response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.info("导出变量预警结果详情");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        System.out.println(list.toString().replaceAll("\u0020|\\[|\\]",""));
    }
}

