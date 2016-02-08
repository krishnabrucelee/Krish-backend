package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Template;
import ck.panda.domain.repository.jpa.OsCategoryRepository;
import ck.panda.util.CloudStackOSService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for OS category entity.
 *
 */
@Service
public class OsCategoryServiceImpl implements OsCategoryService {

    /** OS category repository reference. */
    @Autowired
    private OsCategoryRepository osCategoryRepo;

    /** Template service reference. */
    @Autowired
    private TemplateService templateService;

    /** CloudStack OS categories service for connectivity with cloudstack. */
    @Autowired
    private CloudStackOSService osCategoryService;

    /** Cloud stack list OS category response. */
    public static final String LIST_OS_CATEGORIES_RESPONSE = "listoscategoriesresponse";

    /** OS category response object. */
    public static final String OS_CATEGORY = "oscategory";

    @Override
    public OsCategory save(OsCategory oscategory) throws Exception {
        return osCategoryRepo.save(oscategory);
    }

    @Override
    public OsCategory update(OsCategory oscategory) throws Exception {
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
        return osCategoryRepo.findAll(pagingAndSorting.toPageRequest());
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
    public List<OsCategory> findByOsCategoryFilters(String type)  throws Exception {
         List<OsCategory> osCategorys = (List<OsCategory>) osCategoryRepo.findAll();
         List<OsCategory> osList = new ArrayList<OsCategory>();
         if (!osCategorys.isEmpty()) {
             for (OsCategory osCategory : osCategorys) {
                 List<Template> templates = templateService.findByTemplateCategory(osCategory, type);
                 if (templates.size() > 0) {
                     osList.add(osCategory);
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
        String response = osCategoryService.listOsCategories(CloudStackConstants.JSON, osCategoryMap);
        JSONArray osCategoryListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(LIST_OS_CATEGORIES_RESPONSE);
        if (responseObject.has(OS_CATEGORY)) {
            osCategoryListJSON = responseObject.getJSONArray(OS_CATEGORY);
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
