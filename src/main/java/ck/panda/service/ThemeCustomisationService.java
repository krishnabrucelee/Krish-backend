package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ThemeCustomisation;
import ck.panda.domain.entity.ThemeCustomisation.CustomType;
import ck.panda.util.domain.CRUDService;

/**
 * Theme customisation service.
 */
@Service
public interface ThemeCustomisationService extends CRUDService<ThemeCustomisation> {

    /**
     * Find list of theme customisation by is active true.
     *
     * @param id theme setting id.
     * @param isActive status of the theme.
     * @return theme setting.
     */
    List<ThemeCustomisation> findByThemeAndIsActive(Long id, Boolean isActive);

    /**
     * Find theme customisation by is active true.
     *
     * @param id theme setting id.
     * @param isActive status of the theme.
     * @return theme setting.
     */
    ThemeCustomisation findByThemeSettingAndIsActive(Long id, Boolean isActive);

    /**
     * Find by custom type.
     *
     * @param customType type of custom
     * @return theme customisation.
     */
    List<ThemeCustomisation> findByCustomType(CustomType type);

    /**
     * Find by custom type and is active.
     *
     * @param customType type of custom
     * @param isActive status of the theme.
     * @return theme customisation.
     */
    List<ThemeCustomisation> findByCustomTypeAndIsActive(CustomType type, Boolean isActive);

}
