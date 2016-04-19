package ck.panda.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.LoginSecurityTrack;
import ck.panda.domain.repository.jpa.LoginSecurityTrackRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/** Login security track Service implementation class. */
@Service
public class LoginSecurityTrackServiceImpl implements LoginSecurityTrackService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Login security track repository reference. */
    @Autowired
    private LoginSecurityTrackRepository loginSecurityTrackRepository;

    /** Login security track string literal. */
    public static final String LOGIN_SECURITY_TRACK = "loginSecurityTrack";

    @Override
    public LoginSecurityTrack save(LoginSecurityTrack loginSecurityTrack) throws Exception {
        Errors errors = validator.rejectIfNullEntity(LOGIN_SECURITY_TRACK, loginSecurityTrack);
        errors = validator.validateEntity(loginSecurityTrack, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            loginSecurityTrack.setIsActive(true);
            return loginSecurityTrackRepository.save(loginSecurityTrack);
        }
    }

    @Override
    public LoginSecurityTrack update(LoginSecurityTrack loginSecurityTrack) throws Exception {
        Errors errors = validator.rejectIfNullEntity(LOGIN_SECURITY_TRACK, loginSecurityTrack);
        errors = validator.validateEntity(loginSecurityTrack, errors);
        return loginSecurityTrackRepository.save(loginSecurityTrack);
    }

    @Override
    public void delete(LoginSecurityTrack loginSecurityTrack) throws Exception {
        loginSecurityTrackRepository.delete(loginSecurityTrack);
    }

    @Override
    public void delete(Long id) throws Exception {
        loginSecurityTrackRepository.delete(id);
    }

    @Override
    public Page<LoginSecurityTrack> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return loginSecurityTrackRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<LoginSecurityTrack> findAll() throws Exception {
        return (List<LoginSecurityTrack>) loginSecurityTrackRepository.findAll();
    }

    @Override
    public LoginSecurityTrack find(Long id) throws Exception {
        return loginSecurityTrackRepository.findOne(id);
    }

    @Override
    public LoginSecurityTrack findByIsActive(Boolean isActive) throws Exception {
        return loginSecurityTrackRepository.findByIsActive(true);
    }

    @Override
    public LoginSecurityTrack findByIpAddress(String hostAddress) {
        return loginSecurityTrackRepository.findByIpAddress(hostAddress);
    }
}
