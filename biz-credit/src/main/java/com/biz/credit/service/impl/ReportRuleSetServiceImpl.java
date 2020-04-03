package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.IndustryInfoDAO;
import com.biz.credit.dao.ReportRuleSetDAO;
import com.biz.credit.dao.StrategyDAO;
import com.biz.credit.dao.VariableParamPoolDAO;
import com.biz.credit.service.IReportRuleSetService;
import com.biz.credit.vo.StrategyVO;
import com.biz.config.rest.RestTemplateFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class ReportRuleSetServiceImpl implements IReportRuleSetService {
    @Autowired
    private ReportRuleSetDAO reportRuleSetDAO;
    @Autowired
    private VariableParamPoolDAO variableParamPoolDAO;
    @Autowired
    private RestTemplateFactory restTemplateFactory;
    @Autowired
    private StrategyDAO strategyDAO;
    @Autowired
    private IndustryInfoDAO industryInfoDAO;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String findColumnHeaderByStrategyId(Long strategyId) throws Exception {
        List<String> apiList = reportRuleSetDAO.findApiListByStrategyId(strategyId);
        StrategyVO strategy = strategyDAO.findStrategy(strategyId, null, null);
        if(2!=strategy.getIndustryId()&&1==strategy.getDoScore()){
            List<String> apiProdCodeList = industryInfoDAO.findApiProdCodeListByIndustryId(strategy.getIndustryId());
            apiProdCodeList.forEach(apiProdCode->{
                if(!apiList.contains(apiProdCode)){
                    apiList.add(apiProdCode);
                }
            });
            log.info("/downloadExcelTemplate apiProdCodeList:"+apiProdCodeList);
        }
        log.info("/downloadExcelTemplate apiList:"+apiList);
        String publicKeyInRedis =  stringRedisTemplate.opsForValue().get("biz_credit:publicKey");
        String url = "http://API-ADMIN-SERVICE/api_admin/queryVarsetApis?bizPublicKey=".concat(publicKeyInRedis);
        String apiListStr = restTemplateFactory.restTemplate().getForEntity(url,String.class).getBody();
        JSONArray jsonArray = JSONObject.parseObject(apiListStr).getJSONArray("data");
        Set<String> paramNameSet = new HashSet<>();
        for(int i=0;i<jsonArray.size();i++){
            JSONObject api =  jsonArray.getJSONObject(i);
            JSONObject sourceQueryData = api.getJSONObject("sourceQueryJsonData");
            String apiStr = sourceQueryData.getString("api").concat("_").concat(sourceQueryData.getString("version"));
            if(apiList.contains(apiStr)){
                JSONObject sourceResultData = api.getJSONObject("sourceResultJsonData");
                sourceQueryData.keySet().forEach(key->{
                    if(!StringUtils.equals("api",key.toLowerCase())&&!StringUtils.equals("version",key.toLowerCase())){
                        paramNameSet.add(key);
                    }
                });
                sourceResultData.keySet().forEach(key->{
                    if(!StringUtils.equals("api",key.toLowerCase())&&!StringUtils.equals("version",key.toLowerCase())&&!StringUtils.equals("taskid",key.toLowerCase())){
                        paramNameSet.add(key);
                    }
                });
            }
        }
        return variableParamPoolDAO.findDescriptionListByParamNames(paramNameSet);
    }
}
