package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.repository.jpa.NetworkOfferingRepository;
import ck.panda.util.CloudStackNetworkOfferingService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service implementation for NetworkOffering entity.
 *
 */
@Service
public class NetworkOfferingServiceImpl implements NetworkOfferingService {

    /** NetworkOffering repository reference. */
    @Autowired
    private NetworkOfferingRepository networkRepo;

    /** CloudStack Network Offering service for connectivity with cloudstack. */
    @Autowired
    private CloudStackNetworkOfferingService networkOfferingService;

    @Override
    public NetworkOffering save(NetworkOffering network) throws Exception {
        return networkRepo.save(network);
    }

    @Override
    public NetworkOffering update(NetworkOffering network) throws Exception {
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
        return null;
    }

    @Override
    public List<NetworkOffering> findAll() throws Exception {
        return (List<NetworkOffering>) networkRepo.findAll();
    }

    @Override
    public List<NetworkOffering> findAllFromCSServer() throws Exception {

         List<NetworkOffering> networkOfferingList = new ArrayList<NetworkOffering>();
          HashMap<String, String> networkOfferingMap = new HashMap<String, String>();

          // 1. Get the list of domains from CS server using CS connector
          String response = networkOfferingService.listNetworkOfferings("json", networkOfferingMap);

          JSONArray networkOfferingListJSON = new JSONObject(response).getJSONObject("listnetworkofferingsresponse")
                  .getJSONArray("networkoffering");
          // 2. Iterate the json list, convert the single json entity to domain
          for (int i = 0, size = networkOfferingListJSON.length(); i < size; i++) {
              // 2.1 Call convert by passing JSONObject to Domain entity and Add
              // the converted Domain entity to list
              networkOfferingList.add(NetworkOffering.convert(networkOfferingListJSON.getJSONObject(i)));
          }
          return networkOfferingList;
    }

}
