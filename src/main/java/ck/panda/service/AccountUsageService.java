package ck.panda.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ck.panda.domain.entity.AccountUsage;
import ck.panda.util.domain.CRUDService;

/**
 * Service class for Usage.
 *
 * This service class used to get the resource usage details for each accounts.
 *
 */
@Service
public interface AccountUsageService extends CRUDService<AccountUsage>  {

    List<AccountUsage> updateUsageAccount() throws Exception;
}


