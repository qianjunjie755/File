package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;

public interface ICompanySliceService {
    JSONObject getCompanySlice(String companyName) throws Exception;
}
