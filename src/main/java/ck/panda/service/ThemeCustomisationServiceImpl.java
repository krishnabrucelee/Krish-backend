package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ThemeCustomisation;
import ck.panda.domain.entity.ThemeCustomisation.CustomType;
import ck.panda.domain.repository.jpa.ThemeCustomisationRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

@Service
public class ThemeCustomisationServiceImpl implements ThemeCustomisationService {

    /**
     * Theme customisation repository reference.
     */
    @Autowired
    private ThemeCustomisationRepository themeCustomisationRepo;

    @Override
    public ThemeCustomisation save(ThemeCustomisation themeCustom) throws Exception {
        return themeCustomisationRepo.save(themeCustom);
    }

    @Override
    public ThemeCustomisation update(ThemeCustomisation themeCustom) throws Exception {
        return themeCustomisationRepo.save(themeCustom);
    }

    @Override
    public void delete(ThemeCustomisation themeCustom) throws Exception {
        themeCustomisationRepo.delete(themeCustom);
    }

    @Override
    public void delete(Long id) throws Exception {
        themeCustomisationRepo.delete(id);
    }

    @Override
    public ThemeCustomisation find(Long id) throws Exception {
        return themeCustomisationRepo.findOne(id);
    }

    @Override
    public Page<ThemeCustomisation> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return themeCustomisationRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ThemeCustomisation> findAll() throws Exception {
        return (List<ThemeCustomisation>) themeCustomisationRepo.findAll();
    }

    @Override
    public List<ThemeCustomisation> findByThemeAndIsActive(Long id, Boolean isActive) {
        return themeCustomisationRepo.findByThemeAndIsActive(id, isActive);
    }

    @Override
    public ThemeCustomisation findByThemeSettingAndIsActive(Long id, Boolean isActive) {
        return themeCustomisationRepo.findByThemeSettingAndIsActive(id, isActive);
    }

    @Override
    public List<ThemeCustomisation> findByCustomType(CustomType type) {
        return themeCustomisationRepo.findByCustomType(type);
    }

    @Override
    public List<ThemeCustomisation> findByCustomTypeAndIsActive(CustomType type, Boolean isActive) {
        return themeCustomisationRepo.findByCustomTypeAndIsActive(type, isActive);
    }

}
