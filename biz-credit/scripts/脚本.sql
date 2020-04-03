DROP TABLE IF EXISTS report.t_d_score_level;
CREATE TABLE IF NOT EXISTS report.t_d_score_level (
  api_code VARCHAR(200) NOT NULL COMMENT '客户代码',
  model_code INT(11) NOT NULL COMMENT '对应评分模型ID代码',
  model_type INT(2) NOT NULL COMMENT '模型类型: 3-反欺诈评分 4-评分模型 5-评分卡',
  score_level VARCHAR(10) NOT NULL COMMENT '评分等级',
  score_range VARCHAR(50) NOT NULL COMMENT '评分区间, 如: [0, 200), [200,300)',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (api_code, model_code, model_type, score_level)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='模型评分等级表';

-- 1-企业规则 2-自然人规则 3-反欺诈评分 4-评分模型 5-评分卡 6-决策表 7-决策树 8-额度模型 9-定价模型
DROP TABLE IF EXISTS report.t_d_model_type;
CREATE TABLE IF NOT EXISTS report.t_d_model_type (
  model_type INT(5) NOT NULL COMMENT '模型类型',
  model_type_name VARCHAR(100) NOT NULL COMMENT '模型类型名称',
  model_flag TINYINT(1) DEFAULT '1' NOT NULL COMMENT '模型标识: 1-模型 0-非模型',
  model_order INT(5) NOT NULL COMMENT '模型顺序',
  status TINYINT(1) DEFAULT '1' NOT NULL COMMENT '状态: 1-有效 0-无效',
  create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (model_type)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='模型类型配置表';