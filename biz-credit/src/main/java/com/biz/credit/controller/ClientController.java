package com.biz.credit.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IClientService;
import com.biz.credit.service.ITaskService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.ClientVO;
import com.biz.credit.vo.StrategyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@ApiIgnore
@Slf4j
public class ClientController {

    @Autowired
    private IClientService clientService;
    @Autowired
    private ITaskService taskService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/client")
    public RespEntity updateProduct(ClientVO clientVO){
        RespEntity respEntity = new RespEntity(RespCode.WARN,null);
        String publicKey = stringRedisTemplate.opsForValue().get(Constants.REDIS_BIZ_PUBLIC_KEY);
        try {
            if(StringUtils.equals(publicKey,clientVO.getPublicKey())){
                JSONObject jsonData = JSONObject.parseObject(clientVO.getJsonData());
                JSONArray  productList = jsonData.getJSONArray("productList");
                //List<CrmApiVO> crmApiVOList = clientService.findCrmApiVOList(clientVO.getApiCode(),null,null);
                if(!CollectionUtils.isEmpty(productList)){
                   for(int i=0;i<productList.size();i++){
                       JSONArray productListTmp = new JSONArray();
                       productListTmp.add(productList.getJSONObject(i));
                       JSONObject jsonObject = clientService.updateClient(clientVO.getApiCode(),productListTmp);
                       if(jsonObject.containsKey("moduleTypeRepeatError")){
                           respEntity.changeRespEntity(RespCode.MODULE_TYPE_REPEAT,jsonObject);
                       }else if(jsonObject.containsKey("error")){
                           respEntity.changeRespEntity(RespCode.NO_RESULT,jsonObject);
                       }else{
                           StrategyVO target =  jsonObject.getObject("strategy",StrategyVO.class);
                           if(null!=target)
                               taskService.updateSrcIdForCopyStrategyTemplate(target);
                           respEntity.changeRespEntity(RespCode.SUCCESS,jsonObject);
                       }
                   }
                }

            }

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return respEntity;
    }
}
