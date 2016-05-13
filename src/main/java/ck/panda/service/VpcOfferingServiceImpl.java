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
import ck.panda.domain.entity.SupportedNetwork;
import ck.panda.domain.entity.VpcOffering;
import ck.panda.domain.repository.jpa.VpcOfferingRepository;
import ck.panda.util.CloudStackVPCService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * VPC offering service implementation used to get list of VPC offering and save the VPC offering from cloudstack server.
 *
 */
@Service
public class VpcOfferingServiceImpl implements VpcOfferingService {

    /** VPC offering repository reference. */
    @Autowired
    private VpcOfferingRepository vpcOfferingRepo;

    /** VPC offering cloudstack service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVPCService;

    /** Supported network cloudstack service reference. */
    @Autowired
    private SupportedNetworkService supportedNetworkService;

    /** Constant for list VPC offering response. */
    public static final String CS_LIST_VPC_OFFERING_RESOPNSE = "listvpcofferingsresponse";

    /** Constant for list VPC offering. */
    public static final String CS_VPC_OFFERING = "vpcoffering";

    /** Constant for list VPC service. */
    public static final String CS_VPC_SERVICE = "service";

    @Override
    public VpcOffering save(VpcOffering vpcOffering) throws Exception {
        vpcOfferingRepo.save(vpcOffering);
        List<SupportedNetwork> supportedNetworkList = new ArrayList<SupportedNetwork>();
        if (vpcOffering.getTransServiceList() != null) {
            for (int i = 0; i < vpcOffering.getTransServiceList().size(); i++) {
                SupportedNetwork supportedNetwork = supportedNetworkService.findByName(vpcOffering.getTransServiceList().get(i));
                if (supportedNetwork != null) {
                    supportedNetworkList.add(supportedNetwork);
                    vpcOffering.setSupportedNetworkList(supportedNetworkList);
                }
            }
        }
        return vpcOfferingRepo.save(vpcOffering);
    }

    @Override
    public VpcOffering update(VpcOffering vpcOffering) throws Exception {
        vpcOfferingRepo.save(vpcOffering);
        List<SupportedNetwork> supportedNetworkList = new ArrayList<SupportedNetwork>();
        if (vpcOffering.getTransServiceList() != null) {
            for (int i = 0; i < vpcOffering.getTransServiceList().size(); i++) {
                SupportedNetwork supportedNetwork = supportedNetworkService.findByName(vpcOffering.getTransServiceList().get(i));
                if (supportedNetwork != null) {
                    supportedNetworkList.add(supportedNetwork);
                    vpcOffering.setSupportedNetworkList(supportedNetworkList);
                }
            }
        }
        return vpcOfferingRepo.save(vpcOffering);
    }

    @Override
    public void delete(VpcOffering vpcOffering) throws Exception {
        vpcOfferingRepo.delete(vpcOffering);
    }

    @Override
    public void delete(Long id) throws Exception {
        vpcOfferingRepo.delete(id);
    }

    @Override
    public VpcOffering find(Long id) throws Exception {
        return vpcOfferingRepo.findOne(id);
    }

    @Override
    public Page<VpcOffering> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return vpcOfferingRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VpcOffering> findAll() throws Exception {
        return (List<VpcOffering>) vpcOfferingRepo.findAll();
    }

    @Override
    public List<VpcOffering> findAllFromCSServer() throws Exception {

        List<VpcOffering> vpcOfferingList = new ArrayList<VpcOffering>();
        HashMap<String, String> vpcOfferingMap = new HashMap<String, String>();
        JSONArray vpcOfferingListJSON = null;
        // 1. Get the list of VPC offering from CS server using CS connector
        String response = cloudStackVPCService.listVPCOfferings(vpcOfferingMap, CloudStackConstants.JSON);
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_VPC_OFFERING_RESOPNSE);
        if (responseObject.has(CS_VPC_OFFERING)) {
            vpcOfferingListJSON = responseObject.getJSONArray(CS_VPC_OFFERING);
            // 2. Iterate the json list, convert the single json entity to VPC offering
            for (int i = 0, size = vpcOfferingListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to VPC offering entity and Add
                // the converted VPC offering entity to list
                VpcOffering vpcOffering = VpcOffering.convert(vpcOfferingListJSON.getJSONObject(i));
                List<String> serviceList = new ArrayList<String>();
                if (vpcOfferingListJSON.getJSONObject(i).has(CS_VPC_SERVICE)) {
                    for (int j = 0; j < vpcOfferingListJSON.getJSONObject(i).getJSONArray(CS_VPC_SERVICE).length(); j++) {
                        JSONObject serviceResponseObject = (JSONObject) vpcOfferingListJSON.getJSONObject(i).getJSONArray(CS_VPC_SERVICE).get(j);
                        serviceList.add(serviceResponseObject.getString(CloudStackConstants.CS_NAME));
                    }
                }
                vpcOffering.setTransServiceList(serviceList);
                vpcOfferingList.add(vpcOffering);
            }
        }
        return vpcOfferingList;
    }

    @Override
    public VpcOffering findByUUID(String uuid) throws Exception {
        return vpcOfferingRepo.findByUUID(uuid);
    }



}
