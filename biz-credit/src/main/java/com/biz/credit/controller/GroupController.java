package com.biz.credit.controller;


import com.biz.credit.domain.RespCode;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.GroupService;
import com.biz.credit.vo.SystemGroupVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/group")
@Slf4j
public class GroupController  {

    @Autowired
    private GroupService groupService;


    @RequestMapping("/queryGroupList")
    public RespEntity queryGroupList(){
        RespEntity ret=new RespEntity(RespCode.WARN,null);

        try {
        List<SystemGroupVO> systemGroupList=groupService.queryGroupList();

        ret.changeRespEntity(RespCode.SUCCESS,systemGroupList);
        }catch (Exception e){
            log.info("查询平台类型配置失败");
            return ret;
        }
    return ret;
    }

}
