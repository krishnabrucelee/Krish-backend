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
import ck.panda.domain.entity.Network;
import ck.panda.domain.repository.jpa.NetworkRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNetworkService;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Service implementation for Guest Network entity.
 *
 */
@Service
public class NetworkServiceImpl implements NetworkService {

    /** Network repository reference. */
    @Autowired
    private NetworkRepository networkRepo;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** CloudStack Network service for connectivity with cloudstack. */
    @Autowired
    private CloudStackNetworkService csNetwork;

     @Override
    public Network save(Network network) throws Exception {

        Errors errors = validator.rejectIfNullEntity("Network", network);
        errors = validator.validateEntity(network, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
              return networkRepo.save(network);
        }
    }

    @Override
    public Network update(Network network) throws Exception {
        return networkRepo.save(network);

    }

    @Override
    public void delete(Network id) throws Exception {
        networkRepo.delete(id);
    }

    @Override
    public void delete(Long id) throws Exception {
        networkRepo.delete(id);

    }

    @Override
    public Network find(Long id) throws Exception {
        return networkRepo.findOne(id);
    }

    @Override
    public Page<Network> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return networkRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Network> findAll() throws Exception {
        return (List<Network>) networkRepo.findAll();

    }

    @Override
    public List<Network> findAllFromCSServer() throws Exception {

        List<Network> networkList = new ArrayList<Network>();
          HashMap<String, String> networkMap = new HashMap<String, String>();

          // 1. Get the list of domains from CS server using CS connector
          String response = csNetwork.listNetworks("json", networkMap);

          JSONArray networkListJSON = new JSONObject(response).getJSONObject("listnetworksresponse")
                  .getJSONArray("network");
          // 2. Iterate the json list, convert the single json entity to domain
          for (int i = 0, size = networkListJSON.length(); i < size; i++) {
              // 2.1 Call convert by passing JSONObject to Domain entity and Add
              // the converted Domain entity to list
              networkList.add(Network.convert(networkListJSON.getJSONObject(i)));
          }
          return networkList;
      }



}
