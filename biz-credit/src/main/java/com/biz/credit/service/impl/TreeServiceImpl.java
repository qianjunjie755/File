package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.*;
import com.biz.credit.domain.*;
import com.biz.credit.service.IProjectService;
import com.biz.credit.service.ITreeService;
import com.biz.credit.utils.Constants;
import com.biz.credit.utils.ThresholdUtil;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.DTreeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
public class TreeServiceImpl implements ITreeService {
    @Autowired
    private DTreeDAO dTreeDAO;
    @Autowired
    private DTreeCondDAO dTreeCondDAO;
    @Autowired
    private DTreeVarDAO dTreeVarDAO;
    @Autowired
    private DNodeModelDAO dNodeModelDAO;
    @Autowired
    private DNodeThresholdDAO dNodeThresholdDAO;
    @Autowired
    private VariablePeriodDAO variablePeriodDAO;
    @Autowired
    private IProjectService projectService;

    @Override
    @Transactional
    public RespEntity saveTree(DTreeVO dTreeVO) {
        if (dTreeVO.getProjectId() == null) {
            Project project = projectService.getFirstProject(dTreeVO.getApiCode(), dTreeVO.getUserId());
            dTreeVO.setProjectId(project.getId());
        }
        dTreeVO.setStatus(Constants.TREE_STATUS_SAVE);
        Long parentId=0L;
        if(dTreeVO.getTreeId()==null){
            RespEntity checkResp = checkParams(dTreeVO);
            if(!checkResp.isSuccess()){
                return checkResp;
            }
            if(dTreeVO.getVersionType()==1){
                Double version= dTreeDAO.queryMaxVersionByTreeName(dTreeVO.getTreeName(),dTreeVO.getProjectId());
                dTreeVO.setTreeVersion(version+0.1);
            }else{
                dTreeVO.setTreeVersion(1.0);
                int count = dTreeDAO.existTreeName(dTreeVO.getTreeName(),dTreeVO.getProjectId());
                if(count>0){
                    return RespEntity.error().setData("决策树名称已存在");
                }
            }
            int count=dTreeDAO.insert(dTreeVO);
            dTreeVO.setTreeId(dTreeVO.getTreeId());
            if(!CollectionUtils.isEmpty(dTreeVO.getTreeData())){
               List<DTreeVar> list=dTreeVO.getTreeData();
               for(DTreeVar dTreeVar: list){
                   if(!CollectionUtils.isEmpty(dTreeVar.getChildren())){
                       String jsonstr = JSON.toJSONString(dTreeVO.getTreeData());
                       try{
                           return saveRecursive(jsonstr,dTreeVO.getTreeId(), parentId);
                       }catch (Exception e){
                           log.error(e.getMessage(),e);
                           throw new RuntimeException("保存决策树异常");
                       }

                   }else {
                       if(!StringUtils.isEmpty(dTreeVar.getVarName())){
                           dTreeVar.setTreeId(dTreeVO.getTreeId());
                           dTreeVar.setStatus(Constants.TREEVAR_STATUS_VALID);
                           dTreeVarDAO.insertVar(dTreeVar);
                       }
                   }
               }
            }
        }else{
            dTreeDAO.update(dTreeVO);
            if(!CollectionUtils.isEmpty(dTreeVO.getTreeData())){
                Long treeId=dTreeVO.getTreeId();
                //失效上次保存关系
                int varRelation=dTreeVarDAO.updateVar(treeId);
                //失效上次保存条件
                int relation=dTreeCondDAO.updateCond(treeId);
                log.info("失效上一次保存条件数据[{}]条",varRelation);
                log.info("失效上一次保存条件数据[{}]条",relation);
                List<DTreeVar> list=dTreeVO.getTreeData();
                for(DTreeVar dTreeVar: list){
                    if(!CollectionUtils.isEmpty(dTreeVar.getChildren())){
                        String jsonstr = JSON.toJSONString(dTreeVO.getTreeData());
                        try{
                            return saveRecursive(jsonstr,dTreeVO.getTreeId(), parentId);
                        }catch (Exception e){
                            e.printStackTrace();
                            throw new RuntimeException("保存决策树异常");
                        }
                    }else{
                        if(!StringUtils.isEmpty(dTreeVar.getVarName())){
                            dTreeVar.setTreeId(treeId);
                            dTreeVar.setStatus(Constants.TREEVAR_STATUS_VALID);
                            dTreeVarDAO.insertVar(dTreeVar);
                        }
                    }
                }

            }
        }
        return RespEntity.success().setData(dTreeVO);
    }

