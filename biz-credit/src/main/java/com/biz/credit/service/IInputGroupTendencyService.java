package com.biz.credit.service;

import com.biz.credit.vo.BiJsonResultVO;
import com.biz.credit.vo.BiReportQueryCriteriaVO;

public interface IInputGroupTendencyService {
    BiJsonResultVO findBiJsonResultByBiReportQueryCriteriaVOFromMysSql(BiReportQueryCriteriaVO criteriaVO) throws Exception;
}
