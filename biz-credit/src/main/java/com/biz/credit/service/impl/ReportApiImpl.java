package com.biz.credit.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.dao.ApiInputFileDetailDAO;
import com.biz.credit.dao.IndustryInfoDAO;
import com.biz.credit.dao.ModuleTypeDAO;
import com.biz.credit.service.IReportApiService;
import com.biz.credit.utils.Constants;
import com.biz.credit.utils.DateUtil;
import com.biz.credit.utils.RedisUtil;
import com.biz.credit.vo.IndustryInfoVO;
import com.biz.credit.vo.ModuleTypeVO;
import com.biz.credit.vo.reportApiVO.ApiInputFileDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Service
public class ReportApiImpl implements IReportApiService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IndustryInfoDAO industryInfoDAO;
    @Resource
    private ModuleTypeDAO moduleTypeDAO;
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private ApiInputFileDetailDAO apiInputFileDetailDAO;

    @Value("${biz.decision.basic-url}")
    private String checkUrl;
    @Value("${biz.task.check-json-data}")
    private String checkJsonData;

    @Override
    public JSONObject htmlToPdfApi(HttpServletRequest request) {
        JSONObject req = new JSONObject();
        req.put("code", "03");//03 生成失败
        req.put("message", "failed");
        JSONObject params = new JSONObject();

        ApiInputFileDetailVO apiInputFileDetailVO = new ApiInputFileDetailVO();
        String apiCode = request.getParameter("apiCode");
        if (StringUtils.isNotEmpty(apiCode)) {
            params.put("apiCode", apiCode);
            apiInputFileDetailVO.setApiCode(apiCode);
        } else {
            req.put("code", "07");
            req.put("message", "apiCode不能为空");
            return req;
        }

        // 1-含强规则、评分、企业基础数据 2-企业强规则、企业基础数据 3-含企业基础数据 4-企业强规则、评分、企业基础数据、法人强规则、法人基础数据 5-评分、企业基础数据
        String appType = request.getParameter("appType");//模板类型
        if (StringUtils.isNotEmpty(appType)) {
            params.put("reportType", appType);
            apiInputFileDetailVO.setReportType(Integer.parseInt(appType));
        } else {
            req.put("code", "07");
            req.put("message", "appType模板类型不能为空");
            return req;
        }

        //根据apicode、 模板类型appType 查询moduleTypeId
        ModuleTypeVO api = new ModuleTypeVO();
        api.setIsTemplate(1);
        api.setReportType(Integer.parseInt(appType));
        ModuleTypeVO moduleType = null;
        try {
            moduleType = moduleTypeDAO.findModuleTypeTemplateByReportType(api);
        } catch (Exception e) {
            log.info("moduleTypeDAO.findModuleTypeTemplateByReportType:" + e.getMessage());
            e.printStackTrace();
        }
        int moduleTypeId = moduleType.getModuleTypeId();
        if (moduleTypeId < 0) {
            req.put("code", "07");
            req.put("message", "apiCode=" + apiCode + "#appType" + appType + ":根据apicode和模板类型appType没有查询到信息");
            log.info("apiCode=" + apiCode + "#appType" + appType + ":根据apicode和模板类型appType没有查询到信息");
            return req;
        }
        params.put("moduleTypeId", moduleTypeId);
        apiInputFileDetailVO.setModuleTypeId(moduleTypeId);

        String appInd = request.getParameter("appInd");//场景类型  Rent-企业信用报告_融资租赁
        if (StringUtils.isNotEmpty(appInd)) {
            apiInputFileDetailVO.setIndustryType(appInd);
            //根据场景类型industryType查询industryId
            IndustryInfoVO vo = new IndustryInfoVO();
            vo.setIndustryType(appInd);
            IndustryInfoVO industryInfoVO = null;
            try {
                industryInfoVO = industryInfoDAO.findIndustryInfoByIndustryType(vo);
            } catch (Exception e) {
                log.info("industryInfoDAO.findIndustryInfoByIndustryType:" + e.getMessage());
                e.printStackTrace();
            }

            int industryId = industryInfoVO.getIndustryId();
            if (industryId > 0) {
                params.put("industryId", industryId);
                apiInputFileDetailVO.setIndustryId(industryId);
            } else {
                params.put("industryId", "1");
                apiInputFileDetailVO.setIndustryId(1);
            }
        } else {
            params.put("industryId", "1");//默认为1 企业信用报告_融资租赁
            apiInputFileDetailVO.setIndustryId(1);
            apiInputFileDetailVO.setIndustryType("");
        }

        String appId = request.getParameter("appId");
        if (StringUtils.isNotEmpty(appId)) {
            params.put("appId", appId);
            apiInputFileDetailVO.setAppId(appId);
        } else {
            req.put("code", "07");
            req.put("message", "appId不能为空");
            return req;
        }

        String keyNo = request.getParameter("keyNo");//公司名称
        if (StringUtils.isNotEmpty(keyNo)) {
            params.put("keyNo", keyNo);
            apiInputFileDetailVO.setKeyNo(keyNo);
        } else {
            req.put("code", "07");
            req.put("message", "keyNo公司名称不能为空");
            return req;
        }

        String idNumber = request.getParameter("idNumber");//法人身份证
        if (StringUtils.isNotEmpty(idNumber)) {
            params.put("idNumber", idNumber);
            apiInputFileDetailVO.setIdNumber(idNumber);
        } else {
            req.put("code", "07");
            req.put("message", "idNumber法人身份证不能为空");
            return req;
        }

        String cellPhone = request.getParameter("cellPhone");//法人手机号
        if (StringUtils.isNotEmpty(cellPhone)) {
            params.put("cellPhone", cellPhone);
            apiInputFileDetailVO.setCellPhone(cellPhone);
        } else {
            req.put("code", "07");
            req.put("message", "cellPhone法人手机号不能为空");
            return req;
        }

        String name = request.getParameter("name");//法人名称
        if (StringUtils.isNotEmpty(name)) {
            params.put("name", name);
            apiInputFileDetailVO.setName(name);
        } else {
            req.put("code", "07");
            req.put("message", "name法人姓名不能为空");
            return req;
        }

        String creditCode = request.getParameter("creditCode");//统一社会信用代码
        if (StringUtils.isNotEmpty(creditCode)) {
            params.put("creditCode", creditCode);
            apiInputFileDetailVO.setCreditCode(creditCode);
        } else {
            req.put("code", "07");
            req.put("message", "creditCode统一社会信用代码不能为空");
            return req;
        }

        String bankId = request.getParameter("bankId");//银行卡
        if (StringUtils.isNotEmpty(bankId)) {
            params.put("bankId", bankId);
            apiInputFileDetailVO.setBankId(bankId);
        } else {
            req.put("code", "07");
            req.put("message", "bankId银行卡号不能为空");
            return req;
        }

        String homeAddr = request.getParameter("homeAddr");//居住地址
        if (StringUtils.isNotEmpty(homeAddr)) {
            params.put("homeAddr", homeAddr);
            apiInputFileDetailVO.setHomeAddr(homeAddr);
        } else {
            req.put("code", "07");
            req.put("message", "homeAddr居住地址不能为空");
            return req;
        }

        String bizAddr = request.getParameter("bizAddr");//工作地址
        if (StringUtils.isNotEmpty(bizAddr)) {
            params.put("bizAddr", bizAddr);
            apiInputFileDetailVO.setBizAddr(bizAddr);
        } else {
            req.put("code", "07");
            req.put("message", "bizAddr工作地址不能为空");
            return req;
        }
        //将入参信息插入表api_input_file_detail ,将入参信息存入reids
        //定时任务进程会根据入库数据定时跑任务生产pdf，更新status状态
        long inputFileDetailId = addApiInputFileDetail(apiInputFileDetailVO);
        if (inputFileDetailId < 0) {
            req.put("code", "00");
            req.put("data", inputFileDetailId);
            req.put("message", "sucess");
        } else {
            req.put("code", "03");//数据插入失败
            req.put("message", "failed");
        }

        return req;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 3600, rollbackFor = {RuntimeException.class, Exception.class})
    public long addApiInputFileDetail(ApiInputFileDetailVO apiInputFileDetailVO) {
        Long inputFileDetailId = redisUtil.getApiReportId(Constants.ApiInputFileDetail);//获取负自增id ，基于redis方法实现
        apiInputFileDetailVO.setInputFileDetailId(inputFileDetailId);

        Date date = new Date();
        String year = DateUtil.parseDateToStr(date, "yyyy");
        String month = DateUtil.parseDateToStr(date, "yyyyMM");
        String day = DateUtil.parseDateToStr(date, "yyyy-MM-dd");
        apiInputFileDetailVO.setYear(Integer.parseInt(year));
        apiInputFileDetailVO.setMonth(Integer.parseInt(month));
        apiInputFileDetailVO.setDate(day);
        apiInputFileDetailVO.setStatus(0);
        //插入api进件数据
        try {
            apiInputFileDetailDAO.addApiInputFileDetail(apiInputFileDetailVO);
        } catch (Exception e) {
            log.error("apiInputFileDetailDAO.addApiInputFileDetail:" + e.getMessage(), e);
            return 0L;
        }

        //将入参信息存入reids   detailId_apicode_taskid_userid_groupid_moduletypeid_industryId
        //默认 userid =0  groupid = -1 industryId=1   inputFileDetailId=taskid
        String keyInitial = "biz_credit:report:initial";
        String key = (inputFileDetailId + "").concat("_")
                .concat(apiInputFileDetailVO.getApiCode()).concat("_")
                .concat(inputFileDetailId + "").concat("_")
                .concat("0").concat("_")
                .concat("-1").concat("_")
                .concat(apiInputFileDetailVO.getModuleTypeId().toString()).concat("_")
                .concat(apiInputFileDetailVO.getIndustryId().toString());
        stringRedisTemplate.opsForList().rightPush(keyInitial, key);
        return inputFileDetailId;
    }

    @Override
    public ApiInputFileDetailVO queryApiInputFileDetailById(ApiInputFileDetailVO apiInputFileDetailVO) {
        //根据inputFileDetailId查询pdf文件是否生成
        ApiInputFileDetailVO apiVo = null;
        try {
            apiVo = apiInputFileDetailDAO.queryApiInputFileDetailById(apiInputFileDetailVO);
        } catch (Exception e) {
            log.info("apiInputFileDetailDAO.queryApiInputFileDetailById:" + e.getMessage());
            e.printStackTrace();
        }
        return apiVo;
    }





    /**
     * 自定义版第三方接口解析
     *
     * @return
     */
    public ApiInputFileDetailVO getApiCusomParams(JSONObject _platform, String apiCode, ApiInputFileDetailVO apiInputFileDetailVO) {
        String appType = _platform.getString("appType");//模板类型
        if (StringUtils.isNotEmpty(appType)) {
            apiInputFileDetailVO.setReportType(Integer.parseInt(appType));
        }

        String strategyId = _platform.getString("appStrategy");//第三方接口返回数据格式  1-pdf  2-json
        ModuleTypeVO moduleType = null;
        try {
            apiInputFileDetailVO.setStrategyId(Integer.parseInt(strategyId));
            //根据strategyId 查询模板表module_type  第三方接口请求无权限限制，相关配置用默认值
            moduleType = moduleTypeDAO.findModuleTypeTemplateByStrategyId(apiInputFileDetailVO.getStrategyId());
        } catch (Exception e) {
            log.info("第三方接口进件解析[moduleTypeDAO.findModuleTypeTemplateByStrategyId:" + e.getMessage() + "]");
            e.printStackTrace();
            return null;
        }

        if (moduleType == null) {
            log.info("第三方接口进件解析[apiCode:" + apiCode + "#appType:" + appType + ":根据模板类型appType没有查询到信息]");
            return null;
        }
        apiInputFileDetailVO.setModuleTypeId(moduleType.getModuleTypeId());

        String appInd = _platform.getString("appInd");//场景类型  Rent-企业信用报告_融资租赁
        if (StringUtils.isNotEmpty(appInd)) {
            apiInputFileDetailVO.setIndustryType(appInd);
            //根据场景类型industryType查询industryId
            IndustryInfoVO vo = new IndustryInfoVO();
            vo.setIndustryType(appInd);
            IndustryInfoVO industryInfoVO = null;
            try {
                industryInfoVO = industryInfoDAO.findIndustryInfoByIndustryType(vo);
            } catch (Exception e) {
                log.info("第三方接口进件解析[industryInfoDAO.findIndustryInfoByIndustryType:" + e.getMessage() + "]");
                log.info("第三方接口进件解析[根据appInd[{}]未查询到industryInfo场景数据]", appInd);
                e.printStackTrace();
                return null;
            }

            int industryId = industryInfoVO.getIndustryId();
            if (industryId > 0) {
                apiInputFileDetailVO.setIndustryId(industryId);
            } else {
                apiInputFileDetailVO.setIndustryId(1);
            }
        } else {
            apiInputFileDetailVO.setIndustryType("");
            apiInputFileDetailVO.setIndustryId(1);
        }

        String appId = _platform.getString("appId");
        if (StringUtils.isNotEmpty(appId)) {
            apiInputFileDetailVO.setAppId(appId);
        } else {
            log.info("第三方接口进件解析[appId不能为空]");
            return null;
        }

        return apiInputFileDetailVO;
    }

    /**
     * 将入参信息插入表api_input_file_detail
     *
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, timeout = 3600, rollbackFor = {RuntimeException.class, Exception.class})
    public long addApiInputFileDetailData(ApiInputFileDetailVO apiInputFileDetailVO) {
        Long inputFileDetailId = redisUtil.getApiReportId(Constants.ApiInputFileDetail);//获取负自增id ，基于redis方法实现
        apiInputFileDetailVO.setInputFileDetailId(inputFileDetailId);

        Date date = new Date();
        String year = DateUtil.parseDateToStr(date, "yyyy");
        String month = DateUtil.parseDateToStr(date, "yyyyMM");
        String day = DateUtil.parseDateToStr(date, "yyyy-MM-dd");
        apiInputFileDetailVO.setYear(Integer.parseInt(year));
        apiInputFileDetailVO.setMonth(Integer.parseInt(month));
        apiInputFileDetailVO.setDate(day);
        apiInputFileDetailVO.setStatus(0);

        //插入api进件数据
        apiInputFileDetailDAO.addApiInputFileDetail(apiInputFileDetailVO);
        if (!CollectionUtils.isEmpty(apiInputFileDetailVO.getParams())) {
            apiInputFileDetailDAO.addInputFileParams(inputFileDetailId, apiInputFileDetailVO.getParams());
        }
        return inputFileDetailId;
    }
}
