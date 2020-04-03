package com.biz.chart.service.impl;

import com.biz.chart.repository.ScheduleDAO;
import com.biz.chart.service.IScheduleService;
import com.biz.relation.repository.NodeNeo4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ScheduleServiceImpl implements IScheduleService {

    @Autowired
    private ScheduleDAO dao;

    @Autowired
    private NodeNeo4j nodeNeo4j;

    /**
     * 获取清理的图谱ID
     *
     * @param startId
     * @return
     */
    @Override
    public List<Long> getCleanCharts(long startId) {
        return dao.queryCleanCharts(startId);
    }

    /**
     * 删除Neo4j历史图谱数据
     *
     * @param chartId
     */
    @Override
    public void deleteNeo4jChart(Long chartId) {
        nodeNeo4j.deleteByChartId(chartId);
    }

    /**
     * 更新图谱清理状态
     *
     * @param chartId
     * @return
     */
    @Override
    public Integer updateChartClean(Long chartId) {
        return dao.updateChartClean(chartId);
    }
}
