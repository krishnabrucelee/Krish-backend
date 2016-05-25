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
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.VPNCustomerGateway;
import ck.panda.domain.repository.jpa.VPNCustomerGatewayRepository;
import ck.panda.util.CSVPNService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * VPNCustomerGateway service implementation class.
 *
 */
@Service
public class VPNCustomerGatewayServiceImpl implements VPNCustomerGatewayService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VPNCustomerGatewayServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private VPNCustomerGatewayRepository gatewayRepo;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CSVPNService csVpnService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Reference of the project service*/
    @Autowired
    private ProjectService projectService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Reference of the domain service. */
    @Autowired
    private DomainService domainService;

    /** Constant for peer detection. */
    public static final String CS_listcustomer_gateway = "listvpncustomergatewaysresponse";

    /** Constant for peer detection. */
    public static final String VPN_CUSOTMER_GATEWAY = "vpncustomergateway";

    @Override
    public VPNCustomerGateway save(VPNCustomerGateway vpnGateway) throws Exception {
        LOGGER.debug(vpnGateway.getUuid());
        return gatewayRepo.save(vpnGateway);
    }

    @Override
    public VPNCustomerGateway update(VPNCustomerGateway vpnGateway) throws Exception {
        LOGGER.debug(vpnGateway.getUuid());
        return gatewayRepo.save(vpnGateway);
    }

    @Override
    public void delete(VPNCustomerGateway vpnGateway) throws Exception {
        gatewayRepo.delete(vpnGateway);
    }

    @Override
    public void delete(Long id) throws Exception {
        gatewayRepo.delete(id);
    }

    @Override
    public VPNCustomerGateway find(Long id) throws Exception {
        VPNCustomerGateway vpnGateway = gatewayRepo.findOne(id);
        return vpnGateway;
    }

    @Override
    public Page<VPNCustomerGateway> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return gatewayRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VPNCustomerGateway> findAll() throws Exception {
        return (List<VPNCustomerGateway>) gatewayRepo.findAll();
    }

    @Override
    public List<VPNCustomerGateway> findAllFromCSServer() throws Exception {
        List<Project> projectList = projectService.findAllByActive(true);
        List<VPNCustomerGateway> gatewayList = new ArrayList<VPNCustomerGateway>();
        for (Project project: projectList) {
            HashMap<String, String> gatewayMap = new HashMap<String, String>();
            gatewayMap.put(CloudStackConstants.CS_PROJECT_ID, project.getUuid());
            gatewayList = getGatewayList(gatewayMap, gatewayList);
        }

        HashMap<String, String> gatewayMap = new HashMap<String, String>();
        gatewayMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        gatewayList = getGatewayList(gatewayMap, gatewayList);
        return gatewayList;
        }


    /**
     * Get Gateway List for Sync.
     *
     * @param gatewayMap hashMap of the network
     * @param GatewayList list of vpn customer gateway.
     * @return return vpn customer gateway
     * @throws Exception unHandled Exceptions
     */
    private List<VPNCustomerGateway> getGatewayList(HashMap<String, String> gatewayMap, List<VPNCustomerGateway> gatewayList) throws Exception {
        config.setServer(1L);
        // 1. Get the list of pods from CS server using CS connector
        String response = csVpnService.listVpnCustomerGateways(gatewayMap, CloudStackConstants.JSON);
        JSONArray podListJSON = null;
        configServer.setServer(1L);
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_listcustomer_gateway);
        if (responseObject.has(VPN_CUSOTMER_GATEWAY)) {
            podListJSON = responseObject.getJSONArray(VPN_CUSOTMER_GATEWAY);
            // 2. Iterate the json list, convert the single json entity to vpnGateway
            for (int i = 0, size = podListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity and
                // Add
                // the converted vpnGateway entity to list
                VPNCustomerGateway vpnGateway = VPNCustomerGateway.convert(podListJSON.getJSONObject(i));
                vpnGateway.setDomainId(convertEntityService.getDomainId(vpnGateway.getTransDomainId()));
                if (vpnGateway.getTransProjectId() != null) {
                    vpnGateway.setProjectId(convertEntityService.getProjectId(vpnGateway.getTransProjectId()));
                    vpnGateway.setDepartmentId(projectService.find(vpnGateway.getProjectId()).getDepartmentId());
                } else {
                    Domain domain = domainService.find(vpnGateway.getDomainId());
                    vpnGateway.setDepartmentId(convertEntityService
                            .getDepartmentByUsernameAndDomains(vpnGateway.getTransDepartmentId(), domain));
                }
                gatewayList.add(vpnGateway);
            }
          }
        return gatewayList;
    }

}