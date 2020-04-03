package com.biz.chart.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.chart.entity.*;
import com.biz.chart.entity.Node;
import com.biz.chart.entity.Relation;
import com.biz.chart.entity.vo.ChartVO;
import com.biz.chart.entity.vo.InputVO;
import com.biz.chart.entity.vo.NodeTypeVO;
import com.biz.chart.entity.vo.RelationTypeVO;
import com.biz.chart.repository.ChartDAO;
import com.biz.chart.repository.Neo4jNodeDAO;
import com.biz.chart.repository.Neo4jRelationDAO;
import com.biz.chart.service.IChartService;
import com.biz.chart.utils.Constants;
import com.biz.relation.BizRelationChart;
import com.biz.relation.entity.*;
import com.biz.relation.enums.EChart;
import com.biz.relation.neo4j.Neo4jNode;
import com.biz.relation.neo4j.Neo4jRelation;
import com.biz.relation.utils.RelationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChartServiceImpl implements InitializingBean, IChartService {

    @Autowired
    private ChartDAO chartDAO;

    @Autowired
    private Neo4jNodeDAO nodeDAO;

    @Autowired
    private Neo4jRelationDAO relationDAO;

    @Autowired
    private BizRelationChart bizRelationChart;

    private Map<Integer, String> nodeTypes;

    @Override
    public void afterPropertiesSet() {
        nodeTypes = new HashMap<>();
        List<NodeTypeVO> nodeTypeVOs = chartDAO.queryNodeType();
        for (NodeTypeVO vo : nodeTypeVOs) {
            nodeTypes.put(vo.getNodeType(), vo.getTypeName());
        }
        log.info("关系节点类型初始化完成!!");
    }

    /**
     * 获取关系类型
     *
     * @param type
     * @return
     */
    @Override
    public JSONObject getRelationType(Integer type) {
        JSONObject data = new JSONObject();
        List<RelationTypeVO> typeList = chartDAO.queryRelationType(type);
        if (typeList != null) {
            String code = null;
            Integer groupId = null;
            RelationGroup group = null;
            List<RelationGroup> groups = null;
            for (RelationTypeVO vo : typeList) {
                if (!Objects.equals(vo.getGroupCode(), code)) {
                    code = vo.getGroupCode();
                    groups = new ArrayList<>();
                    data.put(code, groups);
                }
                if (!Objects.equals(vo.getGroupId(), groupId)) {
                    groupId = vo.getGroupId();
                    group = new RelationGroup();
                    group.setId(groupId);
                    group.setName(vo.getGroupName());
                    groups.add(group);
                }
                RelationType relationType = new RelationType();
                relationType.setId(vo.getId());
                relationType.setName(vo.getName());
                group.addRelationType(relationType);
            }
        }
        return data;
    }

    /**
     * 获取图谱关系
     *
     * @param chartId
     * @return
     */
    @Override
    public List<RelationType> getChartRelationTypes(Long chartId) {
        String shipIds = chartDAO.queryChartRelations(chartId);
        if (StringUtils.isBlank(shipIds)) {
            return new ArrayList<>();
        }
        return chartDAO.queryRelationTypes(shipIds);
    }

    /**
     * 获取指定类型所有的关系ID
     *
     * @param type
     * @return
     */
    @Override
    public List<Integer> getRelationIds(Integer type) {
        List<Integer> ids = new ArrayList<>();
        List<RelationTypeVO> typeList = chartDAO.queryRelationType(type);
        if (typeList != null) {
            for (RelationTypeVO vo : typeList) {
                ids.add(vo.getId());
            }
        }
        return ids;
    }

    /**
     * 获取图谱数据
     *
     * @param chartId
     * @return
     */
    @Override
    public RelationChart getRelationChart(Long chartId) {
        Neo4jChart neo4jChart = new Neo4jChart();
        Set<Neo4jNode> nodeSet = new HashSet<>();
        List<Neo4jRelation> relationList = relationDAO.queryRelations(chartId);
        for (Neo4jRelation relation : relationList) {
            nodeSet.add(relation.getStartNode());
            nodeSet.add(relation.getEndNode());
        }
        neo4jChart.setNodes(nodeSet.stream().collect(Collectors.toList()));
        neo4jChart.setRelations(relationList);
        return RelationUtils.convertChart(neo4jChart);
    }

    /**
     * 获取图谱的节点分页数据
     *
     * @param chartId
     * @param nodeName
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public JSONObject getChartNodes(Long chartId, String nodeName, Integer pageNo, Integer pageSize) {
        Page<Neo4jNode> pages;
        int page = pageNo - 1;
        Pageable pageable = PageRequest.of(page, pageSize);
        if (StringUtils.isBlank(nodeName)) {
            pages = nodeDAO.queryNodes(chartId, pageable);
        } else {
            nodeName = ".*" + nodeName + ".*";
            pages = nodeDAO.queryNodesByName(chartId, nodeName, pageable);
        }
        long total = pages.getTotalElements();
        List<Neo4jNode> nodeList = pages.getContent();
        //
        List<Node> nodes = new ArrayList<>();
        if (nodeList != null) {
            for (Neo4jNode neo4jNode : nodeList) {
                Node node = new Node();
                node.setId(neo4jNode.getId());
                node.setName(neo4jNode.getName());
                node.setType(this.nodeTypes.get(neo4jNode.getType()));
                Set<String> labelSet = RelationUtils.convertToSet(neo4jNode.getLabels());
                if (!CollectionUtils.isEmpty(labelSet)) {
                    StringBuilder labels = new StringBuilder();
                    int i = 0;
                    for (String label : labelSet) {
                        if (i > 0) {
                            labels.append(", ");
                        }
                        labels.append(label);
                        i++;
                    }
                    node.setLabels(labels.toString());
                }
                Set<String> attrSet = RelationUtils.convertToSet(neo4jNode.getProperties());
                if (!CollectionUtils.isEmpty(attrSet)) {
                    StringBuilder properties = new StringBuilder();
                    int i = 0;
                    for (String property : attrSet) {
                        if (i > 0) {
                            properties.append(", ");
                        }
                        properties.append(property);
                        i++;
                    }
                    node.setProperties(properties.toString());
                }
                nodes.add(node);
            }
        }
        JSONObject data = new JSONObject();
        data.put("total", total);
        data.put("rows", nodes);
        return data;
    }

    /**
     * 获取图谱的所有节点数据
     *
     * @param chartId
     * @param nodeName
     * @return
     */
    @Override
    public List<Node> getChartNodes(Long chartId, String nodeName) {
        List<Node> nodes = new ArrayList<>();
        List<Neo4jNode> nodeList;
        if (StringUtils.isBlank(nodeName)) {
            nodeList = nodeDAO.queryNodes(chartId);
        } else {
            nodeName = ".*" + nodeName + ".*";
            nodeList = nodeDAO.queryNodesByName(chartId, nodeName);
        }
        if (nodeList != null) {
            for (Neo4jNode neo4jNode : nodeList) {
                Node node = new Node();
                node.setId(neo4jNode.getId());
                node.setName(neo4jNode.getName());
                node.setType(this.nodeTypes.get(neo4jNode.getType()));
                Set<String> labelSet = RelationUtils.convertToSet(neo4jNode.getLabels());
                if (!CollectionUtils.isEmpty(labelSet)) {
                    StringBuilder labels = new StringBuilder();
                    int i = 0;
                    for (String label : labelSet) {
                        if (i > 0) {
                            labels.append(", ");
                        }
                        labels.append(label);
                        i++;
                    }
                    node.setLabels(labels.toString());
                }
                Set<String> attrSet = RelationUtils.convertToSet(neo4jNode.getProperties());
                if (!CollectionUtils.isEmpty(attrSet)) {
                    StringBuilder properties = new StringBuilder();
                    int i = 0;
                    for (String property : attrSet) {
                        if (i > 0) {
                            properties.append(", ");
                        }
                        properties.append(property);
                        i++;
                    }
                    node.setProperties(properties.toString());
                }
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * 获取图谱关系分页数据
     *
     * @param chartId
     * @param relationName
     * @param nodeName
     * @param pageNo
     * @param pageSize
     * @return
     */
    @Override
    public JSONObject getChartRelations(Long chartId, String relationName, String nodeName, Integer pageNo, Integer pageSize) {
        Page<Neo4jRelation> pages;
        int page = pageNo - 1;
        Pageable pageable = PageRequest.of(page, pageSize);
        if (StringUtils.isBlank(relationName) && StringUtils.isBlank(nodeName)) {
            pages = relationDAO.queryRelations(chartId, pageable);
        } else if (StringUtils.isNotBlank(relationName) && StringUtils.isBlank(nodeName)) {
            pages = relationDAO.queryRelationsByRelationName(chartId, relationName, pageable);
        } else if (StringUtils.isBlank(relationName) && StringUtils.isNotBlank(nodeName)) {
            nodeName = ".*" + nodeName + ".*";
            pages = relationDAO.queryRelationsByNodeName(chartId, nodeName, pageable);
        } else {
            nodeName = ".*" + nodeName + ".*";
            pages = relationDAO.queryRelationsByName(chartId, relationName, nodeName, pageable);
        }
        long total = pages.getTotalElements();
        List<Neo4jRelation> relationList = pages.getContent();
        //
        List<Relation> relations = new ArrayList<>();
        if (relationList != null) {
            for (Neo4jRelation neo4jRelation : relationList) {
                Relation relation = new Relation();
                relation.setId(neo4jRelation.getId());
                relation.setName(neo4jRelation.getName());
                relation.setStartId(neo4jRelation.getStartNode().getId());
                relation.setStartName(neo4jRelation.getStartNode().getName());
                relation.setEndId(neo4jRelation.getEndNode().getId());
                relation.setEndName(neo4jRelation.getEndNode().getName());
                relations.add(relation);
            }
        }
        JSONObject data = new JSONObject();
        data.put("total", total);
        data.put("rows", relations);
        return data;
    }

    /**
     * 获取图谱所有关系数据
     *
     * @param chartId
     * @param relationName
     * @param nodeName
     * @return
     */
    @Override
    public List<Relation> getChartRelations(Long chartId, String relationName, String nodeName) {
        List<Relation> relations = new ArrayList<>();
        List<Neo4jRelation> relationList;
        if (StringUtils.isBlank(relationName) && StringUtils.isBlank(nodeName)) {
            relationList = relationDAO.queryRelations(chartId);
        } else if (StringUtils.isNotBlank(relationName) && StringUtils.isBlank(nodeName)) {
            relationList = relationDAO.queryRelationsByRelationName(chartId, relationName);
        } else if (StringUtils.isBlank(relationName) && StringUtils.isNotBlank(nodeName)) {
            nodeName = ".*" + nodeName + ".*";
            relationList = relationDAO.queryRelationsByNodeName(chartId, nodeName);
        } else {
            nodeName = ".*" + nodeName + ".*";
            relationList = relationDAO.queryRelationsByName(chartId, relationName, nodeName);
        }
        if (relationList != null) {
            for (Neo4jRelation neo4jRelation : relationList) {
                Relation relation = new Relation();
                relation.setId(neo4jRelation.getId());
                relation.setName(neo4jRelation.getName());
                relation.setStartId(neo4jRelation.getStartNode().getId());
                relation.setStartName(neo4jRelation.getStartNode().getName());
                relation.setEndId(neo4jRelation.getEndNode().getId());
                relation.setEndName(neo4jRelation.getEndNode().getName());
                relations.add(relation);
            }
        }
        return relations;
    }

    /**
     * 查询当日同类进件ID
     *
     * @param chartInput
     * @return
     */
    @Override
    public Long getChartInputId(ChartInput chartInput) {
        List<Integer> ids = chartInput.getShipIds();
        Collections.sort(ids);
        String shipIds = "";
        int i = 0;
        for (Integer id : ids) {
            if (i > 0) {
                shipIds += ",";
            }
            shipIds += id.toString();
            i++;
        }

        InputVO inputVO = new InputVO();
        inputVO.setApiCode(chartInput.getApiCode());
        inputVO.setChartType(chartInput.getType().value());
        inputVO.setChartDepth(chartInput.getDepth());
        inputVO.setChartThreshold(chartInput.getThreshold());
        inputVO.setChartRelations(shipIds);
        Set<Input> inputs = chartInput.getInputs();
        for (Input input : inputs) {
            inputVO.setCompanyName(input.getCompanyName());
            inputVO.setName(input.getName());
            inputVO.setIdNo(input.getIdNo());
        }
        return chartDAO.queryChartInput(inputVO);
    }

    /**
     * 保存图谱分析进件信息
     *
     * @param userId
     * @param chartInput
     */
    @Override
    @Transactional
    public void saveChartInput(Integer userId, ChartInput chartInput) {
        Set<Input> inputs = chartInput.getInputs();
        if (CollectionUtils.isEmpty(inputs)) {
            throw new RuntimeException("图谱分析未提交要分析的企业或者董监高信息!!");
        }
        List<Integer> ids = chartInput.getShipIds();
        Collections.sort(ids);
        String shipIds = "";
        int i = 0;
        for (Integer id : ids) {
            if (i > 0) {
                shipIds += ",";
            }
            shipIds += id.toString();
            i++;
        }
        ChartVO chartVO = new ChartVO();
        chartVO.setType(chartInput.getType().value());
        chartVO.setDepth(chartInput.getDepth());
        chartVO.setThreshold(chartInput.getThreshold());
        chartVO.setRelations(shipIds);
        chartVO.setApiCode(chartInput.getApiCode());
        chartVO.setUserId(userId);
        chartDAO.insertChart(chartVO);
        chartDAO.insertChartInput(chartVO.getId(), inputs.stream().collect(Collectors.toList()));
        chartInput.setId(chartVO.getId());
    }

    /**
     * 调用图谱进件分析
     *
     * @param chartInput
     * @param type
     * @return
     */
    @Override
    public RelationChart chartAnalysis(ChartInput chartInput, String type) {
        Integer result = 1;
        RelationChart data = null;
        RelationChart chart = bizRelationChart.analysis(chartInput);
        if (Objects.equals(type, Constants.STAR_NETWORK)) {
            data = chart;
        } else if (Objects.equals(type, Constants.TREE_NETWORK)){
            data = new RelationChart();
            data.setChartId(chartInput.getId());
            List<ChartNode> nodes = chart.getNodes();
            //不支持多企业关系碰撞的树型网络
            if (chartInput.getType() != EChart.MANY_COMPANY && !CollectionUtils.isEmpty(nodes)) {
                MyChart myChart = new MyChart();
                myChart.setChartId(chartInput.getId());
                data = myChart;
                //
                ChartNode root = null;
                for (ChartNode node : nodes) {
                    if (node.isRootNode()) {
                        root = node;
                        break;
                    }
                }
                Neo4jChart neo4jChart = RelationUtils.convertChart(chart);
                //
                Map<Integer, ChartNode> ioNodes = new HashMap<>();
                MyNode rootInNode = new MyNode();
                rootInNode.setId(root.getId());
                rootInNode.setName(root.getName());
                rootInNode.setType(root.getType());
                rootInNode.setLabels(root.getLabels());
                rootInNode.setProperties(root.getProperties());
                rootInNode.setRootNode(root.isRootNode());
                Set<MyNode> subNodes = findSubNodes(neo4jChart.getRelations(), ioNodes, rootInNode, true);
                if (!CollectionUtils.isEmpty(subNodes)) {
                    rootInNode.setSubNodes(subNodes);
                }
                myChart.addInNode(rootInNode);
                //
                ioNodes.clear();
                MyNode rootOutNode = new MyNode();
                rootOutNode.setId(root.getId());
                rootOutNode.setName(root.getName());
                rootOutNode.setType(root.getType());
                rootOutNode.setLabels(root.getLabels());
                rootOutNode.setProperties(root.getProperties());
                rootOutNode.setRootNode(root.isRootNode());
                subNodes = findSubNodes(neo4jChart.getRelations(), ioNodes, rootOutNode, false);
                if (!CollectionUtils.isEmpty(subNodes)) {
                    rootOutNode.setSubNodes(subNodes);
                }
                myChart.addOutNode(rootOutNode);
            }
        } else {
            result = 2;
            data = new RelationChart();
            data.setChartId(chartInput.getId());
            log.error("未知分析类型[{}]", type);
        }
        //更新图谱进件分析结果
        try {
            chartDAO.updateChart(chartInput.getId(), result);
        } catch (Exception e) {
            log.error("更新图谱进件[{}]分析结果失败: {}", chartInput.getId(), e.getMessage());
        }
        return data;
    }

    /**
     * 查找根节点的子节点
     *
     * @param relations
     * @param ioNodes
     * @param rootNode
     * @param isIn
     * @return
     */
    private Set<MyNode> findSubNodes(List<Neo4jRelation> relations, Map<Integer, ChartNode> ioNodes, MyNode rootNode, boolean isIn) {
        if (relations == null) {
            return null;
        }
        ioNodes.put(rootNode.getId(), rootNode);
        Set<MyNode> subNodes = new LinkedHashSet<>();
        for (Neo4jRelation relation : relations) {
            Neo4jNode node4jNode = isIn ? relation.getEndNode() : relation.getStartNode();
            if (Objects.equals(node4jNode.getId(), rootNode.getId())) {
                Neo4jNode node = isIn ? relation.getStartNode() : relation.getEndNode();
                //
                MyNode myNode = new MyNode();
                myNode.setId(node.getId());
                myNode.setName(node.getName());
                myNode.setType(node.getType());
                myNode.setLabels(RelationUtils.convertToSet(node.getLabels()));
                myNode.setProperties(RelationUtils.convertToSet(node.getProperties()));
                myNode.setRootNode(node.isRootNode());
                myNode.setRelationAttr(relation.getAttr());
                if (!ioNodes.containsKey(node.getId())) {
                    Set<MyNode> mySubNodes = findSubNodes(relations, ioNodes, myNode, isIn);
                    if (!CollectionUtils.isEmpty(mySubNodes)) {
                        myNode.setSubNodes(mySubNodes);
                    }
                }
                subNodes.add(myNode);
            }
        }
        return subNodes;
    }

    /**
     * 获取扩展关系数据
     *
     * @param chartId
     * @param nodeId
     * @return
     */
    @Override
    public RelationChart getExtendRelation(Long chartId, Integer nodeId) {
        List<Neo4jNode> neo4jNodes = null;
        List<Neo4jRelation> neo4jRelations = relationDAO.queryExtendRelations(chartId, nodeId);
        if (neo4jRelations == null) {
            Neo4jNode neo4jNode = nodeDAO.queryNodeById(chartId, nodeId);
            if (neo4jNode != null) {
                neo4jNodes = new ArrayList<>();
                neo4jNodes.add(neo4jNode);
            }
        } else {
            Set<Neo4jNode> neo4jNodeSet = new LinkedHashSet<>();
            for (Neo4jRelation relation : neo4jRelations) {
                neo4jNodeSet.add(relation.getStartNode());
                neo4jNodeSet.add(relation.getEndNode());
            }
            neo4jNodes = neo4jNodeSet.stream().collect(Collectors.toList());
        }
        Neo4jChart neo4jChart = new Neo4jChart();
        neo4jChart.setChartId(chartId);
        neo4jChart.setNodes(neo4jNodes);
        neo4jChart.setRelations(neo4jRelations);
        return RelationUtils.convertChart(neo4jChart);
    }

    /**
     * 获取两个节点前的最短路径
     *
     * @param chartId
     * @param startId
     * @param endId
     * @return
     */
    @Override
    public RelationChart getShortestPaths(Long chartId, Integer startId, Integer endId) {
        List<Neo4jNode> neo4jNodes = null;
        List<Neo4jRelation> neo4jRelations = relationDAO.queryShortestRelations(chartId, startId, endId);
        if (neo4jRelations != null) {
            Set<Neo4jNode> neo4jNodeSet = new LinkedHashSet<>();
            for (Neo4jRelation relation : neo4jRelations) {
                neo4jNodeSet.add(relation.getStartNode());
                neo4jNodeSet.add(relation.getEndNode());
            }
            neo4jNodes = neo4jNodeSet.stream().collect(Collectors.toList());
        }
        Neo4jChart neo4jChart = new Neo4jChart();
        neo4jChart.setChartId(chartId);
        neo4jChart.setNodes(neo4jNodes);
        neo4jChart.setRelations(neo4jRelations);
        return RelationUtils.convertChart(neo4jChart);
    }
}
