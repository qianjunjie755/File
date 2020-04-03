package com.biz.credit.service;

import com.biz.credit.vo.CompanyCreditListVO;
import com.biz.credit.vo.CompanyCreditModelVO;

import java.util.List;

public interface ICompanyCreditService {
    List<CompanyCreditListVO> findByCompanyName(String companyName);

    CompanyCreditModelVO findModelByTypeAndCode(Integer modelCode, Integer modelType);
}
