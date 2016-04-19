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
-- Table structure for table `service_offerings_cost`
--

DROP TABLE IF EXISTS `service_offerings_cost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `service_offerings_cost` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `compute_id` bigint(20) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `instance_running_cost_memory` decimal(10,4) DEFAULT '0.0000',
  `instance_running_cost_per_mb` decimal(10,4) DEFAULT '0.0000',
  `instance_running_cost_per_mhz` decimal(10,4) DEFAULT '0.0000',
  `instance_running_cost_per_vcpu` decimal(10,4) DEFAULT '0.0000',
  `instance_running_cost_vcpu` decimal(10,4) DEFAULT '0.0000',
  `instance_stoppage_cost_memory` decimal(10,4) DEFAULT '0.0000',
  `instance_stoppage_cost_per_mb` decimal(10,4) DEFAULT '0.0000',
  `instance_stoppage_cost_per_mhz` decimal(10,4) DEFAULT '0.0000',
  `instance_stoppage_cost_per_vcpu` decimal(10,4) DEFAULT '0.0000',
  `instance_stoppage_cost_vcpu` decimal(10,4) DEFAULT '0.0000',
  `setup_cost` double DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `total_cost` double DEFAULT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_caboo22sggc69o34mpjgj1nqr` (`zone_id`),
  CONSTRAINT `FK_caboo22sggc69o34mpjgj1nqr` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `service_offerings_cost`
--

LOCK TABLES `service_offerings_cost` WRITE;
/*!40000 ALTER TABLE `service_offerings_cost` DISABLE KEYS */;
/*!40000 ALTER TABLE `service_offerings_cost` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-19 18:49:53
