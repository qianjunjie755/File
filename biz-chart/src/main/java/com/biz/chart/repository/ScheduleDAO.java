package com.biz.chart.repository;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleDAO {
    List<Long> queryCleanCharts(@Param("startId") long startId);
    Integer updateChartClean(@Param("chartId") Long chartId);
}
