package com.biz.credit.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.config.rest.RestTemplateFactory;
import com.biz.credit.domain.*;
import com.biz.credit.service.IDNodeParamsService;
import com.biz.credit.service.IInputFileDetailContactService;
import com.biz.credit.service.IReportTaskService;
import com.biz.credit.service.IStrategyService;
import com.biz.credit.utils.*;
import com.biz.credit.vo.*;
import com.biz.decision.BizDecide;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;
import java.util.zip.ZipOutputStream;

@Slf4j
@RestController
@RequestMapping(value = "/reportTask")
public class ReportTaskController {
    @Value("${biz.task.old.reportBiReportDataList}")
    private String reportBiReportDataList;
    @Value("${bi_download_view_min_id}")
    private Integer Bi_Download_View_Min_Id;
    @Autowired
    private IStrategyService strategyService;
    @Autowired
    private IReportTaskService reportTaskService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private IDNodeParamsService nodeParamsService;
    @Autowired
    private IInputFileDetailContactService inputFileDetailContactService;
    @Autowired
    private RestTemplateFactory restTemplateFactory;
    @Autowired
    private BizDecide bizDecide;
    @Value("${select_limit_time}")
    private Integer select_limit_time;

    /**
     * pdf预览
     * 普通用户只可以预览 此apicode和userid下的所有pdf报告
     * 管理员 可以预览 此apicode下的所有pdf报告
     *
     * @return
     */
    @RequestMapping("/viewPdf")
    @ResponseBody
    public void viewPdf(@RequestParam(name = "inputFileDetailId") Integer inputFileDetailId,
                        HttpServletResponse response,
                        HttpServletRequest request,
                        HttpSession session) throws Exception {
        String apiCode = session.getAttribute("apiCode").toString();
        String userId = session.getAttribute("userId").toString();
        //String userType = session.getAttribute("userType").toString();
        String groupId = session.getAttribute("groupId").toString();
        log.info("inputFileDetailId=" + inputFileDetailId + "#apiCode=" + apiCode + "#userId=" + userId + "pdf预览");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            log.info("您的登陆信息已过期，请重新登陆");
            return;
        }
        InputFileDetailVO vo = new InputFileDetailVO();
        vo.setInputFileDetailId(inputFileDetailId);
        vo.setApiCode(apiCode);

