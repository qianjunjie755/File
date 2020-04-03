package com.biz.credit.dao;

import com.biz.credit.domain.CrmApi;
import com.biz.credit.vo.CrmApiVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrmApiDAO {
    CrmApi findCrmApi(@Param("prodCode") String prodCode, @Param("version") Double version);
    List<CrmApiVO> findCrmApiVOList(@Param("apiCode") String apiCode, @Param("reportType") Integer reportType, @Param("industryId") Integer industryId);
    Integer findUserIdByApiCode(@Param("apiCode") String apiCode);
    Integer findCrmApiByCrmApi(@Param("crmApi") CrmApi crmApi);
    int addCrmApiClient(@Param("crmApi") CrmApi crmApi);


}
