package com.biz.credit.dao;

import com.biz.credit.domain.Project;
import com.biz.credit.vo.ProjectVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectDAO {

    List<ProjectVO> queryList(@Param("project") ProjectVO projectVO);

    void insert(@Param("project") Project project);

    void updateStatusByProjectId(@Param("projectId") Long projectId, @Param("status") Integer status);

    ProjectVO queryByProjectId(@Param("projectId") Long projectId);

    int queryCountByName(@Param("projectVO") ProjectVO projectVO);

    int update(@Param("project") ProjectVO projectVO);

    Project queryFirstProject(@Param("apiCode") String apiCode);
}
