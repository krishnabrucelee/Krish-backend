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
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.NetworkOfferingRepository;
import ck.panda.domain.repository.jpa.NetworkRepository;
import ck.panda.domain.repository.jpa.ZoneRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNetworkService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.ConvertUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Service implementation for Network entity.
 *
 */
@Service
public class NetworkServiceImpl implements NetworkService {

    /** Network repository reference. */
    @Autowired
    private NetworkRepository networkRepo;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private ZoneRepository zoneRepository;

    @Autowired
    private NetworkOfferingRepository networkofferingRepo;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil entity;

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NetworkServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Service implementation for Cloudstack Network . */
    @Autowired
    private CloudStackNetworkService csNetwork;

    /** Configuration Utilities. */
    @Autowired
    private ConfigUtil config;

    @Override
    public Network save(Network network) throws Exception {

        if (network.getSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity("Network", network);
        errors = validator.validateEntity(network, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            config.setServer(1L);

            String networkOfferings = csNetwork.createNetwork(network.getDisplayText(),network.getName(), network.getZone().getUuid(),"json",optional(network));
                    JSONObject createComputeResponseJSON = new JSONObject(networkOfferings).getJSONObject("createnetworkresponse")
                        .getJSONObject("network");
                    network.setUuid(createComputeResponseJSON.getString("id"));
                    network.setNetworkType(network.getNetworkType().valueOf(createComputeResponseJSON.getString("type")));
                    network.setDisplayText(createComputeResponseJSON.getString("displaytext"));
                    network.setcIDR(createComputeResponseJSON.getString("cidr"));
                    network.setDomainId(domainRepository.findByUUID(createComputeResponseJSON.getString("domainid")).getId());
                    network.setZoneId(zoneRepository.findByUUID(createComputeResponseJSON.getString("zoneid")).getId());
                    network.setNetworkOfferingId(networkofferingRepo.findByUUID(createComputeResponseJSON.getString("networkofferingid")).getId());
                    network.setStatus(network.getStatus().valueOf(createComputeResponseJSON.getString("state")));
            return networkRepo.save(network);
        }
     } else {
         LOGGER.debug(network.getUuid());
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
    public List<Network> findAllFromCSServerByDomain(String domainUuid) throws Exception {

        List<Network> networkList = new ArrayList<Network>();
          HashMap<String, String> networkMap = new HashMap<String, String>();
          networkMap.put("domainid", domainUuid);
          // 1. Get the list of domains from CS server using CS connector
          String response = csNetwork.listNetworks("json", networkMap);

          JSONArray networkListJSON = new JSONObject(response).getJSONObject("listnetworksresponse")
                  .getJSONArray("network");
          // 2. Iterate the json list, convert the single json entity to domain
          for (int i = 0, size = networkListJSON.length(); i < size; i++) {
              // 2.1 Call convert by passing JSONObject to Domain entity and Add
              // the converted Domain entity to list
              networkList.add(Network.convert(networkListJSON.getJSONObject(i),entity));
          }
          return networkList;
      }

      @Override
      public Network findByUUID(String uuid) throws Exception {
          return networkRepo.findByUUID(uuid);
      }

    /**
     * Hash Map to map the optional values to cloudstack.
     *
     * @param Network Network
     * @return optional
     * @throws Exception Exception
     */
    public HashMap<String, String> optional(Network network) throws Exception {

        HashMap<String, String> optional = new HashMap<String, String>();

        optional.put("networkofferingid", network.getNetworkOffering().getUuid().toString());

        return optional;
    }

}
