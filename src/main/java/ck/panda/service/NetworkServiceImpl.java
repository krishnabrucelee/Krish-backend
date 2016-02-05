package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.Network.Status;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.NetworkRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNetworkService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Service implementation for Network entity.
 */
@Service
public class NetworkServiceImpl implements NetworkService {

    /** Constant for network entity. */
    private static final String NETWORK = "Network";

    /** Constant for cloudStack network . */
    private static final String CS_NETWORK = "network";

    /** Constant for cloudStack network create response. */
    private static final String CS_CREATE_NETWORK_RESPONSE = "createnetworkresponse";

    /** Constant for cloudStack network update response. */
    private static final String CS_UPDATE_NETWORK_RESPONSE = "updatenetworkresponse";

    /** Constant for cloudStack network delete response. */
    private static final String CS_DELETE_NETWORK_RESPONSE = "deletenetworkresponse";

    /** Constant for cloudStack network list response. */
    private static final String CS_LIST_NETWORK_RESPONSE = "listnetworksresponse";

    /** Constant for network type. */
    private static final String CS_TYPE = "type";

    /** Constant for network guestVmcidr. */
    private static final String CS_GUESTVMCIDR = "guestvmcidr";

    /** Network repository reference. */
    @Autowired
    private NetworkRepository networkRepo;

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

    /** Token Detail Utilities. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Project service reference. */
    @Autowired
    private ProjectService projectService;

    /** Zone service reference. */
    @Autowired
    private ZoneService zoneService;

    /** NetworkOffering service reference. */
    @Autowired
    private NetworkOfferingService networkOfferingService;

