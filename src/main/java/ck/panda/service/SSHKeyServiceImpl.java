package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.SSHKey;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.SSHKeyRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackSSHService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/** SSH key service implementation class. */
@Service
public class SSHKeyServiceImpl implements SSHKeyService {

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** SSH Key repository reference. */
    @Autowired
    private SSHKeyRepository sshkeyRepo;

    /** SSH Key repository reference. */
    @Autowired
    private CloudStackSSHService cloudStackSSHService;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** CloudStack configuration reference. */
    @Autowired
    private ConfigUtil configServer;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertEntityService convertEntity;

    /** Domain service reference. */
    @Autowired
    private DomainService domainService;

    /** Project service reference. */
    @Autowired
    private ProjectService projectService;

    /** Constant for SSH key. */
    public static final String SSHKEY = "sshkey";

    /** Constant for SSH keypair response from cloudStack. */
    public static final String CS_SSH_KEYPAIR = "sshkeypair";

    /** Constant for create SSH keypair response from cloudStack. */
    public static final String CS_CREATE_SSH_KEYPAIR = "createsshkeypairresponse";

    /** Constant for register SSH keypair response from cloudStack. */
    public static final String CS_REGISTER_SSH_KEYPAIR = "registersshkeypairresponse";

    /** Constant for list SSH keypair response from cloudStack. */
    public static final String CS_LIST_SSH_KEYPAIR = "listsshkeypairsresponse";

    /** Constant for delete SSH keypair response from cloudStack. */
    public static final String CS_DELETE_SSH_KEYPAIR = "deletesshkeypairresponse";

    /** Constant for keypair response from cloudStack. */
    public static final String CS_KEYPAIR = "keypair";

    /** Constant for private key response from cloudStack. */
    public static final String CS_PRIVATE_KEY = "privatekey";

    @Override
    @PreAuthorize("hasPermission(#sshkey.getIsSyncFlag(), 'CREATE_SSH_KEY')")
    public SSHKey save(SSHKey sshkey, Long id) throws Exception {
        if (sshkey.getIsSyncFlag()) {
            this.validateSSHKey(sshkey, id);
            Errors errors = validator.rejectIfNullEntity(SSHKEY, sshkey);
            errors = validator.validateEntity(sshkey, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                // If publicKey value is null, then create new publicKey
                if (sshkey.getPublicKey() == null) {
                    createSSHKey(sshkey, errors, id);
                }
                // If publicKey value is not null, then register the given key instead of generating new publicKey
                if (sshkey.getPublicKey() != null) {
                    registerSSHKey(sshkey, errors, id);
                }
                return sshkeyRepo.save(sshkey);
            }
        }
        return sshkeyRepo.save(sshkey);
    }

    /**
     * Validate the SSH key.
     *
     * @param id of the login user
     * @param sshkey reference of the SSH Key
     * @throws Exception error occurs
     */
    private void validateSSHKey(SSHKey sshkey, Long id) throws Exception {
        Errors errors = validator.rejectIfNullEntity(SSHKEY, sshkey);
        errors = validator.validateEntity(sshkey, errors);
        SSHKey ssh = sshkeyRepo.findByNameAndDepartmentAndIsActive(sshkey.getName(), convertEntity.getOwnerById(id)
            .getDepartmentId(), true);
        // Check SSHKey name for uniqueness, if not field error occurs
        if (ssh != null && ssh.getName() == sshkey.getName()) {
            errors.addFieldError(GenericConstants.NAME, "error.ssh.key.name.duplicate.ckeck");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    /**
     * To set optional values by validating userType.
     *
     * @param sshkey optional SSH Key values
     * @param id of the login user
     * @return optional values
     * @throws Exception error occurs
     * @throws NumberFormatException error occurs
     */
    public HashMap<String, String> optional(SSHKey sshkey, Long id) throws NumberFormatException, Exception {
        HashMap<String, String> optional = new HashMap<String, String>();
        //If projectId is not null, then optional values will be projectId and domainId else optional values
        //will be account and domainId
        if (sshkey.getProjectId() != null) {
            optional.put(CloudStackConstants.CS_PROJECT_ID, convertEntity.getProjectUuidById(sshkey.getProjectId()));
        }
        if (sshkey.getProjectId() == null && !(convertEntity.getOwnerById(id).getType()).equals(User.UserType.USER)) {
            optional.put(CloudStackConstants.CS_ACCOUNT, (convertEntity.getDepartmentById(sshkey.getDepartmentId())
              .getUserName()));
        } else if (sshkey.getProjectId() == null && (convertEntity.getOwnerById(id).getType()).equals(User.UserType.USER)) {
            optional.put(CloudStackConstants.CS_ACCOUNT, departmentService.find(convertEntity.getOwnerById(id)
                    .getDepartmentId()).getUserName());
        }
        if ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
            optional.put(CloudStackConstants.CS_DOMAIN_ID, (convertEntity.getDomainById(sshkey.getDomainId())
                .getUuid()));
        } else {
            optional.put(CloudStackConstants.CS_DOMAIN_ID, departmentService.find(convertEntity.getOwnerById(id)
                    .getDepartmentId()).getDomain().getUuid());
        }
        return optional;
    }

    /**
     * Cloud stack create SSH Key.
     *
     * @param id of the login user
     * @param sshkey reference of the SSH Key
     * @param errors global error and field errors
     * @throws Exception error
     */
    private void createSSHKey(SSHKey sshkey, Errors errors, Long id) throws Exception {
        configServer.setUserServer();
        String sshkeyResponse = cloudStackSSHService.createSSHKeyPair(sshkey.getName(), CloudStackConstants.JSON,
            optional(sshkey, id));
        JSONObject createSSHResponseJSON = new JSONObject(sshkeyResponse).getJSONObject(CS_CREATE_SSH_KEYPAIR);
        // If credentials for creation of new keypair are not valid, then global error occurs
        if (createSSHResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, createSSHResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        }
        // Get cloud stack SSHkey create response
        JSONObject sshkeypair = createSSHResponseJSON.getJSONObject(CS_KEYPAIR);
        // Set the generated keypair response along with status and also set domainId and departmentId according
        // to Usertype
        sshkey.setName((String) sshkeypair.get(CloudStackConstants.CS_NAME));
        sshkey.setFingerPrint((String) sshkeypair.get(CloudStackConstants.CS_FINGER_PRINT));
        sshkey.setPrivateKey((String) sshkeypair.get(CS_PRIVATE_KEY));
        sshkey.setIsActive(true);
        if (!(convertEntity.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
            sshkey.setDomainId(convertEntity.getOwnerById(id).getDomainId());
        }
        if ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.USER)) {
            sshkey.setDepartmentId(convertEntity.getOwnerById(id).getDepartmentId());
        }
    }

