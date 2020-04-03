package com.biz.warning.dao;

import com.biz.warning.domain.TaskLog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskLogDAO {
    int addTaskLogList(List<TaskLog> taskLogList)throws Exception;
}
