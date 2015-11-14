package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.domain.repository.jpa.VirtualMachineRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CSIsoService;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackServer;
import ck.panda.util.ConvertUtil;
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
import org.springframework.stereotype.Service;

/**
 * Virtual Machine creation, update, start, reboot, stop all operations are
 * handled by this controller.
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

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    /** CloudStack connector reference for instance. */
    @Autowired
    private CloudStackInstanceService cloudStackInstanceService;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Iso service cloud connector. */
    private CSIsoService csIso;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    @Override
    public VmInstance save(VmInstance vminstance) throws Exception {
        if (vminstance.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("vminstance", vminstance);
            errors = validator.validateEntity(vminstance, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put("displayvm", vminstance.getName());
                optional.put("name", vminstance.getName());
                CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
                server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
                cloudStackInstanceService.setServer(server);
                LOGGER.debug("Cloud stack connectivity at VM", cloudConfig.getApiKey());
                LOGGER.debug("Cloud stack connectivity at VM", vminstance.getNetworkUuid());
                optional.put("networkids", vminstance.getNetworkUuid());
                optional.put("displayvm", "true");
                optional.put("name", vminstance.getName());
                optional.put("displayname", vminstance.getName());
                String csResponse = cloudStackInstanceService.deployVirtualMachine(
                        vminstance.getComputeOffering().getUuid(), vminstance.getTemplate().getUuid(),
                        vminstance.getZone().getUuid(), "json", optional);
                JSONObject csInstance = new JSONObject(csResponse).getJSONObject("deployvirtualmachineresponse");
                if (csInstance.has("errorcode")) {
                    errors = this.validateEvent(errors, csInstance.getString("errortext"));
                    throw new ApplicationException(errors);
                } else {
                    LOGGER.debug("VM UUID", csInstance.getString("id"));
                    vminstance.setUuid(csInstance.getString("id"));
                    String instanceResponse = cloudStackInstanceService
                            .queryAsyncJobResult(csInstance.getString("jobid"), "json");
                    JSONObject instance = new JSONObject(instanceResponse).getJSONObject("queryasyncjobresultresponse");
                    if (instance.getString("jobstatus").equals("2")) {
                        errors = this.validateEvent(errors, instance.getJSONObject("jobresult").getString("errortext"));
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        vminstance.setEventMessage(csInstance.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_CREATE));
                        vminstance.setEventMessage("Started creating VM on Server");
                    }
                }
            }
            return virtualmachinerepository.save(vminstance);
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
        virtualmachinerepository.delete(vminstance);
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
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
        cloudStackInstanceService.setServer(server);
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
        Errors errors = validator.createErrors();
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
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_CREATE));
                        vminstance.setEventMessage("");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM START", e);
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
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_STOPPING));
                        vminstance.setEventMessage("");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM STOP", e);
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
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_RUNNING));
                        vminstance.setEventMessage("");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM REBOOT", e);
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
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setEventMessage("Re-installed");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM RE-INSTALL", e);
            }
            break;
        case EventTypes.EVENT_VM_DESTROY:
            try {
                String instanceResponse = cloudStackInstanceService.destroyVirtualMachine(vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("destroyvirtualmachineresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_DESTROYED));
                        vminstance.setEventMessage("Vm destroyed");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM DESTROY", e);
            }
            break;
        case EventTypes.EVENT_VM_EXPUNGE:
            try {
                optional.put("expunge", "true");
                String instanceResponse = cloudStackInstanceService.destroyVirtualMachine(vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("restorevmresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_EXPUNGING));
                        vminstance.setIsRemoved(true);
                        vminstance.setEventMessage("VM EXPUNGING");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM EXPUNGING", e);
            }
            break;
        case EventTypes.EVENT_VM_CREATE:
            try {
                String instanceResponse = cloudStackInstanceService.recoverVirtualMachine(vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("restorevmresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setEventMessage("Re-installed");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM RECOVER", e);
            }
            break;
        case EventTypes.EVENT_VM_RESETPASSWORD:
            try {
                if (vminstance.getStatus().equals("Stopped")) {
                    String instanceResponse = cloudStackInstanceService
                            .resetPasswordForVirtualMachine(vminstance.getUuid());
                    JSONObject instance = new JSONObject(instanceResponse)
                            .getJSONObject("resetpasswordforvirtualmachineresponse");
                    if (instance.has("jobid")) {
                        String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                                "json");
                        JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                        if (jobresult.getString("jobstatus").equals("2")) {
                            vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                            errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                            if (errors.hasErrors()) {
                                throw new ApplicationException(errors);
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
                            errors.addGlobalError(jobresult.getJSONObject("jobresult").getJSONObject("virtualmachine")
                                    .getString("password"));
                            if (errors.hasErrors()) {
                                throw new ApplicationException(errors);
                            }
                        }
                    }
                } else {
                    errors.addGlobalError(
                            "Your instance must be stopped before attempting to change its current password");
                    if (errors.hasErrors()) {
                        throw new ApplicationException(errors);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM Reset password", e);
            }
            break;
        case EventTypes.EVENT_VM_MIGRATE:
            try {
                if (vminstance.getStatus().equals("Running")) {
                    optional.put("hostid", vminstance.getHost().getUuid());
                    String instanceResponse = cloudStackInstanceService.migrateVirtualMachine(vminstance.getUuid(),
                            optional);
                    JSONObject instance = new JSONObject(instanceResponse)
                            .getJSONObject("migratevirtualmachineresponse");
                    if (instance.has("jobid")) {
                        String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                                "json");
                        JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                        if (jobresult.getString("jobstatus").equals("2")) {
                            vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                            errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                            if (errors.hasErrors()) {
                                throw new ApplicationException(errors);
                            }
                            vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                        } else {
                            vminstance.setStatus(Status.Migrating);
                        }
                    }
                } else {
                    errors.addGlobalError("Your instance must be Running before attempting to change its current host");
                    if (errors.hasErrors()) {
                        throw new ApplicationException(errors);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT VM Migrating ", e);
            }
            break;
        case EventTypes.EVENT_ISO_ATTACH:
            try {
                String instanceResponse = csIso.attachIso(vminstance.getIso(), vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("attachisoresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setIsoName(jobresult.getJSONObject("jobresult").getJSONObject("virtualmachine").getString("isoname"));
                        vminstance.setIso(jobresult.getJSONObject("jobresult").getJSONObject("virtualmachine").getString("isoid"));
                    }
                }

            } catch (Exception e) {
                LOGGER.error("ERROR AT VM ATTACH ISO ", e);
            }
            break;
        case EventTypes.EVENT_ISO_DETACH:
            try {
                String instanceResponse = csIso.detachIso(vminstance.getUuid(), "json");
                JSONObject instance = new JSONObject(instanceResponse).getJSONObject("detachisoresponse");
                if (instance.has("jobid")) {
                    String instances = cloudStackInstanceService.queryAsyncJobResult(instance.getString("jobid"),
                            "json");
                    JSONObject jobresult = new JSONObject(instances).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        vminstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                        errors.addGlobalError(jobresult.getJSONObject("jobresult").getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        vminstance.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vminstance.setIsoName(null);
                        vminstance.setIso(null);
                    }
                }

            } catch (Exception e) {
                LOGGER.error("ERROR AT VM DETACH ISO ", e);
            }
            break;
        default:
            LOGGER.debug("No VM Action ", event);
        }
        return virtualmachinerepository.save(vminstance);
    }

    @Override
    public void delete(Long id) throws Exception {
    }

    @Override
    public VmInstance find(Long id) throws Exception {
        return virtualmachinerepository.findOne(id);
    }

    @Override
    public Page<VmInstance> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

    @Override
    public List<VmInstance> findAll() throws Exception {
        return (List<VmInstance>) virtualmachinerepository.findAll();
    }

    @Override
    public VmInstance findByUUID(String uuid) {
        return virtualmachinerepository.findByUUID(uuid);
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
        if (virtualmachinerepository.findByNameAndDepartment(name, department, id) != null) {
            errors.addFieldError("name", "instance already exist");
        }
        return errors;
    }

    @Override
    public List<VmInstance> findAllFromCSServer() throws Exception {
        List<VmInstance> vmList = new ArrayList<VmInstance>();
        HashMap<String, String> vmMap = new HashMap<String, String>();
        // 1. Get the list of vms from CS server using CS connector
        String response = cloudStackInstanceService.listVirtualMachines("json", vmMap);
        JSONArray vmListJSON = new JSONObject(response).getJSONObject("listvirtualmachinesresponse")
                .getJSONArray("virtualmachine");
        // 2. Iterate the json list, convert the single json entity to vm.
        for (int i = 0, size = vmListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to vm entity and Add
            // the converted vm entity to list
            vmList.add(VmInstance.convert(vmListJSON.getJSONObject(i), entity));
        }
        return vmList;
    }
}
