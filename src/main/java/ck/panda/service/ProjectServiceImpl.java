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
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.SSHKey;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.VpnUser;
import ck.panda.domain.repository.jpa.ProjectRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackProjectService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;
import ck.panda.util.error.exception.EntityNotFoundException;
import ck.panda.constants.PingConstants;
import ck.panda.util.PingService;
import ck.panda.util.TokenDetails;

/**
 * Project service implementation used to get list of project and save ,delete, update the project in application
 * database.
 */
@Service
public class ProjectServiceImpl implements ProjectService {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectServiceImpl.class);

    /** Constant used for project response. */
    public static final String RESPONSE_PROJECT = "project";

    /** Constant used for project account response. */
    public static final String RESPONSE_PROJECT_ACCOUNT = "projectaccount";

    /** Constant used for project accounts list response. */
    public static final String LIST_PROJECT_ACCOUNTS = "listprojectaccountsresponse";

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Virtual machine service reference. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** Listing resource limit project service. */
    @Autowired
    private ResourceLimitProjectService resourceProjectService;

    /** project repository reference. */
    @Autowired
    private ProjectRepository projectRepository;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackProjectService cloudStackProjectService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Resource limit department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource limit project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** Configuration Utilities. */
    @Autowired
    private ConfigUtil config;

    /** Mr.ping service reference. */
    @Autowired
    private PingService pingService;

    /** Token details reference. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Network Service Reference */
    @Autowired
    private NetworkService networkService;

    /** Sshkey service reference */
    @Autowired
    private SSHKeyService sshKeyService;

    /** Volume Service Reference. */
    @Autowired
    private VolumeService volumeService;

    @Override
    @PreAuthorize("hasPermission(#project.getSyncFlag(), 'CREATE_PROJECT')")
    public Project save(Project project) throws Exception {
        List<User> users = new ArrayList<User>();
        HashMap<String, String> optional = new HashMap<String, String>();
        // Check whether call for CS sync or our panda.
        if (project.getSyncFlag()) {
            // Do entity validation.
            Errors errors = validator.rejectIfNullEntity(RESPONSE_PROJECT, project);
            errors = validator.validateEntity(project, errors);
            errors = this.validateByName(errors, project.getName(), project.getDepartment(), 0L);
            // Check it has field errors or not.
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else if (pingService.apiConnectionCheck(errors)) {
                optional.put(CloudStackConstants.CS_DOMAIN_ID,
                        convertEntityService.getDomainById(project.getDomainId()).getUuid());
                optional.put(CloudStackConstants.CS_ACCOUNT,
                        convertEntityService.getDepartmentById(project.getDepartmentId()).getUserName());
                config.setUserServer();
                // CS API call for create new project.
                String csResponse = cloudStackProjectService.createProject(CloudStackConstants.JSON, project.getName(),
                        project.getDescription(), optional);
                JSONObject csProject = new JSONObject(csResponse).getJSONObject("createprojectresponse");
                // Check whether API response has error or not.
                if (csProject.has(CloudStackConstants.CS_ERROR_CODE)) {
                    errors = this.validateEvent(errors, csProject.getString(CloudStackConstants.CS_ERROR_TEXT));
                    throw new ApplicationException(errors);
                } else {
                    project.setUuid(csProject.getString(CloudStackConstants.CS_ID));
                    // Set the project owner.
                    users.add(convertEntityService.getOwnerById(project.getProjectOwnerId()));
                    // Give the access to project owner.
                    project.setUserList(users);
                    config.setUserServer();
                    String instancePro = cloudStackProjectService.queryAsyncJobResult(
                            csProject.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    JSONObject resProject = new JSONObject(instancePro)
                            .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (resProject.getString(CloudStackConstants.CS_JOB_STATUS)
                            .equals(GenericConstants.ERROR_JOB_STATUS)) {
                        project.setIsActive(false);
                        // Send global error if CS can't able to create project.
                        throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                                csProject.getString(CloudStackConstants.CS_ERROR_TEXT));
                    } else {
                        LOGGER.debug("New project created, project uuid is "
                                + csProject.getString(CloudStackConstants.CS_ID));
                        project.setIsActive(true);
                        project.setStatus(Project.Status.ENABLED);
                        saveProjectToPing(project);
                    }
                }
            }
        }
        // Save the entity with uuid for created new project in CS.
        project = projectRepository.save(project);
        if (project.getSyncFlag()) {
            for (String keys : convertEntityService.getResourceTypeValue().keySet()) {
                ResourceLimitProject persistProject = resourceProjectService
                        .findByProjectAndResourceType(project.getId(), ResourceLimitProject.ResourceType
                                .valueOf(convertEntityService.getResourceTypeValue().get(keys)), true);
                if (persistProject != null) {
                    resourceProjectService.delete(persistProject);
                }
                ResourceLimitProject resourceLimitProject = new ResourceLimitProject();
                resourceLimitProject.setDepartmentId(project.getDepartmentId());
                resourceLimitProject.setDomainId(project.getDomainId());
                resourceLimitProject.setProjectId(project.getId());
                resourceLimitProject.setMax(0L);
                resourceLimitProject.setAvailable(0L);
                resourceLimitProject.setUsedLimit(0L);
                resourceLimitProject.setResourceType(ResourceLimitProject.ResourceType
                        .valueOf(convertEntityService.getResourceTypeValue().get(keys)));
                resourceLimitProject.setIsSyncFlag(false);
                resourceLimitProject.setIsActive(true);
                resourceProjectService.update(resourceLimitProject);
            }
        }
        return project;
    }

    @Override
    @PreAuthorize("hasPermission(#project.getSyncFlag(), 'EDIT_PROJECT')")
    public Project update(Project project) throws Exception {
        List<User> users = new ArrayList<User>();
        // Check whether call for CS sync or our panda.
        if (project.getSyncFlag()) {
            // Do entity validation.
            Errors errors = validator.rejectIfNullEntity(RESPONSE_PROJECT, project);
            errors = validator.validateEntity(project, errors);
            errors = this.validateByName(errors, project.getName(),
                    convertEntityService.getDepartmentById(project.getDepartmentId()), project.getId());
            // Check it has field errors or not.
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else if (pingService.apiConnectionCheck(errors)) {
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put(CloudStackConstants.CS_DOMAIN_ID,
                        convertEntityService.getDomainById(project.getDomainId()).getUuid());
                optional.put(CloudStackConstants.CS_ACCOUNT,
                        convertEntityService.getDepartmentById(project.getDepartmentId()).getUserName());
                config.setUserServer();
                // CS API call for update existing project.
                String csResponse = cloudStackProjectService.updateProject(project.getUuid(), project.getDescription(),
                        CloudStackConstants.JSON, optional);
                JSONObject csProject = new JSONObject(csResponse)
                        .getJSONObject(CloudStackConstants.CS_PROJECT_UPDATE_RESPONSE);
                // Check whether API response has error or not.
                if (csProject.has(CloudStackConstants.CS_ERROR_CODE)) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                            csProject.getString(CloudStackConstants.CS_ERROR_TEXT));
                }
                  // Project owner update.
                if (project.getProjectOwnerId() != null) {
                    users.add(convertEntityService.getOwnerById(project.getProjectOwnerId()));
                }
                // Project access user list update.
                if (project.getUserList().size() > 0) {
                    for (User user : project.getUserList()) {
                        if (project.getProjectOwnerId() != user.getId()) {
                            users.add(user);
                        }
                    }
                }
                project.setUserList(users);
            }
            String response = cloudStackProjectService.listProjectAccounts(CloudStackConstants.JSON,
                    CloudStackConstants.CS_ACCOUNT_ROLE, project.getUuid());
            JSONArray projectAccountListJSON = null;
            JSONObject responseObject = new JSONObject(response).getJSONObject(LIST_PROJECT_ACCOUNTS);
            if (responseObject.has(RESPONSE_PROJECT_ACCOUNT)) {
                projectAccountListJSON = responseObject.getJSONArray(RESPONSE_PROJECT_ACCOUNT);
                // 2. Iterate the json list, convert the single json entity to Project account.
                for (int i = 0, size = projectAccountListJSON.length(); i < size; i++) {
                    String deleteResponse = cloudStackProjectService.deleteAccountFromProject(project.getUuid(),
                            projectAccountListJSON.getJSONObject(i).getString(CloudStackConstants.CS_ACCOUNT),
                            CloudStackConstants.JSON);
                }
            }
            saveProjectToPing(project);
        }
        // Update project entity.
        return projectRepository.save(project);
    }

    @Override
    public void delete(Project project) throws Exception {
        projectRepository.delete(project);
    }

    @Override
    public void delete(Long id) throws Exception {
        projectRepository.delete(id);
    }

    @Override
    public Project find(Long id) throws Exception {
        // find the project by id.
        Project project = projectRepository.findOne(id);
        // Entity validation.
        if (project == null) {
            throw new EntityNotFoundException("project.not.found");
        }
        return projectRepository.findOne(id);
    }

    @Override
    public Page<Project> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return projectRepository.findAllByStatus(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Project> findAll() throws Exception {
        return (List<Project>) projectRepository.findAll();
    }

    @Override
    public List<Project> getAllProjects(Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && user.getType().equals(User.UserType.DOMAIN_ADMIN)) {
            return (List<Project>) projectRepository.findAllByDomain(user.getDomainId(), true);
        }
        return (List<Project>) projectRepository.findAll();
    }

    /**
     * Check the Project name already exist or not for same department.
     *
     * @param errors already existing error list.
     * @param name name of the project.
     * @param department department object.
     * @param projectId project id.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateByName(Errors errors, String name, Department department, Long projectId) throws Exception {
        if (projectRepository.findByNameAndDepartment(true, name, department, projectId) != null) {
            errors.addFieldError("name", "project.already.exist.for.same.department");
        }
        return errors;
    }

    /**
     * Check the virtual machine CS error handling.
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

    @Override
    @PreAuthorize("hasPermission(#project.getSyncFlag(), 'DELETE_PROJECT')")
    public Project softDelete(Project project) throws Exception {
        Errors errors = validator.rejectIfNullEntity(RESPONSE_PROJECT, project);
        List<VmInstance.Status> statusCode = new ArrayList<VmInstance.Status>();
        project.setIsActive(false);
        project.setStatus(Project.Status.DELETED);
        if (project.getSyncFlag()) {
            statusCode.add(VmInstance.Status.RUNNING);
            statusCode.add(VmInstance.Status.STOPPED);
            statusCode.add(VmInstance.Status.STARTING);
            statusCode.add(VmInstance.Status.STOPPING);
            List<VmInstance> vmList = virtualMachineService.findAllByProjectAndStatus(project.getId(), statusCode);
            List<Network> networkList = networkService.findByProjectAndNetworkIsActive(project.getId(), true);
            List<SSHKey> sshKeyList = sshKeyService.findAllByProjectAndIsActive(project.getId(), true);
            List<Volume> volumeList = volumeService.findAllByProjectAndIsActive(project.getId(), true);
            if (vmList.size() > 0 || networkList.size() > 0 || sshKeyList.size() > 0 || volumeList.size() > 0 ) {
                errors.addGlobalError(GenericConstants.PAGE_ERROR_SEPARATOR + GenericConstants.TOKEN_SEPARATOR
                        + vmList.size() + GenericConstants.TOKEN_SEPARATOR
                        + networkList.size() + GenericConstants.TOKEN_SEPARATOR
                        + sshKeyList.size() + GenericConstants.TOKEN_SEPARATOR
                        + volumeList.size());
            }
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            }
            HashMap<String, String> optional = new HashMap<String, String>();
            optional.put(CloudStackConstants.CS_DOMAIN_ID, project.getDepartment().getDomain().getUuid());
            optional.put(CloudStackConstants.CS_ACCOUNT, project.getDepartment().getUserName());
            config.setUserServer();
            // CS API call for delete project.
            String csResponse = cloudStackProjectService.deleteProject(project.getUuid());
            JSONObject csProject = new JSONObject(csResponse).getJSONObject("deleteprojectresponse");
            if (csProject.has(CloudStackConstants.CS_ERROR_CODE)) {
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                        csProject.getString(CloudStackConstants.CS_ERROR_TEXT));
            }
        }
        List<ResourceLimitDepartment> resourceLimitDepartment = resourceLimitDepartmentService.findAllByDepartmentIdAndIsActive(project.getDepartmentId(), true);
        List<ResourceLimitProject> resourceLimitProject = resourceLimitProjectService.findAllByProjectIdAndIsActive(project.getId(), true);
        for (ResourceLimitDepartment departmentLimit : resourceLimitDepartment) {
            for (ResourceLimitProject projectLimit : resourceLimitProject) {
                if (departmentLimit.getResourceType().toString().equals(projectLimit.getResourceType().toString())) {
                    if (projectLimit.getMax() == -1L) {
                        departmentLimit.setUsedLimit(EmptytoLong(departmentLimit.getUsedLimit()));
                    } else {
                        departmentLimit.setUsedLimit(EmptytoLong(departmentLimit.getUsedLimit()) - EmptytoLong(projectLimit.getMax()));
                    }
                    departmentLimit.setIsSyncFlag(false);
                    resourceLimitDepartmentService.save(departmentLimit);
                    projectLimit.setIsActive(false);
                    projectLimit.setIsSyncFlag(false);
                    resourceLimitProjectService.save(projectLimit);
                }
            }
        }
        // Update project entity.
        return projectRepository.save(project);
    }

    @Override
    public Page<Project> findAllByActive(Boolean isActive, PagingAndSorting pagingAndSorting, Long userId)
            throws Exception {
        List<Project> projects = new ArrayList<Project>();
        // Find all active projects.
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                // Find all active projects by domain.
                return projectRepository.findAllByDomain(user.getDomainId(), pagingAndSorting.toPageRequest(),
                        isActive);
            } else {
                // Find all active projects by department.
                return projectRepository.findAllByDepartmentAndIsActiveAndPage(user.getDepartmentId(), isActive, pagingAndSorting.toPageRequest());
            }
        }
        return projectRepository.findAllByStatus(pagingAndSorting.toPageRequest(), isActive);
    }

    @Override
    public List<Project> findAllByActive(Boolean isActive) throws Exception {
        return projectRepository.findAllByIsActive(isActive);
    }

    @Override
    public List<Project> findAllByDomain(Long id) {
        return projectRepository.findAllByDomain(id, true);
    }

    @Override
    public List<Project> findAllFromCSServerByDomain() throws Exception {
        List<Project> projectList = new ArrayList<Project>();
        HashMap<String, String> projectMap = new HashMap<String, String>();
        projectMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        config.setServer(1L);
        // 1. Get the list of Project from CS server using CS connector
        String response = cloudStackProjectService.listProjects(CloudStackConstants.JSON, projectMap);
        JSONArray projectListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listprojectsresponse");
        if (responseObject.has(RESPONSE_PROJECT)) {
            projectListJSON = responseObject.getJSONArray(RESPONSE_PROJECT);
            // 2. Iterate the json list, convert the single json entity to
            // Project
            for (int i = 0, size = projectListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Project entity
                // and Add
                // the converted Project entity to list
                Project project = Project.convert(projectListJSON.getJSONObject(i));
                project.setDomainId(convertEntityService.getDomainId(project.getTransDomainId()));
                project.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                        project.getTransAccount(), convertEntityService.getDomain(project.getTransDomainId())));
                project.setIsActive(convertEntityService.getState(project.getTransState()));
                project.setStatus((Project.Status) convertEntityService.getStatus(project.getTransState()));
                projectList.add(project);
            }
        }
        return projectList;
    }

    @Override
    public Project findByUuid(String uuid) throws Exception {
        return projectRepository.findByUuid(uuid);
    }

    @Override
    public List<Project> findAllByDepartmentAndIsActive(Long id, Boolean isActive) throws Exception {
        return projectRepository.findAllByDepartmentAndIsActive(id, true);
    }

    @Override
    public List<Project> findAllByUserAndIsActive(Long id, Boolean isActive) throws Exception {
        return projectRepository.findAllByUserAndIsActive(id, isActive);
    }

    @Override
    @PreAuthorize("hasPermission(#project.getSyncFlag(), 'EDIT_PROJECT')")
    public Project removeUser(Project project) throws Exception {
        List<User> users = new ArrayList<User>();
        // Remove user from project.
        if (project.getUserList().size() > 0) {
            for (User user : project.getUserList()) {
                users.add(user);
            }
        }
        // Update project access list.
        project.setUserList(users);
        return projectRepository.save(project);
    }

    @Override
    public Page<Project> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return projectRepository.findAllByDomainIdAndIsActive(domainId, true, pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<Project> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText)
            throws Exception {
          User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
          if (convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.USER)) {
              domainId = user.getDomainId();
               Long departmentId = user.getDepartmentId();
               return projectRepository.findAllByDomainIdAndIsActiveAndSearchText(domainId, true, pagingAndSorting.toPageRequest(),searchText,departmentId);
          }
          if (!convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.ROOT_ADMIN)) {
              domainId = user.getDomainId();
          }
        return projectRepository.findAllByDomainAndSearchText(domainId, true, pagingAndSorting.toPageRequest(),searchText);
   }

    /**
     * Save project details to MR.ping project for usage calculation.
     *
     * @param project domain object
     * @return status
     * @throws Exception raise if error
     */
    public Boolean saveProjectToPing(Project project) throws Exception {
        JSONObject optional = new JSONObject();
        optional.put(PingConstants.UUID, project.getUuid());
        optional.put(PingConstants.NAME, project.getName());
        optional.put(PingConstants.DOMAIN_ID, convertEntityService.getDomainById(project.getDomainId()).getUuid());
        optional.put(PingConstants.DEPARTMENT_UUID, convertEntityService.getDepartmentById(project.getDepartmentId()).getUuid());
        pingService.addProjectToPing(optional);
        return true;
    }

    @Override
    public Project findByProjectNameAndIsActive(String projectAccountName, Long domainId, Boolean isActive) {
        return projectRepository.findByProjectNameAndIsActive(projectAccountName, domainId, isActive);
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
