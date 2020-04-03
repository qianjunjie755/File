package com.biz.credit.controller;

import com.biz.credit.controller.validator.PortraitTaskValidator;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IPortraitConfigService;
import com.biz.credit.service.IPortraitTaskService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Api(tags = "企业画像任务API")
@RestController
@RequestMapping("/portraitTask")
public class PortraitTaskController {

    @Autowired
    private IPortraitConfigService portraitConfigService;

    @Autowired
    private IPortraitTaskService portraitTaskService;

    @ApiOperation(value = "新增任务")
    @PostMapping("/addTask")
    public RespEntity addTask(@RequestBody PortraitTaskReqVO task,
                              HttpSession session){
        String userId = (String) session.getAttribute(Constants.USER_ID);
        RespEntity respEntity = PortraitTaskValidator.addTaskValidate(task);
        if (!respEntity.isSuccess()){
            return respEntity;
        }
        //校验moduleId
        SystemModuleRespVO module = portraitConfigService.querySingleModule(task.getModuleId());
        if (module == null){
            return RespEntity.error().setMsg("系统模块不存在或已停用");
        }
        //校验标签ID
        List<PortraitLabelRespVO> labels = portraitConfigService.queryLabelsByBatch(task.getLabelIds());
        if (CollectionUtils.isEmpty(labels) || labels.size() != task.getLabelIds().size()){
            return RespEntity.error().setMsg("标签校验失败，存在无效标签");
        }
        Integer taskId = portraitTaskService.saveTask(task, labels, Integer.valueOf(userId));
        if (taskId == null){
            return RespEntity.error();
        }
        return RespEntity.success().setData(taskId);
    }

    @ApiOperation(value = "查看任务列表")
    @GetMapping("/getTasks")
    public RespEntity<List<PortraitTaskRespVO>> getTasks(PortraitTaskQueryVO param){
        List<PortraitTaskRespVO> list = portraitTaskService.queryTasks(param);
        return RespEntity.success().setData(list);
    }

    @ApiOperation(value = "根据任务Id查看任务标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "taskId", value = "任务ID-Integer", required = true)
    })
    @GetMapping("/getTaskLabels")
    public RespEntity<List<PortraitTaskLabelRespVO>> getTaskLabels(@RequestParam(name = "taskId") Integer taskId){
        List<PortraitTaskLabelRespVO> list = portraitTaskService.queryTaskLabels(taskId);
        return RespEntity.success().setData(list);
    }
}
