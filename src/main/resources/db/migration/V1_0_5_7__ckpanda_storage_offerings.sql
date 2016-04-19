-- MySQL dump 10.13  Distrib 5.7.9, for linux-glibc2.5 (x86_64)
--
-- Host: localhost    Database: ckpanda
-- ------------------------------------------------------
-- Server version	5.6.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `storage_offerings`
--

DROP TABLE IF EXISTS `storage_offerings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `storage_offerings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `bytes_read_rate` bigint(20) DEFAULT NULL,
  `bytes_write_rate` bigint(20) DEFAULT NULL,
  `iops_read_rate` bigint(20) DEFAULT NULL,
  `iops_write_rate` bigint(20) DEFAULT NULL,
  `max_iops` bigint(20) DEFAULT NULL,
  `min_iops` bigint(20) DEFAULT NULL,
  `disk_size` bigint(20) NOT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '1',
  `is_custom_disk` tinyint(4) NOT NULL DEFAULT '0',
  `is_customized_iops` tinyint(4) DEFAULT '0',
  `is_public` tinyint(4) DEFAULT '1',
  `name` varchar(30) NOT NULL,
  `qos_type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `storage_tags` varchar(255) DEFAULT NULL,
  `storage_type` varchar(255) DEFAULT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kq4b0777b1d5nfumub1qmrc77` (`domain_id`),
  CONSTRAINT `FK_kq4b0777b1d5nfumub1qmrc77` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `storage_offerings`
--

LOCK TABLES `storage_offerings` WRITE;
/*!40000 ALTER TABLE `storage_offerings` DISABLE KEYS */;
/*!40000 ALTER TABLE `storage_offerings` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-19 18:49:54
