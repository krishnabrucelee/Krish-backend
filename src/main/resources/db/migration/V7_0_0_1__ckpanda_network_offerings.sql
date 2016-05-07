ALTER TABLE `ckpanda`.`network_offerings`
ADD COLUMN `for_vpc` bit(1) DEFAULT NULL AFTER `version`;
