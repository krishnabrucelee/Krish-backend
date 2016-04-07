package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.repository.jpa.ResourceLimitDepartmentRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackResourceLimitService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Resource Limit Department Service Implementation.
 */
@Service
public class ResourceLimitDepartmentServiceImpl implements ResourceLimitDepartmentService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLimitDomainServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** ResourceLimitRepository repository reference. */
    @Autowired
    private ResourceLimitDepartmentRepository resourceLimitDepartmentRepo;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackResourceLimitService csResourceLimitService;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Resource limit domain service reference. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /** Resource limit project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** Convert Entity service reference. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** CloudStack Resource Capacity Service. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    @Override
    public ResourceLimitDepartment save(ResourceLimitDepartment resource) throws Exception {
        Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
        errors = validator.validateEntity(resource, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return resourceLimitDepartmentRepo.save(resource);
        }

    }

    @Override
    public ResourceLimitDepartment update(ResourceLimitDepartment resource) throws Exception {
        Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
        errors = validator.validateEntity(resource, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return resourceLimitDepartmentRepo.save(resource);
        }
    }

    @Override
    public void delete(ResourceLimitDepartment resource) throws Exception {
        resourceLimitDepartmentRepo.delete(resource);
    }

    @Override
    public void delete(Long id) throws Exception {
        resourceLimitDepartmentRepo.delete(id);
    }

    @Override
    public ResourceLimitDepartment find(Long id) throws Exception {
        ResourceLimitDepartment resourceLimit = resourceLimitDepartmentRepo.findOne(id);

        LOGGER.debug("Sample Debug Message");
        LOGGER.trace("Sample Trace Message");

        if (resourceLimit == null) {
            throw new EntityNotFoundException("ResourceLimit.not.found");
        }
        return resourceLimit;
    }

    @Override
    public Page<ResourceLimitDepartment> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return resourceLimitDepartmentRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ResourceLimitDepartment> findAll() throws Exception {
        return (List<ResourceLimitDepartment>) resourceLimitDepartmentRepo.findAll();
    }

    /**
     * To set optional values by validating null and empty parameters.
     *
     * @param resource optional resource limit values
     * @return optional values
     */
    public HashMap<String, String> optional(ResourceLimitDepartment resource) {
        HashMap<String, String> optional = new HashMap<String, String>();

        if (resource.getDomainId() != null) {
            optional.put("domainid", resource.getDomain().getUuid());
        }

        if (resource.getDomain() != null) {
            optional.put("domain", resource.getDomain().getName());
        }

        if (resource.getDepartment() != null) {
            optional.put("account", resource.getDepartment().getUserName());
        }

        if (resource.getMax() != null) {
            optional.put("max", resource.getMax().toString());
        }
        return optional;
    }

    @Override
    @Transactional
    @PreAuthorize("hasPermission(null, 'DEPARTMENT_QUOTA_EDIT')")
    public List<ResourceLimitDepartment> createResourceLimits(List<ResourceLimitDepartment> resourceLimits)
            throws Exception {
        Errors errors = this.validateResourceLimit(resourceLimits);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {

            // this.deleteResourceLimitByDepartment(resourceLimits.get(0).getDepartment().getId());
            for (ResourceLimitDepartment resource : resourceLimits) {
                if (resource.getId() != null) {
                    ResourceLimitDepartment resourceData = resourceLimitDepartmentRepo.findOne(resource.getId());
                    resourceData.setMax(resource.getMax());
                    updateResourceDepartment(resourceData);
                    resourceData.setIsActive(true);
                    resourceLimitDepartmentRepo.save(resourceData);
                } else {
                        updateResourceDepartment(resource);
                        resource.setIsActive(true);
                        resourceLimitDepartmentRepo.save(resource);
                }
            }
        }
        return (List<ResourceLimitDepartment>) resourceLimitDepartmentRepo.findAll();
    }

    /**
     * Delete Resource limit.
     *
     * @param departmentId department id.
     */
    private void deleteResourceLimitByDepartment(Long departmentId) {
        List<ResourceLimitDepartment> resourceLimits = resourceLimitDepartmentRepo
                .findAllByDepartmentIdAndIsActive(departmentId, true);
        for (ResourceLimitDepartment resource : resourceLimits) {
            resourceLimitDepartmentRepo.delete(resource);
        }
    }

    /**
     * updating resource limits for department.
     *
     * @param resource resource of department.
     * @throws Exception error
     */
    private void updateResourceDepartment(ResourceLimitDepartment resource) throws Exception {
        config.setServer(1L);
        String resourceLimits = csResourceLimitService.updateResourceLimit(resource.getResourceType().ordinal(), "json",
                optional(resource));
        LOGGER.info("Resource limit update response " + resourceLimits);
        JSONObject resourceLimitsResponse = new JSONObject(resourceLimits).getJSONObject("updateresourcelimitresponse")
                .getJSONObject("resourcelimit");
        if (resourceLimitsResponse.has("errorcode")) {
            LOGGER.debug("ERROR IN RESOURCE DEPARTMENT");
        } else {
            resource.setDomainId(resource.getDomain().getId());
            resource.setResourceType(resource.getResourceType());
            resource.setMax(resource.getMax());
        }
    }

    /**
     * Validating resource limit based on domain resource limits.
     *
     * @param resourceLimits resource limits
     * @return if error with resource.
     * @throws Exception error.
     */
    private Errors validateResourceLimit(List<ResourceLimitDepartment> resourceLimits) throws Exception {
        Errors errors = new Errors(messageSource);
        for (ResourceLimitDepartment resourceLimit : resourceLimits) {
            // Step1: Find max from domain with specific resource type.
            ResourceLimitDomain domainLimit = resourceLimitDomainService.findByDomainAndResourceType(
                    resourceLimit.getDomainId(),
                    ResourceLimitDomain.ResourceType.valueOf(resourceLimit.getResourceType().name()), true);
            // Step2: Find resource count from department for spcific domain and
            // resource type
            Long count = findByResourceCountByDepartmentAndResourceType(resourceLimit.getDomainId(),
                    resourceLimit.getResourceType(), resourceLimit.getDepartmentId(), true);
            Long totalCount = resourceLimit.getMax() + count;
            // if(step1 < step2) {
            if (domainLimit != null) {
                if (domainLimit.getMax() != -1 && domainLimit.getMax() < totalCount) {
                    errors.addFieldError(resourceLimit.getResourceType().toString(),
                            domainLimit.getMax() + " in " + resourceLimit.getResourceType().toString() + " " + " for resource limit domain exceeded");

                }
            } else {
                errors.addGlobalError("update.domain.quota.first");
            }
            // Comparing with project
                Long projectResourceCount = resourceLimitProjectService.findByResourceCountByProjectAndResourceType(
                        resourceLimit.getDepartmentId(),
                        ResourceLimitProject.ResourceType.valueOf(resourceLimit.getResourceType().name()),
                        0L, true);
                if (resourceLimit.getMax() < projectResourceCount) {
                    errors.addFieldError(resourceLimit.getResourceType().toString(),
                            projectResourceCount + " in " + resourceLimit.getResourceType().toString()
                                    + "already allocated to projects of this department");
                }
        }
        return errors;
    }

    @Override
    public List<ResourceLimitDepartment> findAllByDepartmentIdAndIsActive(Long departmentId, Boolean isActive)
            throws Exception {
        return (List<ResourceLimitDepartment>) resourceLimitDepartmentRepo
                .findAllByDepartmentIdAndIsActive(departmentId, isActive);
    }

    @Override
    public Long findByResourceCountByDepartmentAndResourceType(Long domainId,
            ResourceLimitDepartment.ResourceType resourceType, Long departmentId, Boolean isActive) throws Exception {
        return resourceLimitDepartmentRepo.findByResourceCountByDepartmentAndResourceType(domainId, resourceType,
                departmentId, isActive);
    }

    @Override
    public ResourceLimitDepartment findByDepartmentAndResourceType(Long departmentId,
            ResourceLimitDepartment.ResourceType resourceType, Boolean isActive) {
        return resourceLimitDepartmentRepo.findByDepartmentAndResourceType(departmentId, resourceType, isActive);
    }

    @Override
    public HashMap<String, String> getResourceLimitsOfDepartment(Long domainId) {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
        for(String name : resourceTypeMap.keySet()) {
            Long resourceDepartmentCount = resourceLimitDepartmentRepo.findTotalCountOfResourceDepartment(domainId, ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceDepartmentCount != null) {
            resourceMaxCount.put(resourceTypeMap.get(name), resourceDepartmentCount.toString());
            }
        }
        return resourceMaxCount;
    }

    @Override
    public HashMap<String, String> getResourceLimitsOfProject(Long projectId) {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
        for(String name : resourceTypeMap.keySet()) {
            Long resourceDepartmentCount = resourceLimitDepartmentRepo.findTotalCountOfResourceProject(projectId, ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceDepartmentCount != null) {
            resourceMaxCount.put(resourceTypeMap.get(name), resourceDepartmentCount.toString());
            }
        }

        return resourceMaxCount;

    }

    @Override
    public HashMap<String, Long> getSumOfDepartmentMin(Long id) throws Exception {
        // Used for setting optional values for resource count.
        HashMap<String, String> domainCountMap = new HashMap<String, String>();
        domainCountMap.put(CloudStackConstants.CS_ACCOUNT, convertEntityService.getDepartmentById(id).getUserName());
        // Sync for resource count in domain
        String csResponse = cloudStackResourceCapacity.updateResourceCount(
                convertEntityService.getDepartmentById(id).getDomain().getUuid(), domainCountMap, "json");
        convertEntityService.resourceCount(csResponse);
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, Long> resourceMap = new HashMap<String, Long>();
        for (String name : resourceTypeMap.keySet()) {
            ResourceLimitDepartment resourceLimit = resourceLimitDepartmentRepo.findByDepartmentAndResourceType(id,
                    ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            Long resourceProjectCount = resourceLimitProjectService.getTotalCountOfResourceProject(id,
                    ResourceLimitProject.ResourceType.valueOf(resourceTypeMap.get(name)));
            if (resourceLimit == null || resourceProjectCount == null) {
                resourceMap.put(resourceTypeMap.get(name), (0L + 0L));
            } else {
                resourceMap.put(resourceTypeMap.get(name),
                        (resourceProjectCount + resourceLimit.getUsedLimit()));
            }
        }
        return resourceMap;
    }

    @Override
    public HashMap<String, Long> getSumOfDepartmentMax(Long id) throws Exception {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, Long> resourceMap = new HashMap<String, Long>();
        for (String name : resourceTypeMap.keySet()) {
            ResourceLimitDepartment resourceLimit = resourceLimitDepartmentRepo.findByDepartmentAndResourceType(id,
                    ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            ResourceLimitDomain domainLimitMax = resourceLimitDomainService.findByDomainAndResourceType(
                    convertEntityService.getDepartmentById(id).getDomainId(),
                    ResourceLimitDomain.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            Long resourceDepartmentCount = resourceLimitDepartmentRepo.findTotalCountOfResourceDepartment(
                    convertEntityService.getDepartmentById(id).getDomainId(),
                    ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (domainLimitMax == null || resourceDepartmentCount == null) {
                resourceMap.put(resourceTypeMap.get(name), ((0L) + (domainLimitMax.getMax() - 0L)));
            } else {
                resourceMap.put(resourceTypeMap.get(name),
                        ((resourceLimit.getMax()) + (domainLimitMax.getMax() - resourceDepartmentCount)));
            }
        }
        return resourceMap;
    }

}
