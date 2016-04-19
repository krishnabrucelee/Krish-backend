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
-- Table structure for table `ip_addresses`
--

DROP TABLE IF EXISTS `ip_addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ip_addresses` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `display` bit(1) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_source_nat` bit(1) DEFAULT NULL,
  `is_static_nat` bit(1) DEFAULT NULL,
  `network_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `public_ip_address` varchar(255) NOT NULL,
  `state` varchar(255) DEFAULT NULL,
  `updated_user_id` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) NOT NULL,
  `version` bigint(20) DEFAULT NULL,
  `vlan` varchar(255) DEFAULT NULL,
  `instance_id` bigint(20) DEFAULT NULL,
  `vpn_for_display` bit(1) DEFAULT NULL,
  `vpn_ip_range` varchar(255) DEFAULT NULL,
  `vpn_preshared_key` varchar(255) DEFAULT NULL,
  `vpn_state` varchar(255) DEFAULT NULL,
  `vpn_uuid` varchar(255) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_8osu28pn0mcwt5qdjgff1ahpc` (`department_id`),
  KEY `FK_35i25bf5nf61icpe6r8x3vnih` (`domain_id`),
  KEY `FK_l7kn6gjpsdpu4kgensklfyhg8` (`network_id`),
  KEY `FK_7f6mr86hcnwco1jgobdf76oo2` (`project_id`),
  KEY `FK_fdcd3rlcwqfys9at0fp7rsbmn` (`instance_id`),
  KEY `FK_gfi8wfvgddp0lcb6jlj05l096` (`zone_id`),
  CONSTRAINT `FK_35i25bf5nf61icpe6r8x3vnih` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`),
  CONSTRAINT `FK_7f6mr86hcnwco1jgobdf76oo2` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `FK_8osu28pn0mcwt5qdjgff1ahpc` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FK_fdcd3rlcwqfys9at0fp7rsbmn` FOREIGN KEY (`instance_id`) REFERENCES `vm_instances` (`id`),
  CONSTRAINT `FK_gfi8wfvgddp0lcb6jlj05l096` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  CONSTRAINT `FK_l7kn6gjpsdpu4kgensklfyhg8` FOREIGN KEY (`network_id`) REFERENCES `networks` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ip_addresses`
--

LOCK TABLES `ip_addresses` WRITE;
/*!40000 ALTER TABLE `ip_addresses` DISABLE KEYS */;
/*!40000 ALTER TABLE `ip_addresses` ENABLE KEYS */;
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
