package com.biz.credit.dao;

import com.biz.credit.domain.BIDate;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BIDateDAO {
    int addBIDate(@Param("biDate") BIDate biDate) throws Exception;
}
