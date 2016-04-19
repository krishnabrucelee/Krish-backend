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
-- Table structure for table `networks`
--

DROP TABLE IF EXISTS `networks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `networks` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cidr` varchar(255) DEFAULT NULL,
  `clean_up` bit(1) DEFAULT NULL,
  `created_by` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `display_text` varchar(255) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `gateway` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `netmask` varchar(255) DEFAULT NULL,
  `network_domain` varchar(255) DEFAULT NULL,
  `networkoffering_id` bigint(20) DEFAULT NULL,
  `network_restart` bit(1) DEFAULT NULL,
  `network_type` varchar(255) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `instance_id` bigint(20) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_p69kcy3an78wmofrhfwuue16n` (`department_id`),
  KEY `FK_30ex7v5c8cmyew9fqifbkfb4s` (`domain_id`),
  KEY `FK_syonij8irgse8p31n5yah5s3k` (`networkoffering_id`),
  KEY `FK_kx645iojhrfyh9624sesiweew` (`project_id`),
  KEY `FK_gn5hpc8y3lkgcc7x6ddj3ixa4` (`instance_id`),
  KEY `FK_6be9hgohe2o507r3rnl4xld4` (`zone_id`),
  CONSTRAINT `FK_30ex7v5c8cmyew9fqifbkfb4s` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`),
  CONSTRAINT `FK_6be9hgohe2o507r3rnl4xld4` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  CONSTRAINT `FK_gn5hpc8y3lkgcc7x6ddj3ixa4` FOREIGN KEY (`instance_id`) REFERENCES `vm_instances` (`id`),
  CONSTRAINT `FK_kx645iojhrfyh9624sesiweew` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `FK_p69kcy3an78wmofrhfwuue16n` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FK_syonij8irgse8p31n5yah5s3k` FOREIGN KEY (`networkoffering_id`) REFERENCES `network_offerings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `networks`
--

LOCK TABLES `networks` WRITE;
/*!40000 ALTER TABLE `networks` DISABLE KEYS */;
/*!40000 ALTER TABLE `networks` ENABLE KEYS */;
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
