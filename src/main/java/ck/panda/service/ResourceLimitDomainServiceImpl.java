/**
 *
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
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitDomain.ResourceType;
import ck.panda.domain.repository.jpa.ResourceLimitDomainRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackResourceLimitService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Resource Limit Domain Service Implementation.
 */
@Service
public class ResourceLimitDomainServiceImpl implements ResourceLimitDomainService {

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
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** ResourceLimitRepository repository reference. */
    @Autowired
    private ResourceLimitDomainRepository resourceLimitDomainRepo;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackResourceLimitService csResourceLimitService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Cloud stack sync service. */
    @Autowired
    private SyncService syncService;

    @Override
    public ResourceLimitDomain save(ResourceLimitDomain resource) throws Exception {
        if (resource.getIsSyncFlag()) {

            Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
            errors = validator.validateEntity(resource, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                return resourceLimitDomainRepo.save(resource);
            }
        } else {
            return resourceLimitDomainRepo.save(resource);
        }
    }

    @Override
    public ResourceLimitDomain update(ResourceLimitDomain resource) throws Exception {
        if (resource.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
            errors = validator.validateEntity(resource, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                return resourceLimitDomainRepo.save(resource);
            }
        } else {
            return resourceLimitDomainRepo.save(resource);
        }
    }

    @Override
    public void delete(ResourceLimitDomain resource) throws Exception {
        resourceLimitDomainRepo.delete(resource);
    }

    @Override
    public void delete(Long id) throws Exception {
        resourceLimitDomainRepo.delete(id);
    }

    @Override
    public ResourceLimitDomain find(Long id) throws Exception {
        ResourceLimitDomain resourceLimit = resourceLimitDomainRepo.findOne(id);

        LOGGER.debug("Sample Debug Message");
        LOGGER.trace("Sample Trace Message");

        if (resourceLimit == null) {
            throw new EntityNotFoundException("ResourceLimit.not.found");
        }
        return resourceLimit;
    }

    @Override
    public Page<ResourceLimitDomain> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return resourceLimitDomainRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ResourceLimitDomain> findAll() throws Exception {
        return (List<ResourceLimitDomain>) resourceLimitDomainRepo.findAll();
    }

    /**
     * To set optional values by validating null and empty parameters.
     *
     * @param resource optional resource limits values
     * @return optional values
     * @throws Exception if error occurs
     */
    public HashMap<String, String> optional(ResourceLimitDomain resource) throws Exception {
        HashMap<String, String> optional = new HashMap<String, String>();

        if (resource.getDomainId() != null) {
            optional.put("domainid", convertEntityService.getDomainUuidById(resource.getDomainId()));
        }

        if (resource.getMax() != null) {
            optional.put("max", resource.getMax().toString());
        }
        return optional;
    }

    @Override
    public List<ResourceLimitDomain> findAllFromCSServerDomain(String domainId) throws Exception {
        List<ResourceLimitDomain> resourceList = new ArrayList<ResourceLimitDomain>();
        HashMap<String, String> resourceMap = new HashMap<String, String>();
        resourceMap.put("domainid", domainId);
        // 1. Get the list of ResourceLimit from CS server using CS connector
        String response = csResourceLimitService.listResourceLimits("json", resourceMap);
        JSONArray resourceListJSON = new JSONObject(response).getJSONObject("listresourcelimitsresponse")
                .getJSONArray("resourcelimit");

        // 2. Iterate the json list, convert the single json entity to
        // Resource limit
        for (int i = 0, size = resourceListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to StorageOffering entity
            // and Add
            // the converted Resource limit entity to list
            ResourceLimitDomain resource = ResourceLimitDomain.convert(resourceListJSON.getJSONObject(i));
            resource.setDomainId(convertEntityService.getDomainId(resource.getTransDomainId()));
            resource.setUniqueSeperator(resource.getTransDomainId() + resource.getResourceType().toString());
            resourceList.add(resource);
        }
        return resourceList;
    }

