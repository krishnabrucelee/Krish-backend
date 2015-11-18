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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.repository.jpa.ResourceLimitProjectRepository;
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
public class ResourceLimitProjectServiceImpl implements ResourceLimitProjectService {

       /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourceLimitDomainServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** ResourceLimitRepository repository reference. */
    @Autowired
    private ResourceLimitProjectRepository resourceLimitRepo;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackResourceLimitService csResourceService;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    @Override
    public ResourceLimitProject save(ResourceLimitProject resource) throws Exception {
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
    public ResourceLimitProject update(ResourceLimitProject resource) throws Exception {
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
    public void delete(ResourceLimitProject resource) throws Exception {
        resourceLimitRepo.delete(resource);
    }

    @Override
    public void delete(Long id) throws Exception {
        resourceLimitRepo.delete(id);
    }

    @Override
    public ResourceLimitProject find(Long id) throws Exception {
        ResourceLimitProject resourceLimit = resourceLimitRepo.findOne(id);

         LOGGER.debug("Sample Debug Message");
         LOGGER.trace("Sample Trace Message");

         if (resourceLimit == null) {
             throw new EntityNotFoundException("ResourceLimit.not.found");
         }
         return resourceLimit;
    }

    @Override
    public Page<ResourceLimitProject> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return resourceLimitRepo.findAllByActive(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<ResourceLimitProject> findAll() throws Exception {
        return (List<ResourceLimitProject>) resourceLimitRepo.findAll();
    }

    @Override
    public List<ResourceLimitProject> findAllFromCSServerProject(String projectId) throws Exception {
        List<ResourceLimitProject> resourceList = new ArrayList<ResourceLimitProject>();
        HashMap<String, String> resourceMap = new HashMap<String, String>();
        resourceMap.put("projectid", projectId);
        // 1. Get the list of ResourceLimit from CS server using CS connector
        String response = csResourceService.listResourceLimits("json", resourceMap);
        JSONArray resourceListJSON = new JSONObject(response).getJSONObject("listresourcelimitsresponse")
                .getJSONArray("resourcelimit");

        // 2. Iterate the json list, convert the single json entity to
        // Resource limit
        for (int i = 0, size = resourceListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to StorageOffering entity
            // and Add
            // the converted Resource limit entity to list
            resourceList.add(ResourceLimitProject.convert(resourceListJSON.getJSONObject(i), entity));
        }
        return resourceList;
   }

}
