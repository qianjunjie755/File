package com.biz.credit.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.*;
import com.biz.credit.utils.Constants;
import com.biz.credit.utils.ExcelUtil;
import com.biz.credit.utils.ReportUtils;
import com.biz.credit.vo.*;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/reportCustomerBI")
public class ReportCustomerBIController {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${biz_input_param_all}")
    private String bizInputParamAll;
    @Autowired
    private IInputGroupTendencyService inputGroupTendencyService;
    @Autowired
    private IIndustryInfoService industryInfoService;
    @Autowired
    private IReportTaskService reportTaskService;
    @Autowired
    private IInputDataService inputDataService;
    @Autowired
    private IRuleDataService iRuleDataService;
    @Autowired
    private IRuleHitDetailService ruleHitDetailService;
    @Autowired
    private IRelatedPersonService relatedPersonService;
    @Autowired
    private IBIInputFileDetailHistoryService biInputFileDetailHistoryService;


    /**
     * 总体进件数量趋势
     */
    @GetMapping("/queryInputCompanyTotalBI")
    public RespEntity queryInputCompanyTotalBI(BiInputDataVO biInputDataVO, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            String apiCode = (String) session.getAttribute("apiCode");
            if (StringUtils.isEmpty(apiCode)) {
                ret.setMsg("您的登陆信息已过期，请重新登陆");
                return ret;
            }
            biInputDataVO.setApiCode(apiCode);
            log.info("queryInputCompanyTotalBI_param:" + JSONObject.toJSONString(biInputDataVO));
            ret = inputDataService.findBiInputDataByDayList(biInputDataVO);
            log.info("queryInputCompanyTotalBI_result:" + ret);
            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("biz-report调用biz-intelligence微服务失败");
            ret.setCode("500");
            return ret;
        }
    }

    /**
     * 命中规则次数趋势
     */
    @GetMapping("/queryHitRuleCountBI")
    public RespEntity queryHitRuleCountBI(BiRuleDataVO biRuleDataVO, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            String apiCode = (String) session.getAttribute("apiCode");
            if (StringUtils.isEmpty(apiCode)) {
                ret.setMsg("您的登陆信息已过期，请重新登陆");
                return ret;
            }
            biRuleDataVO.setApiCode(apiCode);
            log.info("queryHitRuleCountBI_param:" + JSONObject.toJSONString(biRuleDataVO));


            RespEntity retJson = iRuleDataService.findBiRuleDataByDayList(biRuleDataVO);
            if (StringUtils.equals("00", retJson.getCode())) {
                ret.changeRespEntity(RespCode.SUCCESS, retJson.getData());
            }else {
                ret.setCode(retJson.getCode());
                ret.setMsg(retJson.getMsg());
            }
            log.info("queryHitRuleCountBI_result:" + JSONObject.toJSONString(retJson));
            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("biz-report调用biz-intelligence微服务失败");
            ret.setCode("500");
            return ret;
        }
    }

    /**
     * 有命中规则进件情况
     */
    @GetMapping("/queryHitRuleCompanyCountBI")
    public RespEntity queryHitRuleCompanyCountBI(BiRuleDataVO biRuleDataVO, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            String apiCode = (String) session.getAttribute("apiCode");
            if (StringUtils.isEmpty(apiCode)) {
                ret.setMsg("您的登陆信息已过期，请重新登陆");
                return ret;
            }
            biRuleDataVO.setApiCode(apiCode);

            log.info("queryHitRuleCountBI_param:" + JSONObject.toJSONString(biRuleDataVO));
            RespEntity retJson = iRuleDataService.findHitRuleCompanyByDayList(biRuleDataVO);
            log.info("queryHitRuleCountBI_result:" +JSONObject.toJSONString(retJson));
            if (StringUtils.equals("00", retJson.getCode())) {
                ret.changeRespEntity(RespCode.SUCCESS, retJson.getData());
            }else {
                ret.setCode(retJson.getCode());
                ret.setMsg(retJson.getMsg());
            }

            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("biz-report调用biz-intelligence微服务失败");
            ret.setCode("500");
            return ret;
        }
    }

    /**
     * 进件总数与有命中规则进件情况
     */
    @GetMapping("/queryInCompanyAndHitRuleBI")
    public RespEntity queryInCompanyAndHitRuleBI(BiInputDataVO biInputDataVO, BiRuleDataVO biRuleDataVO, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            String apiCode = (String) session.getAttribute("apiCode");
            if (StringUtils.isEmpty(apiCode)) {
                ret.setMsg("您的登陆信息已过期，请重新登陆");
                return ret;
            }
            biRuleDataVO.setApiCode(apiCode);
            biInputDataVO.setApiCode(apiCode);

            //进件总数
            log.info("queryInputCompanyTotalBI_param:" + JSONObject.toJSONString(biInputDataVO));
            RespEntity retJsonInCompanyTotal = inputDataService.findBiInputDataByDayList(biInputDataVO);
            log.info("queryInputCompanyTotalBI_result:" + JSONObject.toJSONString(retJsonInCompanyTotal));

            //有命中规则进件
            log.info("queryHitRuleCountBI_param:" + JSONObject.toJSONString(biRuleDataVO));
            RespEntity retJsonHitRule = iRuleDataService.findHitRuleCompanyByDayList(biRuleDataVO);
            log.info("queryHitRuleCountBI_result:" + JSONObject.toJSONString(retJsonHitRule));

            if (StringUtils.equals("00", retJsonInCompanyTotal.getCode()) && StringUtils.equals("00", retJsonHitRule.getCode())) {
                JSONObject data = new JSONObject();
                data.put("inputCompany", retJsonInCompanyTotal.getData());
                data.put("hitRule", retJsonHitRule.getData());
                ret.changeRespEntity(RespCode.SUCCESS, data);
            } else {
                ret.setMsg("进件总数与有命中规则进件情况 查询失败");
                return ret;
            }
            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("biz-report调用biz-intelligence微服务失败");
            ret.setCode("500");
            return ret;
        }
    }

    /**
     * 参与评分进件情况
     */
    @GetMapping("/queryCompanyScoreBI")
    public RespEntity queryCompanyScoreBI(BiInputDataVO biInputDataVO, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            String apiCode = (String) session.getAttribute("apiCode");
            if (StringUtils.isEmpty(apiCode)) {
                ret.setMsg("您的登陆信息已过期，请重新登陆");
                return ret;
            }
            biInputDataVO.setApiCode(apiCode);


            log.info("queryCompanyScoreBI_param:" + JSONObject.toJSONString(biInputDataVO));
            RespEntity retJson = inputDataService.findCompanyScoreByDayList(biInputDataVO);
            log.info("queryCompanyScoreBI_result:" + JSONObject.toJSONString(retJson));
            if (StringUtils.equals("00", retJson.getCode())) {
                ret.changeRespEntity(RespCode.SUCCESS, retJson.getData());
            }else {
                ret.setCode(retJson.getCode());
                ret.setMsg(retJson.getMsg());
            }
            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("biz-report调用biz-intelligence微服务失败");
            ret.setCode("500");
            return ret;
        }
    }

    /**
     * 进件总数与 参与评分进件情况
     */
    @GetMapping("/queryInCompanyAndScoreBI")
    public RespEntity queryInCompanyAndScoreBI(BiInputDataVO biInputDataVO, BiRuleDataVO biRuleDataVO, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            String apiCode = (String) session.getAttribute("apiCode");
            if (StringUtils.isEmpty(apiCode)) {
                ret.setMsg("您的登陆信息已过期，请重新登陆");
                return ret;
            }
            biRuleDataVO.setApiCode(apiCode);
            biInputDataVO.setApiCode(apiCode);

            //进件总数
            log.info("queryInputCompanyTotalBI_param:" + JSONObject.toJSONString(biInputDataVO));
            RespEntity retJsonInCompanyTotal = inputDataService.findBiInputDataByDayList(biInputDataVO);
            log.info("queryInputCompanyTotalBI_result:" + JSONObject.toJSONString(retJsonInCompanyTotal));

            //参与评分进件
            log.info("queryCompanyScoreBI_param:" + JSONObject.toJSONString(biRuleDataVO));
            RespEntity retJsonCompanyScore = inputDataService.findCompanyScoreByDayList(biInputDataVO);
            log.info("queryCompanyScoreBI_result:" + JSONObject.toJSONString(retJsonCompanyScore));

            if (StringUtils.equals("00", retJsonInCompanyTotal.getCode()) && StringUtils.equals("00", retJsonCompanyScore.getCode())) {
                JSONObject data = new JSONObject();
                data.put("inputCompany", retJsonInCompanyTotal.getData());
                data.put("scoreCompany", retJsonCompanyScore.getData());
                ret.changeRespEntity(RespCode.SUCCESS, data);
            } else {
                ret.setMsg("进件总数与参与评分进件情况查询失败");
                return ret;
            }
            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("biz-report调用biz-intelligence微服务失败");
            ret.setCode("500");
            return ret;
        }
    }

    /**
     * BI查询模板列表（包含apicode下的全部类型）
     * @throws Exception
     */
    @RequestMapping(value = "/queryModuleTypeListBI" , method = RequestMethod.GET)
    @ResponseBody
    public RespEntity queryModuleTypeList(@RequestParam(value = "inputType", defaultValue = "1")Integer inputType,
                                          HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN,null);
        JSONObject jo = new JSONObject();
        List<ModuleTypeVO> list = new ArrayList<>();
        ModuleTypeVO module = new ModuleTypeVO();
        module.setModuleTypeId(0);
        module.setModuleTypeName("全部");
        module.setReportType(0);
        module.setStatus("1");
        list.add(module);
        if(Objects.equals(2,inputType)){
            jo.put("total",list.size());
            jo.put("rows", ReportUtils.getObjectListASC(list));//先按照ModuleTypeId 正序排列
            ret.changeRespEntity(RespCode.SUCCESS,jo);
            return ret;
        }
        String apiCode = (String)session.getAttribute("apiCode");
        if(StringUtils.isEmpty(apiCode)){
            ret.setMsg("您的登陆信息已过期，请重新登陆");
            return ret;
        }
        try {
            ModuleTypeVO moduleType=new ModuleTypeVO();
            moduleType.setApiCode(apiCode);
            List<ModuleTypeVO> retList = reportTaskService.queryModuleTypeList(moduleType);
            if(!CollectionUtils.isEmpty(retList)){
                list.addAll(retList);
            }
            jo.put("total",list.size());
            jo.put("rows", ReportUtils.getObjectListASC(list));//先按照ModuleTypeId 正序排列
            ret.changeRespEntity(RespCode.SUCCESS,jo);
        }catch (Exception e){
            e.printStackTrace();
            log.info("查询模板列表失败");
            return ret;
        }
        return ret;
    }

    /**
     * 命中最多规则情况
     */
    @GetMapping("/findHitRuleMostList")
    public RespEntity findHitRuleMostList(BiRuleDataVO biRuleDataVO, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            String apiCode = (String) session.getAttribute("apiCode");
            if (StringUtils.isEmpty(apiCode)) {
                ret.setMsg("您的登陆信息已过期，请重新登陆");
                return ret;
            }
            biRuleDataVO.setApiCode(apiCode);
            log.info("findHitRuleMostList_param:" + JSONObject.toJSONString(biRuleDataVO));
            RespEntity retJson = iRuleDataService.findHitRuleMostByDayList(biRuleDataVO);
            log.info("findHitRuleMostList_result:" + JSONObject.toJSONString(retJson));
            if (StringUtils.equals("00", retJson.getCode())) {
                ret.changeRespEntity(RespCode.SUCCESS, retJson.getData());
            }else {
                ret.setCode(retJson.getCode());
                ret.setMsg(retJson.getCode());
            }

            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            log.info("biz-report调用biz-intelligence微服务失败");
            ret.setCode("500");
            return ret;
        }
    }

    /**
     * 评分分布区间情况
     */
    @GetMapping("/queryScoreIntervalBI")
    public RespEntity queryScoreInterval(BiInputDataVO biInputDataVO, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            String apiCode = (String) session.getAttribute("apiCode");
            if (StringUtils.isEmpty(apiCode)) {
                ret.setMsg("您的登陆信息已过期，请重新登陆");
                return ret;
            }
            biInputDataVO.setApiCode(apiCode);
            log.info("queryScoreInterval_param:" + JSONObject.toJSONString(biInputDataVO));
            RespEntity retJson = inputDataService.findCompanyScoreIntervalByDayList(biInputDataVO);
            log.info("queryScoreInterval_result:" + JSONObject.toJSONString(retJson));
            if (StringUtils.equals("00", retJson.getCode())) {
                ret.changeRespEntity(RespCode.SUCCESS, retJson.getData());
            } else {
                ret.setCode(ret.getCode());
                ret.setMsg(retJson.getMsg());
            }
            return ret;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.info("biz-report调用biz-intelligence微服务失败");
            ret.setCode("500");
            return ret;
        }
    }

    @GetMapping("/inputGroupTendency")
    public RespEntity queryInputGroupTendency(@RequestParam(value = "interval", required = false) String interval,
                                              @RequestParam(value = "reportType", defaultValue = "0") String reportType,
                                              @RequestParam(value = "moduleTypeId", defaultValue = "0") String moduleTypeId,
                                              @RequestParam(value = "industryId", defaultValue = "0") String industryId,
                                              @RequestParam(value = "groupId", defaultValue = "0") String groupId,
                                              @RequestParam(value = "startDate", required = false) String startDate,
                                              @RequestParam(value = "endDate", required = false) String endDate,
                                              HttpSession session) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json;charset=utf-8");
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = (String) session.getAttribute("apiCode");
        String userGroupId = session.getAttribute("groupId").toString();
        String userType = session.getAttribute("userType").toString();
        if (StringUtils.equals("0", userType)) {
            userGroupId = groupId;
        }
        BiReportQueryCriteriaVO biReportQueryCriteriaVO = new BiReportQueryCriteriaVO();
        biReportQueryCriteriaVO.setGroupId(Integer.parseInt(userGroupId));
        biReportQueryCriteriaVO.setIndustryId(Integer.parseInt(industryId));
        biReportQueryCriteriaVO.setApiCode(apiCode);
        DateTime now = DateTime.now();
        if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            startDate = now.toString("yyyy-MM-dd");
            endDate = startDate;
        } else if (StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            startDate = endDate;
        } else if (StringUtils.isNotEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            endDate = now.toString("yyyy-MM-dd");
        }
        biReportQueryCriteriaVO.setStartDate(startDate);
        biReportQueryCriteriaVO.setEndDate(endDate);
        biReportQueryCriteriaVO.setInterval(interval);
        biReportQueryCriteriaVO.setReportType(Integer.parseInt(reportType));
        biReportQueryCriteriaVO.setModuleTypeId(Integer.parseInt(moduleTypeId));
        log.info("inputGroupTendency_param:" + JSONObject.toJSONString(biReportQueryCriteriaVO));
        try {
            BiJsonResultVO biJsonResultVO = inputGroupTendencyService.findBiJsonResultByBiReportQueryCriteriaVOFromMysSql(biReportQueryCriteriaVO);
            if (null != biJsonResultVO) {
                ret.changeRespEntity(RespCode.SUCCESS, biJsonResultVO);
            }
            log.info("inputGroupTendency_result:" + JSONObject.toJSONString(biJsonResultVO));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ret;
    }

    @GetMapping("/ruleHitList")
    public RespEntity queryRuleHitList(@RequestParam(value = "interval", required = false) String interval,
                                       @RequestParam(value = "moduleTypeId", defaultValue = "0") String moduleTypeId,
                                       @RequestParam(value = "reportType", defaultValue = "0") String reportType,
                                       @RequestParam(value = "industryId", defaultValue = "0") String industryId,
                                       @RequestParam(value = "groupId", defaultValue = "0") String groupId,
                                       @RequestParam(value = "startDate", required = false) String startDate,
                                       @RequestParam(value = "endDate", required = false) String endDate,
                                       @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                                       HttpSession session) {
        BiReportQueryCriteriaVO biReportQueryCriteriaVO = new BiReportQueryCriteriaVO();
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = (String) session.getAttribute("apiCode");
        String userGroupId = session.getAttribute("groupId").toString();
        String userType = session.getAttribute("userType").toString();
        if (StringUtils.equals("0", userType)) {
            userGroupId = groupId;
        }
        biReportQueryCriteriaVO.setGroupId(Integer.parseInt(userGroupId));
        biReportQueryCriteriaVO.setIndustryId(Integer.parseInt(industryId));
        biReportQueryCriteriaVO.setApiCode(apiCode);
        DateTime now = DateTime.now();
        if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            startDate = now.toString("yyyy-MM-dd");
            endDate = startDate;
        } else if (StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            startDate = endDate;
        } else if (StringUtils.isNotEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            endDate = now.toString("yyyy-MM-dd");
        }

        biReportQueryCriteriaVO.setStartDate(startDate);
        biReportQueryCriteriaVO.setEndDate(endDate);
        biReportQueryCriteriaVO.setInterval(interval);
        biReportQueryCriteriaVO.setReportType(Integer.parseInt(reportType));
        biReportQueryCriteriaVO.setPageNo(pageNo);
        biReportQueryCriteriaVO.setModuleTypeId(Integer.parseInt(moduleTypeId));
        biReportQueryCriteriaVO.setPageSize(pageSize);
        log.info("ruleHitList_param:" + JSONObject.toJSONString(biReportQueryCriteriaVO));
        Page<BiRuleDataVO> page = PageHelper.startPage(biReportQueryCriteriaVO.getPageNo(), biReportQueryCriteriaVO.getPageSize());
        try {
            ruleHitDetailService.findBiJsonResultByBiReportQueryCriteriaVOByMysql(biReportQueryCriteriaVO);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", page.getTotal());
        jsonObject.put("biDataList", page.getResult());
        jsonObject.put("pageNo", biReportQueryCriteriaVO.getPageNo());
        jsonObject.put("pageSize", biReportQueryCriteriaVO.getPageSize());
        if (null != page.getResult()) {
            log.info("ruleHitList_result:" + JSONArray.toJSONString(page.getResult()));
        }
        ret.changeRespEntity(RespCode.SUCCESS, jsonObject);
        return ret;
    }

    /**
     * 查询用户分组
     * @param request
     * @return
     */
    @GetMapping("/getGroupList")
    public RespEntity getGroupList(@RequestParam(value = "inputType", defaultValue = "1") Integer inputType,
                                   HttpServletRequest request) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        if (Objects.equals(2, inputType)) {
            JSONArray groupList = new JSONArray();
            JSONObject group = new JSONObject();
            group.put("id", "0");
            group.put("name", "全部");
            groupList.add(group);
            ret.changeRespEntity(RespCode.SUCCESS, groupList);
            return ret;
        }

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", request.getHeader("cookie"));
        String jsonResult = restTemplate.exchange("http://AUTH-SERVICE/auth/group/getGroupList", HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class).getBody();
        ret.changeRespEntity(RespCode.SUCCESS, JSONObject.parseObject(jsonResult).getJSONArray("result"));
        return ret;
    }

    /**
     * 下载命中明细
     * @param interval          周期
     * @param reportType        报告类型编号
     * @param industryId        场景编号
     * @param groupId           用户分组编号
     * @param startDate         开始日期（用户自定义日期）
     * @param endDate            结束日期（用户自定义日期）
     * @param session
     *
     */
    @GetMapping("/getDownloadRuleHitListParam")
    public RespEntity downloadRuleHitList(@RequestParam(value = "interval",required = false)String interval,
                                          @RequestParam(value = "reportType", defaultValue = "0")String reportType,
                                          @RequestParam(value = "moduleTypeId", defaultValue = "0")String moduleTypeId,
                                          @RequestParam(value = "industryId", defaultValue = "0")String industryId,
                                          @RequestParam(value = "groupId", defaultValue = "0")String groupId,
                                          @RequestParam(value = "startDate",required = false)String startDate,
                                          @RequestParam(value = "endDate",required = false)String endDate,
                                          HttpSession session) {
        String apiCode = (String) session.getAttribute("apiCode");
        String userGroupId = session.getAttribute("groupId").toString();
        String userType = session.getAttribute("userType").toString();
        if (StringUtils.equals("0", userType)) {
            userGroupId = groupId;
        }
        JSONObject reqParam = new JSONObject();
        reqParam.put("groupId", userGroupId);
        reqParam.put("industryId", industryId);
        reqParam.put("apiCode", apiCode);
        DateTime now = DateTime.now();
        if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            startDate = now.toString("yyyy-MM-dd");
            endDate = startDate;
        } else if (StringUtils.isEmpty(startDate) && StringUtils.isNotEmpty(endDate)) {
            startDate = endDate;
        } else if (StringUtils.isNotEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            endDate = now.toString("yyyy-MM-dd");
        }
        reqParam.put("startDate", startDate);
        reqParam.put("endDate", endDate);
        reqParam.put("interval", interval);
        reqParam.put("moduleTypeId", moduleTypeId);
        reqParam.put("reportType", reportType);
        reqParam.put("pageNo", 1);
        reqParam.put("pageSize", Integer.MAX_VALUE);
        log.info("ruleHitList_param:" + reqParam.toJSONString());
        session.setAttribute("downloadRuleHitListJson", reqParam.toJSONString());
        JSONObject data = new JSONObject();
        data.put("downloadStatus", "1");
        RespEntity ret = new RespEntity(RespCode.SUCCESS, data);
        return ret;
    }

    @GetMapping("/downloadRuleHitList")
    public void downloadRuleHitList(HttpSession session,HttpServletResponse response) {
        Object reqParamJson = session.getAttribute("downloadRuleHitListJson");
        if (null != reqParamJson) {
            String headerStr = "命中规则详情";//文件名
            try {
                headerStr = new String(headerStr.getBytes("gb2312"), "ISO8859-1");// headerString为中文时转码
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
            response.setHeader("Content-disposition", "attachment; filename="
                    + headerStr + ".xlsx");// filename是下载的xls的名，建议最好用英文
            response.setContentType("application/msexcel;charset=UTF-8");// 设置类型
            response.setHeader("Pragma", "No-cache");// 设置头
            response.setHeader("Cache-Control", "no-cache");// 设置头
            response.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");// 设置头
            response.setDateHeader("Expires", 0);// 设置日期头


            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "application/json;charset=utf-8");
            BiReportQueryCriteriaVO biReportQueryCriteriaVO = JSONObject.parseObject(reqParamJson.toString(), BiReportQueryCriteriaVO.class);
            session.removeAttribute("downloadRuleHitListJson");
            try {
                List<BiRuleDataVO> dataList = ruleHitDetailService.findBiJsonResultByBiReportQueryCriteriaVOByMysql(biReportQueryCriteriaVO);
                if (!CollectionUtils.isEmpty(dataList)) {
                    try (
                            Workbook workbook = ExcelUtil.ruleHitListWorkBookCreate(dataList);
                    ) {
                        workbook.write(response.getOutputStream());
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @GetMapping("/queryInputFileDetailHistory")
    public RespEntity queryInputFileDetailHistory(@RequestParam(value = "companyName",required = false)String companyName ,
                                                  @RequestParam(value = "username",required = false)String username ,
                                                  @RequestParam(value = "groupId", defaultValue = "0")Integer groupId ,
                                                  @RequestParam(value = "reportType", defaultValue = "0")Integer reportType,
                                                  @RequestParam(value = "moduleTypeId", defaultValue = "0")Integer moduleTypeId,
                                                  @RequestParam(value = "status", defaultValue = "0")Integer status,
                                                  @RequestParam(value = "startDate",required = false)String startDate,
                                                  @RequestParam(value = "endDate",required = false)String endDate,
                                                  @RequestParam(value = "pageNo", defaultValue = "1")Integer pageNo,
                                                  @RequestParam(value = "pageSize", defaultValue = "10")Integer pageSize,
                                                  @RequestParam(value = "strategyResult", defaultValue = "-2")String strategyResult,
                                                  @RequestParam(value = "industryId", defaultValue = "0")Integer industryId,
                                                  @RequestParam(value = "inputType", defaultValue = "1")Integer inputType,
                                                  HttpServletRequest request){
        RespEntity ret = new RespEntity(RespCode.WARN,null);
        BIInputFileDetailHistoryVO biInputFileDetailHistoryVO = new BIInputFileDetailHistoryVO();
        biInputFileDetailHistoryVO.setKeyNo(companyName);
        if("全部".equals(username)){
           username = StringUtils.EMPTY;
        }
        biInputFileDetailHistoryVO.setUsername(username);
        biInputFileDetailHistoryVO.setGroupId(groupId);
        biInputFileDetailHistoryVO.setReportType(reportType);
        biInputFileDetailHistoryVO.setModuleTypeId(moduleTypeId);
        biInputFileDetailHistoryVO.setStatus(status);
        biInputFileDetailHistoryVO.setStartDate(startDate);
        biInputFileDetailHistoryVO.setEndDate(endDate);
        biInputFileDetailHistoryVO.setIndustryId(industryId);
        biInputFileDetailHistoryVO.setStrategyResult(strategyResult);
        biInputFileDetailHistoryVO.setInputType(inputType);

        String apiCode = request.getSession().getAttribute("apiCode").toString();
        String userType = request.getSession().getAttribute("userType").toString();
        String userGroupId = request.getSession().getAttribute("groupId").toString();
        String userId = request.getSession().getAttribute("userId").toString();

        biInputFileDetailHistoryVO.setApiCode(apiCode);
        if(!StringUtils.equals("0",userType)){
            biInputFileDetailHistoryVO.setGroupId(Integer.parseInt(userGroupId));
            if(StringUtils.equals("1",userType)){
                biInputFileDetailHistoryVO.setUserId(Integer.parseInt(userId));
            }
        }else{
            biInputFileDetailHistoryVO.setGroupId(Integer.parseInt(userGroupId));
        }
        if(StringUtils.isNotEmpty(startDate)&&!StringUtils.equals("null",startDate.toLowerCase().trim())){
            biInputFileDetailHistoryVO.setStartDate(startDate.concat(" 00:00:00"));
        }
        if(StringUtils.isNotEmpty(endDate)&&!StringUtils.equals("null",endDate.toLowerCase().trim())){
            biInputFileDetailHistoryVO.setEndDate(endDate.concat(" 23:59:59"));
        }
        String columnHead1 = biInputFileDetailHistoryService.findColumnHead(biInputFileDetailHistoryVO);
        String relatedPersonHead1 = relatedPersonService.findRelatedPersonHead(biInputFileDetailHistoryVO);
        biInputFileDetailHistoryVO.setRelatedPersonHead(relatedPersonHead1);
        Page<BIInputFileDetailHistoryVO> page = PageHelper.startPage(pageNo, pageSize);
        biInputFileDetailHistoryService.findListByPage(biInputFileDetailHistoryVO);
        List<BIInputFileDetailHistoryVO> list = new ArrayList<>();
        if(!CollectionUtils.isEmpty(page.getResult())){
            page.getResult().forEach(input->{
                input.setRelatedPersonHead(relatedPersonHead1);
                relatedPersonService.findRelatedPersons(input);
                list.add(input);
            });
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total",page.getTotal());
        jsonObject.put("rows",list);
        jsonObject.put("columnHead",columnHead1);
        jsonObject.put("relatedPersonHead",biInputFileDetailHistoryVO.getRelatedPersonHead());

        List<IndustryInfoVO> industryInfoVOList =  industryInfoService.findAllIndustryInfoVOAndVersionList();
        Map<Integer,String> industryNameMap = new HashMap<>();
        industryInfoVOList.forEach(industryInfoVO -> {
            industryNameMap.put(industryInfoVO.getIndustryId(),industryInfoVO.getIndustryName());
        });
        JSONObject retData = new JSONObject();
        retData.put("total",jsonObject.getString("total"));
        JSONArray rows = jsonObject.getJSONArray("rows");
        String columnHead = jsonObject.getString("columnHead");
        List<String> headList = new ArrayList<>();
        headList.addAll(new ArrayList<>(Arrays.asList(columnHead.split("_"))));
        String relatedPersonHead = jsonObject.getString("relatedPersonHead");
        List<String> relatedPersonHeadList = new ArrayList<>();
        List<String> relatedPersonHeadListAll = new ArrayList<>();
        if(StringUtils.isNotEmpty(relatedPersonHead)){
            relatedPersonHeadListAll.addAll(new ArrayList<>(Arrays.asList(relatedPersonHead.split("_"))));
            relatedPersonHeadList.addAll(new ArrayList<>(relatedPersonHeadListAll.subList(0,relatedPersonHeadListAll.size()/2)));
        }
        List<List<String>> companyList = new ArrayList<>();
        List<String> headFinalList = new ArrayList<>();
        if(rows.size()>0){
            for(int i=0;i<rows.size();i++){
                List<String> company = new ArrayList<>();
                JSONObject companyJsonObject = rows.getJSONObject(i);
                company.add(companyJsonObject.getString("inputFileDetailId"));
                JSONArray personVOList = companyJsonObject.getJSONArray("personVOList");
                headList.forEach(head->{
                    if(StringUtils.isEmpty(companyJsonObject.getString(Constants.DetailPropMap.get(head)))){
                        company.add("-");
                    }else{
                        company.add(companyJsonObject.getString(Constants.DetailPropMap.get(head)));
                    }
                });
                if(Objects.equals(1,inputType)){
                    Integer industryIdTmp = companyJsonObject.getInteger("industryId");
                    String industryName = null==industryIdTmp?StringUtils.EMPTY:industryNameMap.get(industryIdTmp);
                    company.add(StringUtils.isNotEmpty(industryName)?industryName:"-");
                }

                company.add(StringUtils.equals("2",companyJsonObject.getString("status"))?"失败":(StringUtils.equals("0",companyJsonObject.getString("status"))?"生成中":"成功"));
                company.add(companyJsonObject.getString("strategyResultCN"));
                company.add(companyJsonObject.getString("realname"));
                company.add(companyJsonObject.getString("username"));
                company.add(companyJsonObject.getString("groupName"));
                company.add(companyJsonObject.getString("createTime"));
                if(relatedPersonHeadList.size()>0){
                    personVOList.forEach(personObj->{
                        JSONObject person = (JSONObject)personObj;
                        relatedPersonHeadList.forEach(head->{
                            if(StringUtils.isEmpty(person.getString(Constants.DetailPropMap.get(head)))){
                                company.add("-");
                            }else{
                                company.add(person.getString(Constants.DetailPropMap.get(head)));
                            }
                        });
                    });
                }
                companyList.add(company);
            }
            headList.add(0,"进件编号");
            if(Objects.equals(1,inputType)){
                headList.add("场景");
            }
            headList.add("报告状态");
            headList.add("策略建议");
            headList.add("姓名");
            headList.add("账户");
            headList.add("分组");
            headList.add("进件时间");
            headList.addAll(relatedPersonHeadListAll);

            if(!CollectionUtils.isEmpty(headList)){
                headList.forEach(head->{
                    headFinalList.add(head.replaceAll("法人代表","申请人"));
                });
            }
            retData.put("companyList",companyList);
        }else{
            headList.add("进件编号");
            if(!StringUtils.equals("0",moduleTypeId.toString())){
                try {
                    headList.addAll(new ArrayList<>(Arrays.asList(columnHead.split("_"))));
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }else{
                headList.addAll(new ArrayList<>(Arrays.asList(bizInputParamAll.split("_"))));
            }
            if(Objects.equals(1,inputType)){
                headList.add("场景");
            }
            headList.add("报告状态");
            headList.add("策略建议");
            headList.add("姓名");
            headList.add("账户");
            headList.add("分组");
            headList.add("进件日期");
            headList.addAll(relatedPersonHeadListAll);
            //兼容法人代表和申请人
            if(!CollectionUtils.isEmpty(headList)){
                headList.forEach(head->{
                    headFinalList.add(head.replaceAll("法人代表","申请人"));
                });
            }
            retData.put("companyList",Collections.emptyList());
        }
        retData.put("headList",headFinalList);
        ret.changeRespEntity(RespCode.SUCCESS,retData);
        return ret;
    }

}
