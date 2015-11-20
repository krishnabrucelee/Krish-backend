package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ck.panda.domain.entity.Account;
import ck.panda.domain.entity.Domain;

/**
 * JPA Repository for Department entity.
 */
@Repository
public interface AccountRepository extends PagingAndSortingRepository<Account, Long> {


      /**
     * Find the department for same domain with username and is active status.
     *
     * @param userName user name of the department.
     * @param domain Domain reference.
     * @param isActive get the department list based on active/inactive status.
     * @return department name.
     */
    @Query(value = "select dpt from Account dpt where dpt.userName=:userName AND  dpt.domain =:domain AND dpt.isActive =:isActive")
    Account findByNameAndDomainAndIsActive(@Param("userName") String userName, @Param("domain") Domain domain, @Param("isActive")  Boolean isActive);

    /**
     * Find all the active or inactive departments with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the department list based on active/inactive status.
     * @return list of departments.
     */
    @Query(value = "select dpt from Account dpt where dpt.isActive =:isActive")
    Page<Account> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the Account with active status.
     *
     * @param isActive get the Account list based on active/inactive status.
     * @return list of Accounts.
     */
    @Query(value = "select dpt from Account dpt where dpt.isActive =:isActive")
    List<Account> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find the Account by uuid.
     *
     * @param uuid Account uuid.
     * @param isActive get the Account list based on active/inactive status.
     * @return Account.
     */
    @Query(value = "select dpt from Account dpt where dpt.isActive =:isActive AND dpt.uuid=:uuid)")
    Account findByUuidAndIsActive(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Find the account by domain ID and is Active.
     *
     * @param domainId of each domain.
     * @param isActive get the Account list based on active/inactive status.
     * @return Account.
     */
    @Query(value = "select dpt from Account dpt where dpt.isActive =:isActive AND dpt.domainId=:domainId)")
    List<Account> findByDomain(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);
}
