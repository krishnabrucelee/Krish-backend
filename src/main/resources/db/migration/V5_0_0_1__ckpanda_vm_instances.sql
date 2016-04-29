ALTER TABLE `ckpandas5`.`vm_instances`
ADD COLUMN `instance_username` VARCHAR(255) NULL AFTER `zone_id`,
ADD COLUMN `instance_osType` VARCHAR(255) NULL AFTER `instance_username`,
ADD COLUMN `instance_public_ipaddress` BIGINT NULL AFTER `instance_osType`,
ADD COLUMN `Instance_guest_ipaddress` BIGINT NULL AFTER `instance_public_ipaddress`;