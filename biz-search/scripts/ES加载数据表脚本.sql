CREATE TABLE `t_basic_info` (
  `id` bigint(18) NOT NULL AUTO_INCREMENT COMMENT '序号',
  `company_name` varchar(200) NOT NULL COMMENT '公司名称',
  `credit_code` varchar(50) DEFAULT NULL COMMENT '统一信用社代码',
  `regist_no` varchar(50) DEFAULT NULL COMMENT '注册号',
  `regist_capital` varchar(50) DEFAULT NULL COMMENT '注册资本',
  `run_status` varchar(50) DEFAULT NULL COMMENT '经营状态',
  `province` VARCHAR(100) DEFAULT NULL COMMENT '省份',
  `set_up_time` varchar(30) DEFAULT NULL COMMENT '成立时间',
  `legal_person_name` varchar(200) DEFAULT NULL COMMENT '法人名称',
  `regist_address` varchar(1024) DEFAULT NULL COMMENT '注册地址',
  `biz_scope` varchar(8192) DEFAULT NULL COMMENT '经营范围',
  `is_load` tinyint(1) DEFAULT '0' NOT NULL COMMENT '是否加载标志: 0-未加载 1-已加载',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uidx_entity_name` (`company_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin AUTO_INCREMENT=1 COMMENT='工商基本信息';
