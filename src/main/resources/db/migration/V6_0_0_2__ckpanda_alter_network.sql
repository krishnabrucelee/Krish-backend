ALTER TABLE `ckpanda`.`networks`
ADD COLUMN `network_creation_type` varchar(255) DEFAULT NULL AFTER `zone_id`,
ADD COLUMN `vpc_id` bigint(20) DEFAULT NULL AFTER `network_creation_type`;