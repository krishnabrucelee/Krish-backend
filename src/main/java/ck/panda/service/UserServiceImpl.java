package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EmailConstants;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.LoginHistory;
import ck.panda.domain.entity.Permission;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.Role;
import ck.panda.domain.entity.RolePrincipal;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.Status;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.rabbitmq.util.EmailEvent;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.DateConvertUtil;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/** UserService implementation class. */
@Service
public class UserServiceImpl implements UserService {

    /** Constant for user disable response. */
    private static final String DISABLE_USER = "disableuserresponse";

    /** Constant for user enable response. */
    private static final String ENABLE_USER = "enableuserresponse";

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** User repository reference. */
    @Autowired
    private UserRepository userRepository;

    /** CloudStack user service reference. */
    @Autowired
    private CloudStackUserService csUserService;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Project service reference. */
    @Autowired
    private ProjectService projectService;

    /** Cloud stack configuration utility for CS connector. */
    @Autowired
    private ConfigUtil config;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Reference of domain Service . */
    @Autowired
    private DomainService domainService;

    /** Reference of Token Details Util. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Reference of cloudStack Constants. */
    private CloudStackConstants cloudStackConstants;

    /** Virtual machine service. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** Role service. */
    @Autowired
    private RoleService roleService;

    /** Role service. */
    @Autowired
    private SyncService syncService;

    /** Email job service. */
    @Autowired
    private EmailJobService emailJobService;

    /** Login history service attribute. */
    @Autowired
    private LoginHistoryService loginHistoryService;

    /** Constant for generic UTF. */
    public static final String CS_UTF = "utf-8";

