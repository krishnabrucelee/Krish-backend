package ck.panda.service;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.domain.entity.LoginHistory;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.LoginHistoryRepository;
import ck.panda.util.DateConvertUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Login history service implementation
 */
public class LoginHistoryServiceImpl implements LoginHistoryService {

    /** Login History repository reference. */
    @Autowired
    private LoginHistoryRepository loginRepo;

    /** User Service reference. */
    @Autowired
    private UserService userService;

    /** General Configuration service attribute. */
    @Autowired
    private GeneralConfigurationService generalConfigurationService;

    @Override
    public LoginHistory save(LoginHistory t) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LoginHistory update(LoginHistory t) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void delete(LoginHistory t) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Long id) throws Exception {
        // TODO Auto-generated method stub

    }

    @Override
    public LoginHistory find(Long id) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<LoginHistory> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LoginHistory> findAll() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LoginHistory saveLoginDetails(String userName, String password, String domain, String rememberMe, String loginToken)
            throws Exception {
        if (domain.equals("BACKEND_ADMIN")) {
            domain = "ROOT";
        }
        User user = userService.findByUser(userName, password, domain);
        if (domain.equals("ROOT") && user == null) {
            LoginHistory loginDetails = new LoginHistory();
            loginDetails.setIsAlreadyLogin(true);
            loginDetails.setRememberMe(rememberMe);
            loginDetails.setUserId(0L);
            loginDetails.setLoginToken(loginToken);
            loginDetails.setRememberMeExpireDate(DateConvertUtil.getTimestamp());
            return loginRepo.save(loginDetails);
        } else {
            LoginHistory loginDetails = new LoginHistory();
            LoginHistory loginHistoryId = findByUserId(user.getId());
            GeneralConfiguration generalConfiguration = generalConfigurationService.findByIsActive(true);
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.add(Calendar.DATE, generalConfiguration.getRememberMeExpiredDays());
            Long currentTimeStamp = cal.getTimeInMillis() / 1000;
            if (loginHistoryId == null) {
                loginDetails.setIsAlreadyLogin(true);
                loginDetails.setRememberMe(rememberMe);
                loginDetails.setUserId(user.getId());
                loginDetails.setLoginToken(loginToken);
                loginDetails.setRememberMeExpireDate(currentTimeStamp);
                return loginRepo.save(loginDetails);
            } else {
                loginHistoryId.setIsAlreadyLogin(true);
                loginHistoryId.setRememberMeExpireDate(currentTimeStamp);
                loginHistoryId.setRememberMe(rememberMe);
                loginHistoryId.setLoginToken(loginToken);
                return loginRepo.save(loginHistoryId);
            }
        }
    }

    @Override
    public LoginHistory findByUserIdAndAlreadyLogin(Long userId, Boolean isAlreadyLogin) {
        return loginRepo.findByUserIdAndAlreadyLogin(userId, isAlreadyLogin);
    }

    @Override
    public LoginHistory findByUserId(Long userId) {
        return loginRepo.findByUserId(userId);
    }

    @Override
    public LoginHistory findByLoginToken(String userId) {
        return loginRepo.findByLoginToken(userId);
    }

    @Override
    public LoginHistory updateLogoutStatus(Long id) throws Exception {
        LoginHistory loginHistory = loginRepo.findByUserId(id);
        loginHistory.setIsAlreadyLogin(false);
        loginHistory.setRememberMe("false");
        loginHistory.setRememberMeExpireDate(null);
        return loginRepo.save(loginHistory);
    }
}