    /** Domain service reference. */
    @Autowired
    private DomainService domainService;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Virtual Machine service reference. */
    @Autowired
    private VirtualMachineService vmService;

    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'ADD_ISOLATED_NETWORK')")
    public Network save(Network network, Long userId) throws Exception {

        if (network.getSyncFlag()) {
            User user = convertEntityService.getOwnerById(userId);
            Errors errors = validator.rejectIfNullEntity(NETWORK, network);
            errors = validator.validateEntity(network, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                config.setUserServer();
                Zone zoneObject = convertEntityService.getZoneById(network.getZoneId());
                String networkOfferings = csNetwork.createNetwork(zoneObject.getUuid(), CloudStackConstants.JSON, optional(network, userId));
                JSONObject createNetworkResponseJSON = new JSONObject(networkOfferings)
                        .getJSONObject(CS_CREATE_NETWORK_RESPONSE);
                if (createNetworkResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                    errors = this.validateEvent(errors, createNetworkResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                    throw new ApplicationException(errors);
                }
                JSONObject networkResponse = createNetworkResponseJSON.getJSONObject(CS_NETWORK);
                network.setUuid(networkResponse.getString(CloudStackConstants.CS_ID));
                network.setNetworkType(network.getNetworkType().valueOf(networkResponse.getString(CS_TYPE)));
                network.setDisplayText(networkResponse.getString(CloudStackConstants.CS_DISPLAY_TEXT));
                network.setcIDR(networkResponse.getString(CloudStackConstants.CS_CIDR));
                network.setDomainId(domainService.findbyUUID(networkResponse.getString(CloudStackConstants.CS_DOMAIN_ID)).getId());
                network.setZoneId(zoneService.findByUUID(networkResponse.getString(CloudStackConstants.CS_ZONE_ID)).getId());
                network.setNetworkOfferingId(
                networkOfferingService.findByUUID(networkResponse.getString(CloudStackConstants.CS_NETWORK_OFFERING_ID)).getId());
                network.setStatus(network.getStatus().valueOf(networkResponse.getString(CloudStackConstants.CS_STATE).toUpperCase()));
                if (network.getProjectId() != null) {
                    network.setProjectId(convertEntityService.getProjectId(networkResponse.getString(CloudStackConstants.CS_PROJECT_ID)));
                } else {
                    if (network.getDepartmentId() != null) {
                        network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                                departmentService.find(network.getDepartmentId()).getUserName(),
                                domainService.find(network.getDomainId())));
                    } else {
                        network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(departmentService
                                .find(user.getDepartmentId()).getUserName(),
                                domainService.find(network.getDomainId())));
                    }
                }
                network.setGateway(networkResponse.getString(CloudStackConstants.CS_GATEWAY));
                network.setIsActive(true);
                return networkRepo.save(network);
            }
        } else {
            // To check Network UUID while Syncing Network.
            LOGGER.debug("Sync-Network UUID :" + network.getUuid());
            network.setIsActive(true);
            return networkRepo.save(network);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'EDIT_NETWORK')")
    public Network update(Network network) throws Exception {
        if (network.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(NETWORK, network);
            errors = validator.validateEntity(network, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optional = new HashMap<String, String>();
                if (network.getName() != null && network.getName().trim() != "") {
                    optional.put(CloudStackConstants.CS_NAME, network.getName());
                }
                if (network.getDisplayText() != null && network.getDisplayText().trim() != "") {
                    optional.put(CloudStackConstants.CS_DISPLAY_TEXT, network.getDisplayText());
                }
                if (network.getcIDR() != null && network.getcIDR().trim() != "") {
                    Network networkcidr = networkRepo.findOne(network.getId());
                    if (network.getcIDR().equals(networkcidr.getcIDR())) {
                        LOGGER.info("network params");
                    } else {
                        optional.put(CS_GUESTVMCIDR, network.getcIDR());
                    }
                }
                if (network.getNetworkOfferingId() != null) {
                    NetworkOffering networkOffer = convertEntityService
                            .getNetworkOfferingById(network.getNetworkOfferingId());
                    optional.put(CloudStackConstants.CS_NETWORK_OFFERING_ID, networkOffer.getUuid());
                }
                if (network.getNetworkDomain() != null && network.getNetworkDomain().trim() != "") {
                    optional.put(CloudStackConstants.CS_NETWORK_DOMAIN, network.getNetworkDomain());
                }
                config.setUserServer();
                String updateNetworkResponse = csNetwork.updateNetwork(network.getUuid(), optional, CloudStackConstants.JSON);
                JSONObject jobId = new JSONObject(updateNetworkResponse).getJSONObject(CS_UPDATE_NETWORK_RESPONSE);
                Thread.sleep(5000);
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    String jobResponse = csNetwork.networkJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    Thread.sleep(2000);
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                        network.setStatus(Status.ALLOCATED);
                        network.setIsActive(true);
                        network.setName(network.getName());
                        network.setDisplayText(network.getDisplayText());
                        network.setNetworkOfferingId(network.getNetworkOfferingId());
                        network.setGateway(network.getGateway());
                        network.setcIDR(network.getcIDR());
                        network.setNetMask(network.getNetMask());
                        network.setNetworkDomain(network.getNetworkDomain());
                    } else {
                        JSONObject jobresponse = jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                        if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                            if (jobresponse.has(CloudStackConstants.CS_ERROR_CODE)) {
                                errors = this.validateEvent(errors, jobresponse.getString(CloudStackConstants.CS_ERROR_TEXT));
                                throw new ApplicationException(errors);
                            }
                        }
                    }
                }
            }
        }
        return networkRepo.save(network);
    }

    @Override
    public void delete(Network network) throws Exception {
        networkRepo.delete(network);
    }

    @Override
    public void delete(Long id) throws Exception {
        networkRepo.delete(id);
    }

    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'DELETE_NETWORK')")
    public Network softDelete(Network network) throws Exception {
        Errors errors = validator.rejectIfNullEntity(NETWORK, network);
        errors = validator.validateEntity(network, errors);
        network.setIsActive(false);
        if (network.getSyncFlag()) {
            List<VmInstance> vmResponse = vmService.findAllByNetworkAndVmStatus(network.getId(),
                    VmInstance.Status.EXPUNGING);
            if (vmResponse.size() != 0) {
                errors.addGlobalError("Network is associated with Vm instances. You cannot delete this network");
            }
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            network.setIsActive(false);
            network.setStatus(Network.Status.DESTROY);
            if (network.getSyncFlag()) {
                String networkResponse = csNetwork.deleteNetwork(network.getUuid(), CloudStackConstants.JSON);
                JSONObject jobId = new JSONObject(networkResponse).getJSONObject(CS_DELETE_NETWORK_RESPONSE);
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    String jobResponse = csNetwork.networkJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    JSONObject jobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                }
            }
        }
        return networkRepo.save(network);
    }

    @Override
    public Network find(Long id) throws Exception {
        return networkRepo.findOne(id);
    }

    @Override
    public Page<Network> findAllByActive(PagingAndSorting pagingAndSorting, Long userId) throws Exception {

       User user = convertEntityService.getOwnerById(userId);
       // Check the user is not a root and admin and set the domain value from login detail
       if (user.getType().equals(User.UserType.ROOT_ADMIN)) {
           return networkRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
       }

       if (user.getType().equals(User.UserType.DOMAIN_ADMIN)) {
           return networkRepo.findByDomainIsActive(pagingAndSorting.toPageRequest(), true, user.getDomainId());
       }

        Page<Network> network = this.getNetworkListByUser(pagingAndSorting,userId);
        return network;
    }

    /**
     * Get the Network list based on the active status.
     *
     * @param pagingAndSorting do pagination with sorting for network.
     * @param userId id of the user.
     * @return network
     * @throws Exception exception
     */
    private Page<Network> getNetworkListByUser(PagingAndSorting pagingAndSorting, Long userId) throws  Exception {
                User user = convertEntityService.getOwnerById(userId);
                 if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                List<Network> networkList = new ArrayList<Network>();
                for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                    List<Network> projectNetwork = networkRepo.findByProjectDepartmentAndNetwork(project.getId(),
                            user.getDepartmentId(), true);
                    networkList.addAll(projectNetwork);
                }
                List<Network> networks = networkList.stream().distinct().collect(Collectors.toList());
                Page<Network> listingNetworksWithPagination = new PageImpl<Network>(networks);
                return (Page<Network>) listingNetworksWithPagination;
            } else {
                return networkRepo.findByDepartmentAndPagination(user.getDepartmentId(), true,
                    pagingAndSorting.toPageRequest());
            }
    }

    @Override
    public List<Network> findAll() throws Exception {
        return (List<Network>) networkRepo.findAll();
    }

    @Override
    public List<Network> findAllFromCSServerByDomain() throws Exception {
        List<Project> projectList = projectService.findAllByActive(true);
        List<Network> networkList = new ArrayList<Network>();
        for (Project project: projectList) {
            HashMap<String, String> networkMap = new HashMap<String, String>();
            networkMap.put(CloudStackConstants.CS_PROJECT_ID, project.getUuid());
            networkList = getNetworkList(networkMap, networkList);
        }

        HashMap<String, String> networkMap = new HashMap<String, String>();
        networkMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        networkList = getNetworkList(networkMap, networkList);
        return networkList;
        }


    /**
     * Get Network List for Sync.
     *
     * @param networkMap hashMap of the network
     * @param networkList list of network
     * @return return network
     * @throws Exception unHandled Exceptions
     */
    private List<Network> getNetworkList(HashMap<String, String> networkMap, List<Network> networkList) throws Exception {

        // 1. Get the list of domains from CS server using CS connector
        String response = csNetwork.listNetworks(CloudStackConstants.JSON, networkMap);
        JSONArray networkListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_NETWORK_RESPONSE);
        if (responseObject.has(CS_NETWORK)) {
            networkListJSON = responseObject.getJSONArray(CS_NETWORK);
            // 2. Iterate the json list, convert the single json entity to
            // domain
            for (int i = 0, size = networkListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity
                // and Add the converted Domain entity to list
                Network network = Network.convert(networkListJSON.getJSONObject(i));
                network.setDomainId(convertEntityService.getDomainId(network.getTransDomainId()));
                network.setZoneId(convertEntityService.getZoneId(network.getTransZoneId()));
                network.setNetworkOfferingId(
                        convertEntityService.getNetworkOfferingId(network.getTransNetworkOfferingId()));
                 network.setProjectId(convertEntityService.getProjectId(network.getTransProjectId()));
                 network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                         network.getTransDepartmentId(), convertEntityService.getDomain(network.getTransDomainId())));
                 if (network.getTransProjectId() != null) {
                    Project project = projectService.findByUuid(network.getTransProjectId());
                   network.setDepartmentId(project.getDepartmentId());
                }

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
     * Check the Network CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    /**
     * Hash Map to map the optional values to cloudstack.
     *
     * @return optional
     * @param network Network
     * @param userId idof the user
     * @throws Exception Exception
     */
    public HashMap<String, String> optional(Network network, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        HashMap<String, String> optional = new HashMap<String, String>();
        if (network.getNetMask() != null && network.getNetMask().trim() != "") {
            optional.put(CloudStackConstants.CS_NETMASK, network.getNetMask());
        }
        if (network.getGateway() != null && network.getGateway().trim() != "") {
            optional.put(CloudStackConstants.CS_GATEWAY, network.getGateway());
        }
        if (network.getNetworkDomain() != null && network.getNetworkDomain().trim() != "") {
            optional.put(CloudStackConstants.CS_NETWORK_DOMAIN, network.getNetworkDomain());
        }
        if (network.getDomainId() != null) {
            optional.put(CloudStackConstants.CS_DOMAIN_ID, convertEntityService.getDomainById(network.getDomainId()).getUuid());
        } else {
            optional.put(CloudStackConstants.CS_DOMAIN_ID,
                    domainService.find(user.getDomainId()).getUuid());
        }
        if (network.getName() != null && network.getName().trim() != "") {
            optional.put(CloudStackConstants.CS_NAME, network.getName());
        }
        if (network.getDisplayText() != null && network.getDisplayText().trim() != "") {
            optional.put(CloudStackConstants.CS_DISPLAY_TEXT, network.getDisplayText());
        }
        if (network.getNetworkOfferingId() != null) {
            optional.put(CloudStackConstants.CS_NETWORK_OFFERING_ID,
                    convertEntityService.getNetworkOfferingById(network.getNetworkOfferingId()).getUuid());
        }
        if (network.getProjectId() != null) {
            optional.put(CloudStackConstants.CS_PROJECT_ID, convertEntityService.getProjectById(network.getProjectId()).getUuid());

        } else {
            if (network.getDepartmentId() != null) {
                optional.put(CloudStackConstants.CS_ACCOUNT, departmentService.find(network.getDepartmentId()).getUserName());
            } else {
                optional.put(CloudStackConstants.CS_ACCOUNT, departmentService
                        .find(user.getDepartmentId()).getUserName());
            }
        }
        return optional;
    }

    @Override
    public List<Network> findByDepartmentAndNetworkIsActive(Long department, Boolean isActive) throws Exception {
        return networkRepo.findByDepartmentAndNetworkIsActive(department, true);
    }

    @Override
    public List<Network> findByProjectAndNetworkIsActive(Long projectId, Boolean isActive) throws Exception {
        return networkRepo.findByProjectAndNetworkIsActive(projectId, true);
    }

    @Override
    public Network findById(Long id) throws Exception {
        return networkRepo.findById(id);
    }

    @Override
    public List<Network> findAllByActive(Boolean isActive) throws Exception {
        return networkRepo.findAllByIsActive(true);
    }

    @Override
    public Network save(Network network) throws Exception {
        if (!network.getSyncFlag()) {
            return networkRepo.save(network);
        }
        return network;
    }

    @Override
    public Page<Network> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return networkRepo.findAll(pagingAndSorting.toPageRequest());

    }

  }
