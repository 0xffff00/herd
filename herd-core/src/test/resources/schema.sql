
/*Table structure for table `hd_me_file` */

DROP TABLE IF EXISTS `hd_me_file`;

CREATE TABLE `hd_me_file` (
  `path` varchar(255) NOT NULL,
  `hash` char(40) NOT NULL,
  `size` bigint(10) DEFAULT NULL,
  `mime_type` varchar(80) DEFAULT NULL,
  `desc` varchar(255) DEFAULT NULL,
  `sync_time` datetime DEFAULT NULL,
  PRIMARY KEY (`path`),
  KEY `HD_IDX_HASH_1` (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hd_me_file_image` */

DROP TABLE IF EXISTS `hd_me_file_image`;

CREATE TABLE `hd_me_file_image` (
  `hash` char(40) NOT NULL,
  `type` varchar(60) DEFAULT NULL,
  `height` int(11) DEFAULT NULL,
  `width` int(11) DEFAULT NULL,
  `exif_bit_depth` smallint(6) DEFAULT NULL,
  `exif_make` varchar(200) DEFAULT NULL,
  `exif_model` varchar(200) DEFAULT NULL,
  `exif_date_time` datetime DEFAULT NULL,
  `exif_color_space` varchar(200) DEFAULT NULL,
  `exif_exposure_time` varchar(200) DEFAULT NULL,
  `exif_white_balance` varchar(200) DEFAULT NULL,
  `exif_aperture` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hd_me_repo` */

DROP TABLE IF EXISTS `hd_me_repo`;

CREATE TABLE `hd_me_repo` (
  `name` varchar(100) NOT NULL,
  `path` varchar(2000) DEFAULT NULL,
  `url` varchar(2000) DEFAULT NULL,
  `type` char(8) DEFAULT NULL,
  `desc` varchar(2000) DEFAULT NULL,
  `save_time` datetime DEFAULT NULL,
  `state` char(1) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `hd_meta` */

DROP TABLE IF EXISTS `hd_meta`;

CREATE TABLE `hd_meta` (
  `name` varchar(255) NOT NULL,
  `val` varchar(255) DEFAULT NULL,
  `desc` varchar(1000) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
