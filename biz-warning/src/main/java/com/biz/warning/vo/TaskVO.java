package com.biz.warning.vo;

import com.biz.warning.domain.Task;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 预警任务vo类
 */
@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@NoArgsConstructor
public class TaskVO extends Task implements Serializable {
    private String lastUpdateTimeStr;
    private String createTimeStr;
    private String startDateStr;
    private String endDateStr;
    private Integer withStrategyInfo;
    private String execInterval;
    //private Task task;
    private StrategyVO strategyVO;
    private String execIntervalUnit;
    private Integer execIntervalNum;
    private Integer handle = 1;
    private String ids;
    private List<Integer> idList;


    public void setHandle(Integer handle) {
        if(null!=handle)
            this.handle = handle;
    }

    public Integer getExecIntervalNum() {
        if(StringUtils.isNotEmpty(execInterval)){
            execIntervalNum = Integer.parseInt(execInterval.substring(0,execInterval.length()-1));
        }
        return execIntervalNum;
    }

    public String getExecIntervalUnit() {
        if(StringUtils.isNotEmpty(execInterval)){
            execIntervalUnit = execInterval.substring(execInterval.length()-1);
        }
        return execIntervalUnit;
    }
    public TaskVO (Long taskId){
        setTaskId(taskId);
    }
}
