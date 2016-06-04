package ck.panda.web.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.domain.entity.ThemeSetting;
import ck.panda.service.GeneralConfigurationService;
import ck.panda.service.ThemeSettingService;

/** Home controller for theme setting */
@RestController
@RequestMapping("/home")
public class HomeController {

    /**
     * Theme setting service reference.
     */
    @Autowired
    private ThemeSettingService themeSettingService;

    /**
     * General configuration service reference.
     */
    @Autowired
    private GeneralConfigurationService generalConfigurationService;

    /**
     * Get the home page theme settings.
     *
     * @return theme settings
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/list")
    public ThemeSetting listThemeSetting() throws Exception {
        return themeSettingService.findByThemeAndIsActive(true);
    }

    /**
     * Get the general configuration.
     * @return general settings
     * @throws Exception raise if error
     */
    @RequestMapping(value = "/generalConfiguration")
    public GeneralConfiguration generalConfigurationSetting() throws Exception {
        return generalConfigurationService.findByIsActive(true);
    }
}
