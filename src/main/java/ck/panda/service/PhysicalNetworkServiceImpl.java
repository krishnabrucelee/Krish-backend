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
import ck.panda.domain.entity.PhysicalNetwork;
import ck.panda.domain.entity.PhysicalNetwork.Status;
import ck.panda.domain.repository.jpa.PhysicalNetworkRepository;
import ck.panda.util.CloudStackVPCService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Physical network service implementation used to get list of physical network
 * and save the physical network from cloudstack server.
 *
 */
@Service
public class PhysicalNetworkServiceImpl implements PhysicalNetworkService {

    /** Physical network repository reference. */
    @Autowired
    private PhysicalNetworkRepository physicalNetworkRepo;

    /** Physical network cloudstack service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVPCService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Constant for list physical network response. */
    public static final String CS_PHYSICAL_NETWORK_RESOPNSE = "listphysicalnetworksresponse";

    /** Constant for list physical network. */
    public static final String CS_PHYSICAL_NETWORK_SERVICE = "physicalnetwork";

    @Override
    public PhysicalNetwork save(PhysicalNetwork physicalNetwork) throws Exception {
        return physicalNetworkRepo.save(physicalNetwork);
    }

    @Override
    public PhysicalNetwork update(PhysicalNetwork physicalNetwork) throws Exception {
        return physicalNetworkRepo.save(physicalNetwork);
    }

    @Override
    public void delete(PhysicalNetwork physicalNetwork) throws Exception {
        physicalNetwork.setStatus(Status.DISABLED);
        physicalNetworkRepo.save(physicalNetwork);
    }

    @Override
    public void delete(Long id) throws Exception {
        physicalNetworkRepo.delete(id);
    }

    @Override
    public PhysicalNetwork find(Long id) throws Exception {
        return physicalNetworkRepo.findOne(id);
    }

    @Override
    public Page<PhysicalNetwork> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return physicalNetworkRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<PhysicalNetwork> findAll() throws Exception {
        return (List<PhysicalNetwork>) physicalNetworkRepo.findAll();
    }

    @Override
    public List<PhysicalNetwork> findAllFromCSServer() throws Exception {

        List<PhysicalNetwork> physicalNetworkList = new ArrayList<PhysicalNetwork>();
        HashMap<String, String> physicalNetworkMap = new HashMap<String, String>();
        JSONArray physicalNetworkListJSON = null;
        // 1. Get the list of physical network from CS server using CS connector
        String response = cloudStackVPCService.listPhysicalNetworks(physicalNetworkMap, CloudStackConstants.JSON);
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_PHYSICAL_NETWORK_RESOPNSE);
        if (responseObject.has(CS_PHYSICAL_NETWORK_SERVICE)) {
            physicalNetworkListJSON = responseObject.getJSONArray(CS_PHYSICAL_NETWORK_SERVICE);
            // 2. Iterate the json list, convert the single json entity to physical network
            for (int i = 0, size = physicalNetworkListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to physical network entity and Add
                // the converted physical network entity to list
                PhysicalNetwork physicalNetwork = PhysicalNetwork.convert(physicalNetworkListJSON.getJSONObject(i));
                physicalNetwork.setZoneId(convertEntityService.getZoneId(physicalNetwork.getTransZone()));
                physicalNetwork.setDomainId(convertEntityService.getDomainId(physicalNetwork.getTransDomain()));
                physicalNetworkList.add(physicalNetwork);

            }
        }
        return physicalNetworkList;
    }

    @Override
    public PhysicalNetwork findByUuid(String uuid) throws Exception {
        return physicalNetworkRepo.findByUuid(uuid);
    }

}
