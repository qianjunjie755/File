package com.biz.warning.scheduler;

import com.biz.strategy.BizTask;
import com.biz.strategy.entity.Task;
import com.biz.strategy.entity.TaskInput;
import com.biz.warning.dao.EntityDAO;
import com.biz.warning.dao.TaskDAO;
import com.biz.warning.vo.TaskVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;

/**
 * Description: 任务调度
 */
@Slf4j
@Component
public class TaskSchedule {

    @Autowired
    private BizTask bizTask;

    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private EntityDAO entityDAO;

    @Autowired
    private RedisLockRegistry redisLockRegistry;

    @Scheduled(cron = "${biz.warning.task-crontab}")
    public void initTaskInput() throws Exception {
        Lock lock = redisLockRegistry.obtain(":warning:task");
        if (!lock.tryLock()) {
            return;
        }
        try {
            long startId = 0;
            DateTime dateTime = DateTime.now();
            log.info("预警任务开始发布!!");
            while (true) {
                List<TaskVO> taskList = taskDAO.findListByMinTaskId(Long.valueOf(startId));
                if (CollectionUtils.isEmpty(taskList)) {
                    break;
                }
                for (TaskVO taskVO : taskList) {
                    Task task = new Task();
                    task.setTaskId(taskVO.getTaskId().intValue());
                    task.setTaskName(taskVO.getTaskName());
                    task.setStrategyId(taskVO.getStrategyId().intValue());
                    task.setApiCode(taskVO.getApiCode());
                    if (taskVO.getUserId() != null) {
                        task.setUserId(taskVO.getUserId().intValue());
                    }
                    String interval = taskVO.getExecIntervalUnit();
                    int num = taskVO.getExecIntervalNum();
                    DateTime lastExecTime = dateTime.minusDays(NumberUtils.INTEGER_ONE.intValue());
                    if (StringUtils.equals("d", interval) && NumberUtils.INTEGER_ONE < num) {
                        lastExecTime = lastExecTime.minusDays(num - 1);
                    } else if (StringUtils.equals("m", interval)) {
                        lastExecTime = lastExecTime.plusDays(NumberUtils.INTEGER_ONE.intValue());
                        lastExecTime = lastExecTime.minusMonths(num);
                    }
                    int lastExecTimeInt = Integer.parseInt(lastExecTime.toString("yyyyMMdd"));
                    List<TaskInput> taskInputList = entityDAO.findTaskInputListByTaskId(taskVO.getTaskId().intValue());
                    List<TaskInput> taskInputListFinal = new ArrayList<>();
                    taskInputList.forEach(taskInput -> {
                        if (null != taskInput.getExecDate()) {
                            int taskInputExecDate = Integer.parseInt(taskInput.getExecDate().toString().replaceAll("-", StringUtils.EMPTY));
                            if (taskInputExecDate <= lastExecTimeInt) {
                                taskInputListFinal.add(taskInput);
                            }
                        } else {
                            taskInputListFinal.add(taskInput);
                        }
                    });

                    if (!CollectionUtils.isEmpty(taskInputListFinal)) {
                        try {
                            task.setTaskInputs(taskInputListFinal);
                            bizTask.issue(task);
                        } catch (Exception e) {
                            log.error("任务[" + task + "]发布异常: " + e.getMessage(), e);
                        }
                    }
                }
                startId = taskList.get(taskList.size() - 1).getTaskId();
            }
            log.info("预警任务发布完成!!");
        } finally {
            lock.unlock();
        }
    }

}
