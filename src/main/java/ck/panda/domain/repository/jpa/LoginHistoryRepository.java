package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import ck.panda.domain.entity.LoginHistory;

/**
 * Login history repository
 */
public interface LoginHistoryRepository extends PagingAndSortingRepository<LoginHistory, Long>{

    /**
     * Get Login History by User id and already login.
     *
     * @param userId user id
     * @param isAlreadyLogin already login
     * @return LoginHistory
     */
    @Query(value = "SELECT login FROM LoginHistory login WHERE login.userId = :userId AND login.isAlreadyLogin = :isAlreadyLogin")
    LoginHistory findByUserIdAndAlreadyLogin(@Param("userId") Long userId, @Param("isAlreadyLogin") Boolean isAlreadyLogin);

    /**
     * find by login token.
     *
     * @param loginToken id
     * @return Login history
     */
    @Query(value = "SELECT login FROM LoginHistory login WHERE login.loginToken = :loginToken")
    LoginHistory findByLoginToken(@Param("loginToken") String loginToken);

    /**
     * Get Login History by User id.
     *
     * @param userId user id
     * @return LoginHistory
     */
    @Query(value = "SELECT login FROM LoginHistory login WHERE login.userId = :userId")
    LoginHistory findByUserId(@Param("userId") Long userId);

}
