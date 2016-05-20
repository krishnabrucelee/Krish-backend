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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.SupportedNetwork;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VPC;
import ck.panda.domain.entity.Network.NetworkCreationType;
import ck.panda.domain.entity.Network.Status;
import ck.panda.domain.entity.ResourceLimitDepartment.ResourceType;
import ck.panda.domain.entity.NetworkOffering;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VpnUser;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.IpAddress.State;
import ck.panda.domain.repository.jpa.NetworkRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackNetworkService;
import ck.panda.util.CloudStackOptionalUtil;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;

/**Network service implementation class. */
@Service
public class NetworkServiceImpl implements NetworkService {

    /** Constant for network entity. */
    private static final String NETWORK = "Network";

    /** Constant for cloudStack network. */
    private static final String CS_NETWORK = "network";

    /** Constant for cloudStack network create response. */
    private static final String CS_CREATE_NETWORK_RESPONSE = "createnetworkresponse";

    /** Constant for cloudStack network update response. */
    private static final String CS_UPDATE_NETWORK_RESPONSE = "updatenetworkresponse";

    /** Constant for cloudStack network delete response. */
    private static final String CS_DELETE_NETWORK_RESPONSE = "deletenetworkresponse";

    /** Constant for cloudStack network list response. */
    private static final String CS_LIST_NETWORK_RESPONSE = "listnetworksresponse";

    /** Constant for cloudstack response restart. */
    private static final String CS_RESTART_NETWORK_RESPONSE = "restartnetworkresponse";

    /** Update quota constants. */
    public static final String  CS_Network = "Network", CS_IP = "IP", Update = "update", Delete = "delete", CS_Project = "Project", CS_Department = "Department";

    /** Constant for clean up. */
    private static final String CS_CLEAN_UP = "cleanup";

    /** Constant for network guestVmcidr. */
    private static final String CS_GUESTVMCIDR = "guestvmcidr";

    /** Network repository reference. */
    @Autowired
    private NetworkRepository networkRepo;

    /** Quota limit validation reference. */
    @Autowired
    private QuotaValidationService quotaLimitValidation;

    /** support service reference. */
    @Autowired
    private SupportedNetworkService supportedNetworkService;

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

    /** Nic service reference. */
    @Autowired
    private NicService nicService;

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

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** Sync Service reference. */
    @Autowired
    private SyncService syncService;

    /** Sync Service reference. */
    @Autowired
    private AsynchronousJobService asyncService;

    /** CloudStack connector reference for resource capacity. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /** Update Resource Count service reference. */
    @Autowired
    private UpdateResourceCountService updateResourceCountService;

    /** For listing VPN user list from cloudstack server. */
    @Autowired
    private VpnUserService vpnUserService;

    /**IP Address service reference.  */
    @Autowired
    private IpaddressService ipService;

    /** Token details reference. */
    @Autowired
    private TokenDetails tokenDetails;

