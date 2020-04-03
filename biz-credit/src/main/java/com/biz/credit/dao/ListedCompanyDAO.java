package com.biz.credit.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ListedCompanyDAO {
    String queryStockCode(@Param("companyName") String companyName);
}
