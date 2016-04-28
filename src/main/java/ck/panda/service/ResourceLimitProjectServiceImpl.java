/**
 * Resource limit allication for each project will manage in this service.
 */
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
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.ResourceLimitProject.ResourceType;
import ck.panda.domain.repository.jpa.ResourceLimitProjectRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackResourceLimitService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Resource Limit Service Implementation.
 */
@Service
public class ResourceLimitProjectServiceImpl implements ResourceLimitProjectService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLimitProjectServiceImpl.class);

    /** Constant for resource limits. */
    public static final String RESOUCE_LIMITS = "resourcelimits";

    /** Constant for resource limit. */
    public static final String CS_RESOUCE_LIMIT = "resourcelimit";

    /** Constant for list resource limit. */
    public static final String CS_LIST_RESOURCE_RESPONSE = "listresourcelimitsresponse";

    /** Constant for update resource limit. */
    public static final String CS_UPDATE_RESOURCE_RESPONSE = "updateresourcelimitresponse";

    /** Constant for max resource limits. */
    public static final String CS_MAX = "max";

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** ResourceLimitRepository repository reference. */
    @Autowired
    private ResourceLimitProjectRepository resourceLimitProjectRepo;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackResourceLimitService csResourceLimitService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Resource limit domain service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Reference of the Sync service. */
    @Autowired
    private SyncService syncService;

    @Override
    public ResourceLimitProject save(ResourceLimitProject resource) throws Exception {
        if (resource.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(RESOUCE_LIMITS, resource);
            errors = validator.validateEntity(resource, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                return resourceLimitProjectRepo.save(resource);
            }
        } else {
            return resourceLimitProjectRepo.save(resource);
        }
    }

    @Override
    public ResourceLimitProject update(ResourceLimitProject resource) throws Exception {
        if (resource.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(RESOUCE_LIMITS, resource);
            errors = validator.validateEntity(resource, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                return resourceLimitProjectRepo.save(resource);
            }
        } else {
            return resourceLimitProjectRepo.save(resource);
        }
    }

    @Override
    public void delete(ResourceLimitProject resource) throws Exception {
        resourceLimitProjectRepo.delete(resource);
    }

    @Override
    public void delete(Long id) throws Exception {
        resourceLimitProjectRepo.delete(id);
    }

    @Override
    public ResourceLimitProject find(Long id) throws Exception {
        ResourceLimitProject resourceLimit = resourceLimitProjectRepo.findOne(id);
        if (resourceLimit == null) {
            throw new EntityNotFoundException("ResourceLimit.not.found");
        }
        return resourceLimit;
    }

    @Override
    public Page<ResourceLimitProject> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return resourceLimitProjectRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ResourceLimitProject> findAll() throws Exception {
        return (List<ResourceLimitProject>) resourceLimitProjectRepo.findAll();
    }

    /**
     * To set optional values by validating null and empty parameters.
     *
     * @param resource optional resource limit values
     * @return optional values
     */
    public HashMap<String, String> optionalValues(ResourceLimitProject resource) {
        HashMap<String, String> optionalMap = new HashMap<String, String>();
        if (resource.getProject() != null) {
            optionalMap.put(CloudStackConstants.CS_PROJECT_ID, resource.getProject().getUuid());
        }
        if (resource.getMax() != null) {
            optionalMap.put(CS_MAX, resource.getMax().toString());
        }
        return optionalMap;
    }

    @Override
    public List<ResourceLimitProject> findAllFromCSServerProject(String projectId) throws Exception {
        List<ResourceLimitProject> resourceList = new ArrayList<ResourceLimitProject>();
        HashMap<String, String> resourceMap = new HashMap<String, String>();
        resourceMap.put(CloudStackConstants.CS_PROJECT_ID, projectId);
        // 1. Get the list of ResourceLimit from CS server using CS connector
        String response = csResourceLimitService.listResourceLimits(CloudStackConstants.JSON, resourceMap);
        JSONArray resourceListJSON = new JSONObject(response).getJSONObject(CS_LIST_RESOURCE_RESPONSE)
                .getJSONArray(CS_RESOUCE_LIMIT);
        // 2. Iterate the json list, convert the single json entity to
        // Resource limit
        for (int i = 0, size = resourceListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to StorageOffering entity
            // and Add the converted Resource limit entity to list
            ResourceLimitProject resource = ResourceLimitProject.convert(resourceListJSON.getJSONObject(i));
            resource.setProjectId(convertEntityService.getProject(resource.getTransProjectId()).getId());
            resource.setUniqueSeperator(
                    resource.getTransProjectId() + "-" + ResourceType.values()[(resource.getTransResourceType())]);
            resource.setDomainId(convertEntityService.getDomainId(resource.getTransDomainId()));
            resource.setDepartmentId(convertEntityService.getProjectById(resource.getProjectId()).getDepartmentId());
            resource.setUniqueSeperator(resource.getUniqueSeperator() + resource.getResourceType().toString());
            resourceList.add(resource);
        }
        return resourceList;
    }

    /**
     * updating resource limits for project.
     *
     * @param resource resource of project.
     * @throws Exception error at resource limit project
     */
    private void updateResourceProject(ResourceLimitProject resource) throws Exception {
        config.setServer(1L);
        String resourceLimits = csResourceLimitService.updateResourceLimit(resource.getResourceType().ordinal(), CloudStackConstants.JSON,
                optionalValues(resource));
        LOGGER.info("Resource limit project update response " + resourceLimits);
        JSONObject resourceLimitsResponse = new JSONObject(resourceLimits).getJSONObject(CS_UPDATE_RESOURCE_RESPONSE)
                .getJSONObject(CS_RESOUCE_LIMIT);
        if (resourceLimitsResponse.has(CloudStackConstants.CS_ERROR_CODE)) {
            LOGGER.debug("ERROR IN RESOURCE PROJECT");
        } else {
            resource.setDomainId(resource.getDomain().getId());
            resource.setResourceType(resource.getResourceType());
            resource.setMax(resource.getMax());
        }
    }

    @Override
    @PreAuthorize("hasPermission(null, 'PROJECT_QUOTA_EDIT')")
    public List<ResourceLimitProject> createResourceLimits(List<ResourceLimitProject> resourceLimits) throws Exception {
        Errors errors = this.validateResourceLimit(resourceLimits);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            for (ResourceLimitProject resource : resourceLimits) {
                if (resource.getId() != null) {
                    ResourceLimitProject resourceData = resourceLimitProjectRepo.findOne(resource.getId());
                    resourceData.setMax(resource.getMax());
                    resourceData.setIsActive(true);
                    updateResourceProject(resourceData);
                    resourceLimitProjectRepo.save(resourceData);
                    ResourceLimitDepartment resourceDatas = resourceLimitDepartmentService.findByDepartmentAndResourceType(resource.getDepartmentId(), updateUsedCount(resourceData), true);
                    Long resourceCount = resourceLimitProjectRepo.findByDepartmentIdAndResourceType(resource.getDepartmentId(), resource.getResourceType(), true);
                    Long resourceCounts = resourceLimitProjectRepo.findByDepartmentIdAndResourceTypeAndResourceMax(resource.getDepartmentId(), resource.getResourceType(), true);
                    resourceDatas.setUsedLimit(EmptytoLong(resourceCounts) + EmptytoLong(resourceCount));
                    resourceLimitDepartmentService.save(resourceDatas);
                } else {
                    updateResourceProject(resource);
                    resource.setIsActive(true);
                    resourceLimitProjectRepo.save(resource);
                    ResourceLimitDepartment resourceDatas = resourceLimitDepartmentService.findByDepartmentAndResourceType(resource.getDepartmentId(), updateUsedCount(resource), true);
                    Long resourceCount = resourceLimitProjectRepo.findByDepartmentIdAndResourceType(resource.getDepartmentId(), resource.getResourceType(), true);
                    Long resourceCounts = resourceLimitProjectRepo.findByDepartmentIdAndResourceTypeAndResourceMax(resource.getDepartmentId(), resource.getResourceType(), true);
                    resourceDatas.setUsedLimit(EmptytoLong(resourceCounts) + EmptytoLong(resourceCount));
                    resourceLimitDepartmentService.save(resourceDatas);
                }
            }
        }
        return (List<ResourceLimitProject>) resourceLimitProjectRepo.findAll();
    }


    /**
     * Resource type based on project resource.
     *
     * @param resource type of the project.
     * @return resource type
     * @throws Exception error in resource limit project.
     */
    public ResourceLimitDepartment.ResourceType updateUsedCount(ResourceLimitProject resource)
            throws Exception {
        switch (resource.getResourceType()) {
            case Instance:
                return ResourceLimitDepartment.ResourceType.Instance;
            case IP:
                return ResourceLimitDepartment.ResourceType.IP;
            case Volume:
                return ResourceLimitDepartment.ResourceType.Volume;
            case Snapshot:
                return ResourceLimitDepartment.ResourceType.Snapshot;
            case Template:
                return ResourceLimitDepartment.ResourceType.Template;
            case Project:
                return ResourceLimitDepartment.ResourceType.Project;
            case Network:
                return ResourceLimitDepartment.ResourceType.Network;
            case VPC:
                return ResourceLimitDepartment.ResourceType.VPC;
            case CPU:
                return ResourceLimitDepartment.ResourceType.CPU;
            case Memory:
                return ResourceLimitDepartment.ResourceType.Memory;
            case PrimaryStorage:
                return ResourceLimitDepartment.ResourceType.PrimaryStorage;
            case SecondaryStorage:
                return ResourceLimitDepartment.ResourceType.SecondaryStorage;
            default:
                break;
        }
        return null;
    }

    /**
     * Delete resource limit by project.
     *
     * @param projectId project.
     */
    public void deleteResourceLimitByProject(Long projectId) {
        List<ResourceLimitProject> resourceLimits = resourceLimitProjectRepo.findAllByProjectIdAndIsActive(projectId,
                true);
        for (ResourceLimitProject resource : resourceLimits) {
            resourceLimitProjectRepo.delete(resource);
        }
    }

    /**
     * Validating resource limit based on domain resource limits.
     *
     * @param resourceLimits resource limits
     * @return if error with resource.
     * @throws Exception error in resource limit project.
     */
    private Errors validateResourceLimit(List<ResourceLimitProject> resourceLimits) throws Exception {
        Errors errors = new Errors(messageSource);
        for (ResourceLimitProject resourceLimit : resourceLimits) {
            if (!resourceLimit.getResourceType().equals(ResourceLimitProject.ResourceType.Project)) {
            // Step1: Find max from domain with specific resource type.
            ResourceLimitDepartment departmntLimit = resourceLimitDepartmentService.findByDepartmentAndResourceType(
                    convertEntityService.getProjectById(resourceLimit.getProjectId()).getDepartmentId(),
                    ResourceLimitDepartment.ResourceType.valueOf(resourceLimit.getResourceType().name()), true);
            ResourceLimitProject projectLimit = resourceLimitProjectRepo.findByProjectAndResourceType(resourceLimit.getProjectId(), ResourceLimitProject.ResourceType.valueOf(resourceLimit.getResourceType().name()), true);
            // Step2: Find resource count from department for spcific domain and
            // resource type
            Long totalCount = 0L;
			if(resourceLimit.getMax() == -1){
				totalCount = resourceLimit.getMax();
			} else {
				totalCount = resourceLimit.getMax() + (departmntLimit.getUsedLimit() - projectLimit.getMax());
			}
            // if(step1 < step2)
            if (departmntLimit != null) {
            	if(resourceLimit.getMax() == departmntLimit.getMax() && departmntLimit.getMax() == -1L) {

            	} else if (departmntLimit.getMax() != -1 && departmntLimit.getMax() < totalCount) {
                    errors.addFieldError(resourceLimit.getResourceType().toString(),
                    		departmntLimit.getMax() + " in " + resourceLimit.getResourceType().toString() + " " + " for resource limit department exceeded");
                }
            } else {
                errors.addGlobalError("update.department.quota.first");
            }
        	}
        }
        return errors;
    }

    @Override
    public Long findByResourceCountByProjectAndResourceType(Long departmentId,
            ResourceLimitProject.ResourceType resourceType, Long projectId, Boolean isActive) throws Exception {
        Long count = resourceLimitProjectRepo.findByResourceCountByProjectAndResourceType(departmentId, resourceType,
                projectId, isActive);
        Long count1  = resourceLimitProjectRepo.findByResourceCountByProjectAndResourceTypes(departmentId, resourceType,
                projectId, isActive);
        return EmptytoLong(count) + EmptytoLong(count1);
    }

    @Override
    public List<ResourceLimitProject> findAllByProjectIdAndIsActive(Long projectId, Boolean isActive) throws ApplicationException, Exception {
        return (List<ResourceLimitProject>) resourceLimitProjectRepo.findAllByProjectIdAndIsActive(projectId, isActive);
    }

    @Override
    public ResourceLimitProject findByProjectAndResourceType(Long projectId, ResourceType resourceType,
            Boolean isActive) throws Exception {
        return resourceLimitProjectRepo.findByProjectAndResourceType(projectId, resourceType, isActive);
    }

    @Override
    public ResourceLimitProject findResourceByProjectAndResourceType(Long projectId, ResourceType resourceType,
            Boolean isActive) throws Exception {
        return resourceLimitProjectRepo.findResourceByProjectAndResourceType(projectId, resourceType, isActive);
    }

    @Override
    public List<ResourceLimitProject> findAllByProjectAndIsActive(Long projectId, Boolean isActive) throws ApplicationException, Exception {
        //This call is for update resource limit form ACS.
        return (List<ResourceLimitProject>) resourceLimitProjectRepo.findAllByProjectIdAndIsActive(projectId, isActive);
    }

    @Override
    public HashMap<String, String> getResourceLimitsOfProject(Long domainId) {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
        for (String name : resourceTypeMap.keySet()) {
            Long resourceProjectCount = resourceLimitProjectRepo.findTotalCountOfResourceProject(domainId, ResourceLimitProject.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            Long resourceProjectCounts = resourceLimitProjectRepo.findTotalCountOfResourceProjects(domainId, ResourceLimitProject.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            resourceMaxCount.put(resourceTypeMap.get(name), String.valueOf((EmptytoLong(resourceProjectCount) + EmptytoLong(resourceProjectCounts))));
        }
        return resourceMaxCount;
    }

    @Override
    public Long getTotalCountOfResourceProject(Long departmentId, ResourceLimitProject.ResourceType resourceType) {
            Long resourceProjectCount = resourceLimitProjectRepo.findTotalCountOfResourceDepartment(departmentId, resourceType, true);
            Long resourceProjectCounts = resourceLimitProjectRepo.findTotalCountOfResourceDepartments(departmentId, resourceType, true);
        return EmptytoLong(resourceProjectCount) + EmptytoLong(resourceProjectCounts);
    }
    @Override
    public HashMap<String, String> getResourceLimitsOfDepartment(Long departmentId) {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
        for (String name : resourceTypeMap.keySet()) {
            Long resourceProjectCount = resourceLimitProjectRepo.findTotalCountOfResourceDepartment(departmentId, ResourceLimitProject.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            Long resourceProjectCounts = resourceLimitProjectRepo.findTotalCountOfResourceDepartments(departmentId, ResourceLimitProject.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceProjectCount != null) {
                resourceMaxCount.put(resourceTypeMap.get(name), String.valueOf((EmptytoLong(resourceProjectCount) + EmptytoLong(resourceProjectCounts))));
            }
        }
        return resourceMaxCount;
    }

    @Override
    public HashMap<String, Long> getSumOfProjectMin(Long id) throws Exception {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, Long> resourceMap = new HashMap<String, Long>();
        for (String name : resourceTypeMap.keySet()) {
            ResourceLimitProject resourceLimitProject = resourceLimitProjectRepo.findByProjectAndResourceType(id, ResourceLimitProject.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceLimitProject != null) {
                resourceMap.put(resourceTypeMap.get(name),resourceLimitProject.getUsedLimit());
            }
        }
        return resourceMap;
    }

    @Override
    public HashMap<String, Long> getSumOfProjectMax(Long id) throws Exception {
        Project projectResponse = convertEntityService.getProjectById(id);
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, Long> resourceMap = new HashMap<String, Long>();
        for (String name : resourceTypeMap.keySet()) {
            ResourceLimitDepartment resourceLimitDepartment = resourceLimitDepartmentService.findByDepartmentAndResourceType(projectResponse.getDepartmentId(), ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            ResourceLimitProject resourceLimitProject = resourceLimitProjectRepo.findByProjectAndResourceType(id, ResourceLimitProject.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceLimitProject != null) {
                if (resourceLimitDepartment.getMax() == -1) {
                    resourceMap.put(resourceTypeMap.get(name), -1L);
                } else {
                    resourceMap.put(resourceTypeMap.get(name),resourceLimitProject.getMax() + (resourceLimitDepartment.getMax() - resourceLimitDepartment.getUsedLimit()));
                }
            } else {
                if (resourceLimitDepartment.getUsedLimit() == null) {
                    resourceMap.put(resourceTypeMap.get(name), resourceLimitDepartment.getMax());
                } else {
                	 if (resourceLimitDepartment.getMax() == -1) {
                		 resourceMap.put(resourceTypeMap.get(name), -1L);
                	 } else {
                		 resourceMap.put(resourceTypeMap.get(name),(resourceLimitDepartment.getMax() - resourceLimitDepartment.getUsedLimit()));
                	 }
                }
            }
        }
        return resourceMap;
    }

    /**
     * Empty check.
     *
     * @param value long value
     * @return long.
     */
    public Long EmptytoLong(Long value) {
        if (value == null) {
            return 0L;
        }
        return value;
    }

}
