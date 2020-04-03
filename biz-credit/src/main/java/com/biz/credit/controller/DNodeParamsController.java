package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.InputFileDetailContact;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IDNodeParamsService;
import com.biz.credit.service.IInputFileDetailContactService;
import com.biz.credit.service.IReportTaskService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/nodeParams")
public class DNodeParamsController {

    @Autowired
    private IDNodeParamsService nodeParamsService;
    @Autowired
    private IReportTaskService reportTaskService;
    @Autowired
    private IInputFileDetailContactService inputFileDetailContactService;


    @GetMapping("/mapTest")
    public RespEntity getDNodeParamsMap(@RequestParam(value = "flowId",required = false)Long flowId,
                                        @RequestParam(value = "inputFileDetailId",required = false) Integer inputFileDetailId,
                                        HttpServletRequest request){
        String apiCode = request.getSession().getAttribute(Constants.API_CODE).toString();
        RespEntity respEntity = new RespEntity(RespCode.SUCCESS,null);
        DFlowVO dFlowVO = new DFlowVO();
        dFlowVO.setFlowId(flowId);
        dFlowVO.setApiCode(apiCode);
        DTaskVO dTaskVO = nodeParamsService.findDTaskVOByDFlowVO(dFlowVO);
        if(null!=inputFileDetailId){
            InputFileDetailVO inputFileDetailVO = new InputFileDetailVO();
            inputFileDetailVO.setApiCode(apiCode);
            inputFileDetailVO.setInputFileDetailId(inputFileDetailId);
            InputFileDetailVO retInput = reportTaskService.findInputFileDetail(inputFileDetailVO);
            JSONObject retInputJson = JSONObject.parseObject(JSONObject.toJSONString(retInput));
            if(null!=retInput){
                dTaskVO.getRows().forEach(group->{
                    group.setTitle(group.getTitle().replaceAll("填写",StringUtils.EMPTY));
                    group.getItems().forEach(param->{
                        String propName = Constants.DetailPropMap.get(param.getKey());
                        if(StringUtils.isNotEmpty(propName)){
                            String value = retInputJson.getString(propName);
                            param.setValue(value);
                        }
                    });
                });

                if(dTaskVO.getRelatedP().equals(1)){
                    List<InputFileDetailContactVO> relatedPersonList = inputFileDetailContactService.findInputFileDetailContactVOList(inputFileDetailId);
                    while(!CollectionUtils.isEmpty(relatedPersonList)){
                        InputFileDetailContact relatedPerson = relatedPersonList.remove(0);
                        JSONObject rpJson = JSONObject.parseObject(JSONObject.toJSONString(relatedPerson));
                        Integer relatedPGroupIndex = dTaskVO.getRelatedPIndexList().remove(0);
                        DTaskGroupVO relatedPGroup = dTaskVO.getRows().get(relatedPGroupIndex);
                        relatedPGroup.getItems().forEach(item->{
                            String propName = Constants.DetailPropMap.get(item.getKey());

                            String value = rpJson.getString(propName);
                            item.setValue(value);
                        });
                    }
                }
            }
        }
        respEntity.changeRespEntity(RespCode.SUCCESS,dTaskVO);
        return respEntity;
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId","12131");
        jsonObject.put("apiCode",231321);
        InputFileDetailVO javaObj = JSONObject.toJavaObject(jsonObject, InputFileDetailVO.class);
        System.out.println(JSONObject.toJSONString(javaObj));

    }

}
