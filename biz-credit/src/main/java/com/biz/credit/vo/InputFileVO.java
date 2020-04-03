package com.biz.credit.vo;

import com.biz.credit.domain.InputFile;
import com.biz.credit.domain.InputFileDetail;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InputFileVO extends InputFile implements Serializable {
    private List<InputFileDetail> inputFileDetails;
}
