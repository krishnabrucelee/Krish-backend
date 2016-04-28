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
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.SSHKey;
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
import ck.panda.constants.PingConstants;
import ck.panda.util.PingService;

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

    /** Resource limit department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource limit domain service reference. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

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

    /** User Service reference. */
    @Autowired
    private UserService userService;

    /** sshkey service reference. */
    @Autowired
    private SSHKeyService sshkeyService;

    /** Network Service reference .*/
    @Autowired
    private NetworkService networkService;

    /** Mr.ping service reference. */
    @Autowired
    private PingService pingService;

    /** Baremetal system constant. */
    public static final String BAREMETAL_SYSTEM_ACCOUNT = "baremetal-system-account";

    /** Username constant. */
    public static final String USER_NAME = "userName";

    @Override
    @PreAuthorize("hasPermission(#department.getSyncFlag(), 'ADD_DEPARTMENT')")
    public Department save(Department department, Long userId) throws Exception {
        if (department.getSyncFlag()) {
            User user = convertEntityService.getOwnerById(userId);
            // Check the user is not a root and admin and set the domain value from login detail
            if (!user.getType().equals(User.UserType.ROOT_ADMIN)) {
                department.setDomainId(user.getDomainId());
            }

            // Validate department.
            this.validateDepartment(department);
            Domain domain = domainService.find(department.getDomainId());
            department.setDomainId(department.getDomainId());
            department.setIsActive(true);
            department.setStatus(Department.Status.ENABLED);
            department.setType(Department.AccountType.USER);
            HashMap<String, String> accountMap = new HashMap<String, String>();
            accountMap.put(CloudStackConstants.CS_DOMAIN_ID, String.valueOf(domain.getUuid()));
            //TODO : This will be the hardcoded values for the dummy user after creating department it will remove from the cloudstack.
            config.setServer(1L);
            String createAccountResponse = csAccountService.createAccount(
                    String.valueOf(CloudStackConstants.CS_USER_TYPE), "test@test.com", "first", "last",
                    department.getUserName(), "test", CloudStackConstants.JSON, accountMap);

            JSONObject createAccountResponseJSON = new JSONObject(createAccountResponse)
                    .getJSONObject(CloudStackConstants.CS_ACCOUNT_RESPONSE).getJSONObject(CloudStackConstants.CS_ACCOUNT);
            JSONObject userObj = createAccountResponseJSON.getJSONArray(CloudStackConstants.CS_USER).getJSONObject(0);
            config.setServer(1L);
            csUserService.deleteUser(userObj.getString(CloudStackConstants.CS_ID), CloudStackConstants.JSON);
            department.setUuid((String) createAccountResponseJSON.get(CloudStackConstants.CS_ID));
            LOGGER.debug("Department created successfully" + department.getUserName());
            saveDepartmentToPingProject(department);
        }
        department = departmentRepo.save(department);
		if (department.getSyncFlag()) {
			for (String keys : convertEntityService.getResourceTypeValue().keySet()) {
				ResourceLimitDepartment persistDepartment = resourceLimitDepartmentService
						.findByDepartmentAndResourceType(department.getId(), ResourceLimitDepartment.ResourceType
								.valueOf(convertEntityService.getResourceTypeValue().get(keys)), true);
				if (persistDepartment != null) {
					resourceLimitDepartmentService.delete(persistDepartment);
				}
				ResourceLimitDepartment resourceLimitDepartment = new ResourceLimitDepartment();
				resourceLimitDepartment.setDepartmentId(department.getId());
				resourceLimitDepartment.setDomainId(department.getDomainId());
				resourceLimitDepartment.setMax(0L);
				resourceLimitDepartment.setAvailable(0L);
				resourceLimitDepartment.setUsedLimit(0L);
				resourceLimitDepartment.setResourceType(ResourceLimitDepartment.ResourceType
						.valueOf(convertEntityService.getResourceTypeValue().get(keys)));
				resourceLimitDepartment.setIsSyncFlag(false);
				resourceLimitDepartment.setIsActive(true);
				resourceLimitDepartmentService.update(resourceLimitDepartment);
			}
		}
        return department;
    }

    /**
     * Validate the department.
     *
     * @param department reference of the department.
     * @throws Exception error occurs
     */
    private void validateDepartment(Department department) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.CS_DEPARTMENT, department);
        errors = validator.validateEntity(department, errors);
        if (pingService.apiConnectionCheck(errors)) {
            Department dep = departmentRepo.findByUsernameDomainAndIsActive(department.getUserName(),
                department.getDomainId(), true);
            if (dep != null && department.getId() != dep.getId()) {
                errors.addFieldError(USER_NAME, "department.already.exist.for.same.domain");
            }
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            }
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
            HashMap<String, String> accountMap = new HashMap<String, String>();
            accountMap.put(CloudStackConstants.CS_DOMAIN_ID, domain.getUuid());
            accountMap.put(CloudStackConstants.CS_ACCOUNT, departmentedit.getUserName());
            config.setServer(1L);
            csAccountService.updateAccount(department.getUserName(), accountMap);
            LOGGER.debug("Department updated successfully" + department.getUserName());
            saveDepartmentToPingProject(department);
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
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.CS_DEPARTMENT, department);
        errors = validator.validateEntity(department, errors);
        if (department.getSyncFlag()) {
            List<Project> projectResponse = projectService.findAllByDepartmentAndIsActive(department.getId(), true);
            List<VmInstance> vmResponse = vmService.findAllByDepartmentAndVmStatus(department.getId(), VmInstance.Status.EXPUNGING);
            List<Role> roleResponse = roleService.findByDepartmentAndIsActive(department.getId(), true);
            List<Volume> volumeResponse = volumeService.findByDepartmentAndIsActive(department.getId(), true);
            List<User> userResponse = userService.findByDepartment(department.getId());
            List<SSHKey> sshkeyResponse = sshkeyService.findAllByDepartmentAndIsActive(department.getId(), true);
            List<Network> networkResponse = networkService.findByDepartmentAndNetworkIsActive(department.getId(), true);
            if (projectResponse.size() != 0 || vmResponse.size() != 0
                    || roleResponse.size() != 0 || volumeResponse.size() != 0 || sshkeyResponse.size() != 0 ) {
                errors.addGlobalError(GenericConstants.PAGE_ERROR_SEPARATOR + GenericConstants.TOKEN_SEPARATOR
                        + projectResponse.size() + GenericConstants.TOKEN_SEPARATOR
                        + vmResponse.size() + GenericConstants.TOKEN_SEPARATOR
                        + roleResponse.size() + GenericConstants.TOKEN_SEPARATOR
                        + volumeResponse.size() + GenericConstants.TOKEN_SEPARATOR
                        + sshkeyResponse.size() + GenericConstants.TOKEN_SEPARATOR
                        + networkResponse.size() + GenericConstants.TOKEN_SEPARATOR
                        + userResponse.size());

            }
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            department.setIsActive(false);
            department.setStatus(Department.Status.DELETED);
            if (department.getSyncFlag()) {
                config.setServer(1L);
                String departmentResponse = csAccountService.deleteAccount(department.getUuid(), CloudStackConstants.JSON);
                JSONObject jobId = new JSONObject(departmentResponse).getJSONObject(CloudStackConstants.CS_DELETE_ACCOUNT_RESPONSE);
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    config.setServer(1L);
                    String jobResponse = csAccountService.accountJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                }
                LOGGER.debug("Department deleted successfully" + department.getUserName());
            }
        }
        List<ResourceLimitDomain> resourceLimitDomain = resourceLimitDomainService.findAllByDomainIdAndIsActive(department.getDomainId(), true);
        List<ResourceLimitDepartment> resourceLimitDepartment = resourceLimitDepartmentService.findAllByDepartmentIdAndIsActive(department.getId(), true);
        for (ResourceLimitDomain domainLimit : resourceLimitDomain) {
        	for (ResourceLimitDepartment departmentLimit : resourceLimitDepartment) {
        		if (domainLimit.getResourceType().toString().equals(departmentLimit.getResourceType().toString())) {
					if (departmentLimit.getMax() == -1L) {
						domainLimit.setUsedLimit(EmptytoLong(domainLimit.getUsedLimit()));
					} else {
						domainLimit.setUsedLimit(
								EmptytoLong(domainLimit.getUsedLimit()) - EmptytoLong(departmentLimit.getMax()));
					}
        			domainLimit.setIsSyncFlag(false);
                    resourceLimitDomainService.save(domainLimit);
                    departmentLimit.setIsActive(false);
                    resourceLimitDepartmentService.save(departmentLimit);
        		}
        	}
        }
        return departmentRepo.save(department);
    }

    @Override
    public List<Department> findAllFromCSServer() throws Exception {
        List<Department> departmentList = new ArrayList<Department>();
        HashMap<String, String> departmentMap = new HashMap<String, String>();
        departmentMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        config.setServer(1L);
        // 1. Get the list of accounts from CS server using CS connector
        String response = csAccountService.listAccounts(CloudStackConstants.JSON, departmentMap);
        JSONArray userListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CloudStackConstants.CS_LIST_ACCOUNT_RESPONSE);
        if (responseObject.has(CloudStackConstants.CS_ACCOUNT)) {
            userListJSON = responseObject.getJSONArray(CloudStackConstants.CS_ACCOUNT);
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
        return departmentRepo.findAllByIsActive(isActive, AccountType.USER);
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
        if (!department.getSyncFlag()) {
            return departmentRepo.save(department);
        }
        return department;
    }

    @Override
    public Page<Department> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return departmentRepo.findAllByDomainIdAndIsActive(domainId, true, AccountType.USER, pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Department> findAllByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception {
        return (List<Department>) departmentRepo.findByDomainAndIsActive(domainId, isActive, AccountType.USER);
    }

    @Override
    public List<Department> findAllByDomainAccountTypeAndIsActive(Long domainId, Boolean isActive, AccountType domainAdmin) throws Exception {
        return (List<Department>) departmentRepo.findByDomainAndIsActive(domainId, isActive, AccountType.DOMAIN_ADMIN);
    }

    /**
     * Save department details to MR.ping project for usage calculation.
     *
     * @param department domain object
     * @return status
     * @throws Exception raise if error
     */
    public Boolean saveDepartmentToPingProject(Department department) throws Exception {
        JSONObject optional = new JSONObject();
        optional.put(PingConstants.UUID, department.getUuid());
        optional.put(PingConstants.NAME, department.getUserName());
        optional.put(PingConstants.DOMAIN_ID, domainService.find(department.getDomainId()).getUuid());
        pingService.addDepartmentToPing(optional);
        return true;
    }

	@Override
	public List<Department> findAllByActive(Boolean isActive) throws Exception {
		List<Department.AccountType> accountTypes = new  ArrayList<Department.AccountType>();
    	accountTypes.add(Department.AccountType.USER);
    	accountTypes.add(Department.AccountType.DOMAIN_ADMIN);
    	accountTypes.add(Department.AccountType.ROOT_ADMIN);
		return findByAccountTypesAndActive(accountTypes, isActive);
	}

	/**
     * Empty check.
     *
     * @param value long value
     * @return long.
     */
    public Long EmptytoLong(Long value) {
        if (value == null) {
            return 0L;
        }
        return value;
    }

}
