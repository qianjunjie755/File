package com.biz.credit.dao;

import com.biz.credit.domain.PortraitTask;
import com.biz.credit.domain.PortraitTaskLabel;
import com.biz.credit.vo.PortraitTaskLabelRespVO;
import com.biz.credit.vo.PortraitTaskRespVO;
import com.biz.credit.vo.PortraitTaskQueryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortraitTaskDAO {
    void insertTask(@Param("task") PortraitTask portraitTask);

    void insertTaskLabelBatch(@Param("list") List<PortraitTaskLabel> taskLabels);

    void updateTaskLabelStatus(@Param("labelId") Integer labelId,
                               @Param("status") Integer status,
                               @Param("userId") Integer userId);

    List<PortraitTaskRespVO> findTasks(@Param("param") PortraitTaskQueryVO param);

    List<PortraitTaskLabelRespVO> findTaskLabels(@Param("taskId") Integer taskId);
}
