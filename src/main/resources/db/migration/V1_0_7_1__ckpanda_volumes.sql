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
-- Table structure for table `volumes`
--

DROP TABLE IF EXISTS `volumes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `volumes` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `checksum` varchar(255) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `max_iops` bigint(20) DEFAULT NULL,
  `min_iops` bigint(20) DEFAULT NULL,
  `disk_size` bigint(20) DEFAULT NULL,
  `disk_size_flag` bit(1) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `event_message` varchar(255) DEFAULT NULL,
  `format` varchar(255) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '1',
  `is_removed` bit(1) DEFAULT NULL,
  `is_shrink` bit(1) DEFAULT NULL,
  `name` varchar(20) NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `storage_offer_id` bigint(20) DEFAULT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `instance_id` bigint(20) DEFAULT NULL,
  `volume_type` int(11) NOT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_s5jhpvd2j6brjk2syo5mcx69p` (`department_id`),
  KEY `FK_k3vbcxipc756iyl39vqhlwx6p` (`domain_id`),
  KEY `FK_4fnsmdrw233p56qp8xa84hf72` (`project_id`),
  KEY `FK_rmr5pfrayeb5vxp9mypnl0qk4` (`storage_offer_id`),
  KEY `FK_bsrtm26dxxlyuv8jk78u890ar` (`instance_id`),
  KEY `FK_kdtiv5amyrbosbftx61fnqapf` (`zone_id`),
  CONSTRAINT `FK_4fnsmdrw233p56qp8xa84hf72` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `FK_bsrtm26dxxlyuv8jk78u890ar` FOREIGN KEY (`instance_id`) REFERENCES `vm_instances` (`id`),
  CONSTRAINT `FK_k3vbcxipc756iyl39vqhlwx6p` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`),
  CONSTRAINT `FK_kdtiv5amyrbosbftx61fnqapf` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  CONSTRAINT `FK_rmr5pfrayeb5vxp9mypnl0qk4` FOREIGN KEY (`storage_offer_id`) REFERENCES `storage_offerings` (`id`),
  CONSTRAINT `FK_s5jhpvd2j6brjk2syo5mcx69p` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `volumes`
--

LOCK TABLES `volumes` WRITE;
/*!40000 ALTER TABLE `volumes` DISABLE KEYS */;
/*!40000 ALTER TABLE `volumes` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-19 18:49:56
