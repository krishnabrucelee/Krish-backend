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
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.repository.jpa.ResourceLimitDomainRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackResourceLimitService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.ConvertUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * Resource Limit Service Implementation.
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

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    @Override
    public ResourceLimitDomain save(ResourceLimitDomain resource) throws Exception {
         if (resource.getIsSyncFlag()) {

             Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
             errors = validator.validateEntity(resource, errors);

             if (errors.hasErrors()) {
                 throw new ApplicationException(errors);
             } else {
                 //createVolume(resource, errors);
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
                 //updateResource(resource, errors);
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
     */
    public HashMap<String, String> optional(ResourceLimitDomain resource) {
        HashMap<String, String> optional = new HashMap<String, String>();

        if (resource.getDomainId() != null) {
            optional.put("domainid", resource.getDomain().getUuid());
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
            resourceList.add(ResourceLimitDomain.convert(resourceListJSON.getJSONObject(i), entity));
        }
        return resourceList;
    }

    @Override
    public List<ResourceLimitDomain> createResourceLimits(List<ResourceLimitDomain> resourceLimits) throws Exception {

        Errors errors = this.validateResourceLimit(resourceLimits);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            this.deleteResourceLimitByDomain(resourceLimits.get(0).getDomain().getId());
            for (ResourceLimitDomain resource : resourceLimits) {
                updateResourceDomain(resource);
                resource.setIsActive(true);
                resource.setDomainId(resource.getDomain().getId());
                resourceLimitDomainRepo.save(resource);
            }
        }
        return (List<ResourceLimitDomain>) resourceLimitDomainRepo.findAll();
    }

    private void deleteResourceLimitByDomain(Long domainId) {
        List<ResourceLimitDomain> resourceLimits = resourceLimitDomainRepo.findAllByDomainIdAndIsActive(domainId, true);
        for(ResourceLimitDomain resource: resourceLimits) {
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
            Long departmentResourceCount = resourceLimitDepartmentService.findByResourceCountByDepartmentAndResourceType(resourceLimit.getDomainId(), ResourceLimitDepartment.ResourceType.valueOf(resourceLimit.getResourceType().name()), 0L, true);
            if (resourceLimit.getMax() < departmentResourceCount) {
                errors.addFieldError(resourceLimit.getResourceType().toString(), departmentResourceCount + " " + resourceLimit.getResourceType().toString() + " already allocated to departments of this domain");
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
    private void updateResourceDomain(ResourceLimitDomain resource) throws Exception {
        config.setServer(1L);
        String resourceLimits = csResourceLimitService.updateResourceLimit(resource.getResourceType().ordinal(), "json", optional(resource));
        LOGGER.info("Resource limit update response " + resourceLimits);
        JSONObject resourceLimitsResponse = new JSONObject(resourceLimits).getJSONObject("updateresourcelimitresponse")
                .getJSONObject("resourcelimit");
        if (resourceLimitsResponse.has("errorcode")) {
            System.out.println("=========== +"
                    + "============= ERROR IN RESOURCE DOMAIN");
        }
    }

    @Override
    public List<ResourceLimitDomain> findAllByDomainIdAndIsActive(Long domainId, Boolean isActive) throws Exception {
        return (List<ResourceLimitDomain>) resourceLimitDomainRepo.findAllByDomainIdAndIsActive(domainId, isActive);
    }

    @Override
    public ResourceLimitDomain findByDomainAndResourceType(Long domainId, ResourceLimitDomain.ResourceType resourceType, Boolean isActive) throws Exception {
        return resourceLimitDomainRepo.findByDomainAndResourceType(domainId, resourceType, isActive);
    }

}