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

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.domain.repository.jpa.DomainRepository;
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

    @Autowired
    private DomainRepository domainRepository;

    /** Autowired cloudstackaccountservice object. */
    @Autowired
    private CloudStackAccountService csAccountService;

    /** Autowired configutill object. */
    @Autowired
    private ConfigUtil configServer;


    @Override
    public Department save(Department department) throws Exception {

        if(department.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("department", department);
            errors = validator.validateEntity(department, errors);
            errors = this.validateName(errors, department.getUserName(), department.getDomain(), (long) 0);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                department.setDomainId(department.getDomain().getId());
                department.setIsActive(true);
                department.setStatus(Department.Status.ENABLED);
                department.setType(Department.AccountType.USER);


                csAccountService.setServer(configServer.setServer(1L));
                HashMap<String, String> accountMap = new HashMap<String, String>();
                accountMap.put("domainid", String.valueOf(department.getDomain().getUuid()));
                String createAccountResponse =  csAccountService.createAccount(String.valueOf(department.getType().ordinal()),
                        department.getEmail(), department.getFirstName(), department.getLastName(), department.getUserName(), department.getPassword(), "json", accountMap);

                JSONObject createAccountResponseJSON = new JSONObject(createAccountResponse).getJSONObject("createaccountresponse")
                        .getJSONObject("account");
                System.out.println(createAccountResponseJSON);
                department.setUuid((String) createAccountResponseJSON.get("id"));
                return departmentRepo.save(department);
            }
            } else {
                return departmentRepo.save(department);
        }
    }

    /**
     * Check the department name already exist or not for same domain.
     *
     * @param errors already existing error list.
     * @param name name of the department.
     * @param domain domain object.
     * @return errors.
     * @throws Exception
     */
    private Errors validateName(Errors errors, String name, Domain domain, Long departmentId) throws Exception {
        if (departmentRepo.findByNameAndDomain(name, domain, departmentId) != null) {
            errors.addFieldError("name", "department.already.exist.for.same.domain");
        }
        return errors;
    }

    @Override
    public Department update(Department department) throws Exception {
        if(department.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("department", department);
            errors = validator.validateEntity(department, errors);
            errors = this.validateName(errors, department.getUserName(),  department.getDomain(), department.getId());
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                Department departmentedit = departmentRepo.findOne(department.getId());

                department.setDomainId(department.getDomain().getId());
                csAccountService.setServer(configServer.setServer(1L));
                HashMap<String, String> accountMap = new HashMap<String, String>();
                accountMap.put("domainid", department.getDomain().getUuid());
                accountMap.put("account", departmentedit.getUserName());
                csAccountService.updateAccount(department.getUserName(), accountMap);
                return departmentRepo.save(department);
            }

        } else {
              return departmentRepo.save(department);
          }
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

        LOGGER.debug("Sample Debug Message");
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

    public Page<Department> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
                return departmentRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }


    @Override
    public Department softDelete(Department department) throws Exception {
        department.setIsActive(false);
        department.setStatus(Department.Status.DELETED);
        //set server for finding value in configuration
        csAccountService.setServer(configServer.setServer(1L));
        csAccountService.deleteAccount(department.getUuid());
        return departmentRepo.save(department);
    }

    @Override
    public List<Department> findByName(String query) throws Exception {
            return (List<Department>) departmentRepo.findAllByActive(query);
    }

    @Override
    public List<Department> findAllByActive() throws Exception {
        //TODO: For Usage
        //Step1: Get all the account details


        //Step2: Get the usage response for each account from cloudstack
        return departmentRepo.findAllByActive();
    }

    @Override
    public List<Department> findAllFromCSServer() throws Exception {
          List<Department> departmentList = new ArrayList<Department>();
          HashMap<String, String> departmentMap = new HashMap<String, String>();

          // 1. Get the list of users from CS server using CS connector
          String response = csAccountService.listAccounts("json", departmentMap);
          JSONArray userListJSON = new JSONObject(response).getJSONObject("listaccountsresponse")
                  .getJSONArray("account");
          // 2. Iterate the json list, convert the single json entity to user
          for (int i = 0, size = userListJSON.length(); i < size; i++) {
              // 2.1 Call convert by passing JSONObject to User entity and Add
              // the converted User entity to list
              departmentList.add(Department.convert(userListJSON.getJSONObject(i)));
          }
          return departmentList;
      }

    }
