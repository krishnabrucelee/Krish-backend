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
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.SSHKey;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.User.UserType;
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

    @Override
    @PreAuthorize("hasPermission(#sshkey.getIsSyncFlag(), 'CREATE_SSH_KEY')")
    public SSHKey save(SSHKey sshkey) throws Exception {
        if (sshkey.getIsSyncFlag()) {
            this.validateSSHKey(sshkey);
            Errors errors = validator.rejectIfNullEntity("sshkey", sshkey);
            errors = validator.validateEntity(sshkey, errors);
            cloudStackSSHService.setServer(configServer.setServer(1L));
            HashMap<String, String> optional = new HashMap<String, String>();
            User user = convertEntity.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
            if ((user != null && user.getType().equals(UserType.ROOT_ADMIN)) || (user != null && user.getType().equals(UserType.DOMAIN_ADMIN))) {
                optional.put("account", String.valueOf(convertEntity.getDepartmentById(sshkey.getDepartmentId()).getUserName()));
                optional.put("domainid", String.valueOf(convertEntity.getDomainById(sshkey.getDomainId()).getUuid()));
            } else {
                optional.put("domainid", departmentService
                        .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getDomain().getUuid());
                optional.put("account", departmentService
                        .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName());
            }
            if (sshkey.getPublicKey() != null) {
                String sshkeyResponse = cloudStackSSHService.registerSSHKeyPair(sshkey.getName(), sshkey.getPublicKey(), "json", optional);
                JSONObject registerSSHResponseJSON = new JSONObject(sshkeyResponse).getJSONObject("registersshkeypairresponse");
                if (registerSSHResponseJSON.has("errorcode")) {
                 errors = this.validateEvent(errors, registerSSHResponseJSON.getString("errortext"));
                    throw new ApplicationException(errors);
                }
                JSONObject sshkeypair = registerSSHResponseJSON.getJSONObject("keypair");
                sshkey.setName((String) sshkeypair.get("name"));
                sshkey.setFingerPrint((String) sshkeypair.get("fingerprint"));
                sshkey.setIsActive(true);
                if (user != null && user.getType().equals(UserType.USER)) {
                    sshkey.setDomainId(Long.parseLong(tokenDetails.getTokenDetails("domainid")));
                    sshkey.setDepartmentId(Long.parseLong(tokenDetails.getTokenDetails("departmentid")));
                }
            } else if (sshkey.getPublicKey() == null) {
            String sshkeyResponse = cloudStackSSHService.createSSHKeyPair(sshkey.getName(), "json", optional);
            JSONObject createSSHResponseJSON = new JSONObject(sshkeyResponse).getJSONObject("createsshkeypairresponse");

            if (createSSHResponseJSON.has("errorcode")) {
                errors = this.validateEvent(errors, createSSHResponseJSON.getString("errortext"));
                throw new ApplicationException(errors);
            }
            JSONObject sshkeypair = createSSHResponseJSON.getJSONObject("keypair");
            sshkey.setName((String) sshkeypair.get("name"));
            sshkey.setFingerPrint((String) sshkeypair.get("fingerprint"));
            sshkey.setPrivatekey((String) sshkeypair.get("privatekey"));
            sshkey.setIsActive(true);
            if (user != null && user.getType().equals(UserType.USER)) {
                sshkey.setDomainId(Long.parseLong(tokenDetails.getTokenDetails("domainid")));
                sshkey.setDepartmentId(Long.parseLong(tokenDetails.getTokenDetails("departmentid")));
            }
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
                Long.parseLong(tokenDetails.getTokenDetails("departmentid")), true);
        if (ssh != null && ssh.getName() == sshkey.getName()) {
            errors.addFieldError("name", "SSH.key.name.already.exist.for.same.account");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
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
        return sshkeyRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<SSHKey> findAll() throws Exception {
        Department department = departmentService
                .find(Long.valueOf(tokenDetails.getTokenDetails("departmentid")));
        if (department != null && !department.getUserName().equals("ROOT")) {
            return sshkeyRepo
                    .findAllByDepartmentAndIsActive(Long.parseLong(tokenDetails.getTokenDetails("departmentid")), true);
        }
        return (List<SSHKey>) sshkeyRepo.findAllByIsActive(true);
    }

    @Override
    public Page<SSHKey> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        User user = convertEntity.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        if (user != null && user.getType().equals(UserType.ROOT_ADMIN)) {
            return sshkeyRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
        } else if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
            Page<SSHKey> allSSHKeyList = sshkeyRepo.findAllByDomainIsActive(Long.valueOf(tokenDetails.getTokenDetails("domainid")),
                    true, pagingAndSorting.toPageRequest());
            return allSSHKeyList;
        }
        return sshkeyRepo.findAllByDepartmentIsActive(Long.parseLong(tokenDetails.getTokenDetails("departmentid")),
                true, pagingAndSorting.toPageRequest());
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
            optional.put("domainid", convertEntity.getDepartmentById(sshkey.getDepartmentId()).getDomain().getUuid());
            optional.put("account", convertEntity.getDepartmentById(sshkey.getDepartmentId()).getUserName());
            String sshkeyResponse = cloudStackSSHService.deleteSSHKeyPair(sshkey.getName(), "json", optional);
            JSONObject deletesshkeypairresponseJSON = new JSONObject(sshkeyResponse)
                    .getJSONObject("deletesshkeypairresponse");
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
        String response = cloudStackSSHService.listSSHKeyPairs("json", sshKeyMap);
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

    @Override
    public List<SSHKey> findAllByIsActive(Boolean isActive) throws Exception {
        return sshkeyRepo.findAllByDepartmentAndIsActive(Long.parseLong(tokenDetails.getTokenDetails("departmentid")),
                true);
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
