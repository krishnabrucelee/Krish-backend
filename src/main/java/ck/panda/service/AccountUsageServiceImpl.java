package ck.panda.service;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.AccountUsage;
import ck.panda.domain.entity.Department;
import ck.panda.domain.repository.jpa.AccountUsageRepository;
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
public class AccountUsageServiceImpl implements AccountUsageService {

    @Autowired
    private UserService userService;


    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private AccountUsageRepository accountUsageRepo;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountUsageServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private AccountUsageRepository usageRepo;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackUsageService csAccountUsageService;

    @Override
    public AccountUsage save(AccountUsage accountUsage) throws Exception {
//		Errors errors = validator.rejectIfNullEntity("usage", accountUsage);
//        errors = validator.validateEntity(accountUsage, errors);
//
//        if (errors.hasErrors()) {
//            throw new ApplicationException(errors);
//        } else {
              HashMap<String, String> usageMap = new HashMap<String, String>();
              csAccountUsageService.setServer(configServer.setServer(1L));
              String jsonresponse = csAccountUsageService.listUsageRecords("2015-10-29 23:59:59","2015-10-29 12:15:31", "json",usageMap );
              System.out.println(jsonresponse);
//        }
        return usageRepo.save(accountUsage);
    }

    @Override
    public AccountUsage update(AccountUsage usage) throws Exception {
        return usageRepo.save(usage);
    }

    @Override
    public void delete(AccountUsage usage) throws Exception {
        usageRepo.delete(usage);

    }

    @Override
    public void delete(Long id) throws Exception {
        usageRepo.delete(id);

    }

    @Override
    public AccountUsage find(Long id) throws Exception {
        AccountUsage usage = usageRepo.findOne(id);
        return usage;

    }

    @Override
    public Page<AccountUsage> findAll(PagingAndSorting pagingAndSorting) throws Exception {
         return usageRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<AccountUsage> findAll() throws Exception {
        return (List<AccountUsage>) usageRepo.findAll();

    }

    public List<AccountUsage> updateUsageAccount() throws Exception {
        // Step1: Get the list of accounts from cloudstack.
        List<Department>  departmentList =  departmentService.findByAll();
        for(Department department: departmentList) {
            this.getUsageRecordsByAccount(department);
        }
        //Step2: Iterate the list of usage types
        return null;
    }


    private void getUsageRecordsByAccount(Department department) throws Exception {
         csAccountUsageService.setServer(configServer.setServer(1L));
         String jsonresponse = csAccountUsageService.listUsageTypes("json");
         JSONArray usageTypes = null;
         JSONObject responseObject = new JSONObject(jsonresponse).getJSONObject("listusagetypesresponse");
         if (responseObject.has("usagetype")) {
             usageTypes = responseObject.getJSONArray("usagetype");
             for (int i = 0, size = usageTypes.length(); i < size; i++) {

                 HashMap<String, String> usageMap = new HashMap<String, String>();
                 usageMap.put("account", department.getUuid());
                 usageMap.put("domainid", department.getDomainId().toString());
                 usageMap.put("type", usageTypes.getJSONObject(i).get("usagetypeid").toString());
                csAccountUsageService.setServer(configServer.setServer(1L));
                String usageResponse = csAccountUsageService.listUsageRecords("2015-10-29 23:59:59","2015-10-29 12:15:31", "json",usageMap );
                this.upateUsageRecrods(department, usageResponse);
             }
         }
    }

    private void upateUsageRecrods(Department department, String usageResponse) throws Exception {
        JSONObject usageRecordsResponse = new JSONObject(usageResponse).getJSONObject("listusagerecordsresponse");
        JSONArray usageResponseList = null;
        if (usageRecordsResponse.has("usagerecord")) {
            usageResponseList = usageRecordsResponse.getJSONArray("usagerecord");
            for (int i = 0, size = usageResponseList.length(); i < size; i++) {
                accountUsageRepo.findByUsageIdAndOfferingId(usageResponseList.getJSONObject(i).get("usageid").toString(), usageResponseList.getJSONObject(i).get("offeringid").toString(), "2015-10-29 23:59:59","2015-10-29 12:15:31");
            }
        }

    }

}
