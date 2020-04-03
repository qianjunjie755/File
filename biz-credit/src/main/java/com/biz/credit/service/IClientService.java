package com.biz.credit.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.vo.CrmApiVO;

import java.util.List;

public interface IClientService {
    JSONObject updateClient(String apiCode, JSONArray productList) throws Exception;
    List<CrmApiVO> findCrmApiVOList(String apiCode, Integer industryId, Integer reportType);
}
