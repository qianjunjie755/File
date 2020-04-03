package com.biz.credit.dao;

import com.biz.decision.entity.EntityBasic;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportPluginDAO {
    void updateDecideTask(@Param("taskId") long taskId, @Param("status") Integer status);
    int updateDecideEntityBasicInfo(@Param("entityBasic") EntityBasic entityBasic);
    int insertDecideEntityBasicInfo(@Param("entityBasic") EntityBasic entityBasic);
    void updateInput(@Param("inputId") long inputId, @Param("filePath") String filePath, @Param("fileName") String fileName, @Param("status") int status);
    void update3rdInput(@Param("inputId") long inputId, @Param("filePath") String filePath, @Param("fileName") String fileName, @Param("status") int status);
}
