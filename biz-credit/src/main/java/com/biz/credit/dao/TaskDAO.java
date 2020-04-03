package com.biz.credit.dao;

import com.biz.credit.domain.Task;
import com.biz.credit.vo.BiInputDataVO;
import com.biz.credit.vo.TaskVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import javax.naming.ldap.HasControls;
import java.util.HashMap;
import java.util.List;

@Repository
public interface TaskDAO {
    int addTask(@Param("task") Task task);
    int updateTask(@Param("task") Task task);
    int updateTaskStatus(@Param("taskId") Integer taskId);
    List<Task> findTaskList(@Param("task") Task task);
    List<Task> queryTaskByName(@Param("task") Task task);
    Task queryTaskById(@Param("task") Task task);
    TaskVO queryTaskVOById(@Param("task") TaskVO task);

    List<Task> queryTaskListByNameAndDate(@Param("biInputDataVO")BiInputDataVO biInputDataVO);

}
