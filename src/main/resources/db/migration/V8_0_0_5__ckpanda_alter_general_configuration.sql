ALTER TABLE `ckpanda`.`general_configuration`
    ADD COLUMN `default_language` VARCHAR(45) NULL DEFAULT NULL AFTER `session_time`;