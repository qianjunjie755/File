package com.biz.chart.service;

import java.util.List;

public interface IScheduleService {
    List<Long> getCleanCharts(long startId);
    void deleteNeo4jChart(Long chartId);
    Integer updateChartClean(Long chartId);
}
