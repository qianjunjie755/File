package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.*;
import com.biz.credit.domain.ModuleTypeApi;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDFlowService;
import com.biz.credit.service.IDNodeApiService;
import com.biz.credit.service.IDNodeParamsService;
import com.biz.credit.service.IDNodeService;
import com.biz.credit.utils.Constants;
import com.biz.credit.utils.comparators.DTaskParamVOComparator;
import com.biz.credit.vo.*;
import com.biz.decision.BizDecide;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class DFlowServiceImpl implements IDFlowService {

    @Autowired
    private DFlowDAO dFlowDAO;
    @Autowired
    private BizDecide bizDecide;
    @Autowired
    private IDNodeService nodeService;
    @Autowired
    private IDNodeParamsService nodeParamsService;
    @Autowired
    private ModuleTypeApiDAO moduleTypeApiDAO;
    @Autowired
    private ModuleTypeDAO moduleTypeDAO;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Value("${biz.decision.flow-params}")
    private String flowParamsKey;
    @Value("${biz.decision.flow-key}")
    private String decisionFlowKey;

    @Override
    public List<DFlowVO> findDFlowVOList(DFlowVO dFlowVO) {
        return dFlowDAO.findDFlowVOList(dFlowVO);
    }


    @Override
    @Transactional(isolation = Isolation.DEFAULT)
    public RespEntity saveFlow(DFlowVO flowVO) {
        boolean isNew = false;
        if(flowVO.getFlowId() == null){
            isNew = true;
        }
        RespEntity checkResp = checkParams(flowVO,isNew);
        if(!checkResp.isSuccess()){
            return checkResp;
        }
        if(isNew){
            flowVO.setStatus(Constants.COMMON_STATUS_INVALID);
            dFlowDAO.insert(flowVO);
        }else{
            //更新基本数据
            dFlowDAO.update(flowVO);
            //保存节点
            if(!CollectionUtils.isEmpty(flowVO.getNodeVOList())){
                for (DNodeVO nodeVO : flowVO.getNodeVOList()) {
                    nodeVO.setUserId(flowVO.getUserId().toString());
                    nodeVO.setFlowId(flowVO.getFlowId());
                    RespEntity respEntity = nodeService.saveNode(nodeVO);
                    if(!respEntity.isSuccess()){
                        throw new RuntimeException(respEntity.getMsg());
                    }
                }
            }
        }
        return RespEntity.success();
    }

    @Override
    @Transactional(isolation = Isolation.DEFAULT)
    public RespEntity publishFlow(DFlowVO flowVO) {
        if(flowVO.getFlowId() == null){
            return RespEntity.error().setMsg("决策流id不能为空");
        }
        DFlowVO existFlow = dFlowDAO.getById(flowVO.getFlowId());
        if(existFlow == null){
            return RespEntity.error().setMsg("决策流不存在");
        }
        if(Constants.COMMON_STATUS_VALID.equals(existFlow.getStatus())){
            return RespEntity.error().setMsg("不能重复发布决策流");
        }
        //发布不做保存功能
        /*RespEntity saveResp = saveFlow(flowVO);
        if(!saveResp.isSuccess()){
            return saveResp;
        }*/
        //清理决策流缓存
        bizDecide.cleanCache(flowVO.getFlowId().intValue());
        //清理决策流参数缓存
        redisTemplate.opsForHash().delete(flowParamsKey, String.valueOf(flowVO.getFlowId()));
        redisTemplate.opsForHash().delete(decisionFlowKey.replaceAll("decision","varThreshold"),String.valueOf(flowVO.getFlowId()));
        //
        flowVO.setStatus(Constants.COMMON_STATUS_VALID);
        dFlowDAO.update(flowVO);
        //插入moduleType表和moduleTypeApi表
        ModuleTypeVO moduleTypeVO = new ModuleTypeVO();
        moduleTypeVO.setReportType(777);
        moduleTypeVO.setModuleTypeName(existFlow.getFlowName());
        moduleTypeVO.setIsTemplate(0);
        moduleTypeVO.setFlowId(existFlow.getFlowId());
        moduleTypeVO.setStrategyId(-1l);
        moduleTypeVO.setProdCode("WebBizCustomPlusReport");
        moduleTypeVO.setProdName("云端信用报告(自定义策略) ");
        moduleTypeVO.setRadarModelId(-1L);
        moduleTypeVO.setHtmlTemplateName("customTemplate.html");
        ModuleTypeApi moduleTypeApi = new ModuleTypeApi();
        moduleTypeApi.setApiCode(existFlow.getApiCode());
        DTaskVO dTaskVO = nodeParamsService.findDTaskVOByDFlowVO(existFlow);
        List<DTaskParamVO> paramVOList = new ArrayList<>();
        Set<String> paramVONameSet = new HashSet<>();
        List<DTaskParamVO> personParamVOList = new ArrayList<>();
        Set<String> personParamVONameSet = new HashSet<>();
        dTaskVO.getRows().forEach(group->{
            if(!group.getTitle().contains("关联人")){
                group.getItems().forEach(item->{
                    if(!paramVONameSet.contains(item.getKey())){
                        paramVOList.add(item);
                        paramVONameSet.add(item.getKey());
                    }

                });
            }else{
                group.getItems().forEach(item->{
                    if(!personParamVONameSet.contains(item.getKey())){
                        personParamVOList.add(item);
                        personParamVONameSet.add(item.getKey());
                    }

                });
            }

        });
        DTaskParamVOComparator dTaskParamVOComparator = new DTaskParamVOComparator();
        Collections.sort(paramVOList,dTaskParamVOComparator);
        Collections.sort(personParamVOList,dTaskParamVOComparator);
        StringBuffer columnHead = new StringBuffer();
        StringBuffer columnHeadPerson = new StringBuffer();
        paramVOList.forEach(paramVO->{
            columnHead.append(paramVO.getKey()).append("_");
        });
        personParamVOList.forEach(paramVO->{
            columnHeadPerson.append(paramVO.getKey()).append("_");
        });
        if(columnHead.length()>0){
            moduleTypeVO.setColumnHead(columnHead.substring(0,columnHead.length()-1));
        }
        if(columnHeadPerson.length()>0){
            moduleTypeVO.setColumnHeadPerson(columnHeadPerson.substring(0,columnHeadPerson.length()-1));
        }
        ModuleTypeVO moduleTypeVOExisted = moduleTypeDAO.findModuleTypeByFlowId(flowVO.getFlowId());
        if(null==moduleTypeVOExisted){
            moduleTypeDAO.addModuleType(moduleTypeVO);
            moduleTypeApi.setModuleTypeId(moduleTypeVO.getModuleTypeId());
            moduleTypeApi.setReportType(moduleTypeVO.getReportType());
            moduleTypeApi.setValidEnd("3000-12-31");
            moduleTypeApiDAO.addModuleTypeApi(moduleTypeApi);
        }else{
            moduleTypeVOExisted.setColumnHead(moduleTypeVO.getColumnHead());
            moduleTypeVOExisted.setColumnHeadPerson(moduleTypeVO.getColumnHeadPerson());
            moduleTypeDAO.updateColumnHead(moduleTypeVOExisted);
            moduleTypeApi.setModuleTypeId(moduleTypeVOExisted.getModuleTypeId());
            moduleTypeApi.setStatus(Constants.COMMON_STATUS_VALID);
            moduleTypeApiDAO.updateStatusByModuleTypeApi(moduleTypeApi);
        }
        return RespEntity.success();
    }

    @Override
    public RespEntity cancelFlow(DFlowVO flowVO) {
        DFlowVO existedFlowVO = dFlowDAO.getById(flowVO.getFlowId());
        if(null!=existedFlowVO
                &&Objects.equals(flowVO.getApiCode(),existedFlowVO.getApiCode())){
            ModuleTypeVO moduleTypeVO =  moduleTypeDAO.findModuleTypeByFlowId(flowVO.getFlowId());
            DFlowVO updateFlow = new DFlowVO();
            BeanUtils.copyProperties(existedFlowVO,updateFlow);
            updateFlow.setStatus(Constants.COMMON_STATUS_INVALID);
            dFlowDAO.update(updateFlow);
            if(moduleTypeVO!=null){
                ModuleTypeApi moduleTypeApi = new ModuleTypeApi();
                moduleTypeApi.setStatus(Constants.COMMON_STATUS_INVALID);
                moduleTypeApi.setApiCode(flowVO.getApiCode());
                moduleTypeApi.setModuleTypeId(moduleTypeVO.getModuleTypeId());
                moduleTypeApiDAO.updateStatusByModuleTypeApi(moduleTypeApi);
            }
            RespEntity respEntity = RespEntity.success();
            respEntity.setData(updateFlow);
            return respEntity;
        }
        return RespEntity.error();
    }

    @Override
    public List<DFlowVO> getFlowList(DFlowVO flowVO) {
        return dFlowDAO.queryList(flowVO);
    }

    @Override
    public DFlowVO getFlowDetailById(Long flowId,String apiCode,String userId) {
        DFlowVO flowVO = dFlowDAO.getById(flowId);
        List<DNodeVO> nodeList = nodeService.getListByFlowId(flowId);
        nodeList.forEach(node -> {
            JSONObject nodeDetail = new JSONObject();
            node.setNodeConfig(nodeDetail);
            node.setNodeApiList(new ArrayList<>());
            node.setNodeParamList(new ArrayList<>());
        });
        flowVO.setNodeList(nodeList);
        return flowVO;
    }

    private RespEntity checkParams(DFlowVO flowVO,boolean isNew) {
        if(StringUtils.isEmpty(flowVO.getFlowName())){
            return RespEntity.error().setMsg("决策流名称不能为空");
        }
        if(isNew){
            //判断名称是否存在
            int count = dFlowDAO.queryCountByName(flowVO.getFlowName(),flowVO.getApiCode());
            if(count > 0){
                return RespEntity.error().setMsg("决策流名称已存在，请更换");
            }
        }
        return RespEntity.success();
    }
}
