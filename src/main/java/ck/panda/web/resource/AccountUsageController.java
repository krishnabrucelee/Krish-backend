package ck.panda.web.resource;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import ck.panda.domain.entity.AccountUsage;
import ck.panda.domain.entity.Department;
import ck.panda.service.AccountUsageService;
import ck.panda.service.DepartmentService;
import ck.panda.util.web.ApiController;
import ck.panda.util.web.CRUDController;

public class AccountUsageController extends CRUDController<AccountUsage> implements ApiController {

    @Autowired
    private AccountUsageService accountUsageService;


    /**
     * Get the account details and update with the cloud stack database.
     *
     * @return projects project list.
     * @throws Exception error occurs.
     */
    @RequestMapping(value = "updateUsage", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    protected List<AccountUsage> updateAccountUsage() throws Exception {
        return accountUsageService.updateUsageAccount();

    }
}
