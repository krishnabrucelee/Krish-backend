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
-- Table structure for table `sshkeys`
--

DROP TABLE IF EXISTS `sshkeys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sshkeys` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_by` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `finger_print` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `public_key` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_2wujtjv37vm3frhpshxppa7kt` (`department_id`),
  KEY `FK_sgkf33vmyw61oupxhe3t8hlh4` (`domain_id`),
  KEY `FK_9c3su7cn7i6b8kbtcnfq40qqy` (`project_id`),
  CONSTRAINT `FK_2wujtjv37vm3frhpshxppa7kt` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FK_9c3su7cn7i6b8kbtcnfq40qqy` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `FK_sgkf33vmyw61oupxhe3t8hlh4` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sshkeys`
--

LOCK TABLES `sshkeys` WRITE;
/*!40000 ALTER TABLE `sshkeys` DISABLE KEYS */;
/*!40000 ALTER TABLE `sshkeys` ENABLE KEYS */;
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
