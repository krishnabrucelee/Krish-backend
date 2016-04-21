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
-- Table structure for table `templates`
--

DROP TABLE IF EXISTS `templates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `templates` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `architecture` varchar(255) DEFAULT NULL,
  `bootable` tinyint(4) DEFAULT '1',
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `department` tinyblob,
  `department_id` bigint(20) DEFAULT NULL,
  `description` varchar(255) NOT NULL,
  `detailed_description` text,
  `display_text` varchar(255) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `dynamically_scalable` tinyint(4) DEFAULT '0',
  `extractable` tinyint(4) DEFAULT '0',
  `featured` tinyint(4) DEFAULT '0',
  `format` varchar(255) NOT NULL,
  `hvm` tinyint(4) DEFAULT '0',
  `hypervisor_type_id` bigint(20) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `keyboard_type` varchar(255) DEFAULT NULL,
  `minimum_core` int(11) DEFAULT NULL,
  `minimum_memory` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `nic_adapter` varchar(255) DEFAULT NULL,
  `one_time_chargeable` tinyint(4) DEFAULT '0',
  `os_category` bigint(20) DEFAULT NULL,
  `os_type` bigint(20) DEFAULT NULL,
  `os_version` varchar(255) DEFAULT NULL,
  `password_enabled` tinyint(4) DEFAULT '0',
  `reference_url` varchar(255) DEFAULT NULL,
  `root_disk_controller` varchar(255) DEFAULT NULL,
  `routing` tinyint(4) DEFAULT '0',
  `share` tinyint(4) DEFAULT '0',
  `size` bigint(20) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `template_creation_type` bit(1) DEFAULT NULL,
  `template_owner_id` bigint(20) DEFAULT NULL,
  `type` varchar(255) DEFAULT NULL,
  `unique_name` varchar(255) DEFAULT NULL,
  `update_count` int(11) DEFAULT NULL,
  `updated_user_id` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `xs_version` tinyint(4) DEFAULT '0',
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_586rmfe4p8vd3rn4nxajcqc4k` (`domain_id`),
  KEY `FK_j6tkyvhn782wgujqlyb6vs934` (`hypervisor_type_id`),
  KEY `FK_20m6pqf60tifl6nm3wk26n2ma` (`os_category`),
  KEY `FK_948kuq8jhvsk27s1w1hd6n03g` (`os_type`),
  KEY `FK_ssuo5p4mj6mg609yha81d0x59` (`template_owner_id`),
  KEY `FK_96xpjvae3kofuudufna1rbilm` (`zone_id`),
  CONSTRAINT `FK_20m6pqf60tifl6nm3wk26n2ma` FOREIGN KEY (`os_category`) REFERENCES `os_categories` (`id`),
  CONSTRAINT `FK_586rmfe4p8vd3rn4nxajcqc4k` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`),
  CONSTRAINT `FK_948kuq8jhvsk27s1w1hd6n03g` FOREIGN KEY (`os_type`) REFERENCES `os_types` (`id`),
  CONSTRAINT `FK_96xpjvae3kofuudufna1rbilm` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  CONSTRAINT `FK_j6tkyvhn782wgujqlyb6vs934` FOREIGN KEY (`hypervisor_type_id`) REFERENCES `hypervisors` (`id`),
  CONSTRAINT `FK_ssuo5p4mj6mg609yha81d0x59` FOREIGN KEY (`template_owner_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `templates`
--

LOCK TABLES `templates` WRITE;
/*!40000 ALTER TABLE `templates` DISABLE KEYS */;
/*!40000 ALTER TABLE `templates` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-19 18:49:55
