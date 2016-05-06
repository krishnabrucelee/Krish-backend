ALTER TABLE `ckpanda`.`ip_addresses`
ADD COLUMN `vpc_id` bigint(20) DEFAULT NULL AFTER `zone_id`;