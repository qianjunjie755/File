package com.biz.credit.dao;

import com.biz.credit.domain.IndustryInfoApi;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IndustryInfoApiDAO {
    int addIndustryInfoApi(@Param("industryInfoApi") IndustryInfoApi industryInfoApi)throws Exception;
    int findCountByIndustryInfoApi(@Param("industryInfoApi") IndustryInfoApi industryInfoApi)throws Exception;
}
