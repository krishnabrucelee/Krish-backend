package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.ThemeSetting;

/**
 * Theme setting repository.
 */
public interface ThemeSettingRepository extends PagingAndSortingRepository<ThemeSetting, Long> {

    /**
     * Find theme setting by is active true.
     *
     * @param isActive status of the theme.
     * @return theme setting.
     */
    @Query(value = "SELECT theme FROM ThemeSetting theme WHERE theme.isActive = :isActive ")
    ThemeSetting findByThemeAndIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find List of theme setting by is active true.
     *
     * @param isActive status of the theme.
     * @return theme setting.
     */
    @Query(value = "SELECT theme FROM ThemeSetting theme WHERE theme.isActive = :isActive")
    List<ThemeSetting> findByIsActive(@Param("isActive") Boolean isActive);

}
