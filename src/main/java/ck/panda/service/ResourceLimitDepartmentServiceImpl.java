package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.repository.jpa.ResourceLimitDepartmentRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackResourceLimitService;
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

    /** ResourceLimitRepository repository reference. */
    @Autowired
    private ResourceLimitDepartmentRepository resourceLimitRepo;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackResourceLimitService csResourceService;

    /** List the domains in cloudstack server. */
    @Autowired
    private DomainService domainService;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    @Override
    public ResourceLimitDepartment save(ResourceLimitDepartment resource) throws Exception {
         if (resource.getIsSyncFlag()) {

             Errors errors = validator.rejectIfNullEntity("resourcelimits", resource);
             errors = validator.validateEntity(resource, errors);

             if (errors.hasErrors()) {
                 throw new ApplicationException(errors);
             } else {
                 //createVolume(resource, errors);
                 return resourceLimitRepo.save(resource);
             }
         } else {
             return resourceLimitRepo.save(resource);
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
                 return resourceLimitRepo.save(resource);
             }
         } else {
             return resourceLimitRepo.save(resource);
         }
    }

    @Override
    public void delete(ResourceLimitDepartment resource) throws Exception {
        resourceLimitRepo.delete(resource);
    }

    @Override
    public void delete(Long id) throws Exception {
        resourceLimitRepo.delete(id);
    }

    @Override
    public ResourceLimitDepartment find(Long id) throws Exception {
        ResourceLimitDepartment resourceLimit = resourceLimitRepo.findOne(id);

         LOGGER.debug("Sample Debug Message");
         LOGGER.trace("Sample Trace Message");

         if (resourceLimit == null) {
             throw new EntityNotFoundException("ResourceLimit.not.found");
         }
         return resourceLimit;
    }

    @Override
    public Page<ResourceLimitDepartment> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return resourceLimitRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ResourceLimitDepartment> findAll() throws Exception {
        return (List<ResourceLimitDepartment>) resourceLimitRepo.findAll();
    }

    @Override
    public List<ResourceLimitDepartment> findAllFromCSServerDepartment(Long domainId, String department)
            throws Exception {
        List<ResourceLimitDepartment> resourceList = new ArrayList<ResourceLimitDepartment>();
        HashMap<String, String> resourceMap = new HashMap<String, String>();
        resourceMap.put("domainid", domainService.find(domainId).getUuid());
        resourceMap.put("account", department);

        // 1. Get the list of ResourceLimit from CS server using CS connector
        String response = csResourceService.listResourceLimits("json", resourceMap);
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

}
