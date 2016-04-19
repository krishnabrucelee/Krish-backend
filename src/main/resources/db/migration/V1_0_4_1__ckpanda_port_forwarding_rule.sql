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
-- Table structure for table `port_forwarding_rule`
--

DROP TABLE IF EXISTS `port_forwarding_rule`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `port_forwarding_rule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `fordisplay` tinyint(4) DEFAULT '0',
  `ipaddress_id` bigint(20) DEFAULT NULL,
  `is_active` tinyint(4) DEFAULT '1',
  `network_id` bigint(20) DEFAULT NULL,
  `private_end_port` int(11) DEFAULT NULL,
  `private_start_port` int(11) DEFAULT NULL,
  `protocol_type` varchar(255) DEFAULT NULL,
  `public_end_port` int(11) DEFAULT NULL,
  `public_start_port` int(11) DEFAULT NULL,
  `upated_user_id` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `vm_guest_ip` varchar(255) DEFAULT NULL,
  `instance_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_ptmn6m9ivafv0tvuh6ud6mqsx` (`ipaddress_id`),
  KEY `FK_ic5ctby0r09fukwv7unh1oa3r` (`network_id`),
  KEY `FK_jsv83qy9jckayxiuub01afwpk` (`instance_id`),
  CONSTRAINT `FK_ic5ctby0r09fukwv7unh1oa3r` FOREIGN KEY (`network_id`) REFERENCES `networks` (`id`),
  CONSTRAINT `FK_jsv83qy9jckayxiuub01afwpk` FOREIGN KEY (`instance_id`) REFERENCES `vm_instances` (`id`),
  CONSTRAINT `FK_ptmn6m9ivafv0tvuh6ud6mqsx` FOREIGN KEY (`ipaddress_id`) REFERENCES `ip_addresses` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `port_forwarding_rule`
--

LOCK TABLES `port_forwarding_rule` WRITE;
/*!40000 ALTER TABLE `port_forwarding_rule` DISABLE KEYS */;
/*!40000 ALTER TABLE `port_forwarding_rule` ENABLE KEYS */;
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
