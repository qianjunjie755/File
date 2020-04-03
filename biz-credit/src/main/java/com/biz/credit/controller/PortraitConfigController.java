package com.biz.credit.controller;

import com.biz.credit.controller.validator.PortraitConfigValidator;
import com.biz.credit.domain.PortraitLabel;
import com.biz.credit.domain.RespEntity;
import com.biz.credit.service.IPortraitConfigService;
import com.biz.credit.utils.Constants;
import com.biz.credit.vo.*;
import com.biz.utils.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

/**
 * 企业画像任务-模块/类目/标签配置相关Controller
 */
@Api(tags = {"企业画像配置相关API", "模块/类目/标签配置"})
@RestController
@RequestMapping("/portraitConfig")
public class PortraitConfigController {

    @Autowired
    private IPortraitConfigService portraitConfigService;

    /**
     * 系统模块查询
     * @return
     */
    @ApiOperation(value = "系统模块列表查询")
    @GetMapping("/getSystemModules")
    public RespEntity<List<SystemModuleRespVO>> getSystemModules(){
        List<SystemModuleRespVO> modules = portraitConfigService.querySystemModules();
        return RespEntity.success().setData(modules);
    }

    /**
     * 添加分类组以及子分类
     * @param portraitType
     * @param session
     * @return
     */
    @ApiOperation(value = "添加分类组", notes = "分类组下有二级子分类")
    @PostMapping("/addType")
    public RespEntity addPortraitType(@RequestBody PortraitTypeReqVO portraitType,
                                      HttpSession session){
        String userId = (String) session.getAttribute(Constants.USER_ID);
        RespEntity respEntity = PortraitConfigValidator.addTypeValidate(portraitType);
        if (!respEntity.isSuccess()){
            return respEntity;
        }
        portraitConfigService.addPortraitType(Integer.valueOf(userId), portraitType);
        return RespEntity.success();
    }

    /**
     * 查询分类组
     * @param param
     * @return
     */
    @ApiOperation(value = "查询分类组", notes = "只查询分类组，不含子分类")
    @GetMapping("/getTypeGroup")
    public RespEntity<List<PortraitTypeGroupRespVO>> getPortraitTypeGroup(PortraitTypeGroupQueryVO param){
        if (StringUtils.isNotBlank(param.getUpdateTime())){
            boolean flag = DateUtil.validateIsDate(param.getUpdateTime());
            if (flag){
                Date updateTime = DateUtil.parseStrToDate(param.getUpdateTime(),
                        DateUtil.DATE_FORMAT_YYYY_MM_DD);
                param.setUpdateTime(DateUtil.parseDateToStr(updateTime,
                        DateUtil.DATE_FORMAT_YYYY_MM_DD));
            }else {
                return RespEntity.error().setMsg("时间格式有误：yyyy-mm-dd");
            }
        }
        return RespEntity.success().setData(portraitConfigService.queryPortraitTypeGroup(param));
    }

