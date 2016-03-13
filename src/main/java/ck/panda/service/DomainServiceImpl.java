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

import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Domain.Status;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAccountService;
import ck.panda.util.CloudStackDomainService;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.EncryptionUtil;
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

    /** Autowired CloudStackUserService object. */
    @Autowired
    private CloudStackUserService csUserService;

    /** Autowired permission service. */
    @Autowired
    private PermissionService permissionService;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Reference of Cloud Stack Department service. */
    @Autowired
    private CloudStackAccountService csAccountService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Reference of the department entity service. */
    @Autowired
    private DepartmentService deptService;

    /** Reference of the User entity service. */
    @Autowired
    private UserService userService;

    /** Autowired roleService. */
    @Autowired
    private RoleService roleService;

    /** Secret key for the user encryption. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

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
                configServer.setServer(1L);
                HashMap<String, String> optional = new HashMap<String, String>();
                String domainResponse = domainService.createDomain(domain.getCompanyNameAbbreviation(), "json",
                        optional);
                JSONObject createDomainResponseJSON = new JSONObject(domainResponse)
                        .getJSONObject("createdomainresponse");
                if (createDomainResponseJSON.has("errorcode")) {
                    errors = this.validateEvent(errors, createDomainResponseJSON.getString("errortext"));
                    throw new ApplicationException(errors);
                }
                String cityHeadquarter = domain.getCityHeadquarter();
                String companyAddress = domain.getCompanyAddress();
                String email = domain.getEmail();
                String name = domain.getName();
                String lastName = domain.getLastName();
                String password = domain.getPassword();
                String phone = domain.getPhone();
                String portalUserName = domain.getPortalUserName();
                String primaryFirstName = domain.getPrimaryFirstName();
                String secondaryContactEmail = domain.getSecondaryContactEmail();
                String secondaryContactName = domain.getSecondaryContactName();
                String secondaryContactLastName = domain.getSecondaryContactLastName();
                String secondaryContactPhone = domain.getSecondaryContactPhone();

                JSONObject createDomain = createDomainResponseJSON.getJSONObject("domain");
                if (domainRepo.findByUUID(createDomain.getString("id")) != null) {
                    domain = domainRepo.findByUUID(createDomain.getString("id"));
                }
                domain.setUuid(createDomain.getString("id"));
                domain.setIsActive(true);
                domain.setStatus(Status.ACTIVE);
                domain.setCityHeadquarter(cityHeadquarter);
                domain.setCompanyAddress(companyAddress);
                domain.setEmail(email);
                domain.setName(name);
                domain.setLastName(lastName);
                domain.setPassword(password);
                domain.setPhone(phone);
                domain.setPortalUserName(portalUserName);
                domain.setPrimaryFirstName(primaryFirstName);
                domain.setSecondaryContactEmail(secondaryContactEmail);
                domain.setSecondaryContactName(secondaryContactName);
                domain.setSecondaryContactLastName(secondaryContactLastName);
                domain.setSecondaryContactPhone(secondaryContactPhone);

                String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
                byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                String encryptedPassword = new String(EncryptionUtil.encrypt(password, originalKey));
                domain.setPassword(encryptedPassword);
                persistedDomain = domainRepo.save(domain);
                LOGGER.debug("Company created : " + domain.getName());
                Department department = this.createDomainAdmin(persistedDomain, password);
            }
        }
        persistedDomain = domainRepo.save(domain);
        return persistedDomain;
    }

    /**
     * Find department.
     *
     * @param persistedDomain the domain.
     * @param password for user portal.
     * @return department list.
     * @throws Exception unhandled errors.
     */
    private Department createDomainAdmin(Domain persistedDomain, String password) throws Exception {
        Errors errors = validator.rejectIfNullEntity("domain", persistedDomain);
        errors = validator.validateEntity(persistedDomain, errors);
        HashMap<String, String> optional = new HashMap<String, String>();
        Department department = new Department();
        department.setDomainId(persistedDomain.getId());
        department.setUserName(persistedDomain.getPortalUserName());
        department.setDescription("HOD for this company " + persistedDomain.getName());
        department.setType(Department.AccountType.DOMAIN_ADMIN);
        department.setIsActive(true);
        optional.put("domainid", String.valueOf(persistedDomain.getUuid()));
        String accountresponse = csAccountService.createAccount(String.valueOf(department.getType().ordinal()),
                persistedDomain.getEmail(), persistedDomain.getPrimaryFirstName(), persistedDomain.getLastName(),
                department.getUserName(), persistedDomain.getPassword(), "json", optional);
        JSONObject createAccountResponseJSON = new JSONObject(accountresponse).getJSONObject("createaccountresponse");
        if (createAccountResponseJSON.has("errorcode")) {
            errors = this.validateEvent(errors, createAccountResponseJSON.getString("errortext"));
            throw new ApplicationException(errors);
        }
        JSONObject createDomain = createAccountResponseJSON.getJSONObject("account");
        department.setUuid((String) createDomain.get("id"));
        department.setSyncFlag(false);
        department = deptService.save(department);
        User user = User.convert(createDomain.getJSONArray("user").getJSONObject(0));
        user.setDepartmentId(convertEntityService.getDepartmentId(user.getTransDepartment()));
        user.setDomainId(convertEntityService.getDomainId(user.getTransDomainId()));
        user.setPassword(persistedDomain.getPassword());
        User updatedUser = userService.save(user);
        this.syncUpdateUserRole(updatedUser);
        optional.clear();
        optional.put("password", password);
        String userresponse = csUserService.updateUser(user.getUuid(), optional, "json");
        JSONObject updateUserJSON = new JSONObject(userresponse).getJSONObject("updateuserresponse");
        LOGGER.debug("Department created : " + department.getUserName());
        return department;
    }

    /**
     * Update user role.
     *
     * @param userObj to set
     */
    public void syncUpdateUserRole(User userObj) {
        List<UserType> types = new ArrayList<UserType>();
        types.add(UserType.DOMAIN_ADMIN);
        try {
            Role newRole = new Role();
            newRole.setName("FULL_PERMISSION");
            newRole.setDepartmentId(userObj.getDepartmentId());
            newRole.setDescription("Allow full permission");
            newRole.setStatus(Role.Status.ENABLED);
            newRole.setPermissionList(permissionService.findAll());
            Role updatedRole = roleService.save(newRole);
            userObj.setRoleId(updatedRole.getId());
            userService.update(userObj);
        } catch (Exception e) {
            LOGGER.debug("syncUpdateUserRole" + e);
        }
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
                domainMap.put("name", domain.getCompanyNameAbbreviation());
                // set server for maintain session with configuration values
                configServer.setServer(1L);
                String updateDomainResponse = domainService.updateDomain(domain.getUuid(), "json", domainMap);
                JSONObject updateDomainResponseJSON = new JSONObject(updateDomainResponse)
                        .getJSONObject("updatedomainresponse");
                if (updateDomainResponseJSON.has("errorcode")) {
                    errors = this.validateEvent(errors, updateDomainResponseJSON.getString("errortext"));
                    throw new ApplicationException(errors);
                }
                JSONObject updateDomain = updateDomainResponseJSON.getJSONObject("domain");
                domain.setCompanyNameAbbreviation((String) updateDomain.get("name"));
                String cityHeadquarter = domain.getCityHeadquarter();
                String companyAddress = domain.getCompanyAddress();
                String name = domain.getName();
                String secondaryContactEmail = domain.getSecondaryContactEmail();
                String secondaryContactName = domain.getSecondaryContactName();
                String secondaryContactLastName = domain.getSecondaryContactLastName();
                String secondaryContactPhone = domain.getSecondaryContactPhone();
                domain.setCityHeadquarter(cityHeadquarter);
                domain.setCompanyAddress(companyAddress);
                domain.setName(name);
                domain.setSecondaryContactEmail(secondaryContactEmail);
                domain.setSecondaryContactName(secondaryContactName);
                domain.setSecondaryContactLastName(secondaryContactLastName);
                domain.setSecondaryContactPhone(secondaryContactPhone);
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
        Domain domain = domainRepo.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && domain.getName().equals("ROOT")) {
            return (List<Domain>) domainRepo.findAllByDomainAndIsActive(true);
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
        if (domain.getSyncFlag()) {
            // checking whether department belong to that domain has resource.
            List<Department> department = deptService.findAllByDomainAndIsActive(domain.getId(), true);
            if (department.size() != 0) {
                errors.addGlobalError(
                        "error.domain.delete.confirmation");
            }
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            domain.setIsActive(false);
            domain.setStatus(Domain.Status.INACTIVE);
            if (domain.getSyncFlag()) {
                configServer.setServer(1L);
                // Deleting default department with its user while before deleting a domain.
                List<Department> departmentList = deptService.findAllByDomainAccountTypeAndIsActive(domain.getId(), true, AccountType.DOMAIN_ADMIN);
                for (Department department : departmentList) {
                    String departmentResponse = csAccountService.deleteAccount(department.getUuid(), CloudStackConstants.JSON);
                    JSONObject jobId = new JSONObject(departmentResponse).getJSONObject(CloudStackConstants.CS_DELETE_ACCOUNT_RESPONSE);
                    if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                        String jobResponse = csAccountService.accountJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                        JSONObject jobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    }
                    List<User> userList = userService.findAllByDomainDepartmentIdUserTypeAndIsActive(domain.getId(), true,department.getId(), UserType.DOMAIN_ADMIN);
                    for (User user : userList) {
                        user.setIsActive(false);
                        user.setStatus(User.Status.DELETED);
                        configServer.setServer(1L);
                        csUserService.deleteUser((user.getUuid()), CloudStackConstants.JSON);
                        user.setSyncFlag(false);
                        userService.save(user);
                    }
                    department.setIsActive(false);
                    department.setStatus(Department.Status.DELETED);
                    department.setSyncFlag(false);
                    deptService.save(department);
               }
                // After deleting all the resources going to delete domain.
                String deleteResponse = domainService.deleteDomain(domain.getUuid(),CloudStackConstants.JSON);
                JSONObject deleteJobId = new JSONObject(deleteResponse).getJSONObject("deletedomainresponse");
                domain.setIsActive(false);
                domain.setSyncFlag(false);
                domain.setStatus(Status.INACTIVE);
            }
        }
        return domainRepo.save(domain);
    }

    @Override
    public List<Domain> findAllFromCSServer() throws Exception {
        List<Domain> domainList = new ArrayList<Domain>();
        HashMap<String, String> domainMap = new HashMap<String, String>();
        domainMap.put("listall", "true");
        configServer.setServer(1L);
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
     * @param domain object reference of the domain.
     * @throws Exception error occurs
     */
    private void validateDomain(Domain domain) throws Exception {
        Errors errors = validator.rejectIfNullEntity("domain", domain);
        errors = validator.validateEntity(domain, errors);
        Domain validateDomain = domainRepo.findByName(domain.getCompanyNameAbbreviation(), true);
        if (validateDomain != null && domain.getId() != validateDomain.getId()) {
            errors.addFieldError("companyNameAbbreviation", "domain.already.exist");
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
     * @throws Exception application errors.
     * @param pagingAndSorting do pagination with sorting for domains.
     * @return list of domains.
     */
    public Page<Domain> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return domainRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public Domain findByName(String domainName) {
        return domainRepo.findByName(domainName, true);
    }

    @Override
    public Domain findDomain() throws Exception {
        return domainRepo.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
    }
}
