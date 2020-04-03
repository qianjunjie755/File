package com.biz.credit.vo;

import com.biz.credit.domain.Task;
import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public class TaskVO extends Task implements Serializable {
    private InputFileVO inputFile;
    private String endTime;
    private String beginTime;
    private String type;
    private Integer groupId;
    private UploadLimitVO uploadLimitVO;
    private String htmlTemplateName;
    private Integer moduleTypeId;
    private Long flowId;
    private String detailJson;

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskVO task = (TaskVO) o;
        return Objects.equals(getTaskId(), task.getTaskId());
    }

    public int hashCode() {
        return Objects.hash(getTaskId());
    }


}
