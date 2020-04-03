package com.biz.credit.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Slf4j
@Setter
@Getter
public class RelatedPersonVO implements Serializable {
    private String relatedPerson = StringUtils.EMPTY;
    private String relatedIdNumber = StringUtils.EMPTY;
    private String relatedCellPhone = StringUtils.EMPTY;
    private String relatedBizAddress = StringUtils.EMPTY;
    private String relatedHomeAddress = StringUtils.EMPTY;


}
