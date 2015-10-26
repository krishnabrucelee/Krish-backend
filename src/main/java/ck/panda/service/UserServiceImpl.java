package ck.panda.service;

import java.util.Base64;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.util.AppValidator;
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

    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    @Override
    public User save(User user) throws Exception {

        Errors errors = validator.rejectIfNullEntity("user", user);
        errors = validator.validateEntity(user, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
        	String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
        	byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
        	SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
        	String encryptedPassword = new String(EncryptionUtil.encrypt(user.getUserName(), originalKey));
        	user.setPassword(encryptedPassword);
        	user.setIsActive(true);
            return userRepository.save(user);
        }
    }

    @Override
    public User update(User user) throws Exception {
    	  Errors errors = validator.rejectIfNullEntity("user", user);
          errors = validator.validateEntity(user, errors);

          if (errors.hasErrors()) {
              throw new ApplicationException(errors);
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
        return null;
    }

}
