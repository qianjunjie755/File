package com.biz.credit.vo.reportApiVO;

import com.biz.credit.domain.ApiInputFileDetail;
import com.biz.credit.domain.ApiInputFileDetailContact;
import lombok.Data;

import java.util.List;

@Data
public class ApiInputFileDetailVO extends ApiInputFileDetail {
    private Integer moduleTypeId;
    private Integer groupId;
    private Integer userId;
    private Integer strategyId;
    private Integer taskId;
    private Integer taskType;//1- API_PDF 2-API_JSON 3-WEB_PDF 4-API_SCORE
    private Boolean isCheck=false;//公司是否存在
    private List<ApiInputFileDetailContact> contactList;
}
