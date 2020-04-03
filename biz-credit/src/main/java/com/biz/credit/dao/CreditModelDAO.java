package com.biz.credit.dao;

import com.biz.credit.domain.DNodeParam;
import com.biz.credit.vo.ApiVO;
import com.biz.credit.vo.IndustryInfoVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditModelDAO {

    List<IndustryInfoVO> queryCreditModelList(@Param("apiCode") String apiCode);
    List<ApiVO> queryIndustryInfoApiList(@Param("industryId") Integer industryId);
    List<DNodeParam> queryIndustryInfoApi(@Param("industryId") Integer industryId);
    List<String> queryApiList(@Param("apiCode") String apiCode);

}
