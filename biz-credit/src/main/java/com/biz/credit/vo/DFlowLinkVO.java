package com.biz.credit.vo;

import com.biz.credit.domain.DFlowLink;
import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel("决策流所属业务环节对象")
@Getter
@Setter
@NoArgsConstructor
public class DFlowLinkVO extends DFlowLink {
    public DFlowLinkVO(Integer id,String linkName,Integer bizId,Integer status,String createTime,String updateTime){
        setId(id);setLinkName(linkName);setBizId(bizId);setStatus(status);setCreateTime(createTime);setUpdateTime(updateTime);
    }
}
