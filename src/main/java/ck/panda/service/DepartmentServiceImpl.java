package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Department;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAccountService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Department service implementation class.
 */
@Service
public class DepartmentServiceImpl implements DepartmentService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Department repository reference. */
    @Autowired
    private DepartmentReposiory departmentRepo;

    /** CloudStack account service reference. */
    @Autowired
    private CloudStackAccountService csAccountService;

    /** CloudStack configuration reference. */
    @Autowired
    private ConfigUtil configServer;

    /** Secret key for the user encryption. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    @Override
    public Department save(Department department) throws Exception {

        if (department.getSyncFlag()) {
            // Validate department
            this.validateDepartment(department);

            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
            String encryptedPassword = new String(EncryptionUtil.encrypt(department.getPassword(), originalKey));
            department.setPassword(encryptedPassword);
            department.setDomainId(department.getDomain().getId());
            department.setIsActive(true);
            department.setStatus(Department.Status.ENABLED);
            department.setType(Department.AccountType.USER);
            csAccountService.setServer(configServer.setServer(1L));
            HashMap<String, String> accountMap = new HashMap<String, String>();
            accountMap.put("domainid", String.valueOf(department.getDomain().getUuid()));
            String createAccountResponse = csAccountService.createAccount(
                    String.valueOf(department.getType().ordinal()), department.getEmail(), department.getFirstName(),
                    department.getLastName(), department.getUserName(), department.getPassword(), "json", accountMap);

            JSONObject createAccountResponseJSON = new JSONObject(createAccountResponse)
                    .getJSONObject("createaccountresponse").getJSONObject("account");
            department.setUuid((String) createAccountResponseJSON.get("id"));
        }
        return departmentRepo.save(department);
    }

    /**
     * Validate the department.
     *
     * @param department reference of the department.
     * @throws Exception error occurs
     */
    private void validateDepartment(Department department) throws Exception {
        Errors errors = validator.rejectIfNullEntity("department", department);
        errors = validator.validateEntity(department, errors);
        Department dep = departmentRepo.findByNameAndDomainAndIsActive(department.getUserName(), department.getDomain(),
                true);
        if (dep != null && department.getId() != dep.getId()) {
            errors.addFieldError("userName", "department.already.exist.for.same.domain");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Department update(Department department) throws Exception {
        if (department.getSyncFlag()) {
            // Validate department
            this.validateDepartment(department);

            Department departmentedit = departmentRepo.findOne(department.getId());
            department.setDomainId(department.getDomain().getId());
            csAccountService.setServer(configServer.setServer(1L));
            HashMap<String, String> accountMap = new HashMap<String, String>();
            accountMap.put("domainid", department.getDomain().getUuid());
            accountMap.put("account", departmentedit.getUserName());
            csAccountService.updateAccount(department.getUserName(), accountMap);
        }
        return departmentRepo.save(department);
    }

    @Override
    public void delete(Department department) throws Exception {
        departmentRepo.delete(department);
    }

    @Override
    public void delete(Long id) throws Exception {
        departmentRepo.delete(id);
    }

    @Override
    public Department find(Long id) throws Exception {
        Department department = departmentRepo.findOne(id);
        if (department == null) {
            throw new EntityNotFoundException("department.not.found");
        }
        return department;
    }

    @Override
    public Page<Department> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return departmentRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Department> findAll() throws Exception {
        return (List<Department>) departmentRepo.findAll();
    }

    /**
     * Find all the departments with pagination.
     *
     * @throws Exception application errors.
     * @param pagingAndSorting do pagination with sorting for departments.
     * @return list of departments.
     */
    public Page<Department> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return departmentRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public Department softDelete(Department department) throws Exception {
        department.setIsActive(false);
        department.setStatus(Department.Status.DELETED);
        // set server for finding value in configuration
        csAccountService.setServer(configServer.setServer(1L));
        csAccountService.deleteAccount(department.getUuid());
        return departmentRepo.save(department);
    }

    @Override
    public List<Department> findAllByIsActive(Boolean isActive) throws Exception {
        //TODO :For Usage
        //Step1: Get all the account details

        //Step2: Get the usage response for each account from cloudstack
        return departmentRepo.findAllByIsActive(true);
    }

    @Override
    public List<Department> findAllFromCSServer() throws Exception {
        List<Department> departmentList = new ArrayList<Department>();
        HashMap<String, String> departmentMap = new HashMap<String, String>();

        // 1. Get the list of accounts from CS server using CS connector
        String response = csAccountService.listAccounts("json", departmentMap);
        JSONArray userListJSON = new JSONObject(response).getJSONObject("listaccountsresponse").getJSONArray("account");
        // 2. Iterate the json list, convert the single json entity to user
        for (int i = 0, size = userListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to Department entity and
            // Add the converted Department entity to list
            departmentList.add(Department.convert(userListJSON.getJSONObject(i)));
        }
        return departmentList;
    }

    @Override
    public Department findByUuidAndIsActive(String uuid, Boolean isActive) throws Exception {
        return (Department) departmentRepo.findByUuidAndIsActive(uuid, isActive);
    }

    @Override
    public Department findByUuid(String uuid) throws Exception {
        return (Department) departmentRepo.findByUuid(uuid);
    }

}
