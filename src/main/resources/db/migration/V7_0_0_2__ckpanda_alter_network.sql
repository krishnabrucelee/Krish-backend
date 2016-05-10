ALTER TABLE `ckpanda`.`networks`
ADD COLUMN `acl_id` bigint(20) DEFAULT NULL AFTER `id`;