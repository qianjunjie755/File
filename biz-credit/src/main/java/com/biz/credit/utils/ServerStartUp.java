package com.biz.credit.utils;

import com.biz.credit.dao.ApiParamCfgDAO;
import com.biz.credit.dao.DFlowPropsDAO;
import com.biz.credit.domain.DFlowPlatform;
import com.biz.credit.vo.ApiParamCfgVO;
import com.biz.credit.vo.DFlowBizVO;
import com.biz.credit.vo.DFlowLinkVO;
import com.biz.credit.vo.DFlowPlatformVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * application启动时，会执行这个class
 */
@Slf4j
@Component
//@Order(value = 1)
public class ServerStartUp implements InitializingBean {

    @Value("${apiCode}")
    private  String apiCode;
    @Value("${login_username}")
    private  String login_username;
    @Value("${login_password}")
    private  String login_password;
    @Value("${biz.api_admin.redis.keys.api-param-cfg-map}")
    private String apiParamCfgMapKey;
    @Value("${biz.decision.flow-key}")
    private String bizDecisionFlowKey;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ApiParamCfgDAO apiParamCfgDAO;
    @Autowired
    private DFlowPropsDAO dFlowPropsDAO;

    @Override
    public void afterPropertiesSet() throws Exception {
        //禁用ssl验证
        DisableSSLCertificateCheckUtil.disableChecks();
        log.info("已经禁用ssl验证");//此输出可以测试中文乱码，暂不去掉
        //服务启动，获取登陆接口所需要的信息，并存入redis
        redisTemplate.opsForValue().set("biz_credit:login_params", "userName=" + login_username + "&password=" + login_password + "&apiCode=" + apiCode);
        //每次重启应用服务，删除上次的tokenid；  redis默认是RDB模式持久化存储，所以需要手动删除上次缓存数据
        redisTemplate.delete("biz_credit:tokenid");
        List<ApiParamCfgVO> list = apiParamCfgDAO.findApiParamCfgList();
        HashOperations<String, String, ApiParamCfgVO> hashOps = redisTemplate.opsForHash();
        HashOperations<String, String, String> stringHashOps = redisTemplate.opsForHash();
        String riskParamTypeRedisKey = apiParamCfgMapKey.replaceAll("param","risk-param");
        list.forEach(param -> {
            hashOps.put(apiParamCfgMapKey, param.getParamName(), param);
            if(param.getRiskParamType()>0){
                stringHashOps.put(riskParamTypeRedisKey,param.getRiskParamType().toString(),param.getParamName());
                stringHashOps.put(riskParamTypeRedisKey,param.getParamName(),param.getRiskParamType().toString());
                stringHashOps.put(riskParamTypeRedisKey,param.getParamCode(),param.getRiskParamType().toString());
            }
        });

        HashOperations<String, String, String> intHashOps = redisTemplate.opsForHash();
        String platFormKey=bizDecisionFlowKey.replaceAll("allFlows","allPlatForm");
        List<DFlowPlatformVO> platformVOList = dFlowPropsDAO.findPlatformList(null);
        Constants.FLOW_PLATFORM_MAP.clear();
        platformVOList.forEach(vo->{
            Constants.FLOW_PLATFORM_MAP.put(vo.getId(),vo);
            intHashOps.put(platFormKey,vo.getId().toString(),vo.getPlatFormName());
        });
        String bizKey=bizDecisionFlowKey.replaceAll("allFlows","allBiz");
        List<DFlowBizVO> bizVOList = dFlowPropsDAO.findBizList(null,null);
        bizVOList.forEach(vo->{
            Constants.FLOW_BIZ_MAP.put(vo.getId(),vo);
            intHashOps.put(bizKey,vo.getId().toString(),vo.getBizName());
        });
        String linkKey=bizDecisionFlowKey.replaceAll("allFlows","allLink");
        List<DFlowLinkVO> linkVOList = dFlowPropsDAO.findLinkList(null,null);
        linkVOList.forEach(vo->{
            Constants.FLOW_LINK_MAP.put(vo.getId(),vo);
            intHashOps.put(linkKey,vo.getId().toString(),vo.getLinkName());
        });


    }
}
