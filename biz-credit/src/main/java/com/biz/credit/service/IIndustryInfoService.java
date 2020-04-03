package com.biz.credit.service;

import com.biz.credit.vo.IndustryInfoVO;
import com.biz.credit.vo.IndustryInfoViewVO;

import java.util.List;

public interface IIndustryInfoService {
    List<IndustryInfoVO> findAllIndustryInfoVOList();
    List<IndustryInfoVO> findAllIndustryInfoVOAndVersionList();
    List<IndustryInfoViewVO> findAllIndustryInfoVOListView();
    IndustryInfoViewVO findByIndustryCode(String industryCode, String version);
}
