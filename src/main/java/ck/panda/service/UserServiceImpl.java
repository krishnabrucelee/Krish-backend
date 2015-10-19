package ck.panda.service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.UserRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 *
 * UserService implementation class.
 */
@Service
public class UserServiceImpl implements UserService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Autowired user repository object.  */
    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(User user) throws Exception {

        Errors errors = validator.rejectIfNullEntity("user", user);
        errors = validator.validateEntity(user, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(user.getUserName().getBytes(),0, user.getUserName().length());
            String hashedPass = new BigInteger(1,messageDigest.digest()).toString(16);
            user.setPassword(hashedPass);
            user.setIsActive(true);
            return userRepository.save(user);
        }
    }

    @Override
    public User update(User t) throws Exception {
        return null;
    }

    @Override
    public void delete(User t) throws Exception {

    }

    @Override
    public void delete(Long id) throws Exception {

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
