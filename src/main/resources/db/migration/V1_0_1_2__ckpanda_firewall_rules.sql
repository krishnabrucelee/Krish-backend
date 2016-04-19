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
-- Table structure for table `firewall_rules`
--

DROP TABLE IF EXISTS `firewall_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `firewall_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `display` bit(1) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `end_port` int(11) DEFAULT NULL,
  `icmp_code` int(11) DEFAULT NULL,
  `icmp_message` int(11) DEFAULT NULL,
  `ipaddress_id` bigint(20) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `network_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `protocol` varchar(255) DEFAULT NULL,
  `purpose` varchar(255) DEFAULT NULL,
  `source_cidr` varchar(255) DEFAULT NULL,
  `start_port` int(11) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `traffic_type` varchar(255) DEFAULT NULL,
  `upated_user_id` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `vpc` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_m8g4leal55jw7t28k5so04hy9` (`department_id`),
  KEY `FK_15k7e8eehdddhgnd5bo4m4tur` (`domain_id`),
  KEY `FK_nf35qkcx81o1rdboqba1anf2l` (`ipaddress_id`),
  KEY `FK_9vcacagi98rh90wxxrfxwbddr` (`network_id`),
  KEY `FK_3hlr8bm6mqm094bb63tp1jl2h` (`project_id`),
  CONSTRAINT `FK_15k7e8eehdddhgnd5bo4m4tur` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`),
  CONSTRAINT `FK_3hlr8bm6mqm094bb63tp1jl2h` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `FK_9vcacagi98rh90wxxrfxwbddr` FOREIGN KEY (`network_id`) REFERENCES `networks` (`id`),
  CONSTRAINT `FK_m8g4leal55jw7t28k5so04hy9` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FK_nf35qkcx81o1rdboqba1anf2l` FOREIGN KEY (`ipaddress_id`) REFERENCES `ip_addresses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `firewall_rules`
--

LOCK TABLES `firewall_rules` WRITE;
/*!40000 ALTER TABLE `firewall_rules` DISABLE KEYS */;
/*!40000 ALTER TABLE `firewall_rules` ENABLE KEYS */;
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
