/*
Navicat MySQL Data Transfer

Source Server         : 192.168.90.209
Source Server Version : 50527
Source Host           : 192.168.90.209:3306
Source Database       : blue

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2013-03-22 14:12:34
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `appconf`
-- ----------------------------
DROP TABLE IF EXISTS `appconf`;
CREATE TABLE `appconf` (
  `appid` int(11) NOT NULL,
  `appname` varchar(100) DEFAULT NULL,
  `apppath` varchar(100) DEFAULT NULL,
  `appdb` varchar(255) DEFAULT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime DEFAULT NULL,
  `version` int(11) DEFAULT NULL,
  UNIQUE KEY `app_conf_appid` (`appid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of appconf
-- ----------------------------
