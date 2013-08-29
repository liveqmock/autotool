/*
Navicat MySQL Data Transfer

Source Server         : 192.168.90.209
Source Server Version : 50527
Source Host           : 192.168.90.209:3306
Source Database       : blue

Target Server Type    : MYSQL
Target Server Version : 50527
File Encoding         : 65001

Date: 2013-03-22 14:13:03
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `appupdatelog`
-- ----------------------------
DROP TABLE IF EXISTS `appupdatelog`;
CREATE TABLE `appupdatelog` (
  `id` int(4) NOT NULL AUTO_INCREMENT,
  `appid` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  `status` int(11) NOT NULL,
  `jgdm` varchar(50) DEFAULT NULL,
  `ip` varchar(100) DEFAULT NULL,
  `createTime` datetime NOT NULL,
  `updateTime` datetime NOT NULL,
  `desc` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `jgdm_log_indix` (`jgdm`,`appid`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8;
