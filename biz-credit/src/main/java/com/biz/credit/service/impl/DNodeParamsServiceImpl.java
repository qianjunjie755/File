package com.biz.credit.service.impl;

import com.biz.credit.dao.ApiParamCfgDAO;
import com.biz.credit.dao.DFlowDAO;
import com.biz.credit.dao.DNodeParamsDAO;
import com.biz.credit.service.IDNodeParamsService;
import com.biz.credit.utils.comparators.DTaskParamVOComparator;
import com.biz.credit.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class DNodeParamsServiceImpl implements IDNodeParamsService {

    @Value("${biz.api_admin.redis.keys.api-param-cfg-map}")
    private String apiParamCfgMapKey;


    @Autowired
    private DNodeParamsDAO dNodeParamsDAO;
    @Autowired
    private ApiParamCfgDAO apiParamCfgDAO;
    @Autowired
    private DFlowDAO dFlowDAO;
    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public DTaskVO findDTaskVOByDFlowVO(DFlowVO dFlowVO) {
        DTaskParamVOComparator dTaskParamVOComparator = new DTaskParamVOComparator();
        DTaskVO  dTaskVO = new DTaskVO();
        List<DFlowVO> dFlowVOList = dFlowDAO.findDFlowVOList(dFlowVO);
        if(CollectionUtils.isEmpty(dFlowVOList)){
            return dTaskVO;
        }
        dTaskVO.setRelatedP(0);
        dTaskVO.setCompany(false);
        DFlowVO dFlowVORet = dFlowVOList.iterator().next();
        List<ApiParamCfgVO> apiParamCfgList = apiParamCfgDAO.findApiParamCfgList();
        Map<String,Integer> paramTypeMap = new HashMap<>();
        Set<Integer> paramTypeSet = new HashSet<>();
        Map<String,ApiParamCfgVO> apiParamCfgVOMap = new HashMap<>();
        apiParamCfgList.forEach(apiRequestMapVO -> {
            paramTypeMap.put(apiRequestMapVO.getParamName(),apiRequestMapVO.getParamType());
            //paramTypeSet.add(apiRequestMapVO.getParamType());
            apiParamCfgVOMap.put(apiRequestMapVO.getParamName(),apiRequestMapVO);
        });
        Map<String,DTaskGroupVO> groupMap = new LinkedHashMap<>();
        List<DNodeParamsVO> dNodeParamsVOList = dNodeParamsDAO.findDNodeParamsVOListByDFlowVO(dFlowVO);
        Map<String,DTaskParamVO> paramsMap = new LinkedHashMap<>();
        dNodeParamsVOList.forEach(dNodeParamsVO -> {
            ApiParamCfgVO apiParamCfgVO = apiParamCfgVOMap.get(dNodeParamsVO.getName());
            if(null!=apiParamCfgVO){
                dNodeParamsVO.setOrder(apiParamCfgVO.getParamOrder());
                dNodeParamsVO.setParamType(apiParamCfgVO.getParamType());
                DTaskParamVO dTaskParamVO = paramsMap.get(dNodeParamsVO.getName());
                if(null!=dTaskParamVO){
                    if("0".equals(dTaskParamVO.getRequired())&&1==dNodeParamsVO.getRequired().intValue()){
                        dTaskParamVO.setRequired(dNodeParamsVO.getRequired().toString());
                    }
                }else{
                    paramsMap.put(dNodeParamsVO.getName(),new DTaskParamVO(dNodeParamsVO));
                }
                paramTypeSet.add(apiParamCfgVO.getParamType());
            }
        });
        paramsMap.forEach((paramName,param)->{
            Integer paramType = null==paramTypeMap.get(paramName)?1:paramTypeMap.get(paramName);
            String groupName = DTaskGroupVO.GROUP_NAME_MAP.get(paramType);
            if(groupMap.containsKey(groupName)){
                groupMap.get(groupName).getItems().add(param);
            }else{
                DTaskGroupVO dTaskGroupVO = new DTaskGroupVO();
                dTaskGroupVO.getItems().add(param);
                dTaskGroupVO.setTitle(DTaskGroupVO.GROUP_NAME_MAP.get(paramType));
                groupMap.put(dTaskGroupVO.getTitle(),dTaskGroupVO);
            }
        });
        Integer relatedP = dFlowVORet.getRelatedP();
        dTaskVO.setCompany(paramTypeSet.contains(1));
        if(paramTypeSet.contains(2)){
            DTaskGroupVO personGroup = groupMap.get(DTaskGroupVO.GROUP_NAME_MAP.get(2));
            if(relatedP.equals(1)){
                dTaskVO.setRelatedP(relatedP);
                DTaskGroupVO.GROUP_NAME_MAP.forEach((paramIndex,groupName)->{
                    if(paramIndex>2){
                        DTaskGroupVO dTaskGroupVO = new DTaskGroupVO();
                        dTaskGroupVO.setTitle(groupName);
                        personGroup.getItems().forEach(param->{
                            DTaskParamVO dTaskParamVO = new DTaskParamVO();
                            BeanUtils.copyProperties(param,dTaskParamVO);
                            dTaskParamVO.setKey("关联人".concat(dTaskParamVO.getKey()));
                            dTaskParamVO.setRequired("0");
                            dTaskGroupVO.getItems().add(dTaskParamVO);
                        });
                        groupMap.put(dTaskGroupVO.getTitle().concat(paramIndex.toString()),dTaskGroupVO);
                    }
                });
            }
            String personPrefix = paramTypeSet.contains(1)?"法人代表":"申请人";
            personGroup.getItems().forEach(param->{
                param.setKey(personPrefix.concat(param.getKey()));
            });
        }
        Set<String> checkSet = new HashSet<>();
        groupMap.values().forEach(group->{
            dTaskVO.getRows().add(group);
            if(group.getTitle().contains("法人")&&!dTaskVO.getCompany()){
                group.setTitle(group.getTitle().replaceAll("法人","申请人"));
            }
            if(group.getTitle().contains("关联人")){
                dTaskVO.getRelatedPIndexList().add(dTaskVO.getRows().size()-1);
                group.getItems().forEach(param->{
                    if(!checkSet.contains(param.getKey())){
                        dTaskVO.getRelatedParamVOList().add(param);
                        checkSet.add(param.getKey());
                    }
                });
            }else{
                group.getItems().forEach(param->{
                    dTaskVO.getParamVOList().add(param);
                });
            }
        });
        dTaskVO.getRows().forEach(group->{
            Collections.sort(group.getItems(),dTaskParamVOComparator);
        });
        Collections.sort(dTaskVO.getParamVOList(),dTaskParamVOComparator);
        Collections.sort(dTaskVO.getRelatedParamVOList(),dTaskParamVOComparator);
        return dTaskVO;
    }

    @Override
    public void findDTaskParamVOList(DFlowVO dFlowVO,List<DTaskParamVO> paramVOList,List<DTaskParamVO> relatedParamVOList) {
        DTaskParamVOComparator dTaskParamVOComparator = new DTaskParamVOComparator();
        DTaskVO  dTaskVO = new DTaskVO();
        List<DFlowVO> dFlowVOList = dFlowDAO.findDFlowVOList(dFlowVO);
        DFlowVO dFlowVORet = dFlowVOList.iterator().next();
        dFlowVO.setRelatedP(dFlowVORet.getRelatedP());
        if(1==dFlowVORet.getRelatedP()){

        }

        List<DNodeParamsVO> dNodeParamsVOList = dNodeParamsDAO.findDNodeParamsVOListByDFlowVO(dFlowVO);
        HashOperations<String,String,ApiParamCfgVO> hashOps = redisTemplate.opsForHash();
        Map<String,ApiParamCfgVO> map = hashOps.entries(apiParamCfgMapKey);
        List<DTaskParamVO> dTaskParamVOList = new ArrayList<>();
        dNodeParamsVOList.forEach(dNodeParam->{
            ApiParamCfgVO apiParamCfgVO = map.get(dNodeParam.getName());
            dNodeParam.setOrder(apiParamCfgVO.getParamOrder());
            dNodeParam.setParamType(apiParamCfgVO.getParamType());
            DTaskParamVO dTaskParamVO = new DTaskParamVO(dNodeParam);
            dTaskParamVOList.add(dTaskParamVO);
        });
        Collections.sort(dTaskParamVOList,dTaskParamVOComparator);
    }
}
