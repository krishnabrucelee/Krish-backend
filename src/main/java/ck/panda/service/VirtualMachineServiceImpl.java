package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.repository.jpa.VirtualMachineRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackIsoService;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackServer;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

/**
 * Virtual Machine creation, update, start, reboot, stop all operations are handled by this controller.
 */
@Service
public class VirtualMachineServiceImpl implements VirtualMachineService {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Virtual Machine repository reference. */
    @Autowired
    private VirtualMachineRepository virtualmachinerepository;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Network repository reference. */
    @Autowired
    private VolumeService volumeService;

    /** Project service for listing projects. */
    @Autowired
    private ProjectService projectService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

    /** CloudStack connector reference for resource capacity. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Iso service cloud connector. */
    @Autowired
    private CloudStackIsoService csIso;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** Token details connector. */
    @Autowired
    private TokenDetails tokenDetails;

    @Override
    @PreAuthorize("hasPermission(#vminstance.getSyncFlag(), 'CREATE_VM')")
    public VmInstance save(VmInstance vminstance) throws Exception {
        LOGGER.debug("instance sync ", vminstance.getSyncFlag());
        if (vminstance.getSyncFlag()) {

            Errors errors = validator.rejectIfNullEntity("vminstance", vminstance);
            errors = validator.validateEntity(vminstance, errors);
            errors = this.validateName(errors, vminstance.getName(), vminstance.getDepartment(), 0L);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                String isAvailable = isResourceAvailable(vminstance);
                if (isAvailable != null) {
                    errors = validator.sendGlobalError(isAvailable);
                    throw new ApplicationException(errors);
                } else {
                    HashMap<String, String> optional = new HashMap<String, String>();
                    optional.put("displayname", vminstance.getName());
                    vminstance.setDisplayName(vminstance.getName());
                    optional.put("name", vminstance.getName());
                    vminstance.setNetworkId(convertEntityService.getNetworkByUuid(vminstance.getNetworkUuid()));
                    config.setUserServer();
                    LOGGER.debug("Cloud stack connectivity at VM", vminstance.getNetworkUuid());
                    optional.put("networkids", vminstance.getNetworkUuid());
                    optional.put("displayvm", "true");
                    optional.put("displayname", vminstance.getName());
                    optional.put("keyboard", "us");
                    optional.put("name", vminstance.getName());
                    if (vminstance.getProjectId() != null) {
                        optional.put("projectid",
                                convertEntityService.getProjectById(vminstance.getProjectId()).getUuid());
                    } else {
                        optional.put("account",
                                convertEntityService.getDepartmentById(vminstance.getDepartmentId()).getUserName());
                        optional.put("domainid",
                                convertEntityService.getDomainById(vminstance.getDomainId()).getUuid());
                    }
                    if (vminstance.getStorageOfferingId() != null) {
                        optional.put("diskofferingid",
                                convertEntityService.getStorageOfferById(vminstance.getStorageOfferingId()).getUuid());
                    }
                    String csResponse = cloudStackInstanceService.deployVirtualMachine(
                            convertEntityService.getComputeOfferById(vminstance.getComputeOfferingId()).getUuid(),
                            convertEntityService.getTemplateById(vminstance.getTemplateId()).getUuid(),
                            convertEntityService.getZoneById(vminstance.getZoneId()).getUuid(), "json", optional);
                    JSONObject csInstance = new JSONObject(csResponse).getJSONObject("deployvirtualmachineresponse");
                    if (csInstance.has("errorcode")) {
                        errors = this.validateEvent(errors, csInstance.getString("errortext"));
                        throw new ApplicationException(errors);
                    } else {
                        LOGGER.debug("VM UUID", csInstance.getString("id"));
                        vminstance.setUuid(csInstance.getString("id"));
                        String instanceResponse = cloudStackInstanceService
                                .queryAsyncJobResult(csInstance.getString("jobid"), "json");
                        JSONObject instance = new JSONObject(instanceResponse)
                                .getJSONObject("queryasyncjobresultresponse");
                        if (instance.getString("jobstatus").equals("2")) {
                            vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                            vminstance.setEventMessage(csInstance.getJSONObject("jobresult").getString("errortext"));
                        } else {
                            vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_CREATE));
                            vminstance.setEventMessage("Started creating VM on Server");
                        }
                    }
                }
                return virtualmachinerepository.save(vminstance);
            }
        } else {
            return virtualmachinerepository.save(vminstance);
        }
    }

    @Override
    public VmInstance update(VmInstance vminstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity("vminstance", vminstance);
        errors = validator.validateEntity(vminstance, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return virtualmachinerepository.save(vminstance);
        }
    }

    @Override
    public void delete(VmInstance vminstance) throws Exception {
        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_EXPUNGING));
        vminstance.setIsRemoved(true);
        virtualmachinerepository.save(vminstance);
    }

    /**
     * Open connection for CS API call.
     *
     * @param vmId VM uuid.
     * @return VmInstance.
     * @throws Exception unhandled exceptions.
     */
    public VmInstance getCSConnector(String vmId) throws Exception {
        // instantiate Cloud Stack connector for an instance service.
        config.setUserServer();
        csIso.setServer(server);
        VmInstance vminstance = virtualmachinerepository.findByUUID(vmId);
        return vminstance;
    }

    /**
     * VM related all quick actions are handled here.
     *
     * @throws Exception if unhandled exception occurs.
     */
    @Override
    public VmInstance vmEventHandle(String vmId, String event) throws Exception {
        VmInstance vminstance = getCSConnector(vmId);
        HashMap<String, String> optional = new HashMap<String, String>();
        Errors errors = null;
        switch (event) {
        case EventTypes.EVENT_VM_START:
            try {
                String instanceResponse = cloudStackInstanceService.startVirtualMachine(vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("startvirtualmachineresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(
                                    jobresult.getJSONObject("jobresult").getString("errortext"));
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_CREATE));
                        vminstance.setEventMessage("");
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM START", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_STOP:
            try {
                String instanceResponse = cloudStackInstanceService.stopVirtualMachine(vminstance.getUuid(), "json",
                        optional);
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("stopvirtualmachineresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(
                                    jobresult.getJSONObject("jobresult").getString("errortext"));
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_STOPPING));
                        vminstance.setEventMessage("");
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM STOP", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_REBOOT:
            try {
                String instanceResponse = cloudStackInstanceService.rebootVirtualMachine(vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("rebootvirtualmachineresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(
                                    jobresult.getJSONObject("jobresult").getString("errortext"));
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_RUNNING));
                        vminstance.setEventMessage("");
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM REBOOT", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_RESTORE:
            try {
                String instanceResponse = cloudStackInstanceService.restoreVirtualMachine(vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("restorevmresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(
                                    jobresult.getJSONObject("jobresult").getString("errortext"));
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setEventMessage("Re-installed");
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM RE-INSTALL", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_DESTROY:
            try {
                String instanceResponse = cloudStackInstanceService.destroyVirtualMachine(vminstance.getUuid(), "json",
                        optional);
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("destroyvirtualmachineresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(
                                    jobresult.getJSONObject("jobresult").getString("errortext"));
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_DESTROYED));
                        vminstance.setEventMessage("Vm destroyed");
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM DESTROY", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_EXPUNGE:
            try {
                optional.put("expunge", "true");
                String instanceResponse = cloudStackInstanceService.destroyVirtualMachine(vminstance.getUuid(), "json",
                        optional);
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("destroyvirtualmachineresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(
                                    jobresult.getJSONObject("jobresult").getString("errortext"));
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_EXPUNGING));
                        vminstance.setIsRemoved(true);
                        vminstance.setEventMessage("VM EXPUNGING");
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM EXPUNGING", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_CREATE:
            try {
                String instanceResponse = cloudStackInstanceService.recoverVirtualMachine(vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("recovervirtualmachineresponse");
                if (instance.has("errorcode")) {
                    errors = validator.sendGlobalError(instance.getString("errortext"));
                    if (errors.hasErrors()) {
                        throw new BadCredentialsException(instance.getString("errortext"));
                    }
                    vminstance.setEventMessage(instance.getString("errortext"));
                } else {
                    vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_CREATE));
                    vminstance.setEventMessage("VM Recover");
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM RECOVER", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        default:
            LOGGER.debug("No VM Action ", event);
        }
        return virtualmachinerepository.save(vminstance);
    }

    /**
     * VM related all quick actions are handled here.
     *
     * @throws Exception if unhandled exception occurs.
     */
    @Override
    public VmInstance vmEventHandleWithVM(VmInstance vmInstance, String event) throws Exception {
        VmInstance vminstance = getCSConnector(vmInstance.getUuid());
        HashMap<String, String> optional = new HashMap<String, String>();
        Errors errors = null;
        switch (event) {
        case EventTypes.EVENT_VM_MIGRATE:
            try {
                if (vminstance.getStatus().equals(Status.Running)) {
                    errors = validator.sendGlobalError("No Hosts are available for Migration");
                    if (errors.hasErrors()) {
                        throw new BadCredentialsException("No Hosts are available for Migration");
                    }
                    optional.put("hostid", vmInstance.getHostUuid());
                    String instanceResponse = cloudStackInstanceService.migrateVirtualMachine(vmInstance.getUuid(),
                            optional);
                    JSONObject instance = new JSONObject(instanceResponse)
                            .getJSONObject("migratevirtualmachineresponse");
                    if (instance.has("jobid")) {
                        String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                                "json");
                        JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                        if (jobresult.getString("jobstatus").equals("2")) {
                            errors = validator
                                    .sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                            if (errors.hasErrors()) {
                                throw new BadCredentialsException(
                                        jobresult.getJSONObject("jobresult").getString("errortext"));
                            }
                            vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                        } else {
                            vminstance.setStatus(Status.Migrating);
                        }
                    }
                } else {
                    errors.addGlobalError("Your instance must be Running before attempting to change its current host");
                    if (errors.hasErrors()) {
                        throw new BadCredentialsException(
                                "Your instance must be Running before attempting to change its current host");
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM Migrating ", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_ISO_ATTACH:
            try {
                String instanceResponse = csIso.attachIso(vmInstance.getIso(), vmInstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("attachisoresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(
                                    jobresult.getJSONObject("jobresult").getString("errortext"));
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else if (jobresult.getString("jobstatus").equals("1")) {
                        vminstance.setIsoName(jobresult.getJSONObject("jobresult").getJSONObject("virtualmachine")
                                .getString("isoname"));
                        vminstance.setIso(jobresult.getJSONObject("jobresult").getJSONObject("virtualmachine")
                                .getString("isoid"));
                    }
                }

            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM ATTACH ISO ", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_ISO_DETACH:
            try {
                String instanceResponse = csIso.detachIso(vmInstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("detachisoresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setIsoName(null);
                        vminstance.setIso(null);
                    }
                }

            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM DETACH ISO ", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_RESETPASSWORD:
            try {
                if (vmInstance.getPassword().equalsIgnoreCase("show")) {
                    String instanceResponse = cloudStackInstanceService.getVMPassword(vmInstance.getUuid());
                    System.out.println(instanceResponse);
                    JSONObject instance = new JSONObject(instanceResponse).getJSONObject("getvmpasswordresponse");
                    String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
                    byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                    SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                    if (vminstance.getVncPassword() != null) {
                        String encryptedPassword = new String(
                                EncryptionUtil.decrypt(vminstance.getVncPassword(), originalKey));
                        errors = validator.sendGlobalError("Your instance current password is " + encryptedPassword);
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException("Your instance current password is " + encryptedPassword);
                        }
                    } else {
                        errors = validator.sendGlobalError("No password are currently assigned for VM");
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException("No password are currently assigned for VM");
                        }
                    }
                } else {
                    if (vminstance.getStatus().equals(Status.Stopped)) {
                        String instanceResponse = cloudStackInstanceService
                                .resetPasswordForVirtualMachine(vmInstance.getUuid());
                        JSONObject instance = new JSONObject(instanceResponse)
                                .getJSONObject("resetpasswordforvirtualmachineresponse");
                        if (instance.has("jobid")) {
                            String instances = cloudStackInstanceService
                                    .queryAsyncJobResult(instance.getString("jobid"), "json");
                            JSONObject jobresult = new JSONObject(instances)
                                    .getJSONObject("queryasyncjobresultresponse");
                            if (jobresult.getString("jobstatus").equals("2")) {
                                errors = validator
                                        .sendGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                                if (errors.hasErrors()) {
                                    throw new BadCredentialsException(
                                            jobresult.getJSONObject("jobresult").getString("errortext"));
                                }
                                vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                            } else if (jobresult.getString("jobstatus").equals("1")) {
                                String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes("utf-8"));
                                byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                                SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");
                                String encryptedPassword = new String(
                                        EncryptionUtil.encrypt(jobresult.getJSONObject("jobresult")
                                                .getJSONObject("virtualmachine").getString("password"), originalKey));
                                vminstance.setVncPassword(encryptedPassword);
                                virtualmachinerepository.save(vminstance);
                                errors = validator.sendGlobalError(jobresult.getJSONObject("jobresult")
                                        .getJSONObject("virtualmachine").getString("password"));
                                if (errors.hasErrors()) {
                                    throw new BadCredentialsException(jobresult.getJSONObject("jobresult")
                                            .getJSONObject("virtualmachine").getString("password"));
                                }
                            }
                        }
                    } else {
                        errors = validator.sendGlobalError(
                                "Your instance must be stopped before attempting to change its current password");
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(
                                    "Your instance must be stopped before attempting to change its current password");
                        }
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT VM Reset password", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        default:
            LOGGER.debug("No VM Action ", event);
        }
        return virtualmachinerepository.save(vminstance);
    }

    @Override
    public void delete(Long id) throws Exception {
        virtualmachinerepository.delete(id);
    }

    @Override
    public VmInstance find(Long id) throws Exception {
        return virtualmachinerepository.findOne(id);
    }

    @Override
    public Page<VmInstance> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                Page<VmInstance> allInstanceList = virtualmachinerepository.findAllByDomainIsActive(user.getDomainId(),
                        Status.Expunging, pagingAndSorting.toPageRequest());
                return allInstanceList;
            } else {
                if (projectService.findByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                    for (Project project : projectService.findByUserAndIsActive(user.getId(), true)) {
                        List<VmInstance> allInstanceTempList = virtualmachinerepository
                                .findAllByUserAndProjectIsActiveAndStatus(Status.Expunging, user, project);
                        allInstanceList.addAll(allInstanceTempList);
                    }
                    List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                    Page<VmInstance> allInstanceLists = new PageImpl<VmInstance>(instances,
                            pagingAndSorting.toPageRequest(), pagingAndSorting.getPageSize());
                    return (Page<VmInstance>) allInstanceLists;
                } else {
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByUserIsActiveAndStatus(Status.Expunging, pagingAndSorting.toPageRequest(), user);
                    return (Page<VmInstance>) allInstanceLists;
                }

            }
        }
        return virtualmachinerepository.findAllByIsActive(Status.Expunging, pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<VmInstance> findAllByStatus(PagingAndSorting pagingAndSorting, String status) throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                Page<VmInstance> allInstanceList = virtualmachinerepository.findAllByDomainIsActiveAndStatus(
                        user.getDomainId(), Status.valueOf(status), pagingAndSorting.toPageRequest());
                return allInstanceList;
            } else {
                if (projectService.findByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                    for (Project project : projectService.findByUserAndIsActive(user.getId(), true)) {
                        List<VmInstance> allInstanceTempList = virtualmachinerepository
                                .findAllByUserAndProjectIsActive(Status.valueOf(status), user, project);
                        allInstanceList.addAll(allInstanceTempList);
                    }
                    List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                    Page<VmInstance> allInstanceLists = new PageImpl<VmInstance>(instances,
                            pagingAndSorting.toPageRequest(), pagingAndSorting.getPageSize());
                    return (Page<VmInstance>) allInstanceLists;
                } else {
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByUserIsActive(Status.valueOf(status), pagingAndSorting.toPageRequest(), user);
                    return (Page<VmInstance>) allInstanceLists;
                }

            }
        }
        return virtualmachinerepository.findAllByStatus(Status.valueOf(status), pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VmInstance> findAll() throws Exception {
        try {
            User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));

            if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
                if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                    List<VmInstance> allInstanceList = virtualmachinerepository.findAllByDomain(user.getDomainId(),
                            Status.Expunging);
                    return allInstanceList;
                } else {
                    if (projectService.findByUserAndIsActive(user.getId(), true).size() > 0) {
                        List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                        for (Project project : projectService.findByUserAndIsActive(user.getId(), true)) {
                            List<VmInstance> allInstanceTempList = virtualmachinerepository
                                    .findAllByUserAndProject(Status.Expunging, user, project);
                            allInstanceList.addAll(allInstanceTempList);
                        }
                        List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                        return instances;
                    } else {
                        List<VmInstance> allInstanceLists = virtualmachinerepository.findAllByUser(Status.Expunging,
                                user);
                        return allInstanceLists;
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (List<VmInstance>) virtualmachinerepository.findAllByIsActive(Status.Expunging);
    }

    @Override
    public VmInstance findByUUID(String uuid) {
        return virtualmachinerepository.findByUUID(uuid);
    }

    @Override
    @PreAuthorize("hasPermission(null, 'UPGRADE_VM')")
    public VmInstance upgradeDowngradeVM(VmInstance vminstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity("vminstance", vminstance);
        errors = validator.validateEntity(vminstance, errors);
        config.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        String volumeS = cloudStackInstanceService.scaleVirtualMachine(vminstance.getUuid(),
                vminstance.getComputeOffering().getUuid(), "json", optional);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject("scalevirtualmachineresponse");

        if (jobId.has("errorcode")) {
            errors = this.validateEvent(errors, jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
            // volume.setUuid((String) jobId.get("jobid"));
            if (jobId.has("jobid")) {
                String jobResponse = cloudStackInstanceService.queryAsyncJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.has("vminstance")) {
                    vminstance.setUuid((String) jobresult.get("id"));
                }
                if (jobresult.getString("jobstatus").equals("2")) {
                    vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                }
                if (jobresult.getString("jobstatus").equals("0")) {
                    vminstance.setComputeOfferingId(vminstance.getComputeOffering().getId());
                }
                if (jobresult.getString("jobstatus").equals("1")) {
                    vminstance.setComputeOfferingId(vminstance.getComputeOffering().getId());
                }
                virtualmachinerepository.save(vminstance);
            }
        }
        return vminstance;
    }

    /**
     * Check the virtual machine CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception unhandled exceptions.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    /**
     * Check the instance name already exist in department.
     *
     * @param errors already existing error list.
     * @param name name of the instance.
     * @param department department object.
     * @param id instance id.
     * @return errors.
     * @throws Exception unhandled exceptions.
     */
    private Errors validateName(Errors errors, String name, Department department, Long id) throws Exception {
        if ((virtualmachinerepository.findByNameAndDepartment(name, department, Status.Expunging)) != null) {
            errors.addGlobalError("Instance name already exist in" + department.getUserName() + " department");
        }
        return errors;
    }

    @Override
    public List<VmInstance> findAllFromCSServer() throws Exception {
        List<Project> project = projectService.findAllByActive(true);
        List<VmInstance> vmList = new ArrayList<VmInstance>();
        for (int j = 0; j <= project.size(); j++) {
            HashMap<String, String> vmMap = new HashMap<String, String>();
            vmMap.put("listall", "true");
            if (j == project.size()) {
                vmMap.put("listall", "true");
            } else {
                vmMap.put("projectid", project.get(j).getUuid());
            }
            // 1. Get the list of vms from CS server using CS connector
            String response = cloudStackInstanceService.listVirtualMachines("json", vmMap);
            JSONArray vmListJSON = null;
            JSONObject responseObject = new JSONObject(response).getJSONObject("listvirtualmachinesresponse");
            if (responseObject.has("virtualmachine")) {
                vmListJSON = responseObject.getJSONArray("virtualmachine");
                // 2. Iterate the json list, convert the single json entity to
                // vm.
                for (int i = 0, size = vmListJSON.length(); i < size; i++) {
                    // 2.1 Call convert by passing JSONObject to vm entity and
                    // Add
                    // the converted vm entity to list
                    VmInstance vmInstance = VmInstance.convert(vmListJSON.getJSONObject(i));
                    VmInstance persistInstance = findByUUID(vmInstance.getUuid());
                    if (persistInstance != null) {
                        if (volumeService.findByInstanceAndVolumeType(persistInstance.getId()) != null) {
                            vmInstance.setVolumeSize(
                                    volumeService.findByInstanceAndVolumeType(persistInstance.getId()).getDiskSize());
                        }
                    }
                    vmInstance.setDomainId(convertEntityService.getDomainId(vmInstance.getTransDomainId()));
                    vmInstance.setZoneId(convertEntityService.getZoneId(vmInstance.getTransZoneId()));
                    vmInstance.setNetworkId(convertEntityService.getNetworkId(vmInstance.getTransNetworkId()));
                    vmInstance.setProjectId(convertEntityService.getProjectId(vmInstance.getTransProjectId()));
                    vmInstance.setHostId(convertEntityService.getHostId(vmInstance.getTransHostId()));
                    vmInstance.setInstanceOwnerId(convertEntityService.getUserByName(vmInstance.getTransDisplayName(),
                            convertEntityService.getDomain(vmInstance.getTransDomainId())));
                    vmInstance.setDepartmentId(
                            convertEntityService.getDepartmentByUsernameAndDomains(vmInstance.getTransDepartmentId(),
                                    convertEntityService.getDomain(vmInstance.getTransDomainId())));
                    vmInstance.setTemplateId(convertEntityService.getTemplateId(vmInstance.getTransTemplateId()));
                    vmInstance.setComputeOfferingId(
                            convertEntityService.getComputeOfferId(vmInstance.getTransComputeOfferingId()));
                    if (vmInstance.getHostId() != null) {
                        vmInstance.setPodId(convertEntityService
                                .getPodIdByHost(convertEntityService.getHostId(vmInstance.getTransHostId())));
                    }

                    vmList.add(vmInstance);
                }
            }
        }
        return vmList;
    }

    public Integer findCountByStatus(Status status) {
        try {
            User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));

            if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
                if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                    List<VmInstance> allInstanceList = virtualmachinerepository
                            .findAllByDomainIsActiveAndStatus(user.getDomainId(), status);
                    return allInstanceList.size();
                } else {
                    if (projectService.findByUserAndIsActive(user.getId(), true).size() > 0) {
                        List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                        for (Project project : projectService.findByUserAndIsActive(user.getId(), true)) {
                            List<VmInstance> allInstanceTempList = virtualmachinerepository
                                    .findAllByUserAndProjectIsActive(status, user, project);
                            allInstanceList.addAll(allInstanceTempList);
                        }
                        List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                        return instances.size();
                    } else {
                        List<VmInstance> allInstanceLists = virtualmachinerepository.findAllByUserIsActive(status,
                                user);
                        return allInstanceLists.size();
                    }
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return virtualmachinerepository.findCountByStatus(status);

    }

    @Override
    public VmInstance updateDisplayName(VmInstance vminstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity("vminstance", vminstance);
        errors = validator.validateEntity(vminstance, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            if (vminstance.getTransDisplayName() != null
                    && !vminstance.getTransDisplayName().trim().equalsIgnoreCase("")) {
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put("displayName", vminstance.getTransDisplayName());
                cloudStackInstanceService.updateVirtualMachine(vminstance.getUuid(), optional);
                vminstance.setDisplayName(vminstance.getTransDisplayName());
            }
            return virtualmachinerepository.save(vminstance);
        }
    }

    @Override
    public List<VmInstance> findByDepartment(Long id) throws Exception {
        return virtualmachinerepository.findByDepartment(id);
    }

    @Override
    public List<VmInstance> findByProjectAndStatus(Long projectId, List<Status> statusCode) throws Exception {
        return virtualmachinerepository.findByProjectAndStatus(projectId, statusCode);

    }

    @Override
    public List<VmInstance> findByDepartmentAndStatus(Long departmentId, List<Status> statusCode) throws Exception {
        return virtualmachinerepository.findByDepartmentAndStatus(departmentId, statusCode);
    }

    /**
     * Check resouce capacity to create new VM.
     *
     * @param vm vm instance.
     * @return error message.
     * @throws Exception unhandled errors.
     */
    public String isResourceAvailable(VmInstance vm) throws Exception {
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        this.server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
        Long memory = 0L, cpu = 0L, primaryStorage = 0L, ip = 0L, secondaryStorage = 0L, tempCount = 0L;
        String errMessage = null;
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put("zoneid", convertEntityService.getZoneById(vm.getZoneId()).getUuid());
        String csResponse = cloudStackResourceCapacity.listCapacity(optional, "json");
        JSONArray capacityArrayJSON = null;
        JSONObject csCapacity = new JSONObject(csResponse).getJSONObject("listcapacityresponse");
        if (csCapacity.has("capacity")) {
            capacityArrayJSON = csCapacity.getJSONArray("capacity");
            for (int i = 0, size = capacityArrayJSON.length(); i < size; i++) {
                String type = capacityArrayJSON.getJSONObject(i).getString("type");
                Long tempTotalCapacity = Long.valueOf(capacityArrayJSON.getJSONObject(i).getString("capacitytotal"));
                Long tempCapacityUsed = Long.valueOf(capacityArrayJSON.getJSONObject(i).getString("capacityused"));
                switch (type) {
                case "0":
                    tempCount = updateResourceCount(vm, "9");
                    memory = tempTotalCapacity - tempCapacityUsed;
                    if (memory < Long.valueOf(convertEntityService.getComputeOfferById(vm.getComputeOfferingId())
                            .getMemory().toString())) {
                        if (vm.getProjectId() != null) {
                            errMessage = "Maximum number of resources of type 'memory' for project "
                                    + convertEntityService.getProjectById(vm.getProjectId()).getName()
                                    + " has been exceeded.";
                        } else {
                            errMessage = "Maximum number of resources of type 'memory' for current domain "
                                    + convertEntityService.getDomainById(vm.getDomainId()).getName()
                                    + " has been exceeded.";
                        }
                    }
                    break;
                case "1":
                    tempCount = updateResourceCount(vm, "8");
                    cpu = tempTotalCapacity - tempCapacityUsed;
                    if (Long.valueOf(convertEntityService.getComputeOfferById(vm.getComputeOfferingId()).getClockSpeed()
                            .toString()) > cpu) {
                        if (vm.getProjectId() != null) {
                            errMessage = "Maximum number of resources of type 'cpu' for project "
                                    + convertEntityService.getProjectById(vm.getProjectId()).getName()
                                    + " has been exceeded.";
                        } else {
                            errMessage = "Maximum number of resources of type 'cpu' for current domain "
                                    + convertEntityService.getDomainById(vm.getDomainId()).getName()
                                    + " has been exceeded.";
                        }
                    }
                    break;
                case "2":
                    tempCount = updateResourceCount(vm, "11");
                    secondaryStorage = tempTotalCapacity - tempCapacityUsed;
                    if (secondaryStorage < tempCount) {
                        if (vm.getProjectId() != null) {
                            errMessage = "Maximum number of resources of type 'seconday storage' for project "
                                    + convertEntityService.getProjectById(vm.getProjectId()).getName()
                                    + " has been exceeded.";
                        } else {
                            errMessage = "Maximum number of resources of type 'seconday storage' for current domain "
                                    + convertEntityService.getDomainById(vm.getDomainId()).getName()
                                    + " has been exceeded.";
                        }
                    }
                    break;
                case "3":
                    tempCount = updateResourceCount(vm, "10");
                    primaryStorage = tempTotalCapacity - tempCapacityUsed;
                    if (primaryStorage < convertEntityService.getTemplateById(vm.getTemplateId()).getSize()) {
                        if (vm.getProjectId() != null) {
                            errMessage = "Maximum number of resources of type 'primary storage' for project "
                                    + convertEntityService.getProjectById(vm.getProjectId()).getName()
                                    + " has been exceeded.";
                        } else {
                            errMessage = "Maximum number of resources of type 'primary storage' for current domain "
                                    + convertEntityService.getDomainById(vm.getDomainId()).getName()
                                    + " has been exceeded.";
                        }
                    }
                    break;
                case "4":
                    tempCount = updateResourceCount(vm, "1");
                    ip = tempTotalCapacity - tempCapacityUsed;
                    optional.clear();
                    optional.put("associatedNetworkId", vm.getNetworkUuid());
                    optional.put("listall", "true");
                    String csIpResponse = cloudStackResourceCapacity.listPublicIpAddress(optional, "json");
                    JSONObject csIpCapacity = new JSONObject(csIpResponse)
                            .getJSONObject("listpublicipaddressesresponse");
                    if (csIpCapacity.has("count")) {
                        LOGGER.debug("Already Ip address acquired ", type);
                    } else {
                        if (ip < 1) {
                            if (vm.getProjectId() != null) {
                                errMessage = "Maximum number of resources of type 'public IP addresses' for project "
                                        + convertEntityService.getProjectById(vm.getProjectId()).getName()
                                        + " has been exceeded.";
                            } else {
                                errMessage = "Maximum number of resources of type 'public IP addresses' for current domain "
                                        + convertEntityService.getDomainById(vm.getDomainId()).getName()
                                        + " has been exceeded.";
                            }
                        }
                    }
                    break;
                default:
                    LOGGER.debug("No Resource ", type);
                }
            }

        }
        return errMessage;
    }

    /**
     * Check resouce count to create new VM.
     *
     * @param vm vm instance.
     * @param type resource type.
     * @return resouce count.
     * @throws Exception unhandled errors.
     */
    public Long updateResourceCount(VmInstance vm, String type) throws Exception {
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        this.server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
        HashMap<String, String> optional = new HashMap<String, String>();
        if (vm.getProjectId() != null) {
            optional.put("projectid", convertEntityService.getProjectById(vm.getProjectId()).getUuid());
        } else {
            optional.put("domainid", convertEntityService.getDomainById(vm.getDomainId()).getUuid());
        }
        optional.put("resourcetype", type);
        String csResponse = cloudStackResourceCapacity.updateResourceCount(optional, "json");
        JSONArray capacityArrayJSON = null;
        JSONObject csCapacity = new JSONObject(csResponse).getJSONObject("updateresourcecountresponse");
        if (csCapacity.has("resourcecount")) {
            capacityArrayJSON = csCapacity.getJSONArray("resourcecount");
            for (int i = 0, size = capacityArrayJSON.length(); i < size; i++) {
                String resource = capacityArrayJSON.getJSONObject(i).getString("resourcecount");
                return Long.valueOf(resource);

            }
        }
        return 0L;
    }

}
