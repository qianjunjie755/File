package com.biz.warning.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.service.IAuthService;
import com.biz.strategy.BizTask;
import com.biz.warning.domain.RuleSet;
import com.biz.warning.service.IRuleService;
import com.biz.warning.service.IRuleSetService;
import com.biz.warning.service.ITaskService;
import com.biz.warning.service.IVariableService;
import com.biz.warning.util.RedisUtil;
import com.biz.warning.util.RespCode;
import com.biz.warning.util.RespEntity;
import com.biz.warning.util.SysDict;
import com.biz.warning.vo.RuleSetVO;
import com.biz.warning.vo.RuleVO;
import com.biz.warning.vo.TaskVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 预警任务控制类
 */
@RestController
@SuppressWarnings({"unchecked","unused"})
@ResponseBody
@Slf4j
public class TaskController {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ITaskService taskService;
    @Value("${se_redis_prefix}")
    private String strategyEnginePrefix;
    @Value("${spring.profiles.active}")
    private String springProfilesActive;
    private String ruleVORedisKey = "{biz:warning:hash}:";
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private BizTask bizTask;
    @Autowired
    private IRuleService ruleService;
    @Autowired
    private IRuleSetService ruleSetService;
    @Autowired
    private IVariableService variableService;
    @Autowired
    private IAuthService authService;
    private String strategyPrefix = "{biz_credit:risk_warning:task:strategy:template：32131321}:";


