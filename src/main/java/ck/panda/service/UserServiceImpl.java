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
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAccountService;
import ck.panda.util.ConfigUtil;
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

    /** Autowired cloudstackaccountservice object. */
    @Autowired
    private CloudStackAccountService csAccountService;

    /** Autowired configutill object. */
    @Autowired
    private ConfigUtil configServer;

    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    @Override
    public User save(User user) throws Exception {

    	if(user.getSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity("user", user);
        errors = validator.validateEntity(user, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            String encryptedPassword = new String(EncryptionUtil.encrypt(user.getPassword(), originalKey));
            user.setPassword(encryptedPassword);
            user.setIsActive(true);
            csAccountService.setServer(configServer.setServer(1L));
            HashMap<String, String> userMap = new HashMap<String, String>();
            csAccountService.createAccount(String.valueOf(user.getType().ordinal()),
                    user.getEmail(), user.getFirstName(), user.getLastName(), user.getUserName(), user.getPassword(), "json", userMap);
            return userRepository.save(user);
        }
    	} else {
    		return userRepository.save(user);
    	}
    }

    @Override
    public User update(User user) throws Exception {
    	if(user.getSyncFlag()) {
          Errors errors = validator.rejectIfNullEntity("user", user);
          errors = validator.validateEntity(user, errors);

          if (errors.hasErrors()) {
              throw new ApplicationException(errors);
          } else {
              return userRepository.save(user);
          }
    	} else {
    		return userRepository.save(user);
    	}
    }

    @Override
    public void delete(User t) throws Exception {

    }

    @Override
    public void delete(Long id) throws Exception {
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
          String response = csAccountService.listAccounts("json", userMap);
          JSONArray userListJSON = new JSONObject(response).getJSONObject("listaccountsresponse")
                  .getJSONArray("account");
          // 2. Iterate the json list, convert the single json entity to user
          for (int i = 0, size = userListJSON.length(); i < size; i++) {
              // 2.1 Call convert by passing JSONObject to User entity and Add
              // the converted User entity to list
              userList.add(User.convert(userListJSON.getJSONObject(i)));
          }
          return userList;
      }

	@Override
	public List<User> findByName(String query) throws Exception {
		return userRepository.findAllByActive(query);
	}

}
