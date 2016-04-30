package ck.panda.service;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.ThemeCustomisation;
import ck.panda.domain.entity.ThemeCustomisation.CustomType;
import ck.panda.domain.entity.ThemeSetting;
import ck.panda.domain.repository.jpa.ThemeSettingRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Theme setting service implementation.
 */
@Service
public class ThemeSettingServiceImpl implements ThemeSettingService {

    /** Get Theme Setting repository configuration. */
    @Autowired
    private ThemeSettingRepository themeSettingRepo;

    /** Get Theme Setting repository configuration. */
    @Autowired
    private ThemeCustomisationService themeCustomisationService;

    @Override
    public ThemeSetting save(ThemeSetting theme) throws Exception {
        return themeSettingRepo.save(theme);
    }

    @Override
    public ThemeSetting update(ThemeSetting theme) throws Exception {
        return themeSettingRepo.save(theme);
    }

    @Override
    public void delete(ThemeSetting theme) throws Exception {
        themeSettingRepo.delete(theme);
    }

    @Override
    public void delete(Long id) throws Exception {
        themeSettingRepo.delete(id);
    }

    @Override
    public ThemeSetting find(Long id) throws Exception {
        return themeSettingRepo.findOne(id);
    }

    @Override
    public Page<ThemeSetting> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return themeSettingRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ThemeSetting> findAll() throws Exception {
        return (List<ThemeSetting>) themeSettingRepo.findAll();
    }

    @Override
    public ThemeSetting findByThemeAndIsActive(Boolean isActive) {
        return themeSettingRepo.findByThemeAndIsActive(isActive);
    }

    @Override
    public List<ThemeSetting> findByIsActive(Boolean isActive) {
        List<ThemeSetting> themeList = new ArrayList<ThemeSetting>();
        ThemeSetting theme = themeSettingRepo.findByThemeAndIsActive(true);
        if (theme != null) {
            List<ThemeCustomisation> customHeaderList = themeCustomisationService
                    .findByCustomTypeAndIsActive(CustomType.HEADER, true);
            List<ThemeCustomisation> customFooterList = themeCustomisationService
                    .findByCustomTypeAndIsActive(CustomType.FOOTER, true);
            theme.setHeaders(customHeaderList);
            theme.setFooters(customFooterList);
            themeList.add(theme);
            return themeList;
        } else {
            return null;
        }
    }

    @Override
    public ThemeSetting saveThemeCustomisation(ThemeSetting theme, String headers, String footers) throws Exception {
        JSONArray headerResult = new JSONArray(headers);
        JSONArray footerResult = new JSONArray(footers);
        List<ThemeCustomisation> footerList = new ArrayList<ThemeCustomisation>();
        List<ThemeCustomisation> headerList = new ArrayList<ThemeCustomisation>();
        List<ThemeCustomisation> customFooterList = themeCustomisationService.findByCustomType(CustomType.FOOTER);
        for (ThemeCustomisation customCheck : customFooterList) {
            customCheck.setIsActive(false);
            themeCustomisationService.save(customCheck);
        }
        List<ThemeCustomisation> customHeaderList = themeCustomisationService.findByCustomType(CustomType.HEADER);
        for (ThemeCustomisation customCheck : customHeaderList) {
            customCheck.setIsActive(false);
            themeCustomisationService.save(customCheck);
        }
        for (int j = 0; j < footerResult.length(); j++) {
            if (!footerResult.getJSONObject(j).getString(CloudStackConstants.CS_ID).startsWith("c")) {
                ThemeCustomisation themeCustom = themeCustomisationService
                        .find(Long.valueOf(footerResult.getJSONObject(j).getString(CloudStackConstants.CS_ID)));
                if (themeCustom != null) {
                    if (footerResult.getJSONObject(j).has(CloudStackConstants.CS_NAME)) {
                        themeCustom.setName(footerResult.getJSONObject(j).getString(CloudStackConstants.CS_NAME));
                    }
                    if (footerResult.getJSONObject(j).has(CloudStackConstants.CS_URL)) {
                        themeCustom.setUrl(footerResult.getJSONObject(j).getString(CloudStackConstants.CS_URL));
                    }
                    themeCustom.setIsActive(true);
                    themeCustomisationService.save(themeCustom);
                }
            } else {
                if (footerResult.getJSONObject(j).has(CloudStackConstants.CS_NAME) || (footerResult.getJSONObject(j).has(CloudStackConstants.CS_URL))) {
                    ThemeCustomisation customisation = new ThemeCustomisation();
                    customisation.setIsActive(true);
                    customisation.setThemeSettingId(theme.getId());
                    customisation.setName(footerResult.getJSONObject(j).getString(CloudStackConstants.CS_NAME));
                    customisation.setUrl(footerResult.getJSONObject(j).getString(CloudStackConstants.CS_URL));
                    customisation.setCustomType(CustomType.FOOTER);
                    themeCustomisationService.save(customisation);
                    footerList.add(customisation);
                }
            }
        }

        for (int i = 0; i < headerResult.length(); i++) {
            if (!headerResult.getJSONObject(i).getString(CloudStackConstants.CS_ID).startsWith("c")) {
                ThemeCustomisation themeCustom = themeCustomisationService
                        .find(Long.valueOf(headerResult.getJSONObject(i).getString(CloudStackConstants.CS_ID)));
                if (themeCustom != null) {
                    if (headerResult.getJSONObject(i).has(CloudStackConstants.CS_NAME)) {
                        themeCustom.setName(headerResult.getJSONObject(i).getString(CloudStackConstants.CS_NAME));
                    }
                    if (headerResult.getJSONObject(i).has(CloudStackConstants.CS_URL)) {
                        themeCustom.setUrl(headerResult.getJSONObject(i).getString(CloudStackConstants.CS_URL));
                    }
                    themeCustom.setIsActive(true);
                    themeCustomisationService.save(themeCustom);
                }
            } else if (headerResult.getJSONObject(i).has(CloudStackConstants.CS_NAME) || (headerResult.getJSONObject(i).has(CloudStackConstants.CS_URL))) {
                ThemeCustomisation customisation = new ThemeCustomisation();
                customisation.setIsActive(true);
                customisation.setThemeSettingId(theme.getId());
                customisation.setName(headerResult.getJSONObject(i).getString(CloudStackConstants.CS_NAME));
                customisation.setUrl(headerResult.getJSONObject(i).getString(CloudStackConstants.CS_URL));
                customisation.setCustomType(CustomType.HEADER);
                themeCustomisationService.save(customisation);
                headerList.add(customisation);
            }
        }
        List<ThemeSetting> themeList = themeSettingRepo.findByIsActive(true);
        List<ThemeCustomisation> footer = new ArrayList<ThemeCustomisation>();
        List<ThemeCustomisation> header = new ArrayList<ThemeCustomisation>();
        for (ThemeSetting themeSetting : themeList) {
            ThemeSetting persistTheme = find(themeSetting.getId());
            footer = persistTheme.getFooters();
            header = persistTheme.getHeaders();
            footer.addAll(footerList);
            header.addAll(headerList);
            persistTheme.setFooters(footer);
            persistTheme.setHeaders(header);
            update(persistTheme);
        }
        save(theme);
        return theme;
    }
}
