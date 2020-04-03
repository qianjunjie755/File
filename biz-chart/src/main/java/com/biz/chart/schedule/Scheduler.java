package com.biz.chart.schedule;

import com.biz.chart.service.IScheduleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class Scheduler {

    @Value("${biz.relation-chart.server}")
    private Integer serverNo;

    @Autowired
    private IScheduleService service;

    @Scheduled(cron = "0 10 0 * * *")
    public void cleanNeo4jChart() {
        //运行在0或者1的服务中
        if (!Objects.equals(serverNo, 0) && !Objects.equals(serverNo, 1)) {
            return;
        }
        log.info("Neo4j历史图谱数据清理开始!!");
        long startId = 0;
        //每次只取1000条
        while (true) {
            List<Long> chartIds = service.getCleanCharts(startId);
            if (CollectionUtils.isEmpty(chartIds)) {
                break;
            }
            for (Long chartId : chartIds) {
                startId = chartId;
                try {
                    //清理Neo4j数据库数据
                    service.deleteNeo4jChart(chartId);
                    //更新表状态
                    service.updateChartClean(chartId);
                    log.info("图谱[{}]Neo4j历史数据已清理!", chartId);
                } catch (Exception e) {
                    log.error("图谱[" + chartId + "]Neo4j历史数据清理异常: " + e.getMessage(), e);
                }
            }
            startId += 1;
        }
        log.info("Neo4j历史图谱数据清理结束!!");
    }
}
