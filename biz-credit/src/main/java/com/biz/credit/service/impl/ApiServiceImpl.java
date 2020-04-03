package com.biz.credit.service.impl;

import com.biz.credit.dao.ApiDAO;
import com.biz.credit.service.IApiService;
import com.biz.credit.utils.ExcelUtil;
import com.biz.credit.vo.ApiRequestVO;
import com.biz.credit.vo.ApiResponseVO;
import com.biz.credit.vo.ApiVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ApiServiceImpl implements IApiService {
    @Value("${biz.api.excel-path}")
    private String apiExcelPath;
    @Autowired
    private ApiDAO apiDAO;
    @Async
    @Override
    public void syncApiInfo() {
        Map<String, ApiVO> apiMap = new LinkedHashMap<>();
        List<ApiVO> bizValidApiList = apiDAO.findBizValidApiList();

        try {
            apiMap.putAll(ExcelUtil.getApisFromExcel(bizValidApiList,apiExcelPath));
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        if(!CollectionUtils.isEmpty(apiMap)){
            apiMap.forEach((apiName,apiVO)->{
                log.info("apiName:"+apiName+" update start;");
                /*String[] tmp =  apiName.split("\\|");
                String nameCH = tmp[0];
                String prodCode=tmp[1];
                String sheetName = tmp[2];*/
                List<ApiVO> apiCheckList = apiDAO.findApiList(apiVO);
                if(CollectionUtils.isEmpty(apiCheckList)){
                    log.info("{},这个产品还没添加。",apiName);
                    //apiDAO.addApi(apiVO);
                }else{
                    apiVO.setId(apiCheckList.iterator().next().getId());
                    apiVO.setIsDeleted(0);
                    apiDAO.updateApi(apiVO);
                    apiVO.getRequestParamList().forEach(apiRequestVO -> {
                        apiRequestVO.setApiId(apiVO.getId());
                        apiRequestVO.setApiProdCode(apiVO.getProdCode());
                        apiRequestVO.setApiVersion(apiVO.getVersion());
                        List<ApiRequestVO> apiRequestVOList = apiDAO.findApiRequestVOList(apiRequestVO);
                        if(CollectionUtils.isEmpty(apiRequestVOList)){
                            apiDAO.addApiRequest(apiRequestVO);
                        }else{
                            apiRequestVO.setStatus(1);
                            apiDAO.updateApiRequest(apiRequestVO);
                        }

                    });
                    apiDAO.updateApiResponseVOStatusByApiId(apiVO.getId(),0);
                    apiVO.getResponseParamList().forEach(apiResponseVO -> {
                        apiResponseVO.setParentId(0l);
                    });
                    updateApiResponseList(apiVO,apiVO.getResponseParamList());
                }
            });
        }
    }


    public void updateApiResponseList(ApiVO apiVO, List<ApiResponseVO> apiResponseVOList){
        apiResponseVOList.forEach(apiResponseVO -> {
            apiResponseVO.setApiId(apiVO.getId());
            apiResponseVO.setApiProdCode(apiVO.getProdCode());
            apiResponseVO.setApiVersion(apiVO.getVersion());
            apiResponseVO.setStatus(1);
            List<ApiResponseVO> checkList = apiDAO.findApiResponseVOList(apiResponseVO);
            if(CollectionUtils.isEmpty(checkList)){
                apiDAO.addApiResponseVO(apiResponseVO);
            }else{
                apiResponseVO.setId(checkList.iterator().next().getId());
                apiResponseVO.setStatus(1);
                apiDAO.updateApiResponseVO(apiResponseVO);
            }
            if(!CollectionUtils.isEmpty(apiResponseVO.getChildren())){
                apiResponseVO.getChildren().forEach(temp->{
                    temp.setParentId(apiResponseVO.getId());
                });
                updateApiResponseList(apiVO,apiResponseVO.getChildren());
            }
        });
    }

    @Override
    public List<Map<String,Object>> getSourceCount(String apiCode) {
        return apiDAO.querySourceCount(apiCode);
    }

    @Override
    public List<ApiVO> getApiList(ApiVO apiVO) {
        return apiDAO.queryList(apiVO);
    }


    @Override
    public ApiVO getApiDetail(ApiVO apiVO) {
        //api详情
        ApiVO api = apiDAO.queryApiByCodeAndVersion(apiVO);
        //入参列表
        ApiRequestVO apiRequestVO = new ApiRequestVO();
        apiRequestVO.setApiId(api.getId());
        api.setRequestParamList(apiDAO.queryApiRequestList(apiRequestVO));
        //出参列表
        ApiResponseVO apiResponseVO = new ApiResponseVO();
        apiResponseVO.setApiId(api.getId());
        api.setResponseParamList(apiDAO.queryApiResponseList(apiResponseVO));
        return api;
    }



}
