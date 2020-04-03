CREATE TABLE `t_system_module` (
  `module_id` int(11) NOT NULL COMMENT '主体id',
  `module_name` varchar(100) DEFAULT NULL COMMENT '名称',
  `status` tinyint(1) DEFAULT '1' NOT NULL COMMENT '状态：1-启用 0-停用',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='系统模块配置表';

CREATE TABLE `t_portrait_type` (
  `type_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '类目id',
  `type_code` varchar(50) NOT NULL COMMENT '编码',
  `type_name` varchar(200) DEFAULT NULL COMMENT '名称',
  `module_id` int(11) NOT NULL COMMENT '系统模块id',
  `status` tinyint(1) DEFAULT '1' NOT NULL COMMENT '状态：1-启用 0-停用',
  `parent_id` int(11) DEFAULT '-1' NOT NULL COMMENT '父类型ID',
  `update_user` int(11) COMMENT '更新用户ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` int(11) COMMENT '创建用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 COMMENT='画像类目配置表';

CREATE TABLE `t_portrait_label` (
  `label_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '标签id',
  `label_code` varchar(45) NOT NULL COMMENT '标签代码',
  `label_name` varchar(100) DEFAULT NULL COMMENT '标签名称',
  `label_desc` varchar(500) COMMENT '标签备注',
  `label_type` tinyint(1) DEFAULT '1' NOT NULL COMMENT '标签类型：1-静态标签 2-动态标签',
  `label_enum` varchar(100) COMMENT '标签枚举',
  `type_id` int(11) NOT NULL COMMENT '类目ID',
  `table_name` varchar(100) COMMENT '对应表',
  `calc_logic` varchar(500) COMMENT '计算逻辑',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 1-启用 0-停用 2-挂起',
  `update_user` int(11) COMMENT '更新用户ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` int(11) COMMENT '创建用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 COMMENT='画像标签表';

CREATE TABLE `t_portrait_value` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `label_id` int(11) NOT NULL COMMENT '标签id',
  `label_value` varchar(500) COMMENT '标签备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 COMMENT='画像值记录表';

CREATE TABLE `t_portrait_task` (
  `task_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `task_name` varchar(200) NOT NULL COMMENT '任务名称',
  `task_type` tinyint(1) NOT NULL COMMENT '任务类型: 1-固定频率（每天/每周/每月/每年） 2-有效期（起始终止日期）',
  `module_id` int(11) NOT NULL COMMENT '系统模块id',
  `interval` varchar(5) COMMENT '频率: 1D/1W/1M/1Y（每天/每周/每月/每年）',
  `start_date` date COMMENT '起始日期',
  `end_date` date COMMENT '终止日期',
  `last_run_time` datetime COMMENT '最近运行时间',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 1-启用 0-停用',
  `update_user` int(11) COMMENT '更新用户ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` int(11) COMMENT '创建用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 COMMENT='画像计算任务表';

CREATE TABLE `t_portrait_task_label` (
  `task_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '任务id',
  `label_id` int(11) NOT NULL COMMENT '标签ID',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '状态: 1-启用 0-停用',
  `update_user` int(11) COMMENT '更新用户ID',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_user` int(11) COMMENT '创建用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`task_id`, `label_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 COMMENT='画像计算任务标签表';