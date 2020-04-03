package com.biz.warning.dao;

import com.biz.warning.vo.ParamVO;
import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.TaskVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskDAO {
        /**
         * 添加预警任务
         * @param task 预警任务
         * @return 影响的记录行数
         * @throws Exception
         */
        long addTask(@Param("task") TaskVO task) throws Exception;

        /**
         * 更新任务信息(任务名称，简介)
         * @param task
         * @return
         * @throws Exception
         */
        long updateTask(@Param("task") TaskVO task) throws Exception;
        long updateTaskNameBCDescriptionStrategyId(@Param("task") TaskVO task) throws Exception;
        /**
         *  更新任务状态
         * @param task  任务
         * @return 影响的记录行数
         * @throws Exception
         */
        long updateTaskStatus(@Param("task") TaskVO task) throws Exception;

        /**
         *  根据任务编号（完全匹配），任务名称（模糊匹配），时间范围以及用户id查找任务集合
         * @param taskVO 查询条件
         * @return 人物集合
         * @throws Exception
         */
        List<TaskVO> findListByTaskVO(@Param("taskVO") TaskVO taskVO) throws Exception;
        TaskVO findByTask(@Param("task") TaskVO task) throws Exception;

        List<TaskVO> findListByMinTaskId(@Param("minTaskId") Long minTaskId) throws Exception;
        int updateTaskForTemplateNameBuild(@Param("task") TaskVO task) throws Exception;
        List<RuleSetVO> findRuleSetListByTask(@Param("task") TaskVO task) throws  Exception;
        String getHeadListByTaskId(@Param("taskId") Integer taskId)throws  Exception;

        List<ParamVO> queryTasksByApiCode(@Param("apiCode") String apiCode);
        Integer queryEntityCount(@Param("apiCode") String apiCode, @Param("entityName") String entityName);
}
