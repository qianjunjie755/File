package com.biz.credit.dao;

import com.biz.credit.domain.CompanyCredit;
import com.biz.credit.vo.CompanyCreditListVO;
import com.biz.credit.vo.CompanyCreditModelVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CompanyCreditDAO {
    void insertBatch(@Param("list") List<CompanyCredit> list);

    List<CompanyCreditListVO> findByCompanyName(@Param("companyName") String companyName);

    List<CompanyCreditModelVO> findModelNameByModelCode(@Param("modelCode") Integer modelCode,
                                                        @Param("modelType") Integer modelType);
}
