package com.biz.credit.vo;

import com.biz.credit.domain.DFlowPlatform;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ApiModel("决策流所属平台对象")
public class DFlowPlatformVO extends DFlowPlatform {

    public DFlowPlatformVO(Integer id, String platFormName, Integer status, String createTime, String updateTime){
        setId(id);setPlatFormName(platFormName);setStatus(status);setCreateTime(createTime);setUpdateTime(updateTime);
    }
}
