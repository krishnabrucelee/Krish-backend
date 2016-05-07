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
import ck.panda.domain.entity.SupportedNetwork;
import ck.panda.domain.repository.jpa.SupportedNetworkRepository;
import ck.panda.util.CloudStackVPCService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Supported network service implementation used to get list of supported network and save the supported network from cloudstack server.
 *
 */
@Service
public class SupportedNetworkServiceImpl implements SupportedNetworkService {

    /** Supported network repository reference. */
    @Autowired
    private SupportedNetworkRepository supportedNetworkRepo;

    /** Supported network cloudstack service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVPCService;

    /** Network service provider cloudstack service reference. */
    @Autowired
    private NetworkServiceProviderService networkServiceProviderService;

    /** Constant for list supported network response. */
    public static final String CS_LIST_SUPPORTED_NETWORK_RESOPNSE = "listsupportednetworkservicesresponse";

    /** Constant for list supported network. */
    public static final String CS_NETWORK_SERVICE = "networkservice";

    /** Constant for network provider. */
    public static final String CS_PROVIDER = "provider";

    @Override
    public SupportedNetwork save(SupportedNetwork supportedNetwork) throws Exception {
        supportedNetworkRepo.save(supportedNetwork);
        List<NetworkServiceProvider> networkServiceProviderList = new ArrayList<NetworkServiceProvider>();
        if (supportedNetwork.getTransProviderList() != null) {
            for (int i = 0; i < supportedNetwork.getTransProviderList().size(); i++) {
                NetworkServiceProvider networkServiceProvider = networkServiceProviderService.findByUuid(supportedNetwork.getTransProviderList().get(i));
                if (networkServiceProvider != null) {
                    networkServiceProviderList.add(networkServiceProvider);
                    supportedNetwork.setNetworkServiceProviderList(networkServiceProviderList);
                }
            }
        }
        return supportedNetworkRepo.save(supportedNetwork);
    }

    @Override
    public SupportedNetwork update(SupportedNetwork supportedNetwork) throws Exception {
        supportedNetworkRepo.save(supportedNetwork);
        List<NetworkServiceProvider> networkServiceProviderList = new ArrayList<NetworkServiceProvider>();
        if (supportedNetwork.getTransProviderList() != null) {
            for (int i = 0; i < supportedNetwork.getTransProviderList().size(); i++) {
                NetworkServiceProvider networkServiceProvider = networkServiceProviderService.findByUuid(supportedNetwork.getTransProviderList().get(i));
                if (networkServiceProvider != null) {
                    networkServiceProviderList.add(networkServiceProvider);
                    supportedNetwork.setNetworkServiceProviderList(networkServiceProviderList);
                }
            }
        }
        return supportedNetworkRepo.save(supportedNetwork);
    }

    @Override
    public void delete(SupportedNetwork supportedNetwork) throws Exception {
        supportedNetworkRepo.delete(supportedNetwork);
    }

    @Override
    public void delete(Long id) throws Exception {
        supportedNetworkRepo.delete(id);
    }

    @Override
    public SupportedNetwork find(Long id) throws Exception {
        return supportedNetworkRepo.findOne(id);
    }

    @Override
    public Page<SupportedNetwork> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return supportedNetworkRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<SupportedNetwork> findAll() throws Exception {
        return (List<SupportedNetwork>) supportedNetworkRepo.findAll();
    }

    @Override
    public List<SupportedNetwork> findAllFromCSServer() throws Exception {

        List<SupportedNetwork> supportedNetworkList = new ArrayList<SupportedNetwork>();
        HashMap<String, String> supportedNetworkMap = new HashMap<String, String>();
        JSONArray supportedNetworkListJSON = null;
        // 1. Get the list of supported network from CS server using CS connector
        String response = cloudStackVPCService.listSupportedNetworkServices(supportedNetworkMap, CloudStackConstants.JSON);
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_SUPPORTED_NETWORK_RESOPNSE);
        if (responseObject.has(CS_NETWORK_SERVICE)) {
            supportedNetworkListJSON = responseObject.getJSONArray(CS_NETWORK_SERVICE);
            // 2. Iterate the json list, convert the single json entity to supported network
            for (int i = 0, size = supportedNetworkListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to supported network entity and Add
                // the converted supported network entity to list
                SupportedNetwork supportedNetwork = SupportedNetwork.convert(supportedNetworkListJSON.getJSONObject(i));
                List<String> providerList = new ArrayList<String>();
                if (supportedNetworkListJSON.getJSONObject(i).has(CS_PROVIDER)) {
                    for (int j = 0; j < supportedNetworkListJSON.getJSONObject(i).getJSONArray(CS_PROVIDER).length(); j++) {
                        JSONObject providerResponseObject = (JSONObject) supportedNetworkListJSON.getJSONObject(i).getJSONArray(CS_PROVIDER).get(j);
                        if (networkServiceProviderService.findByName(providerResponseObject.getString(CloudStackConstants.CS_NAME)) != null) {
                            providerList.add(networkServiceProviderService.findByName(providerResponseObject.getString(CloudStackConstants.CS_NAME)).getUuid().toString());
                        }

                    }
                }
                supportedNetwork.setTransProviderList(providerList);
                supportedNetworkList.add(supportedNetwork);
            }
        }
        return supportedNetworkList;
    }

    @Override
    public SupportedNetwork findByName(String name) throws Exception {
        return supportedNetworkRepo.findByName(name);
    }

}
