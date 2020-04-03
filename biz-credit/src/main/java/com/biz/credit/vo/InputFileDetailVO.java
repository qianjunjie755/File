package com.biz.credit.vo;

import com.biz.credit.domain.InputFileDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InputFileDetailVO extends InputFileDetail implements Serializable {
    private Integer userId;
    private String apiCode;
    private String endTime;
    private String beginTime;
    private Integer moduleTypeId;
    private Integer reportType;
    private Integer industryId;
    private Integer taskType=1;
    private Integer strategyId;
    private List users;
    private Integer taskStatus;//任务状态  1-处理中 2-处理完成
    private String apiTaskId;

    public void setTaskType(Integer taskType) {
        if(null!= taskType)
            this.taskType = taskType;
    }

    public InputFileDetailVO(){
        super();
    }

    public InputFileDetailVO(Integer inputFileDetailId){
        super.setInputFileDetailId(inputFileDetailId);
    }
    public InputFileDetailVO(Integer inputFileDetailId,Integer userId){
      this.userId = userId;  super.setInputFileDetailId(inputFileDetailId);
    }
    public InputFileDetailVO(Integer inputFileDetailId,String apiCode){
        this.apiCode = apiCode;  super.setInputFileDetailId(inputFileDetailId);
    }
}
