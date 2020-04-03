package com.biz.credit.utils;

import lombok.Data;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

@Data
public class ReqUtil implements Serializable {
    private String reqHost;
    private String reqPort;
    private String reqUri;
    private String reqParam;
    private String prodCode;
    private String apiCode;

    public String gengerateReqUrl(){
        String url = StringUtils.EMPTY;

        return url;
    }
}
