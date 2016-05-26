DROP PROCEDURE IF EXISTS `alter_theme_settings`;
DELIMITER $$
CREATE PROCEDURE `alter_theme_settings` ()
BEGIN
    DECLARE columnCount INT DEFAULT 0;
    SELECT count(*) INTO columnCount
       FROM INFORMATION_SCHEMA.COLUMNS
       WHERE table_name = 'theme_setting'
       AND table_schema = 'ckpanda'
       AND column_name = 'welcome_content' OR column_name = 'footer_content';
    IF columnCount <= 0 THEN
        ALTER TABLE `ckpanda`.`theme_setting`
            ADD COLUMN `footer_content` text DEFAULT NULL AFTER `logo_img_path`,
            ADD COLUMN `welcome_content` text DEFAULT NULL AFTER `footer_content`;
    END IF;
    UPDATE `ckpanda`.`theme_setting` SET `background_img_file`='theme_background.jpg', `background_img_path`='/home/Assistanz/theme/images', `created_user_id`=0,`is_active`=1, `logo_img_file`='theme_logo.jpg', `logo_img_path`='/home/Assistanz/theme/images', `footer_content`='<h5 style=" text-align: center;"><span style="color: rgb(206, 198, 206);">Panda Cloud Management Portal</span></h5><h5 style=" text-align: center;"><span style="color: rgb(206, 198, 206);"> Copyright © 2015 BlueTek Co.Ltd, All rights reserved.</span><br></h5>', `welcome_content`='<h4 style="text-align: center;"><span style="color: rgb(206, 198, 206);">Please Login To Panda User Console</span></h4>' WHERE `id`='1';
END