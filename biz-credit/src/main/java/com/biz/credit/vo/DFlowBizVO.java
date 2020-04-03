package com.biz.credit.vo;

import com.biz.credit.domain.DFlowBiz;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel("决策流所属业务对象")
@Setter
@Getter
@NoArgsConstructor
public class DFlowBizVO extends DFlowBiz {
    public DFlowBizVO(Integer id,String bizName,Integer platformId,Integer status,String createTime,String updateTime){
        setId(id);setBizName(bizName);setPlatformId(platformId);setStatus(status);setCreateTime(createTime);setUpdateTime(updateTime);
    }
}
