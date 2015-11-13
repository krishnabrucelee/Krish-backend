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
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.ConvertUtil;
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

    /** Autowired configutill object. */
    @Autowired
    private ConfigUtil configServer;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    @Override
    public User save(User user) throws Exception {

        if(user.getSyncFlag()) {
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
            user.setPassword(encryptedPassword);
            user.setIsActive(true);
            csUserService.setServer(configServer.setServer(1L));
            HashMap<String, String> userMap = new HashMap<String, String>();
            String cloudResponse = csUserService.createUser(user.getDepartment().getUserName(),
                    user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(), "json", userMap);
            JSONObject createUserResponseJSON = new JSONObject(cloudResponse).getJSONObject("createuserresponse")
                    .getJSONObject("user");
            user.setUuid((String) createUserResponseJSON.get("id"));
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
        if(user.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("user", user);
            errors = validator.validateEntity(user, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
        	  configServer.setServer(1L);
              HashMap<String, String> optional = new HashMap<String, String>();
              optional.put("username", user.getUserName());
              optional.put("email", user.getEmail());
              optional.put("firstname", user.getFirstName());
              optional.put("lastname", user.getLastName());
              optional.put("password", user.getPassword());
        	  csUserService.updateUser(user.getUuid(), optional,"json");
              return userRepository.save(user);
          }
        } else {
            return userRepository.save(user);
        }
    }

    @Override
    public void delete(User user) throws Exception {
    	if(user.getSyncFlag() == true) {
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
    public List<User> findAllFromCSServer() throws Exception {
          List<User> userList = new ArrayList<User>();
          HashMap<String, String> userMap = new HashMap<String, String>();

          // 1. Get the list of users from CS server using CS connector
          String response = csUserService.listUsers(userMap,"json");
          JSONArray userListJSON = new JSONObject(response).getJSONObject("listusersresponse")
                  .getJSONArray("user");
          // 2. Iterate the json list, convert the single json entity to user
          for (int i = 0, size = userListJSON.length(); i < size; i++) {
              // 2.1 Call convert by passing JSONObject to User entity and Add
              // the converted User entity to list
              userList.add(User.convert(userListJSON.getJSONObject(i), entity));
          }
          return userList;
      }

    @Override
    public List<User> findByName(String query) throws Exception {
        return userRepository.findAllByActive(query);
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

}