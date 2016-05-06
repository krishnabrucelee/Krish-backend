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
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.AffinityGroupType;
import ck.panda.domain.repository.jpa.AffinityGroupTypeRepository;
import ck.panda.util.CloudStackAffinityGroupService;
import ck.panda.util.domain.vo.PagingAndSorting;

/** Affinity group type service implementation class. */
@Service
public class AffinityGroupTypeServiceImpl implements AffinityGroupTypeService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AffinityGroupTypeServiceImpl.class);

    /** Affinity group type repository reference. */
    @Autowired
    private AffinityGroupTypeRepository affinityGroupTypeRepository;

    /** CloudStack reference for affinity group type. */
    @Autowired
    private CloudStackAffinityGroupService cloudStackAffinityGroupService;

    /** Constant for affinity group type response from cloudStack. */
    public static final String CS_LIST_AFFINITY_GROUP_TYPE_RESPONSE = "listaffinitygrouptypesresponse";

    /** Constant for affinity group type from cloudStack. */
    public static final String CS_AFFINITY_GROUP_TYPE = "affinityGroupType";

    @Override
    public AffinityGroupType save(AffinityGroupType affinityGroupType) throws Exception {
        affinityGroupType.setIsActive(true);
        return affinityGroupTypeRepository.save(affinityGroupType);
    }

    @Override
    public AffinityGroupType update(AffinityGroupType affinityGroupType) throws Exception {
        return affinityGroupTypeRepository.save(affinityGroupType);
    }

    @Override
    public void delete(AffinityGroupType affinityGroupType) throws Exception {
        affinityGroupTypeRepository.delete(affinityGroupType);
    }

    @Override
    public void delete(Long id) throws Exception {
        affinityGroupTypeRepository.delete(id);
    }

    @Override
    public AffinityGroupType find(Long id) throws Exception {
        return affinityGroupTypeRepository.findOne(id);
    }

    @Override
    public Page<AffinityGroupType> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return affinityGroupTypeRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<AffinityGroupType> findAll() throws Exception {
        return (List<AffinityGroupType>) affinityGroupTypeRepository.findAll();
    }

    @Override
    public List<AffinityGroupType> findAllFromCSServer() throws Exception {
        List<AffinityGroupType> affinityGroupTypeList = new ArrayList<AffinityGroupType>();
        HashMap<String, String> affinityGroupTypeMap = new HashMap<String, String>();
        JSONArray affinityGroupTypeListJSON = null;
        // 1. Get the list of affinity group type from CS server using CS connector
        String response = cloudStackAffinityGroupService.listAffinityGroupTypes(CloudStackConstants.JSON, affinityGroupTypeMap);
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_AFFINITY_GROUP_TYPE_RESPONSE);
        if (responseObject.has(CS_AFFINITY_GROUP_TYPE)) {
            affinityGroupTypeListJSON = responseObject.getJSONArray(CS_AFFINITY_GROUP_TYPE);
            // 2. Iterate the json list, convert the single json entity to affinity group type
            for (int i = 0, size = affinityGroupTypeListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to affinity group type entity and Add
                // the converted affinity group type entity to list
                if (affinityGroupTypeListJSON.getJSONObject(i).has(CloudStackConstants.CS_TYPE)) {
                    AffinityGroupType affinityGroupType = new AffinityGroupType();
                    affinityGroupType.setIsActive(true);
                    affinityGroupType.setType(affinityGroupTypeListJSON.getJSONObject(i).getString(CloudStackConstants.CS_TYPE));
                    affinityGroupTypeList.add(affinityGroupType);
                }
            }
        }
        return affinityGroupTypeList;
    }

    @Override
    public AffinityGroupType findByType(String type) throws Exception {
        return affinityGroupTypeRepository.findByType(type);
    }

}
