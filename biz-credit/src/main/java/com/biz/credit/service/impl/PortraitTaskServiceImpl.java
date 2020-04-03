package com.biz.credit.service.impl;

import com.biz.credit.dao.PortraitTaskDAO;
import com.biz.credit.domain.PortraitTask;
import com.biz.credit.domain.PortraitTaskLabel;
import com.biz.credit.service.IPortraitTaskService;
import com.biz.credit.vo.*;
import com.biz.utils.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PortraitTaskServiceImpl implements IPortraitTaskService {

    @Autowired
    private PortraitTaskDAO portraitTaskDAO;

    @Override
    @Transactional
    public Integer saveTask(PortraitTaskReqVO task, List<PortraitLabelRespVO> labels, Integer userId) {
        PortraitTask portraitTask = new PortraitTask();
        BeanUtils.copyProperties(task, portraitTask);
        String now = DateUtil.parseDateToStr(new Date(), DateUtil.DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS);
        portraitTask.setCreateTime(now);
        portraitTask.setCreateUser(userId);
        portraitTask.setUpdateTime(now);
        portraitTask.setUpdateUser(userId);
        portraitTaskDAO.insertTask(portraitTask);

        //添加画像任务标签
        List<PortraitTaskLabel> taskLabels = new ArrayList<>();
        for (PortraitLabelRespVO label : labels){
            PortraitTaskLabel taskLabel = new PortraitTaskLabel();
            taskLabel.setTaskId(portraitTask.getTaskId());
            taskLabel.setLabelId(label.getLabelId());
            taskLabel.setStatus(label.getStatus());
            taskLabel.setCreateTime(now);
            taskLabel.setCreateUser(userId);
            taskLabel.setUpdateTime(now);
            taskLabel.setUpdateUser(userId);
            taskLabels.add(taskLabel);
        }
        portraitTaskDAO.insertTaskLabelBatch(taskLabels);
        return portraitTask.getTaskId();
    }

    @Override
    public List<PortraitTaskRespVO> queryTasks(PortraitTaskQueryVO param) {
        return portraitTaskDAO.findTasks(param);
    }

    @Override
    public List<PortraitTaskLabelRespVO> queryTaskLabels(Integer taskId) {
        return portraitTaskDAO.findTaskLabels(taskId);
    }
}
