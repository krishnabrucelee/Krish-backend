package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.OsType;
import ck.panda.domain.repository.jpa.OsTypeRepository;
import ck.panda.util.CloudStackOSService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for OS type entity.
 *
 */
@Service
public class OsTypeServiceImpl implements OsTypeService {

    /** OS type repository reference. */
    @Autowired
    private OsTypeRepository ostyperepository;

    /** Lists types of operating systems in cloudstack server. */
    @Autowired
    private CloudStackOSService osTypeService;

    @Override
    public List<OsType> findAll() throws Exception {
        return (List<OsType>) ostyperepository.findAll();
    }

    @Override
    public OsType save(OsType ostype) throws Exception {
        return ostyperepository.save(ostype);
    }

    @Override
    public OsType update(OsType ostype) throws Exception {
        return ostyperepository.save(ostype);
    }

    @Override
    public void delete(OsType ostype) throws Exception {
        ostyperepository.delete(ostype);
    }

    @Override
    public void delete(Long id) throws Exception {
        ostyperepository.delete(id);
    }

    @Override
    public OsType find(Long id) throws Exception {
        return ostyperepository.findOne(id);
    }

    @Override
    public Page<OsType> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return null;
    }

    @Override
    public List<OsType> findAllFromCSServer() throws Exception {
          List<OsType> osTypeList = new ArrayList<OsType>();
          HashMap<String, String> osTypeMap = new HashMap<String, String>();

          // 1. Get the list of domains from CS server using CS connector
          String response = osTypeService.listOsTypes("json", osTypeMap);

          JSONArray osTypeListJSON = new JSONObject(response).getJSONObject("listostypesresponse")
                  .getJSONArray("ostype");
          // 2. Iterate the json list, convert the single json entity to domain
          for (int i = 0, size = osTypeListJSON.length(); i < size; i++) {
              // 2.1 Call convert by passing JSONObject to ostype entity and Add
              // the converted ostype entity to list
              osTypeList.add(OsType.convert(osTypeListJSON.getJSONObject(i)));
          }
          return osTypeList;
      }
    }
