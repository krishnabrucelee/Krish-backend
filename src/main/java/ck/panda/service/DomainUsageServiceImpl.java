package ck.panda.service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.DomainUsage;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Invoice;
import ck.panda.domain.repository.mongo.DomainUsageRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackUsageService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.ConvertUtil;
import ck.panda.util.JsonUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Usage service implementation class.
 */
@EnableScheduling
@Service
public class DomainUsageServiceImpl implements DomainUsageService {

    @Autowired
    private UserService userService;


    @Autowired
    private DepartmentService departmentService;


    @Autowired
    private DomainService domainService;

    @Autowired
    private DomainUsageRepository domainUsageRepo;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainUsageServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private DomainUsageRepository usageRepo;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    @Autowired
    private ConvertUtil convertUtil;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackUsageService csAccountUsageService;

    @Override
    public DomainUsage save(DomainUsage domainUsage) throws Exception {
        return usageRepo.save(domainUsage);
    }

    @Override
    public DomainUsage update(DomainUsage usage) throws Exception {
        return usageRepo.save(usage);
    }

    @Override
    public void delete(DomainUsage usage) throws Exception {
        usageRepo.delete(usage);
    }

    @Override
    public void delete(Long id) throws Exception {
        //usageRepo.delete(id);
    }

    @Override
    public DomainUsage find(Long id) throws Exception {
        return null;

    }

    @Override
    public Page<DomainUsage> findAll(PagingAndSorting pagingAndSorting) throws Exception {
         return usageRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<DomainUsage> findAll() throws Exception {
        return (List<DomainUsage>) usageRepo.findAll();

    }


    public void updateDomainUsage() throws Exception {
        // Step1: Get the list of accounts from cloudstack.
        // Step2: Get the list of usage types
        // Step3: Get the usage records for each domain.
        // Step4: Check the usage already exist with our database by offering and usage.
        // Step4.1: If already exist then update the usage details.
        // Step4.2: Else add the new usage records

    }





}
