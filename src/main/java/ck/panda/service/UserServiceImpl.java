package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.Status;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/** UserService implementation class. */
@Service
public class UserServiceImpl implements UserService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Autowired user repository object.  */
    @Autowired
    private UserRepository userRepository;

    /** Autowired CloudStackUserService object. */
    @Autowired
    private CloudStackUserService csUserService;

    /** Inject departmentService business logic. */
    @Autowired
    private DepartmentService departmentService;

    /** Inject Account Service business logic. */
    @Autowired
    private AccountService accountService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Inject domain service business logic. */
    @Autowired
    private DomainRepository domainRepository;

    /** Autowired TokenDetails */
    @Autowired
    private TokenDetails tokenDetails;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    @Override
    @PreAuthorize("hasPermission(#user.getSyncFlag(), 'CREATE_USER')")
    public User save(User user) throws Exception {
        if (user.getSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity("user", user);
        errors = validator.validateEntity(user, errors);
        errors = this.validateName(errors, user.getUserName(), user.getDomain());
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            user.setType(User.UserType.USER);
            user.setStatus(Status.ACTIVE);
            user.setRoleId(user.getRole().getId());
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            String encryptedPassword = new String(EncryptionUtil.encrypt(user.getPassword(), originalKey));
            user.setIsActive(true);
            config.setServer(1L);
            HashMap<String, String> userMap = new HashMap<String, String>();
            userMap.put("domainid", user.getDomain().getUuid());
            String cloudResponse = csUserService.createUser(user.getDepartment().getUserName(),
                    user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(), "json", userMap);
            JSONObject createUserResponseJSON = new JSONObject(cloudResponse).getJSONObject("createuserresponse");
                if (createUserResponseJSON.has("errorcode")) {
                errors.addFieldError("username", "user.already.exist.for.same.domain");
                throw new ApplicationException(errors);
            }
            JSONObject userRes = createUserResponseJSON.getJSONObject("user");
            user.setUuid((String) userRes.get("id"));
            user.setPassword(encryptedPassword);
            user.setDomainId(user.getDomain().getId());
            return userRepository.save(user);
        }
        } else {
        	user.setStatus(Status.ACTIVE);
            return userRepository.save(user);
        }
    }

    /**
     * Check the user name already exist or not for same domain.
     *
     * @param errors already existing error list.
     * @param name name of the user.
     * @param domain domain object.
     * @return errors.
     * @throws Exception
     */
    private Errors validateName(Errors errors, String name, Domain domain) throws Exception {
    	User user = userRepository.findByUserNameAndDomain(name, domain);
        if (user != null && user.getStatus() != User.Status.DELETED) {
            errors.addFieldError("username", "user.already.exist.for.same.domain");
        }
        return errors;
    }

    @Override
    @PreAuthorize("hasPermission(#user.getSyncFlag(), 'EDIT_USER')")
    public User update(User user) throws Exception {
        if (user.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("user", user);
            errors = validator.validateEntity(user, errors);
            errors = this.validateName(errors, user.getUserName(), user.getDomain());
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
              config.setServer(1L);
              HashMap<String, String> optional = new HashMap<String, String>();
              optional.put("domainid", user.getDomain().getUuid());
              optional.put("username", user.getUserName());
              optional.put("email", user.getEmail());
              optional.put("firstname", user.getFirstName());
              optional.put("lastname", user.getLastName());
              optional.put("password", user.getPassword());
              csUserService.updateUser(user.getUuid(), optional,"json");
              String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
              byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
              SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
              String encryptedPassword = new String(EncryptionUtil.encrypt(user.getPassword(), originalKey));
              user.setPassword(encryptedPassword);
              return userRepository.save(user);
          }
        } else {
        	user.setStatus(Status.ACTIVE);
            return userRepository.save(user);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#user.getSyncFlag(), 'DELETE_USER')")
    public void delete(User user) throws Exception {
        if (user.getSyncFlag() == true) {
            config.setServer(1L);
            csUserService.deleteUser(user.getId().toString(), "json");
            this.softDelete(user);
        } else {
            this.softDelete(user);
        }
    }

    @Override
    @PreAuthorize("hasPermission(null, 'DELETE_USER')")
    public void delete(Long id) throws Exception {
        User user = userRepository.findOne(id);
        config.setServer(1L);
        csUserService.deleteUser(user.getUuid(), "json");
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
        return (List<User>) userRepository.findAll();
    }

    @Override
    public List<User> findAllFromCSServerByDomain() throws Exception {
        List<User> userList = new ArrayList<User>();
        HashMap<String, String> userMap = new HashMap<String, String>();
        // userMap.put("domainid", domainUuid);
        userMap.put("listall", "true");
        // 1. Get the list of users from CS server using CS connector
        String response = csUserService.listUsers(userMap, "json");
        JSONArray userListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listusersresponse");
        if (responseObject.has("user")) {
            userListJSON = responseObject.getJSONArray("user");
            // 2. Iterate the json list, convert the single json entity to user
            for (int i = 0, size = userListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to User entity and Add
                // the converted User entity to list

                User user = User.convert(userListJSON.getJSONObject(i));
                user.setDepartment(convertEntityService.getDepartment(user.getTransDepartment()));
                user.setDomainId(convertEntityService.getDomainId(user.getTransDomainId()));
                userList.add(user);
            }
        }
        return userList;
    }

    @Override
    public List<User> findByName(String query) throws Exception {
        return userRepository.findAllByActive(query);
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
    public User findByUserNameAndDomain(String userName, Domain domain) throws Exception {
        return userRepository.findByUserNameAndDomain(userName, domain);
    }

    @Override
    public User findByUser(String userName, String password, String domainName) throws Exception {
        String domain = domainName.trim();
        if (domain.equals("/")) {
            domain = "ROOT";
        }
        User user = userRepository.findByUser(userName.trim(), domainRepository.findByName(domain));
        if (user != null && password != null) {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            String encryptedPassword = new String(EncryptionUtil.encrypt(password, originalKey));
            user.setPassword(encryptedPassword);
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public User softDelete(User user) throws Exception {
        user.setIsActive(false);
        user.setStatus(User.Status.DELETED);

        // set server for finding value in configuration
        config.setUserServer();
        csUserService.deleteUser((user.getUuid()), "json");
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
    public Page<User> findAllUserByDomain(PagingAndSorting pagingAndSorting) throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            return userRepository.findAllUserByDomain(pagingAndSorting.toPageRequest(), domain, true);
        }
        return userRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<User> findAllUserByDomain() throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        return userRepository.findAllUserByDomain(domain);
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

}
