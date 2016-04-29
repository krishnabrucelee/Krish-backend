package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.ThemeCustomisation;
import ck.panda.domain.entity.ThemeCustomisation.CustomType;

/**
 * Theme customisation repository.
 */
public interface ThemeCustomisationRepository extends PagingAndSortingRepository<ThemeCustomisation, Long> {

    /**
     * Find list of theme customisation by is active true.
     *
     * @param themeSettingId theme setting id.
     * @param isActive status of the theme.
     * @return theme setting.
     */
    @Query(value = "SELECT theme FROM ThemeCustomisation theme WHERE theme.isActive = :isActive AND theme.themeSettingId = :themeSettingId")
    List<ThemeCustomisation> findByThemeAndIsActive(@Param("themeSettingId") Long themeSettingId, @Param("isActive") Boolean isActive);

    /**
     * Find theme customisation by is active true.
     *
     * @param id theme setting id.
     * @param isActive status of the theme.
     * @return theme setting.
     */
    @Query(value = "SELECT theme FROM ThemeCustomisation theme WHERE theme.isActive = :isActive AND theme.id = :id")
    ThemeCustomisation findByThemeSettingAndIsActive(@Param("id") Long id, @Param("isActive") Boolean isActive);

    /**
     * Find by custom type.
     *
     * @param customType type of custom
     * @return theme customisation.
     */
    @Query(value = "SELECT theme FROM ThemeCustomisation theme WHERE theme.customType = :customType")
    List<ThemeCustomisation> findByCustomType(@Param("customType") CustomType customType);

    /**
     * Find by custom type and is active.
     *
     * @param customType type of custom
     * @param isActive status of the theme.
     * @return theme customisation.
     */
    @Query(value = "SELECT theme FROM ThemeCustomisation theme WHERE theme.customType = :customType AND theme.isActive = :isActive")
    List<ThemeCustomisation> findByCustomTypeAndIsActive(@Param("customType") CustomType customType, @Param("isActive") Boolean isActive);
}