    /** Constant for generic AES. */
    public static final String CS_AES = "AES";

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    @Override
    @PreAuthorize("hasPermission(#user.getSyncFlag(), 'CREATE_USER')")
    public User save(User user, Long id) throws Exception {
        String dept;
        HashMap<String, String> userMap = new HashMap<String, String>();
        List<User> users = new ArrayList<User>();
        if (user.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(cloudStackConstants.CS_USER, user);
            errors = validator.validateEntity(user, errors);
            errors = this.validateName(errors, user.getUserName(), user.getDomain(), user.getId());
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                user.setType(User.UserType.USER);
                user.setStatus(Status.ACTIVE);
                user.setRoleId(user.getRole().getId());
                String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
                byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
                String encryptedPassword = new String(EncryptionUtil.encrypt(user.getPassword(), originalKey));
                user.setIsActive(true);
                if ((convertEntityService.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
                    userMap.put(cloudStackConstants.CS_DOMAIN_ID, convertEntityService.getDomainById(user.getDomainId()).getUuid());
                } else {
                    userMap.put(CloudStackConstants.CS_DOMAIN_ID, departmentService.find(convertEntityService.getOwnerById(id)
                        .getDepartmentId()).getDomain().getUuid());
                }
                if (!(convertEntityService.getOwnerById(id).getType()).equals(User.UserType.USER)) {
                    dept = user.getDepartment().getUserName();
                } else {
                    dept = departmentService.find(convertEntityService.getOwnerById(id).getDepartmentId()).getUserName();
                }
                if ((convertEntityService.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
                    config.setUserServer();
                }
                else {
                    config.setServer(1L);
                }
                String cloudResponse = csUserService.createUser(dept, user.getEmail(),
                        user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(), cloudStackConstants.JSON,
                        userMap);
                JSONObject createUserResponseJSON = new JSONObject(cloudResponse).getJSONObject("createuserresponse");
                if (createUserResponseJSON.has(cloudStackConstants.CS_ERROR_CODE)) {
                    errors.addFieldError(cloudStackConstants.CS_USER_NAME, "user.already.exist.for.same.domain");
                    throw new ApplicationException(errors);
                }
                JSONObject userRes = createUserResponseJSON.getJSONObject(cloudStackConstants.CS_USER);
                user.setUuid((String) userRes.get(cloudStackConstants.CS_ID));
                user.setPassword(encryptedPassword);
                if (!(convertEntityService.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
                    user.setDomainId(convertEntityService.getOwnerById(id).getDomainId());
                }
                if ((convertEntityService.getOwnerById(id).getType()).equals(User.UserType.USER)) {
                    user.setDepartmentId(convertEntityService.getOwnerById(id).getDepartmentId());
                }
                user.setDomainId(user.getDomainId());
                user.setStatus(user.getStatus()
                        .valueOf(userRes.getString(CloudStackConstants.CS_STATE).toUpperCase()));
                user = userRepository.save(user);
                if (user.getProjectList() != null) {
                    for (Project project : user.getProjectList()) {
                        Project persistProject = projectService.find(project.getId());
                        users = persistProject.getUserList();
                        users.add(user);
                        persistProject.setUserList(users);
                        persistProject.setSyncFlag(false);
                        projectService.update(persistProject);
                    }
                }
                return user;
            }
        } else {
            return userRepository.save(user);
        }
    }

    /**
     * Check the user name already exist or not for same domain.
     *
     * @param errors already existing error list.
     * @param name name of the user.
     * @param domain domain object.
     * @param id user id.
     * @return errors.
     * @throws Exception unhandled errors.
     */
    private Errors validateName(Errors errors, String name, Domain domain, Long id) throws Exception {
        User user = userRepository.findByUserNameAndDomainAndActiveAndUserId(name, domain, true, id);
        if (user != null && user.getStatus() != User.Status.DELETED) {
            errors.addFieldError(cloudStackConstants.CS_USER_NAME, "user.already.exist.for.same.domain");
        }
        return errors;
    }

    @Override
    @PreAuthorize("hasPermission(#user.getSyncFlag(), 'EDIT_USER')")
    public User update(User user, Long id) throws Exception {
        if (user.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(cloudStackConstants.CS_USER, user);
            errors = validator.validateEntity(user, errors);
            errors = this.validateName(errors, user.getUserName(), user.getDomain(), user.getId());
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {

                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put(cloudStackConstants.CS_DOMAIN_ID, user.getDomain().getUuid());
                optional.put(cloudStackConstants.CS_USER_NAME, user.getUserName());
                if ((convertEntityService.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
                    config.setUserServer();
                }
                else {
                    config.setServer(1L);
                }
                csUserService.updateUser(user.getUuid(), optional, cloudStackConstants.JSON);
                if (user.getType() == User.UserType.DOMAIN_ADMIN) {
                    Domain domain = user.getDomain();
                    domain.setPortalUserName(user.getUserName());
                    domain.setSyncFlag(false);
                    domainService.update(domain);
                }
                return userRepository.save(user);
            }
        } else {
            return userRepository.save(user);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#user.getSyncFlag(), 'DELETE_USER')")
    public void delete(User user) throws Exception {
        if (user.getSyncFlag()) {
            config.setServer(1L);
            csUserService.deleteUser(user.getId().toString(), CloudStackConstants.JSON);
        } else {
            // async call delete.
            this.softDelete(user);
        }
    }

    @Override
    @PreAuthorize("hasPermission(null, 'DELETE_USER')")
    public void delete(Long id) throws Exception {
        User user = userRepository.findOne(id);
        config.setServer(1L);
        csUserService.deleteUser(user.getUuid(), cloudStackConstants.JSON);
        this.softDelete(user);
    }

    @Override
    public User find(Long id) throws Exception {
        return userRepository.findOne(id);
    }

    @Override
    public Page<User> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return userRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<User> findAll() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        if (user.getType().equals(User.UserType.DOMAIN_ADMIN)) {
            return userRepository.findAllByDomainIdAndSearchTextAndStatusCount(user.getDomainId(), "", User.Status.DELETED, true);
        } else if (user.getType().equals(User.UserType.USER)) {
            return userRepository.findAllByDomainIdAndSearchTextAndUserIdCount(user.getDomainId(), "", User.Status.DELETED, true, user.getId());
        }
        return (List<User>) userRepository.findAll();
    }

    @Override
    public List<User> findBySync() throws Exception {
         return (List<User>) userRepository.findAll();
    }

    @Override
    public List<User> findAllFromCSServerByDomain() throws Exception {
        List<User> userList = new ArrayList<User>();
        HashMap<String, String> userMap = new HashMap<String, String>();
        // userMap.put("domainid", domainUuid);
        userMap.put(cloudStackConstants.CS_LIST_ALL, "true");
        // 1. Get the list of users from CS server using CS connector
        config.setServer(1L);
        String response = csUserService.listUsers(userMap, cloudStackConstants.JSON);
        JSONArray userListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listusersresponse");
        if (responseObject.has(cloudStackConstants.CS_USER)) {
            userListJSON = responseObject.getJSONArray(cloudStackConstants.CS_USER);
            // 2. Iterate the json list, convert the single json entity to user
            for (int i = 0, size = userListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to User entity and Add
                // the converted User entity to list.
                User user = User.convert(userListJSON.getJSONObject(i));
                if(user.getType() == UserType.DOMAIN_ADMIN) {
                    Domain domain = domainService.findByUUIDAndIsActive(user.getTransDomainId());
                    domain.setEmail(user.getEmail());
                    domain.setLastName(user.getLastName());
                    domain.setPrimaryFirstName(user.getFirstName());
                    domain.setPortalUserName(user.getUserName());
                    domain.setSyncFlag(false);
                    domainService.save(domain);
                }
                if (!user.getUserName().equalsIgnoreCase("baremetal-system-account")) {
                    user.setDepartmentId((convertEntityService.getDepartment(user.getTransDepartment()).getId()));
                    user.setDomainId(convertEntityService.getDomainId(user.getTransDomainId()));
                    userList.add(user);
                }
            }
        }
        return userList;
    }

    @Override
    public User findByNameAndDomain(String query, Domain domain) throws Exception {
        return userRepository.findAllByActiveAndName(query, domain);
    }

    @Override
    public List<User> findByDepartment(Long departmentId) throws Exception {
        Department department = departmentService.find(departmentId);
        return userRepository.findByDepartment(department);
    }

    @Override
    public List<User> findByDepartmentWithLoggedUser(Long departmentId, Long userId) throws Exception {
        Department department = departmentService.find(departmentId);
        User user = userRepository.findOne(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                return userRepository.findByDepartment(department);
            } else {
                List<User> users = new ArrayList<User>();
                users.add(user);
                return users;
            }
        }
        return userRepository.findByDepartment(department);
    }

    @Override
    public User findByUserNameAndDomain(String userName, Domain domain) throws Exception {
        return userRepository.findByUserNameAndDomainAndActive(userName, domain, true);
    }

    @Override
    public User findByUser(String userName, String password, String domainName) throws Exception {
        String domain = domainName.trim();
        if (domain.equals("/")) {
            domain = "ROOT";
        }
        User user = userRepository.findByUser(userName.trim(), domainService.findByName(domain), true);
        if (user != null && password != null) {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
            String encryptedPassword = new String(EncryptionUtil.encrypt(password, originalKey));
            user.setPassword(encryptedPassword);
            userRepository.save(user);
        }
        return user;
    }

    @Override
    @PreAuthorize("hasPermission(#user.getSyncFlag(), 'DELETE_USER')")
    public User softDelete(User user,Long id) throws Exception {
        user.setIsActive(false);
        user.setStatus(User.Status.DELETED);
        if (user.getSyncFlag()) {
            // set server for finding value in configuration
            if ((convertEntityService.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
                config.setUserServer();
            }
            else {
                config.setServer(1L);
            }
            csUserService.deleteUser((user.getUuid()), cloudStackConstants.JSON);
            if (user.getType() == UserType.DOMAIN_ADMIN) {
                Role role = roleService.find(user.getRoleId());
                role.setStatus(Role.Status.DISABLED);
                role.setIsActive(false);
                role.setSyncFlag(false);
                roleService.save(role);
            }
        }
        return userRepository.save(user);
    }

    /**
     * Check the user CS error handling.
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
    public Page<User> findAllUserByDomain(PagingAndSorting pagingAndSorting, Long userId, Status status) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (user.getType().equals(User.UserType.DOMAIN_ADMIN)) {
            return userRepository.findAllByUserPanelAndDomainId(user.getDomainId(), "", status, pagingAndSorting.toPageRequest(), true);
        } else if (user.getType().equals(User.UserType.USER)) {
            return userRepository.findAllByUserPanelAndDomainIdAndUserId(user.getDomainId(), "", status, pagingAndSorting.toPageRequest(), true, user.getId());
        }
        return userRepository.findAllUserByStatus(pagingAndSorting.toPageRequest(), status);
    }

    @Override
    public List<User> findAllUserByDomain(Long userId) throws Exception {
        User user = userRepository.findOne(userId);
        return userRepository.findAllUserByDomain(user.getDomain());
    }

    @Override
    public List<User> findAllRootAdminUser() throws Exception {
        return userRepository.findAllRootAdminUser(UserType.ROOT_ADMIN);
    }

    @Override
    public List<User> findUsersByTypesAndActive(List<UserType> types, Boolean isActive) throws Exception {
        return (List<User>) userRepository.findUsersByTypesAndActive(types, isActive);
    }

    @Override
    public List<User> assignUserRoles(List<User> users) throws Exception {
        for (User user : users) {
            userRepository.save(user);
        }
        return users;
    }

    @Override
    public User findByUuIdAndIsActive(String uuid, Boolean isActive) throws Exception {
        return userRepository.findByUuIdAndIsActive(uuid, isActive);
    }

    @Override
    public List<User> findByRole(Long roleId, Boolean isActive) throws Exception {
        return userRepository.findByRole(roleId, isActive);
    }

    @Override
    public List<User> findAllByProject(Long projectId) throws Exception {
        Project project = projectService.find(projectId);
        List<User> projectUsers = project.getUserList();
        if (projectUsers.size() > 0) {
            return userRepository.findAllByDepartmentAndIsActive(true, project.getDepartmentId(), projectUsers);
        }
        return userRepository.findByDepartment(project.getDepartment());
    }

    @Override
    @PreAuthorize("hasPermission(null, 'ENABLE_USER')")
    public User enableUser(Long userId) throws Exception {
        Errors errors = null;
        User user = userRepository.findOne(userId);
        HashMap<String, String> optional = new HashMap<String, String>();
        config.setServer(1L);
        String csUserResponse = csUserService.enableUser(user.getUuid(), CloudStackConstants.JSON);
        JSONObject createComputeResponseJSON = new JSONObject(csUserResponse)
                .getJSONObject(ENABLE_USER);
        if (createComputeResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, createComputeResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        }
        JSONObject enableUserResponse = createComputeResponseJSON.getJSONObject(CloudStackConstants.CS_USER);
        user.setUuid((String) enableUserResponse.get(CloudStackConstants.CS_ID));
        user.setIsActive(true);
        user.setStatus(user.getStatus()
                .valueOf(enableUserResponse.getString(CloudStackConstants.CS_STATE).toUpperCase()));
        return userRepository.save(user);
    }

    @Override
    @PreAuthorize("hasPermission(null, 'DISABLE_USER')")
    public User disableUser(Long userId) throws Exception {
        Errors errors = null;
        User user = userRepository.findOne(userId);
        HashMap<String, String> optional = new HashMap<String, String>();
        config.setServer(1L);
        String csUserResponse = csUserService.disableUser(user.getUuid(), CloudStackConstants.JSON);
        JSONObject jobResponse = new JSONObject(csUserResponse)
                .getJSONObject(DISABLE_USER);
        if (jobResponse.has(CloudStackConstants.CS_JOB_ID)) {
            String csJobResponse = csUserService.associatedJobResult(jobResponse.getString(CloudStackConstants.CS_JOB_ID),
                    CloudStackConstants.JSON);
            JSONObject jobresult = new JSONObject(csJobResponse)
                    .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
            user.setStatus(Status.DISABLED);
        }
        return userRepository.save(user);
      }

    @Override
    @PreAuthorize("hasPermission(null, 'RESET_USER_PASSWORD')")
    public User updatePassword(User user) throws Exception {
        if (user.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(cloudStackConstants.CS_USER, user);
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(CS_UTF));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, CS_AES);
            if (user.getPassword() != null) {
                User validateUser = userRepository.findOne(user.getId());
                String validatePassword = new String(EncryptionUtil.encrypt(user.getPassword(), originalKey));
                if (!validateUser.getPassword().equals(validatePassword)) {
                    errors.addGlobalError("Old password didnot match. Please re-enter old password");
                }
            }
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put(cloudStackConstants.CS_PASSWORD, user.getConfirmPassword());
                config.setServer(1L);
                csUserService.updateUser(user.getUuid(), optional, cloudStackConstants.JSON);
                if (user.getType() == User.UserType.DOMAIN_ADMIN) {
                    Domain domain = user.getDomain();
                    domain.setPortalUserName(user.getUserName());
                    domain.setSyncFlag(false);
                    domainService.update(domain);
                }
                String encryptedPassword = new String(EncryptionUtil.encrypt(user.getConfirmPassword(), originalKey));
                user.setPassword(encryptedPassword);
            }
            sendEmailForPasswordUpdate(user);
            return userRepository.save(user);
        } else {
            user.setStatus(Status.ACTIVE);
            return userRepository.save(user);
        }
    }

    /**
     * Send an email for password updated user.
     *
     * @param user updated password details of user
     * @throws Exception if errors throws exception.
     */
    private void sendEmailForPasswordUpdate(User user) throws Exception {
        syncService.syncUpdateUserRole();
        EmailEvent emailEvent = new EmailEvent();
        emailEvent.setEntityUuid(user.getUuid());
        emailEvent.setResourceUuid(user.getUuid());
        emailEvent.setEvent(EventTypes.EVENT_USER_UPDATE);
        emailEvent.setEventType(EmailConstants.ACCOUNT);
        emailEvent.setEventDateTime(user.getUpdatedDateTime().toString());
        emailEvent.setUser(convertEntityService.getOwnerByUuid(user.getUuid()).toString());
        emailEvent.setSubject(EmailConstants.SUBJECT_ACCOUNT_PASSWORD);
        emailJobService.sendMessageToQueue(emailEvent);
    }

    @Override
    public User findByUserValidList(Long id) throws Exception {
        userRepository.findOne(id);
        User user = convertEntityService.getOwnerById(id);
        user.setApiKey(tokenDetails.getTokenDetails(cloudStackConstants.CS_API_KEY));
        user.setSecretKey(tokenDetails.getTokenDetails(cloudStackConstants.CS_SECRET_KEY));
        user.setPassword(null);
        return user;
    }

    @Override
    public Page<User> findAllByUserPanelAndDomainId(Long domainId, String searchText, PagingAndSorting pagingAndSorting, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.DOMAIN_ADMIN)) {
            domainId = user.getDomainId();
        }
        List<UserType> userType = new ArrayList<UserType>();
        Page<User> listUser = null;
        if (searchText.equals(UserType.DOMAIN_ADMIN.toString()) || searchText.equals(UserType.ROOT_ADMIN.toString())
                || searchText.equals(UserType.USER.toString())) {
            if (searchText.equals(UserType.DOMAIN_ADMIN.toString())) {
                userType.add(UserType.DOMAIN_ADMIN);
            } else if (searchText.equals(UserType.ROOT_ADMIN.toString())) {
                userType.add(UserType.ROOT_ADMIN);
            } else if (searchText.equals(UserType.USER.toString())) {
                userType.add(UserType.USER);
            }
            if (user.getType().equals(User.UserType.USER)) {
                listUser = userRepository.findAllByUserPanelAndDomainIdAndUserId(domainId, searchText, userType, User.Status.DELETED, pagingAndSorting.toPageRequest(), true, userId);
            } else {
                listUser = userRepository.findAllByUserPanelAndDomainId(domainId, searchText, userType, User.Status.DELETED, pagingAndSorting.toPageRequest(), true);
            }
        } else {
            if (user.getType().equals(User.UserType.USER)) {
                listUser = userRepository.findAllByUserPanelAndDomainIdAndUserId(domainId, searchText, User.Status.DELETED, pagingAndSorting.toPageRequest(), true, userId);
            } else {
                listUser = userRepository.findAllByUserPanelAndDomainId(domainId, searchText, User.Status.DELETED, pagingAndSorting.toPageRequest(), true);
            }
        }
        return listUser;
    }

    @Override
    public Page<User> findAllByDomainId(Long domainId, String searchText, PagingAndSorting pagingAndSorting, Long userId) throws Exception {

        List<UserType> userType = new ArrayList<UserType>();
        Page<User> listUser = null;
        if (searchText.equals(UserType.DOMAIN_ADMIN.toString()) || searchText.equals(UserType.ROOT_ADMIN.toString())
                || searchText.equals(UserType.USER.toString())) {
            if (searchText.equals(UserType.DOMAIN_ADMIN.toString())) {
                userType.add(UserType.DOMAIN_ADMIN);
            } else if (searchText.equals(UserType.ROOT_ADMIN.toString())) {
                userType.add(UserType.ROOT_ADMIN);
            } else if (searchText.equals(UserType.USER.toString())) {
                userType.add(UserType.USER);
            }
            listUser = userRepository.findAllByDomainId(domainId, searchText, true, userType, pagingAndSorting.toPageRequest());
        } else {
            listUser = userRepository.findAllByDomainId(domainId, searchText, pagingAndSorting.toPageRequest(), true);
        }
        return listUser;
    }

    @Override
    public List<User> findAllByDomainDepartmentIdUserTypeAndIsActive(Long domainId, Boolean isActive,Long departmentId, UserType domainAdmin) throws Exception {
        return (List<User>) userRepository.findByDomainAndIsActive(domainId, true ,departmentId, UserType.DOMAIN_ADMIN);
    }

    @Override
    public User save(User user) throws Exception {
        if (!user.getSyncFlag()) {
            return userRepository.save(user);
        }
        return user;
    }

    @Override
    public User findByUuId(String uuid) throws Exception {
        return userRepository.findByUuId(uuid);
    }

    @Override
    public User findAllByUserTypeAndIsActive(Boolean isActive, UserType rootAdmin) {
        return userRepository.findAllByUserTypeAndIsActive(rootAdmin, isActive);
    }

    @Override
    public Page<User> findAllByActive(PagingAndSorting pagingAndSorting, Long id) throws Exception {
        User user = convertEntityService.getOwnerById(id);
        if (user.getType().equals(User.UserType.DOMAIN_ADMIN)) {
            return userRepository.findAllByUserPanelAndDomainId(user.getDomainId(), "", User.Status.DELETED, pagingAndSorting.toPageRequest(), true);
        } else if (user.getType().equals(User.UserType.USER)) {
            return userRepository.findAllByUserPanelAndDomainIdAndUserId(user.getDomainId(), "", User.Status.DELETED, pagingAndSorting.toPageRequest(), true, user.getId());
        }
        return userRepository.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public User updateSuspended(User user) throws Exception {
        User users = userRepository.findOne(user.getId());
        users.setStatus(User.Status.SUSPENDED);
        // Update the vm status to stopped while suspending the account
        if (user.getStatus() == User.Status.SUSPENDED) {
            virtualMachineService.updateVmToStoppedByOwnerAndStatus(users, VmInstance.Status.RUNNING);
        }
        return userRepository.save(users);
    }

    @Override
    public User findByUserNameAndActive(String userName, Boolean isActive) {
        return userRepository.findByUserNameAndActive(userName, isActive);
    }

    @Override
    public String findByUserSessionDetails(Long id) throws Exception {
       User user = userRepository.findOne(id);
       Department department = departmentService.find(user.getDepartment().getId());
       LoginHistory loginHistory = loginHistoryService.findByUserIdAndAlreadyLogin(user.getId(), true);
       Role role = roleService.findWithPermissionsByNameDepartmentAndIsActive(user.getRole().getName(), department.getId(), true);
       JSONObject jsonObject = new JSONObject();
       try {
           TimeZone timeZone = Calendar.getInstance().getTimeZone();
           jsonObject.put(CloudStackConstants.CS_ID, user.getId());
           jsonObject.put(RolePrincipal.LOGIN_USER_NAME, user.getUserName());
           jsonObject.put(RolePrincipal.LOGIN_USER_TYPE, user.getType());
           jsonObject.put(RolePrincipal.LOGIN_USER_DOMAIN_NAME, user.getDomain().getName());
           jsonObject.put(RolePrincipal.LOGIN_USER_DOMAIN_ABBREVIATION_NAME, user.getDomain().getCompanyNameAbbreviation());
           jsonObject.put(RolePrincipal.LOGIN_USER_DOMAIN_ID, user.getDomain().getId());
           jsonObject.put(RolePrincipal.LOGIN_USER_DEPARTMENT_ID, user.getDepartment().getId());
           jsonObject.put(RolePrincipal.LOGIN_USER_STATUS, user.getStatus());
           jsonObject.put(RolePrincipal.LOGIN_TIME, DateConvertUtil.getTimestamp());
           jsonObject.put(RolePrincipal.TIME_ZONE, timeZone.getID());
           jsonObject.put(RolePrincipal.REMEMBER_ME, loginHistory.getRememberMe());
           JSONArray jsonArray = new JSONArray();
           Map<String, Object> hashList = new HashMap<String, Object>();
           for (int i = 0; i < role.getPermissionList().size(); i++) {
               Permission permission = role.getPermissionList().get(i);
               hashList.put(CloudStackConstants.CS_ID, permission.getId());
               hashList.put(RolePrincipal.ROLE_ACTION, permission.getAction());
               hashList.put(RolePrincipal.ROLE_ACTION_KEY, permission.getActionKey());
               hashList.put(RolePrincipal.ROLE_DESCRIPTION, permission.getDescription());
               hashList.put(RolePrincipal.ROLE_STATUS, permission.getIsActive());
               jsonArray.put(hashList);
               hashList = new HashMap<String, Object>();
           }
           jsonObject.put(RolePrincipal.ROLE_PERMISSION_LIST, jsonArray);
       } catch (Exception e) {
           e.getMessage();
       }
       return jsonObject.toString();
    }

    @Override
    public List<User> findByRootAdminUser() throws Exception {
        return userRepository.findByRootAdminUser(UserType.ROOT_ADMIN,true);
    }

    @Override
    public List<User> findAllByDomain(Domain domain) throws Exception {
        return userRepository.findAllUserByDomain(domain);
    }

    @Override
    public List<User> findBySearchCount(Long domainId, String searchText, Long userId, String flag) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.DOMAIN_ADMIN)) {
            domainId = user.getDomainId();
        }
        List<UserType> userType = new ArrayList<UserType>();
        List<User> listUser = null;
        if (searchText.equals(UserType.DOMAIN_ADMIN.toString()) || searchText.equals(UserType.ROOT_ADMIN.toString())
                || searchText.equals(UserType.USER.toString())) {
            if (searchText.equals(UserType.DOMAIN_ADMIN.toString())) {
                userType.add(UserType.DOMAIN_ADMIN);
            } else if (searchText.equals(UserType.ROOT_ADMIN.toString())) {
                userType.add(UserType.ROOT_ADMIN);
            } else if (searchText.equals(UserType.USER.toString())) {
                userType.add(UserType.USER);
            }
            if (user.getType().equals(User.UserType.USER)) {
                listUser = userRepository.findAllByDomainIdAndSearchTextAndUserIdAndUserTypeCount(domainId, searchText, userType, User.Status.DELETED, true, userId);
            } else {
                if (flag.equals("pandaAdminPanel")) {
                    listUser = userRepository.findAllByDomainIdAndUserTypeAndStatusWithDeletedCount(domainId, searchText, userType, true);
                } else {
                    listUser = userRepository.findAllByDomainIdAndUserTypeAndStatusCount(domainId, searchText, userType, User.Status.DELETED, true);
                }
            }
        } else {
            if (user.getType().equals(User.UserType.USER)) {
                listUser = userRepository.findAllByDomainIdAndSearchTextAndUserIdCount(domainId, searchText, User.Status.DELETED, true, userId);
            } else {
                if (flag.equals("pandaAdminPanel")) {
                    listUser = userRepository.findAllByDomainIdAndSearchTextAndStatusWithDeletedCount(domainId, searchText, true);
                } else {
                    listUser = userRepository.findAllByDomainIdAndSearchTextAndStatusCount(domainId, searchText, User.Status.DELETED, true);
                }
            }
        }
        return listUser;
    }

    @Override
    public User update(User user) throws Exception {
        if (!user.getSyncFlag()) {
            return userRepository.save(user);
        }
        return user;
    }

    @Override
    public User softDelete(User user) throws Exception {
        if (!user.getSyncFlag()) {
            return userRepository.save(user);
        }
        return user;
    }
}
