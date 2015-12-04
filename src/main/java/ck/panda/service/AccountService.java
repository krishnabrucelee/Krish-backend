package ck.panda.service;

import ck.panda.domain.entity.Account;
import ck.panda.domain.entity.Domain;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Service class for account.
 *
 * This service provides basic CRUD and essential api's for Account related business actions.
 */
@Service
public interface AccountService  extends CRUDService<Account>  {

     /**
      * Find all the accounts with active status.
      *
      * @param pagingAndSorting pagination and sorting values.
      * @return list of departments with pagination.
      * @throws Exception error occurs
      */
     Page<Account> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

     /**
     * Find all the accounts with active status.
     *
     * @param isActive account status Active/Inactive
     * @return list of account with active status
     * @throws Exception error occurs.
     */
    List<Account> findAllByIsActive(Boolean isActive) throws Exception;

    /**
     * To get list of accounts from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<Account> findAllFromCSServerByDomain() throws Exception;

    /**
     * Find the accounts based on the given Uuid and isActive status.
     *
     * @param uuid account uuid.
     * @param isActive account status Active/Inactive
     * @return account.
     * @throws Exception error occurs.
     */
    Account findByUuidAndIsActive(String uuid, Boolean isActive) throws Exception;

    /**
     * Find the name of user and isActive Status.
     *
     * @param name of the user.
     * @param isActive account status Active/Inactive.
     * @return account.
     */
    Account findByUsername(String name, Boolean isActive);

    /**
     * Find by Domain.
     *
     * @param domainId of the user
     * @param isActive account status Active/Inactive.
     * @return account.
     */
    List<Account> findByDomain(Long domainId, Boolean isActive);

    /**
     * Find by Domain name and isActive Status.
     *
     * @param userName of the Domain.
     * @param domain object for user.
     * @param isActive account status Active/Inactive.
     * @return account.
     */
    Account findByNameAndDomainAndIsActive(String userName, Domain domain, Boolean isActive);

    /**
     * Find by Department and isActive Status.
     *
     * @param userName of the account.
     * @param domain object.
     * @param isActive account status Active/Inactive.
     * @return account.
     */
    Account findByNameAndDomainAndIsActiveAndUserType(String userName, Domain domain, Boolean isActive);

    /**
     * Soft delete for account.
     *
     * @param account object
     * @return account
     * @throws Exception unhandled errors.
     */
    Account softDelete(Account account) throws Exception;


}
