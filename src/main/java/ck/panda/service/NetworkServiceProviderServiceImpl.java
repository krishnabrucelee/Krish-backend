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
import ck.panda.domain.entity.NetworkServiceProvider;
import ck.panda.domain.entity.NetworkServiceProvider.Status;
import ck.panda.domain.repository.jpa.NetworkServiceProviderRepository;
import ck.panda.util.CloudStackVPCService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Network service provider service implementation used to get list of network service provider
 * and save the network service provider from cloudstack server.
 *
 */
@Service
public class NetworkServiceProviderServiceImpl implements NetworkServiceProviderService {

    /** Network service provider repository reference. */
    @Autowired
    private NetworkServiceProviderRepository networkServiceProviderRepo;

    /** Network service provider cloudstack service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVPCService;

    /** Constant for list network service provider response. */
    public static final String CS_NETWORK_SERVICE_RESOPNSE = "listnetworkserviceprovidersresponse";

    /** Constant for list network service provider. */
    public static final String CS_LIST_NETWORK_SERVICE = "networkserviceprovider";

    @Override
    public NetworkServiceProvider save(NetworkServiceProvider networkServiceProvider) throws Exception {
        return networkServiceProviderRepo.save(networkServiceProvider);
    }

    @Override
    public NetworkServiceProvider update(NetworkServiceProvider networkServiceProvider) throws Exception {
        return networkServiceProviderRepo.save(networkServiceProvider);
    }

    @Override
    public void delete(NetworkServiceProvider networkServiceProvider) throws Exception {
        networkServiceProvider.setStatus(Status.DISABLED);
        networkServiceProviderRepo.save(networkServiceProvider);
    }

    @Override
    public void delete(Long id) throws Exception {
        networkServiceProviderRepo.delete(id);
    }

    @Override
    public NetworkServiceProvider find(Long id) throws Exception {
        return networkServiceProviderRepo.findOne(id);
    }

    @Override
    public Page<NetworkServiceProvider> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return networkServiceProviderRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<NetworkServiceProvider> findAll() throws Exception {
        return (List<NetworkServiceProvider>) networkServiceProviderRepo.findAll();
    }

    @Override
    public List<NetworkServiceProvider> findAllFromCSServer() throws Exception {

        List<NetworkServiceProvider> networkServiceProviderList = new ArrayList<NetworkServiceProvider>();
        HashMap<String, String> networkServiceProviderMap = new HashMap<String, String>();
        JSONArray networkServiceProviderListJSON = null;
        // 1. Get the list of network service provider from CS server using CS connector
        String response = cloudStackVPCService.listNetworkServiceProviders(networkServiceProviderMap, CloudStackConstants.JSON);
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_NETWORK_SERVICE_RESOPNSE);
        if (responseObject.has(CS_LIST_NETWORK_SERVICE)) {
            networkServiceProviderListJSON = responseObject.getJSONArray(CS_LIST_NETWORK_SERVICE);
            // 2. Iterate the json list, convert the single json entity to network service provider
            for (int i = 0, size = networkServiceProviderListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to network service provider entity and Add
                // the converted network service provider entity to list
                NetworkServiceProvider networkServiceProvider = NetworkServiceProvider.convert(networkServiceProviderListJSON.getJSONObject(i));
                networkServiceProviderList.add(networkServiceProvider);

            }
        }
        return networkServiceProviderList;
    }

    @Override
    public NetworkServiceProvider findByUuid(String uuid) throws Exception {
        return networkServiceProviderRepo.findByUuid(uuid);
    }

    @Override
    public NetworkServiceProvider findByName(String name) throws Exception {
        return networkServiceProviderRepo.findByName(name);
    }

}
