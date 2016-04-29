package ck.panda.service;

import java.util.List;

import org.json.JSONException;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ThemeSetting;
import ck.panda.util.domain.CRUDService;

/**
 * Theme setting service.
 */
@Service
public interface ThemeSettingService  extends CRUDService<ThemeSetting>{

    /**
     * Find theme setting and is active.
     * @param isActive true or false
     * @return theme setting
     */
    ThemeSetting findByThemeAndIsActive(Boolean isActive);

    /**
     * Find list of theme setting and is active.
     * @param isActive true or false
     * @return theme setting
     */
    List<ThemeSetting> findByIsActive(Boolean isActive);

    /**
     * Save theme customisation and theme setting.
     * @param theme theme setting
     * @param headers header
     * @param footers footer
     * @return theme customisation
     * @throws JSONException json error
     * @throws Exception exception
     */
    ThemeSetting saveThemeCustomisation(ThemeSetting theme, String headers, String footers) throws JSONException, Exception;

}
