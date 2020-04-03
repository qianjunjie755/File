package com.biz.credit.service;

import com.biz.credit.vo.*;

import java.util.List;

public interface IPortraitTaskService {
    /**
     * 保存画像任务
     * @param task
     * @param labels
     * @param userId
     * @return
     */
    Integer saveTask(PortraitTaskReqVO task, List<PortraitLabelRespVO> labels, Integer userId);

    /**
     * 查询任务列表
     * @param param
     * @return
     */
    List<PortraitTaskRespVO> queryTasks(PortraitTaskQueryVO param);

    /**
     * 查询任务标签列表
     * @param taskId
     * @return
     */
    List<PortraitTaskLabelRespVO> queryTaskLabels(Integer taskId);
}
