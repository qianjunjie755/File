package com.biz.warning.plugin;

import com.biz.warning.dao.EntityDAO;
import com.biz.warning.util.tools.ApplicationDateValidator;
import com.biz.warning.vo.EntityVO;
import com.biz.strategy.entity.EntityBasic;
import com.biz.strategy.entity.TaskInput;
import com.biz.strategy.plugins.UpdateInput;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateInputService extends UpdateInput {

    @Autowired
    private EntityDAO entityDAO;

    @Override
    public void execute(TaskInput taskInput) {
        try {
            DateTime now = DateTime.now();
            int nowInt = Integer.parseInt(now.toString("yyyyMMdd"));

            //LocalDate now = LocalDate.now();
            DateTime expireDatetime = ApplicationDateValidator.parseFromString(taskInput.getExpireTime());
            int expireTimeInt = Integer.parseInt(expireDatetime.toString("yyyyMMdd"));
            //LocalDate expireTime = LocalDate.parse(EngineUtils.dateConvert(taskInput.getExpireTime()));
            EntityVO entityVO = new EntityVO();
            entityVO.setEntityId(Long.valueOf(taskInput.getInputId()));
            entityVO.setParentAppId(taskInput.getAppId());
            entityVO.setEntityStatus(expireTimeInt>nowInt?1:2);
            entityVO.setExecDate(now.toString("yyyy-MM-dd"));
            entityDAO.updateEntityParentAppIdAndStatus(entityVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }

        //记录进件基础信息
        EntityBasic basicInfo = taskInput.getBasicInfo();
        if (basicInfo == null) {
            log.warn("进件[{}]工商基础信息为空!!", taskInput.getInputId());
            return;
        }

        try {
            if (entityDAO.updateEntityBasicInfo(basicInfo) <= 0) {
                entityDAO.insertEntityBasicInfo(basicInfo);
            }
            log.info("进件[{}]工商基础信息更新完成!", taskInput.getInputId());
        } catch (Exception e) {
            log.error("进件[" + taskInput.getInputId() + "]工商基础信息更新失败: " + e.getMessage(), e);
        }
    }
}
