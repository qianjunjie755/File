package com.biz.credit.controller;

import com.alibaba.fastjson.JSONObject;
import com.biz.credit.limit.RequestTimes;
import com.biz.credit.service.IReportApiService;
import com.biz.credit.vo.reportApiVO.ApiInputFileDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@RestController
@RequestMapping("/reportApi")
public class ReportApiController {

    @Value("${biz.decision.report.domain-address}")
    private String domain_address;
    @Resource
    private IReportApiService iReportApiService;

    /**
     * 第三方客户调用api接口
     * 生成各个版本的征信报告
     * 废弃-暂不使用-2018/11/01
     * @param request
     * @return
     */
    @RequestMapping(value = "/htmlToPdfApi", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject htmlToPdfApi(HttpServletRequest request) {
        String d= (String) request.getAttribute("ifovertimes");
        return iReportApiService.htmlToPdfApi(request);
    }



    /**
     * 第三方接口，下载pdf文件
     * id  python端taskid
     * RequestTimes (count限制次数 属性必须是常量；  time周期 ，单位毫秒 60000=1分钟)
     * @return
     * @throws IOException
     */
    @RequestTimes(count = 6)
    @RequestMapping(value = "/downloadFileApi")
    public ResponseEntity<byte[]> downloadFileApi(HttpServletRequest request) throws IOException {
        String id=request.getParameter("id");
        if(StringUtils.isEmpty(id)){
            log.info("id不能为空");
            return null;
        }

        //ifovertimes=true 超出限制次数
        if (request.getAttribute("ifovertimes").equals("true")) {
            log.info("apiTaskId="+id+"超出下载上限：每分钟6次");
            return null;
        }

        ApiInputFileDetailVO vo = new ApiInputFileDetailVO();
        vo.setApiTaskId(id);
        ApiInputFileDetailVO apiVo = iReportApiService.queryApiInputFileDetailById(vo);
        if(apiVo==null){
            log.info("ApiTaskId="+id+"未查到进件数据");
            return null;
        }
        String pdfFilePath=apiVo.getPdfFilePath()+apiVo.getPdfFileName();//pdf文件名

        String company_name=apiVo.getKeyNo();
        if(StringUtils.isEmpty(company_name)){
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            company_name=sdf.format(date);
        }

        File file=new File(pdfFilePath);
        if (!file.exists()){
            log.info("apiPdfPath="+pdfFilePath+"#pdf文件不存在");
            return null;
        }
        HttpHeaders headers = new HttpHeaders();
        String finalFileName = "report";//默认
        String userAgentStr=request.getHeader("USER-AGENT");
        if (StringUtils.isNotEmpty(userAgentStr)){
            String UserAgent = userAgentStr.toLowerCase();
            if(UserAgent.indexOf("firefox") >= 0){
                finalFileName =  new String((company_name+".pdf").getBytes("UTF-8"),"iso-8859-1");
            }else {
                try {
                    finalFileName = URLEncoder.encode(company_name,"UTF-8")+".pdf";//为了解决中文名称乱码问题
                }catch (UnsupportedEncodingException e){
                    log.info("文件名字转换异常");
                    e.printStackTrace();
                }
            }
        }

        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + finalFileName + "\"");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.OK);
    }

    /**
     * 根据inputFileDetailId查询pdf生成状态
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/queryApiInputFileDetailById", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject queryApiInputFileDetailById(@RequestParam(name = "inputFileDetailId") String inputFileDetailId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "03");//03 默认生成失败
        jsonObject.put("message", "failed");

        ApiInputFileDetailVO vo = new ApiInputFileDetailVO();
        vo.setInputFileDetailId(Long.parseLong(inputFileDetailId));
        ApiInputFileDetailVO apiVo = iReportApiService.queryApiInputFileDetailById(vo);

        if (apiVo.getStatus() == 1) {//生成成功 00
            jsonObject.put("code", "00");
            jsonObject.put("message", "success");
            JSONObject result = new JSONObject();
            result.put("downloadURL", domain_address + "reportApi/downloadFileApi?id=" + (apiVo.getInputFileDetailId()+"").replaceAll("-","api"));
            jsonObject.put("data", result);
        } else if (apiVo.getStatus() == 2) {//生成失败03
            jsonObject.put("code", "03");
            jsonObject.put("message", "failed");
        } else {//生成中08
            jsonObject.put("code", "08");
            jsonObject.put("message", "loading");
        }
        return jsonObject;
    }

}
