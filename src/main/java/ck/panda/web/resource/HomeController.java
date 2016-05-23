package ck.panda.web.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ck.panda.domain.entity.ThemeSetting;
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

    @RequestMapping(value = "/list")
    public ThemeSetting listThemeSetting() throws Exception {
        return themeSettingService.findByThemeAndIsActive(true);
    }
}
