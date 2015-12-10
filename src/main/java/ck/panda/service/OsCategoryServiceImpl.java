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
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.Status;
import ck.panda.domain.entity.Template.Type;
import ck.panda.domain.repository.jpa.OsCategoryRepository;
import ck.panda.domain.repository.jpa.TemplateRepository;
import ck.panda.util.CloudStackOSService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for OS category entity.
 *
 */
@Service
public class OsCategoryServiceImpl implements OsCategoryService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OsCategoryServiceImpl.class);

    /** OS category repository reference. */
    @Autowired
    private OsCategoryRepository osCategoryRepo;

    /** Template repository reference. */
    @Autowired
    private TemplateRepository templateRepository;

    /** CloudStack os categories service for connectivity with cloudstack. */
    @Autowired
    private CloudStackOSService osCategoryService;

    @Override
    public OsCategory save(OsCategory oscategory) throws Exception {
        LOGGER.debug(oscategory.getUuid());
        return osCategoryRepo.save(oscategory);
    }

    @Override
    public OsCategory update(OsCategory oscategory) throws Exception {
        LOGGER.debug(oscategory.getUuid());
        return osCategoryRepo.save(oscategory);
    }

    @Override
    public void delete(OsCategory oscategory) throws Exception {
        osCategoryRepo.delete(oscategory);
    }

    @Override
    public void delete(Long id) throws Exception {
        osCategoryRepo.delete(id);
    }

    @Override
    public OsCategory find(Long id) throws Exception {
        return osCategoryRepo.findOne(id);
    }

    @Override
    public Page<OsCategory> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

    @Override
    public List<OsCategory> findAll() throws Exception {
        return (List<OsCategory>) osCategoryRepo.findAll();
    }

    @Override
    public OsCategory findbyUUID(String uuid) throws Exception {
        return osCategoryRepo.findByUUID(uuid);
    }

    @Override
    public List<OsCategory> findByOsCategoryFilters() {
        List<OsCategory> osCategory = (List<OsCategory>) osCategoryRepo.findAll();
        List<OsCategory> osList = new ArrayList<OsCategory>();
        if(!osCategory.isEmpty()) {
            for(OsCategory os:osCategory) {
         List<Template> templates = templateRepository.findByOsCategoryFilters(Type.SYSTEM, Status.ACTIVE, os);
         if(templates.size() > 0){
             osList.add(os);
         }
            }
        }
        return osList;
    }

    @Override
    public List<OsCategory> findAllFromCSServer() throws Exception {

        List<OsCategory> osCategoryList = new ArrayList<OsCategory>();
        HashMap<String, String> osCategoryMap = new HashMap<String, String>();
        // 1. Get the list of domains from CS server using CS connector
        String response = osCategoryService.listOsCategories("json", osCategoryMap);
        JSONArray osCategoryListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listoscategoriesresponse");
        if (responseObject.has("oscategory")) {
            osCategoryListJSON = responseObject.getJSONArray("oscategory");
            // 2. Iterate the json list, convert the single json entity to
            // domain
            for (int i = 0, size = osCategoryListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity and
                // Add
                // the converted Domain entity to list
                osCategoryList.add(OsCategory.convert(osCategoryListJSON.getJSONObject(i)));
            }
        }
        return osCategoryList;
    }
}
