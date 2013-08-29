# MySQL-Front 5.1  (Build 2.7)

/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE */;
/*!40101 SET SQL_MODE='' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES */;
/*!40103 SET SQL_NOTES='ON' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;


# Host: 192.168.90.125    Database: autotool
# ------------------------------------------------------
# Server version 5.5.27-log

USE `autotool`;

#
# Source for table appconf
#

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

#
# Dumping data for table appconf
#
LOCK TABLES `appconf` WRITE;
/*!40000 ALTER TABLE `appconf` DISABLE KEYS */;

INSERT INTO `appconf` VALUES (11,'数据交换平台','/home/autotool/app/dataE-node.zip','auto','2013-03-20 20:14:40','2013-08-28 13:49:48',800);
/*!40000 ALTER TABLE `appconf` ENABLE KEYS */;
UNLOCK TABLES;

#
# Source for table appupdatelog
#

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

#
# Dumping data for table appupdatelog
#
LOCK TABLES `appupdatelog` WRITE;
/*!40000 ALTER TABLE `appupdatelog` DISABLE KEYS */;

INSERT INTO `appupdatelog` VALUES (1,11,800,1,'01330000000001','01330000000001','2013-08-27 15:07:41','2013-08-28 13:49:48',NULL);
/*!40000 ALTER TABLE `appupdatelog` ENABLE KEYS */;
UNLOCK TABLES;

#
# Source for table aq_aqyxqk
#

DROP TABLE IF EXISTS `aq_aqyxqk`;
CREATE TABLE `aq_aqyxqk` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `WXYID` int(11) DEFAULT NULL,
  `KSSJ` date DEFAULT NULL,
  `JSSJ` date DEFAULT NULL,
  `AQYXQKMS` varchar(1000) DEFAULT NULL,
  `CJRID` int(11) DEFAULT NULL,
  `CJSJ` datetime DEFAULT NULL,
  `KJ` date DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='安全运行情况';

#
# Dumping data for table aq_aqyxqk
#
LOCK TABLES `aq_aqyxqk` WRITE;
/*!40000 ALTER TABLE `aq_aqyxqk` DISABLE KEYS */;

/*!40000 ALTER TABLE `aq_aqyxqk` ENABLE KEYS */;
UNLOCK TABLES;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
