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
import org.springframework.stereotype.Service;
import com.google.common.base.Optional;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.Type;
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

    /** Autowired configutill object. */
    @Autowired
    private ConfigUtil configServer;

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
    public User save(User user) throws Exception {

        if (user.getSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity("user", user);
        errors = validator.validateEntity(user, errors);
        errors = this.validateName(errors, user.getUserName(), user.getDomain());
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            user.setType(User.Type.USER);
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            String encryptedPassword = new String(EncryptionUtil.encrypt(user.getPassword(), originalKey));
            user.setIsActive(true);
            csUserService.setServer(configServer.setServer(1L));
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
            return userRepository.save(user);
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
     * @return errors.
     * @throws Exception
     */
    private Errors validateName(Errors errors, String name, Domain domain) throws Exception {
        if (userRepository.findByUserNameAndDomain(name, domain) != null) {
            errors.addFieldError("username", "user.already.exist.for.same.domain");
        }
        return errors;
    }

    @Override
    public User update(User user) throws Exception {
        if (user.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("user", user);
            errors = validator.validateEntity(user, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
              configServer.setServer(1L);
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
            return userRepository.save(user);
        }
    }

    @Override
    public void delete(User user) throws Exception {
        if (user.getSyncFlag() == true) {
            configServer.setServer(1L);
            csUserService.deleteUser(user.getId().toString(), "json");
            userRepository.delete(user);
        } else {
                userRepository.delete(user);
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        User user = userRepository.findOne(id);
        configServer.setServer(1L);
        csUserService.deleteUser(user.getUuid(), "json");
        userRepository.delete(id);
    }

    @Override
    public User find(Long id) throws Exception {
        return null;
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
                user.setAccountId(convertEntityService.getAccountIdByUsernameAndDomain(user.getTransAccount(),convertEntityService.getDomain(user.getTransDomainId())));
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
    public User findByUser(Optional<String> userName, Optional<String> password, Optional<String> domainName) throws Exception {
        String domain = domainName.get().trim();
        if (domain.equals("/")) {
            domain = "ROOT";
        }
        User user = userRepository.findByUser(userName.get().trim(), domainRepository.findByName(domain));
        if (user != null) {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            String encryptedPassword = new String(EncryptionUtil.encrypt(password.get(), originalKey));
            user.setPassword(encryptedPassword);
            userRepository.save(user);
        }
        return user;
    }

    @Override
    public User softDelete(User user) throws Exception {
        user.setIsActive(false);
        user.setStatus(User.Status.DELETED);
        return userRepository.save(user);
    }

    @Override
    public List<User> findByAccountId(Long accountId) throws Exception {
        return userRepository.findByAccountId(accountId);
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
            return userRepository.findAllUserByDomain(pagingAndSorting.toPageRequest(), domain);
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
        return userRepository.findAllRootAdminUser(Type.ROOT_ADMIN);
    }

}
