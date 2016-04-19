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
-- Table structure for table `load_balance_rules_vm_ip_address`
--

DROP TABLE IF EXISTS `load_balance_rules_vm_ip_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `load_balance_rules_vm_ip_address` (
  `load_balance_rules` bigint(20) NOT NULL,
  `vm_ip_address` bigint(20) NOT NULL,
  KEY `FK_8t82prfyjo5fld2wnu2wib3jl` (`vm_ip_address`),
  KEY `FK_ak2jw24kpyatt6qnuwylu0fv5` (`load_balance_rules`),
  CONSTRAINT `FK_8t82prfyjo5fld2wnu2wib3jl` FOREIGN KEY (`vm_ip_address`) REFERENCES `vm_ipaddress` (`id`),
  CONSTRAINT `FK_ak2jw24kpyatt6qnuwylu0fv5` FOREIGN KEY (`load_balance_rules`) REFERENCES `load_balance_rules` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `load_balance_rules_vm_ip_address`
--

LOCK TABLES `load_balance_rules_vm_ip_address` WRITE;
/*!40000 ALTER TABLE `load_balance_rules_vm_ip_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `load_balance_rules_vm_ip_address` ENABLE KEYS */;
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
