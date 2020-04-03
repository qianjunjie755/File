package com.biz.credit.plugins;

import com.alibaba.fastjson.JSONObject;
import com.biz.decision.common.Result;
import com.biz.decision.entity.Api;
import com.biz.decision.entity.ApiInfo;
import com.biz.decision.entity.Input;
import com.biz.decision.report.params.IApiParam;
import com.biz.decision.report.params.MyApiParams;
import com.biz.decision.service.IInvokeService;
import com.biz.utils.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 上市公司股票代码参数获取
 */
@Slf4j
@Component
public class StockCodeParams implements IApiParam {

    @Autowired
    private IInvokeService service;

    @Override
    public void afterPropertiesSet() {
        MyApiParams.register("BizListedCompanyIPO", "1.0", this);
        MyApiParams.register("BizListedCompanyBalanceSheet", "1.0", this);
        MyApiParams.register("BizListedCompanyProincStatement", "1.0", this);
        MyApiParams.register("BizListedCompanyCashFlow", "1.0", this);
        MyApiParams.register("BizListedCompanyGuarantee", "1.0", this);
        MyApiParams.register("BizListedCompanyViolation", "1.0", this);
    }

    @Override
    public void make(String apiCode, Input input) {
        if (StringUtils.isBlank(apiCode) || input == null) {
            return;
        }
        String code = "stock_code";
        JSONObject params = input.getParams();
        if (params != null && params.containsKey(code)) {
            return;
        }
        ApiInfo apiInfo = service.getApiInfo(new Api("BizBaseInfo", "1.0"));
        if (apiInfo == null) {
            log.error("未获取到API[BizBaseInfo|1.0]信息!!");
            return;
        }
        Result result = service.callBasicApi(apiCode, input, apiInfo);
        if (!result.ok()) {
            log.error(result.getMessage());
            return;
        }
        JSONObject data = (JSONObject) result.getData();
        Object value = JsonPath.eval(data, "$.Result.IsOnStock");
        boolean isOnStock = Objects.equals("1", (value == null ? StringUtils.EMPTY : value.toString()));
        value = JsonPath.eval(data, "$.Result.StockNumber");
        String stockCode = value == null ? StringUtils.EMPTY : value.toString();
        input.addParam(code, stockCode);
        log.info("[{}]是否上市公司[{}], 股票代码[{}]!!", input.getCompanyName(), isOnStock, stockCode);
    }
}
