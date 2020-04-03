package com.biz.credit.service.impl;

import com.biz.credit.dao.*;
import com.biz.credit.domain.*;
import com.biz.credit.domain.responseData.BiInputDataRes;
import com.biz.credit.service.IReportTaskService;
import com.biz.credit.utils.DateUtil;
import com.biz.credit.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

@Service
@Slf4j
public class ReportTaskImpl implements IReportTaskService {

    @Resource
    private TaskDAO taskDAO;
    @Resource
    private InputFileDetailDAO inputFileDetailDAO;
    @Resource
    private InputFileDAO inputFileDAO;
    @Resource
    private ModuleTypeTemplateDAO moduleTypeTemplateDAO;
    @Resource
    private ModuleTypeDAO moduleTypeDAO;
    @Resource
    private IndustryInfoDAO industryInfoDAO;
    @Resource
    private ApiInputFileDetailDAO apiInputFileDetailDAO;
    @Autowired
    private InputFileDetailContactDAO contactDAO;
    @Autowired
    private StrategyDAO strategyDAO;

    @Override
    @Transactional
    public int addTask(TaskVO task) throws Exception {
        int count = taskDAO.addTask(task);
        InputFileVO inputFile = task.getInputFile();
        inputFile.setTaskId(task.getTaskId());
        inputFileDAO.addInputFile(inputFile);
        List<InputFileDetail> inputFileDetails = inputFile.getInputFileDetails();
        inputFileDetails.forEach(detail -> {
            detail.setTaskId(task.getTaskId());
            detail.setInputFileId(inputFile.getInputFileId());
        });
        inputFileDetailDAO.addInputFileDetails(inputFileDetails);
        for (InputFileDetail detail : inputFileDetails) {
            if (!CollectionUtils.isEmpty(detail.getParams())){
                inputFileDetailDAO.addInputFileParams(detail.getInputFileDetailId(), detail.getParams());
            }
            if (!CollectionUtils.isEmpty(detail.getContacts())) {
                contactDAO.addContacts(detail.getContacts());
                for (InputFileDetailContact contact : detail.getContacts()) {
                    contactDAO.insertContactParams(contact.getContactId(), contact.getParams());
                }
            }
        }
        return count;
    }

    @Override
    public int updateTask(Task task) {
        return taskDAO.updateTask(task);
    }

    @Override
    public List<Task> findTaskList(Task task) {
        return taskDAO.findTaskList(task);
    }

    @Override
    public int addInputFileDetail(InputFileDetail inputFileDetail) {
        return inputFileDetailDAO.addInputFileDetail(inputFileDetail);
    }

    @Override
    public int updateInputFileDetail(InputFileDetail inputFileDetail) {
        return inputFileDetailDAO.updateInputFileDetail(inputFileDetail);
    }

    @Override
    public List<InputFileDetailVO> findInputFileDetailList(InputFileDetailVO inputFileDetail) {
        return inputFileDetailDAO.findInputFileDetailList(inputFileDetail);
    }

    @Override
    public List<ModuleTypeTemplate> findModuleTypeTemplateList(ModuleTypeTemplate moduleTypeTemplate) {
        return moduleTypeTemplateDAO.findModuleTypeTemplateList(moduleTypeTemplate);
    }

    @Override
    public InputFileDetailVO findInputFileDetail(InputFileDetailVO inputFileDetail)  {
        if(Objects.nonNull(inputFileDetail.getInputFileDetailId())&&inputFileDetail.getInputFileDetailId()<0){
            return apiInputFileDetailDAO.findInputFileDetail(inputFileDetail);
        }
        return inputFileDetailDAO.findInputFileDetail(inputFileDetail);
    }

    @Override
    public Flow getInputFileDetailParams(Integer inputFileDetailId) {
        Flow flow = null;
        List<InputFileDetailParamVO> vos = inputFileDetailDAO.findInputFileDetailParams(inputFileDetailId);
        if (!CollectionUtils.isEmpty(vos)) {
            for (InputFileDetailParamVO vo : vos) {
                if (flow == null) {
                    flow = new Flow();
                    flow.setFlowId(vo.getFlowId());
                    flow.setFlowName(vo.getFlowName());
                    flow.setModuleTypeId(vo.getModuleTypeId());
                }
                Param param = new Param();
                param.setCode(vo.getCode());
                param.setName(vo.getName());
                param.setType(vo.getType());
                param.setRequired(vo.getRequired());
                param.setValue(vo.getValue() == null ? StringUtils.EMPTY : vo.getValue());
                flow.addParam(param);
            }
        }
        return flow;
    }

    @Override
    public InputFileDetailVO findInputFileDetailForApiInput(InputFileDetailVO inputFileDetail) {
        return apiInputFileDetailDAO.findInputFileDetail(inputFileDetail);
    }

