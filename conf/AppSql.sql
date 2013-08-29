use autotool;
drop table if exists aq_aqyxqk;
CREATE TABLE `aq_aqyxqk` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `WXYID` int(11) DEFAULT NULL,
  `KSSJ` date DEFAULT NULL,
  `JSSJ` date DEFAULT NULL,
  `AQYXQKMS` varchar(1000) DEFAULT NULL,
  `CJRID` int(11) DEFAULT NULL,
  `CJSJ` datetime DEFAULT NULL,
  `XGRID` int(11) DEFAULT NULL,
  `XGSJ` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8 COMMENT='安全运行情况';
alter table aq_aqyxqk  drop COLUMN XGRID;
alter table aq_aqyxqk  change COLUMN XGSJ KJ date;
commit;