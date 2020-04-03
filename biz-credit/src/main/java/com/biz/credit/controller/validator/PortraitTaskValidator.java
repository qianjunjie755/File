package com.biz.credit.controller.validator;

import com.biz.credit.domain.RespEntity;
import com.biz.credit.vo.PortraitTaskReqVO;
import com.biz.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.DataUtil;
import org.springframework.util.CollectionUtils;

import java.util.Date;

/**
 * 画像任务参数校验器
 */
public class PortraitTaskValidator {
    public static RespEntity addTaskValidate(PortraitTaskReqVO task) {
        RespEntity respEntity = RespEntity.error();
        if (task.getModuleId() == null || task.getModuleId() <= 0){
            return respEntity.setMsg("模块ID不能为空");
        }
        if (StringUtils.isBlank(task.getTaskName())){
            return respEntity.setMsg("任务名称不能为空");
        }
        if (task.getTaskType() == null){
            return respEntity.setMsg("任务类型不能为空");
        }else {
            if (task.getTaskType() != 1 && task.getTaskType() != 2){
                return respEntity.setMsg("任务类型不合法");
            }
            if (task.getTaskType() == 1){
                if (StringUtils.isBlank(task.getInterval())){
                    return respEntity.setMsg("任务固定频率不能为空");
                }
                task.setStartDate(null);
                task.setEndDate(null);
            }
            if (task.getTaskType() == 2 &&
                    (StringUtils.isBlank(task.getStartDate()) || StringUtils.isBlank(task.getEndDate()))){
                return respEntity.setMsg("任务有效起始日期和终止日期不能为空");
            }
            if (task.getTaskType() == 2){
                Date startDate = DateUtil.parseStrToDate(task.getStartDate(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
                if (startDate != null){
                    task.setStartDate(DateUtil.parseDateToStr(startDate, DateUtil.DATE_FORMAT_YYYY_MM_DD));
                }else {
                    return respEntity.setMsg("任务有效起始日期格式非法");
                }
                Date endDate = DateUtil.parseStrToDate(task.getEndDate(), DateUtil.DATE_FORMAT_YYYY_MM_DD);
                if (endDate != null){
                    task.setEndDate(DateUtil.parseDateToStr(endDate, DateUtil.DATE_FORMAT_YYYY_MM_DD));
                }else {
                    return respEntity.setMsg("任务有效终止日期格式非法");
                }
                task.setInterval(null);
            }
        }
        if (CollectionUtils.isEmpty(task.getLabelIds())){
            return respEntity.setMsg("标签列表不能为空");
        }
        return RespEntity.success();
    }
}
