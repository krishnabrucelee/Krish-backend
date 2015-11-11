package ck.panda.service;

import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Usage;
import ck.panda.domain.repository.jpa.UsageRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackUsageService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Usage service implementation class.
 */
@Service
public class UsageServiceImpl implements UsageService {

    @Autowired
    private UserService userService;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UsageServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private UsageRepository usageRepo;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackUsageService usageService;

    @Override
    public Usage save(Usage accountUsage) throws Exception {
//		Errors errors = validator.rejectIfNullEntity("usage", accountUsage);
//        errors = validator.validateEntity(accountUsage, errors);
//
//        if (errors.hasErrors()) {
//            throw new ApplicationException(errors);
//        } else {
              HashMap<String, String> usageMap = new HashMap<String, String>();
              usageService.setServer(configServer.setServer(1L));
              String jsonresponse = usageService.listUsageRecords("2015-10-29 23:59:59","2015-10-29 12:15:31", "json",usageMap );
              System.out.println(jsonresponse);
//        }
        return usageRepo.save(accountUsage);
    }

    @Override
    public Usage update(Usage usage) throws Exception {
        return usageRepo.save(usage);
    }

    @Override
    public void delete(Usage usage) throws Exception {
        usageRepo.delete(usage);

    }

    @Override
    public void delete(Long id) throws Exception {
        usageRepo.delete(id);

    }

    @Override
    public Usage find(Long id) throws Exception {
        Usage usage = usageRepo.findOne(id);
        return usage;

    }

    @Override
    public Page<Usage> findAll(PagingAndSorting pagingAndSorting) throws Exception {
         return usageRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Usage> findAll() throws Exception {
        return (List<Usage>) usageRepo.findAll();

    }


}
