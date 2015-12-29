package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAccountService;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Department service implementation class.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private DepartmentReposiory departmentRepo;

    /** CloudStack account service reference. */
    @Autowired
    private CloudStackAccountService csAccountService;

    /** CloudStack configuration reference. */
    @Autowired
    private ConfigUtil configServer;

    /** Secret key for the user encryption. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Domain repository reference. */
    @Autowired
    private DomainService domainService;

    /** Project service reference. */
    @Autowired
    private ProjectService projectService;

    /** Virtual Machine service reference. */
    @Autowired
    private VirtualMachineService vmService;

    /** Role Service reference. */
    @Autowired
    private RoleService roleService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Volume Service reference. */
    @Autowired
    private VolumeService volumeService;

    /** Autowired CloudStackUserService object. */
    @Autowired
    private CloudStackUserService csUserService;

    /** User repository reference. */
    @Autowired
    private UserService userService;

    @Override
    @PreAuthorize("hasPermission(#department.getSyncFlag(), 'ADD_DEPARTMENT')")
    public Department save(Department department) throws Exception {

        if (department.getSyncFlag()) {

            //Check the user is not a root and admin and set the domain value from login detail

            if(!String.valueOf(tokenDetails.getTokenDetails("usertype")).equals("ROOT_ADMIN")) {
                department.setDomainId(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
            }

            // Validate department
            this.validateDepartment(department);
            Domain domain = domainService.find(department.getDomainId());
            department.setDomainId(department.getDomainId());
            department.setIsActive(true);
            department.setStatus(Department.Status.ENABLED);
            department.setType(Department.AccountType.USER);
            config.setServer(1L);
            HashMap<String, String> accountMap = new HashMap<String, String>();
            accountMap.put("domainid", String.valueOf(domain.getUuid()));
            String createAccountResponse = csAccountService.createAccount(
                    String.valueOf(department.getType().ordinal()), "test@test.com", "first",
                    "last", department.getUserName(), "test", "json", accountMap);

            JSONObject createAccountResponseJSON = new JSONObject(createAccountResponse)
                    .getJSONObject("createaccountresponse").getJSONObject("account");
            JSONObject userObj = createAccountResponseJSON.getJSONArray("user").getJSONObject(0);
            csUserService.deleteUser(userObj.getString("id"), "json");
            department.setUuid((String) createAccountResponseJSON.get("id"));
            LOGGER.debug("Department created successfully" + department.getUserName());
        }
        return departmentRepo.save(department);
    }

    /**
     * Validate the department.
     *
     * @param department reference of the department.
     * @throws Exception error occurs
     */
    private void validateDepartment(Department department) throws Exception {
        Errors errors = validator.rejectIfNullEntity("department", department);
        errors = validator.validateEntity(department, errors);
        Department dep = departmentRepo.findByNameAndDomainAndIsActive(department.getUserName(), department.getDomainId(),
                true);
        if (dep != null && department.getId() != dep.getId()) {
            errors.addFieldError("userName", "department.already.exist.for.same.domain");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#department.getSyncFlag(), 'EDIT_DEPARTMENT')")
    public Department update(Department department) throws Exception {
        if (department.getSyncFlag()) {
            // Validate department
            this.validateDepartment(department);
            Domain domain = domainService.find(department.getDomainId());

            Department departmentedit = departmentRepo.findOne(department.getId());
            department.setDomainId(department.getDomainId());
            config.setServer(1L);
            HashMap<String, String> accountMap = new HashMap<String, String>();
            accountMap.put("domainid", domain.getUuid());
            accountMap.put("account", departmentedit.getUserName());
            csAccountService.updateAccount(department.getUserName(), accountMap);
            LOGGER.debug("Department updated successfully" + department.getUserName());
        }
        return departmentRepo.save(department);
    }

    @Override
    public void delete(Department department) throws Exception {
        departmentRepo.delete(department);
    }

    @Override
    public void delete(Long id) throws Exception {
        departmentRepo.delete(id);
    }

    @Override
    public Department find(Long id) throws Exception {
        Department department = departmentRepo.findOne(id);
        if (department == null) {
            throw new EntityNotFoundException("department.not.found");
        }
        return department;
    }

    @Override
    public List<Department>findDomain(Long id)throws Exception {
       return  departmentRepo.findByDomain(id);
    }
    @Override
    public Page<Department> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return departmentRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Department> findAll() throws Exception {
        return (List<Department>) departmentRepo.findAllByIsActive(true, AccountType.USER);
    }

    @Override
    public List<Department> findByAll() throws Exception {
        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            return (List<Department>) departmentRepo.findByDomainAndIsActive(domain.getId(), true, AccountType.USER);
        }
        return (List<Department>) departmentRepo.findAllByIsActive(true, AccountType.USER);
    }

    /**
     * Find all the departments with pagination.
     *
     * @throws Exception application errors.
     * @param pagingAndSorting do pagination with sorting for departments.
     * @return list of departments.
     */
    public Page<Department> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            return departmentRepo.findByDomainAndIsActive(domain.getId(), true, pagingAndSorting.toPageRequest(), AccountType.USER);
        }
        return departmentRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true, AccountType.USER);
    }

    @Override
    @PreAuthorize("hasPermission(#department.getSyncFlag(), 'DELETE_DEPARTMENT')")
    public Department softDelete(Department department) throws Exception {
        Errors errors = validator.rejectIfNullEntity("department", department);
        errors = validator.validateEntity(department, errors);
        config.setServer(1L);
        List<Project> projectResponse =  projectService.findByDepartmentAndIsActive(department.getId(), true);
        List<VmInstance> vmResponse  =  vmService.findByDepartment(department.getId());
        List<Role> roleResponse = roleService.findByDepartment(department);
        List<Volume> volumeResponse = volumeService.findByDepartment(department.getId());
        List<User> userResponse = userService.findByDepartment(department.getId());
        if (projectResponse.size() != 0  || vmResponse.size() != 0 || roleResponse.size()!= 0 || volumeResponse.size() != 0 ) {
         errors.addGlobalError( "You have following resources for this department: <br><ul><li>Project : " + projectResponse.size() +
                    "</li><li>Instance : " +vmResponse.size()+
                    "</li><li>Role : " +roleResponse.size()+
                    "</li><li>Volume : " +volumeResponse.size() +
                    "</li><li>User : " + userResponse.size() +
                    "</li></ul><br>Kindly delete associated resources and try again");

        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        else {

             department.setIsActive(false);
             department.setStatus(Department.Status.DELETED);
             String departmentResponse = csAccountService.deleteAccount(department.getUuid(), "json");
             JSONObject jobId = new JSONObject(departmentResponse).getJSONObject("deleteaccountresponse");
             if (jobId.has("jobid")) {
                 String jobResponse = csAccountService.accountJobResult(jobId.getString("jobid"), "json");
                 JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
            }
             LOGGER.debug("Department deleted successfully" + department.getUserName());
        }

        return departmentRepo.save(department);
    }

    @Override
    public List<Department> findAllByIsActive(Boolean isActive) throws Exception {
        //TODO :For Usage
        //Step1: Get all the account details

        //Step2: Get the usage response for each account from cloudstack
        return departmentRepo.findAllByIsActive(true, AccountType.USER);
    }

    @Override
    public List<Department> findAllFromCSServerByDomain() throws Exception {
        List<Department> departmentList = new ArrayList<Department>();
        HashMap<String, String> departmentMap = new HashMap<String, String>();
        //departmentMap.put("domainid", domainUuid);
         departmentMap.put("listall", "true");

        // 1. Get the list of accounts from CS server using CS connector
        String response = csAccountService.listAccounts("json", departmentMap);
        JSONArray userListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listaccountsresponse");
        if (responseObject.has("account")) {
            userListJSON = responseObject.getJSONArray("account");
            // 2. Iterate the json list, convert the single json entity to user
            for (int i = 0, size = userListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Department entity
                // and
                // Add the converted Department entity to list
                Department department = Department.convert(userListJSON.getJSONObject(i));
                department.setDomainId(convertEntityService.getDomainId(department.getTransDomainId()));
//              TODO : Need to get root admin account for role permission so you need to check the department where our you use.
                if (!department.getUserName().equalsIgnoreCase("baremetal-system-account")) {
                    departmentList.add(department);
                }
            }
        }
        return departmentList;
    }

    @Override
    public Department findByUuidAndIsActive(String uuid, Boolean isActive) throws Exception {
        return (Department) departmentRepo.findByUuidAndIsActive(uuid, isActive);
    }

    @Override
    public List<Department> findByDomainAndIsActive(Long domainId, Boolean isActive) {
        return departmentRepo.findByDomainAndIsActive(domainId, isActive, AccountType.USER);
    }

    @Override
    public Department findByUsername(String name, Boolean isActive) {
        return (Department) departmentRepo.findByUsername(name, isActive);
    }

    @Override
    public Department findByUsernameAndDomain(String name, Domain domain, Boolean isActive) {
        return (Department) departmentRepo.findByUsernameAndDomain(name, domain, isActive);
    }

    @Override
    public List<Department> findDepartmentsByAccountTypesAndActive(List<AccountType> types, Boolean isActive) throws Exception {
        return (List<Department>) departmentRepo.findDepartmentsByAccountTypesAndActive(types, isActive);
    }

    public List<Department> findAllBySync() throws Exception {
        return (List<Department>) departmentRepo.findAll();
    }

    @Override
    public List<Department> findDepartmentsByDomainAndAccountTypesAndActive(Long domainId, List<AccountType> types, Boolean isActive) throws Exception {
        return (List<Department>) departmentRepo.findDepartmentsByDomainAndAccountTypesAndActive(domainId, types, isActive);
    }

}
