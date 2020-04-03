package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.*;
import com.biz.credit.domain.*;
import com.biz.credit.service.*;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.biz.credit.utils.Constants.ENTERPRISE_RULE;

@Slf4j
@Service
public class DNodeServiceImpl implements IDNodeService {

    @Autowired
    private IDNodeRuleService nodeRuleService;

    @Autowired
    private IDNodeApiService nodeApiService;

    @Autowired
    private IDAntiFraudService antiFraudService;

    @Autowired
    private DNodeDAO nodeDAO;

    @Autowired
    private IDNodeModelService nodeModelService;

    @Autowired
    private ITreeService iTreeService;
    @Autowired
    private ICreditModelService iCreditModelService;
    @Autowired
    private ITableService iTableService;
    @Autowired
    private IScoreCardService iScoreCardService;
    @Autowired
    private DRuleDAO dRuleDAO;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DAntiFraudDAO dAntiFraudDAO;
    @Autowired
    private CreditModelDAO creditModelDAO;

    @Value("${biz.model.flow.node-rule-key}")
    private String DNODE_RULE_KEY;


    @Override
    @Transactional
    public RespEntity saveNode(DNodeVO nodeVO) {
        boolean isNew = false;
        if(nodeVO.getNodeId() == null){
            isNew = true;
        }
        RespEntity checkNodeResp = checkNodeParams(nodeVO,isNew);
        if(!checkNodeResp.isSuccess()){
            return checkNodeResp;
        }
        if(isNew){
            nodeVO.setStatus(Constants.COMMON_STATUS_VALID);
            nodeDAO.insertNodeBasic(nodeVO);
        }else{
            nodeDAO.updateNodeBasic(nodeVO);
        }
        if(!nodeVO.getModified()){
            //没有没修改
            return RespEntity.success();
        }
        FlowParamConfigVO flowParamConfigVO=new FlowParamConfigVO();
        flowParamConfigVO.setIndustryInfo(new IndustryInfoVO());
        flowParamConfigVO.setScoreCardInfo(new ScoreCardVO());
        flowParamConfigVO.setTreeInfo(new DTree());
        flowParamConfigVO.setTableInfo(new DTable());

        flowParamConfigVO.setNodeApiList(nodeVO.getNodeApiList());
        RespEntity nodeApiRespEntity = nodeApiService.updateNodeApi(nodeVO.getNodeId(), nodeVO.getNodeApiList());
        if(!nodeApiRespEntity.isSuccess()){
            throw new RuntimeException("保存节点api异常:" + nodeApiRespEntity.getMsg());
        }

        //失效数据
        nodeModelService.invalidByNodeId(nodeVO.getNodeId());
        if(nodeVO.getChooseTabList().contains(ENTERPRISE_RULE)){
            List<DNodeRuleVO> nodeRuleVOList =nodeVO.getCompanyNodeRuleConfig().getNodeRuleList();
            List<DNodeRuleVO> companyNodeRuleList=new ArrayList<>();
            for(DNodeRuleVO nodeRuleVO:nodeRuleVOList){
                if(nodeRuleVO.getChoose()){
                    companyNodeRuleList.add(nodeRuleVO);
                }
            }
            flowParamConfigVO.setCompanyRuleList(companyNodeRuleList);
            RespEntity respEntity = nodeRuleService.saveCompanyNodeRule(nodeVO.getNodeId(), nodeVO.getCompanyNodeRuleConfig(), nodeVO.getUserId(), isNew);
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点企业规则数据异常:" + respEntity.getMsg());
            }
        }
        if(nodeVO.getChooseTabList().contains(Constants.NATURAL_PERSON_RULE)){
            List<DNodeRuleVO> nodeRuleVOList =nodeVO.getPersonalNodeRuleConfig().getNodeRuleList();
            List<DNodeRuleVO> personalNodeRuleList=new ArrayList<>();
            for(DNodeRuleVO nodeRuleVO:nodeRuleVOList){
                if(nodeRuleVO.getChoose()){
                    personalNodeRuleList.add(nodeRuleVO);
                }
            }
            flowParamConfigVO.setPerRuleList(personalNodeRuleList);
            RespEntity respEntity = nodeRuleService.savePersonalNodeRule(nodeVO.getNodeId(), nodeVO.getPersonalNodeRuleConfig(), nodeVO.getUserId(), isNew);
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点自然人规则数据异常:" + respEntity.getMsg());
            }
        }
        if(nodeVO.getChooseTabList().contains(Constants.ANTI_FRAUD_SCORE)){
            flowParamConfigVO.setAntiFraudInfo(nodeVO.getNodeAntiFraudConfig().getAntiFraud().get(0));
            RespEntity respEntity = antiFraudService.saveAntiFraudConfig(nodeVO.getNodeId(), nodeVO.getNodeAntiFraudConfig());
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点反欺诈模型数据异常:" + respEntity.getMsg());
            }
        }
        if(nodeVO.getChooseTabList().contains(Constants.CREDIT_SCORE_MODEL)){
            //评分模型
            flowParamConfigVO.setIndustryInfo(nodeVO.getNodeCreditModelConfig().getCreditModelData().get(0));
            RespEntity respEntity = iCreditModelService.saveCreditModelConfig(nodeVO.getNodeId(), nodeVO.getNodeCreditModelConfig(), isNew);
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点信用评分模型数据异常:" + respEntity.getMsg());
            }
        }
        if(nodeVO.getChooseTabList().contains(Constants.SCORE_CARD)){
            //评分卡
            ScoreCardVO scoreCardVO=new ScoreCardVO();
            scoreCardVO.setScoreCardId(nodeVO.getNodeScoreCardConfig().getScoreCardId());
            flowParamConfigVO.setScoreCardInfo(scoreCardVO);
            RespEntity respEntity = iScoreCardService.saveScoreCardConfig(nodeVO.getNodeId(),nodeVO.getNodeScoreCardConfig());
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点评分卡数据异常:" + respEntity.getMsg());
            }
        }
        if(nodeVO.getChooseTabList().contains(Constants.DECISION_TREE)){
            //决策树
            DTree dTree=new DTree();
            dTree.setTreeId(nodeVO.getNodeTreeConfig().getTreeId());
            flowParamConfigVO.setTreeInfo(dTree);
            RespEntity respEntity = iTreeService.saveTreeConfig(nodeVO.getNodeId(),nodeVO.getNodeTreeConfig());
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点决策树数据异常:" + respEntity.getMsg());
            }
        }
        if(nodeVO.getChooseTabList().contains(Constants.DECISION_TABLE)){
            //决策表
            DTable dTable=new DTable();
            dTable.setTableId(nodeVO.getNodeTableConfig().getTableId());
            flowParamConfigVO.setTableInfo(dTable);
            RespEntity respEntity = iTableService.saveTableConfig(nodeVO.getNodeId(),nodeVO.getNodeTableConfig());
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点决策表数据异常:" + respEntity.getMsg());
            }
        }
        Set<DNodeParam> paramsList=queryNodePrarams(flowParamConfigVO);
        log.info("保存节点参数开始：nodeId=[{}]",nodeVO.getNodeId());
        if(!CollectionUtils.isEmpty(paramsList)){
            paramsList.forEach(param->{
                log.info("param:"+JSONObject.toJSONString(param));
            });
        }
        log.info("保存节点参数结束：");
        List<DNodeParam> paramsList1=new ArrayList<>();
        paramsList1.addAll(paramsList);
        if(CollectionUtils.isEmpty(nodeVO.getNodeParamList())){
            //节点参数
           RespEntity respEntity = saveInPrarams(nodeVO.getNodeId(),paramsList1);
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点参数数据异常:" + respEntity.getMsg());
            }
        }else{
            /*if(CollectionUtils.isEmpty(nodeVO.getChooseTabList())){
                //节点参数
                RespEntity respEntity = saveInPrarams(nodeVO.getNodeId(),nodeVO.getNodeParamList());
                if(!respEntity.isSuccess()){
                    throw new RuntimeException("保存节点参数数据异常:" + respEntity.getMsg());
                }
            }else {
                List<DNodeParam> paramsList2=nodeVO.getNodeParamList();
                List<DNodeParam> paramsLists=list(paramsList1,paramsList2);
                RespEntity respEntity = saveInPrarams(nodeVO.getNodeId(),paramsLists);
                if(!respEntity.isSuccess()){
                    throw new RuntimeException("保存节点参数数据异常:" + respEntity.getMsg());
                }
            }*/

            List<DNodeParam> paramsList2=nodeVO.getNodeParamList();
            List<DNodeParam> paramsLists=MergeToHeavyList(paramsList1,paramsList2);
            RespEntity respEntity = saveInPrarams(nodeVO.getNodeId(),paramsLists);
            if(!respEntity.isSuccess()){
                throw new RuntimeException("保存节点参数数据异常:" + respEntity.getMsg());
            }
        }


        return RespEntity.success().setData(nodeVO);
    }
    private List<DNodeParam> MergeToHeavyList(List<DNodeParam> paramsList1,List<DNodeParam> paramsList2){
        if(CollectionUtils.isEmpty(paramsList1)){
            return paramsList2;
        }
        List<DNodeParam> dNodeParamList=new ArrayList<>();
        for(DNodeParam dNodeParam: paramsList1){
            for(DNodeParam dparam1 :paramsList2){
                if(dNodeParam.getName().equals(dparam1.getName())){
                    if(dparam1.getIsRequired()==true){
                        dNodeParam.setRequired(1);
                        dNodeParam.setIsRequired(true);
                    }else {
                        dNodeParam.setRequired(0);
                        dNodeParam.setIsRequired(false);
                    }

                }
            }
            dNodeParamList.add(dNodeParam);
        }
        return dNodeParamList;

    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void invalidByFlowId(Long flowId) {
        nodeDAO.updateStatusByFlowId(flowId,Constants.COMMON_STATUS_INVALID);
    }

    @Override
    public List<DNodeVO> getListByFlowId(Long flowId) {
        return nodeDAO.queryListByFlowId(flowId);
    }

    @Override
    public void invalidById(Long nodeId) {
        nodeDAO.updateStatusById(nodeId,Constants.COMMON_STATUS_INVALID);
    }


    private RespEntity checkNodeParams(DNodeVO nodeVO,boolean isNew) {
        if(StringUtils.isEmpty(nodeVO.getNodeName())){
            return RespEntity.error().setMsg("节点名称不能为空");
        }
        if(nodeVO.getExecNextNode() == null){
            return RespEntity.error().setMsg("是否执行下一节点不能为空");
        }
        if(nodeVO.getFlowId() == null){
            return RespEntity.error().setMsg("所属决策流不能为空");
        }
        if(nodeVO.getNodeOrder() == null){
            return RespEntity.error().setMsg("节点排序不能为空");
        }
        //校验节点名称是否重复
        int count = nodeDAO.queryCount(nodeVO);
        if(count  > 0){
            return RespEntity.error().setMsg("节点名称:#nodeName已存在，请更换".replace("#nodeName",nodeVO.getNodeName()));
        }
        return RespEntity.success();
    }
    @Override
    public List<DNodeParam> queryInPararms(Long nodeId) {
        List<DNodeParam> paramList=new ArrayList<>();
        List<DNodeParam> nodeParamList= nodeDAO.queryInPararms(nodeId);
        nodeParamList.forEach(dNodeParam ->{
            if(dNodeParam.getRequired()==1){
                dNodeParam.setIsRequired(true);
            }else{
                dNodeParam.setIsRequired(false);
            }
            paramList.add(dNodeParam);
        });
        return paramList;
    }
    @Override
    public Set<DNodeParam> queryNodePrarams(FlowParamConfigVO flowParamConfigVO) {
        Set<DNodeParam> dParamsList=new HashSet<DNodeParam>();
        List<DNodeParam> paramslist = new ArrayList<>();
        if(!CollectionUtils.isEmpty(flowParamConfigVO.getNodeApiList())) {
            //数据源api
            List<DNodeApiVO> nodeApiList1 = flowParamConfigVO.getNodeApiList();
            for (DNodeApi dNodeApi : nodeApiList1) {
                List<DNodeParam> nodeApiParamList = nodeDAO.queryParamsList(dNodeApi.getApiProdCode(), dNodeApi.getApiVersion());
                paramslist.addAll(nodeApiParamList);
            }
        }
        if(!CollectionUtils.isEmpty(flowParamConfigVO.getCompanyRuleList())){
            //企业规则
            List<DNodeRuleVO> ruleApiList=flowParamConfigVO.getCompanyRuleList();
            List<DNodeParam> nodeApiParamList = dRuleDAO.queryByRuleId(ruleApiList);
            paramslist.addAll(nodeApiParamList);

        }
        if(!CollectionUtils.isEmpty(flowParamConfigVO.getPerRuleList())) {
            //自然人规则
            List<DNodeRuleVO> ruleVarApiList = flowParamConfigVO.getPerRuleList();
            List<DNodeParam> nodeApiParamList = dRuleDAO.queryByRuleId(ruleVarApiList);
            paramslist.addAll(nodeApiParamList);
        }
        if(!StringUtils.isEmpty(flowParamConfigVO.getAntiFraudInfo())) {
            //反欺诈
            DAntiFraud dAntiFraud = flowParamConfigVO.getAntiFraudInfo();
            List<DNodeParam> dAntiFraudParamsList = dAntiFraudDAO.queryParamsAntiFraud(dAntiFraud.getId());
            paramslist.addAll(dAntiFraudParamsList);
        }
        if(!StringUtils.isEmpty(flowParamConfigVO.getIndustryInfo().getIndustryId())) {
            //信用评分模型
            List<DNodeParam> creditModelParamList = creditModelDAO.queryIndustryInfoApi(flowParamConfigVO.getIndustryInfo().getIndustryId());
            paramslist.addAll(creditModelParamList);
        }
        if(!StringUtils.isEmpty(flowParamConfigVO.getScoreCardInfo().getScoreCardId())) {
            //评分卡
            List<DNodeParam> scoreCardParamList = iScoreCardService.queryScoreCardApiList(flowParamConfigVO.getScoreCardInfo().getScoreCardId());
            paramslist.addAll(scoreCardParamList);
        }
        if(!StringUtils.isEmpty(flowParamConfigVO.getTableInfo().getTableId())) {
            //决策表
            List<DNodeParam> tableParamList = iTableService.queryTableApiList(flowParamConfigVO.getTableInfo().getTableId());
            paramslist.addAll(tableParamList);
        }
        if(!StringUtils.isEmpty(flowParamConfigVO.getTreeInfo().getTreeId())) {
            //决策树
            List<DNodeParam> treeParamList = iTreeService.queryTreeApiList(flowParamConfigVO.getTreeInfo().getTreeId());
            paramslist.addAll(treeParamList);
        }
        if(!CollectionUtils.isEmpty(paramslist)){
            for (DNodeParam dParams : paramslist) {
                DNodeParam tu  = new DNodeParam();
                tu.setCode(dParams.getCode());
                tu.setName(dParams.getName());
                tu.setRequired(dParams.getRequired());
                tu.setChoose(true);
                if(dParams.getRequired()==1){
                    tu.setIsRequired(true);
                }else{
                    tu.setIsRequired(false);
                }
                tu.setIsChoose(1);

                for(DNodeParam dNodeParam: paramslist){
                    if(dParams.getName().equals(dNodeParam.getName())){
                        if(dNodeParam.getRequired()==1){
                            tu.setRequired(1);
                        }
                    }
                }
                StringBuffer sb = new StringBuffer();
                for(DNodeParam dNodeParam: paramslist){
                    if(dParams.getName().equals(dNodeParam.getName())){
                        if(!(sb.toString().length() <= 0)){
                            sb.append(",");
                        }
                        sb.append(dNodeParam.getFields());
                    }
                }
                tu.setFields(excludeRepetition(sb.toString()));
                dParamsList.add(tu);
            }

        }else{
           return dParamsList;
        }

        return dParamsList;
    }
    public String  excludeRepetition(String str){
        String[] arr = str.split(",");
        List<String> list = new ArrayList<>();
        for(int i = 0; i < arr.length; i++){
            String s = arr[i].trim();
            if(!list.contains(s)){
                list.add(s);
            }
        }
        StringBuffer sb = new StringBuffer();
        for(String s : list){
            if(!(sb.toString().length() <= 0)){
                sb.append(",");
            }
            sb.append(s);
        }
        return sb.toString();
    }
    @Override
    @Transactional
    public RespEntity deleteNode(Long nodeId) {
        if(nodeId == null){
            return RespEntity.error().setMsg("节点id不能为空");
        }
        DNode node = nodeDAO.queryById(nodeId);
        if(node == null){
            return RespEntity.error().setMsg("节点不存在");
        }
        nodeDAO.updateStatusById(nodeId,Constants.COMMON_STATUS_INVALID);
        List<DNodeVO> nodeList = nodeDAO.queryListByFlowId(node.getFlowId());
        for(int i = 0 ; i < nodeList.size() ; i ++){
            nodeList.get(i).setNodeOrder((long)i);
            nodeDAO.updateNodeBasic(nodeList.get(i));
        }
        return RespEntity.success().setMsg("删除成功");
    }

    @Override
    public JSONObject getNodeDetail(Long nodeId, String userId, String apiCode) {
        //规则配置
        DNodeRuleVO nodeRuleVO = new DNodeRuleVO();
        nodeRuleVO.setModelType(Constants.ENTERPRISE_RULE);
        nodeRuleVO.setNodeId(nodeId);
        nodeRuleVO.setApiCode(apiCode);
        nodeRuleVO.setRuleType(Constants.ENTERPRISE_RULE);
        long start =System.currentTimeMillis();
        NodeRuleConfig companyNodeRuleConfig = nodeRuleService.getNodeRuleConfig(nodeRuleVO);
        log.info("companyNodeRuleConfig  cost:"+(System.currentTimeMillis()-start)+"ms");
        nodeRuleVO.setModelType(Constants.NATURAL_PERSON_RULE);
        nodeRuleVO.setRuleType(Constants.NATURAL_PERSON_RULE);
        start =System.currentTimeMillis();
        NodeRuleConfig personalNodeRuleConfig = nodeRuleService.getNodeRuleConfig(nodeRuleVO);
        log.info("personalNodeRuleConfig cost:"+(System.currentTimeMillis()-start)+"ms");
        //反欺诈评分
        DAntiFraudVO antiFraudVO = new DAntiFraudVO();
        antiFraudVO.setNodeId(nodeId);
        antiFraudVO.setApiCode(apiCode);
        start =System.currentTimeMillis();
        DNodeAntiFraudConfig antiFraudConfig = antiFraudService.getAntiFraudConfig(antiFraudVO);
        log.info("antiFraudConfig cost:"+(System.currentTimeMillis()-start)+"ms");
        //信用评分模型
        DNodeConfigVO nodeConfigVO=new DNodeConfigVO();
        nodeConfigVO.setApiCode(apiCode);
        nodeConfigVO.setNodeId(nodeId);
        start =System.currentTimeMillis();
        List<JSONObject> queryCreditModelConfig = iCreditModelService.queryCreditModelConfig(nodeConfigVO);
        log.info("queryCreditModelConfig cost:"+(System.currentTimeMillis()-start)+"ms");
        //评分卡
        start =System.currentTimeMillis();
        List<JSONObject> scoreCardConfig = iScoreCardService.queryScoreCardConfig(nodeConfigVO);
        log.info("scoreCardConfig cost:"+(System.currentTimeMillis()-start)+"ms");
        //决策表
        start =System.currentTimeMillis();
        List<JSONObject> queryTableConfig = iTableService.queryTableConfig(nodeConfigVO);
        log.info("queryTableConfig cost:"+(System.currentTimeMillis()-start)+"ms");
        //决策树
        start =System.currentTimeMillis();
        List<JSONObject> queryTreeConfig = iTreeService.queryTreeConfig(nodeConfigVO);
        log.info("queryTreeConfig cost:"+(System.currentTimeMillis()-start)+"ms");
        JSONObject res = new JSONObject();
        res.put("companyNodeRuleConfig",companyNodeRuleConfig);
        res.put("personalNodeRuleConfig",personalNodeRuleConfig);
        res.put("nodeAntiFraudConfig",antiFraudConfig);
        res.put("creditModelConfigList",queryCreditModelConfig);
        res.put("scoreCardConfigList",scoreCardConfig);
        res.put("tableConfigList",queryTableConfig);
        res.put("treeConfigList",queryTreeConfig);
        return res;
    }

    @Override
    public Set<DNodeParam> getPrarams(FlowParamConfigVO flowParamConfigVO) {
        Set<DNodeParam> paramSet =new HashSet<>();
        if(flowParamConfigVO.getNodeId()==null){
            return queryNodePrarams(flowParamConfigVO);
        }else {
            Set<DNodeParam> paramsList=queryNodePrarams(flowParamConfigVO);
            List<DNodeParam> paramsList1=new ArrayList<>();
            paramsList1.addAll(paramsList);
            List<DNodeParam> paramsList2=queryInPararms(flowParamConfigVO.getNodeId());
            List<DNodeParam> paramsLists=MergeToHeavyList(paramsList1,paramsList2);
            paramSet.addAll(paramsLists);
           return  paramSet;
        }
    }

    @Override
    @Transactional
    public RespEntity saveInPrarams(Long nodeId,List<DNodeParam> dNodeParamList) {
        if(dNodeParamList==null || nodeId == null){
            return RespEntity.error().setMsg("请求参数为空");
        }
        try{
            //失效节点参数
            nodeDAO.updateParam(nodeId);
            log.info("节点参数入库开始：nodeId=[{}]",nodeId);
            dNodeParamList.forEach(dNodeParam->{
                if(dNodeParam.getIsRequired()!=null){
                    if(dNodeParam.getIsRequired()==true){
                        dNodeParam.setRequired(1);
                    }else{
                        dNodeParam.setRequired(0);
                    }
                }
                if(dNodeParam.getChoose()==true){
                    dNodeParam.setIsChoose(1);
                }else{
                    dNodeParam.setIsChoose(0);
                }
                dNodeParam.setNodeId(nodeId);
                dNodeParam.setStatus(Constants.COMMON_STATUS_VALID);
                nodeDAO.insertInParams(dNodeParam);
                log.info("dNodeParam:"+JSONObject.toJSONString(dNodeParam));
            });
            log.info("节点参数入库结束：nodeId=[{}]，成功入库[{}]条",nodeId,dNodeParamList.size());
        }catch (Exception e){
            throw new RuntimeException("保存节点参数数据异常:" + e.getMessage());
        }
        return RespEntity.success();
    }

}
