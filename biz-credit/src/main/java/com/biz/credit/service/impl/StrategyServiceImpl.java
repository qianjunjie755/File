package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.ListedCompanyDAO;
import com.biz.credit.dao.StrategyDAO;
import com.biz.credit.domain.Param;
import com.biz.credit.service.IReportApiService;
import com.biz.credit.service.IStrategyService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.ParamVO;
import com.biz.credit.vo.ProductVO;
import com.biz.credit.vo.reportApiVO.ApiInputFileDetailVO;
import com.biz.decision.BizDecide;
import com.biz.decision.common.Result;
import com.biz.decision.entity.EntityBasic;
import com.biz.decision.entity.Input;
import com.biz.decision.entity.Person;
import com.biz.decision.entity.Task;
import com.biz.decision.enums.EReqType;
import com.biz.decision.report.entity.InputData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class StrategyServiceImpl implements IStrategyService {

    @Autowired
    private BizDecide bizDecide;

    @Autowired
    private StrategyDAO strategyDAO;

    @Autowired
    private ListedCompanyDAO listedDAO;

    @Autowired
    private IReportApiService reportApiService;

    @Value("${biz.decision.report.domain-address}")
    private String domainAddress;

    @Value("${biz.decision.flow-params}")
    private String flowParamsKey;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${biz.decision.report.is-async-invoke-report}")
    private boolean isAsyncInvokeReport;

    /**
     * 获取客户决策流数据
     * @param apiCode
     * @return
     */
    @Override
    public JSONObject getDecideFlows(String apiCode) {
        JSONObject flows = new JSONObject();
        List<ParamVO> vos = strategyDAO.queryFlowParams(apiCode, null);
        if (!CollectionUtils.isEmpty(vos)) {
            Integer flowId = null;
            JSONArray params = null;
            JSONArray data = new JSONArray();
            for (ParamVO vo : vos) {
                if (!Objects.equals(flowId, vo.getFlowId())) {
                    flowId = vo.getFlowId();
                    JSONObject flow = new JSONObject();
                    flow.put("flowId", vo.getFlowId());
                    flow.put("flowName", vo.getFlowName());
                    params = new JSONArray();
                    flow.put("flowParams", params);
                    data.add(flow);
                }
                JSONObject param = new JSONObject();
                param.put("code", vo.getCode());
                param.put("name", vo.getName());
                param.put("type", vo.getType());
                param.put("required", vo.getRequired());
                params.add(param);
            }
            flows.put("flowList", data);
        }
        return flows;
    }

    /**
     * 启动决策流
     *
     * @param request
     * @return
     */
    @Override
    public JSONObject startDecideFlow(Map<String, Object> request) {
        JSONObject object = new JSONObject();
        object.put("code", "03");
        object.put("msg", "Params error!");
        //
        String jsonData = JSON.toJSONString(request);
        log.info("参数信息: {}", jsonData);
        JSONObject jsonObject = JSON.parseObject(jsonData);
        String apiCode = jsonObject.getString("apiCode");
        Integer flowId = jsonObject.getInteger("flowId");
        String flowTaskId = jsonObject.getString("flowTaskId");
        Integer type = jsonObject.getInteger("type");
        JSONObject params = jsonObject.getJSONObject("params");
        JSONObject platform = jsonObject.getJSONObject("_platform");
        //参数校验
        if (StringUtils.isBlank(apiCode) || flowId == null || StringUtils.isBlank(flowTaskId) ||
            type == null || params == null || platform == null) {
            log.error("apiCode|flowId|flowTaskId|type|进件参数信息不能为空!!");
            return object;
        }
        String appId = platform.getString("appId");
        if (StringUtils.isBlank(appId)) {
            log.error("appId不能为空!!");
            return object;
        }
        //
        EReqType reqType;
        //1-返回PDF 2-风控 3-返回JSON(新版) 4-返回综合信用评分
        if (Objects.equals(type, 1)) {
            reqType = EReqType.TRD_PDF;
        } else if (Objects.equals(type, 2)) {
            return object; //征信不支持
        } else if (Objects.equals(type, 3)) {
            reqType = EReqType.TRD_JSON;
        } else if (Objects.equals(type, 4)) {
            reqType = EReqType.TRD_SCORE;
        } else {
            return object;
        }

        //获取决策流参数
        List<ParamVO> flowParams = getFlowParams(apiCode, flowId);
        if (CollectionUtils.isEmpty(flowParams)) {
            return object;
        }
        //必要参数校验
        List<Param> paramList = new ArrayList<>();
        for (ParamVO vo : flowParams) {
            String value = params.getString(vo.getCode());
            if (Objects.equals(vo.getRequired(), 1)) {
                if (StringUtils.isBlank(value)) {
                    log.error("[{}][{}]为必填参数!!", vo.getCode(), vo.getName());
                    object.put("msg", "[" + vo.getCode() + "][" + vo.getName() + "]为必填参数!!");
                    return object;
                }
            }
            Param param = new Param();
            param.setCode(vo.getCode());
            param.setName(vo.getName());
            param.setType(vo.getType());
            param.setRequired(vo.getRequired());
            param.setValue(value);
            paramList.add(param);
        }
        //查询决策流对应Prod_Code和Prod_Name;
        ProductVO vo = strategyDAO.queryProductByFlowId(apiCode, flowId);
        if (vo == null) {
            log.error("未获取到决策流[{}]的模板信息!!", flowId);
            return object;
        }
        //
        String companyName = params.getString(Constants.COMPANY_NAME);
        String creditCode = params.getString(Constants.CREDIT_CODE);
        String companyBankId = params.getString(Constants.BANK_ID);
        String name = params.getString(Constants.NAME);
        String idNo = params.getString(Constants.ID_NO);
        String telNo = params.getString(Constants.CELL);
        String homeAddr = params.getString(Constants.HOME_ADDR);
        String workAddr = params.getString(Constants.WORK_ADDR);
        //
        ApiInputFileDetailVO inputVO = new ApiInputFileDetailVO();
        inputVO.setApiCode(apiCode);
        inputVO.setCreditCode(creditCode);
        inputVO.setKeyNo(companyName);
        inputVO.setBankId(companyBankId);
        inputVO.setName(name);
        inputVO.setIdNumber(idNo);
        inputVO.setCellPhone(telNo);
        inputVO.setHomeAddr(homeAddr);
        inputVO.setBizAddr(workAddr);
        inputVO.setAppId(appId);
        inputVO.setReportType(777);
        inputVO.setApiTaskId(flowTaskId);
        inputVO.setTaskType((reqType == null || reqType == EReqType.TRD_JSON) ? 2 : reqType.value()); //1- API_PDF 2-API_JSON 3-WEB_PDF 4-API_SCORE
        inputVO.setReportType(vo.getReportType());
        inputVO.setParams(paramList);
        long inputId = reportApiService.addApiInputFileDetailData(inputVO);
        //
        Task task = new Task();
        task.setTaskId(inputId);
        task.setFlowId(flowId);
        task.setTaskName(companyName + "(" + name + ")");
        task.setApiCode(apiCode);
        if (vo == null) {
            log.warn("未获取到决策流[{}]的模板信息!!", flowId);
        } else {
            task.setProdCode(vo.getProdCode());
            task.setProdName(vo.getProdName());
        }
        task.setReqType(reqType);
        Input input = new Input();
        input.setInputId(inputId);
        input.setCompanyName(companyName);
        input.setCreditCode(creditCode);
        if (StringUtils.isNotBlank(companyName)) {
            String stockCode = listedDAO.queryStockCode(companyName);
            if (StringUtils.isNotBlank(stockCode)) {
                input.addParam(Constants.STOCK_CODE, stockCode);
            }
        }
        //企业参数
        for (ParamVO v : flowParams) {
            if (Objects.equals(v.getType(), 1)) {
                input.addParam(v.getCode(), params.getString(v.getCode()));
            }
        }
        input.setAppId(appId);
        input.setApiTaskId(flowTaskId);
        //
        int count = 0;
        Person person = new Person();
        person.setName(name);
        person.setIdNo(idNo);
        person.setTelNo(telNo);
        //个人参数
        for (ParamVO v : flowParams) {
            if (Objects.equals(v.getType(), 2)) {
                String value = params.getString(v.getCode());
                if (value != null) {
                    count++;
                }
                person.addParam(v.getCode(), value);
            }
        }
        //个人有效参数大于0
        if (count > 0) {
            input.setPerson(person);
        }
        task.addInput(input);
        //异步(评分只能同步调用)
        if (isAsyncInvokeReport && reqType != EReqType.TRD_SCORE) {
            try {
                bizDecide.issue(task);
                object.put("code", "00");
                object.put("msg", "Success");
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                object.put("code", "03");// 失败
                object.put("msg", "Failed!");
            }
            log.info("客户[{}]决策流[{}]任务已启动[{}][{}]!", apiCode, flowId, appId, flowTaskId);
        }
        //同步
        else {
            Result ret = Result.ERROR();
            try {
                bizDecide.execute(task);
                ret = input.getResult();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            //报告处理成功
            if (ret.ok()) {
                String downloadUrl = StringUtils.EMPTY;
                String viewPdfUrl = StringUtils.EMPTY;
                if (reqType == EReqType.TRD_PDF) {
                    downloadUrl = domainAddress + "reportApi/downloadFileApi?id=" + input.getApiTaskId();
                    viewPdfUrl = domainAddress + "reportTask/viewPdf?inputFileDetailId=" + input.getInputId();
                }
                InputData inputData = (InputData) ret.getData();
                JSONObject result = new JSONObject();
                result.put("suggestion", inputData.getAdvise().stringValue());
                result.put("score", inputData.getScore());
                result.put("level", inputData.getLevel());
                result.put("rule_hit", (inputData.getJsonData() == null ? StringUtils.EMPTY : inputData.getJsonData()));
                result.put("download_url", downloadUrl);
                result.put("view_pdf_url", viewPdfUrl);
                object.put("code", "00");
                object.put("msg", "success");
                object.put("result", result);
            }
            //无数据
            else if (ret.noDataFound()) {
                object.put("code", "01");
                object.put("msg", "no data found");
            }
            //其他错误
            else {
                object.put("code", "03");
                object.put("msg", "system error!");
            }
            log.info("客户[{}]决策流[{}]任务已处理完成[{}][{}]!", apiCode, flowId, appId, flowTaskId);
        }

        return object;
    }

    /**
     * 获取决策流参数
     *
     * @param apiCode
     * @param flowId
     * @return
     */
    @Override
    public List<ParamVO> getFlowParams(String apiCode, Integer flowId) {
        List<String> params = null;
        HashOperations<String, String, List<String>> redisOps = redisTemplate.opsForHash();
        try {
            params = redisOps.get(flowParamsKey, String.valueOf(flowId));
        } catch (Throwable e) {
            log.warn("从缓存中获取决策流[ID={}]参数信息失败: {}", flowId, e.getMessage());
        }
        //
        List<ParamVO> flowParams = null;
        if (!CollectionUtils.isEmpty(params)) {
            flowParams = new ArrayList<>();
            for (String object : params) {
                flowParams.add(JSON.parseObject(object, ParamVO.class));
            }
        } else {
            List<ParamVO> vos = strategyDAO.queryFlowParams(apiCode, flowId);
            if (CollectionUtils.isEmpty(vos)) {
                log.error("客户[{}]决策流[{}]不存在!!", apiCode, flowId);
                return null;
            }
            flowParams = vos;
            params = new ArrayList<>();
            for (ParamVO vo : vos) {
                params.add(JSON.toJSONString(vo));
            }
            try {
                redisOps.put(flowParamsKey, String.valueOf(flowId), params);
                redisTemplate.expire(flowParamsKey, com.biz.decision.common.Constants.EXPIRE_TIME, TimeUnit.DAYS);
                log.info("决策流[ID={}]参数已更新到缓存!!", flowId);
            } catch (Exception e) {
                log.warn("决策流[ID={}]参数写入缓存失败: {}", flowId, e.getMessage());
            }
        }
        return flowParams;
    }

    @Override
    public EntityBasic getBasicInfo(String companyName) {
        if (StringUtils.isBlank(companyName)) {
            return null;
        }
        return strategyDAO.queryBasicInfo(companyName);
    }
}

