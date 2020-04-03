create table t_detail_config (
  nav_id        int(11) NOT NULL COMMENT '导航ID',
  nav_name      varchar(100) NOT NULL COMMENT '导航名称',
  nav_suffix    varchar(200) COMMENT '导航后缀',
  nav_level     tinyint(1) NOT NULL COMMENT '导航层级:1,2,3',
  nav_order     tinyint(1) NOT NULL COMMENT '导航顺序',
  nav_parent_id int(11) DEFAULT '-1' NOT NULL COMMENT '父导航',
  data_source   tinyint(1) DEFAULT '1' NOT NULL COMMENT '数据来源: 1-表 2-接口',
  data_type     tinyint(1) DEFAULT '1' NOT NULL COMMENT '导航内容格式: 1-散图 2-表格',
  data_scripts  text COMMENT '数据查询脚本(data_source=1)',
  api_prod_code varchar(100) COMMENT 'API产品代码(data_source=2)',
  api_version   varchar(5) COMMENT 'API版本号(data_source=2)',
  status        tinyint(1) DEFAULT '1' NOT NULL COMMENT '状态: 1-有效 0-无效',
  create_time   datetime DEFAULT current_timestamp NOT NULL COMMENT '创建时间',
  PRIMARY KEY (nav_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT '企业详情导航配置表';

create table t_detail_config_attr (
  nav_id        int(11) not null COMMENT 'ID',
  attr_name     varchar(200) not null COMMENT '属性名称',
  attr_column   varchar(100) COMMENT '属性取值列(数据来源是表)',
  attr_json     varchar(1000) COMMENT '属性取值JSON(数据来源是接口)',
  attr_order    int(11) not null COMMENT '属性展示顺序',
  attr_convert  varchar(1024)  COMMENT '属性转换java代码, 必须是CONVERT函数',
  status        tinyint(1) default '1 'not null COMMENT '状态: 1-有效 0-无效',
  create_time   datetime default current_timestamp not null COMMENT '创建时间',
  PRIMARY KEY (`nav_id`, `attr_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT '企业详情页面属性配置表';