    @Override
    @PreAuthorize("hasPermission(#resourceLimits.get(0).getIsSyncFlag(), 'DOMAIN_QUOTA')")
    public List<ResourceLimitDomain> createResourceLimits(List<ResourceLimitDomain> resourceLimits) throws Exception {
		if (resourceLimits.get(0).getIsSyncFlag()) {
			Errors errors = this.validateResourceLimit(resourceLimits);
			if (errors.hasErrors()) {
				throw new ApplicationException(errors);
			} else {
				for (ResourceLimitDomain resource : resourceLimits) {
					String isError = updateResourceDomain(resource, errors);
					if (isError != null) {
						throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, isError);
					} else {
						if (resource.getId() != null) {
							ResourceLimitDomain resourceData = resourceLimitDomainRepo.findOne(resource.getId());
							resourceData.setMax(resource.getMax());
							resourceData.setIsActive(true);
							resourceData.setDomainId(resource.getDomainId());
							resourceLimitDomainRepo.save(resourceData);
						} else {
							updateResourceDomain(resource, errors);
							resource.setIsActive(true);
							resourceLimitDomainRepo.save(resource);
						}
					}
				}
			}
			return (List<ResourceLimitDomain>) resourceLimitDomainRepo
					.findAllByDomainIdAndIsActive(resourceLimits.get(0).getDomainId(), true);
		} else {
			return (List<ResourceLimitDomain>) resourceLimitDomainRepo
					.findAllByDomainIdAndIsActive(resourceLimits.get(0).getDomainId(), true);
		}
    }

    /**
     * Delete Resource limit.
     *
     * @param domainId domain id.
     */
    public void deleteResourceLimitByDomain(Long domainId) {
        List<ResourceLimitDomain> resourceLimits = resourceLimitDomainRepo.findAllByDomainIdAndIsActive(domainId, true);
        for (ResourceLimitDomain resource : resourceLimits) {
            resourceLimitDomainRepo.delete(resource);
        }
    }

    /**
     * Validating resource limit based on domain resource limits.
     *
     * @param resourceLimits resource limits
     * @return if error with resource.
     * @throws Exception error.
     */
	private Errors validateResourceLimit(List<ResourceLimitDomain> resourceLimits) throws Exception {
		Errors errors = new Errors(messageSource);
		for (ResourceLimitDomain resourceLimit : resourceLimits) {
			if (!resourceLimit.getResourceType().equals(ResourceLimitDomain.ResourceType.Project)) {
				Long departmentResourceCount = resourceLimitDepartmentService
						.findByResourceCountByDepartmentAndResourceType(resourceLimit.getDomainId(),
								ResourceLimitDepartment.ResourceType.valueOf(resourceLimit.getResourceType().name()),
								0L, true);
				ResourceLimitDomain resourceLimitDomain = resourceLimitDomainRepo.findByDomainAndResourceType(
						resourceLimit.getDomainId(),
						ResourceLimitDomain.ResourceType.valueOf(resourceLimit.getResourceType().name()), true);
				Long totalCount = 0L;
				if (resourceLimit.getMax() == -1) {
					totalCount = EmptytoLong(resourceLimit.getMax());
				} else {
					totalCount = EmptytoLong(resourceLimit.getMax());
				}
			}
		}
		return errors;
	}

    /**
     * updating resource limits for Domain.
     *
     * @param resource resource of Domain.
     * @throws Exception error
     */
    private String updateResourceDomain(ResourceLimitDomain resource, Errors errors) throws Exception {
        String errMessage = null;
        config.setServer(1L);
		if (!resource.getResourceType().equals(ResourceLimitDomain.ResourceType.Project)) {
			String resourceLimits = csResourceLimitService.updateResourceLimit(resource.getResourceType().ordinal(),
					"json", optional(resource));
			LOGGER.info("Resource limit update response " + resourceLimits);
			JSONObject resourceLimitsResponse = new JSONObject(resourceLimits)
					.getJSONObject("updateresourcelimitresponse");
			if (resourceLimitsResponse.has("errorcode")) {
				errMessage = resourceLimitsResponse.getString("errortext");
			}
		}
        return errMessage;
    }

    @Override
    public List<ResourceLimitDomain> findAllByDomainIdAndIsActive(Long domainId, Boolean isActive) throws Exception {
        return (List<ResourceLimitDomain>) resourceLimitDomainRepo.findAllByDomainIdAndIsActive(domainId, isActive);
    }

    @Override
    public ResourceLimitDomain findByDomainAndResourceType(Long domainId, ResourceType resourceType,
            Boolean isActive) throws Exception {
        return resourceLimitDomainRepo.findByDomainAndResourceType(domainId, resourceType, isActive);
    }

    @Override
    public ResourceLimitDomain findAllByResourceType(Long domainById) {
        return resourceLimitDomainRepo.deleteByDomainAndIsActive(domainById, true);
    }

    @Override
    public ResourceLimitDomain findByDomainAndResourceCount(Long domainId, ResourceType resource, Boolean isActive) {
        return resourceLimitDomainRepo.findByDomainAndResourceCount(domainId, resource, isActive);
    }

    @Override
    public List<ResourceLimitDomain> findCurrentLoginDomain() throws NumberFormatException, Exception {
        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        return (List<ResourceLimitDomain>) resourceLimitDomainRepo.findAllByDomainIdAndIsActive(domain.getId(), true);
    }

    @Override
    public List<ResourceLimitDomain> findByDomainIdAndResourceType(Long domainId, ResourceType resource, Boolean isActive) throws Exception {
    	return (List<ResourceLimitDomain>) resourceLimitDomainRepo.findAllByDomainIdAndIsActiveAndResourceType(domainId, resource, true);
    }

    @Override
    public void asyncResourceDomain(Long domainId) throws Exception {
        syncService.syncResourceLimitDomain(convertEntityService.getDomainById(domainId));
    }

    @Override
	public HashMap<String, String> getResourceLimitsOfDomain(Long domainId) {
		HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
		HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
		for (String name : resourceTypeMap.keySet()) {
			Long resourceDomainCount = resourceLimitDomainRepo.findTotalCountOfResourceDomain(domainId,
					ResourceLimitDomain.ResourceType.valueOf(resourceTypeMap.get(name)), true);
			if (resourceDomainCount != null) {
				resourceMaxCount.put(resourceTypeMap.get(name), EmptytoLong(resourceDomainCount).toString());
			}
		}
		return resourceMaxCount;
	}

    private Long EmptytoLong(Long value) {
    	if (value == null) {
            return 0L;
        }
        return value;
	}

	@Override
    public HashMap<String, String> getResourceLimitsOfProject(Long domainId) {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, String> resourceMaxCount = new HashMap<String, String>();
        for (String name : resourceTypeMap.keySet()) {
            Long resourceDomainCount = resourceLimitDomainRepo.findTotalCountOfResourceProject(domainId,
                    ResourceLimitDomain.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            if (resourceDomainCount != null) {
                resourceMaxCount.put(resourceTypeMap.get(name), EmptytoLong(resourceDomainCount).toString());
            }
        }
        return resourceMaxCount;
    }

    @Override
    public List<ResourceLimitDomain> findAllByDomainId(Long domainId) throws Exception {
        return (List<ResourceLimitDomain>) resourceLimitDomainRepo.findAllByDomainIdAndIsActive(domainId, true);
    }

    @Override
    public HashMap<String, Long> getSumOfDomainMin(Long id) throws Exception {
        HashMap<String, String> resourceTypeMap = convertEntityService.getResourceTypeValue();
        HashMap<String, Long> resourceMap = new HashMap<String, Long>();
        for (String name : resourceTypeMap.keySet()) {
            ResourceLimitDomain resourceLimitDomain = resourceLimitDomainRepo.findByDomainAndResourceCount(id, ResourceLimitDomain.ResourceType.valueOf(resourceTypeMap.get(name)), true);
            resourceMap.put(resourceTypeMap.get(name), EmptytoLong(resourceLimitDomain.getUsedLimit()));
        }
        return resourceMap;
    }

}
