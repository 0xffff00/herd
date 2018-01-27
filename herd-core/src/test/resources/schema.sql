/*Table structure for table `hd_me_file` */

DROP TABLE IF EXISTS `hd_me_file`;

CREATE TABLE `hd_me_file` (
  `dir_path`  VARCHAR(255) NOT NULL,
  `file_name` VARCHAR(255) NOT NULL,
  `hash`      CHAR(40)     NOT NULL,
  `size`      BIGINT(10)   DEFAULT NULL,
  `mime_type` VARCHAR(80)  DEFAULT NULL,
  `desc`      VARCHAR(255) DEFAULT NULL,
  `sync_time` DATETIME     DEFAULT NULL,
  PRIMARY KEY (`dir_path`, `file_name`),
  KEY `HD_IDX_HASH_1` (`hash`)
) ENGINE =InnoDB DEFAULT CHARSET = utf8;

/*Table structure for table `hd_me_file_image` */

DROP TABLE IF EXISTS `hd_me_file_image`;

CREATE TABLE `hd_me_file_image` (
  `hash`               CHAR(40) NOT NULL,
  `type`               VARCHAR(60)  DEFAULT NULL,
  `height`             INT(11)      DEFAULT NULL,
  `width`              INT(11)      DEFAULT NULL,
  `exif_bit_depth`     SMALLINT(6)  DEFAULT NULL,
  `exif_make`          VARCHAR(200) DEFAULT NULL,
  `exif_model`         VARCHAR(200) DEFAULT NULL,
  `exif_date_time`     DATETIME     DEFAULT NULL,
  `exif_color_space`   VARCHAR(200) DEFAULT NULL,
  `exif_exposure_time` VARCHAR(200) DEFAULT NULL,
  `exif_white_balance` VARCHAR(200) DEFAULT NULL,
  `exif_aperture`      VARCHAR(200) DEFAULT NULL,
  PRIMARY KEY (`hash`)
) ENGINE =InnoDB DEFAULT CHARSET = utf8;

/*Table structure for table `hd_me_repo` */

DROP TABLE IF EXISTS `hd_me_repo`;

CREATE TABLE `hd_me_repo` (
  `name`      VARCHAR(100) NOT NULL,
  `path`      VARCHAR(2000) DEFAULT NULL,
  `url`       VARCHAR(2000) DEFAULT NULL,
  `type`      CHAR(8)       DEFAULT NULL,
  `desc`      VARCHAR(2000) DEFAULT NULL,
  `save_time` DATETIME      DEFAULT NULL,
  `state`     CHAR(1)       DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE =InnoDB DEFAULT CHARSET = utf8;

/*Table structure for table `hd_meta` */

DROP TABLE IF EXISTS `hd_meta`;

CREATE TABLE `hd_meta` (
  `name` VARCHAR(255) NOT NULL,
  `val`  VARCHAR(255)  DEFAULT NULL,
  `desc` VARCHAR(1000) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE =InnoDB DEFAULT CHARSET = utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
