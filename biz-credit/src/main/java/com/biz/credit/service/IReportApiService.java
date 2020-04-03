package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.vo.reportApiVO.ApiInputFileDetailVO;

import javax.servlet.http.HttpServletRequest;

public interface IReportApiService {
     JSONObject htmlToPdfApi(HttpServletRequest request) ;
     long addApiInputFileDetail(ApiInputFileDetailVO apiInputFileDetailVO) ;
     ApiInputFileDetailVO queryApiInputFileDetailById(ApiInputFileDetailVO apiInputFileDetailVO) ;
     long addApiInputFileDetailData(ApiInputFileDetailVO apiInputFileDetailVO);
}
