package com.biz.credit.vo;

import com.biz.credit.domain.InputFileDetailContact;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class InputFileDetailContactVO extends InputFileDetailContact implements Serializable {
    private String relatedPerson;
    private String relatedIdNumber;
    private String relatedCellPhone;
    private String relatedHomeAddress;
    private String relatedBizAddress;
}
