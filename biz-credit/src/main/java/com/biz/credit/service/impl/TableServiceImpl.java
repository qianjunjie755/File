package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.*;
import com.biz.credit.domain.*;
import com.biz.credit.service.IProjectService;
import com.biz.credit.service.ITableService;
import com.biz.credit.utils.Constants;
import com.biz.credit.utils.ThresholdUtil;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.DTableVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class TableServiceImpl implements ITableService {
    @Autowired
    private DTableDAO dTableDAO;
    @Autowired
    private DTableCondDAO dTableCondDAO;
    @Autowired
    private DNodeModelDAO dNodeModelDAO;
    @Autowired
    private DNodeThresholdDAO dNodeThresholdDAO;
    @Autowired
    private DTableVarDAO dTableVarDAO;
    @Autowired
    private IProjectService projectService;

    @Override
    @Transactional
    public RespEntity saveTable(DTableVO dTableVO){
        if (dTableVO.getProjectId() == null) {
            Project project = projectService.getFirstProject(dTableVO.getApiCode(), dTableVO.getUserId());
            dTableVO.setProjectId(project.getId());
        }
        dTableVO.setStatus(Constants.TABLE_STATUS_SAVE);
        if(dTableVO.getTableId()==null){
            int result =dTableDAO.queryTableVersion(dTableVO);
            if(result>0){
                return RespEntity.error().setMsg("当前决策表已存在");
            }
            int count= dTableDAO.insert(dTableVO);
           dTableVO.setTableId(dTableVO.getTableId());
            if(!CollectionUtils.isEmpty(dTableVO.getTableVarList())) {
                setTableVarAndCond(dTableVO);
            }

        }else{
            RespEntity checkResp = checkParams(dTableVO);
            if(!checkResp.isSuccess()){
                return checkResp;
            }
            dTableDAO.update(dTableVO);
            Long tableId=dTableVO.getTableId();
            //插入变量顺序
            if(!CollectionUtils.isEmpty(dTableVO.getTableVarList())){
                //失效条件
                int relation=dTableCondDAO.updateCond(tableId);
                log.info("失效上一次保存条件数据[{}]条",relation);
                //失效变量
                int var=dTableVarDAO.updateVar(tableId);
                log.info("失效上一次保存变量数据[{}]条",var);
                setTableVarAndCond(dTableVO);
            }
        }
        return RespEntity.success();
    }

    @Override
    @Transactional
    public RespEntity publishTable(DTableVO dTableVO) {
        RespEntity checkResp = checkParams(dTableVO);
        if(!checkResp.isSuccess()){
            return checkResp;
        }
        dTableVO.setStatus(Constants.TABLE_STATUS_PUSH);
        if(dTableVO.getTableId()==null){
            if(dTableVO.getTableVersion()!=null){
                int result =dTableDAO.queryTableVersion(dTableVO);
                if(result>0){
                    return RespEntity.error().setMsg("当前决策表版本已存在");
                }
            }
            int count= dTableDAO.insert(dTableVO);
            Long tableId=dTableVO.getTableId();
            dTableVO.setTableId(tableId);
            //插入变量顺序
           setTableVarAndCond(dTableVO);
        }else{
            dTableDAO.update(dTableVO);
            if(!CollectionUtils.isEmpty(dTableVO.getTableVarList())){
                //失效变量
                int var=dTableVarDAO.updateVar(dTableVO.getTableId());
                log.info("失效上一次保存关系数据[{}]条",var);
                //失效条件
                int relation=dTableCondDAO.updateCond(dTableVO.getTableId());
                log.info("失效上一次保存条件数据[{}]条",relation);
                //插入变量顺序
                setTableVarAndCond(dTableVO);
            }
        }
        return RespEntity.success();
    }
    public  RespEntity  setTableVarAndCond(DTableVO dTableVO){
        Long parentId=0L;
        List<DTableVar> dTableVarList=dTableVO.getTableVarList();
        for(DTableVar dTableVar:dTableVarList){
            if(dTableVar.getSrcVarId()==null){
                return RespEntity.error().setMsg("变量不能为空");
            }
            dTableVar.setTableId(dTableVO.getTableId());
            dTableVar.setStatus(Constants.TABLEVAR_STATUS_VALID);
            dTableVarDAO.insert(dTableVar);
            List<DTableCond> dTableCondList=dTableVar.getTableCondList();
            dTableCondList.forEach(dTableCond -> {
                dTableCond.setTableId(dTableVO.getTableId());
                dTableCond.setStatus(Constants.TABLECOND_STATUS_VALID);
                dTableCond.setVarId(dTableVar.getVarId());
                Expression expression=new Expression();
                expression.setLeft(dTableCond.getLeft());
                expression.setLeftExpr(dTableCond.getLeftExpr());
                expression.setRight(dTableCond.getRight());
                expression.setRightExpr(dTableCond.getRightExpr());
                dTableCond.setCondJudge(ThresholdUtil.convert(expression));
                if(dTableCond.getParentCondOrder()== -1){
                    dTableCond.setParentId(parentId);
                }else{
                    DTableVar dTableVar1=new DTableVar();
                    dTableVar1.setTableId(dTableVO.getTableId());
                    dTableVar1.setVarOrder(dTableVar.getVarOrder()-1);
                    Long varid=dTableVarDAO.queryParentVarId(dTableVar1);
                    DTableCond dTableCond1=new DTableCond();
                    dTableCond1.setTableId(dTableVO.getTableId());
                    dTableCond1.setVarId(varid);
                    dTableCond1.setParentCondOrder(dTableCond.getParentCondOrder());
                    Long ParentId=dTableCondDAO.queryByParentId(dTableCond1);
                    dTableCond.setParentId(ParentId);
                }
                dTableCondDAO.insertCond(dTableCond);
            });
        }
        return RespEntity.success();
    }
    @Override
    public DTableVO getTableByTableId(Long tableId) {
        DTableVO dTableVO =dTableDAO.queryById(tableId);
        List<DTableVar> tableVarList=dTableVarDAO.queryVar(tableId);
        List<DTableCond> tableCondList =dTableCondDAO.queryCondList(tableId);
        List<DTableVar> dTableVarList=new ArrayList<>();
        for(DTableVar dTableVar :tableVarList){
            List<DTableCond> tableCondList1=new ArrayList<>();
            for(DTableCond dTableCond : tableCondList){
                if(dTableVar.getVarId().equals(dTableCond.getVarId())){
                    Expression expression=ThresholdUtil.convert(dTableCond.getCondJudge());
                    dTableCond.setLeft(expression.getLeft());
                    dTableCond.setLeftExpr(expression.getLeftExpr());
                    dTableCond.setRightExpr(expression.getRightExpr());
                    dTableCond.setRight(expression.getRight());
                    dTableCond.setCondType(dTableVar.getCondType());
                    if(dTableCond.getParentId()==0){
                        dTableCond.setParentCondOrder(-1);
                    }
                    for(DTableCond dTableCond1 : tableCondList){
                        if(dTableCond.getId().equals(dTableCond1.getParentId()) && "=缺失值".equals(dTableCond1.getCondJudge())){
                            dTableCond.setIsExist(true);
                        }
                        if(dTableCond.getId().equals(dTableCond1.getId()) && "=缺失值".equals(dTableCond1.getCondJudge())){
                            dTableCond.setDefaultValue(true);
                        }
                        if(dTableCond.getParentId().equals(dTableCond1.getId())){
                            dTableCond.setParentCondOrder(dTableCond1.getCondOrder());
                        }
                        if(dTableCond.getId().equals(dTableCond1.getParentId())){
                            for(DTableVar dTableVar1 :tableVarList){
                                if(dTableCond1.getVarId().equals(dTableVar1.getVarId())){
                                    dTableCond.setNextCondType(dTableVar1.getCondType());
                                }
                            }
                        }
                    }
                    tableCondList1.add(dTableCond);
                }
            }
            dTableVar.setTableCondList(tableCondList1);
            dTableVarList.add(dTableVar);
        }
        dTableVO.setTableVarList(dTableVarList);
        return dTableVO;
    }

    @Override
    public List<DTable> getTableList(Long projectId, String apiCode) {
        DTableVO dTableVO=new DTableVO();
        dTableVO.setProjectId(projectId);
        dTableVO.setApiCode(apiCode);
        return dTableDAO.queryTableList(dTableVO);
    }

    @Override
    public boolean existTableName(String tableName,Long projectId) {
        int count = dTableDAO.existTableName(tableName,projectId);
        return count != 0;
    }

    @Override
    public String getMaxVersionByTableName(String tableName) {
        return dTableDAO.queryMaxVersionByTableName(tableName);
    }

    @Override
    public List<DTableVO> getVersionListByTableName(Long projectId,String tableName) {
        DTableVO dTableVO=new DTableVO();
        dTableVO.setProjectId(projectId);
        dTableVO.setTableName(tableName);
        return dTableDAO.queryVersionListByTableName(dTableVO);
    }
    @Override
    public List<JSONObject> queryTableConfig(DNodeConfigVO nodeConfigVO) {
        DNodeModel dNodeModel=new DNodeModel();
        dNodeModel.setNodeId(nodeConfigVO.getNodeId());
        dNodeModel.setApiCode(Long.parseLong(nodeConfigVO.getApiCode()));
        dNodeModel.setModelType(Constants.DECISION_TABLE);
        List<com.biz.credit.vo.DTableVO> tableList = dTableDAO.findTableConfig(dNodeModel);
        List<JSONObject> tList = new ArrayList<>();
        tableList.forEach(dTableVO -> {
            JSONObject jsonObject = new JSONObject();
                if(!StringUtils.isEmpty(dTableVO.getJudge())){
                    DNodeThreshold dNodeThreshold=new DNodeThreshold();
                    List<DNodeThreshold> nodeThresholds=new ArrayList<>();
                    dNodeThreshold.setType(dTableVO.getType());
                        if(dTableVO.getReturnType()==2){
                            dNodeThreshold.setJudge(dTableVO.getJudge());
                            nodeThresholds.add(dNodeThreshold);
                        }else{
                            String Judge=dTableVO.getJudge().substring(2);
                            dNodeThreshold.setJudge(Judge);
                            nodeThresholds.add(dNodeThreshold);
                        }
                    jsonObject.put("nodeThresholdList",nodeThresholds);
                }else{
                    List<DNodeThreshold> list =new ArrayList<>();
                    DNodeThreshold dNodeThreshold=new DNodeThreshold();
                    dNodeThreshold.setJudge("");
                    list.add(dNodeThreshold);
                    jsonObject.put("nodeThresholdList",list);
                }
            jsonObject.put("tableId",dTableVO.getTableId());
            jsonObject.put("name", dTableVO.getProjectName());
            jsonObject.put("returnType", dTableVO.getReturnType());
            jsonObject.put("tableVersion", dTableVO.getTableVersion());
            jsonObject.put("tableDesc", dTableVO.getTableDesc());
            jsonObject.put("returnField", dTableVO.getReturnField());
            jsonObject.put("tableName", dTableVO.getTableName());
            jsonObject.put("userId", dTableVO.getUserId());
            jsonObject.put("choose",dTableVO.isChoose());

            tList.add(jsonObject);
        });
        return tList;
    }

    @Override
    @Transactional
    public RespEntity deleteTableByTableId(Long tableId) {
        try{
            int table=dTableDAO.deleteTableByTableId(tableId);
            //失效变量
            //int var=dTableVarDAO.updateVar(tableId);
            //失效条件
           // int relation=dTableCondDAO.updateCond(tableId);
        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("删除决策表失败");
        }
        return RespEntity.success();
    }

    @Override
    public List<DTable> getListByProjectId(Long projectId) {
        return dTableDAO.queryListByProjectId(projectId);
    }

    @Override
    @Transactional
    public RespEntity saveTableConfig(Long nodeId,DNodeConfig nodeTableConfig) {
        if(nodeTableConfig==null){
           return RespEntity.error().setMsg("nodeTableConfig为空");
        }else{
                checkNodeConfigParams(nodeTableConfig);
                DNodeModel dNodeModel=new DNodeModel();
                dNodeModel.setModelType(Constants.DECISION_TABLE);
                dNodeModel.setNodeId(nodeId);
                dNodeModel.setStatus(Constants.COMMON_STATUS_VALID);
                dNodeModel.setModelCode(nodeTableConfig.getTableId());
                //查询
                DNodeModel nodeModel = dNodeModelDAO.queryNodeModel(dNodeModel);
                if(nodeModel==null){
                    dNodeModelDAO.insert(dNodeModel);
                    DNodeThreshold dNodeThreshold=new DNodeThreshold();
                    dNodeThreshold.setModelId(dNodeModel.getModelId());
                    dNodeThreshold.setJudge(nodeTableConfig.getJudge());
                    dNodeThreshold.setType(nodeTableConfig.getType());
                    dNodeThresholdDAO.insert(dNodeThreshold);
                }else{
                    //失效
                    dNodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.DECISION_TABLE,Constants.COMMON_STATUS_INVALID);
                    dNodeModelDAO.insert(dNodeModel);
                    DNodeThreshold dNodeThreshold=new DNodeThreshold();
                    dNodeThreshold.setModelId(dNodeModel.getModelId());
                    dNodeThreshold.setJudge(nodeTableConfig.getJudge());
                    dNodeThreshold.setType(nodeTableConfig.getType());
                    dNodeThresholdDAO.insert(dNodeThreshold);
                }

            return RespEntity.success();
        }

    }

    @Override
    public List<DNodeParam> queryTableApiList(Long tableId) {
        return dTableVarDAO.queryByTableId(tableId);
    }

    private RespEntity checkParams(DTableVO dTableVO) {
        if (StringUtils.isEmpty(dTableVO.getTableName())) {
            return RespEntity.error().setMsg("决策表名称不能为空");
        }
        if (StringUtils.isEmpty(dTableVO.getTableVersion())) {
            return RespEntity.error().setMsg("决策表版本不能为空");
        }
        if (StringUtils.isEmpty(dTableVO.getFieldType())) {
            return RespEntity.error().setMsg("决策表返回字段类型不能为空");
        }
        if (StringUtils.isEmpty(dTableVO.getReturnField())) {
            return RespEntity.error().setMsg("决策表返回字段不能为空");
        }
        if (StringUtils.isEmpty(dTableVO.getReturnType())) {
            return RespEntity.error().setMsg("决策表返回类型不能为空");
        }

        return RespEntity.success();
    }

    private RespEntity checkNodeConfigParams(DNodeConfig dNodeConfig){

        if (StringUtils.isEmpty(dNodeConfig.getJudge())) {
            return RespEntity.error().setMsg("阈值配置区间不能为空");
        }
        if (StringUtils.isEmpty(dNodeConfig.getJudge())) {
            return RespEntity.error().setMsg("阈值配置类型不能为空");
        }
        return RespEntity.success();
    }
}
