package com.biz.warning.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.StrategyVO;
import com.biz.warning.vo.TaskVO;

import java.util.List;
import java.util.Map;

public interface ITaskService {
    long addTask(TaskVO task) throws Exception;
    long updateTask(TaskVO taskVO) throws  Exception;
    long updateTaskStatus(TaskVO taskVO)throws Exception;
    List<TaskVO> findTaskVOListByPage(TaskVO taskVO) throws Exception;
    List<RuleSetVO> findRuleSetListByTask(TaskVO taskVO) throws Exception;
    TaskVO findTaskVOByTaskVO(TaskVO taskVO) throws Exception;
    TaskVO findTaskByTaskIdAndUserId(Integer taskId, Integer userId) throws Exception;
    long copyStrategyTemplate(StrategyVO source, StrategyVO target) throws Exception;
    long updateSrcIdForCopyStrategyTemplate(StrategyVO target) throws Exception;
    void changeEntityTemplateName(TaskVO task) throws Exception;
    String getHeadListByTaskId(Integer taskId)throws  Exception;
    JSONObject getTasks(String apiCode);
    JSONObject startMonitor(Map<String, Object> request) throws Exception;
}
