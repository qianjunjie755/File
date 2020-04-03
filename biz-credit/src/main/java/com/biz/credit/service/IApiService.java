package com.biz.credit.service;

import com.biz.credit.vo.ApiVO;

import java.util.List;
import java.util.Map;

public interface IApiService {
    void syncApiInfo();

    List<ApiVO> getApiList(ApiVO apiVO);

    List<Map<String,Object>> getSourceCount(String apiCode);

    ApiVO getApiDetail(ApiVO apiVO);
}
