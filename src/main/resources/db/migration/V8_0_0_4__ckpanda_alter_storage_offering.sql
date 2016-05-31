ALTER TABLE `ckpanda`.`storage_offerings`
    ADD COLUMN `provisioning_type` VARCHAR(45) NULL DEFAULT NULL AFTER `version`;