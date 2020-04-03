package com.biz.credit.service;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.domain.responseData.BiInputDataRes;
import com.biz.credit.vo.BiInputDataVO;

import java.util.List;

public interface IInputDataService {
    //按照‘月份’维度查询进件详情
    List<BiInputDataRes> findBiInputDataByMonthList(BiInputDataVO biInputDataVO) throws  Exception;
    //按照‘天’维度查询进件详情
    RespEntity findBiInputDataByDayList(BiInputDataVO biInputDataVO) throws  Exception;
    // 按照‘年份’维度查询进件详情
    List<BiInputDataRes> findBiInputDataByYearList(BiInputDataVO biInputDataVO) throws  Exception;

    //按照‘天’维度查询进件详情
    RespEntity findCompanyScoreByDayList(BiInputDataVO biInputDataVO) throws  Exception;

    //按照‘天’维度查询进件详情
    RespEntity findCompanyScoreIntervalByDayList(BiInputDataVO biInputData)throws Exception;
}
