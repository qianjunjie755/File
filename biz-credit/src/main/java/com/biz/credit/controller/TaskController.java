package com.biz.credit.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.*;
import com.biz.credit.service.*;
import com.biz.credit.utils.Constants;
import com.biz.credit.utils.ExcelUtil;
import com.biz.credit.utils.RedisUtil;
import com.biz.credit.utils.ZipUtils;
import com.biz.credit.vo.*;
import com.biz.decision.BizDecide;
import com.biz.decision.entity.Input;
import com.biz.decision.entity.Person;
import com.biz.decision.entity.RelatedPerson;
import com.biz.decision.entity.Task;
import com.biz.decision.enums.EReqType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/reportTask")
@Slf4j
public class TaskController {
    @Value("${upload_root_path}")
    private String uploadRootPath;
    @Value("${biz.task.old.taskInputSizeKey}")
    private String taskInputSizeKey;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IReportTaskService reportTaskService;
    @Autowired
    private IStrategyService strategyService;
    @Autowired
    private BizDecide bizDecide;
    @Autowired
    private ITaskService taskService;
    @Autowired
    private IListedCompanyService listedService;
    @Autowired
    private IDNodeParamsService nodeParamsService;
    @Autowired
    private RedisLockRegistry redisLockRegistry;


    @Value("${spring.profiles.active}")
    private String env;
    @Value("${biz.report.control-key-prefix}")
    private String controlKeyPrefix;
    @Value("${biz.report.control-key-apicode-prefix}")
    private String controlKeyApiCodePrefix;
    @Value("${biz.report.control-key-user-Prefix}")
    private String controlKeyUserPrefix;
    @Value("${biz.report.upload-key-prefix}")
    private String uploadKeyPrefix;



    @RequestMapping(value = "/downloadSignalTest")
    public void downloadSignal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String finalFileName = null;

        //解决各个浏览器中文名称乱码问题
        boolean isMSIE = isMSBrowser(request);
        if (isMSIE) {
            //IE浏览器的乱码问题解决
            finalFileName = URLEncoder.encode("", "UTF-8");
        } else {
            //万能乱码问题解决
            finalFileName = new String("中山市鹏飞电器有限公司".getBytes("UTF-8"), "ISO-8859-1");
        }

