/*
Navicat MySQL Data Transfer

Source Server         : 10.20.10.173
Source Server Version : 50720
Source Host           : 10.20.10.173:3306
Source Database       : data_manager

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2021-01-13 11:47:56
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for t_data_manager_data
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_data`;
CREATE TABLE `t_data_manager_data` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL COMMENT '数据资产的标识，一般与业务相关',
  `data_name` varchar(255) DEFAULT NULL COMMENT '跟type字段配置，type=1(即Hive表）则该字段表示hive表名，type=2(即Hbase表)则该字段表示hbase表名，type=3(即HDFS文件）则该字段表示hdfs完整路径文件名',
  `type` varchar(255) DEFAULT NULL,
  `size` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `destroy_method` int(11) DEFAULT NULL,
  `destroy_time` varchar(255) DEFAULT NULL,
  `zz_public` int(1) DEFAULT NULL,
  `zz_encrypt` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_data_manager_data_template
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_data_template`;
CREATE TABLE `t_data_manager_data_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `code` varchar(255) DEFAULT NULL,
  `column_json` text,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_data_manager_datasource
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_datasource`;
CREATE TABLE `t_data_manager_datasource` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `port` int(11) DEFAULT NULL,
  `category1` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_data_manager_download_log
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_download_log`;
CREATE TABLE `t_data_manager_download_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `data_id` int(11) DEFAULT NULL,
  `data_name` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_data_manager_import_log
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_import_log`;
CREATE TABLE `t_data_manager_import_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type` int(11) DEFAULT NULL,
  `input_type` varchar(255) DEFAULT NULL,
  `input_parameter` varchar(255) DEFAULT NULL,
  `output_type` varchar(255) DEFAULT NULL,
  `output_name` varchar(255) DEFAULT NULL,
  `output_id` int(11) DEFAULT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_data_manager_label
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_label`;
CREATE TABLE `t_data_manager_label` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_data_manager_login_log
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_login_log`;
CREATE TABLE `t_data_manager_login_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL COMMENT '中文名',
  `username` varchar(255) DEFAULT NULL COMMENT '账号',
  `login_time` datetime DEFAULT NULL,
  `login_ip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=566 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_data_manager_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_permission`;
CREATE TABLE `t_data_manager_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `cn_name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `operations` varchar(20000) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_data_manager_permission
-- ----------------------------
INSERT INTO `t_data_manager_permission` VALUES ('1', 'dashboard', '数据总览', '2', '[{‘action’:\'get\',\'defaultCheck\':false,\'describe\':\'删除\'},{\'action\':\'update\',\'defaultCheck\':false,\'describe\':\'修改\'},{\'action\':\'query\',\'defaultCheck\':false,\'describe\':\'查询\'},{\'action\':\'get\',\'defaultCheck\':false,\'describe\':\'详情\'},{\'action\':\'add\',\'defaultCheck\':false,\'describe\':\'新增\'},{\'action\':\'query\',\'defaultCheck\':false,\'describe\':\'查询\'}]');
INSERT INTO `t_data_manager_permission` VALUES ('2', 'console', '数据控制台', '1', '[{\"action\":\"add\",\"defaultCheck\":false,\"describe\":\"新增\"},{\"action\":\"get\",\"defaultCheck\":false,\"describe\":\"详情\"},{\"action\":\"query\",\"defaultCheck\":false,\"describe\":\"查询\"},{\"action\":\"update\",\"defaultCheck\":false,\"describe\":\"修改\"},{\"action\":\"delete\",\"defaultCheck\":false,\"describe\":\"删除\"}]');
INSERT INTO `t_data_manager_permission` VALUES ('3', 'collection', '数据采集', '1', '[{\"action\":\"add\",\"defaultCheck\":false,\"describe\":\"新增\"},{\"action\":\"query\",\"defaultCheck\":false,\"describe\":\"查询\"},{\"action\":\"get\",\"defaultCheck\":false,\"describe\":\"详情\"},{\"action\":\"update\",\"defaultCheck\":false,\"describe\":\"修改\"},{\"action\":\"delete\",\"defaultCheck\":false,\"describe\":\"删除\"}]');
INSERT INTO `t_data_manager_permission` VALUES ('4', 'search', '数据检索', '1', '[{\"action\":\"add\",\"defaultCheck\":false,\"describe\":\"新增\"},{\"action\":\"query\",\"defaultCheck\":false,\"describe\":\"查询\"},{\"action\":\"get\",\"defaultCheck\":false,\"describe\":\"详情\"},{\"action\":\"update\",\"defaultCheck\":false,\"describe\":\"修改\"},{\"action\":\"delete\",\"defaultCheck\":false,\"describe\":\"删除\"}]');
INSERT INTO `t_data_manager_permission` VALUES ('5', 'visual', '数据可视化', '1', '[{\"action\":\"add\",\"defaultCheck\":false,\"describe\":\"新增\"},{\"action\":\"import\",\"defaultCheck\":false,\"describe\":\"导入\"},{\"action\":\"get\",\"defaultCheck\":false,\"describe\":\"详情\"},{\"action\":\"update\",\"defaultCheck\":false,\"describe\":\"修改\"}]');
INSERT INTO `t_data_manager_permission` VALUES ('7', 'tool', '交互式工具', '1', '[{\"action\":\"add\",\"defaultCheck\":false,\"describe\":\"新增\"},{\"action\":\"query\",\"defaultCheck\":false,\"describe\":\"查询\"},{\"action\":\"get\",\"defaultCheck\":false,\"describe\":\"详情\"},{\"action\":\"update\",\"defaultCheck\":false,\"describe\":\"修改\"},{\"action\":\"delete\",\"defaultCheck\":false,\"describe\":\"删除\"}]');
INSERT INTO `t_data_manager_permission` VALUES ('8', 'system', '系统管理', '1', '[{\"action\":\"add\",\"defaultCheck\":false,\"describe\":\"新增\"},{\"action\":\"get\",\"defaultCheck\":false,\"describe\":\"详情\"},{\"action\":\"update\",\"defaultCheck\":false,\"describe\":\"修改\"},{\"action\":\"delete\",\"defaultCheck\":false,\"describe\":\"删除\"}]');
INSERT INTO `t_data_manager_permission` VALUES ('9', 'log', '日志查询', '1', '[{\"action\":\"add\",\"defaultCheck\":false,\"describe\":\"新增\"},{\"action\":\"get\",\"defaultCheck\":false,\"describe\":\"详情\"},{\"action\":\"update\",\"defaultCheck\":false,\"describe\":\"修改\"},{\"action\":\"delete\",\"defaultCheck\":false,\"describe\":\"删除\"}]');
INSERT INTO `t_data_manager_permission` VALUES ('10', 'account', '个人中心', '1', null);

-- ----------------------------
-- Table structure for t_data_manager_relation_datasource_data
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_relation_datasource_data`;
CREATE TABLE `t_data_manager_relation_datasource_data` (
  `datasource_id` int(11) NOT NULL,
  `data_id` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=267 DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_data_manager_relation_label_data
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_relation_label_data`;
CREATE TABLE `t_data_manager_relation_label_data` (
  `label_id` int(11) NOT NULL,
  `data_id` mediumtext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
-- Table structure for t_data_manager_relation_role_permission
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_relation_role_permission`;
CREATE TABLE `t_data_manager_relation_role_permission` (
  `permission_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`permission_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_data_manager_relation_role_permission
-- ----------------------------
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('1', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('2', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('3', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('4', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('5', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('6', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('7', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('8', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('9', '1');
INSERT INTO `t_data_manager_relation_role_permission` VALUES ('10', '1');


-- ----------------------------
-- Table structure for t_data_manager_relation_user_role
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_relation_user_role`;
CREATE TABLE `t_data_manager_relation_user_role` (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  `path` varchar(255) DEFAULT NULL,
  `id` int(11) DEFAULT NULL,
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_data_manager_relation_user_role
-- ----------------------------
INSERT INTO `t_data_manager_relation_user_role` VALUES ('1', '1', null, null);

-- ----------------------------
-- Table structure for t_data_manager_role
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_role`;
CREATE TABLE `t_data_manager_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `cn_name` varchar(255) DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `creatorId` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_data_manager_role
-- ----------------------------
INSERT INTO `t_data_manager_role` VALUES ('1', 'admin', '1', '管理员', '超级管理员', '2020-06-03 17:48:23', '1');


-- ----------------------------
-- Table structure for t_data_manager_search_log
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_search_log`;
CREATE TABLE `t_data_manager_search_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `keyword` varchar(255) DEFAULT NULL,
  `user_id` int(11) DEFAULT NULL,
  `search_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for t_data_manager_session
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_session`;
CREATE TABLE `t_data_manager_session` (
  `id` varchar(64) NOT NULL COMMENT '主键',
  `user_id` int(11) DEFAULT NULL COMMENT '用户id',
  `ip` varchar(45) DEFAULT NULL COMMENT '登录ip',
  `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
-- Table structure for t_data_manager_user
-- ----------------------------
DROP TABLE IF EXISTS `t_data_manager_user`;
CREATE TABLE `t_data_manager_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `status` int(1) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `last_login_ip` varchar(255) DEFAULT NULL,
  `last_login_time` datetime DEFAULT NULL,
  `creator_id` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL,
  `error_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of t_data_manager_user
-- ----------------------------
INSERT INTO `t_data_manager_user` VALUES ('1', '系统管理员', 'admin', '21232f297a57a5a743894a0e4a801fc3', '/avatar.png', '1', '18555113803', '5685164@qq.com', '127.0.0.1', '2021-01-13 11:38:19', '1', '2020-04-03 11:37:50', 'blabla111', '0');
