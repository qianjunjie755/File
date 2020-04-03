package com.biz.credit.dao;

import com.biz.credit.domain.BiRuleData;
import com.biz.credit.domain.responseData.BiRuleDataRes;
import com.biz.credit.domain.responseData.BiRuleIdDataRes;
import com.biz.credit.vo.BiReportQueryCriteriaVO;
import com.biz.credit.vo.BiRuleDataVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiRuleDataDAO {
    int addBiRuleDataList(List<BiRuleDataVO> biRuleDataList) throws Exception;
    int addApiBiRuleDataList(List<BiRuleDataVO> biRuleDataList) throws Exception;



    List<BiRuleDataRes> findBiRuleDataByDayList(@Param("biRuleData") BiRuleData biRuleData)throws Exception;
    List<BiRuleDataRes> findBiRuleDataByMonthList(@Param("biRuleData") BiRuleData biRuleData)throws Exception;
    List<BiRuleDataRes> findBiRuleDataByYearList(@Param("biRuleData") BiRuleData biRuleData)throws Exception;
    List<BiRuleDataRes> findHitRuleCompanyByDayList(@Param("biRuleData") BiRuleData biRuleData)throws Exception;
    List<BiRuleDataRes> findHitRuleCompanyByMonthList(@Param("biRuleData") BiRuleData biRuleData)throws Exception;
    List<BiRuleDataRes> findHitRuleCompanyByYearList(@Param("biRuleData") BiRuleData biRuleData)throws Exception;
    List<BiRuleIdDataRes> findHitRuleMostByDayList(@Param("query") BiRuleData biRuleData)throws Exception;
    List<BiRuleDataVO> findBiRuleDataListByBiReportQueryCriteriaVO(@Param("query") BiReportQueryCriteriaVO query) throws Exception;
}
