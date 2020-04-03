package com.biz.credit.plugins;

import com.biz.credit.dao.ReportPluginDAO;
import com.biz.decision.entity.Task;
import com.biz.decision.plugins.IUpdateTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateDecideTaskServiceImpl implements IUpdateTask {

    @Autowired
    private ReportPluginDAO reportPluginDAO;

    @Override
    public void execute(Task task) {
        reportPluginDAO.updateDecideTask(task.getTaskId(),2);
    }
}
