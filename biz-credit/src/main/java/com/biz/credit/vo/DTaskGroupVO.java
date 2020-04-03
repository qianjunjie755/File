package com.biz.credit.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class DTaskGroupVO implements Serializable {
    private String title;
    private Integer addable;
    private Integer index;
    private List<DTaskParamVO> items = new ArrayList<>();

    public final static Map<Integer,String> GROUP_NAME_MAP = new HashMap<Integer,String>(){
        {
            put(1,"填写企业进件参数");put(2,"填写法人进件参数");
            put(3,"填写关联人进件参数");put(4,"填写关联人进件参数");
        }
    };
}
