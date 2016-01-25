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
import ck.panda.domain.entity.SSHKey;
import ck.panda.domain.repository.jpa.SSHKeyRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackSSHService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
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

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

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

    /** Json response for SSH Key. */
    private static final String JSON = "json";

    /** User type of the login user for SSH Key. */
    private static final String USERTYPE = "usertype";

    /** DomainId for SSH Key. */
    private static final String DOMAINID = "domainid";

    /** DomainId for SSH Key. */
    private static final String DEPARTMENTID = "departmentid";

    @Override
    @PreAuthorize("hasPermission(#sshkey.getIsSyncFlag(), 'CREATE_SSH_KEY')")
    public SSHKey save(SSHKey sshkey) throws Exception {
        if (sshkey.getIsSyncFlag()) {
            this.validateSSHKey(sshkey);
            Errors errors = validator.rejectIfNullEntity("sshkey", sshkey);
            errors = validator.validateEntity(sshkey, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                // If publicKey value is null, then create new publicKey
                if (sshkey.getPublicKey() == null) {
                    createSSHKey(sshkey, errors);
                }
                // If publicKey value is not null, then register the given key instead of generating new publicKey
                if (sshkey.getPublicKey() != null) {
                    registerSSHKey(sshkey, errors);
                }
                return sshkeyRepo.save(sshkey);
            }
        }
        return sshkeyRepo.save(sshkey);
    }

    /**
     * Validate the SSH key.
     *
     * @param sshkey reference of the SSH Key.
     * @throws Exception error occurs
     */
    private void validateSSHKey(SSHKey sshkey) throws Exception {
        Errors errors = validator.rejectIfNullEntity("sshkey", sshkey);
        errors = validator.validateEntity(sshkey, errors);
        SSHKey ssh = sshkeyRepo.findByNameAndDepartmentAndIsActive(sshkey.getName(),
                Long.parseLong(tokenDetails.getTokenDetails(DEPARTMENTID)), true);
        // Check SSHKey name for uniqueness, if not field error occurs
        if (ssh != null && ssh.getName() == sshkey.getName()) {
            errors.addFieldError("name", "SSH.key.name.already.exist.for.same.account");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    /**
     * To set optional values by validating userType.
     *
     * @param sshkey optional SSH Key values
     * @return optional values
     * @throws Exception error occurs
     * @throws NumberFormatException error occurs
     */
    public HashMap<String, String> optional(SSHKey sshkey) throws NumberFormatException, Exception {
        HashMap<String, String> optional = new HashMap<String, String>();
        // If Usertype is root admin or domain admin, then get the optional values from user input or else from
        // token details
        if ((String.valueOf(tokenDetails.getTokenDetails(USERTYPE)).equals("ROOT_ADMIN"))
              || (String.valueOf(tokenDetails.getTokenDetails(USERTYPE)).equals("DOMAIN_ADMIN"))) {
              optional.put("account", String.valueOf(convertEntity.getDepartmentById(sshkey.getDepartmentId())
              .getUserName()));
              optional.put(DOMAINID, String.valueOf(convertEntity.getDomainById(sshkey.getDomainId()).getUuid()));
        } else {
              optional.put(DOMAINID, departmentService
                      .find(Long.parseLong(tokenDetails.getTokenDetails(DEPARTMENTID))).getDomain().getUuid());
              optional.put("account", departmentService
                      .find(Long.parseLong(tokenDetails.getTokenDetails(DEPARTMENTID))).getUserName());
        }
        return optional;
    }

    /**
     * Cloud stack create SSH Key.
     *
     * @param sshkey reference of the SSH Key
     * @param errors global error and field errors
     * @throws Exception error
     */
    private void createSSHKey(SSHKey sshkey, Errors errors) throws Exception {
        cloudStackSSHService.setServer(configServer.setServer(1L));
        String sshkeyResponse = cloudStackSSHService.createSSHKeyPair(sshkey.getName(), JSON, optional(sshkey));
        JSONObject createSSHResponseJSON = new JSONObject(sshkeyResponse).getJSONObject("createsshkeypairresponse");
        // If credentials for creation of new keypair are not valid, then global error occurs
        if (createSSHResponseJSON.has("errorcode")) {
            errors = this.validateEvent(errors, createSSHResponseJSON.getString("errortext"));
            throw new ApplicationException(errors);
        }
        // Get cloud stack SSHkey create response
        JSONObject sshkeypair = createSSHResponseJSON.getJSONObject("keypair");
        // Set the generated keypair response along with status and also set domainId and departmentId according
        // to Usertype
        sshkey.setName((String) sshkeypair.get("name"));
        sshkey.setFingerPrint((String) sshkeypair.get("fingerprint"));
        sshkey.setPrivateKey((String) sshkeypair.get("privatekey"));
        sshkey.setIsActive(true);
        if (String.valueOf(tokenDetails.getTokenDetails(USERTYPE)).equals("USER")) {
            sshkey.setDomainId(Long.parseLong(tokenDetails.getTokenDetails(DOMAINID)));
            sshkey.setDepartmentId(Long.parseLong(tokenDetails.getTokenDetails(DEPARTMENTID)));
        }
    }

    /**
     * Cloud stack register SSH Key.
     *
     * @param sshkey reference of the SSH Key
     * @param errors global error and field errors
     * @throws Exception error
     */
    private void registerSSHKey(SSHKey sshkey, Errors errors) throws Exception {
        cloudStackSSHService.setServer(configServer.setServer(1L));
        String sshkeyResponse = cloudStackSSHService.registerSSHKeyPair(sshkey.getName(), sshkey.getPublicKey(), JSON,
            optional(sshkey));
        JSONObject registerSSHResponseJSON = new JSONObject(sshkeyResponse).getJSONObject("registersshkeypairresponse");
        // If given the given input is invalid, then error is thrown
        if (registerSSHResponseJSON.has("errorcode")) {
            errors = this.validateEvent(errors, registerSSHResponseJSON.getString("errortext"));
            throw new ApplicationException(errors);
        }
        // Get cloud stack SSHkey register response
        JSONObject sshkeypair = registerSSHResponseJSON.getJSONObject("keypair");
        // Set the generated keypair response along with status and also set domainId and departmentId according
        // to Usertype
        sshkey.setName((String) sshkeypair.get("name"));
        sshkey.setFingerPrint((String) sshkeypair.get("fingerprint"));
        sshkey.setIsActive(true);
        if (String.valueOf(tokenDetails.getTokenDetails(USERTYPE)).equals("USER")) {
            sshkey.setDomainId(Long.parseLong(tokenDetails.getTokenDetails(DOMAINID)));
            sshkey.setDepartmentId(Long.parseLong(tokenDetails.getTokenDetails(DEPARTMENTID)));
        }
    }


    @Override
    public SSHKey update(SSHKey sshkey) throws Exception {
        if (sshkey.getIsSyncFlag()) {
            this.validateSSHKey(sshkey);
        }
        return sshkeyRepo.save(sshkey);
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
            throw new EntityNotFoundException("ssh.key.not.found");
        }
        return sshkey;
    }

    @Override
    public Page<SSHKey> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        // If Usertype is root admin, then list the entire SSHKey list according to active status
        // If Usertype is domain admin, then list the SSHKey list with respect to domainId and active status or else
        // list the SSHKey list according to deparmentId along with active status
        if (String.valueOf(tokenDetails.getTokenDetails(USERTYPE)).equals("ROOT_ADMIN")) {
            return sshkeyRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
        } else if (String.valueOf(tokenDetails.getTokenDetails(USERTYPE)).equals("DOMAIN_ADMIN")) {
            Page<SSHKey> allSSHKeyList = sshkeyRepo.findAllByDomainIsActive(Long.valueOf(tokenDetails
            .getTokenDetails(DOMAINID)), true, pagingAndSorting.toPageRequest());
            return allSSHKeyList;
        }
        return sshkeyRepo.findAllByDepartmentIsActive(Long.parseLong(tokenDetails.getTokenDetails(DEPARTMENTID)),
            true, pagingAndSorting.toPageRequest());
    }

    @Override
    public List<SSHKey> findAll() throws Exception {
        // If Usertype is root admin, then list the entire SSHKey list according to active status
        // If Usertype is domain admin, then list the SSHKey list with respect to domainId and active status or else
        // list the SSHKey list according to deparmentId along with active status
        if (String.valueOf(tokenDetails.getTokenDetails(USERTYPE)).equals("ROOT_ADMIN")) {
            return sshkeyRepo.findAllByIsActive(true);
        } else if (String.valueOf(tokenDetails.getTokenDetails(USERTYPE)).equals("DOMAIN_ADMIN")) {
            return sshkeyRepo.findAllByDomainIsActive(Long.valueOf(tokenDetails.getTokenDetails(DOMAINID)),true);
        }
        return sshkeyRepo.findAllByDepartmentAndIsActive(Long.parseLong(tokenDetails.getTokenDetails(DEPARTMENTID)),
            true);
    }

    @Override
    @PreAuthorize("hasPermission(#sshkey.getIsSyncFlag(), 'DELETE_SSH_KEY')")
    public SSHKey softDelete(SSHKey sshkey) throws Exception {
        sshkey.setIsActive(false);
        sshkey.setStatus(SSHKey.Status.DISABLED);
        if (sshkey.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("sshkey", sshkey);
            errors = validator.validateEntity(sshkey, errors);
            cloudStackSSHService.setServer(configServer.setServer(1L));
            HashMap<String, String> optional = new HashMap<String, String>();
            optional.put(DOMAINID, convertEntity.getDepartmentById(sshkey.getDepartmentId()).getDomain().getUuid());
            optional.put("account", convertEntity.getDepartmentById(sshkey.getDepartmentId()).getUserName());
            String sshkeyResponse = cloudStackSSHService.deleteSSHKeyPair(sshkey.getName(), JSON, optional);
            // Get cloud stack SSHkey delete response
            JSONObject deletesshkeypairresponseJSON = new JSONObject(sshkeyResponse)
                    .getJSONObject("deletesshkeypairresponse");
            // Check delete SSHKey response has error
            if (deletesshkeypairresponseJSON.has("errorcode")) {
                errors = validator.sendGlobalError(deletesshkeypairresponseJSON.getString("errortext"));
                throw new ApplicationException(errors);
            }
        }
        return sshkeyRepo.save(sshkey);
    }

    @Override
    public List<SSHKey> findAllFromCSServer() throws Exception {
        List<SSHKey> sshKeyList = new ArrayList<SSHKey>();
        HashMap<String, String> sshKeyMap = new HashMap<String, String>();
        sshKeyMap.put("listall", "true");
        // 1. Get the list of SSH Key from CS server using CS connector
        String response = cloudStackSSHService.listSSHKeyPairs(JSON, sshKeyMap);
        JSONArray sshKeyListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listsshkeypairsresponse");
        if (responseObject.has("sshkeypair")) {
            sshKeyListJSON = responseObject.getJSONArray("sshkeypair");
            // 2. Iterate the json list, convert the single json entity to user
            for (int i = 0, size = sshKeyListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to User entity and Add
                // the converted User entity to list
                SSHKey sshkey = SSHKey.convert(sshKeyListJSON.getJSONObject(i));
                sshkey.setDomainId(convertEntity.getDomainId(sshkey.getTransDomainId()));
                sshkey.setDepartmentId(convertEntity.getDepartmentByUsername(sshkey.getTransDepartment(),
                        domainService.findbyUUID(sshkey.getTransDomainId()).getId()));
                sshKeyList.add(sshkey);
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
}