        /*HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", request.getHeader("cookie"));
        String url = "http://auth-service/auth/users?" + "pageSize={pageSize}";
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("pageSize", "99999999");
        //查询此用户下的子用户，或者普通用户
        String jsonData = restTemplateFactory.restTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class, reqMap).getBody();
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONObject result = json.getJSONObject("result");
        log.info("inputFileDetailId=" + inputFileDetailId + "#usersData=" + result);
        if (result != null) {
            JSONArray usersJson = result.getJSONArray("rows");
            List users = new ArrayList();
            for (int i = 0; i < usersJson.size(); i++) {
                JSONObject object = usersJson.getJSONObject(i);
                users.add(object.getString("userId"));
            }
            log.info("inputFileDetailId=" + inputFileDetailId + "#users=" + users);
            vo.setUsers(users);
        } else {
            log.info("inputFileDetailId=" + inputFileDetailId + "#无此用户");
            return;
        }*/
        List users = new ArrayList(){{add(1);}};
        vo.setUsers(users);
        InputFileDetailVO inputFileDetail = vo.getInputFileDetailId() > 0 ? reportTaskService.findInputFileDetail(vo) : reportTaskService.findInputFileDetailForApiInput(vo);
        if (inputFileDetail == null) {
            log.info("inputFileDetailId=" + inputFileDetailId + "#apiCode=" + apiCode + "#userId=" + userId + "无数据");
            return;
        }
        String downLoadPath = inputFileDetail.getPdfFilePath() + inputFileDetail.getPdfFileName();
        File file = new File(downLoadPath);
        if (!file.exists()) {
            log.info("inputFileDetailId=" + inputFileDetailId + "#pdf文件不存在");
            return;
        }
        response.setContentType("application/pdf");
        FileInputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(new File(downLoadPath));
            out = response.getOutputStream();
            byte[] b = new byte[512];
            while ((in.read(b)) != -1) {
                out.write(b);
            }
        } catch (Exception e) {
            log.error("未找到文件：" + downLoadPath);
            log.error(e.getMessage(), e);
        } finally {
            out.flush();
            in.close();
            out.close();
        }
        //放入redis队列等待收集 begin
        if (inputFileDetailId >= Bi_Download_View_Min_Id && inputFileDetailId > 0) {
            DateTime dateTime = DateTime.now();
            BiReportData biReportData = new BiReportData();
            biReportData.setInputFileDetailId(inputFileDetailId.longValue());
            biReportData.setDataType(2);
            biReportData.setDate(dateTime.toString("yyyyMMdd"));
            biReportData.setDatetime(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
            biReportData.setMonth(Integer.parseInt(dateTime.toString("yyyyMM")));
            String jsonStr = JSONObject.toJSONString(biReportData);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            jsonObject.put("apiCode", apiCode);
            jsonObject.put("reportType", inputFileDetail.getReportType());
            jsonObject.put("groupId", groupId);
            jsonObject.put("industryId", inputFileDetail.getIndustryId());
            jsonObject.put("moduleTypeId", inputFileDetail.getModuleTypeId());
            stringRedisTemplate.opsForList().rightPush(reportBiReportDataList, jsonStr);
        }
    }

    /**
     * 查询报告类型模板信息
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryModuleTypeTemplateList", method = RequestMethod.GET)
    @ResponseBody
    public RespEntity queryModuleTypeTemplateList(@RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        try {
            List<ModuleTypeTemplate> list = reportTaskService.findModuleTypeTemplateList(new ModuleTypeTemplate());
            JSONObject jo = new JSONObject();
            jo.put("total", list.size());
            jo.put("rows", list);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.error("查询报告模块模板信息失败");
            log.error(e.getMessage(),e);
        }
        return ret;
    }

    @RequestMapping(value = "/queryTaskListByCondition",method = RequestMethod.GET)
    @ResponseBody
    public RespEntity queryTaskListByCondition(BiInputDataVO biInputDataVO,HttpSession session,
                                      @RequestParam(name = "pageNo",required =false,defaultValue = "1")Integer pageNo,
                                      @RequestParam(name="pageSize",required = false,defaultValue = "10")Integer pageSize){
        //近期时间和自定义时间只能选择其一当做条件；和公司名称参与查询
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String apiCode = (String) session.getAttribute("apiCode");
        if(StringUtils.isEmpty(apiCode)){
            ret.setMsg("您的登录信息已过期，请重新登录!");
            return ret;
        }
        Page<Task> page = PageHelper.startPage(pageNo, pageSize);
        try {
                List<Task> list = new ArrayList();

            if (StringUtils.isNotEmpty(biInputDataVO.getInterval())) {
                String interval = biInputDataVO.getInterval();//查询周期
                String intervalType = interval.substring(interval.length() - 1, interval.length());
                int size = Integer.parseInt(interval.substring(0, interval.length() - 1));
                if ("D".equals(intervalType)) {//1-按天
                    if (size > select_limit_time) {
                        ret.setMsg("查询时间不能超过" + select_limit_time + "天");
                        return ret;
                    }
                    Date endDate = new Date();
                    Date beginDate = DateUtil.addDate(endDate, intervalType, size);

                    biInputDataVO.setStartDate(DateUtil.parseDateToStr(beginDate, "yyyy-MM-dd"));
                    biInputDataVO.setEndDate(DateUtil.parseDateToStr(endDate, "yyyy-MM-dd"));

                    list = reportTaskService.queryTaskListByCondition(biInputDataVO);
                } else {
                    ret.setMsg("查无数据");
                    ret.setCode("01");
                    return ret;
                }
            } else {//自定义查询时间
                String startDate_ = biInputDataVO.getStartDate();
                String endDate_ = biInputDataVO.getEndDate();
                if (StringUtils.isNotEmpty(startDate_) && StringUtils.isNotEmpty(endDate_)) {
                    List countDay = DateUtil.getDayListOfDate(startDate_, endDate_);
                    if (countDay.size() > select_limit_time) {
                        ret.setMsg("自定义查询时间不能超过" + select_limit_time + "天");
                        return ret;
                    }
                    biInputDataVO.setStartYear(startDate_);
                    biInputDataVO.setEndYear(endDate_);
                    list = reportTaskService.queryTaskListByCondition(biInputDataVO);
                } else {
                    ret.setMsg("查询开始时间或者结束时间不能为空");
                    ret.setCode("07");//参数错误
                    return ret;
                }
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("total",page.getTotal());
            jsonObject.put("rows",page.getResult());
            ret.changeRespEntity(RespCode.SUCCESS,jsonObject);
        } catch (Exception e) {
            log.info("查询所有任务信息失败");
            return ret;
        }
         return ret;

    }



    /**
     * 查询所有任务信息
     *
     * @return 分页查询  每页10条  每次返回1页
     * @throws Exception
     */
    @RequestMapping(value = "/queryTaskList", method = RequestMethod.GET)
    @ResponseBody
    public RespEntity queryTaskList(@RequestParam(name = "taskId", required = false) Integer taskId,
                                    @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                    @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        Task task = new Task();
        String apiCode = (String) session.getAttribute("apiCode");
        String userId = (String) session.getAttribute("userId");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            ret.setMsg("您的登陆信息已过期，请重新登陆");
            return ret;
        }
        task.setUserId(Integer.parseInt(userId));
        task.setApiCode(apiCode);
        task.setTaskId(taskId);

        Page<Task> page = PageHelper.startPage(pageNo, pageSize);
        try {
            List<Task> list = reportTaskService.findTaskList(task);
            JSONObject jo = new JSONObject();
            jo.put("total", page.getTotal());
            jo.put("rows", page.getResult());
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.info("查询所有任务信息失败");
            return ret;
        }
        return ret;
    }

    /**
     * 根据任务id查询 进件数据详情
     *
     * @return 分页查询  每页10条  每次返回1页
     * @throws Exception
     */
    @RequestMapping(value = "/queryInputFileDetailList", method = RequestMethod.GET)
    @ResponseBody
    public RespEntity queryInputFileDetailList(@RequestParam(name = "taskId", required = true) Integer taskId,
                                               @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                               @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize, HttpSession session) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        Page<InputFileDetail> page = PageHelper.startPage(pageNo, pageSize);
        String userId = (String) session.getAttribute("userId");
        if (StringUtils.isEmpty(userId)) {
            ret.setMsg("您的登陆信息已过期，请重新登陆");
            return ret;
        }
        try {
            InputFileDetailVO inputFileDetail = new InputFileDetailVO();
            inputFileDetail.setTaskId(taskId);
            inputFileDetail.setUserId(Integer.parseInt(userId));
            List<InputFileDetailVO> list = reportTaskService.findInputFileDetailList(inputFileDetail);
            if(!CollectionUtils.isEmpty(list)){
                list.forEach(inputFileDetailVO -> {
                    if(StringUtils.isEmpty(inputFileDetailVO.getKeyNo())){
                        inputFileDetailVO.setKeyNo(inputFileDetailVO.getName());
                    }
                });
            }
            JSONObject jo = new JSONObject();
            jo.put("total", page.getTotal());
            jo.put("rows", page.getResult());
            if (list.size() > 0) {
                InputFileDetailVO vo = list.get(0);
                jo.put("taskStatus", vo.getTaskStatus());
            } else {
                jo.put("taskStatus", 1);//1处理中 2处理完成
            }
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.info("根据任务id查询进件数据详情失败");
            return ret;
        }
        return ret;
    }

    /**configScore
     * 根据进件类型、任务名称、公司名称 模糊查询任务详情
     *
     * @return 分页查询  每页20条  每次返回1页
     * @throws Exception
     */
    @RequestMapping(value = "/queryTaskVague", method = RequestMethod.GET)
    @ResponseBody
    public RespEntity queryTaskVague(@RequestParam(name = "type", required = false, defaultValue = "0") String type, @RequestParam(name = "beginTime", required = false) String beginTime,
                                     @RequestParam(name = "endTime", required = false) String endTime,
                                     @RequestParam(name = "taskType", required = false, defaultValue = "0") Integer taskType,
                                     @RequestParam(name = "name", required = false) String name, @RequestParam(name = "pageNo", required = false, defaultValue = "1") Integer pageNo,
                                     @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize, HttpSession session) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String userId = (String) session.getAttribute("userId");
        String apiCode = (String) session.getAttribute("apiCode");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            ret.setCode("");
            ret.setMsg("您的登陆信息已过期，请重新登陆");
            return ret;
        }
        if (beginTime != null && !"".equals(beginTime)) {
            beginTime = beginTime.split(" ")[0] + " 00:00:00";
        }
        if (endTime != null && !"".equals(endTime)) {
            endTime = endTime.split(" ")[0] + " 23:59:59";
        }
        if ("".equals(name) || name == null) {
            JSONObject jo = new JSONObject();
            jo.put("total", 0);
            jo.put("rows", new ArrayList<>());
            ret.changeRespEntity(RespCode.SUCCESS, jo);
            return ret;
        }
        if ("0".equals(type)) {
            //全部
            try {
                TaskVO task = new TaskVO();
                task.setTaskName(name);//任务名称
                task.setUserId(Integer.parseInt(userId));
                task.setApiCode(apiCode);
                task.setBeginTime(beginTime);
                task.setEndTime(endTime);
                task.setTaskType(taskType);
                //1-根据任务名称查询task列表
                List<Task> taskListName = reportTaskService.queryTaskByName(task);
                Set<Integer> setTaskIds = new HashSet<>();
                for (Task taskid : taskListName) {
                    setTaskIds.add(taskid.getTaskId());
                }

                InputFileDetailVO inputFileDetail = new InputFileDetailVO();
                inputFileDetail.setUserId(Integer.parseInt(userId));
                inputFileDetail.setApiCode(apiCode);
                inputFileDetail.setKeyNo(name);//公司名称
                inputFileDetail.setBeginTime(beginTime);
                inputFileDetail.setEndTime(endTime);
                inputFileDetail.setTaskType(taskType);
                //2-根据公司名称组织任务列表
                List<InputFileDetail> taskListKeyNo = reportTaskService.queryTaskByInputDetailName(inputFileDetail);
                Map<Integer, List<InputFileDetail>> taskDetails = new HashMap<>();
                for (InputFileDetail detail : taskListKeyNo) {
                    Integer taskId = detail.getTaskId();
                    List<InputFileDetail> detailList = taskDetails.get(taskId);
                    if (CollectionUtils.isEmpty(detailList)) {
                        taskDetails.put(taskId, new ArrayList<>());
                    }
                    taskDetails.get(taskId).add(detail);
                }

                List<Task> arrayList = new ArrayList<Task>();
                for (Integer taskId : taskDetails.keySet()) {
                    Task tkParam = new Task();
                    tkParam.setTaskId(taskId);
                    Task t = reportTaskService.queryTaskById(tkParam);
                    //如果第1部分数据中的task与第2部分中重合，删除t第1部分中相同的task数据,去重
                    if (setTaskIds.contains(t.getTaskId())) {
                        taskListName.remove(t);
                    }
                    t.setInputFileDetails(taskDetails.get(taskId));
                    arrayList.add(t);
                }

                //将第一部分数据与第二部分数据合并，已经去重
                arrayList.addAll(taskListName);//合并

                //手动分页
                List<Task> taskCount = ReportUtils.getPageList(arrayList, pageNo, pageSize);

                JSONObject jo = new JSONObject();
                jo.put("total", arrayList.size());
                jo.put("rows", taskCount);
                ret.changeRespEntity(RespCode.SUCCESS, jo);
            } catch (Exception e) {
                log.error("根据任务名称或者公司名称模糊查询任务详情失败");
                log.error(e.getMessage(),e);
                return ret;
            }
        } else if ("1".equals(type)) {
            //公司
            try {
                //2-根据公司名称组织任务列表
                InputFileDetailVO inputFileDetail = new InputFileDetailVO();
                inputFileDetail.setUserId(Integer.parseInt(userId));
                inputFileDetail.setApiCode(apiCode);
                inputFileDetail.setKeyNo(name);//公司名称
                inputFileDetail.setBeginTime(beginTime);
                inputFileDetail.setEndTime(endTime);
                inputFileDetail.setTaskType(taskType);
                List<InputFileDetail> taskListKeyNo = reportTaskService.queryTaskByInputDetailName(inputFileDetail);
                Map<Integer, List<InputFileDetail>> taskDetails = new HashMap<>();
                for (InputFileDetail detail : taskListKeyNo) {
                    Integer taskId = detail.getTaskId();
                    List<InputFileDetail> detailList = taskDetails.get(taskId);
                    if (CollectionUtils.isEmpty(detailList)) {
                        taskDetails.put(taskId, new ArrayList<>());
                    }
                    taskDetails.get(taskId).add(detail);
                }

                List<Task> arrayList = new ArrayList<Task>();
                for (Integer taskId : taskDetails.keySet()) {
                    Task tkParam = new Task();
                    tkParam.setTaskId(taskId);
                    Task t = reportTaskService.queryTaskById(tkParam);
                    t.setInputFileDetails(taskDetails.get(taskId));
                    arrayList.add(t);
                }

                //手动分页
                List<Task> taskCount = ReportUtils.getPageList(arrayList, pageNo, pageSize);

                JSONObject jo = new JSONObject();
                jo.put("total", arrayList.size());
                jo.put("rows", taskCount);
                ret.changeRespEntity(RespCode.SUCCESS, jo);
            } catch (Exception e) {
                log.error("根据任务名称或者公司名称模糊查询任务详情失败");
                log.error(e.getMessage(),e);
                return ret;
            }
        } else if ("2".equals(type)) {
            //任务
            try {
                TaskVO task = new TaskVO();
                task.setTaskName(name);//任务名称
                task.setUserId(Integer.parseInt(userId));
                task.setApiCode(apiCode);
                task.setBeginTime(beginTime);
                task.setEndTime(endTime);
                task.setTaskType(taskType);
                //1-根据任务名称查询task列表
                List<Task> taskListName = reportTaskService.queryTaskByName(task);

                //手动分页
                List<Task> taskCount = ReportUtils.getPageList(taskListName, pageNo, pageSize);

                JSONObject jo = new JSONObject();
                jo.put("total", taskListName.size());
                jo.put("rows", taskCount);
                ret.changeRespEntity(RespCode.SUCCESS, jo);
            } catch (Exception e) {
                log.error("根据任务名称或者公司名称模糊查询任务详情失败");
                log.error(e.getMessage(),e);
                return ret;
            }
        }

        return ret;
    }

    /**
     * 批量pdf文件下载  zip打包压缩
     * 根据InputFileDetailids 下载pdf文件
     */
    @RequestMapping(value = "/downLoadZipFile")
    public void downLoadZipFile(@RequestParam(name = "fileDetailIds", required = true) String fileDetailIds, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException {
        log.info("downLoadZipFile批量下载开始!");
        String apiCode = (String) session.getAttribute("apiCode");
        String userId = (String) session.getAttribute("userId");
        //String userType = session.getAttribute("userType").toString();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            log.info("您的登陆信息已过期，请重新登陆");
            return;
        }

        String zipName = "report.zip";
        if ("".equals(fileDetailIds) || fileDetailIds == null) {
            log.info("下载进件明细ids不能为空");
            return;
        }

        String[] strList = fileDetailIds.split(",");
        List<InputFileDetailVO> fileList = null;//查询数据库中记录
        try {
            fileList = reportTaskService.findInputFileDetailListByIds(strList, Integer.parseInt(userId));
            if (fileList.size() > 0) {
                InputFileDetail i = fileList.get(0);
                Task t = new Task();
                t.setTaskId(i.getTaskId());
                Task task = reportTaskService.queryTaskById(t);
                if (task.getTaskName() != null && !"".equals(task.getTaskName())) {
                    String userAgentStr = request.getHeader("USER-AGENT");
                    if (StringUtils.isNotEmpty(userAgentStr)) {
                        String UserAgent = userAgentStr.toLowerCase();
                        if (UserAgent.contains("msie") || UserAgent.contains("trident") || UserAgent.contains("edge")) {
                            // 针对IE或者以IE为内核的浏览器  msie-ie11以下  trident-ie11   edge-Edge浏览器
                            zipName = URLEncoder.encode(task.getTaskName() + ".zip", "UTF-8");
                        } else {
                            // 非IE浏览器的处理
                            zipName = new String((task.getTaskName() + ".zip").getBytes("UTF-8"), "ISO-8859-1");
                        }
                        /*if(UserAgent.indexOf("firefox") >= 0){
                            zipName =  new String((task.getTaskName()+".zip").getBytes("UTF-8"),"iso-8859-1");
                        }else {
                            try {
                                zipName = URLEncoder.encode(task.getTaskName(),"UTF-8")+".zip";//为了解决中文名称乱码问题
                            }catch (UnsupportedEncodingException e){
                                log.info("文件名字转换异常");
                                e.printStackTrace();
                            }
                        }*/
                    }
                }
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", "attachment; filename=" + zipName);
                ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
                try {
                    for (Iterator<InputFileDetailVO> it = fileList.iterator(); it.hasNext(); ) {
                        InputFileDetail file = it.next();
                        File pdfFile = new File(file.getPdfFilePath() + file.getPdfFileName());
                        if (pdfFile.exists()) {
                            if(StringUtils.isEmpty(file.getKeyNo())){
                                file.setKeyNo(file.getName());
                            }
                            if (StringUtils.isNotEmpty(file.getKeyNo()) && StringUtils.isNotEmpty(file.getPdfFileName()) && StringUtils.isNotEmpty(file.getPdfFilePath())) {
                                ZipUtils.doCompress(file.getKeyNo(), file.getPdfFilePath() + file.getPdfFileName(), out);
                                response.flushBuffer();
                            }
                        }
                    }
                    log.info("downLoadZipFile批量下载完成!");
                } catch (Exception e) {
                    log.info("downLoadZipFile批量下载pdf文件失败");
                    return;
                } finally {
                    out.close();
                }
            } else {
                log.info("InputFileDetails[{}]useid[{}]apicode[{}]未查询到进件数据", fileDetailIds, userId, apiCode);
                return;
            }
        } catch (Exception e) {
            log.info("findInputFileDetailListByIds查询失败");
            return;
        }

    }

    /**
     * 批量pdf文件下载  zip打包压缩
     * 批量下载任务taskid下的所有pdf文件
     */
    @RequestMapping(value = "/downLoadZipFileByTaskId")
    public void downLoadZipFileByTaskId(@RequestParam(name = "taskId", required = true) Integer taskId, HttpServletResponse response, HttpServletRequest request, HttpSession session) throws IOException {
        log.info("任务[{}]downLoadZipFileByTaskId批量下载开始!", taskId);
        String apiCode = (String) session.getAttribute("apiCode");
        String userId = (String) session.getAttribute("userId");
        //String userType = session.getAttribute("userType").toString();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            log.info("您的登陆信息已过期，请重新登陆");
            return;
        }

        String zipName = "report.zip";
        List<InputFileDetailVO> fileList = null;//根据taskid查询进件数据
        try {
            InputFileDetailVO vo = new InputFileDetailVO();
            vo.setTaskId(taskId);
            vo.setUserId(Integer.parseInt(userId));
            vo.setApiCode(apiCode);
            fileList = reportTaskService.findInputFileDetailByTaskId(vo);
            if (fileList.size() > 0) {
                InputFileDetail i = fileList.get(0);
                Task t = new Task();
                t.setTaskId(i.getTaskId());
                Task task = reportTaskService.queryTaskById(t);//查询任务详情
                if (task.getTaskName() != null && !"".equals(task.getTaskName())) {
                    String userAgentStr = request.getHeader("USER-AGENT");
                    if (StringUtils.isNotEmpty(userAgentStr)) {
                        String UserAgent = userAgentStr.toLowerCase();
                        if (UserAgent.contains("msie") || UserAgent.contains("trident") || UserAgent.contains("edge")) {
                            // 针对IE或者以IE为内核的浏览器  msie-ie11以下  trident-ie11   edge-Edge浏览器
                            zipName = URLEncoder.encode(task.getTaskName() + ".zip", "UTF-8");
                        } else {
                            // 非IE浏览器的处理
                            zipName = new String((task.getTaskName() + ".zip").getBytes("UTF-8"), "ISO-8859-1");
                        }
                        /*if(UserAgent.indexOf("firefox") >= 0){
                            zipName =  new String((task.getTaskName()+".zip").getBytes("UTF-8"),"iso-8859-1");
                        }else {
                            try {
                                zipName = URLEncoder.encode(task.getTaskName(),"UTF-8")+".zip";//为了解决中文名称乱码问题
                            }catch (UnsupportedEncodingException e){
                                log.info("文件名字转换异常");
                                e.printStackTrace();
                            }
                        }*/
                    }
                }
                response.setContentType("APPLICATION/OCTET-STREAM");
                response.setHeader("Content-Disposition", "attachment; filename=" + zipName);
                ZipOutputStream out = new ZipOutputStream(response.getOutputStream());
                Set<String> keyNoSet = new HashSet<>();
                int j = 1;
                try {
                    for (Iterator<InputFileDetailVO> it = fileList.iterator(); it.hasNext(); ) {
                        InputFileDetail file = it.next();
                        if(StringUtils.isEmpty(file.getKeyNo())){
                            file.setKeyNo(file.getName());
                        }
                        if(keyNoSet.contains(file.getKeyNo())){
                            file.setKeyNo(file.getKeyNo().concat("(").concat(String.valueOf(j)).concat(")"));
                            j++;
                        }else{
                            keyNoSet.add(file.getKeyNo());
                        }
                        if (StringUtils.isNotEmpty(file.getKeyNo()) && StringUtils.isNotEmpty(file.getPdfFileName()) && StringUtils.isNotEmpty(file.getPdfFilePath())) {
                            File pdfFile = new File(file.getPdfFilePath() + file.getPdfFileName());
                            if (pdfFile.exists()) {
                                ZipUtils.doCompress(file.getKeyNo(), file.getPdfFilePath() + file.getPdfFileName(), out);
                                response.flushBuffer();
                            }
                        }
                    }
                    log.info("任务[{}]downLoadZipFileByTaskId批量下载完成!", taskId);
                } catch (Exception e) {
                    log.info("downLoadZipFileByTaskId批量下载pdf文件失败: " + e.getMessage(), e);
                    return;
                } finally {
                    out.close();
                }
            } else {
                log.info("任务[{}]useid[{}]apicode[{}]未查询到进件数据", taskId, userId, apiCode);
                return;
            }
        } catch (Exception e) {
            log.info("findInputFileDetailByTaskId查询失败: " + e.getMessage(), e);
            return;
        }
    }

    /**
     * get请求
     * 单个pdf文件下载
     * 普通用户只可以预览 此apicode和userid下的所有pdf报告
     * 管理员 可以预览 此apicode下的所有pdf报告
     */
    @RequestMapping(value = "/downloadSignal")
    public void downloadSignal(@RequestParam(name = "inputFileDetailId") Integer inputFileDetailId,
                               HttpSession session,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        String userId = session.getAttribute("userId").toString();
        String userType = session.getAttribute("userType").toString();
        String apiCode = session.getAttribute("apiCode").toString();
        String groupId = session.getAttribute("groupId").toString();
        log.info("inputFileDetailId=" + inputFileDetailId + "#apiCode=" + apiCode + "#userId=" + userId + "单个pdf下载");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            log.info("您的登陆信息已过期，请重新登陆");
            return;
        }

        InputFileDetailVO vo = new InputFileDetailVO();
        vo.setInputFileDetailId(inputFileDetailId);
        vo.setApiCode(apiCode);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Cookie", request.getHeader("cookie"));
        String url = "http://auth-service/auth/users?" + "pageSize={pageSize}";
        Map<String, String> reqMap = new HashMap<>();
        reqMap.put("pageSize", "99999999");
        //查询此用户下的子用户，或者普通用户
        String jsonData = restTemplateFactory.restTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class, reqMap).getBody();
        JSONObject json = JSONObject.parseObject(jsonData);
        JSONObject result = json.getJSONObject("result");
        log.info("inputFileDetailId=" + inputFileDetailId + "#usersData=" + result);
        if (result != null) {
            JSONArray usersJson = result.getJSONArray("rows");
            List users = new ArrayList();
            for (int i = 0; i < usersJson.size(); i++) {
                JSONObject object = usersJson.getJSONObject(i);
                users.add(object.getString("userId"));
            }
            log.info("inputFileDetailId=" + inputFileDetailId + "#users=" + users);
            vo.setUsers(users);
        } else {
            log.info("inputFileDetailId=" + inputFileDetailId + "#无此用户");
            return;
        }

        InputFileDetailVO data = null;
        try {
            data = reportTaskService.findInputFileDetail(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (data == null) {
            log.info("inputFileDetailId=" + inputFileDetailId + "#apiCode=" + apiCode + "#userId=" + userId + "此用户无权限下载此报告");
            return;
        }
        String pdfFilePath = data.getPdfFilePath() + data.getPdfFileName();
        String key_no = StringUtils.isEmpty(data.getKeyNo())?data.getName():data.getKeyNo();
        if (key_no == null || "".equals(key_no)) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            key_no = sdf.format(date);
        }

        File file = new File(pdfFilePath);
        if (!file.exists()) {
            log.info("inputFileDetailId=" + inputFileDetailId + "#pdf文件不存在");
            return;
        }
        String finalFileName = null;

        //解决各个浏览器中文名称乱码问题
        boolean isMSIE = isMSBrowser(request);
        if (isMSIE) {
            //IE浏览器的乱码问题解决
            finalFileName = URLEncoder.encode(key_no, "UTF-8");
        } else {
            //万能乱码问题解决
            finalFileName = new String(key_no.getBytes("UTF-8"), "ISO-8859-1");
        }
        response.setHeader("Content-disposition", "attachment; filename="
                + finalFileName + ".pdf");// filename是下载的xls的名，建议最好用英文
        response.setContentType("application/pdf;charset=UTF-8");// 设置类型
        response.setHeader("Pragma", "No-cache");// 设置头
        response.setHeader("Cache-Control", "no-cache");// 设置头
        response.setHeader("Content-Type", "application/pdf");// 设置头
        response.setDateHeader("Expires", 0);// 设置日期头
        FileCopyUtils.copy(new FileInputStream(file), response.getOutputStream());

        //放入redis队列等待收集 begin
        if (inputFileDetailId >= Bi_Download_View_Min_Id) {
            DateTime dateTime = DateTime.now();
            BiReportData biReportData = new BiReportData();
            biReportData.setInputFileDetailId(inputFileDetailId.longValue());
            biReportData.setDataType(1);
            biReportData.setDate(dateTime.toString("yyyyMMdd"));
            biReportData.setDatetime(dateTime.toString("yyyy-MM-dd HH:mm:ss"));
            biReportData.setMonth(Integer.parseInt(dateTime.toString("yyyyMM")));
            String jsonStr = JSONObject.toJSONString(biReportData);
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            jsonObject.put("apiCode", apiCode);
            jsonObject.put("reportType", data.getReportType());
            jsonObject.put("groupId", groupId);
            jsonObject.put("industryId", data.getIndustryId());
            jsonObject.put("moduleTypeId", data.getModuleTypeId());
            stringRedisTemplate.opsForList().rightPush(reportBiReportDataList, jsonStr);
        }
    }

    /**
     * get请求
     *
     * @param response 下载进件模板
     * @throws IOException
     * @returnHttpServletRequest request
     */
    @RequestMapping(value = "/downloadExcelTemplate")
    public void downloadExcelTemplate(@RequestParam(name = "moduleTypeId") Integer moduleTypeId, HttpServletResponse response) throws IOException {
        log.info("downloadExcelTemplate开始下载----");

        try {
            ModuleTypeVO moduleType = reportTaskService.queryModuleType(new ModuleTypeVO(moduleTypeId));
            //去除企业开户账户
            if (null != moduleType && StringUtils.isNotEmpty(moduleType.getColumnHead())) {
                moduleType.setColumnHead(moduleType.getColumnHead().replaceAll("_".concat(Constants.bankId), StringUtils.EMPTY));
            }
            String headerStr = "template";//文件名
            headerStr = new String(headerStr.getBytes("gb2312"), "ISO8859-1");// headerString为中文时转码
            response.setHeader("Content-disposition", "attachment; filename="
                    + headerStr + ".xlsx");// filename是下载的xls的名，建议最好用英文
            response.setContentType("application/msexcel;charset=UTF-8");// 设置类型
            response.setHeader("Pragma", "No-cache");// 设置头
            response.setHeader("Cache-Control", "no-cache");// 设置头
            response.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");// 设置头
            response.setDateHeader("Expires", 0);// 设置日期头
            String columnHead = moduleType.getColumnHead();
            if (StringUtils.isNotEmpty(moduleType.getColumnHeadPerson())) {
                moduleType.setColumnHeadPerson(moduleType.getColumnHeadPerson() + "_" + moduleType.getColumnHeadPerson());
            }
            Workbook workbook = ExcelUtil.createWorkBookByHeaders(columnHead.split("_"), StringUtils.isEmpty(moduleType.getColumnHeadPerson()) ? null : moduleType.getColumnHeadPerson().split("_"));
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            log.info("下载进件模板失败");
        }
    }

    /**
     * get请求
     *
     * @param flowId
     * @param response 下载进件模板
     */
    @GetMapping(value = "/downloadTemplate")
    public void downloadTemplate(@RequestParam(name = "flowId") Integer flowId, HttpSession session, HttpServletResponse response) {
        log.info("downloadTemplate----");
        try {
            String apiCode = session.getAttribute(Constants.API_CODE).toString();
            List<ParamVO> params = strategyService.getFlowParams(apiCode, flowId);
            List<String> head = new ArrayList<>();
            if (params != null) {
                //企业参数数量
                long count = params.stream().filter(v -> Objects.equals(v.getType(), 1)).count();
                for (ParamVO vo : params) {
                    if (Objects.equals(vo.getType(), 2)) {
                        //有企业参数, 则个人参数添加法人前缀
                        if (count > 0) {
                            head.add("法人" + vo.getName());
                        }
                        //无企业参数, 则个人参数添加申请人前缀
                        else {
                            head.add("申请人" + vo.getName());
                        }
                    } else {
                        head.add(vo.getName());
                    }
                }
            }
            String[] header = new String[head.size()];
            head.toArray(header);
            //
            String headerStr = "template";//文件名
            headerStr = new String(headerStr.getBytes("gb2312"), "ISO8859-1");// headerString为中文时转码
            response.setHeader("Content-disposition", "attachment; filename="
                    + headerStr + ".xlsx");// filename是下载的xls的名，建议最好用英文
            response.setContentType("application/msexcel;charset=UTF-8");// 设置类型
            response.setHeader("Pragma", "No-cache");// 设置头
            response.setHeader("Cache-Control", "no-cache");// 设置头
            response.setHeader("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");// 设置头
            response.setDateHeader("Expires", 0);// 设置日期头
            //
            Workbook workbook = ExcelUtil.createWorkBookByHeaders(header, null);
            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            log.error("下载进件模板失败: " + e.getMessage(), e);
        }
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
     * 根据模板类型reporttype查询 模板内容详情
     * 目录预览
     * @throws Exception
     */
    @RequestMapping(value = "/queryModuleTypeDetail", method = RequestMethod.GET)
    @ResponseBody
    public RespEntity queryModuleTypeDetail(@RequestParam(name = "moduleTypeId") Integer moduleTypeId, HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String userId = (String) session.getAttribute("userId");
        String apiCode = (String) session.getAttribute("apiCode");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            ret.setMsg("您的登陆信息已过期，请重新登陆");
            return ret;
        }
        try {
            ModuleTypeVO moduleType = reportTaskService.queryModuleType(new ModuleTypeVO(apiCode, moduleTypeId));
            JSONObject jo = new JSONObject();
            if(null!=moduleType.getFlowId()&&moduleType.getFlowId()>0){
                JSONArray catalog = bizDecide.catalogView(apiCode, moduleType.getFlowId().intValue());
                jo.put("total", catalog.size());
                jo.put("strategyId",moduleType.getStrategyId());
                jo.put("rows", catalog);
            }
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.error("根据模板类型查询模板内容详情失败:" + e.getMessage(), e);
            return ret;
        }
        return ret;
    }

    @GetMapping("/inputFileDetail")
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
        log.info("inputFileDetailVO[{}][userId:{}]:{}", inputFileDetailId, userId, inputFileDetailVO);
        moduleTypeId = inputFileDetailVO == null ? moduleTypeId : inputFileDetailVO.getModuleTypeId();
        ModuleTypeVO moduleType = reportTaskService.queryModuleType(new ModuleTypeVO(apiCode, moduleTypeId));
        if (null != moduleType) {
            if(null!=moduleType.getFlowId()&&moduleType.getFlowId()>0){
                DFlowVO dFlowVO = new DFlowVO();
                dFlowVO.setFlowId(moduleType.getFlowId());
                dFlowVO.setApiCode(apiCode);
                dFlowVO.setStatus(-1);
                DTaskVO dTaskVO = nodeParamsService.findDTaskVOByDFlowVO(dFlowVO);
                if(null!=inputFileDetailVO){
                    JSONObject retInputJson = JSONObject.parseObject(JSONObject.toJSONString(inputFileDetailVO));
                    Map<String,DTaskGroupVO> groupNameMap = new HashMap<>();
                        dTaskVO.getRows().forEach(group->{
                            groupNameMap.put(group.getTitle(),group);
                            group.setTitle(group.getTitle().replaceAll("填写",StringUtils.EMPTY));
                            group.getItems().forEach(param->{
                                String propName = Constants.DetailPropMap.get(param.getKey());
                                if(StringUtils.isNotEmpty(propName)){
                                    String value = retInputJson.getString(propName);
                                    param.setValue(value);
                                }
                            });
                        });
                        if(!groupNameMap.containsKey(Constants.DetailCompanyGroupTitle)&&null!=groupNameMap.get(Constants.DetailLegalPersonTitle)){
                            groupNameMap.get(Constants.DetailLegalPersonTitle).setTitle(Constants.DetailApplyPersonTitle.replaceAll("填写",StringUtils.EMPTY));
                        }
                        if(dTaskVO.getRelatedP().equals(1)){
                            List<InputFileDetailContactVO> relatedPersonList = inputFileDetailContactService.findInputFileDetailContactVOList(inputFileDetailId);
                            while(!CollectionUtils.isEmpty(relatedPersonList)){
                                InputFileDetailContact relatedPerson = relatedPersonList.remove(0);
                                JSONObject rpJson = JSONObject.parseObject(JSONObject.toJSONString(relatedPerson));
                                Integer relatedPGroupIndex = dTaskVO.getRelatedPIndexList().remove(0);
                                DTaskGroupVO relatedPGroup = dTaskVO.getRows().get(relatedPGroupIndex);
                                relatedPGroup.getItems().forEach(item->{
                                    String propName = Constants.DetailPropMap.get(item.getKey());
                                    String value = rpJson.getString(propName);
                                    item.setValue(value);
                                });
                            }
                        }

                }
                //姓名和身份证换个位置
                dTaskVO.getRows().forEach(group->{
                    List<Integer> requiredChangeList = new ArrayList<>();
                    Stream.iterate(0, i -> i + 1).limit(group.getItems().size()).forEach(i -> {
                        DTaskParamVO item = group.getItems().get(i);
                        if(item.getKey().contains("姓名")||item.getKey().contains("身份证")){
                            requiredChangeList.add(i);
                        }
                    });
                    if(requiredChangeList.size()==2){
                        Collections.swap(requiredChangeList,requiredChangeList.get(0),requiredChangeList.get(1));
                    }
                });
                respEntity.changeRespEntity(RespCode.SUCCESS,dTaskVO);
                return respEntity;
            }else{
                List<JSONObject> headFinalList = new ArrayList<>();
                //去除企业开户账户
                if (null == inputFileDetailVO) {
                    moduleType.setColumnHead(moduleType.getColumnHead().replaceAll("_".concat(Constants.bankId), StringUtils.EMPTY));
                } else {
                    DateTime createTime = DateTime.parse(inputFileDetailVO.getCreateTime(), DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
                    //这是去除bankid的时间点
                    if (createTime.getMillis() > Constants.BANK_ID_REMOVED_TIME) {
                        moduleType.setColumnHead(moduleType.getColumnHead().replaceAll("_".concat(Constants.bankId), StringUtils.EMPTY));
                    }
                }
                List<String> headerList = Arrays.asList(moduleType.getColumnHead().split("_"));
                List<String> headListPerson = StringUtils.isEmpty(moduleType.getColumnHeadPerson()) ? new ArrayList<>() : Arrays.asList(moduleType.getColumnHeadPerson().split("_"));
                Map<String, List<String>> headerTemplateListTmp = new LinkedHashMap<>(Constants.DetailInputGroupList);
                headerTemplateListTmp.forEach((title, headerGroup) -> {
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
                    if (!CollectionUtils.isEmpty(hg.getJSONArray("items")))
                        headFinalList.add(hg);
                });
                if (null != inputFileDetailVO) {
                    inputFileDetailVO.dealHeaderJson(headFinalList);
                } else if (!CollectionUtils.isEmpty(headFinalList)
                        && StringUtils.equals(headFinalList.get(headFinalList.size() - 1).getString("title"), Constants.DetailRelatedPersonTitle)) {
                    JSONObject h = JSONObject.parseObject(headFinalList.get(headFinalList.size() - 1).toJSONString());
                    h.put("index", headFinalList.size());
                    headFinalList.add(h);

                }
                JSONObject jo = new JSONObject();
                jo.put("rows", headFinalList);
                jo.put("moduleTypeId",moduleType.getModuleTypeId());
                respEntity.changeRespEntity(RespCode.SUCCESS, jo);
            }
        }
        return respEntity;
    }

    @GetMapping("/inputFileDetailNew")
    public RespEntity getInputDetailParams(@RequestParam(value = "inputFileDetailId") Integer inputFileDetailId) {
        RespEntity resp;
        try {
            Flow flow = reportTaskService.getInputFileDetailParams(inputFileDetailId);
            resp = RespEntity.success();
            resp.setData(flow);
        } catch (Exception e) {
            log.error("" + e.getMessage(), e);
            resp = RespEntity.error();
        }
        return resp;
    }

    /**
     * 查询单个进件详情
     * 暂无使用
     *
     * @param inputFileDetailId
     * @param session
     * @return
     */
    @GetMapping("/queryInputDetailInfo")
    public RespEntity queryInputDetailInfo(
            @RequestParam(value = "inputFileDetailId", required = true) Integer inputFileDetailId,
            HttpSession session, HttpServletRequest request) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String userId = session.getAttribute("userId").toString();
        String userType = session.getAttribute("userType").toString();
        String apiCode = session.getAttribute("apiCode").toString();
        try {
            log.info("inputFileDetailId=" + inputFileDetailId + "#apiCode=" + apiCode + "#userId=" + userId + "#userType=" + userType + "查询单个进件详情入参");
            if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
                log.info("您的登陆信息已过期，请重新登陆");
                return ret;
            }

            InputFileDetailVO vo = new InputFileDetailVO();
            vo.setInputFileDetailId(inputFileDetailId);
            vo.setApiCode(apiCode);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.set("Cookie", request.getHeader("cookie"));
            String url = "http://auth-service/auth/users?" + "pageSize={pageSize}";
            Map<String, String> reqMap = new HashMap<>();
            reqMap.put("pageSize", "99999999");
            //查询此用户下的子用户，或者普通用户
            String jsonData = restTemplateFactory.restTemplate().exchange(url, HttpMethod.GET, new HttpEntity<>(null, httpHeaders), String.class, reqMap).getBody();
            JSONObject json = JSONObject.parseObject(jsonData);
            JSONObject result = json.getJSONObject("result");
            log.info("inputFileDetailId=" + inputFileDetailId + "#usersData=" + result);
            if (result != null) {
                JSONArray usersJson = result.getJSONArray("rows");
                List users = new ArrayList();
                for (int i = 0; i < usersJson.size(); i++) {
                    JSONObject object = usersJson.getJSONObject(i);
                    users.add(object.getString("userId"));
                }
                log.info("inputFileDetailId=" + inputFileDetailId + "#users=" + users);
                vo.setUsers(users);
            } else {
                log.info("inputFileDetailId=" + inputFileDetailId + "#无此用户");
                return ret;
            }

            InputFileDetailVO data = null;
            try {
                data = reportTaskService.findInputFileDetail(vo);
            } catch (Exception e) {
                e.printStackTrace();
                log.info(e.getMessage());
                return ret;
            }
            InputFileDetail info = new InputFileDetail();
            if (data == null) {
                log.info("inputFileDetailId=" + vo.getInputFileDetailId() + "#apiCode=" + vo.getApiCode() + "#userId=" + vo.getUserId() + "#userType=" + userType + "此用户无权限查询此数据或无数据");
                return ret;
            } else {
                info.setStatus(data.getStatus());
            }
            JSONObject jo = new JSONObject();
            jo.put("row", info);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
            return ret;
        }
        return ret;
    }

    /**
     * 查询模板列表
     *
     * @throws Exception
     */
    @GetMapping("/queryModuleTypeList")
    public RespEntity queryModuleTypeList(@RequestParam(value = "industryId", defaultValue = "-2") Integer industryId,
                                          HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String userId = (String) session.getAttribute("userId");
        String apiCode = (String) session.getAttribute("apiCode");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            ret.setMsg("您的登陆信息已过期，请重新登陆");
            return ret;
        }
        try {
            ModuleTypeVO moduleType = new ModuleTypeVO();
            moduleType.setApiCode(apiCode);
            moduleType.setIndustryId(industryId);
            moduleType.setStatus("1");//只查询有效的模板
            List<ModuleTypeVO> list = reportTaskService.queryModuleTypeList(moduleType);
            /*List<ModuleTypeVO> retList = new ArrayList<>();
            if(!CollectionUtils.isEmpty(list)){
                list.forEach(moduleTypeVO -> {
                    if(projectApiService.checkProdCodeValid(apiCode,moduleTypeVO.getProdCode(),1.0)){
                        retList.add(moduleTypeVO);
                    }
                });
            }
            JSONObject jo = new JSONObject();
            jo.put("total", retList.size());
            jo.put("rows", retList);*/
            JSONObject jo = new JSONObject();
            jo.put("total", list.size());
            jo.put("rows", list);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.info("查询模板列表失败");
            log.error(e.getMessage(),e);
            return ret;
        }
        return ret;
    }

    /**
     * 查询决策流
     *
     * @throws Exception
     */
    @GetMapping("/getFlowList")
    public RespEntity getFlowList(HttpSession session) {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        String userId = (String) session.getAttribute("userId");
        String apiCode = (String) session.getAttribute("apiCode");
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            ret.setMsg("您的登陆信息已过期，请重新登陆");
            return ret;
        }
        try {
            List<Flow> list = reportTaskService.getFlowList(apiCode);
            JSONObject jo = new JSONObject();
            jo.put("total", list.size());
            jo.put("rows", list);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            log.info("查询模板列表失败");
            log.error(e.getMessage(),e);
            return ret;
        }
        return ret;
    }

    /**
     * 根据apicode查询行业列表
     *
     * @throws Exception
     */
    @RequestMapping(value = "/queryIndustryList", method = RequestMethod.GET)
    @ResponseBody
    public RespEntity queryIndustryList(
            @RequestParam(value = "inputType",required = false,defaultValue = "1")Integer inputType,
            HttpSession session) throws Exception {
        RespEntity ret = new RespEntity(RespCode.WARN, null);
        JSONObject jo = new JSONObject();
        List<IndustryInfoVO> list = new ArrayList<>();
        if(Objects.equals(2,inputType)){
            jo.put("total", list.size());
            jo.put("rows", list);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
            return ret;
        }

        String userId = session.getAttribute("userId").toString();
        String apiCode = session.getAttribute("apiCode").toString();
        if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(apiCode)) {
            ret.setMsg("您的登陆信息已过期，请重新登陆");
            return ret;
        }
        try {
            IndustryInfoVO industryInfoVO = new IndustryInfoVO();
            industryInfoVO.setApiCode(apiCode);
            List<IndustryInfoVO> retList = reportTaskService.queryIndustryList(industryInfoVO);
            if(!CollectionUtils.isEmpty(retList)){
                list.addAll(retList);
            }
            jo.put("total", list.size());
            jo.put("rows", list);
            ret.changeRespEntity(RespCode.SUCCESS, jo);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("查询行业列表失败");
            return ret;
        }
        return ret;
    }
}
