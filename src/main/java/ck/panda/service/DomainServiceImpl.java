package ck.panda.service;

import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAccountService;
import ck.panda.util.CloudStackDomainService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Domain service implementation class.
 *
 */
@Service
public class DomainServiceImpl implements DomainService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private DomainRepository domainRepo;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackDomainService domainService;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Domain repository reference. */
    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    /** Department service.*/
    private CloudStackAccountService departmentService;

    @Autowired
    private DepartmentService deptService;

    /** Secret key for the user encryption. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    @Override
    public Domain save(Domain domain) throws Exception {
        Domain persistedDomain = null;
         if (domain.getSyncFlag()) {
          this.validateDomain(domain);
         Errors errors = validator.rejectIfNullEntity("domain", domain);
         errors = validator.validateEntity(domain, errors);
         if (errors.hasErrors()) {
             throw new ApplicationException(errors);
         } else {
             // set server for maintain session with configuration values
            domainService.setServer(configServer.setServer(1L));
            HashMap<String,String> optional = new HashMap<String, String>();
            String domainResponse = domainService.createDomain(domain.getName(), "json", optional);
            JSONObject createDomainResponseJSON = new JSONObject(domainResponse).getJSONObject("createdomainresponse");
            if (createDomainResponseJSON.has("errorcode")) {
                errors = this.validateEvent(errors, createDomainResponseJSON.getString("errortext"));
                throw new ApplicationException(errors);
            }
            JSONObject createDomain = createDomainResponseJSON.getJSONObject("domain");
            domain.setUuid((String) createDomain.get("id"));
            domain.setIsActive(true);
            persistedDomain = domainRepo.save(domain);
            LOGGER.debug("Company created : "+ domain.getName());
            Department department = this.createDomainAdmin(persistedDomain);
            persistedDomain.setHodId(department.getId());
            LOGGER.debug("Company upated with HOD : "+ persistedDomain.getHodId());
         }
        }
         persistedDomain = domainRepo.save(domain);
     return persistedDomain;
   }


    private Department createDomainAdmin(Domain persistedDomain) throws Exception {
        Errors errors = validator.rejectIfNullEntity("domain", persistedDomain);
        errors = validator.validateEntity(persistedDomain, errors);
        HashMap<String,String> optional = new HashMap<String, String>();
        Department department = new Department();
        String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
        byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        String encryptedPassword = new String(EncryptionUtil.encrypt(persistedDomain.getPassword(), originalKey));

        department.setDomain(persistedDomain);
        department.setFirstName(persistedDomain.getPrimaryFirstName());
        department.setLastName(persistedDomain.getLastName());
        department.setUserName(persistedDomain.getName());
        department.setEmail(persistedDomain.getEmail());
        department.setPassword(encryptedPassword);
        department.setType(Department.AccountType.DOMAIN_ADMIN);
        department.setIsActive(true);
        optional.put("domainid", String.valueOf(persistedDomain.getUuid()));
        String accountresponse = departmentService.createAccount(String.valueOf(department.getType().ordinal()),department.getEmail(), department.getFirstName(), department.getLastName(), department.getUserName(), department.getPassword(), "json", optional);
        JSONObject createAccountResponseJSON = new JSONObject(accountresponse)
                .getJSONObject("createaccountresponse");
        if (createAccountResponseJSON.has("errorcode")) {
            errors = this.validateEvent(errors, createAccountResponseJSON.getString("errortext"));
            throw new ApplicationException(errors);
        }
        JSONObject createDomain = createAccountResponseJSON.getJSONObject("account");
        department.setUuid((String) createDomain.get("id"));
        department.setSyncFlag(false);
        department = deptService.save(department);
        LOGGER.debug("Department created : "+ department.getUserName());
        return department;
    }

    @Override
    public Domain update(Domain domain) throws Exception {
        LOGGER.debug(domain.getUuid());
        if (domain.getSyncFlag()) {
            this.validateDomain(domain);
           Errors errors = validator.rejectIfNullEntity("domain", domain);
           errors = validator.validateEntity(domain, errors);
           if (errors.hasErrors()) {
               throw new ApplicationException(errors);
           } else {
            HashMap<String, String> domainMap = new HashMap<String, String>();
            domainMap.put("name", domain.getName());
            String updateDomainResponse = domainService.updateDomain(domain.getUuid(), "json", domainMap);
            JSONObject updateDomainResponseJSON = new JSONObject( updateDomainResponse).getJSONObject("updatedomainresponse");
            if (updateDomainResponseJSON.has("errorcode")) {
                 errors = this.validateEvent(errors, updateDomainResponseJSON.getString("errortext"));
                 throw new ApplicationException(errors);
             }
            JSONObject updateDomain = updateDomainResponseJSON.getJSONObject("domain");
            domain.setName((String) updateDomain.get("name"));
             }
           }
        return domainRepo.save(domain);
    }

    @Override
    public void delete(Domain domain) throws Exception {
        domainRepo.delete(domain);
    }

    @Override
    public void delete(Long id) throws Exception {
        domainRepo.delete(id);
    }

    @Override
    public Domain find(Long id) throws Exception {
        Domain domain = domainRepo.findOne(id);
        return domain;
    }

    @Override
    public List<Domain> findAllDomain() throws Exception {
        return (List<Domain>) domainRepo.findAll();
    }

    @Override
    public Page<Domain> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return domainRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Domain> findAll() throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && domain.getName().equals("ROOT")) {
            return (List<Domain>) domainRepo.findAll();
        }
        List<Domain> domains = new ArrayList<Domain>();
        domains.add(deptService.find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getDomain());
        return domains;
    }

    @Override
    public Domain findbyUUID(String uuid) throws Exception {
        return domainRepo.findByUUID(uuid);
    }

    @Override
    public Domain softDelete(Domain domain) throws Exception {
        Errors errors = validator.rejectIfNullEntity("domain", domain);
        domainService.setServer(configServer.setServer(1L));
        List<Department> departmentedit =  deptService.findDomain(domain.getId());
        System.out.println(departmentedit);
        if (departmentedit.size() != 0) {
            errors.addGlobalError("Cannot delete domain. Please make sure all users and sub domains have been removed from the domain before deleting");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        else {
            domain.setIsActive(false);
             domain.setStatus(Domain.Status.INACTIVE);
             String deleteResponse = domainService.deleteDomain(domain.getUuid(), "json");
           JSONObject deleteJobId = new JSONObject(deleteResponse).getJSONObject("deletedomainresponse");
        }
            return domainRepo.save(domain);
    }
    @Override
    public List<Domain> findAllFromCSServer() throws Exception {
        List<Domain> domainList = new ArrayList<Domain>();
        HashMap<String, String> domainMap = new HashMap<String, String>();
        domainMap.put("listall", "true");
        // 1. Get the list of domains from CS server using CS connector
        String response = domainService.listDomains("json", domainMap);

        JSONArray domainListJSON = new JSONObject(response).getJSONObject("listdomainsresponse").getJSONArray("domain");
        // 2. Iterate the json list, convert the single json entity to domain
        for (int i = 0, size = domainListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to Domain entity and Add
            // the converted Domain entity to list
            domainList.add(Domain.convert(domainListJSON.getJSONObject(i)));
        }
        return domainList;
    }

    /**
    * Validate the compute.
    *
    * @param domain object
    *            reference of the domain.
    * @throws Exception
    *             error occurs
    */
    private void validateDomain(Domain domain) throws Exception {
        Errors errors = validator.rejectIfNullEntity("domain", domain);
        errors = validator.validateEntity(domain, errors);
        Domain domaintest = domainRepo.findByName((domain.getName()));
        if (domaintest != null && domain.getId() != domaintest.getId()) {
            errors.addFieldError("name", "domain.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    /**
     * Check the domain CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
       errors.addGlobalError(errmessage);
       return errors;
    }

    /**
     * Find all the domain with pagination.
     *
     * @throws Exception
     *             application errors.
     * @param pagingAndSorting
     *            do pagination with sorting for domains.
     * @return list of domains.
     */
    public Page<Domain> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return domainRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }
}
