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
-- Table structure for table `vm_instances`
--

DROP TABLE IF EXISTS `vm_instances`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vm_instances` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `application_name` varchar(255) DEFAULT NULL,
  `compute_max_iops` int(11) DEFAULT NULL,
  `compute_min_iops` int(11) DEFAULT NULL,
  `compute_offer_id` bigint(20) DEFAULT NULL,
  `cpu_cores` int(11) DEFAULT NULL,
  `cpu_speed` int(11) DEFAULT NULL,
  `instance_usage` varchar(255) DEFAULT NULL,
  `created_user_id` bigint(20) DEFAULT NULL,
  `created_date_time` datetime DEFAULT NULL,
  `department_id` bigint(20) DEFAULT NULL,
  `disk_io_read` int(11) DEFAULT NULL,
  `disk_io_write` int(11) DEFAULT NULL,
  `disk_kbs_read` int(11) DEFAULT NULL,
  `disk_kbs_write` int(11) DEFAULT NULL,
  `max_iops` bigint(20) DEFAULT NULL,
  `min_iops` bigint(20) DEFAULT NULL,
  `disk_size` bigint(20) DEFAULT NULL,
  `display_name` varchar(255) DEFAULT NULL,
  `domain_id` bigint(20) DEFAULT NULL,
  `event_message` varchar(255) DEFAULT NULL,
  `instance_event_type` varchar(255) DEFAULT NULL,
  `host_id` bigint(20) DEFAULT NULL,
  `hypervisor_type_id` bigint(20) DEFAULT NULL,
  `instance_internal_name` varchar(255) DEFAULT NULL,
  `instance_note` varchar(255) DEFAULT NULL,
  `instance_owner_id` bigint(20) DEFAULT NULL,
  `instance_private_ip` varchar(255) DEFAULT NULL,
  `is_removed` bit(1) DEFAULT NULL,
  `instance_iso_id` bigint(20) DEFAULT NULL,
  `instance_iso_name` varchar(255) DEFAULT NULL,
  `ssh_key_id` bigint(20) DEFAULT NULL,
  `memory` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `network_id` bigint(20) DEFAULT NULL,
  `network_kbs_read` int(11) DEFAULT NULL,
  `network_kbs_write` int(11) DEFAULT NULL,
  `network_offer_id` bigint(20) DEFAULT NULL,
  `template_ostype` varchar(255) DEFAULT NULL,
  `is_password_enabled` bit(1) DEFAULT NULL,
  `pod_id` bigint(20) DEFAULT NULL,
  `project_id` bigint(20) DEFAULT NULL,
  `instance_public_ip` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `storage_offer_id` bigint(20) DEFAULT NULL,
  `template_id` bigint(20) DEFAULT NULL,
  `template_name` varchar(255) DEFAULT NULL,
  `updated_user_id` bigint(20) DEFAULT NULL,
  `updated_date_time` datetime DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `version` bigint(20) DEFAULT NULL,
  `vnc_password` varchar(255) DEFAULT NULL,
  `volume_size` bigint(20) DEFAULT NULL,
  `zone_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_a11qryemkxab8xrhek6dgau2m` (`compute_offer_id`),
  KEY `FK_evy2ihnxysbshhv6wd6qj7gn5` (`department_id`),
  KEY `FK_i79pq42ap4hlxw769rwlwd98a` (`domain_id`),
  KEY `FK_2aav7h7htyqlgl3lifxcot0rm` (`host_id`),
  KEY `FK_12otr8mse6l5yksqjehlbbcio` (`hypervisor_type_id`),
  KEY `FK_hgqm6dxr39l9lyuagu6h0pl4o` (`instance_owner_id`),
  KEY `FK_3jsihnrotd6n5khxqimbns7q9` (`ssh_key_id`),
  KEY `FK_6ltvkgft7upb7ka9bwsp7j7mp` (`network_id`),
  KEY `FK_mpf76xgt67bbmvebke5fykuh8` (`network_offer_id`),
  KEY `FK_ew4nh88ucrjm6ae8daquvg2l3` (`project_id`),
  KEY `FK_fsvgem8mpbet2260rard4kftp` (`storage_offer_id`),
  KEY `FK_imld7ye1pckpsk4aetr6c5iak` (`template_id`),
  KEY `FK_3apf62mahbx3pi79546y2p79r` (`zone_id`),
  CONSTRAINT `FK_12otr8mse6l5yksqjehlbbcio` FOREIGN KEY (`hypervisor_type_id`) REFERENCES `hypervisors` (`id`),
  CONSTRAINT `FK_2aav7h7htyqlgl3lifxcot0rm` FOREIGN KEY (`host_id`) REFERENCES `hosts` (`id`),
  CONSTRAINT `FK_3apf62mahbx3pi79546y2p79r` FOREIGN KEY (`zone_id`) REFERENCES `zones` (`id`),
  CONSTRAINT `FK_3jsihnrotd6n5khxqimbns7q9` FOREIGN KEY (`ssh_key_id`) REFERENCES `sshkeys` (`id`),
  CONSTRAINT `FK_6ltvkgft7upb7ka9bwsp7j7mp` FOREIGN KEY (`network_id`) REFERENCES `networks` (`id`),
  CONSTRAINT `FK_a11qryemkxab8xrhek6dgau2m` FOREIGN KEY (`compute_offer_id`) REFERENCES `service_offerings` (`id`),
  CONSTRAINT `FK_evy2ihnxysbshhv6wd6qj7gn5` FOREIGN KEY (`department_id`) REFERENCES `departments` (`id`),
  CONSTRAINT `FK_ew4nh88ucrjm6ae8daquvg2l3` FOREIGN KEY (`project_id`) REFERENCES `projects` (`id`),
  CONSTRAINT `FK_fsvgem8mpbet2260rard4kftp` FOREIGN KEY (`storage_offer_id`) REFERENCES `storage_offerings` (`id`),
  CONSTRAINT `FK_hgqm6dxr39l9lyuagu6h0pl4o` FOREIGN KEY (`instance_owner_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FK_i79pq42ap4hlxw769rwlwd98a` FOREIGN KEY (`domain_id`) REFERENCES `domains` (`id`),
  CONSTRAINT `FK_imld7ye1pckpsk4aetr6c5iak` FOREIGN KEY (`template_id`) REFERENCES `templates` (`id`),
  CONSTRAINT `FK_mpf76xgt67bbmvebke5fykuh8` FOREIGN KEY (`network_offer_id`) REFERENCES `network_offerings` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vm_instances`
--

LOCK TABLES `vm_instances` WRITE;
/*!40000 ALTER TABLE `vm_instances` DISABLE KEYS */;
/*!40000 ALTER TABLE `vm_instances` ENABLE KEYS */;
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
