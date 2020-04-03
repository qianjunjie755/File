package com.biz.warning.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页实体
 */
@Data
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PageVO implements Serializable {
    //页面大小
    private int pageSize = Integer.MAX_VALUE;
    //页数
    private int pageNo = 1;

}
