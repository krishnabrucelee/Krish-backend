ALTER TABLE `ckpanda`.`theme_setting`
            ADD COLUMN `welcome_content_user` text DEFAULT NULL AFTER `welcome_content`,
            ADD COLUMN `splash_title` text DEFAULT NULL AFTER `welcome_content_user`,
            ADD COLUMN `splash_title_user` text DEFAULT NULL AFTER `splash_title`;