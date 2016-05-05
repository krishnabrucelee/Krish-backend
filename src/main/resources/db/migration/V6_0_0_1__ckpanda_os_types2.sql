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
-- Table structure for table `os_types`
--
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 6.6 (64-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50bf3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 6.6 (32-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50cf3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 6.7 (32-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50af3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 6.7 (64-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50ff3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 7.0 (32-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50fa3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 7.0 (64-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50fd3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 7.1 (32-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50fe3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 7.1 (64-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50fe3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 7.2 (32-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50fc3b970', 0);
INSERT INTO `ckpanda`.`os_types` (`created_user_id`, `created_date_time`, `description`, `oscategory_id`, `updated_user_id`, `updated_date_time`, `uuid`, `version`) VALUES
(0, '2016-05-05 00:37:19', 'CentOS 7.2 (64-bit)', (SELECT id FROM `ckpanda`.`os_categories` WHERE name='CentOS'), 0, '2016-05-05 00:37:19', '4fdf10aa-0d19-11e6-a2f6-86b50ee3b970', 0);

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-04-19 18:49:56
