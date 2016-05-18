ALTER TABLE `ckpanda`.`vpc_acl`
ADD COLUMN `is_active` bigint(20) DEFAULT NULL AFTER `version`,
ADD COLUMN `vpc_id` bigint(20) DEFAULT NULL AFTER `is_active`;