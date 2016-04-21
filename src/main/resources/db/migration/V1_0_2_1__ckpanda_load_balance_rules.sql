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
-- Table structure for table `load_balance_rules`
--

DROP TABLE IF EXISTS `load_balance_rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `load_balance_rules` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `algorithm` varchar(255) DEFAULT NULL,
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `display` bit(1) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `icmp_code` int(11) DEFAULT NULL,
  `icmp_message` int(11) DEFAULT NULL,
  `ipaddress_id` bigint(20) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `lb_policy` bigint(20) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `network_id` bigint(20) DEFAULT NULL,
  `private_port` int(11) DEFAULT NULL,
  `protocol` varchar(255) DEFAULT NULL,
  `public_port` int(11) DEFAULT NULL,
  `purpose` varchar(255) DEFAULT NULL,
  `rule_is_active` bit(1) DEFAULT NULL,
  `source_cidr` varchar(255) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `sticky_uuid` varchar(255) DEFAULT NULL,
  `traffic_type` varchar(255) DEFAULT NULL,
  `upated_user_id` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `vpc` varchar(255) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_6w3g5rob1egjuetmvugbp04ua` (`domain_id`),
  KEY `FK_jko1qlbtp27t0tvu47lievgys` (`ipaddress_id`),
  KEY `FK_nbmyrs9jj3ucv84vm7tf4q0uj` (`lb_policy`),
  KEY `FK_7rw04mupcejj21b8kji2k4gyt` (`network_id`),
  KEY `FK_190v9xucpd0wl3vpaep2x7ok8` (`zone_id`),
  CONSTRAINT `FK_190v9xucpd0wl3vpaep2x7ok8` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  CONSTRAINT `FK_6w3g5rob1egjuetmvugbp04ua` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`),
  CONSTRAINT `FK_7rw04mupcejj21b8kji2k4gyt` FOREIGN KEY (`network_id`) REFERENCES `networks` (`id`),
  CONSTRAINT `FK_jko1qlbtp27t0tvu47lievgys` FOREIGN KEY (`ipaddress_id`) REFERENCES `ip_addresses` (`id`),
  CONSTRAINT `FK_nbmyrs9jj3ucv84vm7tf4q0uj` FOREIGN KEY (`lb_policy`) REFERENCES `lb_sticky_policy` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `load_balance_rules`
--

LOCK TABLES `load_balance_rules` WRITE;
/*!40000 ALTER TABLE `load_balance_rules` DISABLE KEYS */;
/*!40000 ALTER TABLE `load_balance_rules` ENABLE KEYS */;
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
