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
import org.springframework.transaction.annotation.Transactional;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.repository.jpa.ResourceLimitDepartmentRepository;
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

    /** List the domains in cloudstack server. */
    @Autowired
    private DomainService domainService;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Resource limit domain service reference. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /** Resource limit project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    @Override
    public ResourceLimitDepartment save(ResourceLimitDepartment resource) throws Exception {
         if (resource.getIsSyncFlag()) {

             Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
             errors = validator.validateEntity(resource, errors);

             if (errors.hasErrors()) {
                 throw new ApplicationException(errors);
             } else {
                 //createVolume(resource, errors);
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
                 //updateResource(resource, errors);
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
    public List<ResourceLimitDepartment> findAllFromCSServerDepartment(Long domainId, String department)
            throws Exception {
        List<ResourceLimitDepartment> resourceList = new ArrayList<ResourceLimitDepartment>();
        HashMap<String, String> resourceMap = new HashMap<String, String>();
        resourceMap.put("domainid", domainService.find(domainId).getUuid());
        resourceMap.put("account", department);

        // 1. Get the list of ResourceLimit from CS server using CS connector
        String response = csResourceLimitService.listResourceLimits("json", resourceMap);
        JSONArray resourceListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listresourcelimitsresponse");
        if (responseObject.has("resourcelimit")) {
            resourceListJSON = responseObject.getJSONArray("resourcelimit");
            // 2. Iterate the json list, convert the single json entity to
            // Resource limit
            for (int i = 0, size = resourceListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to StorageOffering
                // entity
                // and Add
                // the converted Resource limit entity to list
                resourceList.add(ResourceLimitDepartment.convert(resourceListJSON.getJSONObject(i), entity));
            }
        }
        return resourceList;
    }

    @Override
    @Transactional
    public List<ResourceLimitDepartment> createResourceLimits(List<ResourceLimitDepartment> resourceLimits)
            throws Exception {
        Errors errors = this.validateResourceLimit(resourceLimits);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            this.deleteResourceLimitByDepartment(resourceLimits.get(0).getDepartment().getId());
            for (ResourceLimitDepartment resource : resourceLimits) {
                updateResourceDepartment(resource);
                resource.setIsActive(true);
                resourceLimitDepartmentRepo.save(resource);
            }
        }
        return (List<ResourceLimitDepartment>) resourceLimitDepartmentRepo.findAll();
    }

    private void deleteResourceLimitByDepartment(Long departmentId) {
        List<ResourceLimitDepartment> resourceLimits = resourceLimitDepartmentRepo.findAllByDepartmentIdAndIsActive(departmentId, true);
        for(ResourceLimitDepartment resource: resourceLimits) {
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
        String resourceLimits = csResourceLimitService.updateResourceLimit(resource.getResourceType().ordinal(), "json", optional(resource));
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
            //Step1: Find max from domain with specific resource type.
            ResourceLimitDomain domainLimit = resourceLimitDomainService.findByDomainAndResourceType(resourceLimit.getDomainId(), ResourceLimitDomain.ResourceType.valueOf(resourceLimit.getResourceType().name()), true);
            //Step2: Find resource count from department for spcific domain and resource type
            Long count = findByResourceCountByDepartmentAndResourceType(resourceLimit.getDomainId(), resourceLimit.getResourceType(),
                    resourceLimit.getDepartmentId(), true);
            Long totalCount = resourceLimit.getMax() + count;
            //if(step1 < step2) {
            if (domainLimit.getMax() < totalCount) {
                errors.addFieldError(resourceLimit.getResourceType().toString(), resourceLimit.getResourceType().toString() + " Resource limit exceed");

            }
            // Comparing with project
            Long projectResourceCount = resourceLimitProjectService.findByResourceCountByProjectAndResourceType(resourceLimit.getDepartmentId(), ResourceLimitProject.ResourceType.valueOf(resourceLimit.getResourceType().name()), 0L, true);
            if (resourceLimit.getMax() < projectResourceCount) {
                errors.addFieldError(resourceLimit.getResourceType().toString(), projectResourceCount + " " + resourceLimit.getResourceType().toString() + " already allocated to projects of this department");
            }
        }
        return errors;
    }

    @Override
    public List<ResourceLimitDepartment> findAllByDepartmentIdAndIsActive(Long departmentId, Boolean isActive) throws Exception {
        return (List<ResourceLimitDepartment>) resourceLimitDepartmentRepo.findAllByDepartmentIdAndIsActive(departmentId, isActive);
    }

    @Override
    public Long findByResourceCountByDepartmentAndResourceType(Long domainId, ResourceLimitDepartment.ResourceType resourceType, Long departmentId, Boolean isActive) throws Exception {
        return resourceLimitDepartmentRepo.findByResourceCountByDepartmentAndResourceType(domainId, resourceType, departmentId, isActive);
    }

    @Override
    public ResourceLimitDepartment findByDepartmentAndResourceType(Long departmentId, ResourceLimitDepartment.ResourceType resourceType,
            Boolean isActive) {
        return resourceLimitDepartmentRepo.findByDepartmentAndResourceType(departmentId, resourceType, isActive);
    }

}
