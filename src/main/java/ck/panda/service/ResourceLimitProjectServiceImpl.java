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
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.ResourceLimitDomain.ResourceType;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLimitDomainServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** ResourceLimitRepository repository reference. */
    @Autowired
    private ResourceLimitProjectRepository resourceLimitProjectRepo;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackResourceLimitService csResourceLimitService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Message source attribute. */
    @Autowired
    private MessageSource messageSource;

    /** Resource limit domain service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    @Override
    public ResourceLimitProject save(ResourceLimitProject resource) throws Exception {
        if (resource.getIsSyncFlag()) {

            Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
            errors = validator.validateEntity(resource, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                // createVolume(resource, errors);
                return resourceLimitProjectRepo.save(resource);
            }
        } else {
            return resourceLimitProjectRepo.save(resource);
        }
    }

    @Override
    public ResourceLimitProject update(ResourceLimitProject resource) throws Exception {
        if (resource.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
            errors = validator.validateEntity(resource, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                // updateResource(resource, errors);
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
    public HashMap<String, String> optional(ResourceLimitProject resource) {
        HashMap<String, String> optional = new HashMap<String, String>();

        if (resource.getProject() != null) {
            optional.put("projectid", resource.getProject().getUuid());
        }

        if (resource.getMax() != null) {
            optional.put("max", resource.getMax().toString());
        }
        return optional;
    }
    @Override
    public List<ResourceLimitProject> findAllFromCSServerProject(String projectId) throws Exception {
        List<ResourceLimitProject> resourceList = new ArrayList<ResourceLimitProject>();
        HashMap<String, String> resourceMap = new HashMap<String, String>();
        resourceMap.put("projectid", projectId);
        // 1. Get the list of ResourceLimit from CS server using CS connector
        String response = csResourceLimitService.listResourceLimits("json", resourceMap);
        JSONArray resourceListJSON = new JSONObject(response).getJSONObject("listresourcelimitsresponse")
                .getJSONArray("resourcelimit");
        // 2. Iterate the json list, convert the single json entity to
        // Resource limit
        for (int i = 0, size = resourceListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to StorageOffering entity
            // and Add the converted Resource limit entity to list
            ResourceLimitProject resource = ResourceLimitProject.convert(resourceListJSON.getJSONObject(i));
            resource.setProjectId(convertEntityService.getProject(resource.getTransProjectId()).getId());
            resource.setUniqueSeperator(resource.getTransProjectId()+"-"+ResourceType.values()[(resource.getTransResourceType())]);
            resourceList.add(resource);
        }
        return resourceList;
    }

    /**
     * updating resource limits for project.
     *
     * @param resource resource of project.
     * @throws Exception error
     */
    private void updateResourceProject(ResourceLimitProject resource) throws Exception {
        config.setServer(1L);
        String resourceLimits = csResourceLimitService.updateResourceLimit(resource.getResourceType().ordinal(), "json",
                optional(resource));
        LOGGER.info("Resource limit project update response " + resourceLimits);
        JSONObject resourceLimitsResponse = new JSONObject(resourceLimits).getJSONObject("updateresourcelimitresponse")
                .getJSONObject("resourcelimit");
        if (resourceLimitsResponse.has("errorcode")) {
            LOGGER.debug("============= ERROR IN RESOURCE PROJECT =============");
        } else {
            resource.setDomainId(resource.getDomain().getId());
            resource.setResourceType(resource.getResourceType());
            resource.setMax(resource.getMax());
        }
    }

    @Override
    public List<ResourceLimitProject> createResourceLimits(List<ResourceLimitProject> resourceLimits) throws Exception {

        Errors errors = this.validateResourceLimit(resourceLimits);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            this.deleteResourceLimitByProject(resourceLimits.get(0).getProjectId());
            for (ResourceLimitProject resource : resourceLimits) {
                resource.setIsActive(true);
                updateResourceProject(resource);
                resourceLimitProjectRepo.save(resource);
            }
        }
        return (List<ResourceLimitProject>) resourceLimitProjectRepo.findAll();
    }

    /**
     * Delete resource limit by project.
     *
     * @param projectId project.
     */
    private void deleteResourceLimitByProject(Long projectId) {
        List<ResourceLimitProject> resourceLimits = resourceLimitProjectRepo.findAllByProjectIdAndIsActive(projectId, true);
        for(ResourceLimitProject resource: resourceLimits) {
            resourceLimitProjectRepo.delete(resource);
        }
    }

    /**
     * Validating resource limit based on domain resource limits.
     *
     * @param resourceLimits resource limits
     * @return if error with resource.
     * @throws Exception error.
     */
    private Errors validateResourceLimit(List<ResourceLimitProject> resourceLimits) throws Exception {
        Errors errors = new Errors(messageSource);
        for (ResourceLimitProject resourceLimit : resourceLimits) {
            // Step1: Find max from domain with specific resource type.
            ResourceLimitDepartment projectLimit = resourceLimitDepartmentService.findByDepartmentAndResourceType(
                    resourceLimit.getDepartmentId(),
                    ResourceLimitDepartment.ResourceType.valueOf(resourceLimit.getResourceType().name()), true);
            // Step2: Find resource count from department for spcific domain and
            // resource type
            Long count = findByResourceCountByProjectAndResourceType(resourceLimit.getDepartmentId(),
                    resourceLimit.getResourceType(), resourceLimit.getProjectId(), true);
            Long totalCount = resourceLimit.getMax() + count;
            // if(step1 < step2)
            if (projectLimit.getMax() < totalCount) {
                errors.addFieldError(resourceLimit.getResourceType().toString(),
                        resourceLimit.getResourceType().toString() + "Resource limit exceed");
            }
        }
        return errors;
    }

    @Override
    public Long findByResourceCountByProjectAndResourceType(Long departmentId,
            ResourceLimitProject.ResourceType resourceType, Long projectId, Boolean isActive) throws Exception {
        return resourceLimitProjectRepo.findByResourceCountByProjectAndResourceType(departmentId, resourceType, projectId, isActive);
    }

    @Override
    public List<ResourceLimitProject> findAllByProjectIdAndIsActive(Long projectId, Boolean isActive) {
        return (List<ResourceLimitProject>) resourceLimitProjectRepo.findAllByProjectIdAndIsActive(projectId, isActive);
    }
}
