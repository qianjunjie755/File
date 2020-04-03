package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.CreditModelDAO;
import com.biz.credit.dao.DNodeModelDAO;
import com.biz.credit.dao.DNodeThresholdDAO;
import com.biz.credit.domain.DNodeModel;
import com.biz.credit.domain.DNodeThreshold;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.ICreditModelService;
import com.biz.credit.service.IDNodeThresholdService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.ApiVO;
import com.biz.credit.vo.DNodeConfigVO;
import com.biz.credit.vo.IndustryInfoVO;
import com.biz.credit.vo.NodeCreditModelConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Slf4j
@Service
public class CreditModelServiceImpl implements ICreditModelService{
    @Autowired
    private CreditModelDAO creditModelDAO;
    @Autowired
    private DNodeModelDAO dNodeModelDAO;
    @Autowired
    private DNodeThresholdDAO dNodeThresholdDAO;
    @Autowired
    private IDNodeThresholdService nodeThresholdService;
    @Override
    public List<JSONObject> queryCreditModelConfig(DNodeConfigVO nodeConfigVO){
        List<com.biz.credit.vo.IndustryInfoVO> industryInfoList = creditModelDAO.queryCreditModelList(nodeConfigVO.getApiCode());
        List<String> industryApiList = creditModelDAO.queryApiList(nodeConfigVO.getApiCode());
        List<JSONObject> iList = new ArrayList<>();
        List<DNodeThreshold> nodeThresholds=new ArrayList<>();
        Integer modelCode=null;
        if(nodeConfigVO.getNodeId()!=null){
            DNodeModel dNodeModel=new DNodeModel();
            dNodeModel.setModelType(Constants.CREDIT_SCORE_MODEL);
            dNodeModel.setNodeId(nodeConfigVO.getNodeId());
            List<DNodeThreshold> dNodeThresholdList=dNodeThresholdDAO.queryByNodeThreshold(dNodeModel);
            if(!CollectionUtils.isEmpty(dNodeThresholdList)){
                   for(DNodeThreshold dNodeThreshold :dNodeThresholdList){
                       modelCode= dNodeThreshold.getModelCode();
                   }
                nodeThresholds.addAll(dNodeThresholdList);
            }
        }

        for(IndustryInfoVO industryInfoVO : industryInfoList){
            JSONObject jsonObject1 = new JSONObject();
            if(modelCode!=null){
                if(modelCode.equals(industryInfoVO.getIndustryId())){
                    jsonObject1.put("nodeThresholdList",nodeThresholds);
                    jsonObject1.put("choose",true);
                }else{
                    List<DNodeThreshold> list =new ArrayList<>();
                    DNodeThreshold dNodeThreshold=new DNodeThreshold();
                    dNodeThreshold.setJudge("");
                    list.add(dNodeThreshold);
                    jsonObject1.put("nodeThresholdList",list);
                    jsonObject1.put("choose",false);
                }
            }else{
                List<DNodeThreshold> list =new ArrayList<>();
                DNodeThreshold dNodeThreshold=new DNodeThreshold();
                dNodeThreshold.setJudge("");
                list.add(dNodeThreshold);
                jsonObject1.put("nodeThresholdList",list);
                jsonObject1.put("choose",false);
            }
            jsonObject1.put("industryId",industryInfoVO.getIndustryId());
            jsonObject1.put("industryCode", industryInfoVO.getIndustryCode());
            jsonObject1.put("industryName", industryInfoVO.getIndustryName());
            jsonObject1.put("modelCode",industryInfoVO.getModelCode());
            jsonObject1.put("modelVersion", industryInfoVO.getModelVersion());
            Map<Integer,String> hashMap=excludeRepetition(industryInfoVO.getApiProdCode(),industryApiList);
            Set set=hashMap.keySet();
            Iterator iter = set.iterator();
            if("0".equals(iter.next().toString())){
                jsonObject1.put("industryApiList",hashMap.get(0));
                log.info("权限内信用评分模型"+JSON.toJSON(jsonObject1));
                iList.add(jsonObject1);
            }

        }

        return iList;
    }

    public Map excludeRepetition(String str, List<String> industryApiList){
        Map<Integer,String> map=new HashMap();
        String[] arr = str.split(",");
        List<String> list = new ArrayList<>();
        Integer a=0;
        for(int i = 0; i < arr.length; i++){
            String s = arr[i].trim();
            if(industryApiList.contains(s)){
                if(!list.contains(s)){
                    list.add(s);
                }
            }else{
                a=a+1;
            }

        }

        StringBuffer sb = new StringBuffer();
        for(String s : list){
            if(!(sb.toString().length() <= 0)){
                sb.append(",");
            }
            sb.append(s);
        }
        map.put(a,sb.toString());
        return map;
    }

    @Override
    public List<ApiVO> queryCreditModelApiList(Integer creditModelId) {
        return  creditModelDAO.queryIndustryInfoApiList(creditModelId);
    }

    @Override
    @Transactional
    public RespEntity saveCreditModelConfig(Long nodeId, NodeCreditModelConfig nodeCreditModelConfig, boolean isNew) {
        if(nodeCreditModelConfig==null){
            return RespEntity.error().setMsg("nodeCreditModelConfig为空");
        }
        dNodeModelDAO.updateStatusByNodeIdAndType(nodeId, Constants.CREDIT_SCORE_MODEL, Constants.COMMON_STATUS_INVALID);
        DNodeModel dNodeModel=new DNodeModel();
        dNodeModel.setModelType(Constants.CREDIT_SCORE_MODEL);
        dNodeModel.setNodeId(nodeId);
        dNodeModel.setStatus(Constants.COMMON_STATUS_VALID);
        dNodeModel.setModelCode(nodeCreditModelConfig.getCreditModelData().get(0).getIndustryId().longValue());
        dNodeModelDAO.insert(dNodeModel);

        return nodeThresholdService.saveNodeThresholdList(dNodeModel.getModelId(),nodeCreditModelConfig.getNodeThresholdList());
    }
}