    @Override
    @Transactional
    public RespEntity publishTree(DTreeVO dTreeVO) {
        dTreeVO.setStatus(Constants.TREE_STATUS_PUSH);
        Long parentId=0L;
        if(dTreeVO.getTreeId()==null){
            RespEntity checkResp = checkParams(dTreeVO);
            if(!checkResp.isSuccess()){
                return checkResp;
            }
            Double version=dTreeDAO.queryMaxVersionByTreeName(dTreeVO.getTreeName(),dTreeVO.getProjectId());
            dTreeVO.setTreeVersion(version+0.1);
            int count=dTreeDAO.insert(dTreeVO);
            if(!CollectionUtils.isEmpty(dTreeVO.getTreeData() )){
                String jsonstr = JSON.toJSONString(dTreeVO.getTreeData());
                return publishRecursive(jsonstr,dTreeVO.getTreeId(),parentId);
            }
        }else{
            int count=dTreeDAO.update(dTreeVO);
            if(!CollectionUtils.isEmpty(dTreeVO.getTreeData())){
                Long treeId=dTreeVO.getTreeId();
                //失效上次保存关系
                int varRelation=dTreeVarDAO.updateVar(treeId);
                //失效上次保存条件
                int relation=dTreeCondDAO.updateCond(treeId);
                log.info("失效上一次保存条件数据[{}]条",varRelation);
                log.info("失效上一次保存条件数据[{}]条",relation);
                String jsonstr = JSON.toJSONString(dTreeVO.getTreeData() );
                return publishRecursive(jsonstr,treeId,parentId);
            }
        }
        return RespEntity.success();
    }

    @Override
    public DTreeVO getTreeById(Long treeId) {
        DTreeVO dTreeVO =dTreeDAO.queryById(treeId);
        int query=dTreeVarDAO.queryVarByTreeId(treeId);
        if(query==0){
            List<DTreeCond> list=new ArrayList<>();
            DTreeVar dTreeVar=new DTreeVar();
            dTreeVar.setCondType(1);
            dTreeVar.setChildren(list);
            List<DTreeVar> varlist =new ArrayList<>();
            varlist.add(dTreeVar);
            dTreeVO.setTreeData(varlist);
        }else if(query==1){
            DTreeVar dTreeVar=dTreeVarDAO.findByTreeId(treeId);
            if(null!=dTreeVar){
                makeVarPeriod(dTreeVar);
            }
            List<DTreeCond> list= dTreeCondDAO.queryCond(dTreeVar);
            if(CollectionUtils.isEmpty(list)){
                dTreeVar.setChildren(new ArrayList<>());
            }
            for(DTreeCond dTreeCond :list){
                Expression expression=ThresholdUtil.convert(dTreeCond.getCondJudge());
                dTreeCond.setLeft(expression.getLeft());
                dTreeCond.setLeftExpr(expression.getLeftExpr());
                dTreeCond.setRightExpr(expression.getRightExpr());
                dTreeCond.setRight(expression.getRight());
                if(StringUtils.isEmpty(dTreeCond.getOutValue())){
                    dTreeCond.setOutValue(null);
                    dTreeCond.setChildren(new ArrayList<>());

                }else{
                    dTreeCond.setChildren(getListByCond(dTreeCond));
                    //清除父outValue
                    dTreeCond.setOutValue(null);
                }
            }
            dTreeVar.setChildren(list);
            List<DTreeVar> varlist =new ArrayList<>();
            varlist.add(dTreeVar);
            dTreeVO.setTreeData(varlist);
        }else{
            DTreeCond dTreeCond=new DTreeCond();
            dTreeCond.setTreeId(dTreeVO.getTreeId());
            dTreeCond.setId(0L);
            dTreeVO.setTreeData(getVarListByCond(dTreeCond));
        }
        return dTreeVO;
    }
    public List<DTreeVar> getVarListByCond(DTreeCond treeCond){
        List<DTreeVar> dTreeVars = queryChildrenVar(treeCond);
        for (DTreeVar dTreeVar : dTreeVars) {
            dTreeVar.setChildren(getCondListByVar(dTreeVar));
            makeVarPeriod(dTreeVar);
        }
        return dTreeVars;
    }

