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
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.repository.jpa.ResourceLimitDepartmentRepository;
import ck.panda.util.AppValidator;
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

    @Override
    public ResourceLimitDepartment save(ResourceLimitDepartment resource) throws Exception {
		if (resource.getIsSyncFlag()) {
			Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
			errors = validator.validateEntity(resource, errors);
			if (errors.hasErrors()) {
				throw new ApplicationException(errors);
			} else {
				return resourceLimitDepartmentRepo.save(resource);
			}
		} else {
			return resourceLimitDepartmentRepo.save(resource);
		}
    }

    @Override
    public ResourceLimitDepartment update(ResourceLimitDepartment resource) throws Exception {
		if (resource.getIsSyncFlag()) {
			Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
			errors = validator.validateEntity(resource, errors);

			if (errors.hasErrors()) {
				throw new ApplicationException(errors);
			} else {
				return resourceLimitDepartmentRepo.save(resource);
			}
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
     * @throws Exception if error occurs
     */
    public HashMap<String, String> optional(ResourceLimitDepartment resource) throws Exception {
        HashMap<String, String> optional = new HashMap<String, String>();
        if (resource.getDomainId() != null) {
            optional.put("domainid", convertEntityService.getDomainUuidById(resource.getDomainId()));
        }
        if (resource.getDomain() != null) {
            optional.put("domain", convertEntityService.getDomainById(resource.getDomainId()).getName());
        }
        if (resource.getDepartment() != null) {
            optional.put("account", (convertEntityService.getDepartmentById(resource.getDepartmentId()).getUserName()));
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
            for (ResourceLimitDepartment resource : resourceLimits) {
                if (resource.getId() != null) {
                    ResourceLimitDepartment resourceData = resourceLimitDepartmentRepo.findOne(resource.getId());
                    resourceData.setMax(resource.getMax());
                    updateResourceDepartment(resourceData);
                    resourceData.setIsActive(true);
                    resourceLimitDepartmentRepo.save(resourceData);
                    ResourceLimitDomain resourceDatas = resourceLimitDomainService.findByDomainAndResourceCount(resource.getDomainId(), updateUsedCount(resourceData), true);
                    Long resourceCount = resourceLimitDepartmentRepo.findByDomainIdAndResourceType(resource.getDomainId(),resource.getResourceType(),true);
                    Long resourceCounts = resourceLimitDepartmentRepo.findByDomainIdAndResourceTypeAndResourceMax(resource.getDomainId(),resource.getResourceType(),true);
                    resourceDatas.setMax(resourceDatas.getMax());
                    resourceDatas.setUsedLimit((EmptytoLong(resourceCounts) + EmptytoLong(resourceCount)));
                    resourceDatas.setIsSyncFlag(false);
                    resourceLimitDomainService.save(resourceDatas);
                } else {
                    updateResourceDepartment(resource);
                    resource.setIsActive(true);
                    resourceLimitDepartmentRepo.save(resource);
                    ResourceLimitDomain resourceDatas = resourceLimitDomainService.findByDomainAndResourceCount(resource.getDomainId(), updateUsedCount(resource), true);
                    Long resourceCount = resourceLimitDepartmentRepo.findByDomainIdAndResourceType(resource.getDomainId(),resource.getResourceType(),true);
                    Long resourceCounts = resourceLimitDepartmentRepo.findByDomainIdAndResourceTypeAndResourceMax(resource.getDomainId(),resource.getResourceType(),true);
                    resourceDatas.setMax(resourceDatas.getMax());
                    resourceDatas.setUsedLimit(EmptytoLong(resourceCount) + EmptytoLong(resourceCounts));
                    resourceDatas.setIsSyncFlag(false);
                    resourceLimitDomainService.save(resourceDatas);
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
        if(!resource.getResourceType().equals(ResourceLimitDepartment.ResourceType.Project)) {
			String resourceLimits = csResourceLimitService.updateResourceLimit(resource.getResourceType().ordinal(),
					"json", optional(resource));
			LOGGER.info("Resource limit update response " + resourceLimits);
			JSONObject resourceLimitsResponse = new JSONObject(resourceLimits)
					.getJSONObject("updateresourcelimitresponse").getJSONObject("resourcelimit");
			if (resourceLimitsResponse.has("errorcode")) {
				LOGGER.debug("ERROR IN RESOURCE DEPARTMENT");
			} else {
				resource.setDomainId(resource.getDomainId());
				resource.setResourceType(resource.getResourceType());
				resource.setMax(resource.getMax());
			}
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
			if (!resourceLimit.getResourceType().equals(ResourceLimitDepartment.ResourceType.Project)) {
				// Step1: Find max from domain with specific resource type.
				ResourceLimitDomain domainLimit = resourceLimitDomainService.findByDomainAndResourceType(
						resourceLimit.getDomainId(),
						ResourceLimitDomain.ResourceType.valueOf(resourceLimit.getResourceType().name()), true);
				// Step2: Find resource count from department for specific domain and resource type.
				ResourceLimitDepartment department = resourceLimitDepartmentRepo.findByDepartmentAndResourceType(resourceLimit.getDepartmentId(), ResourceLimitDepartment.ResourceType.valueOf(resourceLimit.getResourceType().name()), true);
				Long totalCount = 0L;
				if(resourceLimit.getMax() == -1){
					totalCount = EmptytoLong(resourceLimit.getMax());
				} else {
					totalCount = EmptytoLong(resourceLimit.getMax()) + (EmptytoLong(domainLimit.getUsedLimit()) - EmptytoLong(department.getMax()));
				}
				// if(step1 < step2) {
				if (domainLimit != null) {
					if (EmptytoLong(resourceLimit.getMax()) == EmptytoLong(domainLimit.getMax()) && EmptytoLong(domainLimit.getMax()) == -1L) {

					} else if (EmptytoLong(domainLimit.getMax()) !=-1 && EmptytoLong(domainLimit.getMax()) < totalCount) {
						errors.addFieldError(resourceLimit.getResourceType().toString(),
								domainLimit.getMax() + " in " + resourceLimit.getResourceType().toString() + " "
										+ " for resource limit domain exceeded");
					}
				} else {
					errors.addGlobalError("update.domain.quota.first");
				}
			}
		}
		return errors;
	}

    /**
     * Resource type based on department resource.
     *
     * @param resource of the department.
     * @return resource type
     * @throws Exception if error occurs
     */
    public ResourceLimitDomain.ResourceType updateUsedCount(ResourceLimitDepartment resource)
            throws Exception {
        switch (resource.getResourceType()) {
            case Instance:
                return ResourceLimitDomain.ResourceType.Instance;
            case IP:
                return ResourceLimitDomain.ResourceType.IP;
            case Volume:
                return ResourceLimitDomain.ResourceType.Volume;
            case Snapshot:
                return ResourceLimitDomain.ResourceType.Snapshot;
            case Template:
                return ResourceLimitDomain.ResourceType.Template;
            case Project:
                return ResourceLimitDomain.ResourceType.Project;
            case Network:
                return ResourceLimitDomain.ResourceType.Network;
            case VPC:
                return ResourceLimitDomain.ResourceType.VPC;
            case CPU:
                return ResourceLimitDomain.ResourceType.CPU;
            case Memory:
                return ResourceLimitDomain.ResourceType.Memory;
            case PrimaryStorage:
                return ResourceLimitDomain.ResourceType.PrimaryStorage;
            case SecondaryStorage:
                return ResourceLimitDomain.ResourceType.SecondaryStorage;
            default:
                break;
        }
        return null;
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
        Long count1 = resourceLimitDepartmentRepo.findByResourceCountByDepartmentAndResourceType(domainId, resourceType,
                departmentId, isActive);
        Long count2 = resourceLimitDepartmentRepo.findByResourceCountByDepartmentAndResourceTypes(domainId, resourceType,
                departmentId, isActive);
        return EmptytoLong(count1) + EmptytoLong(count2);
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
        for (String name : resourceTypeMap.keySet()) {
            Long resourceDepartmentCount = resourceLimitDepartmentRepo.findTotalCountOfResourceDepartment(domainId, ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceDepartmentCount != null) {
            resourceMaxCount.put(resourceTypeMap.get(name), EmptytoLong(resourceDepartmentCount).toString());
            }
        }
        return resourceMaxCount;
    }


	@Override
	public HashMap<String, String> getResourceCountsOfDepartment(Long departmentId) {
		HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
		HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
		for (String name : resourceTypeMap.keySet()) {
			Long resourceDepartmentCount = resourceLimitDepartmentRepo.findResourceTotalCountOfResourceDepartment(
					departmentId, ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
			if (resourceDepartmentCount != null) {
				resourceMaxCount.put(resourceTypeMap.get(name), EmptytoLong(resourceDepartmentCount).toString());
			}
		}
		return resourceMaxCount;
	}

	@Override
	public HashMap<String, String> getResourceCountsOfDomain(Long domainId) {
		HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
		HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
		for (String name : resourceTypeMap.keySet()) {
			Long resourceDepartmentCount = resourceLimitDepartmentRepo.findResourceTotalCountOfResourceDomain(
					domainId, ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
			Long resourceCount = resourceLimitDepartmentRepo.findResourceTotalCountOfResourceDomains(
					domainId, ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
			resourceMaxCount.put(resourceTypeMap.get(name), String.valueOf((EmptytoLong(resourceDepartmentCount) + EmptytoLong(resourceCount))));

		}
		return resourceMaxCount;
	}

    @Override
    public HashMap<String, String> getResourceLimitsOfProject(Long projectId) {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
		for (String name : resourceTypeMap.keySet()) {
			Long resourceDepartmentCount = resourceLimitDepartmentRepo.findTotalCountOfResourceProject(projectId,
					ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
			Long resourceDepartmentCounts = resourceLimitDepartmentRepo.findTotalCountOfResourceProjects(projectId,
					ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
			if (resourceDepartmentCount != null) {
				resourceMaxCount.put(resourceTypeMap.get(name),
						String.valueOf((EmptytoLong(resourceDepartmentCount) + EmptytoLong(resourceDepartmentCounts))));
			}
		}
        return resourceMaxCount;
    }

    @Override
    public HashMap<String, Long> getSumOfDepartmentMin(Long id) throws Exception {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, Long> resourceMap = new HashMap<String, Long>();
        for (String name : resourceTypeMap.keySet()) {
            ResourceLimitDepartment resourceLimitDepartment = resourceLimitDepartmentRepo.findByDepartmentAndResourceType(id, ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceLimitDepartment != null) {
                resourceMap.put(resourceTypeMap.get(name), EmptytoLong(resourceLimitDepartment.getUsedLimit()));
            }
        }
        return resourceMap;
    }

    @Override
    public HashMap<String, Long> getSumOfDepartmentMax(Long id) throws Exception {
        Department departmentResponse = convertEntityService.getDepartmentById(id);
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, Long> resourceMap = new HashMap<String, Long>();
        for (String name : resourceTypeMap.keySet()) {
            ResourceLimitDomain resourceLimitDomain = resourceLimitDomainService.findByDomainAndResourceCount(departmentResponse.getDomainId(),
                ResourceLimitDomain.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            ResourceLimitDepartment resourceLimitDepartment = resourceLimitDepartmentRepo.findByDepartmentAndResourceType(id,
                ResourceLimitDepartment.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceLimitDepartment != null) {
                if (resourceLimitDomain.getMax() == -1) {
                    resourceMap.put(resourceTypeMap.get(name), -1L);
                } else {
                    resourceMap.put(resourceTypeMap.get(name), EmptytoLong(resourceLimitDepartment.getMax()) + (EmptytoLong(resourceLimitDomain.getMax()) - EmptytoLong(resourceLimitDomain.getUsedLimit())));
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
