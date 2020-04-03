package com.biz.credit.service;

import com.biz.credit.domain.*;
import com.biz.credit.vo.*;

import java.util.List;

public interface IReportTaskService{
    int addTask(TaskVO task) throws  Exception;
    int updateTask(Task task) throws  Exception;
    List<Task> findTaskList(Task task) throws  Exception;
    int addInputFileDetail(InputFileDetail inputFileDetail);
    int updateInputFileDetail(InputFileDetail inputFileDetail);
    List<InputFileDetailVO> findInputFileDetailList(InputFileDetailVO inputFileDetail);
    List<ModuleTypeTemplate> findModuleTypeTemplateList(ModuleTypeTemplate moduleTypeTemplate);
    InputFileDetailVO findInputFileDetail(InputFileDetailVO inputFileDetail);
    Flow getInputFileDetailParams(Integer inputFileDetailId);
    InputFileDetailVO findInputFileDetailForApiInput(InputFileDetailVO inputFileDetail);
    List<InputFileDetailVO> findInputFileDetailListByIds(String[] strings, Integer userId);
    List<InputFileDetailVO> findInputFileDetailByTaskId(InputFileDetailVO inputFileDetail);
    List<Task> queryTaskByName(TaskVO task);
    List<InputFileDetail> queryTaskByInputDetailName(InputFileDetailVO inputFileDetail);
    List<ModuleTypeTemplateVO> queryModuleTypeDetailById(ModuleTypeTemplateVO moduleTypeTemplate);
    List<ModuleTypeVO> queryModuleTypeList(ModuleTypeVO moduleType);
    List<Flow> getFlowList(String apiCode);
    ModuleTypeVO queryModuleType(ModuleTypeVO moduleTypeVO) ;
    List<IndustryInfoVO> queryIndustryList(IndustryInfoVO moduleType);
    Task queryTaskById(Task task);
    int updateFailedInputFileDetail(InputFileDetailVO inputFileDetail);

    List<Task> queryTaskListByCondition(BiInputDataVO biInputDataVO);
}
