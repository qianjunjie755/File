package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.BiRuleDataDAO;
import com.biz.credit.service.IRuleHitDetailService;
import com.biz.credit.utils.Constants;
import com.biz.credit.utils.DateUtil;
import com.biz.credit.vo.BiReportQueryCriteriaVO;
import com.biz.credit.vo.BiRuleDataVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Slf4j
@Service
public class RuleHitDetailServiceImpl implements IRuleHitDetailService {
    @Autowired
    private BiRuleDataDAO biRuleDataDAO;
    @Override
    public List<BiRuleDataVO> findBiJsonResultByBiReportQueryCriteriaVOByMysql(BiReportQueryCriteriaVO criteriaVO) throws Exception {
        List<BiRuleDataVO> biRuleDataVOList = null;
        DateTime dateTime = DateTime.now();
        String endDate = StringUtils.EMPTY;
        String startDate = StringUtils.EMPTY;
        if(StringUtils.isNotEmpty(criteriaVO.getInterval())){
            endDate = dateTime.plusDays(1).toString(Constants.DATETIME_PATTERN_DATE_YYYYMMDD);
            String unit = criteriaVO.getInterval().substring(criteriaVO.getInterval().length()-1);
            Integer interval = Integer.parseInt(criteriaVO.getInterval().substring(0,criteriaVO.getInterval().length()-1));
            if("d".equals(unit.toLowerCase())){
                startDate = dateTime.minusDays(interval).toString(Constants.DATETIME_PATTERN_DATE_YYYYMMDD);
            }else{
                startDate = dateTime.minusMonths(interval).toString(Constants.DATETIME_PATTERN_DATE_YYYYMM).concat("01");
            }
        }else if(StringUtils.isNotEmpty(criteriaVO.getEndDate())&&StringUtils.isNotEmpty(criteriaVO.getStartDate())){
            DateTime endTime = dateTime.withDate(Integer.parseInt(criteriaVO.getEndDate().split("-")[0]), Integer.parseInt(criteriaVO.getEndDate().split("-")[1]), Integer.parseInt(criteriaVO.getEndDate().split("-")[2]));
            endDate = endTime.plusDays(1).toString(Constants.DATETIME_PATTERN_DATE_YYYYMMDD);
            startDate = criteriaVO.getStartDate().replaceAll("-",StringUtils.EMPTY);
        }else{
            return biRuleDataVOList;
        }
        criteriaVO.setEndDate(endDate);
        criteriaVO.setStartDate(startDate);
        biRuleDataVOList = biRuleDataDAO.findBiRuleDataListByBiReportQueryCriteriaVO(criteriaVO);
        if(!CollectionUtils.isEmpty(biRuleDataVOList)){
            biRuleDataVOList.forEach(biRuleDataVO -> {
                if(StringUtils.isNotEmpty(biRuleDataVO.getThreshold())){
                    JSONObject jsonObject = JSONObject.parseObject(biRuleDataVO.getThreshold());
                    biRuleDataVO.setInterval((StringUtils.isEmpty(jsonObject.getString("interval"))||StringUtils.equals("无",jsonObject.getString("interval")))?"无":jsonObject.getString("interval"));
                    biRuleDataVO.setThreshold(StringUtils.isEmpty(jsonObject.getString("describe"))?StringUtils.EMPTY:jsonObject.getString("describe"));
                }
            });
        }
        return biRuleDataVOList;
    }
}
