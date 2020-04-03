package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.ProjectDAO;
import com.biz.credit.domain.*;
import com.biz.credit.service.*;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.IndustryInfoViewVO;
import com.biz.credit.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Service
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private ProjectDAO projectDAO;

    @Autowired
    private IScoreCardService scoreCardService;

    @Autowired
    private ITreeService treeService;

    @Autowired
    private ITableService tableService;

    @Autowired
    private IIndustryInfoService industryInfoService;

    @Autowired
    private RedisLockRegistry lockRegistry;

    @Override
    public List<ProjectVO> getProjectList(ProjectVO projectVO){
        List<ProjectVO> projectVOList = projectDAO.queryList(projectVO);
        for (ProjectVO project : projectVOList) {
            List<ScoreCard> scoreCardList = scoreCardService.getAllMaxVersionList(project.getId(), projectVO.getApiCode());
            project.setScoreCardList(scoreCardList);
            project.setCanDelete(!isRelated(project.getId()));

            List<DTree> treeList = treeService.getTreeList(project.getId(),projectVO.getApiCode());
            project.setTreeList(treeList);
            List<DTable> tableList = tableService.getTableList(project.getId(),projectVO.getApiCode());
            project.setTableList(tableList);
            List<IndustryInfoViewVO> industryList = industryInfoService.findAllIndustryInfoVOListView();
            project.setIndustryList(industryList);
        }
        return projectVOList;
    }

    @Override
    public JSONObject getProjectModels(String apiCode) {
        Long projectId = null;
        List<ScoreCard> scoreCardList = scoreCardService.getAllMaxVersionList(projectId, apiCode);
        if (scoreCardList == null) {
            scoreCardList = new ArrayList<>();
        }
        List<DTree> treeList = treeService.getTreeList(projectId, apiCode);
        if (treeList == null) {
            treeList = new ArrayList<>();
        }
        List<DTable> tableList = tableService.getTableList(projectId, apiCode);
        if (tableList == null) {
            tableList = new ArrayList<>();
        }

        JSONObject object = new JSONObject();
        object.put("scoreCard", scoreCardList);
        object.put("dTree", treeList);
        object.put("dTable", tableList);
        return object;
    }

    @Override
    @Transactional
    public RespEntity saveProject(ProjectVO projectVO) {
        boolean isNew = false;
        if(projectVO.getId() == null){
            isNew = true;
        }
        RespEntity checkResp = checkParams(projectVO,isNew);
        if(!checkResp.isSuccess()){
            return checkResp;
        }
        if(isNew){
            projectVO.setStatus(Constants.COMMON_STATUS_VALID);
            projectDAO.insert(projectVO);
        }else{
            projectDAO.update(projectVO);
        }
        return RespEntity.success().setData(projectVO.getId());
    }

    @Override
    @Transactional
    public RespEntity deleteProjectById(Long projectId) {
        boolean related = isRelated(projectId);
        if(related){
            return RespEntity.error().setMsg("该项目有关联的子类目，无法删除");
        }
        projectDAO.updateStatusByProjectId(projectId,Constants.COMMON_STATUS_INVALID);
        return RespEntity.success();
    }

    @Override
    public ProjectVO getProjectById(Long projectId) {
        ProjectVO projectVO = projectDAO.queryByProjectId(projectId);
        if(projectVO != null){
            projectVO.setCanDelete(!isRelated(projectVO.getId()));
        }
        return projectVO;
    }

    @Override
    public Project getFirstProject(String apiCode, Long userId) {
        String lockKey = ":project:" + apiCode;
        Lock lock = lockRegistry.obtain(lockKey);
        lock.lock();
        try {
            Project project = projectDAO.queryFirstProject(apiCode);
            if (project == null) {
                project = new Project();
                project.setApiCode(apiCode);
                project.setName("[" + apiCode + "]通用项目");
                project.setStatus(1);
                project.setUserId(userId);
                projectDAO.insert(project);
            }
            return project;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 判断是否被引用
     * @param projectId
     * @return
     */
    public boolean isRelated(Long projectId){
        List<ScoreCard> scoreCardList = scoreCardService.getListByProjectId(projectId);
        if(!CollectionUtils.isEmpty(scoreCardList)){
            return true;
        }
        List<DTree> treeList = treeService.getListByProjectId(projectId);
        if(!CollectionUtils.isEmpty(treeList)){
            return true;
        }
        List<DTable> tableList = tableService.getListByProjectId(projectId);
        if(!CollectionUtils.isEmpty(tableList)){
            return true;
        }
        return false;
    }

    private RespEntity checkParams(ProjectVO projectVO,boolean isNew) {
        if(StringUtils.isEmpty(projectVO.getName())){
            return RespEntity.error().setMsg("项目名称不能为空");
        }
        int count = projectDAO.queryCountByName(projectVO);
        if(count > 0){
            return RespEntity.error().setMsg("项目名称已存在，请更换");
        }
        return RespEntity.success();
    }
}