    @Autowired
    private VPCService vpcService;

    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'ADD_ISOLATED_NETWORK')")
    public Network save(Network network, Long userId) throws Exception {
        if (network.getSyncFlag()) {
            User user = convertEntityService.getOwnerById(userId);
            Errors errors = validator.rejectIfNullEntity(NETWORK, network);
            errors = validator.validateEntity(network, errors);
            HashMap<String, String> optionalMap = new HashMap<String, String>();
            optionalMap.put(CloudStackConstants.CS_ZONE_ID,
                    convertEntityService.getZoneById(network.getZoneId()).getUuid());
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else if (network.getVpcId() == null) {
                /** Used for setting optional values for resource count. */
                HashMap<String, String> domainCountMap = new HashMap<String, String>();
                // check department and project quota validation.
                ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                        .findByDepartmentAndResourceType(network.getDepartmentId(), ResourceType.Instance, true);
                ResourceLimitProject projectLimit = resourceLimitProjectService
                        .findByProjectAndResourceType(network.getProjectId(), ResourceLimitProject.ResourceType.Instance, true);
                if (departmentLimit != null && convertEntityService.getDepartmentById(network.getDepartmentId()).getType()
                        .equals(AccountType.USER)) {
                    if (network.getProjectId() != null) {
                        if (projectLimit != null) {
                            quotaLimitValidation.QuotaLimitCheckByResourceObject(network, NETWORK,
                                network.getProjectId(), "Project");
                        } else {
                            errors.addGlobalError(
                                    "Resource limit for project has not been set. Please update project quota");
                            throw new ApplicationException(errors);
                        }
                    } else {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(network, NETWORK,
                                network.getDepartmentId(), "Department");
                    }
                    try {
                        config.setUserServer();
                        Zone zoneObject = convertEntityService.getZoneById(network.getZoneId());
                        String networkOfferings = csNetwork.createNetwork(zoneObject.getUuid(),
                                CloudStackConstants.JSON, optional(network, userId));
                        JSONObject createNetworkResponseJSON = new JSONObject(networkOfferings)
                                .getJSONObject(CS_CREATE_NETWORK_RESPONSE);
                        if (createNetworkResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                            errors = this.validateEvent(errors,
                                    createNetworkResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                            throw new ApplicationException(errors);
                        }
                        JSONObject networkResponse = createNetworkResponseJSON.getJSONObject(CS_NETWORK);
                        network.setUuid(networkResponse.getString(CloudStackConstants.CS_ID));
                        network.setNetworkType(network.getNetworkType().valueOf(networkResponse.getString(CloudStackConstants.CS_TYPE)));
                        network.setDisplayText(networkResponse.getString(CloudStackConstants.CS_DISPLAY_TEXT));
                        network.setcIDR(networkResponse.getString(CloudStackConstants.CS_CIDR));
                        network.setDomainId(domainService
                                .findbyUUID(networkResponse.getString(CloudStackConstants.CS_DOMAIN_ID)).getId());
                        network.setZoneId(zoneService
                                .findByUUID(networkResponse.getString(CloudStackConstants.CS_ZONE_ID)).getId());
                        network.setNetworkOfferingId(networkOfferingService
                                .findByUUID(networkResponse.getString(CloudStackConstants.CS_NETWORK_OFFERING_ID))
                                .getId());
                        network.setStatus(network.getStatus()
                                .valueOf(networkResponse.getString(CloudStackConstants.CS_STATE).toUpperCase()));
                        if (network.getProjectId() != null) {
                            network.setProjectId(convertEntityService
                                    .getProjectId(networkResponse.getString(CloudStackConstants.CS_PROJECT_ID)));
                        } else {
                            if (network.getDepartmentId() != null) {
                                network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                                        departmentService.find(network.getDepartmentId()).getUserName(),
                                        domainService.find(network.getDomainId())));
                            } else {
                        network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                                departmentService.find(user.getDepartmentId()).getUserName(),
                                        domainService.find(network.getDomainId())));
                            }
                        }
                        network.setGateway(networkResponse.getString(CloudStackConstants.CS_GATEWAY));
                        network.setIsActive(true);
                    } catch (ApplicationException e) {
                        LOGGER.error("ERROR AT NETWORK CREATION", e);
                        throw new ApplicationException(e.getErrors());
                    }
                    if (network.getProjectId() != null) {
                        updateResourceCountService.QuotaUpdateByResourceObject(network, NETWORK, network.getProjectId(),
                                    "Project", "update");
                    } else {
                        updateResourceCountService.QuotaUpdateByResourceObject(network, NETWORK,
                                    network.getDepartmentId(), "Department", "update");
                    }
                    network.setNetworkCreationType(NetworkCreationType.ADVANCED_NETWORK);
                    return networkRepo.save(network);

                } else {
                    errors.addGlobalError(
                            "Resource limit for department has not been set. Please update department quota");
                    throw new ApplicationException(errors);
                }
            } else {
                try {
                    config.setUserServer();
                    Zone zoneObject = convertEntityService.getZoneById(network.getZoneId());
                    String networkOfferings = csNetwork.createNetwork(zoneObject.getUuid(),
                            CloudStackConstants.JSON, optional(network, userId));
                    JSONObject createNetworkResponseJSON = new JSONObject(networkOfferings)
                            .getJSONObject(CS_CREATE_NETWORK_RESPONSE);
                    if (createNetworkResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                        errors = this.validateEvent(errors,
                                createNetworkResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                        throw new ApplicationException(errors);
                    }
                    JSONObject networkResponse = createNetworkResponseJSON.getJSONObject(CS_NETWORK);
                    network.setUuid(networkResponse.getString(CloudStackConstants.CS_ID));
                    network.setNetworkType(network.getNetworkType().valueOf(networkResponse.getString(CloudStackConstants.CS_TYPE)));
                    network.setDisplayText(networkResponse.getString(CloudStackConstants.CS_DISPLAY_TEXT));
                    network.setcIDR(networkResponse.getString(CloudStackConstants.CS_CIDR));
                    network.setDomainId(domainService
                            .findbyUUID(networkResponse.getString(CloudStackConstants.CS_DOMAIN_ID)).getId());
                    network.setZoneId(zoneService
                            .findByUUID(networkResponse.getString(CloudStackConstants.CS_ZONE_ID)).getId());
                    network.setNetworkOfferingId(networkOfferingService
                            .findByUUID(networkResponse.getString(CloudStackConstants.CS_NETWORK_OFFERING_ID))
                            .getId());
                    network.setStatus(network.getStatus()
                            .valueOf(networkResponse.getString(CloudStackConstants.CS_STATE).toUpperCase()));
                    if (network.getProjectId() != null) {
                        network.setProjectId(convertEntityService
                                .getProjectId(networkResponse.getString(CloudStackConstants.CS_PROJECT_ID)));
                    } else {
                        if (network.getDepartmentId() != null) {
                            network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                                    departmentService.find(network.getDepartmentId()).getUserName(),
                                    domainService.find(network.getDomainId())));
                        } else {
                            network.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                                departmentService.find(user.getDepartmentId()).getUserName(),
                                    domainService.find(network.getDomainId())));
                        }
                    }
                    network.setGateway(networkResponse.getString(CloudStackConstants.CS_GATEWAY));
                    network.setIsActive(true);
                } catch (ApplicationException e) {
                    LOGGER.error("ERROR AT NETWORK CREATION", e);
                    throw new ApplicationException(e.getErrors());
                }
                network.setNetworkCreationType(NetworkCreationType.VPC);
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
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    String jobResponse = csNetwork.networkJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
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
        network.setIsActive(false);
        if (network.getSyncFlag()) {
            List<VmInstance> vmResponse = vmService.findAllByNetworkAndVmStatus(network.getId(),
                    VmInstance.Status.EXPUNGING);
            List<Nic> nicResponse = nicService.findAllByNetworkAndIsActive(network.getId(), true);
            if (vmResponse.size() != 0 || nicResponse.size() != 0) {
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                        "Network is associated with Vm instances. You cannot delete this network");
            }
            Errors errors = validator.rejectIfNullEntity(NETWORK, network);
            errors = validator.validateEntity(network, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            }
            // check department and project quota validation.
            ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                    .findByDepartmentAndResourceType(network.getDepartmentId(), ResourceType.Instance, true);
            if (departmentLimit != null) {
                if (network.getProjectId() != null) {
                    // syncService.syncResourceLimitProject(convertEntityService.getProjectById(network.getProjectId()));
                }
                network.setIsActive(false);
                network.setStatus(Network.Status.DESTROY);
                if (network.getSyncFlag()) {
                    /*if (network.getProjectId() != null) {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(
                                convertEntityService.getNetworkById(network.getId()), "IP",
                                convertEntityService.getNetworkById(network.getId()).getProjectId(), "Project");

                    } else if (network.getDepartmentId() != null) {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(
                                convertEntityService.getNetworkById(network.getId()), "IP",
                                convertEntityService.getNetworkById(network.getId()).getDepartmentId(), "Department");
                    }*/
                    config.setUserServer();
                    String networkResponse = csNetwork.deleteNetwork(network.getUuid(), CloudStackConstants.JSON);
                    JSONObject jobId = new JSONObject(networkResponse).getJSONObject(CS_DELETE_NETWORK_RESPONSE);
                    if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                        String jobResponse = csNetwork.networkJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                                CloudStackConstants.JSON);
                        JSONObject jobresult = new JSONObject(jobResponse)
                                .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                        this.ipRelease(network);
                    }
                }
            } else {
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                        "Resource limit for department has not been set. Please update department quota");
            }
        }
        return networkRepo.save(network);
    }

    @Override
    public Network ipRelease(Network network) throws Exception {
        List<IpAddress> ipList = ipService.findByNetwork(network.getId());
        for (IpAddress ip : ipList) {
            List<VpnUser> vpnUserList = vpnUserService.findAllByDepartmentAndDomainAndIsActive(network.getDepartmentId(), network.getDomainId(), true);
            if (vpnUserList.size() != 0) {
            for (VpnUser vpnUser : vpnUserList) {
                vpnUser.setIsActive(false);
                vpnUser.setSyncFlag(false);
                vpnUserService.softDelete(vpnUser);
            }
            }
            ipService.ruleDelete(ip);
            IpAddress ipAddress = new IpAddress();
            ipAddress.setId(ip.getId());
            ipAddress.setState(State.FREE);
            ipAddress.setIsStaticnat(false);
            ipAddress.setIsSourcenat(false);
            ipAddress.setDepartmentId(ip.getDepartmentId());
            ipAddress.setZoneId(ip.getZoneId());
            ipAddress.setDisplay(ip.getDisplay());
            ipAddress.setProjectId(ip.getProjectId());
            ipAddress.setUuid(ip.getUuid());
            ipAddress.setPublicIpAddress(ip.getPublicIpAddress());
            ipAddress.setVmInstanceId(ip.getVmInstanceId());
            ipAddress.setVlan(ip.getVlan());
            ipAddress.setCreatedBy(ip.getCreatedBy());
            ipAddress.setCreatedDateTime(ip.getCreatedDateTime());
            ipAddress = ipService.update(ipAddress);
            // Resource Count delete
            if (ipAddress.getProjectId() != null) {
                updateResourceCountService.QuotaUpdateByResourceObject(ipAddress, CS_IP,
                            ipAddress.getProjectId(), CS_Project, Delete);
            } else {
                updateResourceCountService.QuotaUpdateByResourceObject(ipAddress, CS_IP,
                            ipAddress.getDepartmentId(), CS_Department, Delete);
            }
        }
        return network;
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
            List<Project> allProjectList = new ArrayList<Project>();
            for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                allProjectList.add(project);
            }
            Page<Network> projectNetwork = networkRepo.findByProjectDepartmentAndIsActive(allProjectList,
                    user.getDepartmentId(), true, pagingAndSorting.toPageRequest());
            return projectNetwork;
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
        config.setServer(1L);
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
                 if (network.getTransVpcId() != null) {
                     network.setVpcId(convertEntityService.getVpcId(network.getTransVpcId()));
                     network.setNetworkCreationType(NetworkCreationType.VPC);
                 } else {
                     network.setNetworkCreationType(NetworkCreationType.ADVANCED_NETWORK);
                 }
                 if (network.getTransAclId() != null) {
                     network.setAclId(convertEntityService.getVpcAclId(network.getTransAclId()));
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
            optional.put(CloudStackConstants.CS_DOMAIN_ID,
                    convertEntityService.getDomainById(network.getDomainId()).getUuid());
        } else {
            optional.put(CloudStackConstants.CS_DOMAIN_ID, domainService.find(user.getDomainId()).getUuid());
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
            optional.put(CloudStackConstants.CS_PROJECT_ID,
                    convertEntityService.getProjectById(network.getProjectId()).getUuid());

        } else {
            if (network.getDepartmentId() != null) {
                optional.put(CloudStackConstants.CS_ACCOUNT,
                        departmentService.find(network.getDepartmentId()).getUserName());
            } else {
                optional.put(CloudStackConstants.CS_ACCOUNT,
                        departmentService.find(user.getDepartmentId()).getUserName());
            }
        }
        if (network.getAclId() != null) {
            optional.put(CloudStackConstants.CS_ACL_ID, convertEntityService.getVpcAclById(network.getAclId()).getUuid());
        }
        if (network.getVpcId() != null) {
            optional.put(CloudStackConstants.CS_VPC_ID, convertEntityService.getVpcById(network.getVpcId()).getUuid());
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
    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'RESTART_NETWORK')")
    public Network restartNetwork(Network network) throws Exception {
        Errors errors = validator.rejectIfNullEntity(NETWORK, network);
        errors = validator.validateEntity(network, errors);
        if (network.getSyncFlag()) {
            HashMap<String, String> optionalParams = new HashMap<String, String>();
            // Mapping optional parameters.
            CloudStackOptionalUtil.updateOptionalBooleanValue(CS_CLEAN_UP, network.getCleanUpNetwork(), optionalParams);
            // Configuration value to ACS.
            config.setUserServer();
            // Restart network call to ACS
            String restartResponse = csNetwork.restartNetwork(network.getUuid(), optionalParams,
                    CloudStackConstants.JSON);
            JSONObject jobId = new JSONObject(restartResponse).getJSONObject(CS_RESTART_NETWORK_RESPONSE);
            // Temporarily added thread, will be removed once web socket is
            // done.
            // Checking job id.
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = csNetwork.networkJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                        .equals(CloudStackConstants.PROGRESS_JOB_STATUS)
                        || (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                                .equals(CloudStackConstants.PROGRESS_JOB_STATUS))) {
                    network.setNetworkRestart(true);
                } else {
                    JSONObject jobresponse = jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                    if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                            .equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                        if (jobresponse.has(CloudStackConstants.CS_ERROR_CODE)) {
                            errors = this.validateEvent(errors,
                                    jobresponse.getString(CloudStackConstants.CS_ERROR_TEXT));
                            throw new ApplicationException(errors);
                        }
                    }
                }
            }
        }
        return networkRepo.save(network);
    }

    /**
     * Check resouce capacity to create new Volume.
     *
     * @param volume Volume.
     * @param optionalMap arguments.
     * @return error message.
     * @throws Exception unhandled errors.
     */
    public String isResourceAvailable(Network network, HashMap<String, String> optionalMap) throws Exception {
        Long resourceUsage = 0L, tempCount = 0L;
        String errMessage = null;
        // 1. Initiate CS server connection as ROOT admin.
        config.setServer(1L);
        // 2. List capacity CS API call.
        String csResponse = cloudStackResourceCapacity.listCapacity(optionalMap, CloudStackConstants.JSON);
        JSONObject csCapacity = new JSONObject(csResponse).getJSONObject(CloudStackConstants.CS_CAPACITY_LIST_RESPONSE);
        if (csCapacity.has(CloudStackConstants.CS_CAPACITY)) {
            JSONArray capacityArrayJSON = csCapacity.getJSONArray(CloudStackConstants.CS_CAPACITY);
            for (int i = 0, size = capacityArrayJSON.length(); i < size; i++) {
                String resourceType = capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CAPACITY_TYPE);
                // 2.1 Total capacity in public pool for each resource type.
                Long tempTotalCapacity = Long
                        .valueOf(capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CS_CAPACITY_TOTAL));
                // 2.2 Used capacity in public pool for each resource type.
                Long tempCapacityUsed = Long
                        .valueOf(capacityArrayJSON.getJSONObject(i).getString(CloudStackConstants.CS_CAPACITY_USED));
                if (GenericConstants.RESOURCE_CAPACITY.containsKey(resourceType)) {
                    // 3.1 Total available resource in public pool for each
                    // resource type.
                    resourceUsage = tempTotalCapacity - tempCapacityUsed;
                    // 4. Check whether resource is available to create new
                    // Volume
                    // with resource type.
                    switch (resourceType) {
                    // 4.5 Check public ip address availability.
                    case GenericConstants.RESOURCE_IP_ADDRESS:
                        optionalMap.put(CloudStackConstants.CS_ASSOCIATE_NETWORK, network.getUuid());
                        optionalMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
                        optionalMap.put(CloudStackConstants.CS_FOR_VM_NETWORK, CloudStackConstants.STATUS_ACTIVE);
                        config.setServer(1L);
                        String csIpResponse = cloudStackResourceCapacity.listPublicIpAddress(optionalMap,
                                CloudStackConstants.JSON);
                        JSONObject csIpCapacity = new JSONObject(csIpResponse)
                                .getJSONObject(CloudStackConstants.CS_PUBLIC_IPADDRESS_RESPONSE);
                        if (csIpCapacity.has(CloudStackConstants.CS_CAPACITY_COUNT)) {
                            LOGGER.debug("Already IP address acquired ", resourceType);
                        } else if (resourceUsage < 1) {
                            errMessage = CloudStackConstants.RESOURCE_CHECK + " public.ip.available "
                                    + CloudStackConstants.CONTACT_CLOUD_ADMIN;
                        }
                        break;
                    default:
                        LOGGER.debug("No Resource ", resourceType);
                    }
                }
            }
        }
        // 5. If any resource shortage then return error message otherwise
        // return empty string.
        return errMessage;
    }

    @Override
    public Page<Network> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return networkRepo.findAllByDomainIdAndIsActive(domainId, true, pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Network> findAllByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception {
        return networkRepo.findAllByDomainAndIsActive(domainId, isActive);
    }


    @Override
    public List<Network> findAllByUserId(Long userId) throws Exception {
       User user = convertEntityService.getOwnerById(userId);
       // Check the user is not a root and admin and set the domain value from login detail
       if (user.getType().equals(User.UserType.ROOT_ADMIN)) {
           return networkRepo.findAllByIsActiveWihtoutPaging(true);
       }
       if (user.getType().equals(User.UserType.DOMAIN_ADMIN)) {
           return networkRepo.findAllByDomainIsActive(true, user.getDomainId());
       }
       List<Network> network = this.getNetworkListByUserWihtoutPaging(userId);
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
    private List<Network> getNetworkListByUserWihtoutPaging(Long userId) throws  Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
            List<Project> allProjectList = projectService.findAllByUserAndIsActive(user.getId(), true);
            List<Network> projectNetwork = networkRepo.findAByProjectDepartmentAndIsActiveWithoutPaging(allProjectList,
                    user.getDepartmentId(), true);
            return projectNetwork;
        } else {
            return networkRepo.findByDepartment(user.getDepartmentId(), true);
        }
    }

    @Override
    public List<Network> findAllByDomainId(Long domainId) throws Exception {
        return networkRepo.findAllByDomainIsActive(true, domainId);
    }

      @Override
        public Page<Network> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText)
                throws Exception {
          Page<Network> networks = null ;
              User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
              if (convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.ROOT_ADMIN)) {
                  networks = networkRepo.findByDomainIsActiveAndSearchText(domainId, true, pagingAndSorting.toPageRequest(),searchText);
              } else if (convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.DOMAIN_ADMIN)) {
                  domainId = user.getDomainId();
                  networks = networkRepo.findByDomainIsActiveAndSearchText(domainId, true, pagingAndSorting.toPageRequest(),searchText);
              }
              else if (convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.USER)) {
                     if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                  List<Project> allProjectList = projectService.findAllByUserAndIsActive(user.getId(), true);
                  Page<Network> projectNetwork = networkRepo.findByProjectDepartmentAndIsActiveWithPagingAndSorting(allProjectList,
                          user.getDepartmentId(),true, pagingAndSorting.toPageRequest(),searchText,user.getDomainId());
                  networks = projectNetwork;
              } else {
                  networks = networkRepo.findByDomainIdDepartmentIsActiveAndSearchText(user.getDomainId(), true, pagingAndSorting.toPageRequest(),searchText, user.getDepartmentId());
              }
           }
         return networks;
      }

    @Override
    public List<Network> findNetworkByVpcIdAndIsActive(Long vpcId, Boolean isActive) throws Exception {
        return networkRepo.findNetworkByVpcIdAndIsActive(vpcId, isActive);
    }

    @Override
    public List<Network> findNetworkByVpcIdAndIsActiveAndType(Long vpcId, Boolean isActive, String type) throws Exception {
        Long serviceId = supportedNetworkService.findByName(type).getId();
        return networkRepo.findNetworkByVpcIdAndIsActiveAndType(vpcId, isActive, serviceId);
    }

    @Override
    @PreAuthorize("hasPermission(#network.getSyncFlag(), 'EDIT_NETWORK')")
    public Network replaceAcl(Network network) throws Exception {
        if (network.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(NETWORK, network);
            errors = validator.validateEntity(network, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put(CloudStackConstants.CS_NETWORK_ID, network.getUuid());
                config.setUserServer();
                String updateNetworkResponse = csNetwork.replaceNetworkACLList(network.getAcl().getUuid(), optional, CloudStackConstants.JSON);
                JSONObject jobId = new JSONObject(updateNetworkResponse).getJSONObject("replacenetworkacllistresponse");
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    Thread.sleep(5000);
                    String jobResponse = csNetwork.networkJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                        Network persistNetwork = networkRepo.findByUUID(network.getUuid());
                        persistNetwork.setAclId(network.getAcl().getId());
                        return networkRepo.save(persistNetwork);
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

}
