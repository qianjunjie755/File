package com.biz.credit.dao;

import com.biz.credit.vo.ProjectApiVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectApiDAO {
    List<ProjectApiVO> findList(@Param("projectApiVO") ProjectApiVO projectApiVO);
}
