package com.biz.credit.plugins;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.biz.config.rest.RestTemplateFactory;
import com.biz.credit.dao.ListedCompanyDAO;
import com.biz.credit.service.ICompanySliceService;
import com.biz.credit.utils.Constants;
import com.biz.decision.common.Result;
import com.biz.decision.entity.*;
import com.biz.decision.plugins.IReportName;
import com.biz.decision.service.IInvokeService;
import com.biz.decision.utils.DecideUtils;
import com.biz.utils.BizUtils;
import com.biz.utils.BrCipher;
import com.biz.utils.convert.Convert;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CompanySliceServiceImpl implements IReportName, ICompanySliceService {

    @Value("${biz.api-code}")
    private String bizApiCode;

    @Value("${biz.decision.model-url}")
    private String apiModelUrl;

    @Autowired
    private ListedCompanyDAO dao;

    @Autowired
    private IInvokeService invokeService;

    @Autowired
    private RestTemplateFactory restTemplateFactory;

    @Override
    public String create(String apiCode, Input input) {
        String name = null;
        //将进件信息克隆一份, 避免notify的时候参数信息与submit不一致
        input = copyInput(input);
        if (StringUtils.isNotBlank(input.getCompanyName())) {
            String label = "小型企业";
            //查询上市公司股票代码
            JSONObject params = input.getParams();
            if (params != null && params.containsKey(Constants.STOCK_CODE)) {
                //获取企业基础信息
                Result result = getBaseInfo(apiCode, input);
                if (result.ok()) {
                    //获取上市公司基本信息
                    getCompanyIPO(bizApiCode, input);
                    getCompanyBalanceSheet(bizApiCode, input);
                    getCompanyProincStatement(bizApiCode, input);
                    //获取企业分层信息
                    JSONObject slice = getCompanyHierarchy(apiCode, input);
                    label = slice == null ? "小型企业" : slice.getString("scale");
                }
            }
            name = input.getCompanyName();
            if (StringUtils.isNotBlank(label)) {
                name = name + "\n[" + label + "]";
            }
        } else {
            name = input.getPerson().getName();
        }
        return name;
    }

    private Input copyInput(Input input) {
        Input _input = new Input();
        _input.setInputId(input.getInputId());
        _input.setCompanyName(input.getCompanyName());
        _input.setCreditCode(input.getCreditCode());
        JSONObject params = input.getParams();
        if (params != null) {
            JSONObject _params = new JSONObject();
            _params.putAll(params);
            _input.setParams(_params);
        }
        Person person = input.getPerson();
        if (person != null) {
            Person _person = new Person();
            _person.setIdNo(person.getIdNo());
            _person.setName(person.getName());
            _person.setTelNo(person.getTelNo());
            params = person.getParams();
            if (params != null) {
                JSONObject _params = new JSONObject();
                _params.putAll(params);
                _person.setParams(_params);
            }
            _input.setPerson(_person);
        }
        List<RelatedPerson> relateds = input.getRelatedPersons();
        if (!CollectionUtils.isEmpty(relateds)) {
            for (RelatedPerson related : relateds) {
                RelatedPerson _person = new RelatedPerson();
                _person.setId(related.getId());
                _person.setIdNo(related.getIdNo());
                _person.setName(related.getName());
                _person.setTelNo(related.getTelNo());
                params = related.getParams();
                if (params != null) {
                    JSONObject _params = new JSONObject();
                    _params.putAll(params);
                    _person.setParams(_params);
                }
                _input.addRelatedPerson(_person);
            }
        }
        return _input;
    }

    @Override
    public JSONObject getCompanySlice(String companyName) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "01");
        jsonObject.put("msg", "no data found!");

        if (StringUtils.isBlank(companyName)) {
            jsonObject.put("msg", "企业名称不能为空!!");
            return jsonObject;
        }
        //查询上市公司股票代码
        String stockCode = dao.queryStockCode(companyName);
        if (StringUtils.isNotBlank(stockCode)) {
            JSONObject slice = null;
            Input input = new Input();
            input.setCompanyName(companyName);
            input.addParam(Constants.COMPANY_NAME, companyName);
            input.addParam(Constants.STOCK_CODE, stockCode);
            //获取企业基础信息
            Result result = getBaseInfo(bizApiCode, input);
            if (result.ok()) {
                //获取上市公司基本信息
                getCompanyIPO(bizApiCode, input);
                getCompanyBalanceSheet(bizApiCode, input);
                getCompanyProincStatement(bizApiCode, input);
                //获取企业分层信息
                slice = getCompanyHierarchy(bizApiCode, input);
            }
            //
            if (slice == null) {
                slice = new JSONObject();
                slice.put("industry", "");
                slice.put("code", 2);
                slice.put("scale", "小型企业");
            }
            jsonObject.put("code", "00");
            jsonObject.put("msg", "Success!");
            jsonObject.put("result", slice);
        }
        return jsonObject;
    }

    private Result getBaseInfo(String apiCode, Input input) {
        Result result;
        String apiProdCode = "BizBaseInfo";
        String apiVersion = "1.0";
        ApiInfo apiInfo = invokeService.getApiInfo(new Api(apiProdCode, apiVersion));
        if (apiInfo == null) {
            log.error("未获取到API[{}|{}]信息!!", apiProdCode, apiVersion);
            result = Result.ERROR("未获取到API信息!");
        } else {
            //先获取企业基础信息
            result = invokeService.callBasicApi(apiCode, input, apiInfo);
            if (result.ok()) {
                JSONObject data = BizUtils.filterNull((JSONObject) result.getData());
                String keyNo = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.KeyNo"), (Convert) null);
                //企业类型
                String entType = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.EntType"), (Convert) null);
                //行业（门类+中类，如B061）
                String industryCode = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.Industry.IndustryCode"), (Convert) null);
                String middleCategoryCode = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.Industry.MiddleCategoryCode"), (Convert) null);
                String industry = industryCode + middleCategoryCode;
                //人员规模
                String employees = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.PersonScope"), (Convert) null);
                if (StringUtils.isNotBlank(employees)) {
                    employees = employees.replaceAll("[^\\d.]", "");
                }
                //
                input.addParam("key_no", keyNo);
                input.addParam("ent_type", entType);
                input.addParam("industry", industry);
                input.addParam("employees", employees);
            }
        }
        return result;
    }

    private Result getCompanyIPO(String apiCode, Input input) {
        Result result;
        String apiProdCode = "BizListedCompanyIPO";
        String apiVersion = "1.0";
        ApiInfo apiInfo = invokeService.getApiInfo(new Api(apiProdCode, apiVersion));
        if (apiInfo == null) {
            log.error("未获取到API[{}|{}]信息!!", apiProdCode, apiVersion);
            result = Result.ERROR("未获取到API信息!");
        } else {
            //获取上市公司基本信息
            result = invokeService.callBasicApi(apiCode, input, apiInfo);
            if (result.ok()) {
                JSONObject data = BizUtils.filterNull((JSONObject) result.getData());
                //人员规模
                String employees = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.CompanySSBaseInfoEntity.PersonNumber"), (Convert) null);
                if (StringUtils.isNotBlank(employees)) {
                    employees = employees.replaceAll("[^\\d.]", "");
                }
                input.addParam("employees", employees);
            }
        }
        return result;
    }

    private Result getCompanyBalanceSheet(String apiCode, Input input) {
        Result result;
        String apiProdCode = "BizListedCompanyBalanceSheet";
        String apiVersion = "1.0";
        ApiInfo apiInfo = invokeService.getApiInfo(new Api(apiProdCode, apiVersion));
        if (apiInfo == null) {
            log.error("未获取到API[{}|{}]信息!!", apiProdCode, apiVersion);
            result = Result.ERROR("未获取到API信息!");
        } else {
            //获取上市公司负债信息
            result = invokeService.callBasicApi(apiCode, input, apiInfo);
            if (result.ok()) {
                JSONObject data = BizUtils.filterNull((JSONObject) result.getData());
                //资产总额(元)
                String totalAssets = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.DetailData.BalanceSheet.TotalAssets"), (Convert) null);
                if (StringUtils.isNotBlank(totalAssets)) {
                    String _totalAssets = totalAssets.replaceAll("[^\\d.]", "");
                    if (StringUtils.isNotBlank(_totalAssets)) {
                        BigDecimal i_total = new BigDecimal(_totalAssets);
                        if (totalAssets.contains("亿")) {
                            i_total = i_total.multiply(new BigDecimal(10000 * 10000));
                        } else if (totalAssets.contains("万")) {
                            i_total = i_total.multiply(new BigDecimal(10000));
                        }
                        totalAssets = i_total.toPlainString();
                    }
                }
                input.addParam("total_assets", totalAssets);
            }
        }
        return result;
    }

    private Result getCompanyProincStatement(String apiCode, Input input) {
        Result result;
        String apiProdCode = "BizListedCompanyProincStatement";
        String apiVersion = "1.0";
        ApiInfo apiInfo = invokeService.getApiInfo(new Api(apiProdCode, apiVersion));
        if (apiInfo == null) {
            log.error("未获取到API[{}|{}]信息!!", apiProdCode, apiVersion);
            result = Result.ERROR("未获取到API信息!");
        } else {
            //获取上市公司利润信息
            result = invokeService.callBasicApi(apiCode, input, apiInfo);
            if (result.ok()) {
                JSONObject data = BizUtils.filterNull((JSONObject) result.getData());
                //营业收入(元)
                String taking = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.DetailData.IncomeStatement.OperatingIncome"), (Convert) null);
                if (StringUtils.isNotBlank(taking)) {
                    String _taking = taking.replaceAll("[^\\d.]", "");
                    if (StringUtils.isNotBlank(_taking)) {
                        BigDecimal i_total = new BigDecimal(_taking);
                        if (taking.contains("亿")) {
                            i_total = i_total.multiply(new BigDecimal(10000 * 10000));
                        } else if (taking.contains("万")) {
                            i_total = i_total.multiply(new BigDecimal(10000));
                        }
                        taking = i_total.toPlainString();
                    }
                }
                input.addParam("taking", taking);
            }
        }
        return result;
    }

    private JSONObject getCompanyHierarchy(String apiCode, Input input) {
        String apiProdCode = "BizCompanyHierarchy";
        String apiVersion = "1.0";
        ApiInfo apiInfo = invokeService.getApiInfo(new Api(apiProdCode, apiVersion));
        if (apiInfo == null) {
            log.error("未获取到API[{}|{}]信息!!", apiProdCode, apiVersion);
        } else {
            //先获取企业基础信息
            Result result = callModelApi(apiCode, input, apiInfo);
            if (result.ok()) {
                JSONObject data = BizUtils.filterNull((JSONObject) result.getData());
                //行业
                String industry = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.ind"), (Convert) null);
                //代码
                String code = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.code"), (Convert) null);
                //企业规模
                String scale = DecideUtils.dataConvert(JSONPath.eval(data, "$.Result.scale"), (Convert) null);
                /*String scale = StringUtils.EMPTY;
                if (Objects.equals(_scale, "0")) {
                    scale = "大型企业";
                } else if (Objects.equals(_scale, "1")) {
                    scale = "中型企业";
                } else if (Objects.equals(_scale, "2")) {
                    scale = "小型企业";
                } else if (Objects.equals(_scale, "3")) {
                    scale = "微型企业";
                } else if (Objects.equals(_scale, "4")) {
                    scale = "个体工商户";
                } else if (Objects.equals(_scale, "5")) {
                    scale = StringUtils.EMPTY;
                }*/
                JSONObject object = new JSONObject();
                object.put("industry", industry);
                object.put("code", code);
                object.put("scale", scale);
                return object;
            }
        }
        return null;
    }

    private Result callModelApi(String apiCode, Input input, ApiInfo apiInfo) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.valueOf(com.biz.decision.common.Constants.APPLICATION_FORM_URLENCODED));
        //请求消息体
        JSONObject apiParams = new JSONObject();
        apiParams.put(com.biz.decision.common.Constants.API_PROD_CODE, apiInfo.getApiProdCode());
        apiParams.put(com.biz.decision.common.Constants.API_VERSION, apiInfo.getApiVersion());
        JSONObject data = new JSONObject();
        apiParams.put("data", data);
        if (apiInfo.getParams() != null) {
            for (Param param : apiInfo.getParams()) {
                if (StringUtils.isNotBlank(param.getCode())) {
                    data.put(param.getKey(), DecideUtils.getArgsValue(param, input));
                }
            }
        }
        JSONObject plateForm = new JSONObject();
        plateForm.put(com.biz.decision.common.Constants.APP_ID, input.getAppId());
        apiParams.put(com.biz.decision.common.Constants.PLATFORM, plateForm);
        //组装请求数据
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(com.biz.decision.common.Constants.API_CODE, apiCode);
        map.add(com.biz.decision.common.Constants.API_DATA, apiParams == null ? null : apiParams.toJSONString());
        //API接口调用
        Result result;
        int i = 0;
        while (i < 3) {
            i++;
            try {
                log.info("进件[{}]API[{}]调用模型接口请求数据: {}", input.getInputId(), apiInfo, BrCipher.encode(apiParams));
                ResponseEntity<String> responseEntity = restTemplateFactory.getRestTemplate().postForEntity(apiModelUrl, new HttpEntity<>(map, httpHeaders), String.class);
                String respResult = responseEntity.getBody();
                log.info("进件[{}]API[{}]调用模型接口响应数据: {}", input.getInputId(), apiInfo, respResult);
                JSONObject resp = JSONObject.parseObject(respResult);
                int code = resp == null ? com.biz.decision.common.Constants.ERROR : resp.getIntValue(com.biz.decision.common.Constants.CODE);
                String message = resp == null ? "未知错误!" : resp.getString(com.biz.decision.common.Constants.MESSAGE);
                if (code == com.biz.decision.common.Constants.SUCCESS) {
                    result = Result.OK();
                } else if (code == com.biz.decision.common.Constants.SYSTEM_ERROR && Objects.equals(message, com.biz.decision.common.Constants.TIME_OUT)) {
                    if (i < 3) {
                        log.error("进件[{}]API[{}]调用模型接口超时, 1秒后重试!!", input.getInputId(), apiInfo);
                        Thread.sleep(1000);
                    }
                    continue;
                } else if (code == com.biz.decision.common.Constants.NO_DATA_FOUND) {
                    result = Result.NO_DATA_FOUND();
                } else {
                    result = Result.ERROR(message);
                    log.error("进件[{}]API[{}]调用模型接口返回错误: {}-{}", input.getInputId(), apiInfo, code, message);
                }
                result.setData(resp);
                return result;
            } catch (Exception e) {
                log.error("进件[" + input.getInputId() + "]API[" + apiInfo + "]调用模型接口失败: " + e.getMessage(), e);
                return Result.ERROR(e.getMessage());
            }
        }
        log.error("进件[{}]API[{}]调用模型接口超时!!", input.getInputId(), apiInfo);
        return Result.ERROR("API调用超时!!");
    }
}