    @Override
    public List<InputFileDetailVO> findInputFileDetailListByIds(String[] strings, Integer userId) {
        List<InputFileDetailVO> list = new ArrayList<>();
        for (int i = 0; i < strings.length; i++) {
            InputFileDetailVO vo = new InputFileDetailVO();
            vo.setUserId(userId);
            vo.setInputFileDetailId(Integer.parseInt(strings[i]));
            InputFileDetailVO inputFileDetail = inputFileDetailDAO.findInputFileDetail(vo);
            list.add(inputFileDetail);
        }
        return list;
    }

    @Override
    public List<InputFileDetailVO> findInputFileDetailByTaskId(InputFileDetailVO inputFileDetail) {
        return inputFileDetailDAO.findInputFileDetailByTaskId(inputFileDetail);
    }

    @Override
    public List<Task> queryTaskByName(TaskVO task) {
        return taskDAO.queryTaskByName(task);
    }

    @Override
    public List<InputFileDetail> queryTaskByInputDetailName(InputFileDetailVO inputFileDetail) {
        return inputFileDetailDAO.queryTaskByInputDetailName(inputFileDetail);
    }

    @Override
    public List<ModuleTypeTemplateVO> queryModuleTypeDetailById(ModuleTypeTemplateVO moduleTypeTemplate) {
        //注意 如果ModuleTypeTemplate为末级节点，那么parentCode你能为空
        moduleTypeTemplate.setIsLast(1);
        List<ModuleTypeTemplate> parentCodeList = moduleTypeTemplateDAO.findParentCodeList(moduleTypeTemplate);
        List<ModuleTypeTemplate> list = moduleTypeTemplateDAO.findModuleTypeTemplateList(moduleTypeTemplate);

        List<ModuleTypeTemplateVO> items = new ArrayList<>();

        for (int i = 0; i < parentCodeList.size(); i++) {
            ModuleTypeTemplate parentItem = parentCodeList.get(i);
            ModuleTypeTemplateVO vo = new ModuleTypeTemplateVO();
            vo.setModuleTypeName(parentItem.getModuleTypeName());
            vo.setTypeCode(parentItem.getTypeCode());
            vo.setParentCode(parentItem.getParentCode());
            vo.setApiCode(moduleTypeTemplate.getApiCode());
            vo.setModuleId(parentItem.getModuleId());
            vo.setReportType(parentItem.getReportType());
            List<ModuleTypeTemplate> childItems = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                ModuleTypeTemplate item = list.get(j);
                if (Objects.equals(item.getParentCode(),parentItem.getTypeCode())) {
                    childItems.add(item);
                }
            }
            vo.setChildItems(childItems);
            items.add(vo);
        }
        return items;
    }

    @Override
    public List<ModuleTypeVO> queryModuleTypeList(ModuleTypeVO moduleType) {
        return moduleTypeDAO.findModuleTypeList(moduleType);
    }

    @Override
    public List<Flow> getFlowList(String apiCode) {
        List<Flow> flows = new ArrayList<>();
        List<ParamVO> vos = strategyDAO.queryFlowParams(apiCode, null);
        if (!CollectionUtils.isEmpty(vos)) {
            Integer flowId = null;
            Flow flow = null;
            for (ParamVO vo : vos) {
                if (!Objects.equals(vo.getFlowId(), flowId)) {
                    flowId = vo.getFlowId();
                    flow = new Flow();
                    flow.setFlowId(flowId);
                    flow.setFlowName(vo.getFlowName());
                    flow.setModuleTypeId(vo.getModuleTypeId());
                    flows.add(flow);
                }
                Param param = new Param();
                param.setCode(vo.getCode());
                param.setName(vo.getName());
                param.setType(vo.getType());
                param.setRequired(vo.getRequired());
                flow.addParam(param);
            }
        }
        return flows;
    }

    @Override
    public ModuleTypeVO queryModuleType(ModuleTypeVO moduleTypeVO) {
        return moduleTypeDAO.findModuleTypeById(moduleTypeVO);
    }

    @Override
    public List<IndustryInfoVO> queryIndustryList(IndustryInfoVO industryInfoVO) {
        return industryInfoDAO.findListByIndustryInfoVO(industryInfoVO);
    }

    @Override
    public Task queryTaskById(Task task) {
        return taskDAO.queryTaskById(task);
    }

    @Override
    public int updateFailedInputFileDetail(InputFileDetailVO inputFileDetail) {
        return inputFileDetailDAO.updateFailedInputFileDetail(inputFileDetail);
    }

    @Override
    public List<Task> queryTaskListByCondition(BiInputDataVO biInputDataVO) {
        return taskDAO.queryTaskListByNameAndDate(biInputDataVO);
    }

}
