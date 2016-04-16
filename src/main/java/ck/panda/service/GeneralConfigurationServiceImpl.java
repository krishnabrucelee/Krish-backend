package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.domain.repository.jpa.GeneralConfigurationRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/** General configuration Service implementation class. */
@Service
public class GeneralConfigurationServiceImpl implements GeneralConfigurationService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** General configuration repository reference. */
    @Autowired
    private GeneralConfigurationRepository generalConfigurationRepository;

    /** General configuration string literal. */
    public static final String GENERAL_CONFIGURATION = "generalConfiguration";

    @Override
    public GeneralConfiguration save(GeneralConfiguration generalConfiguration) throws Exception {
        Errors errors = validator.rejectIfNullEntity(GENERAL_CONFIGURATION, generalConfiguration);
        errors = validator.validateEntity(generalConfiguration, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            generalConfiguration.setIsActive(true);
            return generalConfigurationRepository.save(generalConfiguration);
        }
    }

    @Override
    public GeneralConfiguration update(GeneralConfiguration generalConfiguration) throws Exception {
        Errors errors = validator.rejectIfNullEntity(GENERAL_CONFIGURATION, generalConfiguration);
        errors = validator.validateEntity(generalConfiguration, errors);
        return generalConfigurationRepository.save(generalConfiguration);
    }

    @Override
    public void delete(GeneralConfiguration generalConfiguration) throws Exception {
        generalConfigurationRepository.delete(generalConfiguration);
    }

    @Override
    public void delete(Long id) throws Exception {
        generalConfigurationRepository.delete(id);
    }

    @Override
    public Page<GeneralConfiguration> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return generalConfigurationRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<GeneralConfiguration> findAll() throws Exception {
        return (List<GeneralConfiguration>) generalConfigurationRepository.findAll();
    }

    @Override
    public GeneralConfiguration find(Long id) throws Exception {
        return generalConfigurationRepository.findOne(id);
    }

    @Override
    public GeneralConfiguration findByIsActive(Boolean isActive) throws Exception {
        return generalConfigurationRepository.findByIsActive(true);
    }
}
