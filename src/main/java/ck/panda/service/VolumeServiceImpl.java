package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EventTypes;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.ResourceLimitDepartment.ResourceType;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.Volume.Format;
import ck.panda.domain.entity.Volume.Status;
import ck.panda.domain.entity.Volume.VolumeType;
import ck.panda.domain.repository.jpa.VolumeRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackOptionalUtil;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackVolumeService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.JsonValidator;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;

/**
 * Volume Service Implementation.
 */
@Service
public class VolumeServiceImpl implements VolumeService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VolumeServiceImpl.class);

    /** Constant for Cloud stack volumes. */
    public static final String CS_VOLUMES = "volumes";

    /** Constant for Cloud stack volume. */
    public static final String CS_VOLUME = "volume";

    /** Constant for Cloud stack shrink volume. */
    public static final String CS_SHRINK_OK = "shrinkok";

    /** Constant for Cloud stack check sum volume. */
    public static final String CS_CHECKSUM = "checksum";

    /** Constant for Cloud stack volume list response. */
    public static final String CS_LIST_VOLUME_RESPONSE = "listvolumesresponse";

    /** Constant for Cloud stack volume create response. */
    public static final String CS_CREATE_VOLUME_RESPONSE = "createvolumeresponse";

    /** Constant for Cloud stack volume upload response. */
    public static final String CS_UPLOAD_VOLUME_RESPONSE = "uploadvolumeresponse";

    /** Constant for Cloud stack volume attach response. */
    public static final String CS_ATTACH_VOLUME_RESPONSE = "attachvolumeresponse";

    /** Constant for Cloud stack volume detach response. */
    public static final String CS_DETACH_VOLUME_RESPONSE = "detachvolumeresponse";

    /** Constant for Cloud stack volume resize response. */
    public static final String CS_RESIZE_VOLUME_RESPONSE = "resizevolumeresponse";

    /** Constant for Cloud stack volume conversation in GiB. */
    public static final Integer CS_CONVERTION_GIB = 1024 * 1024 * 1024;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** VolumeRepository repository reference. */
    @Autowired
    private VolumeRepository volumeRepo;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackVolumeService csVolumeService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** Department Service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Autowired Project Service. */
    @Autowired
    private ProjectService projectService;

    /** Autowired TokenDetails. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** Autowired Storage Offering Service. */
    @Autowired
    private StorageOfferingService storageService;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** Sync Service reference. */
    @Autowired
    private SyncService syncService;

    /** Sync Service reference. */
    @Autowired
    private AsynchronousJobService asyncService;

    /** Quota limit validation reference. */
    @Autowired
    QuotaValidationService quotaLimitValidation;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** CloudStack connector reference for resource capacity. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    @Override
    @PreAuthorize("hasPermission(#volume.getIsSyncFlag(), 'ADD_VOLUME')")
    public Volume saveVolume(Volume volume, Long userId) throws Exception {
        if (volume.getIsSyncFlag()) {
            this.validateVolumeUniqueness(volume, convertEntityService.getOwnerById(userId).getDomainId(), userId);
            Errors errors = validator.rejectIfNullEntity(CS_VOLUMES, volume);
            errors = validator.validateEntity(volume, errors);
            HashMap<String, String> optionalMap = new HashMap<String, String>();
            optionalMap.put(CloudStackConstants.CS_ZONE_ID,
                    convertEntityService.getZoneById(volume.getZoneId()).getUuid());
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                // check department and project quota validation.
                ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                        .findByDepartmentAndResourceType(volume.getDepartmentId(), ResourceType.Instance, true);
                ResourceLimitProject projectLimit = resourceLimitProjectService
                        .findByProjectAndResourceType(volume.getProjectId(), ResourceLimitProject.ResourceType.Instance, true);
                if (departmentLimit != null && convertEntityService.getDepartmentById(volume.getDepartmentId()).getType()
                        .equals(AccountType.USER)) {
                    if (volume.getProjectId() != null) {
                        if (projectLimit != null) {
                            quotaLimitValidation.QuotaLimitCheckByResourceObject(volume, "Volume",
                                volume.getProjectId(), "Project");
                        } else {
                            errors.addGlobalError(
                                    "Resource limit for project has not been set. Please update project quota");
                            throw new ApplicationException(errors);
                        }
                    } else {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(volume, "Volume",
                                volume.getDepartmentId(), "Department");
                    }

                    // 3. Check the resource availability to create new volume.
                    String isAvailable = isResourceAvailable(volume, optionalMap);
                    if (isAvailable != null) {
                        // 3.1 throws error message about resource shortage.
                        throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, isAvailable);
                    } else {
//                        // update resource for volume
//                        updateResourceForVolumeCreation(volume, errors);
                        // volume creation
                        Volume volumeCS = createVolume(volume, userId, errors);
                        // if volume creation has errors delete the resource and
                        // throws error
                        if (errors.hasErrors()) {
                            throw new ApplicationException(errors);
                        }
                        if (volumeRepo.findByUUID(volumeCS.getUuid()) == null) {
                            volume = volumeRepo.save(volumeCS);
                        }
                    }
                } else {
                    errors.addGlobalError(
                            "Resource limit for department has not been set. Please update department quota");
                    throw new ApplicationException(errors);
                }
            }
            return volume;
        } else {
            return volumeRepo.save(volume);
        }
    }

    /**
     * Update resource for
     * @param volume
     * @param errors
     * @return
     * @throws Exception
     */
    private void updateResourceForVolumeCreation(Volume volume, Errors errors) throws Exception {
        HashMap<String, String> resourceMap = convertEntityService.getResourceTypeValue();
        String r = null;
        try {
            for (int i = 0; i < resourceMap.size(); i++) {
                if (i == 2) {
                    r = r.valueOf(i);
                    if (!convertEntityService.getDepartmentById(volume.getDepartmentId()).getType()
                            .equals(AccountType.USER)) {
                        domainVolumeQuotaCheck(volume, resourceMap, r, errors);
                    } else {

                        if (volume.getProjectId() != null) {
                            ResourceLimitProject projectMax = resourceLimitProjectService.findByProjectAndResourceType(
                                    volume.getProjectId(),
                                    ResourceLimitProject.ResourceType.valueOf(resourceMap.get(r)), true);
                            if (projectMax.getUsedLimit() == null) {
                            } else if (projectMax.getMax() != projectMax.getUsedLimit()) {
                            } else {
                                errors.addGlobalError("quota for creating volume" + " ' " + volume.getName() + " ' "
                                        + "not exists. Please update project resource quota first to continue creating.");
                                throw new ApplicationException(errors);
                            }
//                            projectMax.setAvailable(projectMax.getMax() - projectMax.getUsedLimit());
//                            projectMax.setIsSyncFlag(false);
//                            resourceLimitProjectService.update(projectMax);
                        }
                        if (volume.getDepartmentId() != null) {
                            ResourceLimitDepartment departmentMax = resourceLimitDepartmentService
                                    .findByDepartmentAndResourceType(volume.getDepartmentId(),
                                            ResourceType.valueOf(resourceMap.get(r)), true);
                            if (departmentMax.getUsedLimit() == null) {
                            } else if (departmentMax.getMax() != departmentMax.getUsedLimit()) {
                            } else {
                                errors.addGlobalError("quota for creating volume" + " ' " + volume.getName() + " ' "
                                        + "not exists. Please update department resource quota first to continue creating.");
                                throw new ApplicationException(errors);
                            }
//                            departmentMax.setAvailable(departmentMax.getMax() - departmentMax.getUsedLimit());
//                            resourceLimitDepartmentService.update(departmentMax);
                        }
                        domainVolumeQuotaCheck(volume, resourceMap, r, errors);
                    }
                }
                if (i == 10) {
                    r = r.valueOf(i);
                    if (!convertEntityService.getDepartmentById(volume.getDepartmentId()).getType()
                            .equals(AccountType.USER)) {
                        domainVolumeQuotaCheck(volume, resourceMap, r, errors);
                    } else {

                        if (volume.getProjectId() != null) {
                            ResourceLimitProject projectMax = resourceLimitProjectService.findByProjectAndResourceType(
                                    volume.getProjectId(),
                                    ResourceLimitProject.ResourceType.valueOf(resourceMap.get(r)), true);
                            if (projectMax.getUsedLimit() == null) {
                            } else if (projectMax.getMax() != projectMax.getUsedLimit()) {
                            } else {
                                errors.addGlobalError("quota for creating Primary Storage" + " ' "
                                        + volume.getName() + " ' "
                                        + "not exists. Please update project resource quota first to continue creating.");
                                throw new ApplicationException(errors);
                            }
                            if (projectMax.getUsedLimit() <= projectMax.getMax()) {
                            } else {
                                errors.addGlobalError("quota for creating Primary Storage" + " ' "
                                        + volume.getName() + " ' "
                                        + "not exists. Please update project resource quota first to continue creating.");
                                throw new ApplicationException(errors);
                            }
                        }
                        if (volume.getDepartmentId() != null) {
                            ResourceLimitDepartment departmentMax = resourceLimitDepartmentService
                                    .findByDepartmentAndResourceType(volume.getDepartmentId(),
                                            ResourceType.valueOf(resourceMap.get(r)), true);
                            if (departmentMax.getUsedLimit() == null) {
                            } else if (departmentMax.getMax() != departmentMax.getUsedLimit()) {
                            } else {
                                errors.addGlobalError("quota for creating Primary Storage" + " ' "
                                        + volume.getName() + " ' "
                                        + "not exists. Please update department resource quota first to continue creating.");
                                throw new ApplicationException(errors);
                            }
                            if (departmentMax.getUsedLimit() <= departmentMax.getMax()) {
                            } else {
                                errors.addGlobalError("quota for creating Primary Storage" + " ' "
                                        + volume.getName() + " ' "
                                        + "not exists. Please update department resource quota first to continue creating.");
                                throw new ApplicationException(errors);
                            }
                        }
                        domainVolumeQuotaCheck(volume, resourceMap, r, errors);
                    }
                }
            }
        } catch (ApplicationException e) {
            throw new ApplicationException(e.getErrors());
        }
    }

    private void domainVolumeQuotaCheck(Volume volume, HashMap<String, String> resourceMap, String r, Errors errors)
            throws Exception {
        /** Used for setting optional values for resource count. */
        HashMap<String, String> domainCountMap = new HashMap<String, String>();
            // Resource count for domain
            String csResponse = cloudStackResourceCapacity.updateResourceCount(convertEntityService
                    .getDomainById(convertEntityService.getDepartmentById(volume.getDepartmentId()).getDomainId())
                    .getUuid(), domainCountMap, "json");
            convertEntityService.resourceCount(csResponse);

            ResourceLimitDomain domainMax = resourceLimitDomainService.findByDomainAndResourceType(
                    convertEntityService.getDepartmentById(volume.getDepartmentId()).getDomainId(),
                    ResourceLimitDomain.ResourceType.valueOf(resourceMap.get(r)), true);
            if (domainMax.getMax() <= domainMax.getUsedLimit() && domainMax.getMax() != -1) {
                errors.addGlobalError("quota for creating volume" + " ' " + volume.getName() + " ' "
                        + "not exists. Please update domain resource quota first to continue creating.");
                throw new ApplicationException(errors);
            }
    }

    @Override
    public Volume update(Volume volume) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CS_VOLUMES, volume);
        errors = validator.validateEntity(volume, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return volumeRepo.save(volume);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#volume.getIsSyncFlag(), 'ATTACH_DISK')")
    public Volume attachVolume(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CS_VOLUMES, volume);
            errors = validator.validateEntity(volume, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                Volume volumeCS = attach(volume, errors);
                if (volumeRepo.findByUUID(volumeCS.getUuid()) != null) {
                    volume = volumeRepo.findByUUID(volumeCS.getUuid());
                    volume.setVmInstanceId(volumeCS.getVmInstanceId());
                    volume.setStatus(volumeCS.getStatus());
                }
            }
        }
        return volumeRepo.save(volume);
    }

    @Override
    @PreAuthorize("hasPermission(#volume.getIsSyncFlag(), 'DETACH_DISK')")
    public Volume detachVolume(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CS_VOLUMES, volume);
            errors = validator.validateEntity(volume, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                Volume volumeCS = detach(volume, errors);
                if (volumeRepo.findByUUID(volumeCS.getUuid()) != null) {
                    volume = volumeRepo.findByUUID(volumeCS.getUuid());
                    volume.setVmInstanceId(volumeCS.getVmInstanceId());
                    volume.setStatus(volumeCS.getStatus());
                }
            }
        }
        return volumeRepo.save(volume);
    }

    @Override
    public Volume resizeVolume(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CS_VOLUMES, volume);
            errors = validator.validateEntity(volume, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                Volume volumeCS = resize(volume, errors);
                if (volumeRepo.findByUUID(volumeCS.getUuid()) != null) {
                    volume = volumeRepo.findByUUID(volumeCS.getUuid());
                }
            }
        }
        return volumeRepo.save(volume);
    }

    @Override
    @PreAuthorize("hasPermission(#volume.getIsSyncFlag(), 'UPLOAD_VOLUME')")
    public Volume uploadVolume(Volume volume, Long userId) throws Exception {
        if (volume.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CS_VOLUMES, volume);
            errors = validator.validateEntity(volume, errors);
            this.validateVolumeUniqueness(volume, convertEntityService.getOwnerById(userId).getDomainId(), userId);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optionalMap = new HashMap<String, String>();
                optionalMap.put(CloudStackConstants.CS_ZONE_ID,
                        convertEntityService.getZoneById(volume.getZoneId()).getUuid());
                // check department and project quota validation.
                ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                        .findByDepartmentAndResourceType(volume.getDepartmentId(), ResourceType.Instance, true);
                if (departmentLimit != null && convertEntityService.getDepartmentById(volume.getDepartmentId())
                        .getType().equals(AccountType.USER)) {
                    if (volume.getProjectId() != null) {
//                        syncService
//                                .syncResourceLimitProject(convertEntityService.getProjectById(volume.getProjectId()));
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(volume, "UploadVolume",
                                volume.getProjectId(), "Project");
                    } else {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(volume, "UploadVolume",
                                volume.getDepartmentId(), "Department");
                    }
                    /*if (volume.getDomainId() != null) {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(volume, "UploadVolume",
                                volume.getDomainId(), "Domain");
                    }*/
                    Volume volumeCS = upload(volume, convertEntityService.getOwnerById(userId).getDomainId(), userId,
                            errors);
                    if (errors.hasErrors()) {
                        throw new ApplicationException(errors);
                    }
                    return volumeCS;
                } else {
                    errors.addGlobalError(
                            "Resource limit for department has not been set. Please update department quota");
                    throw new ApplicationException(errors);
                }
            }
        } else {
            return volumeRepo.save(volume);
        }
    }

    @Override
    public void delete(Volume volume) throws Exception {
        volumeRepo.delete(volume);
    }

    @Override
    public void delete(Long id) throws Exception {
        volumeRepo.delete(id);
    }

    @Override
    public Volume find(Long id) throws Exception {
        return volumeRepo.findOne(id);
    }

    @Override
    public Page<Volume> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return volumeRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<Volume> findAllByIsActive(PagingAndSorting pagingAndSorting, Long userId) throws Exception {
        // Checking the volume disk size from cloud stack
        List<Volume> activeVolume = volumeRepo.findAllByActive(true);
        for (int k = 0; k < activeVolume.size(); k++) {
            if (!activeVolume.get(k).getDiskSizeFlag()) {
                HashMap<String, String> volumeMap = new HashMap<String, String>();
                volumeMap.put(CloudStackConstants.CS_ID, activeVolume.get(k).getUuid());
                JSONArray volumeListJSON = null;
                config.setUserServer();
                String response = csVolumeService.listVolumes(CloudStackConstants.JSON, volumeMap);
                JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_VOLUME_RESPONSE);
                if (responseObject.has(CS_VOLUME)) {
                    volumeListJSON = responseObject.getJSONArray(CS_VOLUME);
                    if (JsonValidator
                            .jsonStringValidation(volumeListJSON.getJSONObject(0), CloudStackConstants.CS_STATE)
                            .equals(CloudStackConstants.CS_UPLOADED)) {
                        activeVolume.get(k)
                                .setDiskSize(volumeListJSON.getJSONObject(0).getLong(CloudStackConstants.CS_SIZE));
                        activeVolume.get(k).setDiskSizeFlag(true);
                    } else if (JsonValidator
                            .jsonStringValidation(volumeListJSON.getJSONObject(0), CloudStackConstants.CS_STATE)
                            .equals(CloudStackConstants.CS_UPLOAD_ERROR)) {
                        activeVolume.get(k).setDiskSizeFlag(true);
                    }
                    volumeRepo.save(activeVolume.get(k));
                }
            }
        }
        if (convertEntityService.getOwnerById(userId).getDomainId() != null
                && !convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
            if (convertEntityService.getOwnerById(userId).getType().equals(User.UserType.DOMAIN_ADMIN)) {
                return volumeRepo.findByDomainAndIsActive(convertEntityService.getOwnerById(userId).getDomainId(), true,
                        pagingAndSorting.toPageRequest());
            } else {
                List<Volume.VolumeType> volumeType = new ArrayList<>();
                volumeType.add(VolumeType.DATADISK);
                volumeType.add(VolumeType.ROOT);
                if (projectService.findAllByUserAndIsActive(userId, true).size() > 0) {
                    List<Project> allProjectList = new ArrayList<Project>();
                    for (Project project : projectService.findAllByUserAndIsActive(userId, true)) {
                        allProjectList.add(project);
                    }
                    Page<Volume> allProjectTempList = volumeRepo.findByProjectAndVolumeTypeAndPage(allProjectList,
                            convertEntityService.getOwnerById(userId).getDepartmentId(), volumeType, true, pagingAndSorting.toPageRequest());
                    return allProjectTempList;
                } else {
                    return volumeRepo.findByDepartmentAndVolumeTypeAndPage(
                            convertEntityService.getOwnerById(userId).getDepartmentId(), volumeType, true,
                            pagingAndSorting.toPageRequest());
                }
            }
        }
        return volumeRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Volume> findAll() throws Exception {
        return (List<Volume>) volumeRepo.findAll();
    }

    @Override
    public List<Volume> findByInstanceAndIsActive(Long volume, Long userId) throws Exception {
        if (convertEntityService.getOwnerById(userId).getDomainId() != null
                && !convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
            return volumeRepo.findByInstanceAndDomainIsActive(convertEntityService.getOwnerById(userId).getDomainId(),
                    volume, true);
        }
        return volumeRepo.findByInstanceAndIsActive(volume, true);
    }

    @Override
    public List<Volume> findByVolumeTypeAndIsActive(Long userId) throws Exception {
        if (convertEntityService.getOwnerById(userId).getDomainId() != null
                && !convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
            return volumeRepo.findByVolumeTypeAndIsActive(convertEntityService.getOwnerById(userId).getDomainId(),
                    Volume.VolumeType.DATADISK, true);
        }
        return volumeRepo.findByVolumeTypeAndIsActive(Volume.VolumeType.DATADISK, true);
    }

    @Override
    public List<Volume> findByInstanceAndVolumeTypeAndIsActive(Long volume) throws Exception {
        return volumeRepo.findByInstanceAndVolumeTypeAndIsActive(volume, Volume.VolumeType.ROOT, true);
    }

    /**
     * To set optional values by validating null and empty parameters.
     *
     * @param volume
     *            optional storage offering values
     * @param userId
     *            user details
     * @return optional values
     * @throws Exception
     *             error at option values
     */
    public HashMap<String, String> optionalValuesToMap(Volume volume, Long userId) throws Exception {
        HashMap<String, String> volumeMap = new HashMap<String, String>();
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_SIZE, volume.getDiskSize(), volumeMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_MIN_IOPS, volume.getDiskMinIops(),
                volumeMap);
        CloudStackOptionalUtil.updateOptionalLongValue(CloudStackConstants.CS_MAX_IOPS, volume.getDiskMaxIops(),
                volumeMap);
        CloudStackOptionalUtil.updateOptionalStringValue("diskofferingid",
                convertEntityService.getStorageOfferingById(volume.getStorageOfferingId()), volumeMap);

        if (volume.getProjectId() != null) {
            volumeMap.put(CloudStackConstants.CS_PROJECT_ID,
                    convertEntityService.getProjectUuidById(volume.getProjectId()));
        } else if (volume.getDepartmentId() != null) {
            volumeMap.put(CloudStackConstants.CS_ACCOUNT,
                    convertEntityService.getDepartmentUsernameById(volume.getDepartmentId()));
            volumeMap.put(CloudStackConstants.CS_DOMAIN_ID,
                    departmentService.find(volume.getDepartmentId()).getDomain().getUuid());
        } else {
            if (convertEntityService.getOwnerById(userId).getDomainId() != null
                    && !convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
                volumeMap.put(CloudStackConstants.CS_DOMAIN_ID,
                        convertEntityService.getOwnerById(userId).getDomain().getUuid());
                volumeMap.put(CloudStackConstants.CS_ACCOUNT,
                        convertEntityService.getOwnerById(userId).getDepartment().getUserName());
            }
        }
        return volumeMap;
    }

    @Override
    public List<Volume> findAllFromCSServer() throws Exception {
        List<Project> project = projectService.findAllByActive(true);
        List<Volume> volumeList = new ArrayList<Volume>();
        for (int j = 0; j <= project.size(); j++) {
            HashMap<String, String> volumeMap = new HashMap<String, String>();
            if (j == project.size()) {
                volumeMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
            } else {
                volumeMap.put(CloudStackConstants.CS_PROJECT_ID, project.get(j).getUuid());
            }
            // 1. Get the list of Volume from CS server using CS connector
            config.setServer(1L);
            String response = csVolumeService.listVolumes(CloudStackConstants.JSON, volumeMap);
            JSONArray volumeListJSON = null;
            JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_VOLUME_RESPONSE);
            if (responseObject.has(CS_VOLUME)) {
                volumeListJSON = responseObject.getJSONArray(CS_VOLUME);
                // 2. Iterate the json list, convert the single json entity to
                // Volume
                for (int i = 0, size = volumeListJSON.length(); i < size; i++) {
                    // 2.1 Call convert by passing JSONObject to Volume entity
                    // and Add
                    // the converted Volume entity to list
                    Volume volume = Volume.convert(volumeListJSON.getJSONObject(i));
                    volume.setZoneId(convertEntityService.getZoneId(volume.getTransZoneId()));
                    volume.setDomainId(convertEntityService.getDomainId(volume.getTransDomainId()));
                    volume.setStorageOfferingId(
                            convertEntityService.getStorageOfferId(volume.getTransStorageOfferingId()));
                    volume.setVmInstanceId(convertEntityService.getVmInstanceId(volume.getTransvmInstanceId()));
                    if (volume.getTransProjectId() != null) {
                        volume.setProjectId(convertEntityService.getProjectId(volume.getTransProjectId()));
                        volume.setDepartmentId(projectService.find(volume.getProjectId()).getDepartmentId());
                    } else {
                        // departmentRepository.findByUuidAndIsActive(volume.getTransDepartmentId(),
                        // true);
                        Domain domain = domainService.find(volume.getDomainId());
                        volume.setDepartmentId(convertEntityService
                                .getDepartmentByUsernameAndDomains(volume.getTransDepartmentId(), domain));
                    }
                    volumeList.add(volume);
                }
            }
        }
        return volumeList;
    }

    /**
     * Cloud stack create Volume.
     *
     * @param volume
     *            Volume
     * @param userId
     *            user details
     * @param errors
     *            global error and field errors
     * @throws Exception
     *             error for creating volume
     * @return Volumes
     */
    private Volume createVolume(Volume volume, Long userId, Errors errors) throws Exception {
        config.setUserServer();
        String volumeS = csVolumeService.createVolume(volume.getName(),

                convertEntityService.getZoneUuidById(volume.getZoneId()), CloudStackConstants.JSON,
                optionalValuesToMap(volume, userId));
        LOGGER.info("Volume create response " + volumeS);
        try {
            JSONObject jobId = new JSONObject(volumeS).getJSONObject(CS_CREATE_VOLUME_RESPONSE);

            if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            } else {
                volume.setUuid((String) jobId.get(CloudStackConstants.CS_ID));
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    config.setUserServer();
                    String jobResponse = csVolumeService.volumeJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                            CloudStackConstants.JSON);
                    JSONObject jobresult = new JSONObject(jobResponse)
                            .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (jobresult.has(CloudStackConstants.CS_JOB_RESULT)) {
                        if (jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                .has(CloudStackConstants.CS_ERROR_CODE)) {
                            errors = this.validateEvent(errors,
                                    jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                            .getString(CloudStackConstants.CS_ERROR_TEXT));
                        }
                    }
                    if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                            .equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                        volume.setStatus(Status.valueOf(EventTypes.ALLOCATED));
                    }
                    volume.setIsActive(true);
                    volume.setStorageOfferingId(volume.getStorageOfferingId());
                    if (volume.getDiskSize() != null) {
                        volume.setDiskSize(volume.getDiskSize() * (CS_CONVERTION_GIB));
                    } else {
                        StorageOffering store = storageService.find(volume.getStorageOfferingId());
                        volume.setDiskSize(store.getDiskSize() * (CS_CONVERTION_GIB));
                    }
                    volume.setDiskSizeFlag(true);
                    if (volume.getDomain() != null) {
                        volume.setDomainId(volume.getDomain().getId());
                    } else {
                        volume.setDomainId(volume.getDomainId());
                    }
                    volume.setVolumeType(Volume.VolumeType.DATADISK);
                    if (volume.getProjectId() != null) {
                        volume.setProjectId(volume.getProjectId());
                        Project project = projectService.find(volume.getProjectId());
                        volume.setDepartmentId(project.getDepartmentId());
                    }
                    volume.setCreatedDateTime(volume.getCreatedDateTime());
                    volume.setDiskMaxIops(volume.getDiskMaxIops());
                    volume.setDiskMinIops(volume.getDiskMinIops());
                }
            }
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT VOLUME CREATION", e);
            throw new ApplicationException(e.getErrors());
        }
        return volume;
    }

    /**
     * Check the Storage offering CS error handling.
     *
     * @param errors
     *            error creating status.
     * @param errMessage
     *            error message.
     * @return errors.
     * @throws Exception
     *             error for validation
     */
    private Errors validateEvent(Errors errors, String errMessage) throws Exception {
        errors.addGlobalError(errMessage);
        return errors;
    }

    /**
     * Validate the Volume.
     *
     * @param volume
     *            reference of the Volume.
     * @param userId
     *            user details
     * @param domainId
     *            domain details
     * @throws Exception
     *             error occurs
     */
    private void validateVolumeUniqueness(Volume volume, Long domainId, Long userId) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CS_VOLUMES, volume);
        errors = validator.validateEntity(volume, errors);
        Volume validateVolume = volumeRepo.findByNameAndIsActive(volume.getName(), domainId, userId, true);
        if (validateVolume != null && volume.getId() != validateVolume.getId()) {
            errors.addGlobalError("error.volume.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Volume findByUUID(String uuid) throws Exception {
        return volumeRepo.findByUUID(uuid);
    }

    /**
     * Attach volume to an instance.
     *
     * @param volume
     *            volume
     * @param errors
     *            errors
     * @throws Exception
     *             error for attach volume
     * @return volume
     */
    public Volume attach(Volume volume, Errors errors) throws Exception {
        HashMap<String, String> optional = new HashMap<String, String>();
        if (volume.getVmInstanceId() != null) {
            VmInstance instance = virtualMachineService.find(volume.getVmInstanceId());
            optional.put(CloudStackConstants.CS_VIRTUAL_MACHINE_ID, instance.getUuid());
        } else {
            optional.put(CloudStackConstants.CS_VIRTUAL_MACHINE_ID, volume.getVmInstance().getUuid());
        }
        config.setUserServer();
        String volumeS = csVolumeService.attachVolume(volume.getUuid(), CloudStackConstants.JSON, optional);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject(CS_ATTACH_VOLUME_RESPONSE);

        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            if (volume.getVmInstanceId() != null) {
                volume.setVmInstanceId(volume.getVmInstanceId());
            } else {
                volume.setVmInstanceId(volume.getVmInstance().getId());
            }
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                config.setUserServer();
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.has(CloudStackConstants.CS_JOB_RESULT)) {
                    if (jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                            .has(CloudStackConstants.CS_ERROR_CODE)) {
                        errors = this.validateEvent(errors, jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                .getString(CloudStackConstants.CS_ERROR_TEXT));
                        throw new ApplicationException(errors);
                    }
                    if (jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT).has(CS_VOLUME)) {
                        volume.setUuid((String) jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                .getJSONObject(CS_VOLUME).get(CloudStackConstants.CS_ID));
                        volume.setVmInstanceId(volume.getVmInstanceId());
                        volume.setStatus(Status.READY);
                    }

                }
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                        .equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                    volume.setStatus(Status.READY);
                }
            }
        }
        return volume;
    }

    /**
     * Detach volume to an instance.
     *
     * @param volume
     *            volume
     * @param errors
     *            errors
     * @throws Exception
     *             error for deatch volume
     * @return volume
     */
    public Volume detach(Volume volume, Errors errors) throws Exception {
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put(CloudStackConstants.CS_ID, volume.getUuid());
        config.setUserServer();
        String volumeS = csVolumeService.detachVolume(CloudStackConstants.JSON, optional);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject(CS_DETACH_VOLUME_RESPONSE);
        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            volume.setVmInstanceId(null);
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                config.setUserServer();
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.has(CloudStackConstants.CS_JOB_RESULT)) {
                    if (jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                            .has(CloudStackConstants.CS_ERROR_CODE)) {
                        errors = this.validateEvent(errors, jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                .getString(CloudStackConstants.CS_ERROR_TEXT));
                        throw new ApplicationException(errors);
                    }
                    if (jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT).has(CS_VOLUME)) {
                        volume.setUuid((String) jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                .getJSONObject(CS_VOLUME).get(CloudStackConstants.CS_ID));
                    }
                }
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                        .equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                    volume.setStatus(Status.READY);
                }
            }
        }
        return volume;
    }

    /**
     * Resize volume to an instance.
     *
     * @param volume
     *            volume
     * @param errors
     *            errors
     * @throws Exception
     *             error for resize volume
     * @return volume
     */
    public Volume resize(Volume volume, Errors errors) throws Exception {
        HashMap<String, String> optional = new HashMap<String, String>();
        if (volume.getDiskSize() != null) {
            optional.put(CloudStackConstants.CS_SIZE, volume.getDiskSize().toString());
        }
        if (volume.getDiskMaxIops() != null) {
            optional.put(CloudStackConstants.CS_MAX_IOPS, volume.getDiskMaxIops().toString());
        }
        if (volume.getDiskMinIops() != null) {
            optional.put(CloudStackConstants.CS_MIN_IOPS, volume.getDiskMinIops().toString());
        }
        if (volume.getIsShrink() != null) {
            optional.put(CS_SHRINK_OK, volume.getIsShrink().toString());
        }
        config.setUserServer();
        String volumeS = csVolumeService.resizeVolume(volume.getUuid(), volume.getStorageOffering().getUuid(),
                CloudStackConstants.JSON, optional);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject(CS_RESIZE_VOLUME_RESPONSE);
        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                config.setUserServer();
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                        .equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                    volume.setEventMessage(jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                            .getString(CloudStackConstants.CS_ERROR_TEXT));
                }
                if (jobresult.has(CloudStackConstants.CS_JOB_RESULT)) {
                    if (jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT).has(CS_VOLUME)) {
                        volume.setUuid((String) jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                .getJSONObject(CS_VOLUME).get(CloudStackConstants.CS_ID));
                    }
                }
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                        .equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                    volume.setStatus(Status.READY);
                    volume.setDiskSize(jobresult.getLong(CloudStackConstants.CS_SIZE));
                    volume.setDiskSizeFlag(true);
                }
            }
        }
        return volume;
    }

    @Override
    public Volume softDelete(Volume volume) throws Exception {
        volume.setIsActive(false);
        volume.setStatus(Volume.Status.DESTROY);
        Errors errors = new Errors(messageSource);
        if (volume.getIsSyncFlag()) {
            //check department and project quota validation.
            ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                    .findByDepartmentAndResourceType(volume.getDepartmentId(), ResourceType.Instance, true);
            if (departmentLimit != null) {
                if (volume.getProjectId() != null) {
              //      syncService.syncResourceLimitProject(convertEntityService.getProjectById(volume.getProjectId()));
                }
                // set server for finding value in configuration
                config.setUserServer();
                csVolumeService.deleteVolume(volume.getUuid(), CloudStackConstants.JSON);
            } else {
                errors.addGlobalError("Resource limit for department has not been set. Please update department quota");
                throw new ApplicationException(errors);
            }
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            }
                return volume;
        }
        return volumeRepo.save(volume);
    }

    /**
     * Upload volume to an instance.
     *
     * @param volume
     *            volume
     * @param userId
     *            user details
     * @param domainId
     *            domain details
     * @param errors
     *            errors
     * @throws Exception
     *             error for upload volume
     * @return volume
     */
    public Volume upload(Volume volume, Long domainId, Long userId, Errors errors) throws Exception {
        HashMap<String, String> optional = new HashMap<String, String>();

        if (volume.getProjectId() != null) {
            optional.put(CloudStackConstants.CS_PROJECT_ID,
                    convertEntityService.getProjectUuidById(volume.getProjectId()));
        } else if (volume.getDepartmentId() != null) {
            optional.put(CloudStackConstants.CS_ACCOUNT,
                    convertEntityService.getDepartmentUsernameById(volume.getDepartmentId()));

            optional.put(CloudStackConstants.CS_DOMAIN_ID,
                    departmentService.find(volume.getDepartmentId()).getDomain().getUuid());
        } else {
            if (domainId != null
                    && !convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
                optional.put(CloudStackConstants.CS_DOMAIN_ID, convertEntityService.getDomainById(domainId).getUuid());
                optional.put(CloudStackConstants.CS_ACCOUNT,
                        convertEntityService.getOwnerById(userId).getDepartment().getUserName());
            }
        }
        if (volume.getStorageOfferingId() != null) {
            optional.put(CloudStackConstants.CS_DISK_OFFERING_ID,
                    convertEntityService.getStorageOfferingById(volume.getStorageOfferingId()));
        }
        if (volume.getChecksum() != null) {
            optional.put(CS_CHECKSUM, volume.getChecksum().toString());
        }
        config.setUserServer();
        String volumeS = csVolumeService.uploadVolume(volume.getName(), volume.getFormat().name(),
                convertEntityService.getZoneUuidById(volume.getZoneId()), volume.getUrl(), CloudStackConstants.JSON,
                optional);
        try {
        JSONObject jobId = new JSONObject(volumeS).getJSONObject(CS_UPLOAD_VOLUME_RESPONSE);
        volume.setTransJobId(jobId.getString(CloudStackConstants.CS_JOB_ID));
        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                config.setUserServer();
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);

                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                        .equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                    errors = this.validateEvent(errors, jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                            .getString(CloudStackConstants.CS_ERROR_TEXT));
                    if (errors.hasErrors()) {
                        throw new ApplicationException(errors);
                    }
                }
            }
        }
    } catch (ApplicationException e) {
        LOGGER.error("ERROR AT UPLOAD VOLUME CREATION", e);
        throw new ApplicationException(e.getErrors());
    }
        return volume;
    }

    /**
     * To set response values from cloud stack.
     *
     * @param volume
     *            volume response.
     * @throws Exception
     *             error at upload volume
     */
    private void setUploadVolume(Volume volume) throws Exception {
        String uuid = volume.getUuid();
        Format format = volume.getFormat();
        String url = volume.getUrl();
        if (volumeRepo.findByUUID(volume.getUuid()) == null) {
            volume = volumeRepo.findByUUID(volume.getUuid());
        }
        volume.setUuid(uuid);
        volume.setFormat(format);
        volume.setUrl(url);
        volume.setIsActive(true);
        volume.setDiskSizeFlag(false);
        volume.setVolumeType(Volume.VolumeType.DATADISK);
        volumeRepo.save(volume);
    }

    @Override
    public List<Volume> findByDepartmentAndIsActive(Long departmentId, Boolean isActive) {
        return volumeRepo.findByDepartmentAndIsActive(departmentId, true);
    }

    @Override
    public Volume findByInstanceAndVolumeType(Long volume) throws Exception {
        return volumeRepo.findByInstanceAndVolumeType(volume, Volume.VolumeType.ROOT, true);
    }

    @Override
    public List<Volume> findByProjectAndVolumeType(Long projectId, List<VolumeType> volumeType) {
        return volumeRepo.findByProjectAndVolumeType(projectId, volumeType, true);
    }

    @Override
    public List<Volume> findByDepartmentAndVolumeType(Long departmentId, List<VolumeType> volumeType) {
        return volumeRepo.findByDepartmentAndVolumeType(departmentId, volumeType, true);
    }

    @Override
    public List<Volume> findByDepartmentAndNotProjectAndVolumeType(Long departmentId, Long projectId,
            List<VolumeType> volumeType) {
        return volumeRepo.findByDepartmentAndNotProjectAndVolumeType(departmentId, projectId, volumeType, true);
    }

    @Override
    public List<Volume> findByInstanceForResourceState(Long volume) throws Exception {
        return volumeRepo.findByInstanceAndIsActive(volume, true);
    }

    @Override
    public Integer findAttachedCount(Long userId) throws NumberFormatException, Exception {
        List<Volume.VolumeType> volumeType = new ArrayList<>();
        volumeType.add(VolumeType.DATADISK);
        volumeType.add(VolumeType.ROOT);
        List<Project> projectList = projectService.findAllByUserAndIsActive(userId, true);
        if (convertEntityService.getOwnerById(userId).getDomainId() != null
                && !convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
            if (convertEntityService.getOwnerById(userId).getType().equals(User.UserType.DOMAIN_ADMIN)) {
                Integer domainAttachedCount = volumeRepo
                        .getAttachedCountByDomain(convertEntityService.getOwnerById(userId).getDomainId(), true).size();
                return domainAttachedCount;
            } else {
                if (projectList.size() > 0) {
                    Integer projectAttachedCount = volumeRepo
                            .getAttachedCountByProject(projectList,
                                    convertEntityService.getOwnerById(userId).getDepartmentId(), volumeType, true)
                            .size();
                    return projectAttachedCount;
                } else {
                    Integer departmentAttachedCount = volumeRepo.getAttachedCountByDepartment(
                            convertEntityService.getOwnerById(userId).getDepartmentId(), volumeType, true).size();
                    return departmentAttachedCount;
                }
            }
        } else {
            Integer adminAttachedCount = volumeRepo.getAttachedCountByAdmin(true).size();
            return adminAttachedCount;
        }
    }

    @Override
    public Integer findDetachedCount(Long userId) throws NumberFormatException, Exception {
        List<Volume.VolumeType> volumeType = new ArrayList<>();
        volumeType.add(VolumeType.DATADISK);
        volumeType.add(VolumeType.ROOT);
        List<Project> projectList = projectService.findAllByUserAndIsActive(userId, true);
        if (convertEntityService.getOwnerById(userId).getDomainId() != null
                && !convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
            if (convertEntityService.getOwnerById(userId).getType().equals(User.UserType.DOMAIN_ADMIN)) {
                Integer domainDetachedCount = volumeRepo
                        .getDetachedCountByDomain(convertEntityService.getOwnerById(userId).getDomainId(), true).size();
                return domainDetachedCount;
            } else {
                if (projectList.size() > 0) {
                    Integer projectDetachedCount = volumeRepo
                            .getDetachedCountByProject(projectList,
                                    convertEntityService.getOwnerById(userId).getDepartmentId(), volumeType, true)
                            .size();
                    return projectDetachedCount;
                } else {
                    Integer departmentDetachedCount = volumeRepo.getDetachedCountByDepartment(
                            convertEntityService.getOwnerById(userId).getDepartmentId(), volumeType, true).size();
                    return departmentDetachedCount;
                }
            }
        } else {
            Integer adminDetachedCount = volumeRepo.getDetachedCountByAdmin(true).size();
            return adminDetachedCount;
        }
    }

    @Override
    public Volume save(Volume volume) throws Exception {
        if (!volume.getIsSyncFlag()) {
            return volumeRepo.save(volume);
        }
        return volume;
    }

    @Override
    public Volume findByNameAndIsActive(String volume, Long domainId, Long userId, Boolean isActive) {
        return volumeRepo.findByNameAndIsActive(volume, domainId, userId, true);
    }

    @Override
    public List<Volume> findAllByIsActive(Boolean isActive) throws Exception {
        return (List<Volume>) volumeRepo.findAllByIsActive(true);
    }

    /**
     * Check resouce capacity to create new Volume.
     *
     * @param volume Volume.
     * @param optionalMap arguments.
     * @return error message.
     * @throws Exception unhandled errors.
     */
    public String isResourceAvailable(Volume volume, HashMap<String, String> optionalMap) throws Exception {
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
                // 2.1 Total capacity in public pool for each resource type.
                Long tempTotalCapacity = Long
                        .valueOf(capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CS_CAPACITY_TOTAL));
                // 2.2 Used capacity in public pool for each resource type.
                Long tempCapacityUsed = Long
                        .valueOf(capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CS_CAPACITY_USED));
                if (GenericConstants.RESOURCE_CAPACITY.containsKey(resourceType)) {
                    // 3.1 Total available resource in public pool for each
                    // resource type.
                    resourceUsage = tempTotalCapacity - tempCapacityUsed;
                    // 4. Check whether resource is available to create new Volume
                    // with resource type.
                    switch (resourceType) {
                    // 4.3 Check secondary storage availability.
                    case GenericConstants.RESOURCE_PRIMARY_STORAGE:
                        if (volume.getDiskSize() != null) {
                            if (resourceUsage < volume.getDiskSize()) {
                                errMessage = CloudStackConstants.RESOURCE_CHECK + " primary.storage.available "
                                        + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                            }
                        } else if (resourceUsage < convertEntityService
                                .getStorageOfferById(volume.getStorageOfferingId()).getDiskSize()) {
                            errMessage = CloudStackConstants.RESOURCE_CHECK + " primary.storage.available "
                                    + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                        }
                        break;
                    default:
                        LOGGER.debug("No Resource ", resourceType);
                    }
                }
            }
        }
        // 5. If any resource shortage then return error message otherwise
        // return empty string.
        return errMessage;
    }

    @Override
    public Page<Volume> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return volumeRepo.findAllByDomainAndIsActive(domainId, true, pagingAndSorting.toPageRequest());
    }

    @Override
    public Integer findAttachedCountByDomain(Long domainId) throws NumberFormatException, Exception {
        Integer adminAttachedCount = volumeRepo.getAttachedCountByDomainAndIsActive(domainId, true).size();
        return adminAttachedCount;
    }

    @Override
    public List<Volume> findAllVolumeByUserId(Long userId) throws Exception {
        if (convertEntityService.getOwnerById(userId).getDomainId() != null
                && !convertEntityService.getOwnerById(userId).getType().equals(User.UserType.ROOT_ADMIN)) {
            if (convertEntityService.getOwnerById(userId).getType().equals(User.UserType.DOMAIN_ADMIN)) {
                return volumeRepo.findAllByDomainAndIsActive(convertEntityService.getOwnerById(userId).getDomainId(), true);
            } else {
                List<Volume.VolumeType> volumeType = new ArrayList<>();
                volumeType.add(VolumeType.DATADISK);
                volumeType.add(VolumeType.ROOT);
                if (projectService.findAllByUserAndIsActive(userId, true).size() > 0) {
                    List<Project> projectList = projectService.findAllByUserAndIsActive(userId, true);

                    return volumeRepo.findAllByProjectAndVolumeType(projectList,
                            convertEntityService.getOwnerById(userId).getDepartmentId(), volumeType, true);
                } else {
                    return volumeRepo.findByDepartmentAndVolumeType(
                            convertEntityService.getOwnerById(userId).getDepartmentId(), volumeType, true);
                }
            }
        }
        return volumeRepo.findAllVolumesByActive(true);
    }
}
