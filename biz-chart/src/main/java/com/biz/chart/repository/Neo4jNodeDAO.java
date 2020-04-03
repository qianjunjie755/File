package com.biz.chart.repository;

import com.biz.relation.neo4j.Neo4jNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Neo4jNodeDAO extends Neo4jRepository<Neo4jNode, Long> {

    String NODE_CQL = "MATCH (n:Neo4jNode) where n.chartId={chartId}";

    @Query(value = NODE_CQL + " RETURN n ORDER BY n.id")
    List<Neo4jNode> queryNodes(@Param("chartId") Long chartId);

    @Query(value = NODE_CQL + " RETURN n ORDER BY n.id",
      countQuery = NODE_CQL + " RETURN COUNT(n)")
    Page<Neo4jNode> queryNodes(@Param("chartId") Long chartId, Pageable pageable);

    @Query(value = NODE_CQL + " and n.name=~{nodeName} RETURN n ORDER BY n.id")
    List<Neo4jNode> queryNodesByName(@Param("chartId") Long chartId, @Param("nodeName") String nodeName);

    @Query(value = NODE_CQL + " and n.name=~{nodeName} RETURN n ORDER BY n.id",
      countQuery = NODE_CQL + " and n.name=~{nodeName} RETURN COUNT(n)")
    Page<Neo4jNode> queryNodesByName(@Param("chartId") Long chartId,
                                     @Param("nodeName") String nodeName,
                                     Pageable pageable);

    @Query(value = NODE_CQL + " and n.id={nodeId} RETURN p")
    Neo4jNode queryNodeById(@Param("chartId") Long chartId, @Param("nodeId") Integer nodeId);
}
