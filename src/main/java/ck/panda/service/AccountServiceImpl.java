package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Account;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.repository.jpa.AccountRepository;
import ck.panda.util.CloudStackAccountService;
import ck.panda.util.ConvertUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Department service implementation class.
 */
@Service
public class AccountServiceImpl implements AccountService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    /** Account repository reference. */
    @Autowired
    private AccountRepository accountRepo;

    /** CloudStack account service reference. */
    @Autowired
    private CloudStackAccountService csAccountService;

    /** Reference of the convertutil. */
    @Autowired
    private ConvertUtil convertUtil;

    @Override
    public Account save(Account account) throws Exception {
        return accountRepo.save(account);
    }

    @Override
    public Account update(Account account) throws Exception {
        return accountRepo.save(account);
    }

    @Override
    public void delete(Account account) throws Exception {
        accountRepo.delete(account);
    }

    @Override
    public void delete(Long id) throws Exception {
        accountRepo.delete(id);
    }

    @Override
    public Account find(Long id) throws Exception {
        return accountRepo.findOne(id);
    }

    @Override
    public Page<Account> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return accountRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Account> findAll() throws Exception {
        return (List<Account>) accountRepo.findAll();
    }

    /**
     * Find all the Account with pagination.
     *
     * @throws Exception application errors.
     * @param pagingAndSorting do pagination with sorting for departments.
     * @return list of Accounts.
     */
    public Page<Account> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return accountRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Account> findAllByIsActive(Boolean isActive) throws Exception {
        return accountRepo.findAllByIsActive(true);
    }

    @Override
    public List<Account> findAllFromCSServerByDomain(String domainUuid) throws Exception {
        List<Account> accountList = new ArrayList<Account>();
        HashMap<String, String> accountMap = new HashMap<String, String>();
        accountMap.put("domainid", domainUuid);
        // 1. Get the list of accounts from CS server using CS connector
        String response = csAccountService.listAccounts("json", accountMap);
        JSONArray accountListJSON = new JSONObject(response).getJSONObject("listaccountsresponse").getJSONArray("account");
        // 2. Iterate the json list, convert the single json entity to user
        for (int i = 0, size = accountListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to Department entity and
            // Add the converted Department entity to list
            Account account = Account.convert(accountListJSON.getJSONObject(i), convertUtil);
                accountList.add(account);

        }
        return accountList;
    }

    @Override
    public Account findByUuidAndIsActive(String uuid, Boolean isActive) throws Exception {
        return accountRepo.findByUuidAndIsActive(uuid, isActive);
    }

    @Override
    public List<Account> findByDomain(Long domainId, Boolean isActive) {
        return accountRepo.findByDomain(domainId, isActive);
    }

    @Override
    public Account findByNameAndDomainAndIsActive(String userName, Domain domain, Boolean isActive) {
         return accountRepo.findByNameAndDomainAndIsActive(userName, domain, isActive);

    }

    @Override
    public Account findByUsername(String name, Boolean isActive) {
        return null;
    }


}