    /**
     * Cloud stack register SSH Key.
     *
     * @param id of the login user
     * @param sshkey reference of the SSH Key
     * @param errors global error and field errors
     * @throws Exception error
     */
    private void registerSSHKey(SSHKey sshkey, Errors errors, Long id) throws Exception {
        configServer.setUserServer();
        String sshkeyResponse = cloudStackSSHService.registerSSHKeyPair(sshkey.getName(), sshkey.getPublicKey(),
            CloudStackConstants.JSON, optional(sshkey, id));
        JSONObject registerSSHResponseJSON = new JSONObject(sshkeyResponse).getJSONObject(CS_REGISTER_SSH_KEYPAIR);
        // If given the given input is invalid, then error is thrown
        if (registerSSHResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, registerSSHResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        }
        // Get cloud stack SSHkey register response
        JSONObject sshkeypair = registerSSHResponseJSON.getJSONObject(CS_KEYPAIR);
        // Set the generated keypair response along with status and also set domainId and departmentId according
        // to Usertype
        sshkey.setName((String) sshkeypair.get(CloudStackConstants.CS_NAME));
        sshkey.setFingerPrint((String) sshkeypair.get(CloudStackConstants.CS_FINGER_PRINT));
        sshkey.setIsActive(true);
        if (!(convertEntity.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
            sshkey.setDomainId(convertEntity.getOwnerById(id).getDomainId());
        }
        if ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.USER)) {
            sshkey.setDepartmentId(convertEntity.getOwnerById(id).getDepartmentId());
        }
    }

    @Override
    public void delete(SSHKey sshkey) throws Exception {
        sshkeyRepo.delete(sshkey);
    }

    @Override
    public void delete(Long id) throws Exception {
        sshkeyRepo.delete(id);
    }

    @Override
    public SSHKey find(Long id) throws Exception {
        SSHKey sshkey = sshkeyRepo.findOne(id);
        if (sshkey == null) {
            throw new EntityNotFoundException("error.ssh.key.not.found");
        }
        return sshkey;
    }

    @Override
    public Page<SSHKey> findAll(PagingAndSorting pagingAndSorting, Long id) throws Exception {
        // If Usertype is root admin, then list the entire SSHKey list according to active status
        // If Usertype is domain admin, then list the SSHKey list with respect to domainId and active status or else
        // list the SSHKey list according to deparmentId along with active status
        if ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
            return sshkeyRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
        } else if ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.DOMAIN_ADMIN)) {
            Page<SSHKey> allSSHKeyList = sshkeyRepo.findAllByDomainIsActive((convertEntity.getOwnerById(id)
                .getDomainId()), true, pagingAndSorting.toPageRequest());
            return allSSHKeyList;
        }
        return sshkeyRepo.findAllByDepartmentIsActive((convertEntity.getOwnerById(id).getDepartmentId()), true,
            pagingAndSorting.toPageRequest());
    }

    @Override
    public List<SSHKey> findAll(Long id) throws Exception {
        // If Usertype is root admin, then list the entire SSHKey list according to active status
        // If Usertype is domain admin, then list the SSHKey list with respect to domainId and active status or else
        // list the SSHKey list according to deparmentId along with active status
        if ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.ROOT_ADMIN)) {
            return sshkeyRepo.findAllByIsActive(true);
        } else if ((convertEntity.getOwnerById(id).getType()).equals(User.UserType.DOMAIN_ADMIN)) {
            return sshkeyRepo.findAllByDomainIsActive(convertEntity.getOwnerById(id).getDomainId(),true);
        }
        return sshkeyRepo.findAllByDepartmentAndIsActive(convertEntity.getOwnerById(id).getDepartmentId(), true);
    }

    @Override
    @PreAuthorize("hasPermission(#sshkey.getIsSyncFlag(), 'DELETE_SSH_KEY')")
    public SSHKey softDelete(SSHKey sshkey, Long id) throws Exception {
        sshkey.setIsActive(false);
        sshkey.setStatus(SSHKey.Status.DISABLED);
        if (sshkey.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(SSHKEY, sshkey);
            errors = validator.validateEntity(sshkey, errors);
            HashMap<String, String> optional = new HashMap<String, String>();
            if (sshkey.getProjectId() != null) {
                optional.put(CloudStackConstants.CS_PROJECT_ID, convertEntity.getProjectUuidById(sshkey.getProjectId()));
            } else {
                optional.put(CloudStackConstants.CS_ACCOUNT, (convertEntity.getDepartmentById(sshkey.getDepartmentId())
                  .getUserName()));
            }
            optional.put(CloudStackConstants.CS_DOMAIN_ID, convertEntity.getDepartmentById(sshkey.getDepartmentId())
                    .getDomain().getUuid());
            configServer.setUserServer();
            String sshkeyResponse = cloudStackSSHService.deleteSSHKeyPair(sshkey.getName(), CloudStackConstants.JSON,
                optional);
            // Get cloud stack SSHkey delete response
            JSONObject deletesshkeypairresponseJSON = new JSONObject(sshkeyResponse)
                .getJSONObject(CS_DELETE_SSH_KEYPAIR);
            // Check delete SSHKey response has error
            if (deletesshkeypairresponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = validator.sendGlobalError(deletesshkeypairresponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            }
        }
        return sshkeyRepo.save(sshkey);
    }

    @Override
    public List<SSHKey> findAllFromCSServer() throws Exception {
        List<Project> project = projectService.findAllByActive(true);
        List<SSHKey> sshKeyList = new ArrayList<SSHKey>();
        for (int j = 0; j <= project.size(); j++) {
            HashMap<String, String> sshKeyMap = new HashMap<String, String>();
            if (j == project.size()) {
                sshKeyMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
            } else {
                sshKeyMap.put(CloudStackConstants.CS_PROJECT_ID, project.get(j).getUuid());
            }
        configServer.setServer(1L);
        // 1. Get the list of SSH Key from CS server using CS connector
        String response = cloudStackSSHService.listSSHKeyPairs(CloudStackConstants.JSON, sshKeyMap);
        JSONArray sshKeyListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_SSH_KEYPAIR);
        if (responseObject.has(CS_SSH_KEYPAIR)) {
            sshKeyListJSON = responseObject.getJSONArray(CS_SSH_KEYPAIR);
            // 2. Iterate the json list, convert the single json entity to user
            for (int i = 0, size = sshKeyListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to User entity and Add
                // the converted User entity to list
                SSHKey sshkey = SSHKey.convert(sshKeyListJSON.getJSONObject(i));
                sshkey.setDomainId(convertEntity.getDomainId(sshkey.getTransDomainId()));
                sshkey.setDepartmentId(convertEntity.getDepartmentByUsername(sshkey.getTransDepartment(),
                    domainService.findbyUUID(sshkey.getTransDomainId()).getId()));
                if (j != project.size()) {
                    sshkey.setProjectId(project.get(j).getId());
                    sshkey.setDepartmentId(projectService.find(sshkey.getProjectId()).getDepartmentId());
                }
                sshKeyList.add(sshkey);
            }
        }
        }
        return sshKeyList;
    }

    /**
     * Check the SSH Key CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    @Override
    public List<SSHKey> findAllBySync() throws Exception {
        return (List<SSHKey>) sshkeyRepo.findAll();
    }

    @Override
    public SSHKey save(SSHKey sshkey) throws Exception {
        if (!sshkey.getIsSyncFlag()) {
            return sshkeyRepo.save(sshkey);
        }
        return sshkey;

    }

    @Override
    public SSHKey update(SSHKey sshkey) throws Exception {
        if (!sshkey.getIsSyncFlag()) {
            return sshkeyRepo.save(sshkey);
        }
        return sshkey;
    }

    @Override
    public Page<SSHKey> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

    @Override
    public List<SSHKey> findAll() throws Exception {
        return null;
    }

    @Override
    public List<SSHKey> findAllByDepartmentAndIsActive(Long departmentId, Boolean isActive) throws Exception {
        return sshkeyRepo.findAllByDepartmentAndIsActive(departmentId, true);
    }

    @Override
    public List<SSHKey> findAllByProjectAndIsActive(Long projectId, Boolean isActive) throws Exception {
        return sshkeyRepo.findAllByProjectAndIsActive(projectId, true);
    }
}
