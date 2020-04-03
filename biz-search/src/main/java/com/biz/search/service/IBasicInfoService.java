package com.biz.search.service;

import com.biz.search.entity.BasicInfo;
import com.biz.search.entity.BasicInfoHighLight;
import org.springframework.data.domain.Page;

public interface IBasicInfoService {
    int loadBasicInfo(int limit);
    Page<BasicInfo> queryByCompanyName(String companyName, int pageNo, int pageSize);
    Page<BasicInfoHighLight> queryHighLightByCompanyName(String companyName, int pageNo, int pageSize);
    Page<BasicInfo> queryByCreditCode(String creditCode, int pageNo, int pageSize);
    Page<BasicInfoHighLight> queryHighLightByCreditCode(String creditCode, int pageNo, int pageSize);
}
