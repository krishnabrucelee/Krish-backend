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
-- Table structure for table `vm_ipaddress`
--

DROP TABLE IF EXISTS `vm_ipaddress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vm_ipaddress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guest_ipaddress` varchar(255) DEFAULT NULL,
  `ip_type` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `nic_id` bigint(20) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `instance_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_mi1qstx6l5btlldebbn4l30h2` (`instance_id`),
  CONSTRAINT `FK_mi1qstx6l5btlldebbn4l30h2` FOREIGN KEY (`instance_id`) REFERENCES `vm_instances` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vm_ipaddress`
--

LOCK TABLES `vm_ipaddress` WRITE;
/*!40000 ALTER TABLE `vm_ipaddress` DISABLE KEYS */;
/*!40000 ALTER TABLE `vm_ipaddress` ENABLE KEYS */;
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
