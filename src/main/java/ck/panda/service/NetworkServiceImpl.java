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
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.NetworkOfferingRepository;
import ck.panda.domain.repository.jpa.NetworkRepository;
import ck.panda.domain.repository.jpa.ZoneRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNetworkService;
import ck.panda.util.ConfigUtil;
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

    /** Domain repository reference. */
    @Autowired
    private DomainRepository domainRepository;

    /** Department repository reference. */
    @Autowired
    private DepartmentReposiory departmentRepository;

    /** Zone repository reference. */
    @Autowired
    private ZoneRepository zoneRepository;

    /** NetworkOffering repository reference. */
    @Autowired
    private NetworkOfferingRepository networkofferingRepo;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

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
                    network.setDepartmentId(convertEntityService.getDepartmentByUsername(createComputeResponseJSON.getString("account")));
                    network.setGateway(createComputeResponseJSON.getString("gateway"));
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
    public Network softDelete(Network network) throws Exception {
    	network.setIsActive(false);
    	network.setStatus(Network.Status.Destroy);
        return networkRepo.save(network);
    }


    @Override
    public List<Network> findAllFromCSServerByDomain() throws Exception {

        List<Network> networkList = new ArrayList<Network>();
        HashMap<String, String> networkMap = new HashMap<String, String>();
        networkMap.put("listall", "true");
        // 1. Get the list of domains from CS server using CS connector
        String response = csNetwork.listNetworks("json", networkMap);
        JSONArray networkListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listnetworksresponse");
        if (responseObject.has("network")) {
            networkListJSON = responseObject.getJSONArray("network");
            // 2. Iterate the json list, convert the single json entity to
            // domain
            for (int i = 0, size = networkListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity and
                // Add
                // the converted Domain entity to list
            	Network network = Network.convert(networkListJSON.getJSONObject(i));
            	network.setDomainId(convertEntityService.getDomainId(network.getTransDomainId()));
            	network.setZoneId(convertEntityService.getZoneId(network.getTransZoneId()));
            	network.setNetworkOfferingId(convertEntityService.getNetworkOfferingId(network.getTransNetworkOfferingId()));
                network.setDepartmentId(convertEntityService.getDepartmentByUsername(network.getTransDepartmentId()));
            	networkList.add(network);
            }
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
     * @return optional
     * @param network Network
     * @throws Exception Exception
     */
    public HashMap<String, String> optional(Network network) throws Exception {

        HashMap<String, String> optional = new HashMap<String, String>();

        optional.put("networkofferingid", network.getNetworkOffering().getUuid().toString());
        optional.put("account", network.getDepartment().getUserName());
        optional.put("domainid", network.getDepartment().getDomain().getUuid());

        return optional;
    }

	@Override
	public List<Network> findByDepartment(Long department) throws Exception {
		Department deptNetwork = departmentRepository.findOne(department);
		return networkRepo.findByDepartment(deptNetwork);
	}

}
