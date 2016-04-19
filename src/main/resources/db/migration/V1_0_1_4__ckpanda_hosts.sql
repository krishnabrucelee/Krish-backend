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
-- Table structure for table `hosts`
--

DROP TABLE IF EXISTS `hosts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hosts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cluster_id` bigint(20) DEFAULT NULL,
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `host_ha` varchar(255) DEFAULT NULL,
  `host_ipaddress` varchar(255) DEFAULT NULL,
  `hypervisor` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `pod_id` bigint(20) DEFAULT NULL,
  `state` int(11) DEFAULT NULL,
  `updated_user_id` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_1h19vghvhhcl10pe0wy8e9kpe` (`zone_id`),
  KEY `FK_j9g7suiitbtni0pefhma1ikad` (`pod_id`),
  CONSTRAINT `FK_1h19vghvhhcl10pe0wy8e9kpe` FOREIGN KEY (`zone_id`) REFERENCES `clusters` (`id`),
  CONSTRAINT `FK_j9g7suiitbtni0pefhma1ikad` FOREIGN KEY (`pod_id`) REFERENCES `pods` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hosts`
--

LOCK TABLES `hosts` WRITE;
/*!40000 ALTER TABLE `hosts` DISABLE KEYS */;
/*!40000 ALTER TABLE `hosts` ENABLE KEYS */;
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
