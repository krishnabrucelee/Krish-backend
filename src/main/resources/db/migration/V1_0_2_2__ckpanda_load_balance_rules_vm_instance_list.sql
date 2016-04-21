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
-- Table structure for table `load_balance_rules_vm_instance_list`
--

DROP TABLE IF EXISTS `load_balance_rules_vm_instance_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `load_balance_rules_vm_instance_list` (
  `load_balance_rules` bigint(20) NOT NULL,
  `vm_instance_list` bigint(20) NOT NULL,
  KEY `FK_2naea85ikjr6ox57q4oanr6kd` (`vm_instance_list`),
  KEY `FK_rt02mr2ftjtdfsjstdsuolq2q` (`load_balance_rules`),
  CONSTRAINT `FK_2naea85ikjr6ox57q4oanr6kd` FOREIGN KEY (`vm_instance_list`) REFERENCES `vm_instances` (`id`),
  CONSTRAINT `FK_rt02mr2ftjtdfsjstdsuolq2q` FOREIGN KEY (`load_balance_rules`) REFERENCES `load_balance_rules` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `load_balance_rules_vm_instance_list`
--

LOCK TABLES `load_balance_rules_vm_instance_list` WRITE;
/*!40000 ALTER TABLE `load_balance_rules_vm_instance_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `load_balance_rules_vm_instance_list` ENABLE KEYS */;
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
