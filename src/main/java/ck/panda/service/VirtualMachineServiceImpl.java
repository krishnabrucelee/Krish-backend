package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EventTypes;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Event;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.ResourceLimitDepartment.ResourceType;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.repository.jpa.VirtualMachineRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackInstanceService;
import ck.panda.util.CloudStackIsoService;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackServer;
import ck.panda.util.CloudStackUserService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Virtual Machine service provides deploy instance, update instance, start/stop instance, reboot/reinstall,
 * attachIso/detach iso,etc.,.
 */
@Service
public class VirtualMachineServiceImpl implements VirtualMachineService {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineServiceImpl.class);

    /** Constant for max io disk offering. */
    public static final String CS_MAX_IOPS_DO = ".maxIopsDo";

    /** Constant for min io disk offering. */
    public static final String CS_MIN_IOPS_DO = ".minIopsDo";

    /** Constant for empty string search. */
    public static final String EMPTY_SEARCH_FILTER = "";

    /** Cloud stack user key response. */
    public static final String USER_KEYS = "userkeys";

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Host service reference. */
    @Autowired
    private HostService hostService;

    /** Virtual machine repository reference. */
    @Autowired
    private VirtualMachineRepository virtualmachinerepository;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Volume service reference. */
    @Autowired
    private VolumeService volumeService;

    /** User service reference. */
    @Autowired
    private UserService userService;

    /** Project service reference. */
    @Autowired
    private ProjectService projectService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Quota limit validation reference. */
    @Autowired
    private QuotaValidationService quotaLimitValidation;

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

    /** Hypervisor service reference. */
    @Autowired
    private HypervisorService hypervisorService;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** Sync Service reference. */
    @Autowired
    private SyncService syncService;

    /** Nic service reference. */
    @Autowired
    private NicService nicService;

    /** Cloud stack user service. */
    @Autowired
    private CloudStackUserService cloudStackUserService;

    @Override
    @PreAuthorize("hasPermission(#vmInstance.getSyncFlag(), 'CREATE_VM')")
    public VmInstance save(VmInstance vmInstance) throws Exception {
        // 1. Sync call entity save.
        return virtualmachinerepository.save(convertEncryptPassword(vmInstance));
    }
    /**
     * Create new instance from panda.
     *
     * @param vmInstance vmInstance to save.
     * @param userId created user.
     * @return vm Instance.
     * @throws Exception unhandled exception.
     */
    @Override
    @PreAuthorize("hasPermission(#vmInstance.getSyncFlag(), 'CREATE_VM')")
    public VmInstance saveVmInstance(VmInstance vmInstance, Long userId) throws Exception {
        // 2. Entity validation.
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.ENTITY_VMINSTANCE, vmInstance);
        errors = validator.validateEntity(vmInstance, errors);
        errors = this.validateName(errors, vmInstance.getName(), vmInstance.getDepartment(), 0L);
        if (errors.hasErrors()) {
            // 2.1 If there is entity mismatch then it throws error .
            throw new ApplicationException(errors);
        } else {
            HashMap<String, String> optionalMap = new HashMap<String, String>();
            optionalMap.put(CloudStackConstants.CS_ZONE_ID,
                    convertEntityService.getZoneById(vmInstance.getZoneId()).getUuid());
            if (vmInstance.getNetworkUuid().contains(",")) {
                String[] networkIds = vmInstance.getNetworkUuid().split(",");
                vmInstance.setNetworkId(convertEntityService.getNetworkByUuid(networkIds[0]));
            } else {
                vmInstance.setNetworkId(convertEntityService.getNetworkByUuid(vmInstance.getNetworkUuid()));
            }
            // check department and project quota validation.
            ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService.findByDepartmentAndResourceType(
                    vmInstance.getDepartmentId(), ResourceType.valueOf("Instance"), true);
            ResourceLimitProject projectLimit = resourceLimitProjectService
                    .findByProjectAndResourceType(vmInstance.getProjectId(), ResourceLimitProject.ResourceType.Instance, true);
            if (departmentLimit != null) {
                if (!convertEntityService.getDepartmentById(vmInstance.getDepartmentId())
                        .getType().equals(AccountType.ROOT_ADMIN)) {
                    if (vmInstance.getProjectId() != null) {
                        if (projectLimit != null) {
                            quotaLimitValidation.QuotaLimitCheckByResourceObject(vmInstance, "Instance",
                                    vmInstance.getProjectId(), "Project");
                        } else {
                            errors.addGlobalError(
                                    "Resource limit for project has not been set. Please update project quota");
                            throw new ApplicationException(errors);
                        }
                    } else {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(vmInstance, "Instance",
                                vmInstance.getDepartmentId(), "Department");
                    }
                }
                // 3. Check the resource availability to deploy new vm.
                String isAvailable = isResourceAvailable(vmInstance, optionalMap);
                if (isAvailable != null) {
                    // 3.1 throws error message about resource shortage.
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, isAvailable);
                } else {
                    // 4. set optionalMap arguments for deploy vm API call.
                    optionalMap.clear();
                    vmInstance.setDisplayName(vmInstance.getName());
                    vmInstance.setInstanceUserName(userService.find(vmInstance.getInstanceOwnerId()).getUserName());
                    vmInstance.setInstanceOsType(convertEntityService.getTemplateById(vmInstance.getTemplateId()).getDisplayText());
                    optionalMap.put(CloudStackConstants.CS_NAME, vmInstance.getName());
                    optionalMap.put(CloudStackConstants.CS_NETWORK_IDS, vmInstance.getNetworkUuid());
                    optionalMap.put(CloudStackConstants.CS_DISPLAY_VM, CloudStackConstants.CS_ACTIVE_VM);
                    optionalMap.put(CloudStackConstants.CS_KEYBOARD_TYPE, CloudStackConstants.KEYBOARD_VALUE);
                    optionalMap.put(CloudStackConstants.CS_NAME, vmInstance.getName());
                    if (vmInstance.getHypervisorId() != null) {
                        optionalMap.put(CloudStackConstants.CS_HYPERVISOR_TYPE,
                                hypervisorService.find(vmInstance.getHypervisorId()).getName());
                    }
                    if (vmInstance.getProjectId() != null) {
                        optionalMap.put(CloudStackConstants.CS_PROJECT_ID,
                                convertEntityService.getProjectById(vmInstance.getProjectId()).getUuid());
                    } else {
                        optionalMap.put(CloudStackConstants.CS_ACCOUNT,
                                convertEntityService.getDepartmentById(vmInstance.getDepartmentId()).getUserName());
                        optionalMap.put(CloudStackConstants.CS_DOMAIN_ID,
                                convertEntityService.getDomainById(vmInstance.getDomainId()).getUuid());
                    }
                    if (vmInstance.getStorageOfferingId() != null) {
                        this.customStorageForInstance(vmInstance, optionalMap);
                    }
                    if (vmInstance.getComputeOfferingId() != null) {
                        this.customComputeForInstance(vmInstance, optionalMap);
                    }
                    config.setUserServer();
                    // 5. Get response from CS for new deploy vm API call.
                    String csResponse = cloudStackInstanceService.deployVirtualMachine(
                            convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getUuid(),
                            convertEntityService.getTemplateById(vmInstance.getTemplateId()).getUuid(),
                            convertEntityService.getZoneById(vmInstance.getZoneId()).getUuid(),
                            CloudStackConstants.JSON, optionalMap);
                    JSONObject csInstance = new JSONObject(csResponse).getJSONObject(CloudStackConstants.CS_VM_DEPLOY);
                    if (csInstance.has(CloudStackConstants.CS_ERROR_CODE)) {
                        errors = this.validateEvent(errors, csInstance.getString(CloudStackConstants.CS_ERROR_TEXT));
                        throw new ApplicationException(errors);
                    } else {
                        LOGGER.debug("VM UUID", csInstance.getString(CloudStackConstants.CS_ID));
                        vmInstance.setUuid(csInstance.getString(CloudStackConstants.CS_ID));
                        config.setUserServer();
                        String instanceResponse = cloudStackInstanceService.queryAsyncJobResult(
                                csInstance.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                        JSONObject instance = new JSONObject(instanceResponse)
                                .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                        vmInstance.setEventMessage(csInstance.getString(CloudStackConstants.CS_JOB_ID));
                        if (instance.getString(CloudStackConstants.CS_JOB_STATUS)
                                .equals(GenericConstants.ERROR_JOB_STATUS)) {
                            vmInstance.setStatus(Status.valueOf(EventTypes.EVENT_ERROR.toUpperCase()));
                            vmInstance.setEventMessage(csInstance.getJSONObject(CloudStackConstants.CS_ERROR_TEXT)
                                    .getString(CloudStackConstants.CS_ERROR_TEXT));
                            // 3.1 throws error message about resource
                            // shortage.
                            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                                    instance.getString(CloudStackConstants.CS_ERROR_TEXT));

                        } else {
                            vmInstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_CREATE.toUpperCase()));
                        }
                    }
                }
                // 5.4 Save entity with CS response.
                return virtualmachinerepository.save(convertEncryptPassword(vmInstance));
            } else {
                if (!convertEntityService.getDepartmentById(vmInstance.getDepartmentId())
                        .getType().equals(AccountType.ROOT_ADMIN)) {
                    errors.addGlobalError("Resource limit for department has not been set. Please update department quota");
                }
                throw new ApplicationException(errors);
            }
        }
    }

    /**
     * Custom compute offering for a vm instance.
     *
     * @param vmInstance object for compute.
     * @param optionalMap values to be mapped with the vm instance.
     * @return optional values for vm instance.
     * @throws Exception if error occurs.
     */
    private HashMap<String, String> customComputeForInstance(VmInstance vmInstance,
            HashMap<String, String> optionalMap) throws Exception {
        // If it customized compute offering then assgin value for memory, speed, core in Instance.
        if (convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getCustomized()) {
             optionalMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CloudStackConstants.CS_CUSTOM_CORE, vmInstance.getCpuCore().toString());
             optionalMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CloudStackConstants.CS_CUSTOM_CPU, vmInstance.getCpuSpeed().toString());
             optionalMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CloudStackConstants.CS_CUSTOM_MEMORY, vmInstance.getMemory().toString());
        }
        // If it is customized iops in Compute offering then assign value for min and max iops value in Instance.
        if (convertEntityService.getComputeOfferById(vmInstance.getComputeOfferingId()).getCustomizedIops()) {
            optionalMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CloudStackConstants.CS_MAX_IOPS_REQUEST, vmInstance.getComputeMaxIops().toString());
            optionalMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CloudStackConstants.CS_MIN_IOPS_REQUEST, vmInstance.getComputeMinIops().toString());
        }
        return optionalMap;
    }

    @Override
    public VmInstance update(VmInstance vmInstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.ENTITY_VMINSTANCE, vmInstance);
        errors = validator.validateEntity(vmInstance, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return virtualmachinerepository.save(convertEncryptPassword(vmInstance));
        }
    }

    @Override
    public void delete(VmInstance vmInstance) throws Exception {
        vmInstance.setStatus(Status.valueOf(EventTypes.EVENT_STATUS_EXPUNGING.toUpperCase()));
        vmInstance.setIsRemoved(true);
        virtualmachinerepository.save(vmInstance);
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
     * VM related all quick actions are handled here such as stop, reboot, re-install, destroy, deploy new VM.
     *
     * @throws Exception if unhandled exception occurs.
     */
    @Override
    public VmInstance handleAsyncJobByEventName(String vmId, String event) throws Exception {
        HashMap<String, String> optionalMap = new HashMap<String, String>();
        Errors errors = null;
        String instanceResponse = null;
        JSONObject instance = null;
        String jobState = null;
        VmInstance vmInstance = getCSConnector(vmId);
        if (vmInstance.getProject() != null) {
            optionalMap.put(CloudStackConstants.CS_PROJECT_ID, vmInstance.getProject().getUuid());
        }
        switch (event) {
        // Stops a virtual machine.
        case EventTypes.EVENT_VM_STOP:
            optionalMap.put(CloudStackConstants.CS_FORCED_STOP, CloudStackConstants.CS_ACTIVE_VM);
            config.setUserServer();
            instanceResponse = cloudStackInstanceService.stopVirtualMachine(vmInstance.getUuid(),
                    CloudStackConstants.JSON, optionalMap);
            instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_STOP_RESPONSE);
            jobStatus(instance, vmInstance);
            jobState = jobStatus(instance, vmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                vmInstance.setEventMessage("");
            }
            break;
        // Reboots a virtual machine.
        case EventTypes.EVENT_VM_REBOOT:
            config.setUserServer();
            instanceResponse = cloudStackInstanceService.rebootVirtualMachine(vmInstance.getUuid(),
                    CloudStackConstants.JSON);
            instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_REBOOT_RESPONSE);
            jobState = jobStatus(instance, vmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                vmInstance.setEventMessage("");
            }
            break;
        // Re-install a virtual machine.
        case EventTypes.EVENT_VM_RESTORE:
                config.setUserServer();
                instanceResponse = cloudStackInstanceService.restoreVirtualMachine(vmInstance.getUuid(),
                        CloudStackConstants.JSON);
                instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_RESTORE_RESPONSE);
                jobState = jobStatus(instance, vmInstance);
                if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                        && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                    vmInstance.setEventMessage("Re-installed");
                }

            break;
        // Destroys a virtual machine.
        case EventTypes.EVENT_VM_DESTROY:
            config.setUserServer();
            instanceResponse = cloudStackInstanceService.destroyVirtualMachine(vmInstance.getUuid(),
                    CloudStackConstants.JSON, optionalMap);
            instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_DESTROY_RESPONSE);
            jobState = jobStatus(instance, vmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                vmInstance.setEventMessage("Vm destroyed");
            }
            break;
        // Destroys and expunge a virtual machine.
        case EventTypes.EVENT_VM_EXPUNGE:
            optionalMap.put(CloudStackConstants.CS_VM_ENPUNGE, CloudStackConstants.CS_ACTIVE_VM);
            config.setUserServer();
            instanceResponse = cloudStackInstanceService.destroyVirtualMachine(vmInstance.getUuid(),
                    CloudStackConstants.JSON, optionalMap);
            instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_DESTROY_RESPONSE);
            jobState = jobStatus(instance, vmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                vmInstance.setEventMessage("VM EXPUNGING");
            }
            break;
        // Creates a virtual machine.
        case EventTypes.EVENT_VM_CREATE:
            // check department and project quota validation.
            ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService.findByDepartmentAndResourceType(
                    vmInstance.getDepartmentId(), ResourceType.valueOf("Instance"), true);
            if (departmentLimit != null) {
                if (!convertEntityService.getDepartmentById(vmInstance.getDepartmentId())
                        .getType().equals(AccountType.ROOT_ADMIN)) {
                    if (vmInstance.getProjectId() != null) {
    //                    syncService
    //                            .syncResourceLimitProject(convertEntityService.getProjectById(vmInstance.getProjectId()));
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(vmInstance, "RestoreInstance",
                                vmInstance.getProjectId(), "Project");
                    } else {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(vmInstance, "RestoreInstance",
                                vmInstance.getDepartmentId(), "Department");
                    }
                }
                // 3. Check the resource availability to deploy new vm.
                String isAvailable = isResourceAvailable(vmInstance, optionalMap);
                if (isAvailable != null) {
                    // 3.1 throws error message about resource shortage.
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, isAvailable);
                } else {
                    config.setUserServer();
                    instanceResponse = cloudStackInstanceService.recoverVirtualMachine(vmInstance.getUuid(),
                            CloudStackConstants.JSON);
                    instance = new JSONObject(instanceResponse)
                            .getJSONObject(CloudStackConstants.CS_VM_RECOVER_RESPONSE);
                    if (instance.has(CloudStackConstants.CS_ERROR_CODE)) {
                        vmInstance.setEventMessage(instance.getString(CloudStackConstants.CS_ERROR_TEXT));
                        virtualmachinerepository.save(convertEncryptPassword(vmInstance));
                        errors = validator.sendGlobalError(instance.getString(CloudStackConstants.CS_ERROR_TEXT));
                        if (errors.hasErrors()) {
                            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                                    instance.getString(CloudStackConstants.CS_ERROR_TEXT));
                        }
                    } else {
                        vmInstance.setEventMessage("VM Recover");
                    }
                }
            } else {
                errors.addGlobalError("Resource limit for department has not been set. Please update department quota");
                throw new ApplicationException(errors);
            }
            break;
        default:
            LOGGER.debug("No VM Action ", event);
        }
        return virtualmachinerepository.save(convertEncryptPassword(vmInstance));
    }

    /**
     * VM related all quick actions are handled here such as start, migrate host, attach iso, detach iso, reset
     * password, assign application.
     *
     * @throws Exception if unhandled exception occurs.
     */
    @Override
    public VmInstance handleAsyncJobByVM(VmInstance vmInstance, String event, Long userId) throws Exception {
        HashMap<String, String> optionalMap = new HashMap<String, String>();
        JSONObject instance = null;
        String instanceResponse = null;
        String jobState = null;
        VmInstance persistVmInstance = getCSConnector(vmInstance.getUuid());
        User user = convertEntityService.getOwnerById(userId);
        if (persistVmInstance.getProject() != null) {
            optionalMap.put(CloudStackConstants.CS_PROJECT_ID, persistVmInstance.getProject().getUuid());
        }
        switch (event) {
        // Starts a virtual machine.
        case EventTypes.EVENT_VM_START:
            if (user != null && user.getType().equals(UserType.ROOT_ADMIN)) {
                if (!vmInstance.getHostUuid().isEmpty() && vmInstance.getHostUuid() != null) {
                    optionalMap.put("hostid", vmInstance.getHostUuid());
                }
            }
            config.setUserServer();
            instanceResponse = cloudStackInstanceService.startVirtualMachine(persistVmInstance.getUuid(),
                    CloudStackConstants.JSON, optionalMap);
            instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_START_RESPONSE);
            jobState = jobStatus(instance, persistVmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                if (jobState.equals(GenericConstants.PROGRESS_JOB_STATUS)) {
                    persistVmInstance.setEventMessage("VM starting");
                }
            }
            break;
         // Stops a virtual machine.
        case EventTypes.EVENT_VM_STOP:
            if (vmInstance.getTransForcedStop()) {
                optionalMap.put(CloudStackConstants.CS_FORCED_STOP, CloudStackConstants.CS_ACTIVE_VM);
            }
            config.setUserServer();
            instanceResponse = cloudStackInstanceService.stopVirtualMachine(persistVmInstance.getUuid(),
                    CloudStackConstants.JSON, optionalMap);
            instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_STOP_RESPONSE);
            jobState = jobStatus(instance, persistVmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                persistVmInstance.setEventMessage("stopping vm");
            }
            break;
        // Migrates host of a virtual machine from one to another.
        case EventTypes.EVENT_VM_MIGRATE:
            if (persistVmInstance.getStatus().equals(Status.RUNNING)) {
                if (hostService.findAll().size() <= 1) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, "warning.for.no.host.migration");
                }
                optionalMap.put(CloudStackConstants.CS_HOST_ID, vmInstance.getHostUuid());
                config.setUserServer();
                instanceResponse = cloudStackInstanceService.migrateVirtualMachine(vmInstance.getUuid(),
                        optionalMap);
                instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_MIGRATE_RESPONSE);
                jobState = jobStatus(instance, persistVmInstance);
                if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                        && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                    if (jobState.equals(GenericConstants.PROGRESS_JOB_STATUS)) {
                        persistVmInstance.setStatus(Status.MIGRATING);
                    }
                }
            } else {
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, "warning.for.host.migration");

            }
            break;
        // Attaches ISO to a virtual machine.
        case EventTypes.EVENT_ISO_ATTACH:
            instanceResponse = csIso.attachIso(vmInstance.getIso(), vmInstance.getUuid(), CloudStackConstants.JSON);
            instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_ATTACHISO_RESPONSE);
            jobState = jobStatus(instance, persistVmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                if (jobState.equals(GenericConstants.SUCCEEDED_JOB_STATUS)
                        || jobState.equals(GenericConstants.PROGRESS_JOB_STATUS)) {
                    persistVmInstance.setIsoName(vmInstance.getIso());
                    persistVmInstance.setIso(vmInstance.getIso());
                }
            }
            break;
        // Detaches ISO from a virtual machine.
        case EventTypes.EVENT_ISO_DETACH:
            instanceResponse = csIso.detachIso(vmInstance.getUuid(), CloudStackConstants.JSON);
            instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_DETACHISO_RESPONSE);
            jobState = jobStatus(instance, persistVmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                if (jobState.equals(GenericConstants.SUCCEEDED_JOB_STATUS)) {
                    persistVmInstance.setIsoName(null);
                    persistVmInstance.setIso(null);
                }
            }
            break;
        // Reset password for a virtual machine.
        case EventTypes.EVENT_VM_RESETPASSWORD:
            if (persistVmInstance.getStatus().equals(Status.STOPPED)) {
                config.setUserServer();
                instanceResponse = cloudStackInstanceService.resetPasswordForVirtualMachine(vmInstance.getUuid());
                instance = new JSONObject(instanceResponse).getJSONObject(CloudStackConstants.CS_VM_RESET_PASSWORD_RESPONSE);
                jobState = jobStatus(instance, persistVmInstance);
                if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                        && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                    if (jobState.equals(GenericConstants.SUCCEEDED_JOB_STATUS)) {
                        String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(GenericConstants.CHARACTER_ENCODING));
                        byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
                        SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, GenericConstants.ENCRYPT_ALGORITHM);
                        config.setUserServer();
                        String instances = cloudStackInstanceService.queryAsyncJobResult(
                                instance.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                        JSONObject jobresult = new JSONObject(instances)
                                .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                        String encryptedPassword = new String(
                                EncryptionUtil.encrypt(jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                        .getJSONObject(CloudStackConstants.CS_VM).getString(CloudStackConstants.CS_PASSWORD), originalKey));
                        persistVmInstance.setVncPassword(encryptedPassword);
                        virtualmachinerepository.save(persistVmInstance);
                        throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                                "success.message.for.vm.password.update");
                    }
                }
            } else {
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, "warning.for.reset.password");
            }
            break;
        // Assign application name and application type to a virtual machine.
        case EventTypes.EVENT_ADD_APPLICATION:
            persistVmInstance.setApplication(vmInstance.getApplication());
            persistVmInstance.setApplicationList(vmInstance.getApplicationList());
            break;
        default:
            LOGGER.debug("No VM Action for ", event);
        }
        return virtualmachinerepository.save(convertEncryptPassword(persistVmInstance));
    }

    @Override
    public void delete(Long id) throws Exception {
        virtualmachinerepository.delete(id);
    }

    @Override
    public VmInstance find(Long id) throws Exception {
        return virtualmachinerepository.findVMByID(id);
    }

    @Override
    public Page<VmInstance> findAllVM(PagingAndSorting pagingAndSorting, Long userId) throws Exception {
        // Get user information from token details.
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                // List all Vms are not in expunging state for domain admin.
                Page<VmInstance> allInstanceList = virtualmachinerepository
                        .findAllByDepartmentAndExceptStatusWithPageRequest(user.getDomainId(), Status.EXPUNGING,
                                pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                return allInstanceList;
            } else {
                // Get active project list for current user.
                if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<Project> projectList = new ArrayList<Project>();
                    for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                        // List all Vms are not in expunging state by project and/or department.
                        projectList.add(project);
                    }
                    Page<VmInstance> allInstanceTempList = virtualmachinerepository
                            .findAllByDepartmentAndProjectAndExceptStatusAndPage(Status.EXPUNGING, user.getDepartment(),
                                    projectList, pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                    return (Page<VmInstance>) allInstanceTempList;
                } else {
                    // List all Vms are not in expunging state for ROOT admin.
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndExceptStatusWithPageRequest(user.getDepartmentId(), Status.EXPUNGING,
                                    pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                    return (Page<VmInstance>) allInstanceLists;
                }
            }
        }
        return virtualmachinerepository.findAllByExceptStatusWithPageRequest(Status.EXPUNGING,
                pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<VmInstance> findAllByStatus(PagingAndSorting pagingAndSorting, Status status, Long userId)
            throws Exception {
        // Get user information from token details.
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                // List all Vms by status for domain admin.
                Page<VmInstance> allInstanceList = virtualmachinerepository.findAllByDomainAndStatusWithPageRequest(
                        user.getDomainId(), status, pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                return allInstanceList;
            } else {
                // Get active project list for current user.
                if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<Project> projectList = new ArrayList<Project>();
                    for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                        // List all Vms for the current user by status and project and/or department.
                        projectList.add(project);
                    }
                    Page<VmInstance> allInstanceList = virtualmachinerepository
                            .findAllByDepartmentAndProjectAndStatusAndPage(status, user.getDepartment(), projectList,
                                    pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                    return (Page<VmInstance>) allInstanceList;
                } else {
                    // List all Vms by status for ROOT admin.
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndStatusWithPageRequest(status, user.getDepartment(),
                                    pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                    return (Page<VmInstance>) allInstanceLists;
                }

            }
        }
        return virtualmachinerepository.findAllByStatusWithPageRequest(status, pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VmInstance> findAllVMList(Long userId) throws Exception {
        try {
            // Get user information from token details.
            User user = convertEntityService.getOwnerById(userId);
            if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
                if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                    // List all Vms are not in expunging state for domain admin.
                    List<VmInstance> allInstanceList = virtualmachinerepository
                            .findAllByDomainAndExceptStatus(user.getDomainId(), Status.EXPUNGING, EMPTY_SEARCH_FILTER);
                    return allInstanceList;
                } else {
                    if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                        List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                        // Get active project list for current user.
                        for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                            // List all Vms are not in expunging state by project and/or department.
                            List<VmInstance> allInstanceTempList = virtualmachinerepository
                                    .findAllByDepartmentAndProjectAndExceptStatus(Status.EXPUNGING,
                                            user.getDepartment(), project, EMPTY_SEARCH_FILTER);
                            allInstanceList.addAll(allInstanceTempList);
                        }
                        // List all Vms are not in expunging state by current user, belongs to department and project.
                        List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                        return instances;
                    } else {
                        // List all Vms are not in expunging state for ROOT admin.
                        List<VmInstance> allInstanceLists = virtualmachinerepository
                                .findAllByDepartmentAndExceptStatus(Status.EXPUNGING, user.getDepartment(), EMPTY_SEARCH_FILTER);
                        return allInstanceLists;
                    }
                }
            }
        } catch (NumberFormatException e) {
        } catch (Exception e) {
        }
        return (List<VmInstance>) virtualmachinerepository.findAllByExceptStatus(Status.EXPUNGING);
    }

    @Override
    public VmInstance findByUUID(String uuid) {
        return virtualmachinerepository.findByUUID(uuid);
    }

    @Override
    @PreAuthorize("hasPermission(null, 'RESIZE')")
    public VmInstance upgradeDowngradeVM(VmInstance vmInstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.ENTITY_VMINSTANCE, vmInstance);
        errors = validator.validateEntity(vmInstance, errors);
        HashMap<String, String> optionalMap = new HashMap<String, String>();
        if (vmInstance.getComputeOfferingId() != null) {
            optionalMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CloudStackConstants.CS_CUSTOM_CORE, vmInstance.getCpuCore().toString());
            optionalMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CloudStackConstants.CS_CUSTOM_CPU, vmInstance.getCpuSpeed().toString());
            optionalMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CloudStackConstants.CS_CUSTOM_MEMORY, vmInstance.getMemory().toString());
        }
        // CS API call for Upgrade or downgrade the compute offer plan of an instance.
        config.setUserServer();
        String scaleVm = cloudStackInstanceService.scaleVirtualMachine(vmInstance.getUuid(),
                vmInstance.getComputeOffering().getUuid(), CloudStackConstants.JSON, optionalMap);
        JSONObject jobId = new JSONObject(scaleVm).getJSONObject(CloudStackConstants.SCALE_VM_RESPONSE);
        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            String jobState = jobStatus(jobId, vmInstance);
            if (!jobState.equals(GenericConstants.DEFAULT_JOB_STATUS)
                    && !jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
            }
                if (jobState.equals(GenericConstants.SUCCEEDED_JOB_STATUS)
                        || jobState.equals(GenericConstants.PROGRESS_JOB_STATUS)) {
                    vmInstance.setComputeOfferingId(vmInstance.getComputeOffering().getId());
                }
                virtualmachinerepository.save(convertEncryptPassword(vmInstance));

        }
        return vmInstance;
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
        // Check uniqueness of an instance name on department.
        if ((virtualmachinerepository.findByNameAndDepartment(name, department, Status.EXPUNGING)) != null) {
            errors.addGlobalError("error.for.instance.unique.in.department" + department.getUserName());
        }
        return errors;
    }

    @Override
    public List<VmInstance> findAllFromCSServer() throws Exception {
        List<VmInstance> vmList = new ArrayList<VmInstance>();
        List<Project> project = projectService.findAllByActive(true);
        HashMap<String, String> vmMap = new HashMap<String, String>();
        List<VmInstance> csVmList = null;
        if (project.size() > 0) {
            for (int j = 0; j < project.size(); j++) {
                vmMap.clear();
                vmMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
                vmMap.put(CloudStackConstants.CS_PROJECT_ID, project.get(j).getUuid());
                csVmList = updateVmFromCSServer(vmMap);
                if (csVmList != null) {
                    vmList.addAll(csVmList);
                }
            }
        }
        vmMap.clear();
        vmMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        csVmList = updateVmFromCSServer(vmMap);
        if (csVmList != null) {
            vmList.addAll(csVmList);
        }
        return vmList;
    }

    /**
     * Get count of instance by status.
     */
    @Override
    public Integer findCountByStatus(Status status, Long userId) {
        try {
            // Get user information from token details.
            User user = convertEntityService.getOwnerById(userId);
            if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
                if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                    // Vms count by status for domain admin.
                    List<VmInstance> allInstanceList = virtualmachinerepository
                            .findAllByDomainAndStatus(user.getDomainId(), status, EMPTY_SEARCH_FILTER);
                    return allInstanceList.size();
                } else {
                    // Get active project list for current user.
                    if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                        List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                        for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                            List<VmInstance> allInstanceTempList = virtualmachinerepository
                                    .findAllByDepartmentAndProjectAndStatus(status, user.getDepartment(), project, EMPTY_SEARCH_FILTER);
                            allInstanceList.addAll(allInstanceTempList);
                        }
                        List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                        // Vms count by status belongs to the project and/or department.
                        return instances.size();
                    } else {
                        List<VmInstance> allInstanceLists = virtualmachinerepository
                                .findAllByDepartmentAndStatus(status, user.getDepartment(), EMPTY_SEARCH_FILTER);
                        // Vms count by status for an ROOT admin.
                        return allInstanceLists.size();
                    }
                }
            }
        } catch (NumberFormatException e) {
        } catch (Exception e) {
        }
        return virtualmachinerepository.findCountByStatus(status);
    }

    @Override
    public VmInstance updateDisplayName(VmInstance vminstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.ENTITY_VMINSTANCE, vminstance);
        errors = validator.validateEntity(vminstance, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            // VM instance display name update.
            if (vminstance.getDisplayName() != null && !vminstance.getDisplayName().trim().equalsIgnoreCase("")) {
                HashMap<String, String> optionalMap = new HashMap<String, String>();
                optionalMap.put(CloudStackConstants.CS_VM_DISPLAYNAME, vminstance.getDisplayName());
                config.setUserServer();
                cloudStackInstanceService.updateVirtualMachine(vminstance.getUuid(), optionalMap);
            }
            return virtualmachinerepository.save(convertEncryptPassword(vminstance));
        }
    }

    @Override
    public List<VmInstance> findAllByProjectAndStatus(Long projectId, List<Status> statusCode) throws Exception {
        return virtualmachinerepository.findByProjectAndStatus(projectId, statusCode);
    }

    @Override
    public List<VmInstance> findAllByDepartmentAndStatus(Long departmentId, List<Status> statusCode) throws Exception {
        return virtualmachinerepository.findByDepartmentAndStatus(departmentId, statusCode);
    }

    /**
     * Check resouce capacity to create new VM instance.
     *
     * @param vm vm instance.
     * @param optionalMap arguments.
     * @return error message.
     * @throws Exception unhandled errors.
     */
    public String isResourceAvailable(VmInstance vm, HashMap<String, String> optionalMap) throws Exception {
        Long resourceUsage = 0L, tempCount = 0L;
        String errMessage = null;
        // 1. Initiate CS server connection as ROOT admin.
        config.setServer(1L);
        // 2. List capacity CS API call.
        String csResponse = cloudStackResourceCapacity.listCapacity(optionalMap, CloudStackConstants.JSON);
        JSONObject csCapacity = new JSONObject(csResponse).getJSONObject(CloudStackConstants.CS_CAPACITY_LIST_RESPONSE);
        if (csCapacity.has(CloudStackConstants.CS_CAPACITY)) {
            JSONArray capacityArrayJSON = csCapacity.getJSONArray(CloudStackConstants.CS_CAPACITY);
            for (int i = 0, size = capacityArrayJSON.length(); i < size; i++) {
                String resourceType = capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CAPACITY_TYPE);
                // 2.1 Total capacity in puplic pool for each resource type.
                Long tempTotalCapacity = Long.valueOf(capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CS_CAPACITY_TOTAL));
                // 2.2 Used capacity in puplic pool for each resource type.
                Long tempCapacityUsed = Long.valueOf(capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CS_CAPACITY_USED));
                if (GenericConstants.RESOURCE_CAPACITY.containsKey(resourceType)) {
                    // 3.1 Total available resource in puplic pool for each resource type.
                    resourceUsage = tempTotalCapacity - tempCapacityUsed;
                    // 4. Check whether resource is available to deploy new VM with resource type.
                    switch (resourceType) {
                    // 4.1 Check memory availability.
                    case GenericConstants.RESOURCE_MEMORY:
                        if (convertEntityService.getComputeOfferById(vm.getComputeOfferingId()).getCustomized()) {
                            if (resourceUsage < Long.valueOf(vm.getMemory())) {
                                errMessage = CloudStackConstants.RESOURCE_CHECK + " memory.available " + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                            }
                        } else if (resourceUsage < Long.valueOf(convertEntityService
                                .getComputeOfferById(vm.getComputeOfferingId()).getMemory().toString())) {
                            errMessage = CloudStackConstants.RESOURCE_CHECK + " memory.available " + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                        }
                        break;
                    // 4.2 Check cpu availability.
                    case GenericConstants.RESOURCE_CPU:
                        if (convertEntityService.getComputeOfferById(vm.getComputeOfferingId()).getCustomized()) {
                            if (Long.valueOf(vm.getCpuSpeed()) > resourceUsage) {
                                errMessage = CloudStackConstants.RESOURCE_CHECK + " cpu.available " + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                            }
                        } else if (Long.valueOf(convertEntityService.getComputeOfferById(vm.getComputeOfferingId())
                                .getClockSpeed().toString()) > resourceUsage) {
                            errMessage = CloudStackConstants.RESOURCE_CHECK + " cpu.available " + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                        }
                        break;
                    // 4.4 Check primary storage availability.
                    case GenericConstants.RESOURCE_PRIMARY_STORAGE:
                        if (vm.getStorageOfferingId() != null) {
                            if (convertEntityService.getStorageOfferById(vm.getStorageOfferingId()).getIsCustomDisk()) {
                                if (resourceUsage < vm.getDiskSize() + convertEntityService.getTemplateById(vm.getTemplateId()).getSize()) {
                                    errMessage = CloudStackConstants.RESOURCE_CHECK + " primary.storage.available " + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                                }
                            } else if (resourceUsage < convertEntityService.getStorageOfferById(vm.getStorageOfferingId()).getDiskSize() + convertEntityService.getTemplateById(vm.getTemplateId()).getSize()) {
                                errMessage = CloudStackConstants.RESOURCE_CHECK + " primary.storage.available " + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                            }
                        } else {
                            if (vm.getTemplateId() != null) {
                                if (resourceUsage < convertEntityService.getTemplateById(vm.getTemplateId())
                                        .getSize()) {
                                    errMessage = CloudStackConstants.RESOURCE_CHECK + " primary.storage.available "
                                            + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                                }
                            }
                        }
                        break;
                    // 4.5 Check public ip address availability.
                    case GenericConstants.RESOURCE_IP_ADDRESS:
                        if (vm.getNetworkUuid() != null || vm.getNetworkId() != null) {
                            if (convertEntityService.getNetworkById(vm.getNetworkId()).getVpcId() == null) {
                                if (vm.getNetworkUuid() != null) {
                                    optionalMap.put(CloudStackConstants.CS_ASSOCIATE_NETWORK, vm.getNetworkUuid());
                                } else {
                                    optionalMap.put(CloudStackConstants.CS_ASSOCIATE_NETWORK,
                                            convertEntityService.getNetworkById(vm.getNetworkId()).getUuid());
                                }
                                if (vm.getProjectId() != null) {
                                    optionalMap.put("projectid",
                                            convertEntityService.getProjectById(vm.getProjectId()).getUuid());
                                }
                                optionalMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
                                optionalMap.put(CloudStackConstants.CS_FOR_VM_NETWORK,
                                        CloudStackConstants.STATUS_ACTIVE);
                                config.setServer(1L);
                                String csIpResponse = cloudStackResourceCapacity.listPublicIpAddress(optionalMap,
                                        CloudStackConstants.JSON);
                                JSONObject csIpCapacity = new JSONObject(csIpResponse)
                                        .getJSONObject(CloudStackConstants.CS_PUBLIC_IPADDRESS_RESPONSE);
                                if (csIpCapacity.has(CloudStackConstants.CS_CAPACITY_COUNT)) {
                                    LOGGER.debug("Already IP address acquired ", resourceType);
                                } else if (resourceUsage < 1) {
                                    errMessage = CloudStackConstants.RESOURCE_CHECK + " public.ip.available "
                                            + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                                }
                            }
                        }
                        break;
                    default:
                        LOGGER.debug("No Resource ", resourceType);
                    }
                }
            }
        }
        // 5. If any resource shortage then return error message otherwise return empty string.
        return errMessage;
    }

    @Override
    public VmInstance findById(Long id) {
        return virtualmachinerepository.findOne(id);
    }

    @Override
    public List<VmInstance> findAllByStorageOfferingIdAndVmStatus(Long storageOfferingId, Status status)
            throws Exception {
        return virtualmachinerepository.findByStorageOfferingAndStatus(storageOfferingId, status);
    }

    @Override
    public List<VmInstance> findAllByComputeOfferingIdAndVmStatus(Long computeOfferingId, Status status)
            throws Exception {
        return virtualmachinerepository.findByComputeOfferingAndStatus(computeOfferingId, status);
    }

    @Override
    public List<VmInstance> findAllByDepartmentAndVmStatus(Long departmentId, Status status) throws Exception {
        return virtualmachinerepository.findByDepartmentAndStatus(departmentId, status);
    }

    @Override
    public List<VmInstance> findAllByNetworkAndVmStatus(Long networkId, Status status) throws Exception {
        return virtualmachinerepository.findByNetworkAndExceptStatus(networkId, status);
    }

    @Override
    public List<VmInstance> findByVmStatus(List<Status> status, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        return virtualmachinerepository.findAllByDomainAndExceptInStatus( user.getDomainId(), status);
    }

    /**
     * Convert encrypted password and update the password in existing instance entity.
     *
     * @param vminstance instance object.
     * @return VmInstance
     * @throws Exception unhandled errors.
     */
    private VmInstance convertEncryptPassword(VmInstance vminstance) throws Exception {
        // Set password from CS for an instance with AES encryption.
        if (vminstance.getVncPassword() != null && vminstance.getVncPassword().length() < 10) {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(GenericConstants.CHARACTER_ENCODING));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, GenericConstants.ENCRYPT_ALGORITHM);
            String encryptedPassword = new String(EncryptionUtil.encrypt(vminstance.getVncPassword(), originalKey));
            vminstance.setVncPassword(encryptedPassword);
        }
        return vminstance;
    }

    /**
     * Get job status code when asynchronous call initiated for VM related actions.
     *
     * @param instance json instance response.
     * @param vmInstance vm object.
     * @return job status.
     * @throws Exception unhandled errors.
     */
    private String jobStatus(JSONObject instance, VmInstance vmInstance) throws Exception {
        Errors errors = null;
        JSONObject jobresult = null;
        if (instance.has(CloudStackConstants.CS_JOB_ID)) {
            // 1.Get async job result for given job id.
            config.setUserServer();
            String instances = cloudStackInstanceService
                    .queryAsyncJobResult(instance.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
            jobresult = new JSONObject(instances).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
            // 2. Then check whether it has error or not and throws error if job status is 2.
            if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(GenericConstants.ERROR_JOB_STATUS)) {
                errors = validator.sendGlobalError(jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                        .getString(CloudStackConstants.CS_ERROR_TEXT));
                vmInstance.setEventMessage(jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                        .getString(CloudStackConstants.CS_ERROR_TEXT));
                if (errors.hasErrors()) {
                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                            jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                    .getString(CloudStackConstants.CS_ERROR_TEXT));
                } else {
                    virtualmachinerepository.save(convertEncryptPassword(vmInstance));
                }
            } else {
                return jobresult.getString(CloudStackConstants.CS_JOB_STATUS);
            }
        }
        // 3. If there is no job found then will return job status as 10.
        return GenericConstants.DEFAULT_JOB_STATUS;
    }

    @Override
    public List<VmInstance> findAllByExceptStatus(Status status) throws Exception {
        return virtualmachinerepository.findAllByExceptStatus(status);
    }

    @Override
    public Page<VmInstance> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return virtualmachinerepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VmInstance> findAll() throws Exception {
        return (List<VmInstance>) virtualmachinerepository.findAll();
    }

    /**
     * Update VM from cloudStack server.
     *
     * @param vmMap optional arguments
     * @return VM list
     * @throws Exception if error occurs
     */
    public List<VmInstance> updateVmFromCSServer(HashMap<String, String> vmMap) throws Exception {
        List<VmInstance> vmList = new ArrayList<VmInstance>();
        // 1. Get the list of vms from CS server using CS connector
        config.setServer(1L);
        String response = cloudStackInstanceService.listVirtualMachines(CloudStackConstants.JSON, vmMap);
        JSONArray vmListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CloudStackConstants.CS_LIST_VM_RESPONSE);
        if (responseObject.has(CloudStackConstants.CS_VM)) {
            vmListJSON = responseObject.getJSONArray(CloudStackConstants.CS_VM);
            // 2. Iterate the json list, convert the single json entity to vm.
            for (int i = 0, size = vmListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to vm entity.
                VmInstance vmInstance = VmInstance.convert(vmListJSON.getJSONObject(i));
                VmInstance persistInstance = findByUUID(vmInstance.getUuid());
                if (persistInstance != null) {
                    if (volumeService.findByInstanceAndVolumeType(persistInstance.getId()) != null) {
                        vmInstance.setVolumeSize(
                                volumeService.findByInstanceAndVolumeType(persistInstance.getId()).getDiskSize());
                    }
                }
                // 2.2 update vm entity by transient variable.
                vmInstance.setInstanceGuestIp(ipToLong(vmInstance.getIpAddress()));
                vmInstance.setDomainId(convertEntityService.getDomainId(vmInstance.getTransDomainId()));
                vmInstance.setZoneId(convertEntityService.getZoneId(vmInstance.getTransZoneId()));
                vmInstance.setNetworkId(convertEntityService.getNetworkId(vmInstance.getTransNetworkId()));
                vmInstance.setProjectId(convertEntityService.getProjectId(vmInstance.getTransProjectId()));
                if (vmInstance.getTransHypervisor() != null) {
                    if (hypervisorService.findByName(vmInstance.getTransHypervisor()) != null) {
                        vmInstance
                                .setHypervisorId(hypervisorService.findByName(vmInstance.getTransHypervisor()).getId());
                    }
                }
                vmInstance.setDepartmentId(
                        convertEntityService.getDepartmentByUsernameAndDomains(vmInstance.getTransDepartmentId(),
                                convertEntityService.getDomain(vmInstance.getTransDomainId())));
                if (vmInstance.getTransProjectId() != null) {
                    vmInstance.setDepartmentId(convertEntityService.getProject(vmInstance.getTransProjectId()).getDepartmentId());
                }
                vmInstance.setHostId(convertEntityService.getHostId(vmInstance.getTransHostId()));
                vmInstance.setInstanceOwnerId(convertEntityService.getOwnerByUuid(vmInstance.getTransOwnerId()));
                vmInstance.setTemplateId(convertEntityService.getTemplateId(vmInstance.getTransTemplateId()));
                vmInstance.setInstanceOsType(convertEntityService.getTemplateById(convertEntityService.getTemplateId(vmInstance.getTransTemplateId())).getDisplayText());
                vmInstance.setComputeOfferingId(
                        convertEntityService.getComputeOfferId(vmInstance.getTransComputeOfferingId()));
                if (vmInstance.getHostId() != null) {
                    vmInstance.setPodId(convertEntityService
                            .getPodIdByHost(convertEntityService.getHostId(vmInstance.getTransHostId())));
                }
                if (vmInstance.getTemplateId() != null) {
                    vmInstance.setOsType(convertEntityService.getTemplateById(vmInstance.getTemplateId()).getDisplayText());
                    vmInstance.setInstanceOsType(convertEntityService.getTemplateById(vmInstance.getTemplateId()).getDisplayText());
                }
                if (vmInstance.getTransKeypairName() != null) {
                    vmInstance.setKeypairId(convertEntityService.getSSHKeyByNameAndDepartment(vmInstance.getTransKeypairName(), vmInstance.getDepartmentId()).getId());
                }
                // 2.3 and the converted vm entity to list.
                vmList.add(vmInstance);
            }
        }
        return vmList;
    }

    @Override
    public Page<VmInstance> findAllBySort(PagingAndSorting pagingAndSorting, Status status) throws Exception {
        return virtualmachinerepository.findAllByStatus(pagingAndSorting.toPageRequest(), status);
    }

    @Override
    public Page<VmInstance> findAllByUser(PagingAndSorting pagingAndSorting, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                // List all Vms by status for domain admin.
                Page<VmInstance> allInstanceList = virtualmachinerepository.findAllByDomainAndExceptStatus(
                        user.getDomainId(), Status.EXPUNGING, pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                return allInstanceList;
            } else {
                // Get active project list for current user.
                if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<Project> projectList = new ArrayList<Project>();
                    for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                        // List all Vms for the current user by status and project and/or department.
                        projectList.add(project);
                    }
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndProjectAndExceptStatusAndPage(Status.EXPUNGING, user.getDepartment(),
                                    projectList, pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                    return (Page<VmInstance>) allInstanceLists;
                } else {
                    // List all Vms by status for ROOT admin.
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndExceptStatusWithPageRequest(user.getDepartment().getId(),
                                    Status.EXPUNGING, pagingAndSorting.toPageRequest(), EMPTY_SEARCH_FILTER);
                    return (Page<VmInstance>) allInstanceLists;
                }
            }
        }
        return virtualmachinerepository.findAllByExceptStatusWithPageRequest(Status.EXPUNGING, pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VmInstance> findAllByUser(Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                // List all Vms by status for domain admin.
                List<VmInstance> allInstanceList = virtualmachinerepository
                        .findAllByDomainAndExceptStatus(user.getDomainId(), Status.EXPUNGING, EMPTY_SEARCH_FILTER);
                return allInstanceList;
            } else {
                // Get active project list for current user.
                if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                    for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                        // List all Vms for the current user by status and project and/or department.
                        List<VmInstance> allInstanceTempList = virtualmachinerepository
                                .findAllByDepartmentAndProjectAndExceptStatus(Status.EXPUNGING, user.getDepartment(),
                                        project, EMPTY_SEARCH_FILTER);
                        allInstanceList.addAll(allInstanceTempList);
                    }
                    List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                    // List all Vms by status for the current user, belongs to department and project.
                    return instances;
                } else {
                    // List all Vms by status for ROOT admin.
                    List<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndExceptStatus(Status.EXPUNGING, user.getDepartment(), EMPTY_SEARCH_FILTER);
                    return allInstanceLists;
                }
            }
        }
        return (List<VmInstance>) virtualmachinerepository.findAllByExceptStatus(Status.EXPUNGING);
    }

    /**
     * A method a set Custom storage values for instance.
     *
     * @param vmInstance Vm instance object
     * @param instanceMap instance map
     * @return instance map
     * @throws Exception error
     */
    public HashMap<String, String> customStorageForInstance(VmInstance vmInstance, HashMap<String, String> instanceMap) throws Exception {
            instanceMap.put(CloudStackConstants.CS_DISK_OFFERING_ID,
                    convertEntityService.getStorageOfferById(vmInstance.getStorageOfferingId()).getUuid());
            //Check the disk size not null validation and set the disk size
            if (vmInstance.getDiskSize() != null) {
                instanceMap.put(CloudStackConstants.CS_SIZE, vmInstance.getDiskSize().toString());
            }
            //Check the disk Iops (Max and Min) not null validation and set the disk iops
            if (vmInstance.getDiskMaxIops() != null && vmInstance.getDiskMinIops() != null) {
                instanceMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CS_MAX_IOPS_DO,vmInstance.getDiskMaxIops().toString());
                instanceMap.put(CloudStackConstants.CS_CUSTOM_DETAILS + CS_MAX_IOPS_DO, vmInstance.getDiskMinIops().toString());
            }
            return instanceMap;
       }

    @Override
    public VmInstance findByIdWithVncPassword(Long id) throws Exception {
        VmInstance vmInstance = virtualmachinerepository.findOne(id);
        if (vmInstance.getVncPassword() != null) {
            String strEncoded = Base64.getEncoder()
                    .encodeToString(secretKey.getBytes(GenericConstants.CHARACTER_ENCODING));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length,
                    GenericConstants.ENCRYPT_ALGORITHM);
            String decryptPassword = new String(EncryptionUtil.decrypt(vmInstance.getVncPassword(), originalKey));
            vmInstance.setVncPassword(decryptPassword);
        }
        return vmInstance;
    }

    @Override
    public List<VmInstance> findAllByNic(Long networkId) throws Exception {
        // TODO Auto-generated method stub
        List<VmInstance> vmList = new ArrayList<VmInstance>();
        List<Nic> nicList = nicService.findAllByNetworkAndIsActive(networkId, true);
        for (Nic nic : nicList) {
            vmList.add(nic.getVmInstance());
        }
        return vmList;
    }

    @Override
    public Page<VmInstance> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting, String searchText, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                // List all Vms by status for domain admin.
                Page<VmInstance> allInstanceList = virtualmachinerepository.findAllByDomainAndExceptStatus(
                        user.getDomainId(), Status.EXPUNGING, pagingAndSorting.toPageRequest(), searchText);
                return allInstanceList;
            } else {
                // Get active project list for current user.
                if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<Project> projectList = new ArrayList<Project>();
                    for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                        // List all Vms for the current user by status and project and/or department.
                        projectList.add(project);
                    }
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndProjectAndExceptStatusAndPage(Status.EXPUNGING, user.getDepartment(),
                                    projectList, pagingAndSorting.toPageRequest(), searchText);
                    return (Page<VmInstance>) allInstanceLists;
                } else {
                    // List all Vms by status for ROOT admin.
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndExceptStatusWithPageRequest(user.getDepartment().getId(),
                                    Status.EXPUNGING, pagingAndSorting.toPageRequest(), searchText);
                    return (Page<VmInstance>) allInstanceLists;
                }
            }
        }
        return virtualmachinerepository.findAllByDomainAndExceptStatusWithPageRequest(Status.EXPUNGING, domainId, pagingAndSorting.toPageRequest(), searchText);
    }

    @Override
    public Page<VmInstance> findAllByStatusAndDomain(PagingAndSorting pagingAndSorting, Status status, Long domainId, String searchText, Long userId)
            throws Exception {
        // Get user information from token details.
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                // List all Vms by status for domain admin.
                Page<VmInstance> allInstanceList = virtualmachinerepository.findAllByDomainAndStatusWithPageRequest(
                        user.getDomainId(), status, pagingAndSorting.toPageRequest(), searchText);
                return allInstanceList;
            } else {
                // Get active project list for current user.
                if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<Project> projectList = new ArrayList<Project>();
                    for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                        // List all Vms for the current user by status and project and/or department.
                        projectList.add(project);
                    }
                    Page<VmInstance> allInstanceList = virtualmachinerepository
                            .findAllByDepartmentAndProjectAndStatusAndPage(status, user.getDepartment(), projectList,
                                    pagingAndSorting.toPageRequest(), searchText);
                    return (Page<VmInstance>) allInstanceList;
                } else {
                    // List all Vms by status for ROOT admin.
                    Page<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndStatusWithPageRequest(status, user.getDepartment(),
                                    pagingAndSorting.toPageRequest(), searchText);
                    return (Page<VmInstance>) allInstanceLists;
                }

            }
        }
        return virtualmachinerepository.findAllByStatusAndDomainWithPageRequest(status, domainId, pagingAndSorting.toPageRequest(), searchText);
    }

    @Override
    public Integer findCountByStatusAndDomain(Status status, Long domainId, Long userId, String searchText) {
        try {
            // Get user information from token details.
            User user = convertEntityService.getOwnerById(userId);
            if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
                if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                    // Vms count by status for domain admin.
                    List<VmInstance> allInstanceList = virtualmachinerepository
                            .findAllByDomainAndStatus(user.getDomainId(), status, searchText);
                    return allInstanceList.size();
                } else {
                    // Get active project list for current user.
                    if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                        List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                        for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                            List<VmInstance> allInstanceTempList = virtualmachinerepository
                                    .findAllByDepartmentAndProjectAndStatus(status, user.getDepartment(), project, searchText);
                            allInstanceList.addAll(allInstanceTempList);
                        }
                        List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                        // Vms count by status belongs to the project and/or department.
                        return instances.size();
                    } else {
                        List<VmInstance> allInstanceLists = virtualmachinerepository
                                .findAllByDepartmentAndStatus(status, user.getDepartment(), searchText);
                        // Vms count by status for an ROOT admin.
                        return allInstanceLists.size();
                    }
                }
            }
        } catch (NumberFormatException e) {
        } catch (Exception e) {
        }
        return virtualmachinerepository.findAllByDomainAndStatus(domainId, status, searchText).size();
    }

    @Override
    public List<VmInstance> findAllByDomain(Long domainId, Long userId, String searchText) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            if (user.getType().equals(UserType.DOMAIN_ADMIN)) {
                // List all Vms by status for domain admin.
                List<VmInstance> allInstanceList = virtualmachinerepository
                        .findAllByDomainAndExceptStatus(user.getDomainId(), Status.EXPUNGING, searchText);
                return allInstanceList;
            } else {
                // Get active project list for current user.
                if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                    List<VmInstance> allInstanceList = new ArrayList<VmInstance>();
                    for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                        // List all Vms for the current user by status and project and/or department.
                        List<VmInstance> allInstanceTempList = virtualmachinerepository
                                .findAllByDepartmentAndProjectAndExceptStatus(Status.EXPUNGING, user.getDepartment(),
                                        project, searchText);
                        allInstanceList.addAll(allInstanceTempList);
                    }
                    List<VmInstance> instances = allInstanceList.stream().distinct().collect(Collectors.toList());
                    // List all Vms by status for the current user, belongs to department and project.
                    return instances;
                } else {
                    // List all Vms by status for ROOT admin.
                    List<VmInstance> allInstanceLists = virtualmachinerepository
                            .findAllByDepartmentAndExceptStatus(Status.EXPUNGING, user.getDepartment(), searchText);
                    return allInstanceLists;
                }
            }
        }
        return (List<VmInstance>) virtualmachinerepository.findAllByDomainAndExceptStatus(domainId, Status.EXPUNGING, searchText);
    }

    @Override
    public VmInstance resetSSHKey(VmInstance vmInstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.ENTITY_VMINSTANCE, vmInstance);
        errors = validator.validateEntity(vmInstance, errors);
        HashMap<String, String> optionalMap = new HashMap<String, String>();
        optionalMap.put(CloudStackConstants.CS_DOMAIN_ID, convertEntityService.getDomainUuidById(vmInstance.getDomainId()));
        if (vmInstance.getProjectId() == null) {
            optionalMap.put(CloudStackConstants.CS_ACCOUNT, (convertEntityService.getDepartmentById(vmInstance.getDepartmentId()).getUserName()));
        } else if (vmInstance.getProjectId() != null) {
            optionalMap.put(CloudStackConstants.CS_PROJECT_ID, convertEntityService.getProjectUuidById(vmInstance.getProjectId()));
        }
        // CS API call to reset the ssh key of an instance.
        config.setUserServer();
        String resetSSHKey = cloudStackInstanceService.resetSSHKeyForVirtualMachine(vmInstance.getUuid(),
            CloudStackConstants.JSON, convertEntityService.getSSHKeyById(vmInstance.getKeypairId()).getName(), optionalMap);
        JSONObject jobId = new JSONObject(resetSSHKey).getJSONObject(CloudStackConstants.CS_RESET_KEYPAIR_RESPONSE);
        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            String jobState = jobStatus(jobId, vmInstance);
            if (jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            } else {
                virtualmachinerepository.save(convertEncryptPassword(vmInstance));
            }
        }
        return vmInstance;
    }


    @Override
    public List<VmInstance> findAllByUserAndStatus(User user, Status status) throws Exception {
        return virtualmachinerepository.findAllByUserAndStatus(user, status);
    }

    @Override
    public void updateVmToStoppedByOwnerAndStatus(User user, Status status) {
        for(VmInstance vm : virtualmachinerepository.findAllByUserAndStatus(user, status)) {
            vm.setStatus(Status.STOPPED);
            virtualmachinerepository.save(vm);
        }
    }

    @Override
    public VmInstance affinityGroup(VmInstance vmInstance) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CloudStackConstants.ENTITY_VMINSTANCE, vmInstance);
        errors = validator.validateEntity(vmInstance, errors);
        HashMap<String, String> optionalMap = new HashMap<String, String>();
        String affinityGroupIds = "";
        for (int j = 0; j < vmInstance.getAffinityGroupList().size(); j++) {
            if (j == vmInstance.getAffinityGroupList().size() - 1) {
                affinityGroupIds = affinityGroupIds + vmInstance.getAffinityGroupList().get(j).getUuid();
            } else {
                affinityGroupIds = affinityGroupIds + vmInstance.getAffinityGroupList().get(j).getUuid() + ",";
            }
        }
        if (vmInstance.getAffinityGroupList().size() != 0) {
            optionalMap.put(CloudStackConstants.CS_AFFINITY_GROUP_IDS, affinityGroupIds);
        } else {
            optionalMap.put(CloudStackConstants.CS_AFFINITY_GROUP_IDS, "");
        }
        User user = convertEntityService.getOwnerById(vmInstance.getInstanceOwnerId());
        if (user == null || !apiSecretKeyGeneration(user)) {
            config.setUserServer();
        }
        String affinityGroupResponse = cloudStackInstanceService.updateVMAffinityGroup(vmInstance.getUuid(),
            CloudStackConstants.JSON, optionalMap);
        JSONObject jobId = new JSONObject(affinityGroupResponse).getJSONObject(CloudStackConstants.CS_UPDATE_VM_RESPONSE);
        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            String jobState = jobStatus(jobId, vmInstance);
            if (jobState.equals(GenericConstants.ERROR_JOB_STATUS)) {
                errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            } else {
                virtualmachinerepository.save(convertEncryptPassword(vmInstance));
            }
        }
        return vmInstance;
    }

    @Override
    public List<VmInstance> findInstanceByGroup(Long id) throws Exception {
        List<VmInstance> allInstance = new ArrayList<VmInstance>();
        for (VmInstance instance : virtualmachinerepository.findAll()) {
            for (int i = 0; i < instance.getAffinityGroupList().size(); i++) {
                if (instance.getAffinityGroupList().get(i).getId() == id) {
                    allInstance.add(instance);
                }
            }
        }
        return allInstance;
    }

    /**
     * Get the API and Secret key for a user.
     *
     * @param user to set
     * @return API and Secret key status true/false
     * @throws Exception unhandled exceptions.
     */
    private Boolean apiSecretKeyGeneration(User user) throws Exception {
        config.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put(CloudStackConstants.CS_ID, user.getUuid());
        String listUserByIdResponse = cloudStackUserService.listUsers(optional, CloudStackConstants.JSON);
        JSONObject listUsersResponse = new JSONObject(listUserByIdResponse)
                .getJSONObject(CloudStackConstants.CS_LIST_USER_RESPONSE);
        if (listUsersResponse.has(CloudStackConstants.CS_ERROR_CODE)) {
            return false;
        } else {
            JSONArray userJsonobject = (JSONArray) listUsersResponse.get(CloudStackConstants.CS_USER);
            if (userJsonobject.getJSONObject(0).has(CloudStackConstants.CS_API_KEY)) {
                user.setApiKey(userJsonobject.getJSONObject(0).get(CloudStackConstants.CS_API_KEY).toString());
                user.setSecretKey(userJsonobject.getJSONObject(0).get(CloudStackConstants.CS_SECRET_KEY).toString());
                config.setInstanceUserServer(user.getSecretKey(), user.getApiKey());
                return true;
            } else {
                String keyValueResponse = cloudStackUserService.registerUserKeys(user.getUuid(),
                        CloudStackConstants.JSON);
                JSONObject keyValue = new JSONObject(keyValueResponse)
                        .getJSONObject(CloudStackConstants.CS_REGISTER_KEY_RESPONSE);
                if (keyValue.has(CloudStackConstants.CS_ERROR_CODE)) {
                    return false;
                } else {
                    user.setApiKey(keyValue.getJSONObject(USER_KEYS).getString(CloudStackConstants.CS_API_KEY));
                    user.setSecretKey(keyValue.getJSONObject(USER_KEYS).getString(CloudStackConstants.CS_SECRET_KEY));
                    config.setInstanceUserServer(user.getSecretKey(), user.getApiKey());
                    return true;
                }
            }
        }
    }

    @Override
    public VmInstance findVMByIDWithSpecifiedField(Long vmId) {
        return virtualmachinerepository.findVMByIDWithSpecifiedField(vmId);
    }

    public long ipToLong(String ipAddress) {
        long result = 0;
        if (ipAddress != null) {
            String[] ipAddressInArray = ipAddress.split("\\.");

            for (int i = 0; i < ipAddressInArray.length; i++) {

                int power = 3 - i;
                int ip = Integer.parseInt(ipAddressInArray[i]);
                result += ip * Math.pow(256, power);

            }
        } else {
            result = 0;
        }

        return result;
      }
}
