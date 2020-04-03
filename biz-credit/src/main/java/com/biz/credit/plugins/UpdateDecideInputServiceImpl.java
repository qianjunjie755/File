package com.biz.credit.plugins;

import com.biz.credit.dao.ReportPluginDAO;
import com.biz.decision.common.Constants;
import com.biz.decision.common.Result;
import com.biz.decision.entity.EntityBasic;
import com.biz.decision.entity.Input;
import com.biz.decision.entity.Task;
import com.biz.decision.enums.EReqType;
import com.biz.decision.plugins.IUpdateInput;
import com.biz.decision.report.entity.InputData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateDecideInputServiceImpl implements IUpdateInput {
    @Autowired
    private ReportPluginDAO reportPluginDAO;

    @Override
    public void execute(Task task, Input input) {
        Result result = input.getResult();
        InputData inputData = (InputData) result.getData();
        EReqType type = task.getReqType();
        if (result.ok()) {
             if(input!=null){
                 if(EReqType.WEB_PDF==type){
                     reportPluginDAO.updateInput(input.getInputId(),inputData.getPdfFilePath(),inputData.getPdfFileName(), Constants.STATUS_SUCCESS);
                 }else{
                     reportPluginDAO.update3rdInput(input.getInputId(),inputData.getPdfFilePath(),inputData.getPdfFileName(), Constants.STATUS_SUCCESS);
                 }
             }
        }else{
            if(type == EReqType.WEB_PDF){
                reportPluginDAO.updateInput(input.getInputId(),inputData.getPdfFilePath(),inputData.getPdfFileName(), Constants.STATUS_FAILED);
            }else{
                reportPluginDAO.update3rdInput(input.getInputId(),inputData.getPdfFilePath(),inputData.getPdfFileName(), Constants.STATUS_FAILED);
            }
        }
        EntityBasic basicInfo = input.getBasicInfo();
        if(basicInfo !=null){
            if(reportPluginDAO.updateDecideEntityBasicInfo(basicInfo)<=0){
                reportPluginDAO.insertDecideEntityBasicInfo(basicInfo);
            }
        }

    }
}
