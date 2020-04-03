package com.biz.warning.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.controller.BaseController;
import com.biz.service.IAuthService;
import com.biz.strategy.BizTask;
import com.biz.strategy.entity.TaskInput;
import com.biz.warning.domain.Entity;
import com.biz.warning.service.IEntityService;
import com.biz.warning.service.ITaskService;
import com.biz.warning.util.*;
import com.biz.warning.vo.EntityVO;
import com.biz.warning.vo.TaskVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
public class EntityController extends BaseController {
    @Value("${upload_root_path}")
    private String uploadRootPath;
    @Value("${se_redis_prefix}")
    String strategyEnginePrefix;

    @Autowired
    private IAuthService authService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IEntityService entityService;
    @Autowired
    private ITaskService taskService;
    @Autowired
    private BizTask bizTask;

    @GetMapping("/logout")
    public RespEntity logout(HttpServletRequest request){
        request.getSession().invalidate();//销毁session
        return new RespEntity(RespCode.SUCCESS,null);
    }

    @GetMapping(value="/downloadExcelTemplate")
    public void downloadExcelTemplate(
            @RequestParam(name="taskId", required = true ) Long taskId,
            HttpServletRequest request, HttpServletResponse response){
        log.info("downloadExcelTemplate开始下载----");

        String userId = request.getSession().getAttribute("userId").toString();
        TaskVO task = new TaskVO();
        task.setTaskId(taskId);
        task.setUserId(Long.parseLong(userId));
        try {

            Workbook workbook = entityService.getWorkbookByTask(task);
            //HttpHeaders headers = new HttpHeaders();
            //String fileName = "template.xlsx";
            //FileCopyUtils.copy(new FileInputStream(new File(excelPath)),response.getOutputStream());
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/inputFiles")
    public RespEntity findInputFiles(@RequestParam(name = "taskId") Integer taskId,
                                     @RequestParam(name = "companyName",required = false,defaultValue = StringUtils.EMPTY) String companyName,
                                     @RequestParam(name = "pageNo",required = false,defaultValue = "1")Integer pageNo,
                                     @RequestParam(name = "pageSize",required = false,defaultValue = "10")Integer pageSize,
                                     @RequestParam(name = "startDate",required = false,defaultValue = StringUtils.EMPTY) String startDate,
                                     @RequestParam(name = "endDate",required = false,defaultValue = StringUtils.EMPTY) String endDate,
                                     @RequestParam(name = "entityStatus",required = false,defaultValue = "0") Integer entityStatus,
                                     HttpSession session
                                     ){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        JSONObject jsonObject = new JSONObject();
        String apiCode = session.getAttribute("apiCode").toString();
        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());
        TaskVO task = null;
        List<String> headList = new ArrayList<>();
        JSONArray companyList = new JSONArray();
        jsonObject.put("companyList",companyList);
        try {
            task = taskService.findTaskByTaskIdAndUserId(taskId,null);
            String columnHead = task.getEntityTemplateName();
            headList.addAll(Arrays.asList(columnHead.split("_")));
            List<String> finalHeadList = new ArrayList<>(headList);
            finalHeadList.add(finalHeadList.size(),"监控状态");
            jsonObject.put("headList",finalHeadList);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        try {
            Page<EntityVO> page = PageHelper.startPage(pageNo, pageSize);
            EntityVO entityVO = new EntityVO();
            entityVO.setApiCode(apiCode);
            entityVO.setEntityStatus(entityStatus);
            entityVO.setCompanyName(companyName);
            if(StringUtils.isNotEmpty(startDate))
                entityVO.setStartDate(startDate.split(" ")[0].concat(" 00:00:00"));
            if(StringUtils.isNotEmpty(endDate))
                entityVO.setEndDate(endDate.split(" ")[0].concat(" 23:59:59"));
            entityVO.setTaskId(taskId.longValue());
            entityService.findList(entityVO);
            jsonObject.put("total",page.getTotal());
            if(page.getTotal()>0){
                page.getResult().forEach(company->{
                    JSONObject companyJson = JSONObject.parseObject(JSONObject.toJSONString(company));
                    List<String> info = new ArrayList<>();
                    headList.forEach(head->{
                        String tmp = companyJson.getString(SysDict.DetailPropMap.get(head));
                        info.add(StringUtils.isNotEmpty(tmp)?tmp:StringUtils.EMPTY);
                    });
                    info.add(company.getMonitorStatus());
                    jsonObject.getJSONArray("companyList").add(info);
                });
            }
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respEntity;
    }
    @PostMapping("/inputFile/upload")
    public RespEntity uploadInputFile(@RequestParam("file")MultipartFile file, TaskVO task, HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        session.removeAttribute("rwInputFileCacheKey");
        String apiCode = getApiCode();
        String userId = session.getAttribute("userId").toString();
        List<Integer> userIdList = getUserIdList();
        try {
            InputStream is = file.getInputStream();
            JSONObject jo = new JSONObject();
            //task.setUserId(Long.parseLong(userId));
            task.setIdList(userIdList);
            task.setApiCode(apiCode);
            TaskVO taskVORet = taskService.findTaskVOByTaskVO(task);
            if(null==taskVORet){
                respEntity.changeRespEntity(RespCode.INVALID,null);
                return respEntity;
            }
            Set<String> existedCompanyNames = entityService.findEntityNamesByTask(task);
            List<Object> headList = new ArrayList<>(Arrays.asList(taskVORet.getEntityTemplateName().split("_")));
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            Map<Integer,String> headerMap = new HashMap<>();
            Workbook wb = ExcelUtil.getWorkBook(suffix, is);
            List<String[]> companyList =  ExcelUtil.getExcelData(suffix,wb,jo,headList,headerMap,existedCompanyNames);
            if(jo.containsKey("templateError")){
                jo.put("errorMsg","上传模板入参不正确，请重新下载模板，并核对excel模板入参。要求按顺序入参:".concat(headList.toString().replaceAll("\\[|\\]","")));
                jo.put("errorCount",companyList.size());
                jo.put("successCount",0);
                jo.put("uploadResult",false);
                String errorExcelFileName = UUID.randomUUID().toString().concat(".").concat(suffix);
                String errorExcelPath = uploadRootPath.concat("/error/").concat(errorExcelFileName);
                session.setAttribute("rwErrorPath",errorExcelPath);
                session.setAttribute("rwUploadExcelFileName",file.getOriginalFilename());
                ExcelUtil.addTemplateErrorMsg(wb,jo.getString("errorMsg"));
                OutputStream os = FileUtils.openOutputStream(new File(errorExcelPath));
                wb.write(os);
                respEntity.changeRespEntity(RespCode.TEMPLATE_NAME_WRONG,jo);
                return respEntity;
            }
            List<List<String>>  retCompanyList = new ArrayList<>();
            if(jo.containsKey("uploadResult")){
                String errorExcelFileName = UUID.randomUUID().toString().concat(".").concat(suffix);
                String errorExcelPath = uploadRootPath.concat("/error/").concat(errorExcelFileName);
                session.setAttribute("rwErrorPath",errorExcelPath);
                session.setAttribute("rwUploadExcelFileName",file.getOriginalFilename());
                OutputStream os = FileUtils.openOutputStream(new File(errorExcelPath));
                jo.put("errorMsg","进件信息有错误或重复,下载excel确认");
                wb.write(os);
            }else{
                jo.put("errorCount",0);
                jo.put("uploadResult",true);
            }
            List<String> headerList = new ArrayList<>();
            List<Entity> entityList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(companyList)){
                String dateStr = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");

                for(int i= 0 ;i<headerMap.size();i++){
                    headerList.add(headerMap.get(i));
                }

                for(String[] comp:companyList){
                    Entity entity = new Entity();
                    entity.setApiCode(apiCode);
                    entity.setUserId(Long.parseLong(userId));
                    entity.setUploadTime(dateStr);
                    entity.setTaskId(task.getTaskId());
                    for (int i=0 ; i<comp.length;i++){
                        entity.setParamValue(headerMap.get(i),comp[i]);
                    }
                    if(retCompanyList.size()<6){
                        retCompanyList.add(Arrays.asList(comp));
                    }
                    entityList.add(entity);
                }
                String rwInputFileCacheKey = "{biz_credit:risk_warning:inputFileCache}:".concat(MD5.generateRandomSalt());
                session.setAttribute("rwInputFileCacheKey",rwInputFileCacheKey);
                session.setAttribute("rwTaskId",taskVORet.getTaskId().toString());
                session.setAttribute("rwTaskName",taskVORet.getTaskName());
                session.setAttribute("rwStrategyId",taskVORet.getStrategyId().toString());
                ListOperations<String,Entity> opsForList = redisTemplate.opsForList();
                entityList.forEach(entity -> {
                    opsForList.rightPush(rwInputFileCacheKey,entity);
                });
                redisTemplate.expire(rwInputFileCacheKey,24*3600,TimeUnit.SECONDS);
                //entityService.saveEntityList(entityList);
            }
            String dateStr = DateTime.now().toString("yyyyMMdd");
            session.setAttribute("seEntityStatusRedisKey",strategyEnginePrefix.concat(dateStr).concat("}:status"));
            jo.put("successCount",entityList.size());
            jo.put("companyList",retCompanyList);
            jo.put("headList",headerList);
            respEntity.changeRespEntity(jo.getBooleanValue("uploadResult")?RespCode.SUCCESS:RespCode.UPLOAD_WRONG,jo);

            session.setAttribute("rwSuccessCount",jo.getString("rwSuccessCount"));
            session.setAttribute("rwErrorCount",jo.getString("rwErrorCount"));
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;

    }
    @RequestMapping("/inputFile/submit")
    public RespEntity submitInputFile(HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        Object redisCacheKey = session.getAttribute("rwInputFileCacheKey");
        Integer userId = Integer.valueOf(session.getAttribute("userId").toString());
        long length = 0;
        if(null!=redisCacheKey){
            ListOperations<String,Entity> opsForList = redisTemplate.opsForList();
            length = opsForList.size(redisCacheKey.toString());
            if(length>0){
                int taskId = Integer.parseInt(session.getAttribute("rwTaskId").toString());
                String taskName = session.getAttribute("rwTaskName").toString();
                int strategyId = Integer.parseInt(session.getAttribute("rwStrategyId").toString());
                String apiCode = session.getAttribute(SysDict.API_CODE).toString();
                List<Entity> entityList = opsForList.range (redisCacheKey.toString(), 0, length);
                try {
                    entityService.saveEntityList(entityList);
                    session.removeAttribute("rwInputFileCacheKey");
                    redisTemplate.delete(redisCacheKey.toString());
                    //stringRedisTemplate.opsForZSet().add(session.getAttribute("seEntityStatusRedisKey").toString(),"entityStatus",0);
                    if(!CollectionUtils.isEmpty(entityList)){
                        com.biz.strategy.entity.Task seTask = new com.biz.strategy.entity.Task();
                        seTask.setTaskId(taskId);
                        seTask.setApiCode(apiCode);
                        seTask.setUserId(userId);
                        //seTask.setTaskName(taskVORet.getTaskName());
                        seTask.setStrategyId(strategyId);
                        List<TaskInput> taskInputList = new ArrayList<>();
                        seTask.setTaskInputs(taskInputList);
                        entityList.forEach(entity -> {
                            if(1==entity.getEntityStatus()){
                                TaskInput taskInput = new TaskInput();
                                taskInput.setInputId(entity.getEntityId().intValue());
                                taskInput.setCompanyName(entity.getCompanyName());
                                taskInput.setExpireTime(entity.getExpireTime());
                                taskInput.setLegalPerson(entity.getLegalPerson());
                                taskInput.setLegalPersonID(entity.getPersonId());
                                taskInput.setLegalPersonPhone(entity.getCell());
                                taskInput.setLegalPersonHomeAddr(entity.getHomeAddr());
                                taskInput.setLegalPersonWorkAddr(entity.getBizAddr());
                                taskInput.setCompanyBankId(entity.getBankId());
                                taskInput.setCreditCode(entity.getCreditCode());
                                taskInput.setAppId(entity.getParentAppId());
                                taskInput.setInputDate(entity.getUploadTime().split(" ")[0]);
                                taskInput.setApplyDate(entity.getApplicationDate());
                                seTask.getTaskInputs().add(taskInput);
                            }
                        });
                        log.info("entityList:"+entityList.size());
                        log.info("seTask.getTaskInputs():"+seTask.getTaskInputs().size());
                        if(!CollectionUtils.isEmpty(seTask.getTaskInputs())){
                            bizTask.issue(seTask);
                        }
                        session.removeAttribute("rwTaskId");
                        session.removeAttribute("rwTaskName");
                        session.removeAttribute("rwStrategyId");
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count",length);
        respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        return respEntity;
    }
    @RequestMapping("/inputFile/cancel")
    public RespEntity cancelInputFile(HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        Object redisCacheKey = session.getAttribute("rwInputFileCacheKey");
        long length = 0;
        session.removeAttribute("rwInputFileCacheKey");
        if(null!=redisCacheKey){
            ListOperations<String,Entity> opsForList = redisTemplate.opsForList();
            length = opsForList.size(redisCacheKey.toString());
            if(length>0){
                redisTemplate.delete(redisCacheKey.toString());
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count",length);
        respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        return respEntity;
    }
    @DeleteMapping(value="/inputFiles",produces = "application/json;charset=UTF-8")
    public RespEntity removeEntityList(List<EntityVO> entityVOList,HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        Integer userId = Integer.parseInt(session.getAttribute("userId").toString());
        try {
            int count = entityService.deleteEntityVOList(entityVOList,userId);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count",count);
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respEntity;
    }
    @PostMapping("/inputFile")
    public RespEntity addInputFile(@RequestParam("file")MultipartFile file, TaskVO task, HttpServletRequest request,HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        session.removeAttribute("rwInputFileCacheKey");
        String apiCode = session.getAttribute("apiCode").toString();
        String userId = session.getAttribute("userId").toString();
        String cookie = request.getHeader(SysDict.COOKIE);
        List<Integer> userIds =  authService.getAdminUserIds(cookie);
        try {
            InputStream is = file.getInputStream();
            JSONObject jo = new JSONObject();
            //task.setUserId(Long.parseLong(userId));
            task.setIdList(userIds);
            task.setApiCode(apiCode);
            TaskVO taskVORet = taskService.findTaskVOByTaskVO(task);
            if(null==taskVORet){
                respEntity.changeRespEntity(RespCode.INVALID,null);
                return respEntity;
            }
            Set<String> existedCompanyNames = entityService.findEntityNamesByTask(task);

            List<Object> headList = new ArrayList<>(Arrays.asList(taskVORet.getEntityTemplateName().split("_")));
            String suffix = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
            Map<Integer,String> headerMap = new HashMap<>();
            Workbook wb = ExcelUtil.getWorkBook(suffix, is);
            List<String[]> companyList =  ExcelUtil.getExcelData(suffix,wb,jo,headList,headerMap,existedCompanyNames);
            if(jo.containsKey("templateError")){
                jo.put("msg","上传模板入参不正确，请重新下载模板，并核对excel模板入参。要求按顺序入参:".concat(headList.toString().replaceAll("\\[|\\]","")));
                jo.put("errorCount",companyList.size());
                jo.put("successCount",0);
                jo.put("uploadResult",false);
                String errorExcelFileName = UUID.randomUUID().toString().concat(".").concat(suffix);
                String errorExcelPath = uploadRootPath.concat("/error/").concat(errorExcelFileName);
                session.setAttribute("rwErrorPath",errorExcelPath);
                session.setAttribute("rwUploadExcelFileName",file.getOriginalFilename());
                ExcelUtil.addTemplateErrorMsg(wb,jo.getString("msg"));
                OutputStream os = FileUtils.openOutputStream(new File(errorExcelPath));
                wb.write(os);
                respEntity.changeRespEntity(RespCode.TEMPLATE_NAME_WRONG,jo);
                return respEntity;
            }
            List<List<String>>  retCompanyList = new ArrayList<>();
            if(jo.containsKey("uploadResult")){
                String errorExcelFileName = UUID.randomUUID().toString().concat(".").concat(suffix);
                String errorExcelPath = uploadRootPath.concat("/error/").concat(errorExcelFileName);
                session.setAttribute("rwErrorPath",errorExcelPath);
                session.setAttribute("rwUploadExcelFileName",file.getOriginalFilename());
                OutputStream os = FileUtils.openOutputStream(new File(errorExcelPath));
                wb.write(os);
            }else{
                jo.put("errorCount",0);
                jo.put("uploadResult",true);
            }
            List<String> headerList = new ArrayList<>();
            List<Entity> entityList = new ArrayList<>();
            for(int i= 0 ;i<headerMap.size();i++){
                headerList.add(headerMap.get(i));
            }
            if(!CollectionUtils.isEmpty(companyList)){
                String dateStr = DateTime.now().toString("yyyy-MM-dd HH:mm:ss");
                for(String[] comp:companyList){
                    Entity entity = new Entity();
                    entity.setApiCode(apiCode);
                    entity.setUserId(Long.parseLong(userId));
                    entity.setTaskId(task.getTaskId());
                    entity.setUploadTime(dateStr);
                    for (int i=0 ; i<comp.length;i++){
                        entity.setParamValue(headerMap.get(i),comp[i]);
                    }
                    if(retCompanyList.size()<6){
                        retCompanyList.add(Arrays.asList(comp));
                    }
                    entityList.add(entity);
                }
                /*String inputCacheRedisKey = "{biz_credit:risk_warning:inputFileCache}:".concat(MD5.generateRandomSalt());
                session.setAttribute("rwInputFileCacheKey",inputCacheRedisKey);
                ListOperations<String,Entity> opsForList = redisTemplate.opsForList();
                entityList.forEach(entity -> {
                    opsForList.rightPush(inputCacheRedisKey,entity);
                });
                redisTemplate.expire(inputCacheRedisKey,24*3600,TimeUnit.SECONDS);*/
                entityService.saveEntityList(entityList);
                com.biz.strategy.entity.Task seTask = new com.biz.strategy.entity.Task();
                seTask.setTaskId(taskVORet.getTaskId().intValue());
                seTask.setTaskName(taskVORet.getTaskName());
                seTask.setStrategyId(taskVORet.getStrategyId().intValue());
                seTask.setApiCode(apiCode);
                seTask.setUserId(Integer.valueOf(userId));
                List<TaskInput> taskInputList = new ArrayList<>();
                seTask.setTaskInputs(taskInputList);
                entityList.forEach(entity -> {
                    if(1==entity.getEntityStatus()){
                        TaskInput taskInput = new TaskInput();
                        taskInput.setInputId(entity.getEntityId().intValue());
                        taskInput.setCompanyName(entity.getCompanyName());
                        taskInput.setExpireTime(entity.getExpireTime());
                        taskInput.setLegalPerson(entity.getLegalPerson());
                        taskInput.setLegalPersonID(entity.getPersonId());
                        taskInput.setLegalPersonPhone(entity.getCell());
                        taskInput.setInputDate(entity.getUploadTime().split(" ")[0]);
                        taskInput.setLegalPersonHomeAddr(entity.getHomeAddr());
                        taskInput.setLegalPersonWorkAddr(entity.getBizAddr());
                        taskInput.setCompanyBankId(entity.getBankId());
                        taskInput.setCreditCode(entity.getCreditCode());
                        taskInput.setAppId(entity.getParentAppId());
                        taskInput.setApplyDate(entity.getApplicationDate());
                        seTask.getTaskInputs().add(taskInput);
                    }
                });
                log.info("entityList:"+entityList.size());
                log.info("seTask.getTaskInputs():"+seTask.getTaskInputs().size());
                if(!CollectionUtils.isEmpty(seTask.getTaskInputs())){
                    bizTask.issue(seTask);
                }
                //stringRedisTemplate.opsForZSet().add(strategyEnginePrefix.concat(dateStr).concat("}:status"),"entityStatus",0);
            }
            jo.put("successCount",entityList.size());
            jo.put("companyList",retCompanyList);
            jo.put("headList",headerList);
            if(!jo.getBooleanValue("uploadResult")){
                jo.put("msg","本次进件必填项有错误或重复，成功"+jo.getString("successCount")+"条，错误"+jo.getString("errorCount")+"条");
            }else{
                jo.put("msg","本次进件成功"+jo.getString("successCount")+"条，错误"+jo.getString("errorCount")+"条");
            }
            respEntity.changeRespEntity(jo.getBooleanValue("uploadResult")?RespCode.SUCCESS:RespCode.UPLOAD_WRONG,jo);

            session.setAttribute("rwSuccessCount",jo.getString("rwSuccessCount"));
            session.setAttribute("rwErrorCount",jo.getString("rwErrorCount"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respEntity;

    }

    @GetMapping(value="/downloadWrongExcel")
    public void downloadWrongExcel(HttpSession session, HttpServletRequest request, HttpServletResponse response) {
        String excelPath = session.getAttribute("rwErrorPath").toString();
        String originalFileName = session.getAttribute("rwUploadExcelFileName").toString();
        try {
            boolean isMSIE = false;
            String userAgent = request.getHeader("User-Agent");
            for (String signal : SysDict.IEBrowserSignals) {
                if (userAgent.contains(signal)) {
                    isMSIE = true;
                    break;
                }
            }

            HttpHeaders headers = new HttpHeaders();
            String fileName = "wrong_excel.xlsx";
            /*if (isMSIE) {
                fileName = URLEncoder.encode(originalFileName, "UTF-8");
            } else {
                fileName = new String(originalFileName.getBytes("UTF-8"), "ISO-8859-1");
            }*/
            //fileName = URLEncoder.encode(originalFileName, "UTF-8");
/*            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", fileName);*/
            headers.set("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            headers.set("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            FileCopyUtils.copy(new FileInputStream(new File(excelPath)), response.getOutputStream());
            //FileUtils.deleteQuietly(new File(excelPath));
        } catch (UnsupportedEncodingException e) {
            log.error("不支持的编码格式:" + e.getMessage(), e);
        } catch (IOException e) {
            log.error(e.toString());
            log.error("IO写入异常:" + e.getMessage(), e);
        } catch (Exception e) {
            log.error("异常:" + e.getMessage(), e);
        }
    }


    /**
     * 查找所有进件列表
     *
     * @param
     * @return
     */
    @GetMapping(value = "/entities")
    @ResponseBody
    public RespEntity findEntities(@RequestParam(name="nameOrCode",required=false) String nameOrCode,
                                   @RequestParam(name="uploadTime",required=false) String uploadTime,
                                   @RequestParam(name="expireTime",required=false) String expireTime,
                                   @RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                   HttpSession session) {
        String apiCode = getApiCode();
        List<Integer> userIdList = getUserIdList();
        Page<Entity> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        try{
            entityService.findEntities(apiCode,userIdList,nameOrCode,uploadTime,expireTime);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            log.info("按条件查找进件列表信息失败");
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 按条件查找进件列表
     *
     * @param
     * @return
     */
    @GetMapping(value = "/allEntities")
    @ResponseBody
    public RespEntity findEntities(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                   @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                   HttpSession session) {
        String apiCode = getApiCode();
        List<Integer> userIdList = getUserIdList();
        Page<Entity> page= PageHelper.startPage(pageNo,pageSize);
        RespEntity entity = new RespEntity();
        try{
            entityService.findAllEntities(apiCode,userIdList);
            JSONObject jo = new JSONObject();
            jo.put("total",page.getTotal());
            jo.put("rows",page.getResult());
            entity.changeRespEntity(RespCode.SUCCESS,jo);
            return entity;
        }catch (Exception e){
            log.info("查找所有进件列表信息失败");
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 按名称查找进件详情
     *
     * @param
     * @return
     */
    @GetMapping(value = "/entity")
    @ResponseBody
    public RespEntity findEntityDetail(@RequestParam(value = "entityName") String entityName,
            HttpServletRequest request,HttpSession session) {
        String apiCode = session.getAttribute("apiCode").toString();
        String userId = session.getAttribute("userId").toString();
        RespEntity entity = new RespEntity();
        try{
            EntityVO vo = entityService.findEntityDetail(entityName,request,userId);
            entity.changeRespEntity(RespCode.SUCCESS,vo);
            return entity;
        }catch (Exception e){
            log.info("按名称查找进件详情失败");
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 按名称查找任务列表
     *
     * @param
     * @return
     */
    @GetMapping(value = "/entityTasks")
    @ResponseBody
    public RespEntity findTasksByEntity(@RequestParam(value = "entityName") String entityName) {
        String apiCode = getApiCode();
        List<Integer> userIdList = getUserIdList();
        RespEntity entity = new RespEntity();
        try{
            List<TaskVO> list = entityService.findTasksByEntity(entityName,apiCode,userIdList);
            entity.changeRespEntity(RespCode.SUCCESS,list);
            return entity;
        }catch (Exception e){
            log.info("按名称查找任务列表失败");
            e.printStackTrace();
            return entity;
        }
    }

    /**
     * 按名称删除进件（修改状态）
     *
     * @param
     * @return
     */
    @DeleteMapping(value = "/entity")
    @ResponseBody
    public RespEntity deleteEntity(@RequestParam(value = "entityName") String entityName,
                                        HttpSession session) {
        String apiCode = session.getAttribute("apiCode").toString();
        String userId = session.getAttribute("userId").toString();
        RespEntity entity = new RespEntity();
        try{
            int result = entityService.deleteEntity(entityName,userId);
            entity.changeRespEntity(RespCode.SUCCESS,result);
        }catch (Exception e){
            log.info("按名称删除进件（修改状态）");
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 按名称和任务id删除进件（修改状态）
     *
     * @param
     * @return
     */
    @DeleteMapping(value = "/entity/task")
    @ResponseBody
    public RespEntity deleteEntityByTask(@RequestParam(value = "entityName") String entityName,
                                   @RequestParam(value = "taskId") Integer taskId,
                                   HttpSession session) {
        String apiCode = getApiCode();
        List<Integer> userIdList = getUserIdList();
        RespEntity entity = new RespEntity();
        try{
            int result = entityService.deleteEntityByTask(entityName,taskId,apiCode,userIdList);
            entity.changeRespEntity(RespCode.SUCCESS,result);
        }catch (Exception e){
            log.info("按名称删除进件（修改状态）");
            e.printStackTrace();
        }
        return entity;
    }


    /**
     * 单条进件提交
     *
     * @param
     * @return
     */
    @PostMapping(value = "/entity/singleSubmit")
    @ResponseBody
    public RespEntity singleSubmit(@RequestBody Map<String, Object> task,HttpServletRequest request,
                                   HttpSession session) {
        RespEntity respEntity = new RespEntity(RespCode.ERROR, null);
        JSONObject jsonObject = new JSONObject();
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
        String cookie = request.getHeader(SysDict.COOKIE);
        List<Integer> userIds =  authService.getAdminUserIds(cookie);
        Entity entity = new Entity();
        List<Entity> entityList = new ArrayList<>();
        try {
            TaskVO param = new TaskVO(Long.valueOf(task.get("taskId").toString()));
            //param.setUserId(userId.longValue());
            param.setApiCode(apiCode);
            param.setIdList(userIds);
            TaskVO taskVORet = taskService.findTaskVOByTaskVO(param);
            if (null == taskVORet) {
                respEntity.changeRespEntity(RespCode.INVALID, null);
                return respEntity;
            }
            Set<String> existedCompanyNames = entityService.findEntityNamesByTask(taskVORet);
            List<Map<String, String>> entityInfo = (List<Map<String, String>>) task.get("entity");
            for (Map<String, String> in : entityInfo) {
                String key = in.get("key");
                String value = in.get("value");
                if (StringUtils.equals(SysDict.companyName, key) && existedCompanyNames.contains(value)) {
                    jsonObject.put("msg", "本次进件与已上传进件重复，成功0条，错误1条");
                    respEntity.changeRespEntity(RespCode.COMPANY_NAME_REPEAT, jsonObject);
                    return respEntity;
                }
                entity.setParamValue(key, value == null ? StringUtils.EMPTY : value);
            }
            entity.setTaskId(param.getTaskId());
            entity.setUserId(userId.longValue());
            entity.setApiCode(apiCode);
            entity.setUploadTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            entityList.add(entity);
            entityService.saveEntityList(entityList);
            com.biz.strategy.entity.Task seTask = new com.biz.strategy.entity.Task();
            seTask.setApiCode(apiCode);
            seTask.setUserId(userId);
            seTask.setTaskName(taskVORet.getTaskName());
            seTask.setStrategyId(taskVORet.getStrategyId().intValue());
            seTask.setTaskId(taskVORet.getTaskId().intValue());
            List<TaskInput> taskInputList = new ArrayList<>();
            seTask.setTaskInputs(taskInputList);
            entityList.forEach(input -> {
                if (1 == input.getEntityStatus()) {
                    TaskInput taskInput = new TaskInput();
                    taskInput.setInputId(input.getEntityId().intValue());
                    taskInput.setCompanyName(input.getCompanyName());
                    taskInput.setExpireTime(input.getExpireTime());
                    taskInput.setLegalPerson(input.getLegalPerson());
                    taskInput.setLegalPersonID(input.getPersonId());
                    taskInput.setLegalPersonPhone(input.getCell());
                    taskInput.setInputDate(input.getUploadTime().split(" ")[0]);
                    taskInput.setLegalPersonHomeAddr(input.getHomeAddr());
                    taskInput.setLegalPersonWorkAddr(input.getBizAddr());
                    taskInput.setCompanyBankId(input.getBankId());
                    taskInput.setCreditCode(input.getCreditCode());
                    taskInput.setAppId(input.getParentAppId());
                    taskInput.setApplyDate(input.getApplicationDate());
                    seTask.getTaskInputs().add(taskInput);
                }
            });
            log.info("entityList:" + entityList.size());
            log.info("seTask.getTaskInputs():" + seTask.getTaskInputs().size());
            if (!CollectionUtils.isEmpty(seTask.getTaskInputs())) {
                bizTask.issue(seTask);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        jsonObject.put("count", "1");
        jsonObject.put("entityId", entity.getEntityId());
        jsonObject.put("msg", "本次进件成功1条，错误0条");
        respEntity.changeRespEntity(RespCode.SUCCESS, jsonObject);
        return respEntity;
    }

    /**
     * 查找历史参数
     * @param
     * @return
     */
    @GetMapping(value = "/entity/task/historyParam")
    @ResponseBody
    public RespEntity queryHistoryParam(@RequestParam(value = "entityName") String entityName,
                                         @RequestParam(value = "taskId") Long taskId,
                                         HttpSession session) {
        String apiCode = getApiCode();
        List<Integer> userIdList = getUserIdList();
        RespEntity entity = new RespEntity();
        try{
            Map<String,Object> historyParam = entityService.findHistoryParam(entityName, taskId, apiCode,userIdList);
            entity.changeRespEntity(RespCode.SUCCESS,historyParam);
        }catch (Exception e){
            log.info("查找历史参数失败");
            e.printStackTrace();
        }
        return entity;
    }
}