    @PostMapping(value = "/task",produces = "application/json;charset=UTF-8")
    public RespEntity addTask(HttpSession session, @RequestBody TaskVO task){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        HashOperations<String,String,RuleVO> opsForHash = redisTemplate.opsForHash();
        String apiCode=session.getAttribute(SysDict.API_CODE).toString();
        String userId = session.getAttribute(SysDict.USER_ID).toString();
        String sessionId = session.getId();
        String key = ruleVORedisKey.concat(apiCode).concat(":").concat(userId).concat(":").concat(sessionId);
        long size = opsForHash.size(key);
        if(size>0){
            Map<String,RuleVO> ruleVOMap = opsForHash.entries(key);
            task.getStrategyVO().getRuleSetVOList().forEach(ruleSetVO -> {
                if(!CollectionUtils.isEmpty(ruleSetVO.getRuleVOList())){
                    List<RuleVO> ruleVOList = new ArrayList<>();
                    ruleSetVO.getRuleVOList().forEach(ruleVO -> {
                        if(ruleVOMap.containsKey(ruleVO.getRuleId().toString())){
                            ruleVOList.add(ruleVOMap.get(ruleVO.getRuleId().toString()));
                            opsForHash.delete(key,ruleVO.getRuleId().toString());
                        }else{
                            ruleVOList.add(ruleVO);
                        }
                    });
                    ruleSetVO.setRuleVOList(ruleVOList);
                }
            });
        }
        task.setUserId(Long.parseLong(userId));
        task.setApiCode(apiCode);
        try {
            long count = taskService.addTask(task);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count",count);
            if(count>0){
                jsonObject.put("taskId",task.getTaskId());
                //session.setAttribute("");
                respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
            }else{
                respEntity.changeRespEntity(RespCode.CODE_MAP.get(String.valueOf(count)),null);
            }
            if(size>0){
                redisTemplate.delete(key);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }


    @PutMapping(value = "/task/rule/{ruleId}",produces = "application/json;charset=UTF-8")
    public RespEntity updateTaskVariableList(HttpSession session, @PathVariable("ruleId")Long ruleId, @RequestBody RuleVO ruleVO){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        HashOperations<String,String,RuleVO> opsForHash = redisTemplate.opsForHash();
        String apiCode=session.getAttribute(SysDict.API_CODE).toString();
        String userId = session.getAttribute(SysDict.USER_ID).toString();
        String sessionId = session.getId();
        String key = ruleVORedisKey.concat(apiCode).concat(":").concat(userId).concat(":").concat(sessionId);
        opsForHash.put(key,ruleVO.getRuleId().toString(),ruleVO);
        redisTemplate.expire(key,24*3600,TimeUnit.SECONDS);
        if(null!=ruleVO&&null!=ruleVO.getIsTemplate()
                &&SysDict.IS_TEMPLATE_FALSE ==ruleVO.getIsTemplate()
                &&!CollectionUtils.isEmpty(ruleVO.getVariableList())){
            ruleService.updateRuleForTask(ruleVO);
            ruleVO.getVariableList().forEach(variableVO -> {
                variableService.updateVariableThresholdForTaskUpdate(variableVO);
            });
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("count",opsForHash.size(key));
        respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        return respEntity;
    }

    @GetMapping("/task/rule/{ruleId}")
    public RespEntity getTaskVariableList(HttpSession session, @PathVariable("ruleId")Long ruleId){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String apiCode=session.getAttribute(SysDict.API_CODE).toString();
        String userId = session.getAttribute(SysDict.USER_ID).toString();
        String sessionId = session.getId();
        String key = ruleVORedisKey.concat(apiCode).concat(":").concat(userId).concat(":").concat(sessionId);
        HashOperations<String,String,RuleVO> opsForHash = redisTemplate.opsForHash();
        RuleVO ruleVO = opsForHash.get(key,ruleId.toString());
        if(null==ruleVO){
            RuleVO query = new RuleVO();query.setRuleId(ruleId);query.setApiCode(apiCode);
            ruleVO = ruleService.findRuleForTask(query);
        }
        respEntity.changeRespEntity(RespCode.SUCCESS,ruleVO);

        return respEntity;
    }
    @GetMapping("/task/rule/change/{srcRuleCode}/{version}")
    public RespEntity getRuleBySrcRuleCodeAndVersion(HttpSession session, @PathVariable("srcRuleCode")String srcRuleCode,@PathVariable("version") String version){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String apiCode=session.getAttribute(SysDict.API_CODE).toString();
        String userId = session.getAttribute(SysDict.USER_ID).toString();
        RuleVO query = new RuleVO();
        query.setSrcRuleCode(srcRuleCode);query.setVersion(version);query.setApiCode(apiCode);query.setIsTemplate(SysDict.IS_TEMPLATE_TRUE);
        RuleVO ruleVO = ruleService.findRuleForTask(query);
        respEntity.changeRespEntity(RespCode.SUCCESS,ruleVO);
        return respEntity;
    }


    @PutMapping(value = "/task/{taskId}",produces = "application/json;charset=UTF-8")
    public RespEntity updateTask(HttpSession session,@PathVariable("taskId")Long taskId, @RequestBody TaskVO task){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        HashOperations<String,String,RuleVO> opsForHash = redisTemplate.opsForHash();
        String apiCode=session.getAttribute(SysDict.API_CODE).toString();
        String userId = session.getAttribute(SysDict.USER_ID).toString();
        String sessionId = session.getId();
        String key = ruleVORedisKey.concat(apiCode).concat(":").concat(userId).concat(":").concat(sessionId);
        long size = opsForHash.size(key);
        if(size>0){
            Map<String,RuleVO> ruleVOMap = opsForHash.entries(key);
            task.getStrategyVO().getRuleSetVOList().forEach(ruleSetVO -> {
                if(!CollectionUtils.isEmpty(ruleSetVO.getRuleVOList())){
                    List<RuleVO> ruleVOList = new ArrayList<>();
                    ruleSetVO.getRuleVOList().forEach(ruleVO -> {
                        if(ruleVOMap.containsKey(ruleVO.getRuleId().toString())){
                            ruleVOList.add(ruleVOMap.get(ruleVO.getRuleId().toString()));
                            opsForHash.delete(key,ruleVO.getRuleId().toString());
                        }else{
                            ruleVOList.add(ruleVO);
                        }
                    });
                    ruleSetVO.setRuleVOList(ruleVOList);
                }
            });
        }
        task.setTaskId(taskId);
        task.setUserId(Long.parseLong(userId));
        task.setApiCode(apiCode);
        try {
            long count = null!=task.getTaskStatus()&&(1==task.getTaskStatus()||2==task.getTaskStatus())?0:taskService.updateTask(task);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count",count);
            if(count>0){
                respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
            }else{
                respEntity.changeRespEntity(RespCode.CODE_MAP.get(String.valueOf(count)),null);
            }
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }

    @GetMapping(value = "/tasks",produces = "application/json;charset=UTF-8")
    public RespEntity queryTasks(@RequestParam(value = "pageNo",required = false,defaultValue = "1") Integer pageNo,
                                 @RequestParam(value = "pageSize",required = false,defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "taskCode",required = false,defaultValue = "") String taskCode,
                                 @RequestParam(value = "taskName",required = false,defaultValue = "") String taskName,
                                 @RequestParam(value = "startDate",required = false,defaultValue = "") String startDate,
                                 @RequestParam(value = "endDate",required = false,defaultValue = "") String endDate,
                                 @RequestParam(value = "taskStatus",required = false) Integer taskStatus,
                                 @RequestParam(value = "self",required = false,defaultValue = "0") Integer self,
                                 HttpSession session, HttpServletRequest request){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        Page<TaskVO> pageHelper = PageHelper.startPage(pageNo,pageSize);
        String cookie = request.getHeader(SysDict.COOKIE);
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        String userId = session.getAttribute(SysDict.USER_ID).toString();
        List<Integer> userIds = new ArrayList<>();
        if(1==self){
            userIds.add(Integer.parseInt(userId));
        }else{
            userIds.addAll(authService.getAdminUserIds(cookie));
        }
        log.info("/tasks userId:"+userId);
        log.info("/tasks apiCode:"+apiCode);
        String ids = userIds.toString().replaceAll(SysDict.COLLECTION_REPLACE_TO_STRING,StringUtils.EMPTY);
        log.info("/tasks userIds:"+ids);
        TaskVO taskVO = new TaskVO();
        if(StringUtils.isNotEmpty(startDate)){
            startDate = startDate.concat(" 00:00:00");
        }
        if(StringUtils.isNotEmpty(endDate)){
            endDate = endDate.concat(" 23:59:59");
        }
        taskVO.setIdList(userIds);
        taskVO.setStartDateStr(startDate);
        taskVO.setEndDateStr(endDate);
        taskVO.setTaskCode(taskCode);
        taskVO.setTaskName(taskName);
        taskVO.setApiCode(apiCode);
        taskVO.setIds(ids);
        taskVO.setTaskStatus(taskStatus);
        try {
            taskService.findTaskVOListByPage(taskVO);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("total",pageHelper.getTotal());
            jsonObject.put("rows",pageHelper.getResult());
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }

   /* @PutMapping(value = "/task/status",produces = "application/json;charset=UTF-8")
    public RespEntity changeTaskStatus(@RequestBody TaskVO taskVO, HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String userId = session.getAttribute("userId").toString();
        String apiCode = session.getAttribute("apiCode").toString();
        taskVO.getTask().setUserId(Long.parseLong(userId));
        taskVO.getTask().setApiCode(apiCode);
        try {
            long count = taskService.updateTaskStatus(taskVO);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count",count);
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return respEntity;
    }*/

    @PutMapping(value = "/task/{taskId}/taskStatus/{taskStatus}",produces = "application/json;charset=UTF-8")
    public RespEntity changeTaskStatus(@PathVariable("taskId")Long taskId,@PathVariable("taskStatus")Integer taskStatus, HttpSession session){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String userId = session.getAttribute(SysDict.USER_ID).toString();
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        TaskVO taskVO = new TaskVO();
        taskVO.setTaskId(taskId);
        taskVO.setTaskStatus(taskStatus);
        taskVO.setUserId(Long.parseLong(userId));
        taskVO.setApiCode(apiCode);
        try {
            long count = taskService.updateTaskStatus(taskVO);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("count",count);
            jsonObject.put("taskStatus",taskVO.getTaskStatus());
            if(2==taskVO.getTaskStatus().intValue()){
                TaskVO task = taskService.findTaskVOByTaskVO(taskVO);
                if (task != null) {
                    bizTask.cleanCache(task.getStrategyId().intValue());
                }
            }
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }


    @GetMapping("/task/taskCode")
    public RespEntity getTaskCode(){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String taskCode = redisUtil.generateCodeNo("RWT");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("taskCode",taskCode);
        respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        return respEntity;
    }

    @GetMapping("/task/ruleSet/{taskId}")
    public RespEntity getRuleSets(@PathVariable("taskId")Long taskId,HttpServletRequest request){
        //Integer userId = Integer.parseInt(httpSession.getAttribute("userId").toString());
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String apiCode = request.getSession().getAttribute(SysDict.API_CODE).toString();
        List<Integer> userIds = authService.getAdminUserIds(request.getHeader(SysDict.COOKIE));
        TaskVO task = new TaskVO();
        task.setTaskId(taskId);
        task.setApiCode(apiCode);
        task.setIdList(userIds);
        try {
            List<RuleSetVO> ruleSetList = taskService.findRuleSetListByTask(task);
            JSONObject jsonObject = new JSONObject();
            RuleSetVO ruleSet = new RuleSetVO();
            ruleSet.setRuleSetId(0l);
            ruleSet.setRuleSetName("全部");
            if(CollectionUtils.isEmpty(ruleSetList)){
                List<RuleSet> list= new ArrayList<>();
                list.add(ruleSet);
                jsonObject.put("total",0);
                jsonObject.put("rows",list);
            }else{
                jsonObject.put("total",ruleSetList.size());
                ruleSetList.add(0,ruleSet);
                jsonObject.put("rows",ruleSetList);
            }
            respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }

    @GetMapping("/task/ruleSet/rule/{ruleSetId}")
    public RespEntity getRuleSetForTask(@PathVariable("ruleSetId")Long ruleSetId,HttpSession session){
        RespEntity entity = new RespEntity(RespCode.ERROR,null);
        Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String api_code = (String)session.getAttribute(SysDict.API_CODE);
        RuleSetVO ruleSetVO = new RuleSetVO();
        ruleSetVO.setRuleSetId(ruleSetId);
        ruleSetVO.setApiCode(api_code);
        List<RuleVO> ruleList = ruleSetService.findRulesByRuleSetForTask(ruleSetVO);
        entity.changeRespEntity(RespCode.SUCCESS,ruleList);
        return entity;
    }

    @GetMapping("/task/{taskId}")
    public RespEntity getTaskByTaskId(@PathVariable("taskId")Long taskId,
                                      @RequestParam(value = "withStrategyInfo",required = false,defaultValue = "1") Integer withStrategyInfo,
                                      HttpSession session,HttpServletRequest request){
        RespEntity respEntity = new RespEntity(RespCode.ERROR,null);
        String cookie = request.getHeader(SysDict.COOKIE);
        String userType = session.getAttribute(SysDict.USER_TYPE).toString();
        Integer userId = Integer.parseInt(session.getAttribute(SysDict.USER_ID).toString());
        List<Integer> userIds =  authService.getAdminUserIds(cookie);
        String apiCode = session.getAttribute(SysDict.API_CODE).toString();
        TaskVO query = new TaskVO();
        //query.setUserId(userId.longValue());
        query.setApiCode(apiCode);
        query.setTaskId(taskId);
        query.setIdList(userIds);
        query.setWithStrategyInfo(withStrategyInfo);
        try {
            TaskVO taskVO = taskService.findTaskVOByTaskVO(query);
            if(null!=taskVO&&null!=taskVO.getStrategyVO()){
                taskVO.setStrategyName(taskVO.getStrategyVO().getStrategyName());
                taskVO.setStrategyDescription(taskVO.getStrategyVO().getDescription());
                if(1==withStrategyInfo){
                    session.setAttribute("rwTaskStrategyTemplateRedisKey",strategyPrefix.concat(taskId.toString()));
                }
            }
            taskVO.setHandle(taskVO.getUserId().intValue()==userId.intValue()?1:0);
            respEntity.changeRespEntity(RespCode.SUCCESS,taskVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }

    @GetMapping("/task/initSession")
    public RespEntity initSession(
            @RequestParam(value = "apiCode",required = false,defaultValue = "4000159") String apiCode,
            @RequestParam(value = "userId",required = false,defaultValue = "1") String userId,
            @RequestParam(value = "userType",required = false,defaultValue = "0")String userType,
            @RequestParam(value = "groupId",required = false,defaultValue = "0")String groupId,
            @RequestParam(value = "username",required = false,defaultValue = "bairongadmin")String username,
            HttpSession session){
        RespEntity ret = new RespEntity (RespCode.SUCCESS,null);
        String publicKey = stringRedisTemplate.opsForValue().get("biz_credit:publicKey");
        if(!StringUtils.equals("prod",springProfilesActive)){
            session.setAttribute(SysDict.API_CODE,apiCode);//apicode(如果要切换成指定定用户需要更改)
            session.setAttribute(SysDict.USER_TYPE,userType);//用户类型(如果要切换成指定定用户需要更改)
            session.setAttribute(SysDict.USER_ID,userId);//用户id(如果要切换成指定定用户需要更改)
            session.setAttribute("res1","/report/reportTask,/report/reportCustomerBI");//用户权限
            session.setAttribute("crmApiId","1");//用户apicode对应得编号在auth.t_crm_api表中(如果要切换成指定定用户需要更改)
            session.setAttribute("institutionId","1");//用户apicode对应得编号在auth.t_institution表中(如果要切换成指定定用户需要更改)
            session.setAttribute("username",username);//用户登录账号(如果要切换成指定定用户需要更改)
            session.setAttribute("groupId",groupId);//用户分组编号(如果要切换成指定定用户需要更改)
            session.setAttribute("realname","王大");//用户真实姓名
            session.setAttribute("bizPublicKey",publicKey);//公用密钥
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("sessionId",session.getId());
            ret.changeRespEntity(RespCode.SUCCESS,jsonObject);
        }
        return ret;
    }





    /**
     * 通过任务Id获取任务进件参数列表
     * @param session
     * @return
     */
    @GetMapping("/taskHeadList")
    public RespEntity getHeadListByTaskId(@RequestParam(value = "taskId") Integer taskId,
                                          HttpSession session) {
        RespEntity entity = new RespEntity();
        entity.changeRespEntity(RespCode.NO_RESULT, null);
        try {
            String headList = taskService.getHeadListByTaskId(taskId);
            String[] heads = headList.split("_");
            JSONArray array = new JSONArray();
            for (String head : heads) {
                JSONObject object = new JSONObject();
                object.put("key", head);
                object.put("value", StringUtils.EMPTY);
                array.add(object);
            }
            entity.changeRespEntity(RespCode.SUCCESS, array);
        } catch (Exception e) {
            log.info("通过任务Id获取任务进件参数列表失败: " + e.getMessage(), e);
        }
        return entity;
    }

    @GetMapping(value = "/task/list")
    public JSONObject queryTasks(@RequestParam(name = "apiCode") String apiCode) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject data = taskService.getTasks(apiCode);
            if (CollectionUtils.isEmpty(data)) {
                jsonObject.put("code", "01");// 没有结果
                jsonObject.put("msg", "No result");
            } else {
                jsonObject.put("code", "00");
                jsonObject.put("msg", "Success");
                jsonObject.put("data", data);
            }
        } catch (Exception e) {
            jsonObject.put("code", "03");// 03 默认生成失败
            jsonObject.put("msg", "Failed");
            log.error("获取客户[" + apiCode + "]任务策略信息失败: " + e.getMessage(), e);
        }
        return jsonObject;
    }

    @PostMapping(value = "/task/entity")
    public JSONObject startMonitor(@RequestBody Map<String, Object> request) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = taskService.startMonitor(request);
        } catch (Exception e) {
            jsonObject.put("code", "03");
            jsonObject.put("msg", "Failed!");
            log.error("启动进件监控任务失败: " + e.getMessage(), e);
        }
        return jsonObject;
    }
}
