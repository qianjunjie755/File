package com.biz.credit.dao;

import com.biz.credit.domain.ModuleType;
import com.biz.credit.vo.ModuleTypeVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleTypeDAO {
    int addModuleTypeByTemplate(@Param("moduleType") ModuleType moduleType)throws Exception;
    int addModuleType(@Param("moduleType") ModuleTypeVO moduleType);
    int addModuleTypeByStrategyId(@Param("moduleType") ModuleTypeVO moduleType) throws Exception;
    int updateModuleType(@Param("moduleType") ModuleType moduleType)throws Exception;
    int updateColumnHead(@Param("moduleTypeVO") ModuleTypeVO moduleTypeVO);
    List<ModuleTypeVO> findModuleTypeList(@Param("moduleType") ModuleTypeVO moduleType);
    ModuleTypeVO findModuleTypeById(@Param("moduleType") ModuleTypeVO moduleType) ;
    ModuleTypeVO findModuleTypeByFlowId(@Param("flowId") Long flowId) ;
    ModuleTypeVO findModuleTypeTemplateByReportType(@Param("moduleType") ModuleTypeVO moduleType) throws Exception;
    ModuleTypeVO findModuleTypeTemplateByStrategyId(@Param("strategyId") Integer strategyId);
    ModuleTypeVO findModuleTypeTemplateByFlowId(@Param("flowId") Long flowId);
    ModuleTypeVO findModuleTypeInfoById(@Param("moduleType") ModuleTypeVO moduleType) ;
}