    public void makeVarPeriod(DTreeVar dTreeVar){
        List<VariablePeriod> periodList = variablePeriodDAO.queryListByVarPeriodAndPeriodUnit(dTreeVar.getVarPeriod(),dTreeVar.getPeriodUnit());
        if(!CollectionUtils.isEmpty(periodList)){
            VariablePeriod period = periodList.iterator().next();
            dTreeVar.setPeriodId(period.getId().intValue());
            dTreeVar.setPeriodCnt(period.getContent());
            dTreeVar.setVarPeriod(period.getPeriod());
            dTreeVar.setPeriodUnit(period.getPeriodUnit());
        }
    }


    public List<DTreeVar> getListByCond(DTreeCond dTreeCond){
        List<DTreeVar> dTreeVars = dTreeCondDAO.queryCondOut(dTreeCond);
        List<DTreeVar>  dTreeVars1=new ArrayList<>();
        for (DTreeVar dTreeVar : dTreeVars) {
            dTreeVar.setType("text");
            dTreeVars1.add(dTreeVar);
            makeVarPeriod(dTreeVar);
        }
        return dTreeVars1;
    }

    public List<DTreeCond> getCondListByVar(DTreeVar treeVar){
        List<DTreeCond> dTreeConds = dTreeCondDAO.queryCond(treeVar);
        for (DTreeCond dTreeCond : dTreeConds) {
            Expression expression=ThresholdUtil.convert(dTreeCond.getCondJudge());
            dTreeCond.setLeft(expression.getLeft());
            dTreeCond.setLeftExpr(expression.getLeftExpr());
            dTreeCond.setRightExpr(expression.getRightExpr());
            dTreeCond.setRight(expression.getRight());
            if(StringUtils.isEmpty(dTreeCond.getOutValue())){
                dTreeCond.setOutValue(null);
                if(dTreeCond.getNextVarId()!=null){
                    dTreeCond.setChildren(queryChildrenVarList(dTreeCond.getNextVarId()));
                }

            }else{
                dTreeCond.setChildren(getListByCond(dTreeCond));
                //清除父outValue
                dTreeCond.setOutValue(null);
            }


        }
        return dTreeConds;
    }
    public List<DTreeVar> queryChildrenVarList(Long nextId){
       DTreeVar dTreeVar =dTreeVarDAO.queryById(nextId);
        makeVarPeriod(dTreeVar);
        dTreeVar.setChildren(getCondListByVar(dTreeVar));
        List<DTreeVar> res = new ArrayList<>();
        res.add(dTreeVar);
        return res;
    }
    public List<DTreeVar> queryChildrenVar(DTreeCond treeCond){
        List<DTreeCond> condList = dTreeCondDAO.queryListByCond(treeCond);
        if(CollectionUtils.isEmpty(condList)){
            return new ArrayList<>();
        }
        Long varId = condList.get(0).getVarId();
        DTreeVar dTreeVar = dTreeVarDAO.queryById(varId);
        if(null!=dTreeVar){
            makeVarPeriod(dTreeVar);
        }
        List<DTreeVar> res = new ArrayList<>();
        res.add(dTreeVar);
        return res;
    }
    @Override
    public List<DTree> getTreeList(Long projectId,String apiCode) {
        DTreeVO dTreeVO=new DTreeVO();
        dTreeVO.setProjectId(projectId);
        dTreeVO.setApiCode(apiCode);
        return dTreeDAO.queryTreeList(dTreeVO);
    }

    @Override
    public boolean existTreeName(String treeName,Long projectId) {
        int count = dTreeDAO.existTreeName(treeName,projectId);
        return count != 0;
    }

    @Override
    public Double getMaxVersionByTreeName(String treeName,Long projectId) {
        return dTreeDAO.queryMaxVersionByTreeName(treeName,projectId);
    }

