package com.biz.credit.vo;

import com.biz.credit.domain.DTable;
import com.biz.credit.domain.DTree;
import com.biz.credit.domain.Project;
import com.biz.credit.domain.ScoreCard;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectVO extends Project{

    private Boolean canDelete;

    private List<ScoreCard> scoreCardList;

    private List<DTree> treeList;

    private List<DTable> tableList;

    private List<IndustryInfoViewVO> industryList;
}
