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
-- Table structure for table `snapshots`
--

DROP TABLE IF EXISTS `snapshots`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `snapshots` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `policy_is_active` bit(1) DEFAULT NULL,
  `snapshot_policy_uuid` varchar(255) DEFAULT NULL,
  `snapshottype` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_user_id` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `volume_id` bigint(20) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_kxdtllxm2065lnlie8gyije4o` (`department_id`),
  KEY `FK_97etim7ym3wwo1i054tt24qwv` (`domain_id`),
  KEY `FK_j4lnp3acr187c33mpu69unuvp` (`volume_id`),
  KEY `FK_ghlhlhthrq5oyhyp0hf7bvlbp` (`zone_id`),
  CONSTRAINT `FK_97etim7ym3wwo1i054tt24qwv` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`),
  CONSTRAINT `FK_ghlhlhthrq5oyhyp0hf7bvlbp` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  CONSTRAINT `FK_j4lnp3acr187c33mpu69unuvp` FOREIGN KEY (`volume_id`) REFERENCES `volumes` (`id`),
  CONSTRAINT `FK_kxdtllxm2065lnlie8gyije4o` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `snapshots`
--

LOCK TABLES `snapshots` WRITE;
/*!40000 ALTER TABLE `snapshots` DISABLE KEYS */;
/*!40000 ALTER TABLE `snapshots` ENABLE KEYS */;
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
