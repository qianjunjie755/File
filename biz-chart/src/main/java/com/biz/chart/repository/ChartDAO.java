package com.biz.chart.repository;

import com.biz.chart.entity.RelationType;
import com.biz.chart.entity.vo.ChartVO;
import com.biz.chart.entity.vo.InputVO;
import com.biz.chart.entity.vo.NodeTypeVO;
import com.biz.chart.entity.vo.RelationTypeVO;
import com.biz.relation.entity.Input;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChartDAO {
    List<NodeTypeVO> queryNodeType();
    List<RelationTypeVO> queryRelationType(@Param("type") Integer type);
    String queryChartRelations(@Param("chartId") Long chartId);
    List<RelationType> queryRelationTypes(@Param("shipIds") String shipIds);
    Long queryChartInput(@Param("input") InputVO input);
    Integer insertChart(@Param("chart") ChartVO chartVO);
    Integer insertChartInput(@Param("chartId") Long chartId, @Param("inputs") List<Input> inputs);
    Integer updateChart(@Param("chartId") Long chartId, @Param("status") Integer result);
}
