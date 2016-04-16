package ck.panda.service;

import ck.panda.domain.entity.LoginHistory;
import ck.panda.util.domain.CRUDService;

/**
 * Login history service
 */
public interface LoginHistoryService extends CRUDService<LoginHistory>{

    /**
     * Set the login details history to database.
     *
     * @param userName user name of user
     * @param password password of user
     * @param domain domain of user
     * @param rememberMe true of false
     * @param loginToken login token
     * @return LoginHistory
     * @throws Exception unhandled exception
     */
    LoginHistory saveLoginDetails(String userName, String password, String domain, String rememberMe, String loginToken) throws Exception;

    /**
     * find by login token and is already login.
     *
     * @param userId user id
     * @param isAlreadyLogin is already login
     * @return Login history
     */
    LoginHistory findByUserIdAndAlreadyLogin(Long userId, Boolean isAlreadyLogin);

    /**
     * find by login token and is already login.
     *
     * @param userId user id
     * @return Login history
     */
    LoginHistory findByUserId(Long userId);

    /**
     * find by login token.
     *
     * @param userId user id
     * @return Login history
     */
    LoginHistory findByLoginToken(String userId);

    /**
     * Update the logout status.
     *
     * @param id user id
     * @return Login history
     */
    void updateLogoutStatus(Long id);

}
