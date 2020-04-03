package com.biz.credit.dao;

import com.biz.credit.domain.BiInputData;
import com.biz.credit.domain.responseData.BiInputDataRes;
import com.biz.credit.domain.responseData.BiInputDataScoreRes;
import com.biz.credit.vo.BiInputByGroupVO;
import com.biz.credit.vo.BiReportQueryCriteriaVO;
import com.biz.credit.vo.BiInputDataVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BiInputDataDAO {
    int addBiInputDataList(List<BiInputData> biInputDataList) throws Exception;
    int addApiBiInputDataList(List<BiInputData> biInputDataList) throws Exception;

    List<BiInputDataRes> findBiInputDataByMonthList(@Param("biInputData") BiInputData biInputData)throws Exception;
    List<BiInputDataRes> findBiInputDataByDayList(@Param("biInputData") BiInputData biInputData)throws Exception;
    List<BiInputDataRes> findBiInputDataByYearList(@Param("biInputData") BiInputData biInputData)throws Exception;
    List<BiInputDataRes> findCompanyScoreByDayList(@Param("biInputData") BiInputData biInputData)throws Exception;
    List<BiInputDataScoreRes> findCompanyScoreIntervalByDayList(@Param("biInputData") BiInputData biInputData)throws Exception;
    List<BiInputDataVO> findBiInputDataListByLimitId(@Param("startId")Long startId, @Param("endId") Long endId)throws Exception;
    List<BiInputByGroupVO> findBiInputDataListByGroupCondition(@Param("groupCondition") BiReportQueryCriteriaVO groupCondition);
    List<BiInputDataVO> findModuleTypeIdListByApicode(@Param("apiCode")String apiCode);
}