    /**
     * 根据模块ID查询分类组以及子分类
     * @param moduleId
     * @return
     */
    @ApiOperation(value = "根据模块ID查询分类组", notes = "查询分类组及其子分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "moduleId", value = "模块ID-Integer", required = true)
    })
    @GetMapping("/getTypesByModuleId")
    public RespEntity<List<PortraitTypeRespVO>> getPortraitTypes(@RequestParam(name = "moduleId") Integer moduleId){
        if (moduleId == null || moduleId <=0){
            return RespEntity.error().setMsg("模块ID不能为空");
        }
        return RespEntity.success().setData(portraitConfigService.queryPortraitTypes(moduleId));
    }

    /**
     * 更新子分类所在分类组
     * @param typeGroup
     * @param session
     * @return
     */
    @ApiOperation(value = "更新子分类所在分类组")
    @PostMapping("/updateTypeGroup")
    public RespEntity updatePortraitTypeGroup(@RequestBody PortraitTypeGroupUpdateVO typeGroup,
                                              HttpSession session){
        String userId = (String) session.getAttribute(Constants.USER_ID);
        RespEntity respEntity = PortraitConfigValidator.updateTypeGroupValidate(typeGroup);
        if (!respEntity.isSuccess()){
            return respEntity;
        }
        //校验分类组有效果性
        PortraitTypeVO type = portraitConfigService.querySingleType(typeGroup.getTypeId());
        if (type == null ||
                type.getParentId() != -1 ||
                type.getStatus() == 0){
            return RespEntity.error().setMsg("分类组ID不存在或已停用");
        }
        //校验子分类中是否含有分类组
        List<PortraitTypeVO> childTypes = portraitConfigService.queryTypeByBatch(typeGroup.getChildren());
        if (CollectionUtils.isEmpty(childTypes)){
            return RespEntity.error().setMsg("子分类ID全部不存在");
        }
        for (PortraitTypeVO childType : childTypes){
            if (childType.getParentId() == -1){
                return RespEntity.error().setMsg("分类组不能作为子分类");
            }
        }
        portraitConfigService.updatePortraitTypeGroup(Integer.valueOf(userId), typeGroup);
        return RespEntity.success();
    }

    /**
     * 新增/修改标签
     * @param portraitLabel
     * @param session
     * @return
     */
    @ApiOperation(value = "新增/修改标签")
    @PostMapping("/saveLabel")
    public RespEntity addLabel(@RequestBody PortraitLabelReqVO portraitLabel,
                               HttpSession session){
        String userId = (String) session.getAttribute(Constants.USER_ID);
        RespEntity respEntity = PortraitConfigValidator.saveLabelValidate(portraitLabel);
        if (!respEntity.isSuccess()){
            return respEntity;
        }
        //校验分类ID是否有效
        PortraitTypeVO type = portraitConfigService.querySingleType(portraitLabel.getTypeId());
        if (type == null ||
                type.getParentId() == -1 ||
                type.getStatus() == 0){
            return RespEntity.error().setMsg("分类ID不存在或已停用");
        }
        Integer labelId = portraitConfigService.saveLabel(Integer.valueOf(userId), portraitLabel);
        return RespEntity.success().setData(labelId);
    }

    /**
     * 更新标签状态
     * @param labelId
     * @param status
     * @param session
     * @return
     */
    @ApiOperation(value = "更新标签状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelId", value = "标签ID-Integer", required = true),
            @ApiImplicitParam(name = "status", value = "标签状态：1-启用 0-停用", required = true)
    })
    @PostMapping("/updateLabelStatus")
    public RespEntity updateLabelStatus(@RequestParam(name = "labelId") Integer labelId,
                                        @RequestParam(name = "status") Integer status,
                                        HttpSession session){
        String userId = (String) session.getAttribute(Constants.USER_ID);
        if (labelId == null || labelId <= 0){
            return RespEntity.error().setMsg("标签ID不合法");
        }
        if (status == null){
            return RespEntity.error().setMsg("状态不能为空");
        }
        if (status != 1 && status != 0){
            return RespEntity.error().setMsg("状态值不合法");
        }
        PortraitLabel label = portraitConfigService.querySingleLabel(labelId);
        if (label == null){
            return RespEntity.error().setMsg("该标签不存在");
        }
        if (!status.equals(label.getStatus())){
            portraitConfigService.updateLabelStatus(labelId, status, Integer.valueOf(userId));
        }
        return RespEntity.success();
    }

    /**
     * 查询标签列表
     * @param labelCode
     * @param labelName
     * @param status
     * @return
     */
    @ApiOperation(value = "查询标签列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "labelCode", value = "标签code-String"),
            @ApiImplicitParam(name = "labelName", value = "标签名称"),
            @ApiImplicitParam(name = "status", value = "标签状态：1-启用 0-停用")
    })
    @GetMapping("/getLabels")
    public RespEntity<List<PortraitLabelRespVO>> getLabels(@RequestParam(name = "labelCode") String labelCode,
                                @RequestParam(name = "labelName") String labelName,
                                @RequestParam(name = "status") Integer status){
        return RespEntity.success().setData(portraitConfigService.queryPortraitLabels(labelCode, labelName, status));
    }

}
