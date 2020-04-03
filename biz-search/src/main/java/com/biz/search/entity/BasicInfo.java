package com.biz.search.entity;

import com.biz.search.utils.Constants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Setter
@Getter
@ApiModel("企业工商基本信息")
@Document(indexName = Constants.BASIC_INDEX, type = Constants.BASIC_TYPE)
public class BasicInfo {
    @Id
    @ApiModelProperty(value = "序号")
    private Long id;

    @Field(type = FieldType.text, analyzer = Constants.IK_ANALYZER, searchAnalyzer = Constants.IK_SEARCH_ANALYZER)
    @ApiModelProperty(value = "企业名称")
    private String companyName;

    @Field(type = FieldType.keyword)
    @ApiModelProperty(value = "统一社会信用代码")
    private String creditCode;

    @Field(type = FieldType.keyword)
    @ApiModelProperty(value = "法人姓名")
    private String legalPersonName;

    @Field(type = FieldType.keyword, index = false)
    @ApiModelProperty(value = "注册号")
    private String registerNo;

    @Field(type = FieldType.text, index = false)
    @ApiModelProperty(value = "注册资本")
    private String registerCapital;

    @Field(type = FieldType.text, index = false)
    @ApiModelProperty(value = "注册地址")
    private String registerAddress;

    @Field(type = FieldType.text, index = false)
    @ApiModelProperty(value = "经营状态")
    private String runStatus;

    @Field(type = FieldType.text, index = false)
    @ApiModelProperty(value = "所在省份")
    private String province;

    @Field(type = FieldType.text, index = false)
    @ApiModelProperty(value = "成立时间")
    private String setUpTime;

    @Field(type = FieldType.text, index = false)
    @ApiModelProperty(value = "经营范围")
    private String bizScope;
}
