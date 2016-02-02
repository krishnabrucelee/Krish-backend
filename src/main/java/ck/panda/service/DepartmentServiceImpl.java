package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.repository.jpa.DepartmentRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAccountService;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;
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
    private DepartmentRepository departmentRepo;

    /** CloudStack account service reference. */
    @Autowired
    private CloudStackAccountService csAccountService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

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

    public static final String BAREMETAL_SYSTEM_ACCOUNT = "baremetal-system-account";


    @Override
    @PreAuthorize("hasPermission(#department.getSyncFlag(), 'ADD_DEPARTMENT')")
    public Department save(Department department, Long userId) throws Exception {
        if (department.getSyncFlag()) {
            User user = convertEntityService.getOwnerById(userId);
            // Check the user is not a root and admin and set the domain value from login detail
            if (user.getType().equals(User.UserType.ROOT_ADMIN)) {
                department.setDomainId(user.getDomainId());
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
                    String.valueOf(department.getType().ordinal()), "test@test.com", "first", "last",
                    department.getUserName(), "test", "json", accountMap);

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
        Department dep = departmentRepo.findByUsernameDomainAndIsActive(department.getUserName(),
                department.getDomainId(), true);
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
    public Page<Department> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return departmentRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Department> findAll() throws Exception {
        return (List<Department>) departmentRepo.findAllByIsActive(true, AccountType.USER);
    }

    @Override
    public Page<Department> findAllByActive(PagingAndSorting pagingAndSorting, Long userId) throws Exception {
        Domain domain = convertEntityService.getOwnerById(userId).getDomain();
        if (domain != null && !domain.getName().equals("ROOT")) {
            return departmentRepo.findByDomainAndIsActive(domain.getId(), true, pagingAndSorting.toPageRequest(),
                    AccountType.USER);
        }
        return departmentRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true, AccountType.USER);
    }

    @Override
    @PreAuthorize("hasPermission(#department.getSyncFlag(), 'DELETE_DEPARTMENT')")
    public Department softDelete(Department department) throws Exception {
        Errors errors = validator.rejectIfNullEntity("department", department);
        errors = validator.validateEntity(department, errors);
        config.setServer(1L);
        if (department.getSyncFlag()) {
            List<Project> projectResponse = projectService.findAllByDepartmentAndIsActive(department.getId(), true);
            List<VmInstance> vmResponse = vmService.findByDepartmentAndVmStatus(department.getId(), VmInstance.Status.Expunging);
            List<Role> roleResponse = roleService.findByDepartmentAndIsActive(department.getId(), true);
            List<Volume> volumeResponse = volumeService.findByDepartmentAndIsActive(department.getId(), true);
            List<User> userResponse = userService.findByDepartment(department.getId());
            if (projectResponse.size() != 0 || vmResponse.size() != 0
                    || roleResponse.size() != 0 || volumeResponse.size() != 0) {
                errors.addGlobalError(GenericConstants.PAGE_ERROR_SEPARATOR + GenericConstants.TOKEN_SEPARATOR +
                        projectResponse.size() + GenericConstants.TOKEN_SEPARATOR +
                        vmResponse.size() + GenericConstants.TOKEN_SEPARATOR +
                        roleResponse.size() + GenericConstants.TOKEN_SEPARATOR +
                        volumeResponse.size() + GenericConstants.TOKEN_SEPARATOR +
                        userResponse.size());

            }
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            department.setIsActive(false);
            department.setStatus(Department.Status.DELETED);
            if (department.getSyncFlag()) {
                String departmentResponse = csAccountService.deleteAccount(department.getUuid(), "json");
                JSONObject jobId = new JSONObject(departmentResponse).getJSONObject("deleteaccountresponse");
                if (jobId.has("jobid")) {
                    String jobResponse = csAccountService.accountJobResult(jobId.getString("jobid"), "json");
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                }
                LOGGER.debug("Department deleted successfully" + department.getUserName());
            }
        }
        return departmentRepo.save(department);
    }

    @Override
    public List<Department> findAllFromCSServer() throws Exception {
        List<Department> departmentList = new ArrayList<Department>();
        HashMap<String, String> departmentMap = new HashMap<String, String>();
        departmentMap.put("listall", "true");

        // 1. Get the list of accounts from CS server using CS connector
        String response = csAccountService.listAccounts("json", departmentMap);
        JSONArray userListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listaccountsresponse");
        if (responseObject.has("account")) {
            userListJSON = responseObject.getJSONArray("account");
            // 2. Iterate the json list, convert the single json entity to department
            for (int i = 0, size = userListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to department entity and
                // Add the converted department entity to list
                Department department = Department.convert(userListJSON.getJSONObject(i));
                department.setDomainId(convertEntityService.getDomainId(department.getTransDomainId()));
                if (!department.getUserName().equalsIgnoreCase(BAREMETAL_SYSTEM_ACCOUNT)) {
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
    public List<Department> findByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception {
        Domain domain = domainService.find(domainId);
        if (domain != null && !domain.getName().equals("ROOT")) {
            return (List<Department>) departmentRepo.findByDomainAndIsActive(domain.getId(), isActive, AccountType.USER);
        }
        return departmentRepo.findByDomainAndIsActive(domainId, isActive, AccountType.USER);
    }

    @Override
    public Department findByUsernameDomainAndIsActive(String username, Long domainId, Boolean isActive) {
        return (Department) departmentRepo.findByUsernameDomainAndIsActive(username, domainId, isActive);
    }

    @Override
    public List<Department> findByAccountTypesAndActive(List<AccountType> types, Boolean isActive)
            throws Exception {
        return (List<Department>) departmentRepo.findByAccountTypesAndActive(types, isActive);
    }

    /**
     * Find all department for sync.
     *
     * @return department list.
     * @throws Exception unhandled errors.
     */
    public List<Department> findAllBySync() throws Exception {
        return (List<Department>) departmentRepo.findAll();
    }

    @Override
    public List<Department> findByDomainAndAccountTypesAndActive(Long domainId, List<AccountType> types,
            Boolean isActive) throws Exception {
        return (List<Department>) departmentRepo.findByDomainAndAccountTypesAndActive(domainId, types,
                isActive);
    }

    @Override
    public Department findbyUUID(String uuid) throws Exception {
        return departmentRepo.findByUuidAndIsActive(uuid, true);
    }

    @Override
    public Department save(Department department) throws Exception {
        return null;
    }

}