    @Override
    public List<DTree> getVersionListByTreeName(Long projectId,String TreeName) {
        DTreeVO dTreeVO=new DTreeVO();
        dTreeVO.setProjectId(projectId);
        dTreeVO.setTreeName(TreeName);
        return dTreeDAO.queryVersionListByTreeName(dTreeVO);
    }
    @Override
    public List<JSONObject> queryTreeConfig(DNodeConfigVO nodeConfigVO) {
        DNodeModel dNodeModel=new DNodeModel();
        dNodeModel.setNodeId(nodeConfigVO.getNodeId());
        dNodeModel.setApiCode(Long.parseLong(nodeConfigVO.getApiCode()));
        dNodeModel.setModelType(Constants.DECISION_TREE);
        List<com.biz.credit.vo.DTreeVO> treeList = dTreeDAO.findTreeConfig(dNodeModel);
        List<JSONObject> tList = new ArrayList<>();
        treeList.forEach(dTreeVO -> {
            JSONObject jsonObject = new JSONObject();
            if(!org.springframework.util.StringUtils.isEmpty(dTreeVO.getJudge())){
                DNodeThreshold dNodeThreshold=new DNodeThreshold();
                List<DNodeThreshold> nodeThresholds=new ArrayList<>();
                dNodeThreshold.setType(dTreeVO.getType());
                if(dTreeVO.getReturnType()==2){
                    dNodeThreshold.setJudge(dTreeVO.getJudge());
                    nodeThresholds.add(dNodeThreshold);
                }else{
                    String Judge=dTreeVO.getJudge().substring(2);
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
            jsonObject.put("treeId",dTreeVO.getTreeId());
            jsonObject.put("name", dTreeVO.getProjectName());
            jsonObject.put("returnType", dTreeVO.getReturnType());
            jsonObject.put("treeVersion", dTreeVO.getTreeVersion());
            jsonObject.put("treeDesc", dTreeVO.getTreeDesc());
            jsonObject.put("returnField", dTreeVO.getReturnField());
            jsonObject.put("treeName", dTreeVO.getTreeName());
            jsonObject.put("userId", dTreeVO.getUserId());
            jsonObject.put("choose", dTreeVO.isChoose());
            tList.add(jsonObject);
        });
        return tList;
    }

    @Override
    @Transactional
    public RespEntity deleteTreeByTreeId(Long treeId) {
        try{
            //删除树
            int tree=dTreeDAO.deleteTreeByTreeId(treeId);
             //失效上次保存关系
             //int varRelation=dTreeVarDAO.updateVar(treeId);
             //失效上次保存条件
            // int relation=dTreeCondDAO.updateCond(treeId);
         }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("删除决策树失败");
        }
        return RespEntity.success();
    }

    @Override
    public List<DTree> getListByProjectId(Long projectId) {
        return dTreeDAO.queryListByProjectId(projectId);
    }

    @Override
    @Transactional
    public RespEntity saveTreeConfig(Long nodeId,DNodeConfig nodeTreeConfig) {
        if(nodeTreeConfig ==null){
            return RespEntity.error().setMsg("nodeTreeConfig为空");
        }else{
            checkNodeConfigParams(nodeTreeConfig);
            DNodeModel dNodeModel=new DNodeModel();
            dNodeModel.setModelType(Constants.DECISION_TREE);
            dNodeModel.setNodeId(nodeId);
            dNodeModel.setModelCode(nodeTreeConfig.getTreeId());
            dNodeModel.setStatus(Constants.COMMON_STATUS_VALID);
            //查询
            DNodeModel nodeModeld = dNodeModelDAO.queryNodeModel(dNodeModel);
            if(nodeModeld==null){
                dNodeModelDAO.insert(dNodeModel);
                DNodeThreshold dNodeThreshold=new DNodeThreshold();
                dNodeThreshold.setModelId(dNodeModel.getModelId());
                dNodeThreshold.setJudge(nodeTreeConfig.getJudge());
                dNodeThreshold.setType(nodeTreeConfig.getType());
                dNodeThresholdDAO.insert(dNodeThreshold);
            }else{
                //失效
                dNodeModelDAO.updateStatusByNodeIdAndType(nodeId,Constants.DECISION_TREE,Constants.COMMON_STATUS_INVALID);
                dNodeModelDAO.insert(dNodeModel);
                DNodeThreshold dNodeThreshold=new DNodeThreshold();
                dNodeThreshold.setModelId(dNodeModel.getModelId());
                dNodeThreshold.setJudge(nodeTreeConfig.getJudge());
                dNodeThreshold.setType(nodeTreeConfig.getType());
                dNodeThresholdDAO.insert(dNodeThreshold);
            }
            return RespEntity.success();
        }

    }

    @Override
    public List<DNodeParam> queryTreeApiList(Long treeId) {
        return dTreeVarDAO.queryByTreeId(treeId);
    }

    private  RespEntity  saveRecursive(String jsonStr, Long treeId, Long parentId){
        JSONArray jsonary = JSONArray.parseArray(jsonStr);
        for(int i=0;i<jsonary.size();i++){
            JSONObject treemap = jsonary.getJSONObject(i);
            Integer srcVarId=null;
            Long varId=null;
            if(!"".equals(treemap.get("outValue"))&& treemap.get("outValue")!=null){
                DTreeCond dcond=new DTreeCond();
                dcond.setOutValue(treemap.get("outValue").toString());
                dcond.setId(parentId);
                dTreeCondDAO.update(dcond);
            }else{
                if(!NumberUtils.isNumber(treemap.getString("srcVarId"))){
                    log.error("变量srcVarId不能为空");
                    throw new RuntimeException("变量不能为空");
                }
                srcVarId =treemap.getInteger("srcVarId");
                DTreeVar dTreeVar=new DTreeVar();
                dTreeVar.setTreeId(treeId);
                dTreeVar.setSrcVarId(srcVarId);
                dTreeVar.setStatus(Constants.TREEVAR_STATUS_VALID);
                if(NumberUtils.isNumber(treemap.getString("periodId"))){
                    List<VariablePeriod> periodList =  variablePeriodDAO.queryListByPeriodId(treemap.getInteger("periodId"));
                    if(!CollectionUtils.isEmpty(periodList)){
                        VariablePeriod variablePeriod = periodList.iterator().next();
                        dTreeVar.setPeriodUnit(variablePeriod.getPeriodUnit());
                        dTreeVar.setVarPeriod(variablePeriod.getPeriod());
                        dTreeVar.setPeriodCnt(variablePeriod.getContent());
                        dTreeVar.setPeriodId(variablePeriod.getId().intValue());
                    }
                }
/*
                dTreeVar.setVarPeriod(NumberUtils.isNumber(treemap.getString("varPeriod"))?treemap.getInteger("varPeriod"):null);
                dTreeVar.setPeriodUnit(StringUtils.isNotEmpty(treemap.getString("periodUnit"))?treemap.getString("periodUnit"):StringUtils.EMPTY);*/
                if(StringUtils.isEmpty(treemap.getString("condType"))){
                    log.error("条件类型condType不能为空");
                    throw new RuntimeException("条件类型不能为空");
                }
                dTreeVar.setCondType(treemap.getInteger("condType"));
                dTreeVarDAO.insertVar(dTreeVar);
                varId=dTreeVar.getVarId();
                DTreeCond dc=new DTreeCond();
                dc.setNextVarId(varId);
                dc.setId(parentId);
                dTreeCondDAO.updateNextId(dc);
            }
            if(null!=treemap.getJSONArray("children")){
                String treeVarList = treemap.getJSONArray("children").toJSONString();
                saveRecursive2(treeVarList,treeId,parentId,varId);
            }
        }
        return RespEntity.success();
    }
    private  void saveRecursive2(String twojsonstr,Long treeId,Long  parentId,Long varId){
        JSONArray jsonary = JSONArray.parseArray(twojsonstr);
        for(int i=0;i<jsonary.size();i++){
            JSONObject twomap = jsonary.getJSONObject(i);
            DTreeCond dTreeCond=new DTreeCond();
            dTreeCond.setTreeId(treeId);
            Expression expression=new Expression();
            if(StringUtils.isEmpty(twomap.getString("left"))){
                expression.setLeft(null);
                expression.setLeftExpr(null);
                expression.setRight(twomap.get("right").toString());
                expression.setRightExpr(twomap.getString("rightExpr"));
            }else if(StringUtils.isEmpty(twomap.getString("right"))){
                expression.setLeft(twomap.get("left").toString());
                expression.setLeftExpr(twomap.get("leftExpr").toString());
                expression.setRight(null);
                expression.setRightExpr(null);
            }else {
                expression.setLeft(twomap.get("left").toString());
                expression.setLeftExpr(twomap.get("leftExpr").toString());
                expression.setRight(twomap.get("right").toString());
                expression.setRightExpr(twomap.get("rightExpr").toString());
            }
            dTreeCond.setCondJudge(ThresholdUtil.convert(expression));
            dTreeCond.setCondOrder(Integer.parseInt(twomap.get("condOrder").toString()));
            dTreeCond.setVarId(varId);
            dTreeCond.setStatus(Constants.TREECOND_STATUS_VALID);
            if(StringUtils.isEmpty(twomap.getString("outValue"))){
                dTreeCond.setOutValue(StringUtils.EMPTY);
            }
            dTreeCond.setParentId(parentId);
            dTreeCondDAO.insertCond(dTreeCond);
            if(null!=twomap.getJSONArray("children")){
                String threejsonstr = twomap.getJSONArray("children").toJSONString();
                saveRecursive(threejsonstr,treeId,dTreeCond.getId());
            }


        }
    }
    private  RespEntity  publishRecursive(String jsonStr,Long treeId,Long parentId){
        JSONArray jsonary = JSONArray.parseArray(jsonStr);
        int index=1;
        for(int i=0;i<jsonary.size();i++){
            JSONObject treemap = jsonary.getJSONObject(i);
            Integer srcVarId=null;
            Long varId=null;
            if(StringUtils.isNotEmpty(treemap.getString("outValue"))){
                DTreeCond dcond=new DTreeCond();
                dcond.setOutValue(treemap.getString("outValue"));
                dcond.setId(parentId);
                dTreeCondDAO.update(dcond);
            }else{
                srcVarId = treemap.getInteger("srcVarId");
                DTreeVar dTreeVar=new DTreeVar();
                dTreeVar.setTreeId(treeId);
                dTreeVar.setSrcVarId(srcVarId);
                dTreeVar.setStatus(Constants.TREEVAR_STATUS_VALID);
                dTreeVar.setCondType(treemap.getInteger("condType"));
                if(NumberUtils.isNumber(treemap.getString("periodId"))){
                    List<VariablePeriod> periodList =  variablePeriodDAO.queryListByPeriodId(treemap.getInteger("periodId"));
                    if(!CollectionUtils.isEmpty(periodList)){
                        VariablePeriod variablePeriod = periodList.iterator().next();
                        dTreeVar.setPeriodUnit(variablePeriod.getPeriodUnit());
                        dTreeVar.setVarPeriod(variablePeriod.getPeriod());
                        dTreeVar.setPeriodCnt(variablePeriod.getContent());
                        dTreeVar.setPeriodId(variablePeriod.getId().intValue());
                    }
                }
                dTreeVarDAO.insertVar(dTreeVar);
                varId=dTreeVar.getVarId();
                DTreeCond dc=new DTreeCond();
                dc.setNextVarId(varId);
                dc.setId(parentId);
                dTreeCondDAO.updateNextId(dc);
            }
            if(null != treemap.getJSONArray("children")){
                String treeVarList = treemap.getJSONArray("children").toJSONString();
                publishRecursive2(treeVarList,treeId,parentId,varId);
            }
        }
        return RespEntity.success();
    }
    private  void publishRecursive2(String twojsonstr,Long treeId,Long  parentId,Long varid){
        JSONArray jsonary = JSONArray.parseArray(twojsonstr);
        for(int i=0;i<jsonary.size();i++){
            JSONObject twomap = jsonary.getJSONObject(i);
            DTreeCond dTreeCond=new DTreeCond();
            dTreeCond.setTreeId(treeId);
            Expression expression=new Expression();
            if(StringUtils.isEmpty(twomap.getString("left"))){
                expression.setLeft(null);
                expression.setLeftExpr(null);
                expression.setRight(twomap.get("right").toString());
                expression.setRightExpr(twomap.get("rightExpr").toString());
            }else if(StringUtils.isEmpty(twomap.getString("right"))){
                expression.setLeft(twomap.getString("left"));
                expression.setLeftExpr(twomap.getString("leftExpr"));
                expression.setRight(null);
                expression.setRightExpr(null);
            }else {
                expression.setLeft(twomap.getString("left"));
                expression.setLeftExpr(twomap.getString("leftExpr"));
                expression.setRight(twomap.getString("right"));
                expression.setRightExpr(twomap.getString("rightExpr"));
            }
            dTreeCond.setCondJudge(ThresholdUtil.convert(expression));
            dTreeCond.setCondOrder(twomap.getInteger("condOrder"));
            dTreeCond.setVarId(varid);
            dTreeCond.setStatus(Constants.TREECOND_STATUS_VALID);
            if(StringUtils.isEmpty(twomap.getString("outValue"))){
                dTreeCond.setOutValue(StringUtils.EMPTY);
            }
            dTreeCond.setParentId(parentId);
            dTreeCondDAO.insertCond(dTreeCond);
            if(null != twomap.getJSONArray("children")){
                String threejsonstr = twomap.getJSONArray("children").toJSONString();
                publishRecursive(threejsonstr,treeId,dTreeCond.getId());
            }
        }
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

    private RespEntity checkParams(DTreeVO dTreeVO) {
        if (StringUtils.isEmpty(dTreeVO.getTreeName())) {
            return RespEntity.error().setMsg("决策树名称不能为空");
        }
        if (null==dTreeVO.getProjectId()) {
            return RespEntity.error().setMsg("决策树项目Id不能为空");
        }
        if (null==dTreeVO.getVersionType()) {
            return RespEntity.error().setMsg("决策树versionType不能为空");
        }
        return RespEntity.success();
    }
}
