package com.biz.credit.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.Project;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.ProjectVO;

import java.util.List;

public interface IProjectService {
    List<ProjectVO> getProjectList(ProjectVO projectVO);

    JSONObject getProjectModels(String apiCode);

    RespEntity saveProject(ProjectVO projectVO);

    RespEntity deleteProjectById(Long projectId);

    Project getProjectById(Long projectId);

    Project getFirstProject(String apiCode, Long userId);
}
