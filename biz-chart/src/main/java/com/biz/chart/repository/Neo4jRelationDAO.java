package com.biz.chart.repository;

import com.biz.relation.neo4j.Neo4jRelation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Neo4jRelationDAO extends Neo4jRepository<Neo4jRelation, Long> {

    String RELATION_CQL = "MATCH p=(n:Neo4jNode)-[r:Neo4jRelation]->(m:Neo4jNode) WHERE r.chartId={chartId}";
    String SHORTEST_CQL = "MATCH p=allshortestpaths((n:Neo4jNode)<-[*..9]->(m:Neo4jNode)) WHERE n.chartId={chartId} and m.chartId={chartId}";

    @Query(value = RELATION_CQL + " RETURN p ORDER BY r.id")
    List<Neo4jRelation> queryRelations(@Param("chartId") Long chartId);

    @Query(value = RELATION_CQL + " RETURN p ORDER BY r.id",
      countQuery = RELATION_CQL + " RETURN COUNT(p)")
    Page<Neo4jRelation> queryRelations(@Param("chartId") Long chartId, Pageable pageable);

    @Query(value = RELATION_CQL + " and r.name={name} and (n.name=~{nodeName} or m.name=~{nodeName}) RETURN p ORDER BY r.id")
    List<Neo4jRelation> queryRelationsByName(@Param("chartId") Long chartId,
                                             @Param("name") String name,
                                             @Param("nodeName") String nodeName);

    @Query(value = RELATION_CQL + " and r.name={name} and (n.name=~{nodeName} or m.name=~{nodeName}) RETURN p ORDER BY r.id",
      countQuery = RELATION_CQL + " and r.name={name} and (n.name=~{nodeName} or m.name=~{nodeName}) RETURN COUNT(p)")
    Page<Neo4jRelation> queryRelationsByName(@Param("chartId") Long chartId,
                                             @Param("name") String name,
                                             @Param("nodeName") String nodeName,
                                             Pageable pageable);

    @Query(value = RELATION_CQL + " and r.name={name} RETURN p ORDER BY r.id")
    List<Neo4jRelation> queryRelationsByRelationName(@Param("chartId") Long chartId,
                                                     @Param("name") String name);

    @Query(value = RELATION_CQL + " and r.name={name} RETURN p ORDER BY r.id",
      countQuery = RELATION_CQL + " and r.name={name} RETURN COUNT(p)")
    Page<Neo4jRelation> queryRelationsByRelationName(@Param("chartId") Long chartId,
                                                     @Param("name") String name,
                                                     Pageable pageable);

    @Query(value = RELATION_CQL + " and (n.name=~{nodeName} or m.name=~{nodeName}) RETURN p ORDER BY r.id")
    List<Neo4jRelation> queryRelationsByNodeName(@Param("chartId") Long chartId,
                                                 @Param("nodeName") String nodeName);

    @Query(value = RELATION_CQL + " and (n.name=~{nodeName} or m.name=~{nodeName}) RETURN p ORDER BY r.id",
      countQuery = RELATION_CQL + " and (n.name=~{nodeName} or m.name=~{nodeName}) RETURN COUNT(p)")
    Page<Neo4jRelation> queryRelationsByNodeName(@Param("chartId") Long chartId,
                                                 @Param("nodeName") String nodeName,
                                                 Pageable pageable);

    @Query(value = RELATION_CQL + " and r.name in ['实际控制人','受益所有人'] and m.id={nodeId} RETURN p")
    List<Neo4jRelation> queryExtendRelations(@Param("chartId") Long chartId,
                                             @Param("nodeId") Integer nodeId);

    @Query(value = SHORTEST_CQL + " and n.id={startId} and m.id={endId} RETURN p")
    List<Neo4jRelation> queryShortestRelations(@Param("chartId") Long chartId,
                                               @Param("startId") Integer startId,
                                               @Param("endId") Integer endId);
}
