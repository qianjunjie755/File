package com.biz.credit.dao;

import com.biz.credit.domain.ModuleTypeTemplate;
import com.biz.credit.vo.ModuleTypeTemplateVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleTypeTemplateDAO {
    int addModuleTypeTemplate(@Param("moduleTypeTemplate") ModuleTypeTemplate moduleTypeTemplate);
    int updateModuleTypeTemplate(@Param("moduleTypeTemplate") ModuleTypeTemplate moduleTypeTemplate);
    List<ModuleTypeTemplate> findModuleTypeTemplateList(@Param("moduleTypeTemplate") ModuleTypeTemplate moduleTypeTemplate);
    List<ModuleTypeTemplate> findModuleTypeTemplateListForTask();
    List<ModuleTypeTemplate> findParentCodeList(@Param("moduleTypeTemplate") ModuleTypeTemplateVO moduleTypeTemplate);

}
