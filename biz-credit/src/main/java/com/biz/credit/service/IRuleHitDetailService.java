package com.biz.credit.service;

import com.biz.credit.vo.BiReportQueryCriteriaVO;
import com.biz.credit.vo.BiRuleDataVO;

import java.util.List;

public interface IRuleHitDetailService {
    List<BiRuleDataVO> findBiJsonResultByBiReportQueryCriteriaVOByMysql(BiReportQueryCriteriaVO biReportQueryCriteriaVO) throws Exception;
}
