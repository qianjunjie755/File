package com.biz.credit.service;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.BiRuleDataVO;

public interface IRuleDataService {
    //按照‘天’维度查询进件详情
    RespEntity findBiRuleDataByDayList(BiRuleDataVO biRuleDataVO) throws  Exception;
    //按照‘月份’维度查询进件详情
    // List<BiRuleDataRes> findBiRuleDataByMonthList(BiRuleDataVO biRuleDataVO) throws  Exception;
    // 按照‘年份’维度查询进件详情
    //List<BiRuleDataRes> findBiRuleDataByYearList(BiRuleDataVO biRuleDataVO) throws  Exception;

    //按照‘天’维度查询进件详情
    RespEntity findHitRuleCompanyByDayList(BiRuleDataVO biRuleDataVO) throws  Exception;
    //按照‘月份’维度查询进件详情
    // List<BiRuleDataRes> findHitRuleCompanyByMonthList(BiRuleDataVO biRuleDataVO) throws  Exception;
    // 按照‘年份’维度查询进件详情
    //List<BiRuleDataRes> findHitRuleCompanyByYearList(BiRuleDataVO biRuleDataVO) throws  Exception;

    //按照‘天’维度查询进件详情
    RespEntity findHitRuleMostByDayList(BiRuleDataVO biRuleDataVO) throws  Exception;
    //按照‘月份’维度查询进件详情
    //List<BiRuleIdDataRes> findHitRuleMostByMonthList(BiRuleDataVO biRuleDataVO) throws  Exception;
    // 按照‘年份’维度查询进件详情
    //List<BiRuleIdDataRes> findHitRuleMostByYearList(BiRuleDataVO biRuleDataVO) throws  Exception;
}
