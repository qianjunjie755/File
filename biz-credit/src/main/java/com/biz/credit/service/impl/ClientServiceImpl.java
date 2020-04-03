package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.*;
import com.biz.credit.domain.CrmApi;
import com.biz.credit.domain.IndustryInfoApi;
import com.biz.credit.domain.ModuleTypeApi;
import com.biz.credit.service.IClientService;
import com.biz.credit.service.ITaskService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.CrmApiVO;
import com.biz.credit.vo.IndustryInfoVO;
import com.biz.credit.vo.ModuleTypeVO;
import com.biz.credit.vo.StrategyVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class ClientServiceImpl implements IClientService {
    @Autowired
    private ModuleTypeApiDAO moduleTypeApiDAO;
    @Autowired
    private ModuleTypeDAO moduleTypeDAO;
    @Autowired
    private IndustryInfoDAO industryInfoDAO;
    @Autowired
    private RuleTemplateDAO ruleTemplateDAO;
    @Autowired
    private ModuleTypeRuleDAO moduleTypeRuleDAO;
    @Autowired
    private RuleVariableDAO ruleVariableDAO;
    @Autowired
    private IndustryInfoApiDAO industryInfoApiDAO;
    @Autowired
    private ClientDAO clientDAO;
    @Autowired
    private CrmApiDAO crmApiDAO;
    @Autowired
    private ITaskService taskService;
    private String modelReqUrl = "http://MODEL-SERVICE/model/apiModel";
    @Autowired
    private RestTemplate restTemplate;

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 3600, rollbackFor = {RuntimeException.class, Exception.class})
    @Override
    public JSONObject updateClient(String apiCode, JSONArray productList) throws Exception {
        JSONObject jsonObject = new JSONObject();
        if(null!=productList){
            for(int i=0;i<productList.size();i++){
                JSONObject product = productList.getJSONObject(0);
                String prodCode = product.getString("prodCode");
                String prodName = product.getString("prodName");
                Double version = product.getDouble("version");
                String validEnd = product.getString("endDate");
                CrmApi crmApi = crmApiDAO.findCrmApi(prodCode,version);
                log.info("crmApi:"+crmApi);
                if(null!=crmApi){
                    CrmApi crmApiQuery = new CrmApi();
                    crmApiQuery.setId(crmApi.getId());
                    crmApiQuery.setApiCode(apiCode);
                    crmApiQuery.setReportType(crmApi.getReportType());
                    crmApiQuery.setIndustryId(crmApi.getIndustryId());
                    crmApiQuery.setFlowId(crmApi.getFlowId());
                    int crmApiCount  = crmApiDAO.findCrmApiByCrmApi(crmApiQuery);
                    log.info("crmApiClientCount:"+crmApiCount);
                    if(crmApiCount<=0){
                        Integer targetUserId = crmApiDAO.findUserIdByApiCode(apiCode);
                        if(null==crmApi.getFlowId()) {
                            crmApi.setApiCode(apiCode);
                            ModuleTypeVO moduleTypeQuery = new ModuleTypeVO();
                            moduleTypeQuery.setModuleTypeId(crmApi.getModuleTypeId());
                            ModuleTypeVO moduleTypeVO =  moduleTypeDAO.findModuleTypeById(moduleTypeQuery);
                            IndustryInfoVO industryInfoVO = industryInfoDAO.findIndustryInfoVOByIndustryId(crmApi.getIndustryId());
                            StrategyVO source1 = new StrategyVO();
                            source1.setApiCode("4000159");
                            source1.setUserId(1l);
                            source1.setStrategyId(moduleTypeVO.getStrategyId());
                            if(null!=targetUserId){
                                StrategyVO target1 = new StrategyVO();
                                target1.setApiCode(apiCode);
                                target1.setUserId(targetUserId.longValue());
                                target1.setIsTemplate(1l);
                                target1.setIndustryId(crmApi.getIndustryId());
                                taskService.copyStrategyTemplate(source1,target1);
                                StrategyVO target2 = new StrategyVO();
                                target2.setApiCode(apiCode);
                                target2.setUserId(targetUserId.longValue());
                                target2.setIsTemplate(0l);
                                target2.setIndustryId(crmApi.getIndustryId());
                                taskService.copyStrategyTemplate(target1,target2);
                                ModuleTypeVO clientModuleTypeVO = new ModuleTypeVO();
                                BeanUtils.copyProperties(moduleTypeVO,clientModuleTypeVO);
                                clientModuleTypeVO.setModuleTypeId(null);
                                clientModuleTypeVO.setDescription(apiCode);
                                clientModuleTypeVO.setStrategyId(target2.getStrategyId());
                                clientModuleTypeVO.setIsTemplate(0);
                                clientModuleTypeVO.setModuleTypeName(crmApi.getProdName());
                                clientModuleTypeVO.setProdCode(prodCode);
                                clientModuleTypeVO.setProdName(prodName);
                                clientModuleTypeVO.setReportType(crmApi.getReportType());
                                moduleTypeDAO.addModuleType(clientModuleTypeVO);
                                ModuleTypeApi moduleTypeApi = new ModuleTypeApi();
                                moduleTypeApi.setModuleTypeId(clientModuleTypeVO.getModuleTypeId());
                                moduleTypeApi.setApiCode(apiCode);
                                moduleTypeApi.setReportType(clientModuleTypeVO.getReportType());
                                moduleTypeApi.setStatus(Constants.MODULE_TYPE_API_STATUS_VALID);
                                moduleTypeApi.setValidEnd(validEnd);
                                moduleTypeApiDAO.addModuleTypeApi(moduleTypeApi);
                                IndustryInfoApi industryInfoApi = new IndustryInfoApi();
                                industryInfoApi.setIndustryId(target2.getIndustryId());
                                industryInfoApi.setModuleTypeId(clientModuleTypeVO.getModuleTypeId());
                                industryInfoApi.setApiCode(target2.getApiCode());
                                industryInfoApiDAO.addIndustryInfoApi(industryInfoApi);
                                HttpHeaders httpHeaders = new HttpHeaders();
                                httpHeaders.add("Content-Type","application/json;charset=utf-8");
                                JSONObject modelParam = new JSONObject();
                                modelParam.put("modelCode",industryInfoVO.getModelCode());
                                modelParam.put("version",industryInfoVO.getModelVersion());
                                modelParam.put("apiCode",apiCode);
                                JSONObject reqRetJson = restTemplate.postForEntity(modelReqUrl,new HttpEntity<>(modelParam,httpHeaders),JSONObject.class).getBody();
                                if(null!=reqRetJson)
                                    log.info("model_ret_json:"+reqRetJson.toJSONString());
                                jsonObject.put("strategy",target1);
                                crmApi.setModuleTypeId(clientModuleTypeVO.getModuleTypeId());
                                crmApiDAO.addCrmApiClient(crmApi);
                            }
                        }else {
                            /*if (null != targetUserId) {
                                crmApi.setApiCode(apiCode);
                                DFlowVO dFlowVO = clientDAO.queryFlowVOByFlowId(crmApi.getFlowId());
                                DFlowVO newFlowVO = new DFlowVO();
                                BeanUtils.copyProperties(dFlowVO, newFlowVO);
                                newFlowVO.setFlowId(null);
                                newFlowVO.setApiCode(apiCode);
                                newFlowVO.setUserId(targetUserId.longValue());
                                clientDAO.addDFlow(newFlowVO);
                                List<DNodeVO> templateNodeList = clientDAO.queryListByFlowId(crmApi.getFlowId());
                                Map<Long, DNodeVO> newNodeVOMap = new HashMap<>();
                                List<Long> nodeIdList = new ArrayList<>();
                                templateNodeList.forEach(node -> {
                                    nodeIdList.add(node.getNodeId());
                                    DNodeVO newNode = new DNodeVO();
                                    BeanUtils.copyProperties(node, newNode);
                                    newNode.setFlowId(newFlowVO.getFlowId());
                                    newNode.setNodeId(null);
                                    clientDAO.addDNode(newNode);
                                    newNodeVOMap.put(node.getNodeId(), newNode);
                                    DNodeModel dNodeModel = clientDAO.queryNodeModel(node.getNodeId());
                                    DNodeModel newNodeModel = new DNodeModel();
                                    BeanUtils.copyProperties(dNodeModel, newNodeModel);
                                    newNodeModel.setModelId(null);
                                    newNodeModel.setNodeId(newNode.getNodeId());
                                    clientDAO.addDNodeModel(newNodeModel);
                                    if (dNodeModel.getModelType() < 3) {
                                        List<DNodeRuleVO> nodeRuleVOList = clientDAO.queryDNodeRuleVOListByModelId(dNodeModel.getModelId());
                                        List<DNodeRuleVO> newNodeRuleVOList = new ArrayList<>();
                                        List<DNodeRuleVarVO> newNodeRuleVarVOList = new ArrayList<>();
                                        Map<Long, DNodeRuleVO> ruleMap = new HashMap<>();
                                        List<Long> ruleIdList = new ArrayList<>();
                                        nodeRuleVOList.forEach(nodeRuleVO -> {
                                            ruleIdList.add(nodeRuleVO.getRuleId());
                                            DNodeRuleVO newNodeRuleVO = new DNodeRuleVO();
                                            BeanUtils.copyProperties(nodeRuleVO, newNodeRuleVO);
                                            newNodeRuleVO.setRuleId(null);
                                            newNodeRuleVO.setModelId(newNodeModel.getModelId());
                                            ruleMap.put(nodeRuleVO.getRuleId(), newNodeRuleVO);
                                            newNodeRuleVOList.add(newNodeRuleVO);
                                        });
                                        clientDAO.addDNodeRuleList(newNodeRuleVOList);
                                        List<DNodeRuleVarVO> nodeRuleVarVOList = clientDAO.queryDNodeRuleVarVOListByRuleIdList(ruleIdList);
                                        nodeRuleVarVOList.forEach(nodeRuleVarVO -> {
                                            DNodeRuleVarVO newNodeRuleVarVO = new DNodeRuleVarVO();
                                            BeanUtils.copyProperties(nodeRuleVarVO, newNodeRuleVarVO);
                                            newNodeRuleVarVO.setVarId(null);
                                            newNodeRuleVarVO.setRuleId(ruleMap.get(nodeRuleVarVO.getRuleId()).getRuleId());
                                            newNodeRuleVarVOList.add(newNodeRuleVarVO);
                                        });
                                        clientDAO.addDNodeRuleVarList(newNodeRuleVarVOList);
                                    } else if (Objects.equals(5, dNodeModel.getModelType())) {
                                        //评分卡复制，待开发
                                    } else if (Objects.equals(6, dNodeModel.getModelType())) {
                                        //决策表复制，待开发
                                    } else if (Objects.equals(7, dNodeModel.getModelType())) {
                                        //决策树复制，待开发
                                    }
                                });
                                if (!CollectionUtils.isEmpty(nodeIdList)) {
                                    List<DNodeParamsVO> nodeParamsVOList = clientDAO.queryDNodeParamsVOListByNodeIdList(nodeIdList);
                                    if (!CollectionUtils.isEmpty(nodeParamsVOList)) {
                                        List<DNodeParamsVO> newNodeParamsVOList = new ArrayList<>();
                                        nodeParamsVOList.forEach(nodeParamsVO -> {
                                            DNodeParamsVO newNodeParamsVO = new DNodeParamsVO();
                                            BeanUtils.copyProperties(nodeParamsVO, newNodeParamsVO);
                                            newNodeParamsVO.setId(null);
                                            newNodeParamsVO.setNodeId(newNodeVOMap.get(nodeParamsVO.getNodeId()).getNodeId());
                                            newNodeParamsVOList.add(newNodeParamsVO);
                                        });
                                        clientDAO.addDNodeParamsVOList(newNodeParamsVOList);
                                    }
                                }
                                ModuleTypeVO moduleTypeVO = moduleTypeDAO.findModuleTypeTemplateByFlowId(crmApi.getFlowId());
                                ModuleTypeVO newModuleTypeVO = new ModuleTypeVO();
                                BeanUtils.copyProperties(moduleTypeVO, newModuleTypeVO);
                                newModuleTypeVO.setModuleTypeId(null);
                                newModuleTypeVO.setApiCode(apiCode);
                                newModuleTypeVO.setUserId(targetUserId);
                                newModuleTypeVO.setFlowId(newFlowVO.getFlowId());
                                moduleTypeDAO.addModuleType(newModuleTypeVO);
                                ModuleTypeApi moduleTypeApi = new ModuleTypeApi();
                                moduleTypeApi.setModuleTypeId(newModuleTypeVO.getModuleTypeId());
                                moduleTypeApi.setApiCode(apiCode);
                                moduleTypeApi.setReportType(newModuleTypeVO.getReportType());
                                moduleTypeApi.setValidEnd("3000-01-01 00:00:00");
                                moduleTypeApiDAO.addModuleTypeApi(moduleTypeApi);
                                crmApi.setModuleTypeId(newModuleTypeVO.getModuleTypeId());
                                crmApiDAO.addCrmApiClient(crmApi);
                                jsonObject.put("flowId",newFlowVO.getFlowId());
                                jsonObject.put("moduleTypeId",newModuleTypeVO.getModuleTypeId());
                            }*/
                        }
                    }
                }
            }
        }

        /*int count = 0;
        List<ModuleTypeApi> moduleTypeApiList = moduleTypeApiDAO.findModuleTypeApiListByApiCode(apiCode);
        if(null==productList||productList.size()==0){
            if(!CollectionUtils.isEmpty(moduleTypeApiList)){
                for(ModuleTypeApi moduleTypeApi:moduleTypeApiList){
                    count = count + moduleTypeApiDAO.updateStatusByModuleTypeApi(new ModuleTypeApi(moduleTypeApi.getReportType(),apiCode,0));
                }
            }
        }else{
            if(CollectionUtils.isEmpty(moduleTypeApiList)){
                for(int i=0;i<productList.size();i++){
                    JSONObject product = productList.getJSONObject(i);
                    ClientVO clientVO = new ClientVO();
                    clientVO.setVersion(product.getDouble("version"));
                    clientVO.setProdCode(product.getString("prodCode"));
                    clientVO.setApiCode(apiCode);
                    clientVO.setEndDate(product.getString("endDate"));
                    JSONObject temp = addClient(clientVO);
                    count = count + (temp.getInteger("count")==null?0:temp.getInteger("count"));
                }
            }else{
                Set<String> reportTypeSet = new HashSet<>();
                for(int i=0;i<productList.size();i++){
                    JSONObject product = productList.getJSONObject(i);
                    ClientVO clientVO = new ClientVO();
                    clientVO.setVersion(product.getDouble("version"));
                    clientVO.setProdCode(product.getString("prodCode"));
                    clientVO.setApiCode(apiCode);
                    clientVO.setEndDate(product.getString("endDate"));
                    *//*String endDate = product.getString("endDate");
                    if(StringUtils.isNotEmpty(endDate)){
                        JSONObject endDateJson = new JSONObject();
                        endDateJson.put("apiCode",apiCode);
                        endDateJson.put("prodCode",product.getString("prodCode"));
                        String propKey = endDateJson.toJSONString();
                        stringRedisTemplate.opsForHash().put("biz_credit:report:endDate",propKey,endDate);
                    }*//*
                    JSONObject temp = addClient(clientVO);
                    count = count + (temp.getInteger("count")==null?0:temp.getInteger("count"));
                    reportTypeSet.add(product.getString("prodCode").split("_")[product.getString("prodCode").split("_").length-1]);
                }
                if(!CollectionUtils.isEmpty(moduleTypeApiList)){
                    for(ModuleTypeApi moduleTypeApi:moduleTypeApiList){
                        if(!reportTypeSet.contains(moduleTypeApi.getReportType().toString())){
                            count = count + moduleTypeApiDAO.updateStatusByModuleTypeApi(new ModuleTypeApi(moduleTypeApi.getReportType(),apiCode,0));
                        }
                    }
                }
            }
        }
        jsonObject.put("count",count);*/
        return jsonObject;
    }

    @Override
    public List<CrmApiVO> findCrmApiVOList(String apiCode, Integer industryId, Integer reportType) {
        return crmApiDAO.findCrmApiVOList(apiCode,industryId,reportType);
    }


    /*public JSONObject deleteProduct(String apiCode, String prodCode) throws Exception {
        JSONObject jsonObject = new JSONObject();
        Integer reportType = Integer.parseInt(prodCode.substring(prodCode.lastIndexOf("_")+1));
        int count = moduleTypeApiDAO.updateStatusByModuleTypeApi(new ModuleTypeApi(reportType,apiCode,0));
        jsonObject.put("count",count);
        return jsonObject;
    }*/
}
