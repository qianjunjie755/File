package com.biz.credit.dao;

import com.biz.credit.domain.ModuleTypeApi;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleTypeApiDAO {
    int addModuleTypeApi(@Param("moduleTypeApi") ModuleTypeApi moduleTypeApi) ;
    List<ModuleTypeApi> findModuleTypeApiListByApiCode(@Param("apiCode") String apiCode) throws Exception;
    ModuleTypeApi findCountByModuleTypeIdAndApiCode(@Param("moduleTypeApi") ModuleTypeApi moduleTypeApi) throws Exception;
    int updateStatusByModuleTypeApi(@Param("moduleTypeApi") ModuleTypeApi moduleTypeApi);
    int updateValidEndByModuleTypeApi(@Param("moduleTypeApi") ModuleTypeApi moduleTypeApi) throws Exception;
   // ModuleTypeApi findModuleTypeIdByApiCodeReportType(@Param("moduleTypeApi") ModuleTypeApi moduleTypeApi) throws Exception;
}
