package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.SupportedNetwork;
import ck.panda.domain.entity.NetworkOffering.Status;
import ck.panda.domain.repository.jpa.NetworkOfferingRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNetworkOfferingService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for NetworkOffering entity.
 *
 */
@Service
public class NetworkOfferingServiceImpl implements NetworkOfferingService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkOfferingServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** NetworkOffering repository reference. */
    @Autowired
    private NetworkOfferingRepository networkRepo;

    /** CloudStack Network Offering service for connectivity with cloudstack. */
    @Autowired
    private CloudStackNetworkOfferingService csNetworkOfferingService;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Supported network cloudstack service reference. */
    @Autowired
    private SupportedNetworkService supportedNetworkService;

    /** Constant for cloudStack networkOffering list response. */
    private static final String CS_LIST_NETWORK_OFFERING_RESPONSE = "listnetworkofferingsresponse";

    /** Constant for cloudStack networkOffering. */
    private static final String CS_NETWORK_OFFERING = "networkoffering";

    /** Constant for list VPC service. */
    public static final String CS_VPC_SERVICE = "service";

    @Override
    public NetworkOffering save(NetworkOffering network) throws Exception {
        networkRepo.save(network);
        List<SupportedNetwork> supportedNetworkList = new ArrayList<SupportedNetwork>();
        if (network.getTransServiceList() != null) {
            for (int i = 0; i < network.getTransServiceList().size(); i++) {
                SupportedNetwork supportedNetwork = supportedNetworkService.findByName(network.getTransServiceList().get(i));
                if (supportedNetwork != null) {
                    supportedNetworkList.add(supportedNetwork);
                    network.setSupportedNetworkList(supportedNetworkList);
                }
            }
        }
        return networkRepo.save(network);
    }

    @Override
    public NetworkOffering update(NetworkOffering network) throws Exception {
        networkRepo.save(network);
        List<SupportedNetwork> supportedNetworkList = new ArrayList<SupportedNetwork>();
        if (network.getTransServiceList() != null) {
            for (int i = 0; i < network.getTransServiceList().size(); i++) {
                SupportedNetwork supportedNetwork = supportedNetworkService.findByName(network.getTransServiceList().get(i));
                if (supportedNetwork != null) {
                    supportedNetworkList.add(supportedNetwork);
                    network.setSupportedNetworkList(supportedNetworkList);
                }
            }
        }
        return networkRepo.save(network);
    }

    @Override
    public void delete(NetworkOffering id) throws Exception {
        networkRepo.delete(id);
    }

    @Override
    public void delete(Long id) throws Exception {
        networkRepo.delete(id);
    }

    @Override
    public NetworkOffering find(Long id) throws Exception {
        return networkRepo.findOne(id);
    }

    @Override
    public Page<NetworkOffering> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return networkRepo.findAllByIsolated(pagingAndSorting.toPageRequest(), "Isolated");
    }

    @Override
    public List<NetworkOffering> findAll() throws Exception {
        return (List<NetworkOffering>) networkRepo.findAll();
    }

    @Override
    public List<NetworkOffering> findAllFromCSServer() throws Exception {

        List<NetworkOffering> networkOfferingList = new ArrayList<NetworkOffering>();
        HashMap<String, String> networkOfferingMap = new HashMap<String, String>();
        configServer.setServer(1L);
        // 1. Get the list of networkOffering from CS server using CS connector
        String response = csNetworkOfferingService.listNetworkOfferings(CloudStackConstants.JSON, networkOfferingMap);
        JSONArray networkOfferingListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_NETWORK_OFFERING_RESPONSE);
        if (responseObject.has(CS_NETWORK_OFFERING)) {
            networkOfferingListJSON = responseObject.getJSONArray(CS_NETWORK_OFFERING);
            // 2. Iterate the json list, convert the single json entity to
            // domain
            for (int i = 0, size = networkOfferingListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity and
                // Add
                // the converted networkOffering entity to list
                NetworkOffering networkOffering = NetworkOffering.convert(networkOfferingListJSON.getJSONObject(i));
                List<String> serviceList = new ArrayList<String>();
                if (networkOfferingListJSON.getJSONObject(i).has(CS_VPC_SERVICE)) {
                    for (int j = 0; j < networkOfferingListJSON.getJSONObject(i).getJSONArray(CS_VPC_SERVICE).length(); j++) {
                        JSONObject serviceResponseObject = (JSONObject) networkOfferingListJSON.getJSONObject(i).getJSONArray(CS_VPC_SERVICE).get(j);
                        serviceList.add(serviceResponseObject.getString(CloudStackConstants.CS_NAME));
                    }
                }
                networkOffering.setTransServiceList(serviceList);
                networkOfferingList.add(networkOffering);
            }
        }
        return networkOfferingList;
    }

    @Override
    public NetworkOffering findByUUID(String uuid) throws Exception {
        return networkRepo.findByUUID(uuid);
    }

    @Override
    public List<NetworkOffering> findByIsolatedAndRequired(String csIsolated, String csRequired) throws Exception {
        return networkRepo.findByIsolatedAndRequired(csIsolated, csRequired);
    }

    @Override
    public NetworkOffering findById(Long id) throws Exception {
        return networkRepo.findById(id);
    }

    @Override
    public List<NetworkOffering> findVpcList() throws Exception {
        return networkRepo.findVpcList(true, Status.ENABLED);
    }

}
