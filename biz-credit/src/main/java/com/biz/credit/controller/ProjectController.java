package com.biz.credit.controller;

import com.biz.credit.domain.Project;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IProjectService;
import com.biz.credit.vo.ProjectVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private IProjectService projectService;

    @RequestMapping("/list")
    public RespEntity getProjectList(ProjectVO projectVO, HttpSession session){
        String userId = (String)session.getAttribute("userId");
        String apiCode = (String)session.getAttribute("apiCode");
        projectVO.setUserId(Long.parseLong(userId));
        projectVO.setApiCode(apiCode);
        List<ProjectVO> projectList = projectService.getProjectList(projectVO);
        return RespEntity.success().setData(projectList);
    }

    @GetMapping("/listNew")
    public RespEntity getProjectList(HttpSession session){
        String apiCode = (String)session.getAttribute("apiCode");
        return RespEntity.success().setData(projectService.getProjectModels(apiCode));
    }

    @PostMapping
    public RespEntity saveProject(@RequestBody ProjectVO projectVO,
                                  @RequestParam(value = "groupId")Integer groupId,
                                          HttpSession session){
        if(groupId ==null){
            return RespEntity.error().setMsg("平台类型id不能为空");
        }


        String userId = (String)session.getAttribute("userId");
        String apiCode = (String)session.getAttribute("apiCode");
        projectVO.setApiCode(apiCode);
        projectVO.setUserId(Long.parseLong(userId));
        projectVO.setPlatformId(groupId);
        return projectService.saveProject(projectVO);
    }

    @DeleteMapping("/{projectId}")
    public RespEntity deleteProject(@PathVariable("projectId") Long projectId){
        return projectService.deleteProjectById(projectId);
    }

    @GetMapping("/{projectId}")
    public RespEntity getProjectById(@PathVariable("projectId") Long projectId){
        Project project = projectService.getProjectById(projectId);
        return RespEntity.success().setData(project);
    }

}
