package com.biz.chart.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.chart.entity.Node;
import com.biz.chart.entity.Relation;
import com.biz.chart.entity.RelationType;
import com.biz.chart.service.IChartService;
import com.biz.chart.utils.Constants;
import com.biz.chart.utils.ExcelUtils;
import com.biz.chart.utils.RespEntity;
import com.biz.relation.entity.ChartInput;
import com.biz.relation.entity.Input;
import com.biz.relation.entity.RelationChart;
import com.biz.relation.enums.EChart;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/chart")
public class ChartController {

    @Value("${spring.profiles.active}")
    private String env;

    @Autowired
    private IChartService service;

    /**
     * 初始化SESSION, 测试环境使用
     *
     * @param session
     * @return
     */
    @GetMapping("/initSession")
    public RespEntity initSession(HttpSession session) {
        RespEntity respEntity = RespEntity.success();
        //测试环境
        if (Objects.equals(env, "uat") || Objects.equals(env, "dev")) {
            session.setAttribute("userId", "23");
            session.setAttribute("userType", "0");
            session.setAttribute("realname", "管理员");
            session.setAttribute("userAuth", "admin");
            //
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionId", session.getId());
            respEntity.setData(jsonObject);
        }
        return respEntity;
    }

    /**
     * 获取企业或董监高的关系配置
     *
     * @param type
     * @return
     */
    @GetMapping("/relation")
    public RespEntity getRelationType(@RequestParam("type") Integer type) {
        RespEntity respEntity = RespEntity.success();

        try {
            JSONObject data = service.getRelationType(type);
            respEntity.setData(data);
        } catch (Exception e) {
            log.error("查询[" + type + "]的关系配置失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

    /**
     * 根据分析图谱ID获取关系
     *
     * @param chartId
     * @return
     */
    @GetMapping("/relation/{chartId}")
    public RespEntity getRelations(@PathVariable("chartId") Long chartId) {
        RespEntity respEntity = RespEntity.success();

        try {
            List<RelationType> data = service.getChartRelationTypes(chartId);
            respEntity.setData(data);
        } catch (Exception e) {
            log.error("查询分析图谱[" + chartId + "]的关系失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

    /**
     * 根据分析图谱ID获取图谱数据
     *
     * @param chartId
     * @return
     */
    @GetMapping("/relationChart/{chartId}")
    public RespEntity getRelationChart(@PathVariable("chartId") Long chartId) {
        RespEntity respEntity = RespEntity.success();

        try {
            RelationChart charts = service.getRelationChart(chartId);
            respEntity.setData(charts);
        } catch (Exception e) {
            log.error("查询[" + chartId + "]的关系数据失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

    /**
     * 根据分析图谱ID获取图谱节点数据
     *
     * @param chartId
     * @param nodeName
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/chartNode")
    public RespEntity getChartNodes(@RequestParam(value = "chartId") Long chartId,
                                    @RequestParam(value = "nodeName", required = false) String nodeName,
                                    @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                    @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        RespEntity respEntity = RespEntity.success();

        try {
            JSONObject data = service.getChartNodes(chartId, nodeName, pageNo, pageSize);
            respEntity.setData(data);
        } catch (Exception e) {
            log.error("查询[" + chartId + "]的节点数据失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

    /**
     * 下载图谱节点数据
     *
     * @param chartId
     * @param nodeName
     * @param response
     */
    @GetMapping("/chartNode/download")
    public void downloadChartNodes(@RequestParam(value = "chartId") Long chartId,
                                   @RequestParam(value = "nodeName", required = false) String nodeName,
                                   HttpServletResponse response) {
        try {
            List<Node> data = service.getChartNodes(chartId, nodeName);
            //表头
            Map<String,String> rowMapper = new LinkedHashMap<>();
            rowMapper.put("type","类型");
            rowMapper.put("name","节点名称");
            rowMapper.put("labels","标签");

            String fileName = "关系图谱节点列表.xlsx";
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.flushBuffer();
            XSSFWorkbook workbook = ExcelUtils.getWorkbook(data, rowMapper);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("查询[" + chartId + "]的节点数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据分析图谱ID获取图谱关系数据
     *
     * @param chartId
     * @param relationName
     * @param nodeName
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/chartRelation")
    public RespEntity getChartRelations(@RequestParam(value = "chartId") Long chartId,
                                        @RequestParam(value = "relationName", required = false) String relationName,
                                        @RequestParam(value = "nodeName", required = false) String nodeName,
                                        @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        RespEntity respEntity = RespEntity.success();

        try {
            JSONObject data = service.getChartRelations(chartId, relationName, nodeName, pageNo, pageSize);
            respEntity.setData(data);
        } catch (Exception e) {
            log.error("查询[" + chartId + "]的节点数据失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

    /**
     * 下载图谱关系数据
     *
     * @param chartId
     * @param relationName
     * @param nodeName
     * @param response
     */
    @GetMapping("/chartRelation/download")
    public void downloadChartRelations(@RequestParam(value = "chartId") Long chartId,
                                       @RequestParam(value = "relationName", required = false) String relationName,
                                       @RequestParam(value = "nodeName", required = false) String nodeName,
                                       HttpServletResponse response) {
        try {
            List<Relation> data = service.getChartRelations(chartId, relationName, nodeName);
            //表头
            Map<String,String> rowMapper = new LinkedHashMap<>();
            rowMapper.put("startName","节点名称");
            rowMapper.put("name","关系");
            rowMapper.put("endName","节点名称");

            String fileName = "关系图谱关系列表.xlsx";
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.flushBuffer();
            XSSFWorkbook workbook = ExcelUtils.getWorkbook(data, rowMapper);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("查询[" + chartId + "]的关系数据失败: " + e.getMessage(), e);
        }
    }

    /**
     * 单企业图谱分析
     *
     * @param companyName 公司名称
     * @param depth       深度
     * @param threshold   穿透阈值
     * @param shipIds     选择的关系, ID以逗号分隔
     * @param type        类型：1-星型网络 2-树形结构
     * @param session
     * @return
     */
    @GetMapping("/chartAnalysis/singleCompany")
    public RespEntity chartAnalysis(@RequestParam("companyName") String companyName,
                                    @RequestParam("depth") int depth,
                                    @RequestParam("threshold") String threshold,
                                    @RequestParam("shipIds") String shipIds,
                                    @RequestParam(value = "type", defaultValue = Constants.STAR_NETWORK) String type,
                                    HttpSession session) {

        ChartInput chartInput = new ChartInput();
        chartInput.setType(EChart.ONE_COMPANY);
        chartInput.setDepth(depth);
        chartInput.setThreshold(threshold);
        String[] ids = StringUtils.tokenizeToStringArray(shipIds, ", ");
        if (ids != null) {
            for (String id : ids) {
                chartInput.addShipId(Integer.valueOf(id));
            }
        }

        Integer userId = null;
        if (session != null) {
            Object value = session.getAttribute(Constants.USER_ID);
            if (value != null) {
                userId = Integer.valueOf(value.toString());
            }
            value = session.getAttribute(Constants.API_CODE);
            if (value != null) {
                chartInput.setApiCode(value.toString());
            }
        }

        RespEntity respEntity = RespEntity.success();
        try {
            Input input = new Input();
            input.setCompanyName(companyName);
            chartInput.addInput(input);
            //
            RelationChart chart;
            Long chartId = null; //service.getChartInputId(chartInput);
            if (chartId == null) {
                service.saveChartInput(userId, chartInput);
                chart = service.chartAnalysis(chartInput, type);
            } else {
                log.info("当日存在相同的图谱分析进件请求, 直接查询图谱数据信息!!");
                chart = service.getRelationChart(chartId);
            }
            respEntity.setData(chart);
        } catch (Exception e) {
            log.error("单企业[" + companyName + "]图谱分析失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

    /**
     * 董监高图谱分析
     *
     * @param companyName 公司名称
     * @param name        董监高名称
     * @param idNo        董监高身份证号码
     * @param depth       深度
     * @param threshold   穿透阈值
     * @param shipIds     选择的关系, ID以逗号分隔
     * @param type        类型：1-星型网络 2-树形结构
     * @param session
     * @return
     */
    @GetMapping("/chartAnalysis/dongJianGao")
    public RespEntity chartAnalysis(@RequestParam("companyName") String companyName,
                                    @RequestParam("name") String name,
                                    @RequestParam("idNo") String idNo,
                                    @RequestParam("depth") int depth,
                                    @RequestParam("threshold") String threshold,
                                    @RequestParam("shipIds") String shipIds,
                                    @RequestParam(value = "type", defaultValue = Constants.STAR_NETWORK) String type,
                                    HttpSession session) {
        ChartInput chartInput = new ChartInput();
        chartInput.setType(EChart.DONG_JIAN_GAO);
        chartInput.setDepth(depth);
        chartInput.setThreshold(threshold);
        Input input = new Input();
        input.setCompanyName(companyName);
        input.setName(name);
        input.setIdNo(idNo);
        chartInput.addInput(input);
        String[] ids = StringUtils.tokenizeToStringArray(shipIds, ", ");
        if (ids != null) {
            for (String id : ids) {
                chartInput.addShipId(Integer.valueOf(id));
            }
        }

        Integer userId = null;
        if (session != null) {
            Object value = session.getAttribute(Constants.USER_ID);
            if (value != null) {
                userId = Integer.valueOf(value.toString());
            }
            value = session.getAttribute(Constants.API_CODE);
            if (value != null) {
                chartInput.setApiCode(value.toString());
            }
        }

        RespEntity respEntity = RespEntity.success();
        try {
            RelationChart chart;
            Long chartId = null; //service.getChartInputId(chartInput);
            if (chartId == null) {
                service.saveChartInput(userId, chartInput);
                chart = service.chartAnalysis(chartInput, type);
            } else {
                log.info("当日存在相同的图谱分析进件请求, 直接查询图谱数据信息!!");
                chart = service.getRelationChart(chartId);
            }
            respEntity.setData(chart);
        } catch (Exception e) {
            log.error("单企业[" + companyName + "]图谱分析失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

    /**
     * 图谱节点最短路径查询
     *
     * @param chartId
     * @param startId
     * @param endId
     * @return
     */
    @GetMapping("/chartAnalysis/shortestPaths")
    public RespEntity shortestPathsQuery(@RequestParam("chartId") Long chartId,
                                         @RequestParam("startId") Integer startId,
                                         @RequestParam("endId") Integer endId) {
        RespEntity respEntity = RespEntity.success();
        try {
            RelationChart chart = service.getShortestPaths(chartId, startId, endId);
            respEntity.setData(chart);
        } catch (Exception e) {
            log.error("查询[" + chartId + "][" + startId + "][" + endId + "]最短路径失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

    /**
     * 图谱节点扩展关系查询
     *
     * @param chartId
     * @param nodeId
     * @return
     */
    @GetMapping("/chartAnalysis/extendRelationQuery")
    public RespEntity extendRelationQuery(@RequestParam("chartId") Long chartId,
                                          @RequestParam("nodeId") Integer nodeId) {
        RespEntity respEntity = RespEntity.success();
        try {
            RelationChart chart = service.getExtendRelation(chartId, nodeId);
            respEntity.setData(chart);
        } catch (Exception e) {
            log.error("查询[" + chartId + "]的扩展关系数据失败: " + e.getMessage(), e);
        }
        return respEntity;
    }

}
