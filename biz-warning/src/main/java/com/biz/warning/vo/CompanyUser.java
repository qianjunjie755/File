package com.biz.warning.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompanyUser {
    private String apiCode;
    private Integer userId;
    private String userName;
    private String email;
    private String emailFrequency;
    private String sendTime;
}