        headers.set("Content-Disposition", "attachment; filename=\"" + finalFileName + ".pdf\"");
        headers.setContentType(MediaType.TEXT_HTML);
        FileCopyUtils.copy(new FileInputStream("/var/www/htmltopdf/html/7939.html"), response.getOutputStream());
    }
    public boolean isMSBrowser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        for (String signal : Constants.IEBrowserSignals) {
            if (userAgent.contains(signal))
                return true;
        }
        return false;
    }

    /**
     *新增任务
     */
    @PostMapping("/taskNew")
    public RespEntity addFlowTask(@RequestBody ReqParam reqParam, HttpSession session){
        RespEntity ret = new RespEntity(RespCode.WARN,null);
        if (reqParam.getFlowId() == null || reqParam.getModuleTypeId() == null) {
            ret = RespEntity.error();
            ret.setMsg("决策流ID不能为空!!");
            return ret;
        }

        String userId = session.getAttribute(Constants.USER_ID).toString();
        String userType = session.getAttribute(Constants.USER_TYPE).toString();
        String apiCode = session.getAttribute(Constants.API_CODE).toString();
        String groupId = session.getAttribute("groupId").toString();
        String successCount = NumberUtils.INTEGER_ONE.toString();
        String errorCount = NumberUtils.INTEGER_ZERO.toString();
        String taskUploadKey = "risk-credit:credit:task:upload:";
        long size = 1;
        TaskVO task = new TaskVO();
        task.setFlowId(reqParam.getFlowId().longValue());
        task.setModuleTypeId(reqParam.getModuleTypeId());
        task.setTaskType(reqParam.getTaskType());
        if(Objects.equals(reqParam.getTaskType(), 1)){
            successCount = session.getAttribute("successCount").toString();
            errorCount = session.getAttribute("errorCount").toString();
            taskUploadKey = session.getAttribute("taskUploadKey").toString();
            task.setTaskCode(session.getAttribute("reportTaskCode").toString());
            task.setTaskName(reqParam.getTaskName());
            size = stringRedisTemplate.opsForList().size(taskUploadKey);
        } else {
            String taskCode = redisUtil.generateCodeNo("RTC");
            task.setTaskCode(taskCode);
        }
        if (null == task.getIndustryId()) {
            task.setIndustryId(-1);
        }
        //数量校验，判断是否已超过最大进件数
        {
            String totalLimitKey = controlKeyPrefix.concat(apiCode).concat(":total");
            String totalUserLimitKey = totalLimitKey.concat(":").concat(userId);
            String groupLimitKey = controlKeyPrefix.concat(apiCode).concat(":group:").concat(String.valueOf(groupId));
            int limitType = StringUtils.equals("0", userType) ? 2 : stringRedisTemplate.opsForZSet().score(totalLimitKey, "limitType").intValue();
            int interval = stringRedisTemplate.opsForZSet().score(totalLimitKey, "interval").intValue();
            int groupInterval = StringUtils.equals("0", userType) ? 30 : stringRedisTemplate.opsForZSet().score(groupLimitKey, "interval").intValue();
            int userInterval = stringRedisTemplate.opsForZSet().score(totalLimitKey, "userInterval").intValue();
            int limitCount = stringRedisTemplate.opsForZSet().score(totalLimitKey, "limitCount").intValue();
            int userLimitCount = stringRedisTemplate.opsForZSet().score(totalLimitKey, "userLimitCount").intValue();
            int groupLimitCount = StringUtils.equals("0", userType) ? limitCount : stringRedisTemplate.opsForZSet().score(groupLimitKey, "uploadLimit").intValue();
            DateTime dateTime = DateTime.now();
            int month = dateTime.getMonthOfYear();
            int day = dateTime.getDayOfMonth();
            long totalCount, count;
            if (1 == limitType && !StringUtils.equals("0", userType)) {
                Double tmpCount = stringRedisTemplate.opsForZSet().score(groupLimitKey, groupInterval == 30 ? (month + "mg") : (day + "dg"));
                count = tmpCount == null ? 0 : tmpCount.longValue();
                String suffix = 30 == interval ? (month + "mg") : (day + "dg");
                Double totalCountTmp = stringRedisTemplate.opsForZSet().score(controlKeyPrefix.concat(apiCode).concat(":total"), suffix);
                totalCount = totalCountTmp == null ? 0 : totalCountTmp.intValue();
            } else {
                Double tmpCount = StringUtils.equals("0", userType) ? Integer.MIN_VALUE : stringRedisTemplate.opsForZSet().score(totalUserLimitKey, userInterval == 30 ? (month + "mu") : (day + "du"));
                count = tmpCount == null ? 0 : tmpCount.longValue();
                String suffix = 30 == interval ? (month + "mu") : (day + "du");
                Double totalCountTmp = StringUtils.equals("0", userType) ? stringRedisTemplate.opsForZSet().score(controlKeyApiCodePrefix.concat(apiCode), dateTime.toString("yyyyMM")) : stringRedisTemplate.opsForZSet().score(controlKeyPrefix.concat(apiCode).concat(":total"), suffix);
                totalCount = totalCountTmp == null ? 0 : totalCountTmp.intValue();
            }

            long totalRemain = limitCount - totalCount;
            long remain = (1 == limitType ? groupLimitCount : userLimitCount) - count;
            Double apiCodeLimitTmp = stringRedisTemplate.opsForZSet().score(controlKeyApiCodePrefix.concat(apiCode), "monthLimit");
            long apiCodeLimit = apiCodeLimitTmp == null ? 0 : apiCodeLimitTmp.longValue();
            Double apiCodeUploadTmp = stringRedisTemplate.opsForZSet().score(controlKeyApiCodePrefix.concat(apiCode), dateTime.toString("yyyyMM"));
            long apiCodeUpload = apiCodeUploadTmp == null ? 0 : apiCodeUploadTmp.longValue();
            if (apiCodeLimit > 0 && (apiCodeUpload + size) > apiCodeLimit) {
                JSONObject errJo = new JSONObject();
                errJo.put("remainCount", remain);
                errJo.put("totalRemainCount", totalRemain);
                errJo.put("successCount", size);
                errJo.put("limitType", limitType);
                errJo.put("interval", interval);
                errJo.put("usrOrGroupInterval", 1 == limitType ? groupInterval : userInterval);
                errJo.put("sysRemainCount", apiCodeLimit - apiCodeUpload);
                ret.changeRespEntity(RespCode.SYS_LIMIT_WRONG, errJo);
                return ret;
            }
            if (totalRemain < size || remain < size) {
                JSONObject errJo = new JSONObject();
                errJo.put("remainCount", remain);
                errJo.put("totalRemainCount", totalRemain);
                errJo.put("successCount", size);
                errJo.put("limitType", limitType);
                errJo.put("interval", interval);
                errJo.put("usrOrGroupInterval", 1 == limitType ? groupInterval : userInterval);
                errJo.put("sysRemainCount", apiCodeLimit - apiCodeUpload);
                ret.changeRespEntity(totalRemain < size ? RespCode.TOTAL_LIMIT_WRONG : RespCode.LIMIT_WRONG, errJo);
                return ret;
            }
            stringRedisTemplate.opsForZSet().incrementScore(totalLimitKey, day + "dg", size);
            stringRedisTemplate.opsForZSet().incrementScore(totalLimitKey, day + "du", size);
            stringRedisTemplate.opsForZSet().incrementScore(totalLimitKey, month + "mg", size);
            stringRedisTemplate.opsForZSet().incrementScore(totalLimitKey, month + "mu", size);
            stringRedisTemplate.opsForZSet().incrementScore(totalUserLimitKey, day + "dg", size);
            stringRedisTemplate.opsForZSet().incrementScore(totalUserLimitKey, day + "du", size);
            stringRedisTemplate.opsForZSet().incrementScore(totalUserLimitKey, month + "mg", size);
            stringRedisTemplate.opsForZSet().incrementScore(totalUserLimitKey, month + "mu", size);
            if (!StringUtils.equals("0", groupId)) {
                stringRedisTemplate.opsForZSet().incrementScore(groupLimitKey, day + "dg", size);
                stringRedisTemplate.opsForZSet().incrementScore(groupLimitKey, day + "du", size);
                stringRedisTemplate.opsForZSet().incrementScore(groupLimitKey, month + "mg", size);
                stringRedisTemplate.opsForZSet().incrementScore(groupLimitKey, month + "mu", size);
            }
            stringRedisTemplate.opsForZSet().incrementScore(controlKeyApiCodePrefix.concat(apiCode), dateTime.toString("yyyyMMdd"), size);
            stringRedisTemplate.opsForZSet().incrementScore(controlKeyApiCodePrefix.concat(apiCode), dateTime.toString("yyyyMM"), size);
            stringRedisTemplate.opsForZSet().incrementScore(controlKeyUserPrefix.concat(":").concat(apiCode).concat(":").concat(userId), dateTime.toString("yyyyMMdd"), size);
            stringRedisTemplate.opsForZSet().incrementScore(controlKeyUserPrefix.concat(userId), dateTime.toString("yyyyMM"), size);
        }
        List<InputFileDetail> companyList = new ArrayList<>();
        DateTime detailDateTime = DateTime.now();
        String detailDate = detailDateTime.toString("yyyy-MM-dd");
        Integer detailMonth = Integer.parseInt(detailDateTime.toString("yyyyMM"));
        Integer detailYear = detailMonth/100;
        //
        List<ParamVO> flowParams = strategyService.getFlowParams(apiCode, reqParam.getFlowId());
        if(Objects.equals(task.getTaskType(), 1)){
            List<String> list = stringRedisTemplate.opsForList().range(taskUploadKey, 0, size);
            if (CollectionUtils.isEmpty(flowParams)) {
                ret = RespEntity.error();
                ret.setMsg("决策流参数信息未获取到!!");
                return ret;
            }
            list.forEach(com->{
                JSONObject detail = JSONObject.parseObject(com);
                InputFileDetail company = new InputFileDetail();
                company.setKeyNo(detail.getString(Constants.COMPANY_NAME));
                company.setCellPhone(detail.getString(Constants.CELL));
                company.setName(detail.getString(Constants.NAME));
                company.setIdNumber(detail.getString(Constants.ID_NO));
                company.setCreditCode(detail.getString(Constants.CREDIT_CODE));
                company.setBankId(detail.getString(Constants.BANK_ID));
                company.setHomeAddr(detail.getString(Constants.HOME_ADDR));
                company.setBizAddr(detail.getString(Constants.WORK_ADDR));
                company.setCreateTime(detailDateTime.toString("yyyy-MM-dd HH:mm:ss"));
                company.setLastUpdateTime(detailDateTime.toString("yyyy-MM-dd HH:mm:ss"));
                company.setDate(detailDate);
                company.setMonth(detailMonth);
                company.setYear(detailYear);
                String strParams = detail.getString("_params_");
                List<Param> params = JSONArray.parseArray(strParams, Param.class);
                company.setParams(params);
                /*for (ParamVO vo : flowParams) {
                    Param param = new Param();
                    param.setCode(vo.getCode());
                    param.setName(vo.getName());
                    param.setType(vo.getType());
                    param.setRequired(vo.getRequired());
                    param.setValue(detail.getString(vo.getCode()));
                    company.addParam(param);
                }*/
                /*List<JSONObject> relatedParams = reqParam.getRelatedParams();
                if (!CollectionUtils.isEmpty(relatedParams)) {
                    for (JSONObject rParam : relatedParams) {
                        InputFileDetailContact contact = new InputFileDetailContact();
                        contact.setName(rParam.getString(Constants.CELL));
                        contact.setIdNumber(rParam.getString(Constants.NAME));
                        contact.setCellPhone(rParam.getString(Constants.ID_NO));
                        contact.setHomeAddr(rParam.getString(Constants.HOME_ADDR));
                        contact.setBizAddr(rParam.getString(Constants.WORK_ADDR));
                        for (ParamVO vo : flowParams) {
                            //必须是个人参数
                            if (Objects.equals(vo.getType(), 2)) {
                                Param param = new Param();
                                param.setCode(vo.getCode());
                                param.setName(vo.getName());
                                param.setType(vo.getType());
                                param.setRequired(vo.getRequired());
                                param.setValue(rParam == null ? null : rParam.getString(vo.getCode()));
                                contact.addParam(param);
                            }
                        }
                        //个人参数不为空则添加关联人参数信息
                        if (!CollectionUtils.isEmpty(contact.getParams())) {
                            company.addInputContact(contact);
                        }
                    }
                }*/
                companyList.add(company);
            });
            stringRedisTemplate.expire(taskUploadKey,24*3600,TimeUnit.SECONDS);
        }else{
            JSONObject params = reqParam.getParams();
            if (params == null) {
                params = new JSONObject();
            }
            InputFileDetail company = new InputFileDetail();
            company.setKeyNo(params.getString(Constants.COMPANY_NAME));
            company.setCellPhone(params.getString(Constants.CELL));
            company.setName(params.getString(Constants.NAME));
            company.setIdNumber(params.getString(Constants.ID_NO));
            company.setCreditCode(params.getString(Constants.CREDIT_CODE));
            company.setBankId(params.getString(Constants.BANK_ID));
            company.setHomeAddr(params.getString(Constants.HOME_ADDR));
            company.setBizAddr(params.getString(Constants.WORK_ADDR));
            company.setCreateTime(detailDateTime.toString("yyyy-MM-dd HH:mm:ss"));
            company.setLastUpdateTime(detailDateTime.toString("yyyy-MM-dd HH:mm:ss"));
            company.setDate(detailDate);
            company.setMonth(detailMonth);
            company.setYear(detailYear);
            for (ParamVO vo : flowParams) {
                Param param = new Param();
                param.setCode(vo.getCode());
                param.setName(vo.getName());
                param.setType(vo.getType());
                param.setRequired(vo.getRequired());
                param.setValue(params == null ? null : params.getString(vo.getCode()));
                company.addParam(param);
            }
            List<JSONObject> relatedParams = reqParam.getRelatedParams();
            if (!CollectionUtils.isEmpty(relatedParams)) {
                for (JSONObject rParam : relatedParams) {
                    InputFileDetailContact contact = new InputFileDetailContact();
                    contact.setName(rParam.getString(Constants.CELL));
                    contact.setIdNumber(rParam.getString(Constants.NAME));
                    contact.setCellPhone(rParam.getString(Constants.ID_NO));
                    contact.setHomeAddr(rParam.getString(Constants.HOME_ADDR));
                    contact.setBizAddr(rParam.getString(Constants.WORK_ADDR));
                    for (ParamVO vo : flowParams) {
                        //必须是个人参数
                        if (Objects.equals(vo.getType(), 2)) {
                            Param param = new Param();
                            param.setCode(vo.getCode());
                            param.setName(vo.getName());
                            param.setType(vo.getType());
                            param.setRequired(vo.getRequired());
                            param.setValue(rParam == null ? null : rParam.getString(vo.getCode()));
                            contact.addParam(param);
                        }
                    }
                    //个人参数不为空则添加关联人参数信息
                    if (!CollectionUtils.isEmpty(contact.getParams())) {
                        company.addInputContact(contact);
                    }
                }
            }
            companyList.add(company);
            task.setTaskName(StringUtils.isNotEmpty(company.getKeyNo())?company.getKeyNo():company.getName());
        }
        InputFileVO inputFile = new InputFileVO();
        inputFile.setUserId(Integer.parseInt(userId));
        inputFile.setInputFileDetails(companyList);
        inputFile.setSuccessCount(Integer.parseInt(successCount));
        inputFile.setFailCount(Integer.parseInt(errorCount));
        task.setInputFile(inputFile);
        task.setGroupId(Integer.parseInt(groupId));
        task.setUserId(inputFile.getUserId());
        task.setUserType(Integer.parseInt(userType));
        task.setApiCode(apiCode);
        Lock lock = redisLockRegistry.obtain("taskAdd:" + userId);
        lock.lock();
        try {
            reportTaskService.addTask(task);
            ModuleTypeVO moduleType = reportTaskService.queryModuleType(new ModuleTypeVO(task.getModuleTypeId()));
            String prodCode = moduleType.getProdCode();
            String prodName = moduleType.getProdName();
            String appType = StringUtils.EMPTY;
            if (StringUtils.contains(moduleType.getProdCode(), "_")) {
                prodCode = moduleType.getProdCode().substring(0, moduleType.getProdCode().lastIndexOf("_"));
                appType = moduleType.getProdCode().substring(moduleType.getProdCode().lastIndexOf("_") + 1);
            }
            Task decideTask = new Task();
            decideTask.setTaskId(task.getTaskId());
            decideTask.setFlowId(moduleType.getFlowId().intValue());
            decideTask.setApiCode(apiCode);
            decideTask.setReqType(EReqType.WEB_PDF);
            decideTask.setTaskName(task.getTaskName());
            decideTask.setProdCode(prodCode);
            decideTask.setProdName(prodName);
            decideTask.setAppType(appType);
            decideTask.setUserId(Integer.parseInt(userId));
            decideTask.setUserGroupId(Objects.equals("0", userType) ? -1 : Integer.parseInt(groupId));
            task.getInputFile().getInputFileDetails().forEach(entity -> {
                Input taskInput = new Input();
                //企业参数
                taskInput.setInputId(entity.getInputFileDetailId());
                taskInput.setCompanyName(entity.getKeyNo());
                taskInput.setCreditCode(entity.getCreditCode());
                if (StringUtils.isNotBlank(entity.getKeyNo())) {
                    String stockCode = listedService.getStockCode(entity.getKeyNo());
                    if (StringUtils.isNotBlank(stockCode)) {
                        taskInput.addParam(Constants.STOCK_CODE, stockCode);
                    }
                }
                List<Param> params = entity.getParams();
                if (!CollectionUtils.isEmpty(params)) {
                    for (Param param : params) {
                        if (Objects.equals(param.getType(), 1)) {
                            taskInput.addParam(param.getCode(), param.getValue());
                        }
                    }
                }
                //个人参数
                int count = 0;
                Person person = new Person();
                person.setName(entity.getName());
                person.setIdNo(entity.getIdNumber());
                person.setTelNo(entity.getCellPhone());
                if (!CollectionUtils.isEmpty(params)) {
                    for (Param param : params) {
                        if (Objects.equals(param.getType(), 2)) {
                            if (StringUtils.isNotBlank(param.getValue())) {
                                count++;
                            }
                            person.addParam(param.getCode(), param.getValue());
                        }
                    }
                }
                //个人有效参数大于0
                if (count > 0) {
                    taskInput.setPerson(person);
                }
                //关联人参数
                List<InputFileDetailContact> contacts = entity.getContacts();
                if (!CollectionUtils.isEmpty(contacts)) {
                    for (InputFileDetailContact contact : contacts) {
                        count = 0;
                        RelatedPerson relatedPerson = new RelatedPerson();
                        relatedPerson.setId(contact.getContactId());
                        relatedPerson.setName(contact.getName());
                        relatedPerson.setIdNo(contact.getIdNumber());
                        relatedPerson.setTelNo(contact.getCellPhone());
                        params = contact.getParams();
                        if (!CollectionUtils.isEmpty(params)) {
                            for (Param param : params) {
                                if (Objects.equals(param.getType(), 2)) {
                                    if (StringUtils.isNotBlank(param.getValue())) {
                                        count++;
                                    }
                                    relatedPerson.addParam(param.getCode(), param.getValue());
                                }
                            }
                        }
                        //关联人有效参数大于0
                        if (count > 0) {
                            taskInput.addRelatedPerson(relatedPerson);
                        }
                    }
                }
                decideTask.addInput(taskInput);
            });
            log.info("entityList:" + task.getInputFile().getInputFileDetails().size());
            log.info("seTask.getTaskInputs():" + decideTask.getInputs().size());
            if (!CollectionUtils.isEmpty(decideTask.getInputs())) {
                bizDecide.issue(decideTask);
                JSONObject jo = new JSONObject();
                jo.put("taskId", task.getTaskId());
                ret.changeRespEntity(RespCode.SUCCESS, jo);
            }
            stringRedisTemplate.opsForZSet().add(taskInputSizeKey, task.getTaskId().toString(), task.getInputFile().getInputFileDetails().size());
            stringRedisTemplate.expire(taskInputSizeKey,24*3600,TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }finally {
            lock.unlock();
        }
        return ret;
    }


    @PostMapping("/inputFile")
    public RespEntity uploadInputFile(@RequestParam("file")MultipartFile file, TaskVO taskVO, HttpSession session) {
        Object taskUploadKeyObj = session.getAttribute("taskUploadKey");
        if (null != taskUploadKeyObj) {
            session.removeAttribute("reportErrorPath");
            session.removeAttribute("uploadExcelFileName");
            session.removeAttribute("taskUploadKey");
            session.removeAttribute("successCount");
            session.removeAttribute("errorCount");
            session.removeAttribute("reportTaskCode");
            stringRedisTemplate.delete(taskUploadKeyObj.toString());
        }
        String userId = session.getAttribute(Constants.USER_ID).toString();
        String userType = session.getAttribute(Constants.USER_TYPE).toString();
        String apiCode = session.getAttribute(Constants.API_CODE).toString();
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String taskCode = redisUtil.generateCodeNo("RTC");
        session.setAttribute("reportTaskCode", taskCode);
        taskVO.setTaskCode(taskCode);
        String taskUploadKey = uploadKeyPrefix.concat(taskVO.getTaskCode());
        try {

            //根据moduleTypeId查moduleType
            ModuleTypeVO mtv = new ModuleTypeVO();
            mtv.setModuleTypeId(taskVO.getModuleTypeId());
            if (null == taskVO.getIndustryId()) {
                if (null != taskVO.getFlowId()) {
                    taskVO.setIndustryId(-2);
                } else {
                    taskVO.setIndustryId(1);
                }
            }
            ModuleTypeVO tmpVo = reportTaskService.queryModuleType(mtv);
            List<String> requiredHead = new ArrayList<>();
            List<String> requiredHeadPerson = new ArrayList<>();
            if (null != tmpVo.getFlowId() && tmpVo.getFlowId() > 0) {
                DFlowVO query = new DFlowVO();
                query.setFlowId(tmpVo.getFlowId());
                query.setApiCode(apiCode);
                DTaskVO dTaskVO = nodeParamsService.findDTaskVOByDFlowVO(query);
                StringBuffer columnHeadSBF = new StringBuffer();
                StringBuffer columnHeadPersonSBF = new StringBuffer();

                dTaskVO.getParamVOList().forEach(param -> {
                    if (param.getRequired().equals("1")) {
                        requiredHead.add(param.getKey());
                    } else {
                        requiredHead.add("0");
                    }
                    columnHeadSBF.append(param.getKey()).append("_");
                });
                dTaskVO.getRelatedParamVOList().forEach(param -> {
                    /*if(param.getRequired().equals("1")){
                        requiredHeadPerson.add(param.getKey());
                    }else{
                        requiredHeadPerson.add("0");
                    }*/
                    requiredHeadPerson.add("0");
                    columnHeadPersonSBF.append(param.getKey()).append("_");
                });
                if (columnHeadSBF.length() > 0) {
                    tmpVo.setColumnHead(columnHeadSBF.substring(0, columnHeadSBF.length() - 1));
                }
                if (columnHeadPersonSBF.length() > 0) {
                    tmpVo.setColumnHeadPerson(columnHeadPersonSBF.substring(0, columnHeadPersonSBF.length() - 1));
                }
            }
            //去除企业开户账户
            if (null != tmpVo && StringUtils.isNotEmpty(tmpVo.getColumnHead())) {
                tmpVo.setColumnHead(tmpVo.getColumnHead().replaceAll("_".concat(Constants.bankId), StringUtils.EMPTY));
            }
            List<String> headList = new ArrayList<>();
            if (StringUtils.isNotEmpty(tmpVo.getColumnHead())) {
                //根据moduleTypeId查询ColumnHead
                headList = new ArrayList<>(Arrays.asList(tmpVo.getColumnHead().split("_")));
            }
            List<String> headRelatedList = new ArrayList<>();
            if (StringUtils.isNotEmpty(tmpVo.getColumnHeadPerson())) {
                headRelatedList = new ArrayList<>(Arrays.asList(tmpVo.getColumnHeadPerson().split("_")));
            }
            JSONObject jo = new JSONObject();
            //jo.put("uploadResult",true);
            InputStream is = file.getInputStream();
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            Workbook wb = ExcelUtil.getWorkBook(suffix, is);
            Map<Integer, String> headerMap = new HashMap<>();
            List<String[]> companyList = ExcelUtil.getExcelData(wb, jo, headList, headRelatedList, headerMap, requiredHead, requiredHeadPerson);
            if (jo.containsKey("templateError")) {
                jo.put("errorMsg", "上传模板入参不正确，请重新下载模板，并核对excel模板入参。要求按顺序入参:".concat(headList.toString().replaceAll("\\[|\\]", "")));
                ret.changeRespEntity(RespCode.TEMPLATE_NAME_WRONG, jo);
                return ret;
            }
            jo.put("successCount", companyList.size());
            List<List<String>> retCompanyList = new ArrayList<>();
            if (jo.containsKey("uploadResult")) {
                String errorExcelFileName = UUID.randomUUID().toString().concat(".").concat(suffix);
                String errorExcelPath = uploadRootPath.concat("/error/").concat(errorExcelFileName);
                session.setAttribute("reportErrorPath", errorExcelPath);
                session.setAttribute("uploadExcelFileName", file.getOriginalFilename());
                OutputStream os = FileUtils.openOutputStream(new File(errorExcelPath));
                wb.write(os);
            } else {
                jo.put("errorCount", 0);
                jo.put("uploadResult", true);
            }
            if (!CollectionUtils.isEmpty(companyList)) {
                List<String> headerList = new ArrayList<>();
                for (int i = 0; i < headerMap.size(); i++) {
                    headerList.add(headerMap.get(i));
                }
                //retCompanyList.add(headerList);


                for (String[] comp : companyList) {
                    JSONObject detail = new JSONObject();
                    List<String> relatedInfos = new ArrayList<>(Arrays.asList(comp));
                    for (int i = 0; i < comp.length; i++) {
                        String key = headerMap.get(i);
                        if (Constants.companyName.equals(key)) {
                            detail.put("keyNo", comp[i]);
                            relatedInfos.remove(comp[i]);
                        } else if (Constants.idNumber.equals(key)
                                || Constants.applyIdNumber.equals(key)) {
                            detail.put("idNumber", comp[i]);
                            relatedInfos.remove(comp[i]);
                        } else if (Constants.legalPerson.equals(key)
                                || Constants.applyLegalPerson.equals(key)) {
                            detail.put("name", comp[i]);
                            relatedInfos.remove(comp[i]);
                        } else if (Constants.cellPhone.equals(key)
                                || Constants.applyCellPhone.equals(key)) {
                            detail.put("cellPhone", comp[i]);
                            relatedInfos.remove(comp[i]);
                        } else if (Constants.creditCode.equals(key)) {
                            detail.put("creditCode", comp[i]);
                            relatedInfos.remove(comp[i]);
                        } else if (Constants.bankId.equals(key)) {
                            detail.put("bankId", comp[i]);
                            relatedInfos.remove(comp[i]);
                        } else if (Constants.homeAddress.equals(key)
                                || Constants.applyHomeAddress.equals(key)) {
                            detail.put("homeAddr", comp[i]);
                            relatedInfos.remove(comp[i]);
                        } else if (Constants.bizAddress.equals(key)
                                || Constants.applyBizAddress.equals(key)) {
                            detail.put("bizAddr", comp[i]);
                            relatedInfos.remove(comp[i]);
                        }
                    }
                    if (StringUtils.isNotEmpty(tmpVo.getColumnHeadPerson())) {
                        String[] headArr = tmpVo.getColumnHeadPerson().split("_");
                        int propSize = tmpVo.getColumnHeadPerson().split("_").length;
                        int personSize = relatedInfos.size() / propSize;
                        JSONArray relatedPeople = new JSONArray();
                        for (int i = 0; i < relatedInfos.size(); i = i + propSize) {
                            List<String> props = relatedInfos.subList(i, i + propSize);
                            JSONArray items = new JSONArray();
                            for (int j = 0; j < props.size(); j++) {
                                JSONObject item = new JSONObject();
                                item.put("key", headArr[j]);
                                item.put("value", props.get(j));
                                item.put("required", 0);
                                items.add(item);
                            }
                            relatedPeople.add(items);
                        }
                        detail.put("relatedPeople", relatedPeople);
                    }


                    if (retCompanyList.size() < 6) {
                        retCompanyList.add(Arrays.asList(comp));
                    }
                    stringRedisTemplate.opsForList().rightPush(taskUploadKey, detail.toJSONString());
                }
                jo.put("headList", headerList);
            }
            stringRedisTemplate.expire(taskUploadKey, 48 * 3600, TimeUnit.SECONDS);
            session.setAttribute("taskUploadKey", taskUploadKey);
            jo.put("companyList", retCompanyList);

            ret.changeRespEntity(jo.getBooleanValue("uploadResult") ? RespCode.SUCCESS : RespCode.UPLOAD_WRONG, jo);
            session.setAttribute("successCount", jo.getString("successCount"));
            session.setAttribute("errorCount", jo.getString("errorCount"));


        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ret;
    }


    @PostMapping("/uploadInputFile")
    public RespEntity uploadInputFile(@RequestParam("file") MultipartFile file,
                                      @RequestParam("flowId") Integer flowId,
                                      HttpSession session) {
        Object taskUploadKeyObj = session.getAttribute("taskUploadKey");
        if (null != taskUploadKeyObj) {
            session.removeAttribute("reportErrorPath");
            session.removeAttribute("uploadExcelFileName");
            session.removeAttribute("taskUploadKey");
            session.removeAttribute("successCount");
            session.removeAttribute("errorCount");
            session.removeAttribute("reportTaskCode");
            stringRedisTemplate.delete(taskUploadKeyObj.toString());
        }
        String apiCode = session.getAttribute(Constants.API_CODE).toString();
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String taskCode = redisUtil.generateCodeNo("RTC");
        session.setAttribute("reportTaskCode", taskCode);
        //
        String taskUploadKey = uploadKeyPrefix.concat(taskCode);
        try {
            List<ParamVO> params = strategyService.getFlowParams(apiCode, flowId);
            if (CollectionUtils.isEmpty(params)) {
                ret = RespEntity.error();
                ret.setMsg("未获取到决策流参数!!");
                return ret;
            }
            //企业参数数量
            long count = params.stream().filter(v -> Objects.equals(v.getType(), 1)).count();
            //
            List<String> headList = new ArrayList<>();
            List<String> requiredHead = new ArrayList<>();
            //企业+法人/申请人参数
            if (!CollectionUtils.isEmpty(params)) {
                for (ParamVO vo : params) {
                    if (Objects.equals(vo.getType(), 2)) {
                        String name;
                        //有企业参数, 则个人参数添加法人前缀
                        if (count > 0) {
                            name = "法人" + vo.getName();
                        }
                        //无企业参数, 则个人参数添加申请人前缀
                        else {
                            name = "申请人" + vo.getName();
                        }
                        vo.setName(name);
                        headList.add(name);
                    } else {
                        headList.add(vo.getName());
                    }
                    //
                    if (Objects.equals(vo.getRequired(), 1)) {
                        requiredHead.add(vo.getName());
                    } else {
                        requiredHead.add("0"); //?
                    }
                }
            }
            JSONObject jo = new JSONObject();
            InputStream is = file.getInputStream();
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            Workbook wb = ExcelUtil.getWorkBook(suffix, is);
            Map<Integer, String> headerMap = new HashMap<>();
            List<String[]> companyList = ExcelUtil.getExcelData(wb, jo, (count > 0), headList, headerMap, requiredHead);
            if (jo.containsKey("templateError")) {
                jo.put("errorMsg", "上传模板入参不正确，请重新下载模板，并核对excel模板入参。要求按顺序入参:".concat(headList.toString().replaceAll("\\[|\\]", "")));
                ret.changeRespEntity(RespCode.TEMPLATE_NAME_WRONG, jo);
                return ret;
            }
            jo.put("successCount", companyList.size());
            List<List<String>> retCompanyList = new ArrayList<>();
            if (jo.containsKey("uploadResult")) {
                String errorExcelFileName = UUID.randomUUID().toString().concat(".").concat(suffix);
                String errorExcelPath = uploadRootPath.concat("/error/").concat(errorExcelFileName);
                session.setAttribute("reportErrorPath", errorExcelPath);
                session.setAttribute("uploadExcelFileName", file.getOriginalFilename());
                OutputStream os = FileUtils.openOutputStream(new File(errorExcelPath));
                wb.write(os);
            } else {
                jo.put("errorCount", 0);
                jo.put("uploadResult", true);
            }
            if (!CollectionUtils.isEmpty(companyList)) {
                ParamVO[] header = new ParamVO[headerMap.size()];
                for (ParamVO vo : params) {
                    String name = vo.getName();
                    for (int i = 0; i < headerMap.size(); i++) {
                        if (Objects.equals(name, headerMap.get(i))) {
                            header[i] = vo;
                            break;
                        }
                    }
                }
                //
                List<String> headerList = new ArrayList<>();
                for (ParamVO vo : header) {
                    headerList.add(vo.getName());
                }
                //
                for (String[] comp : companyList) {
                    JSONObject detail = new JSONObject();
                    List<Param> list = new ArrayList<>();
                    for (int i = 0; i < comp.length; i++) {
                        ParamVO vo = header[i];
                        Param param = new Param();
                        param.setCode(vo.getCode());
                        param.setName(vo.getName());
                        param.setType(vo.getType());
                        param.setRequired(vo.getRequired());
                        param.setValue(comp[i]);
                        list.add(param);
                        detail.put(vo.getCode(), comp[i]);
                    }
                    detail.put("_params_", list);
                    //用作前端预览展示
                    if (retCompanyList.size() < 6) {
                        retCompanyList.add(Arrays.asList(comp));
                    }
                    stringRedisTemplate.opsForList().rightPush(taskUploadKey, detail.toJSONString());
                }
                jo.put("headList", headerList);
            }
            stringRedisTemplate.expire(taskUploadKey, 48 * 3600, TimeUnit.SECONDS);
            session.setAttribute("taskUploadKey", taskUploadKey);
            jo.put("companyList", retCompanyList);

            ret.changeRespEntity(jo.getBooleanValue("uploadResult") ? RespCode.SUCCESS : RespCode.UPLOAD_WRONG, jo);
            session.setAttribute("successCount", jo.getString("successCount"));
            session.setAttribute("errorCount", jo.getString("errorCount"));
        } catch (IOException e) {
            log.error("批量进件读取Excel异常: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("批量进件异常: " + e.getMessage(), e);
        }
        return ret;
    }

    @DeleteMapping("/inputFile")
    public RespEntity uploadInputFileCancel(HttpSession session){
        RespEntity ret = new RespEntity(RespCode.WARN,null);
        if(null!= session.getAttribute("reportErrorPath")){
            FileUtils.deleteQuietly(new File(session.getAttribute("reportErrorPath").toString()));
        }
        Object taskUploadKey =  session.getAttribute("taskUploadKey");
        if(null!=taskUploadKey){
            session.removeAttribute("reportErrorPath");
            session.removeAttribute("uploadExcelFileName");
            session.removeAttribute("taskUploadKey");
            session.removeAttribute("successCount");
            session.removeAttribute("errorCount");
            session.removeAttribute("reportTaskCode");
            stringRedisTemplate.delete(taskUploadKey.toString());
        }
        ret.changeRespEntity(RespCode.SUCCESS,null);
        return ret;
    }

    @GetMapping(value="/downloadWrongExcel")
    public void downloadWrongExcel(HttpSession session, HttpServletRequest request, HttpServletResponse response){
        String excelPath = session.getAttribute("reportErrorPath").toString();
        String originalFileName = session.getAttribute("uploadExcelFileName").toString();
        try {
            boolean isMSIE = false;
            String userAgent = request.getHeader("User-Agent");
            for (String signal : Constants.IEBrowserSignals) {
                if (userAgent.contains(signal)) {
                    isMSIE = true;
                    break;
                }
            }

            HttpHeaders headers = new HttpHeaders();
            String fileName = "error_excel";
            /*if (isMSIE) {
                fileName = URLEncoder.encode(originalFileName, "UTF-8");BiInputDataVO
            }else{
                fileName = new String(originalFileName.getBytes("UTF-8"), "ISO-8859-1");
            }*/
            response.setHeader("Content-disposition", "attachment; filename="
                    + fileName + ".xlsx");// filename是下载的xls的名，建议最好用英文
            response.setContentType("application/msexcel;charset=UTF-8");// 设置类型
            response.setHeader("Pragma", "No-cache");// 设置头
            response.setHeader("Cache-Control", "no-cache");// 设置头
            response.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");// 设置头
            response.setDateHeader("Expires", 0);// 设置日期头



            //fileName = URLEncoder.encode(originalFileName, "UTF-8");
            //headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            //headers.setContentDisposition("attachment; filename=\"" + fileName+ "\"");
            //headers.setContentDispositionFormData("attachment", fileName);
            //headers.set("Content-Type","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            //headers.set("Content-Disposition", "attachment; filename=\"" + fileName+ "\"");
            //FileCopyUtils.copy(new FileInputStream(new File(excelPath)),response.getOutputStream());
            Workbook wb = new XSSFWorkbook(FileUtils.openInputStream(new File(excelPath)));
            wb.write(response.getOutputStream());
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(),e);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    @PostMapping(value = "/getTaskCode")
    public RespEntity getTaskCode(HttpSession session){
        RespEntity ret = new RespEntity(RespCode.WARN,null);
        String taskCode = redisUtil.generateCodeNo("RTC");
        session.setAttribute("reportTaskCode",taskCode);
        JSONObject jo = new JSONObject();
        jo.put("taskCode",taskCode);
        ret.changeRespEntity(RespCode.SUCCESS,jo);
        return ret;
    }

    @GetMapping(value = "/getTaskCode")
    public RespEntity getTaskCode2(HttpSession session){
        RespEntity ret = new RespEntity(RespCode.WARN,null);
        String taskCode = redisUtil.generateCodeNo("RTC");
        session.setAttribute("reportTaskCode",taskCode);
        JSONObject jo = new JSONObject();
        jo.put("taskCode",taskCode);
        ret.changeRespEntity(RespCode.SUCCESS,jo);
        return ret;
    }

    


    @GetMapping("/initSessionForTest")
    public RespEntity test3(
            @RequestParam(value = "apiCode",required = false,defaultValue = "4000159") String apiCode,
            @RequestParam(value = "userId",required = false,defaultValue = "1") String userId,
            @RequestParam(value = "userType",required = false,defaultValue = "0")String userType,
            HttpSession session){
        RespEntity ret = new RespEntity (RespCode.SUCCESS,null);
        String publicKey = stringRedisTemplate.opsForValue().get("biz_credit:publicKey");
        if(!StringUtils.equals("prod",env)){
            //SELECT b.institution_id,c.crm_api_id,a.* FROM t_user a INNER JOIN auth.t_institution b
            //ON a.institution_id=b.institution_id
            //INNER JOIN auth.t_crm_api c
            //ON c.institution_id=b.institution_id
            // WHERE a.username='duser'
            session.setAttribute(Constants.API_CODE,apiCode);//apicode(如果要切换成指定定用户需要更改)
            session.setAttribute(Constants.USER_TYPE,userType);//用户类型(如果要切换成指定定用户需要更改)
            session.setAttribute(Constants.USER_ID,userId);//用户id(如果要切换成指定定用户需要更改)
            session.setAttribute("res1","/report/reportTask,/report/reportCustomerBI,/report");//用户权限
            session.setAttribute("crmApiId","1");//用户apicode对应得编号在auth.t_crm_api表中(如果要切换成指定定用户需要更改)
            session.setAttribute("institutionId","1");//用户apicode对应得编号在auth.t_institution表中(如果要切换成指定定用户需要更改)
            session.setAttribute("username","duser");//用户登录账号(如果要切换成指定定用户需要更改)
            session.setAttribute("groupId","36");//用户分组编号(如果要切换成指定定用户需要更改)
            session.setAttribute("realname","小用户d");//用户真实姓名
            session.setAttribute("bizPublicKey",publicKey);//公用密钥
        }
        return ret;
    }

    @RequestMapping(value = "/downLoadZipFileTest")
    public void downLoadZipFile(@RequestParam("path") String path, HttpServletResponse response){
        if(!StringUtils.equals("prod",env)){
            Collection<File> list = FileUtils.listFiles(new File(path), new String[]{"pdf"}, false);
            String zipName = path+"temp.zip";
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition","attachment; filename="+zipName);
            try(
                    ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
                    ){
                for(Iterator<File> it = list.iterator(); it.hasNext();){
                    File file = it.next();
                    ZipUtils.doCompress(file.getName(),file.getAbsolutePath(), out);
                    response.flushBuffer();
                }
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    @RequestMapping("/strategy/generate")
    public RespEntity generateClientTemplateStrategy(@RequestParam(value = "strategyId") Long strategyId,
                                             @RequestParam(value = "apiCode") String apiCode,
                                             @RequestParam(value = "userId") Long userId,
                                             @RequestParam(value = "isTemplate",required = false,defaultValue = "1") Long isTemplate,
                                             @RequestParam(value="srcStrategyId",required = false,defaultValue = "2") Long srcStrategyId,
                                             @RequestParam(value = "industryId",required = false,defaultValue = "-1") Integer industryId,
                                             HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.SUCCESS,null);
        String bairongApiCode = session.getAttribute(Constants.API_CODE).toString();
        Long bairongUserId = Long.parseLong(session.getAttribute(Constants.USER_ID).toString());
        StrategyVO source = new StrategyVO(strategyId,bairongApiCode,bairongUserId);
        StrategyVO target = new StrategyVO(null,apiCode,userId);
        target.setIndustryId(industryId);
        target.setIsTemplate(isTemplate);
        try {
            long count = taskService.copyStrategyTemplate(source, target);
            if(Constants.IS_TEMPLATE_TRUE==target.getIsTemplate()){
                count = count + taskService.updateSrcIdForCopyStrategyTemplate(target);
            }else{
                source.setStrategyId(srcStrategyId);
                taskService.copyModuleTypeByStrategyId(source,target);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count",count);
            jsonObject.put("strategyId",target.getStrategyId());
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }





    public static void main(String[] args) {
        List<Object> list = new ArrayList<>();
        list.add("da");
        list.add("fss");
        list.add("4s");
        list.add("13123214");

        String str= "WebBizRentReport";
        System.out.println(str.substring(0,str.indexOf("_")>-1?str.indexOf("_"):str.length()));
        String str312 = null;
        System.out.println(StringUtils.trim(str312));
    }

    /*@GetMapping("/inputFileDetail/info")
    public RespEntity findInputDetailInfo(
            @RequestParam(value = "inputFileDetailId", required = false, defaultValue = "0") Integer inputFileDetailId,
            @RequestParam(value = "moduleTypeId", required = false, defaultValue = "0") Integer moduleTypeId,
            HttpSession session) {
        RespEntity respEntity = new RespEntity(RespCode.WARN, null);
        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());
        String apiCode = session.getAttribute("apiCode").toString();
        Integer userType = Integer.parseInt(session.getAttribute("userType").toString());

            InputFileDetailVO inputFileDetailVO = null;
            inputFileDetailVO = 0 == userType ? reportTaskService.findInputFileDetail(new InputFileDetailVO(inputFileDetailId, apiCode)) : reportTaskService.findInputFileDetail(new InputFileDetailVO(inputFileDetailId, userId));
            log.info("inputFileDetailVO[{}][userId:{}]:{}" , inputFileDetailId,userId,inputFileDetailVO);
            moduleTypeId = inputFileDetailVO == null ? moduleTypeId : inputFileDetailVO.getModuleTypeId();
            JSONObject query = null == inputFileDetailVO ? new JSONObject() : inputFileDetailVO.makeQueryJsonData();
            JSONObject result = null == inputFileDetailVO ? new JSONObject() : inputFileDetailVO.makeResultJsonData();
            ModuleTypeVO moduleType = reportTaskService.queryModuleType(new ModuleTypeVO(apiCode, moduleTypeId));
            List<JSONObject> headFinalList = new ArrayList<>();
            if(null!=moduleType) {
                List<String> headerList = Arrays.asList(moduleType.getColumnHead().split("_"));
                List<String> headListPerson = StringUtils.isEmpty(moduleType.getColumnHeadPerson()) ? null : Arrays.asList(moduleType.getColumnHeadPerson().split("_"));
                Map<String, List<String>> headerTemplateListTmp = new LinkedHashMap<>(Constants.DetailInputGroupList);
                //List<List<String>> headerTemplateList = new ArrayList<>();
                headerTemplateListTmp.forEach((title, headerGroup) -> {
                    *//*Set<String> tmp = new HashSet<>();
                    headerGroup.forEach(header->{
                        if(!headerList.contains(header)){
                            tmp.add(header);
                        }
                    });
                    tmp.forEach(s->{
                        headerGroup.remove(s);
                    });
                    if(!CollectionUtils.isEmpty(headerGroup)&&(!headerGroup.contains(Constants.RelatedPerson))){
                        headerTemplateList.add(headerGroup);

                    }*//*
                    JSONObject hg = new JSONObject();
                    hg.put("title", title);
                    if (!StringUtils.equals(title, Constants.DetailRelatedPersonTitle)) {
                        hg.put("addable", 0);
                        JSONArray items = new JSONArray();
                        headerGroup.forEach(header -> {
                            if (headerList.contains(header)) {
                                JSONObject item = new JSONObject();
                                item.put("key", header);
                                item.put("value", StringUtils.EMPTY);
                                item.put("required", StringUtils.equals(Constants.companyName, header) ? 1 : 0);
                                items.add(item);
                            }
                        });
                        hg.put("items", items);
                    } else {
                        hg.put("addable", 1);
                        JSONArray items = new JSONArray();
                        headerGroup.forEach(header -> {
                            if (headListPerson.contains(header)) {
                                JSONObject item = new JSONObject();
                                item.put("key", header);
                                item.put("value", StringUtils.EMPTY);
                                item.put("required", StringUtils.equals(Constants.companyName, header) ? 1 : 0);
                                items.add(item);
                            }
                        });
                        hg.put("items", items);
                    }
                    if (!CollectionUtils.isEmpty(hg.getJSONArray("item")))
                        headFinalList.add(hg);
                });
                if (null != inputFileDetailVO) {
                    inputFileDetailVO.dealHeaderJson(headFinalList);
                }
                respEntity.changeRespEntity(RespCode.SUCCESS, headFinalList);
            }
        return respEntity;
    }*/


    /*public static void main(String[] args) throws IOException {
      *//* List<String> list = FileUtils.readLines(new File("C:\\Users\\岑汝毓\\Desktop\\8200003.txt"));
        List<String> result = new ArrayList<>();
       for(String str:list){
           for(String str2:str.split("\\|")) {
               if (StringUtils.startsWith(str2.trim(), "biz") || StringUtils.startsWith(str2.trim(), "Biz") || StringUtils.startsWith(str2.trim(), "var") || StringUtils.startsWith(str2.trim(), "H")){
                   System.out.println(str2.trim());
                   result.add(str2.trim());
                }
           }
       }
       StringBuffer ret = new StringBuffer("(");
       result.forEach(s->{
           ret.append("'").append(s).append("',");
       });
        System.out.println(ret);*//*
      *//*JSONObject json = new JSONObject();

      json.put("inputFileDetailId","1");
      json.put("keyNo","fafafs");
      InputFileDetailVO inputFileDetailVO = JSONObject.parseObject(json.toJSONString(),InputFileDetailVO.class);
      System.out.println(inputFileDetailVO.getKeyNo());*//*
        *//*String json = "{\"store\":{\"book\":[{\"title\":\"高效Java\",\"price\":10},{\"title\":\"研磨设计模式\",\"price\":12},{\"title\":\"重构\",\"isbn\":\"553\",\"price\":8},{\"title\":\"虚拟机\",\"isbn\":\"395\",\"price\":22}],\"bicycle\":{\"color\":\"red\",\"price\":19}}}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONPath.set(jsonObject, "$.store.book[0:].title", "呵呵");
        String title0 = JSONPath.read(json,"$.store.book[0].title").toString();
        System.out.println(title0);
        System.out.println(JSONPath.eval(jsonObject,"$.store.book[1].title"));*//*
        File file = new File("C:\\Users\\岑汝毓\\Desktop\\bi.tar.gz");
        System.out.println(file.getAbsolutePath());

    }*/



}
