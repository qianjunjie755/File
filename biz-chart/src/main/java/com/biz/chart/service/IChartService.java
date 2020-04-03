package com.biz.chart.service;

import com.alibaba.fastjson.JSONObject;
import com.biz.chart.entity.Node;
import com.biz.chart.entity.Relation;
import com.biz.chart.entity.RelationType;
import com.biz.relation.entity.ChartInput;
import com.biz.relation.entity.RelationChart;

import java.util.List;

public interface IChartService {
    JSONObject getRelationType(Integer type);
    List<RelationType> getChartRelationTypes(Long chartId);
    List<Integer> getRelationIds(Integer type);
    RelationChart getRelationChart(Long chartId);
    JSONObject getChartNodes(Long chartId, String nodeName, Integer pageNo, Integer pageSize);
    List<Node> getChartNodes(Long chartId, String nodeName);
    JSONObject getChartRelations(Long chartId, String relationName, String nodeName, Integer pageNo, Integer pageSize);
    List<Relation> getChartRelations(Long chartId, String relationName, String nodeName);
    Long getChartInputId(ChartInput chartInput);
    void saveChartInput(Integer userId, ChartInput chartInput);
    RelationChart chartAnalysis(ChartInput chartInput, String type);
    RelationChart getExtendRelation(Long chartId, Integer nodeId);
    RelationChart getShortestPaths(Long chartId, Integer startId, Integer endId);
}
