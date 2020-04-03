package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IReportTaskService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.InputFileDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
public class ReportInputFileDetailController {
    @Autowired
    private IReportTaskService reportTaskService;
    /**
     * 根据任务id 更新失败进件状态 status 4
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateFailedInputFileDetail", method = RequestMethod.GET)
    @ResponseBody
    public RespEntity updateFailedInputFileDetail(@RequestParam(name = "inputFileDetailId", required = true)Integer inputFileDetailId){
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            InputFileDetailVO inputFileDetail = new InputFileDetailVO();
            inputFileDetail.setInputFileDetailId(inputFileDetailId);
            inputFileDetail.setStatus(Constants.INPUTFILE_DELETE_STATUS);
            int count  = reportTaskService.updateFailedInputFileDetail(inputFileDetail);
            JSONObject jo = new JSONObject();
            jo.put("total", count);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.info("任务ID[{}]删除进件状态更新失败失败:{}",inputFileDetailId,e.getMessage());
            return ret;
        }
        return ret;
    }
}
