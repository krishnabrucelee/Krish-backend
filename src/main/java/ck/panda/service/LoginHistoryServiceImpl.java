package ck.panda.service;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.GeneralConfiguration;
import ck.panda.domain.entity.LoginHistory;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.LoginHistoryRepository;
import ck.panda.util.DateConvertUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Login history service implementation.
 *
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

    /** Back end admin. */
    public static final String BACKEND_ADMIN = "BACKEND_ADMIN";

    /** Root domain. */
    public static final String ROOT = "ROOT";

    /** Time zone. */
    public static final String UTC = "UTC";

    /** Cookie time out. */
    public static final String COOKIE_TIME_OUT = "COOKIE_TIME_OUT";

    @Override
    public LoginHistory save(LoginHistory loginHistory) throws Exception {
        return loginRepo.save(loginHistory);
    }

    @Override
    public LoginHistory update(LoginHistory loginHistory) throws Exception {
        return loginRepo.save(loginHistory);
    }

    @Override
    public void delete(LoginHistory loginHistory) throws Exception {
        loginRepo.delete(loginHistory);
    }

    @Override
    public void delete(Long id) throws Exception {
        loginRepo.delete(id);
    }

    @Override
    public LoginHistory find(Long id) throws Exception {
        return loginRepo.findOne(id);
    }

    @Override
    public Page<LoginHistory> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return loginRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<LoginHistory> findAll() throws Exception {
        return (List<LoginHistory>) loginRepo.findAll();
    }

    @Override
    public LoginHistory saveLoginDetails(String userName, String password, String domain, String rememberMe, String loginToken)
            throws Exception {
        if (domain.equals(BACKEND_ADMIN)) {
            domain = ROOT;
        }
        User user = userService.findByUser(userName, password, domain);
        if (domain.equals(ROOT) && user == null) {
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
            cal.setTimeZone(TimeZone.getTimeZone(UTC));
            Long currentTimeStamp = cal.getTimeInMillis() / 1000;
            cal.add(Calendar.DATE, generalConfiguration.getRememberMeExpiredDays());
            Long expiryTimeStamp = cal.getTimeInMillis() / 1000;
            if (loginHistoryId == null) {
                loginDetails.setIsAlreadyLogin(true);
                loginDetails.setRememberMe(rememberMe);
                loginDetails.setUserId(user.getId());
                loginDetails.setLoginToken(loginToken);
                loginDetails.setRememberMeExpireDate(expiryTimeStamp);
                loginDetails.setSessionExpireTime(currentTimeStamp + (generalConfiguration.getSessionTime() * 60));
                return loginRepo.save(loginDetails);
            } else {
                loginHistoryId.setIsAlreadyLogin(true);
                loginHistoryId.setRememberMeExpireDate(expiryTimeStamp);
                loginHistoryId.setRememberMe(rememberMe);
                loginHistoryId.setLoginToken(loginToken);
                loginHistoryId.setSessionExpireTime(currentTimeStamp + (generalConfiguration.getSessionTime() * 60));
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
    public LoginHistory updateLogoutStatus(Long id, String type) throws Exception {
        LoginHistory loginHistory = loginRepo.findByUserId(id);
        Long currentTimeStamp;
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone(UTC));
        currentTimeStamp = cal.getTimeInMillis() / 1000;
        if (type.equals(COOKIE_TIME_OUT) && loginHistory.getSessionExpireTime() > currentTimeStamp
                && loginHistory.getRememberMe().equals(CloudStackConstants.STATUS_INACTIVE)) {
            return loginHistory;
        } else {
            loginHistory.setIsAlreadyLogin(false);
            loginHistory.setRememberMe(CloudStackConstants.STATUS_INACTIVE);
            loginHistory.setRememberMeExpireDate(null);
            return loginRepo.save(loginHistory);
        }
    }
}
