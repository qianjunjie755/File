package com.biz.credit.dao;

import com.biz.credit.domain.PortraitLabel;
import com.biz.credit.domain.PortraitType;
import com.biz.credit.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortraitConfigDAO {
    List<SystemModuleRespVO> findSystemModules();

    int insertType(@Param("type") PortraitType type);

    List<PortraitTypeGroupRespVO> findPortraitTypeGroup(@Param("param") PortraitTypeGroupQueryVO param);

    List<PortraitTypeRespVO> findPortraitTypes(@Param("moduleId") Integer moduleId);

    void updateParentIdByTypeId(@Param("userId") Integer userId,
                                @Param("typeId") Integer typeId,
                                @Param("children") List<Integer> children);

    PortraitTypeVO findSingleType(@Param("typeId") Integer typeId);

    List<PortraitTypeVO> findTypeByBatch(@Param("typeIds") List<Integer> typeIds);

    PortraitLabel findSingleLabel(@Param("labelId") Integer labelId);

    int insertLabel(@Param("label") PortraitLabel label);

    int updateLabel(@Param("label") PortraitLabel label);

    void updateLabelStatus(@Param("labelId") Integer labelId,
                           @Param("status") Integer status,
                           @Param("userId") Integer userId);

    List<PortraitLabelRespVO> findPortraitLabels(@Param("labelCode") String labelCode,
                                                 @Param("labelName") String labelName,
                                                 @Param("status") Integer status);

    SystemModuleRespVO findSingleModule(@Param("moduleId") Integer moduleId);

    List<PortraitLabelRespVO> findLabelsByBatch(@Param("labelIds") List<Integer> labelIds);
}
