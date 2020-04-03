package com.biz.credit.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class IndustryInfoViewVO implements Serializable {
    private Integer industryId;
    private String industryCode;
    private String industryName;
    private String industryVersion;
    private List<IndustryInfoViewVO> versionList = new ArrayList<>();
    private String industryDescription = StringUtils.EMPTY;
    private Integer boundaryType;
    private List<IndustryInfoBoundaryViewVO> boundaryList = new ArrayList<>();

    public void setIndustryDescription(String industryDescription) {
        if(StringUtils.isNotEmpty(industryDescription))
            this.industryDescription = industryDescription;
    }

    public IndustryInfoViewVO(List<IndustryInfoVO> industryInfoVOList){
        if(!CollectionUtils.isEmpty(industryInfoVOList)){
            Iterator<IndustryInfoVO> it = industryInfoVOList.iterator();
            IndustryInfoVO industryInfoVO = it.next();
            industryId = industryInfoVO.getIndustryId();
            industryCode = industryInfoVO.getIndustryCode();
            industryName = industryInfoVO.getIndustryName().contains("模型")?industryInfoVO.getIndustryName():industryInfoVO.getIndustryName().concat("模型");
            industryVersion = String.format("%.1f",industryInfoVO.getModelVersion());
            setIndustryDescription(industryInfoVO.getIndustryDescription());
            boundaryType = industryInfoVO.getBoundaryType();
            IndustryInfoBoundaryViewVO refuse = new IndustryInfoBoundaryViewVO();
            refuse.setTitle("拒绝");
            refuse.setLeft(industryInfoVO.getRefuseStartScore());
            refuse.setRight(industryInfoVO.getReconsideStartScore());
            refuse.setBoundaryType(boundaryType);
            boundaryList.add(refuse);
            IndustryInfoBoundaryViewVO reconsider = new IndustryInfoBoundaryViewVO();
            reconsider.setTitle("复议");
            reconsider.setLeft(industryInfoVO.getReconsideStartScore());
            reconsider.setRight(industryInfoVO.getAgreeStartScore());
            reconsider.setBoundaryType(boundaryType);
            boundaryList.add(reconsider);
            IndustryInfoBoundaryViewVO agree = new IndustryInfoBoundaryViewVO();
            agree.setTitle("通过");
            agree.setLeft(industryInfoVO.getAgreeStartScore());
            agree.setRight(industryInfoVO.getAgreeEndScore());
            agree.setBoundaryType(boundaryType);
            boundaryList.add(agree);
            versionList.add(makeIndustryInfoViewVO(industryInfoVO));
            while(it.hasNext()){
                versionList.add(makeIndustryInfoViewVO(it.next()));
            }
        }
    }

    public IndustryInfoViewVO makeIndustryInfoViewVO(IndustryInfoVO industryInfoVO){
        IndustryInfoViewVO industryInfoViewVO = new IndustryInfoViewVO();
        industryInfoViewVO.setIndustryId(industryInfoVO.getIndustryId());
        industryInfoViewVO.setIndustryCode(industryInfoVO.getIndustryCode());
        industryInfoViewVO.setIndustryName(industryInfoVO.getIndustryName().contains("模型")?industryInfoVO.getIndustryName():industryInfoVO.getIndustryName().concat("模型"));
        industryInfoViewVO.setIndustryVersion(String.format("%.1f",industryInfoVO.getModelVersion()));
        industryInfoViewVO.setIndustryDescription(industryInfoVO.getIndustryDescription());
        industryInfoViewVO.setBoundaryType(industryInfoVO.getBoundaryType());
        IndustryInfoBoundaryViewVO refuse = new IndustryInfoBoundaryViewVO();
        refuse.setTitle("拒绝");
        refuse.setLeft(industryInfoVO.getRefuseStartScore());
        refuse.setRight(industryInfoVO.getReconsideStartScore());
        refuse.setBoundaryType(industryInfoVO.getBoundaryType());
        industryInfoViewVO.getBoundaryList().add(refuse);
        IndustryInfoBoundaryViewVO reconsider = new IndustryInfoBoundaryViewVO();
        reconsider.setTitle("复议");
        reconsider.setLeft(industryInfoVO.getReconsideStartScore());
        reconsider.setRight(industryInfoVO.getAgreeStartScore());
        reconsider.setBoundaryType(industryInfoVO.getBoundaryType());
        industryInfoViewVO.getBoundaryList().add(reconsider);
        IndustryInfoBoundaryViewVO agree = new IndustryInfoBoundaryViewVO();
        agree.setTitle("通过");
        agree.setLeft(industryInfoVO.getAgreeStartScore());
        agree.setRight(industryInfoVO.getAgreeEndScore());
        agree.setBoundaryType(industryInfoVO.getBoundaryType());
        industryInfoViewVO.getBoundaryList().add(agree);
        return industryInfoViewVO;
    }


    public static void main(String[] args) {
        Double f = 1d;
        System.out.println(String.format("%.1f",f));
    }
}
