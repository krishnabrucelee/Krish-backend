package ck.panda.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.IpAddress.State;
import ck.panda.domain.entity.IpAddress.VpnState;
import ck.panda.domain.entity.ResourceLimitDepartment.ResourceType;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.repository.jpa.IpaddressRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CSVPNService;
import ck.panda.util.CloudStackAddressService;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.ConfigUtil;
import ck.panda.util.EncryptionUtil;
import ck.panda.util.JsonUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;

/**
 * IpAddress service implementation class.
 */
@Service
public class IpaddressServiceImpl implements IpaddressService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(IpaddressServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private IpaddressRepository ipRepo;

    /** CloudStack IP address service for connectivity with cloudstack. */
    @Autowired
    private CloudStackAddressService csipaddressService;

    /** CloudStack VPN service for connectivity with cloudstack. */
    @Autowired
    private CSVPNService csVPNService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Sync Service reference. */
    @Autowired
    private SyncService syncService;

    /** CloudStack connector reference for resource capacity. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Quota limit validation reference. */
    @Autowired
    private QuotaValidationService quotaLimitValidation;

    /** Secret key value is append. */
    @Value(value = "${aes.salt.secretKey}")
    private String secretKey;

    /** Constant for action event configuration in progress. */
    public static final String CS_EVENT_RUNNING = "VPN remote access configuration in progress";

    /** Constant for action event reset in progress. */
    public static final String CS_EVENT_REMOVE = "VPN remote access reset in progress";

    /** Constant for virtual router stopped error message. */
    public static final String CS_ROUTER_STOPPED_ERROR = "Failed to start remote access VPN: router not in right state Stopped";

    /** Constant for action event running status. */
    public static final String CS_RUNNING_STATE = "Running";

    /** Constant for Allocated only. */
    public static final String CS_ALLOCATED_ONLY = "allocatedonly";

    /** Constant for Associated network id. */
    public static final String CS_ASSOCIATED_NETWORK_ID = "associatednetworkid";

    /** Constant for True status. */
    public static final String CS_TRUE = "true";

    /** Constant for Source nat. */
    public static final String CS_IS_SOURCE_NAT = "issourcenat";

    @Override
    public List<IpAddress> acquireIP(Long networkId) throws Exception {
        Errors errors = null;
        HashMap<String, String> optionalMap = new HashMap<String, String>();
        optionalMap.put(CloudStackConstants.CS_ZONE_ID,
                convertEntityService.getZoneById(convertEntityService.getNetworkById(networkId).getZoneId()).getUuid());
        // check department and project quota validation.
        ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService.findByDepartmentAndResourceType(
                convertEntityService.getNetworkById(networkId).getDepartmentId(), ResourceType.Instance, true);
        if (departmentLimit != null && convertEntityService
                .getDepartmentById(convertEntityService.getNetworkById(networkId).getDepartmentId()).getType()
                .equals(AccountType.USER)) {
            if (convertEntityService.getNetworkById(networkId).getProjectId() != null) {
                syncService.syncResourceLimitProject(convertEntityService
                        .getProjectById(convertEntityService.getNetworkById(networkId).getProjectId()));
                quotaLimitValidation.QuotaLimitCheckByResourceObject(convertEntityService.getNetworkById(networkId),
                        "IP", convertEntityService.getNetworkById(networkId).getProjectId(), "Project");
            }
            if (convertEntityService.getNetworkById(networkId).getDepartmentId() != null) {
                quotaLimitValidation.QuotaLimitCheckByResourceObject(convertEntityService.getNetworkById(networkId),
                        "IP", convertEntityService.getNetworkById(networkId).getDepartmentId(), "Department");
            }
            if (convertEntityService.getNetworkById(networkId).getDomainId() != null) {
                quotaLimitValidation.QuotaLimitCheckByResourceObject(convertEntityService.getNetworkById(networkId),
                        "IP", convertEntityService.getNetworkById(networkId).getDomainId(), "Domain");
            }
            // 3. Check the resource availability to acquire new ip.
            String isAvailable = isResourceAvailable(convertEntityService.getNetworkById(networkId), optionalMap);
            if (isAvailable != null) {
                // 3.1 throws error message about resource shortage.
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, isAvailable);
            } else {
                try {
                    // updateResourceForIpCreation(convertEntityService.getNetworkById(networkId),
                    // errors);
                    configServer.setUserServer();
                    Network network = convertEntityService.getNetworkById(networkId);
                    HashMap<String, String> ipMap = new HashMap<String, String>();
                    ipMap.put("domainid", network.getDomain().getUuid());
                    if (network.getProjectId() != null) {
                        ipMap.put("projectid", convertEntityService.getProjectById(network.getProjectId()).getUuid());
                    } else {
                        ipMap.put("account",
                                departmentService
                                        .find(convertEntityService.getDepartmentById(network.getDepartmentId()).getId())
                                        .getUserName());
                    }
                    ipMap.put("zoneid", network.getZone().getUuid());
                    ipMap.put("networkid", network.getUuid());
                    String associatedResponse = csipaddressService.associateIpAddress("json", ipMap);
                    JSONObject csassociatedIPResponseJSON = new JSONObject(associatedResponse)
                            .getJSONObject("associateipaddressresponse");
                    if (csassociatedIPResponseJSON.has("errorcode")) {
                        errors = validator.sendGlobalError(csassociatedIPResponseJSON.getString("errortext"));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(csassociatedIPResponseJSON.getString("errortext"));
                        }
                    } else if (csassociatedIPResponseJSON.has("jobid")) {
                        String jobResponse = csipaddressService
                                .associatedJobResult(csassociatedIPResponseJSON.getString("jobid"), "json");
                        JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                        List<IpAddress> iplist = new ArrayList<IpAddress>();
                        iplist.add(ipRepo.findByUUID(csassociatedIPResponseJSON.getString("id")));
                        return iplist;
                        }
                    return (List<IpAddress>) ipRepo.findByNetwork(networkId, IpAddress.State.ALLOCATED);
                } catch (ApplicationException e) {
                    LOGGER.error("ERROR AT IP AQUIRE", e);
                    throw new ApplicationException(e.getErrors());
                }
            }
        } else {
            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, "Resource limit for department has not been set. Please update department quota");
        }
    }

    @Override
    public IpAddress update(IpAddress ipAddress) throws Exception {
        return ipRepo.save(ipAddress);
    }

    @Override
    public void delete(IpAddress ipAddress) throws Exception {
        ipRepo.delete(ipAddress);
    }

    @Override
    public void delete(Long id) throws Exception {
        ipRepo.delete(id);
    }

    @Override
    public IpAddress find(Long id) throws Exception {
        IpAddress ipAddress = ipRepo.findOne(id);
        return ipAddress;
    }

    @Override
    public Page<IpAddress> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return ipRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<IpAddress> findAll() throws Exception {
        return (List<IpAddress>) ipRepo.findAll();
    }

    @Override
    public IpAddress findbyUUID(String uuid) throws Exception {
        return ipRepo.findByUUID(uuid);
    }

    @Override
    public IpAddress softDelete(IpAddress ipaddress) throws Exception {
        if (!ipaddress.getSyncFlag()) {
            ipaddress.setIsActive(false);
            ipaddress.setState(IpAddress.State.FREE);
        } else {
            ipaddress = this.dissocitateIpAddress(ipaddress.getUuid());
        }
        return ipRepo.save(ipaddress);
    }

    @Override
    public List<IpAddress> findByNetwork(Long networkId) throws Exception {
        return ipRepo.findByNetwork(networkId, State.ALLOCATED);
    }

    @Override
    public Page<IpAddress> findByNetwork(Long networkId, PagingAndSorting pagingAndSorting) throws Exception {
        return ipRepo.findByNetwork(pagingAndSorting.toPageRequest(), networkId, State.ALLOCATED);
    }

    @Override
    public List<IpAddress> findAllFromCSServer() throws Exception {
        List<IpAddress> ipList = new ArrayList<IpAddress>();
        HashMap<String, String> ipMap = new HashMap<String, String>();
        ipMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        ipMap.put("allocatedonly", "false");
        configServer.setServer(1L);
        // 1. Get the list of ipAddress from CS server using CS connector
        String response = csipaddressService.listPublicIpAddresses(CloudStackConstants.JSON, ipMap);
        JSONArray ipAddressListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CloudStackConstants.CS_PUBLIC_IPADDRESS_RESPONSE);
        if (responseObject.has(CloudStackConstants.CS_PUBLIC_IP_ADDRESS)) {
            ipAddressListJSON = responseObject.getJSONArray(CloudStackConstants.CS_PUBLIC_IP_ADDRESS);
            // 2. Iterate the json list, convert the single json entity to pod
            for (int i = 0, size = ipAddressListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to ipAddress entity
                // the converted pod entity to list
                IpAddress ipAddress = IpAddress.convert(ipAddressListJSON.getJSONObject(i));
                ipAddress.setDomainId(convertEntityService.getDomainId(ipAddress.getTransDomainId()));
                ipAddress.setZoneId(convertEntityService.getZoneId(ipAddress.getTransZoneId()));
                ipAddress.setNetworkId(convertEntityService.getNetworkId(ipAddress.getTransNetworkId()));
                ipAddress.setProjectId(convertEntityService.getProjectId(ipAddress.getTransProjectId()));

                //Get all the VPN details
                HashMap<String, String> vpnOptional = new HashMap<String, String>();
                vpnOptional.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
                String vpnResponse = csVPNService.listRemoteAccessVpns(ipAddress.getUuid(), vpnOptional, CloudStackConstants.JSON);
                JSONArray vpnRemoteListJSON = null;
                JSONObject responseVpnObject = new JSONObject(vpnResponse).getJSONObject(CloudStackConstants.CS_REMOTE_ACCESS_VPN_RESPONSE);
                if (responseVpnObject.has(CloudStackConstants.CS_REMOTE_ACCESS_VPN)) {
                    vpnRemoteListJSON = responseVpnObject.getJSONArray(CloudStackConstants.CS_REMOTE_ACCESS_VPN);
                    for (int j = 0; j < vpnRemoteListJSON.length(); j++) {
                        ipAddress.setVpnUuid(JsonUtil.getStringValue(vpnRemoteListJSON.getJSONObject(j), CloudStackConstants.CS_ID));
                        ipAddress.setVpnPresharedKey(convertEncryptedKey(JsonUtil.getStringValue(vpnRemoteListJSON.getJSONObject(j), CloudStackConstants.CS_PRESHARED_KEY)));
                        ipAddress.setVpnState(VpnState.valueOf(JsonUtil.getStringValue(vpnRemoteListJSON.getJSONObject(j), CloudStackConstants.CS_STATE).toUpperCase()));
                        ipAddress.setVpnForDisplay(JsonUtil.getBooleanValue(vpnRemoteListJSON.getJSONObject(j), CloudStackConstants.CS_FOR_DISPLAY));
                    }
                }
                ipList.add(ipAddress);
            }
        }
        return ipList;
    }


	@Override
	public IpAddress UpdateIPByNetwork(String networkId) throws Exception {
		IpAddress publicIpAddress = new IpAddress();
        HashMap<String, String> ipMap = new HashMap<String, String>();
        ipMap.put(CS_ALLOCATED_ONLY, CS_TRUE);
        ipMap.put(CS_ASSOCIATED_NETWORK_ID, networkId);
        ipMap.put(CS_IS_SOURCE_NAT, CS_TRUE);
        configServer.setServer(1L);
        Network network = convertEntityService.getNetworkById(convertEntityService.getNetworkByUuid(networkId));
		if (network.getProjectId() != null) {
            ipMap.put(CloudStackConstants.CS_PROJECT_ID, convertEntityService.getProjectById(network.getProjectId()).getUuid());
		} else {
            ipMap.put(CloudStackConstants.CS_LIST_ALL, CS_TRUE);
		}
        // 1. Get the list of ipAddress from CS server using CS connector
        String response = csipaddressService.listPublicIpAddresses(CloudStackConstants.JSON, ipMap);
        JSONArray ipAddressListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CloudStackConstants.CS_PUBLIC_IPADDRESS_RESPONSE);
        if (responseObject.has(CloudStackConstants.CS_PUBLIC_IP_ADDRESS)) {
            ipAddressListJSON = responseObject.getJSONArray(CloudStackConstants.CS_PUBLIC_IP_ADDRESS);
            // 2. Iterate the json list, convert the single json entity to pod
            for (int i = 0, size = ipAddressListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to ipAddress entity
                // the converted pod entity to list
                IpAddress ipAddress = IpAddress.convert(ipAddressListJSON.getJSONObject(i));
                ipAddress.setDomainId(convertEntityService.getDomainId(ipAddress.getTransDomainId()));
                ipAddress.setZoneId(convertEntityService.getZoneId(ipAddress.getTransZoneId()));
                ipAddress.setNetworkId(convertEntityService.getNetworkId(ipAddress.getTransNetworkId()));
                ipAddress.setProjectId(convertEntityService.getProjectId(ipAddress.getTransProjectId()));

                IpAddress ipAddresses = ipRepo.findByUUID(ipAddress.getUuid());
				if (ipAddresses != null) {
					ipAddresses.setUuid(ipAddress.getUuid());
					ipAddresses.setPublicIpAddress(ipAddress.getPublicIpAddress());
					ipAddresses.setState(ipAddress.getState());
					ipAddresses.setIsSourcenat(ipAddress.getIsSourcenat());
					ipAddresses.setIsStaticnat(ipAddress.getIsStaticnat());
					ipAddresses.setNetworkId(ipAddress.getNetworkId());
					ipAddresses.setDomainId(ipAddress.getDomainId());
					ipAddresses.setZoneId(ipAddress.getZoneId());
					ipAddresses.setProjectId(ipAddress.getProjectId());
					ipAddresses.setIsActive(true);
					publicIpAddress = ipRepo.save(ipAddresses);
				} else {
					publicIpAddress = ipRepo.save(ipAddress);
				}
            }
        }
        return publicIpAddress;
	}

    @Override
    public Page<IpAddress> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return ipRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    /**
     * Check the IP address CS error handling.
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

    @Override
    public IpAddress dissocitateIpAddress(String ipUuid) throws Exception {
        Errors errors = null;
        IpAddress ipAddress = findbyUUID(ipUuid);
        // TODO //check department and project quota validation.
        ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                .findByDepartmentAndResourceType(convertEntityService.getNetworkById(ipAddress.getNetworkId()).getDepartmentId(), ResourceType.Instance, true);

        if (departmentLimit != null) {
            if (convertEntityService.getNetworkById(ipAddress.getNetworkId()).getProjectId() != null) {
                syncService.syncResourceLimitProject(
                        convertEntityService.getProjectById(convertEntityService.getNetworkById(ipAddress.getNetworkId()).getProjectId()));
            }

        try {
            configServer.setUserServer();
            String deleteResponse = csipaddressService.disassociateIpAddress(ipUuid, "json");
            JSONObject jobId = new JSONObject(deleteResponse).getJSONObject("disassociateipaddressresponse");
            if (jobId.has("errorcode")) {
                errors = validator.sendGlobalError(jobId.getString("errortext"));
                if (errors.hasErrors()) {
                    throw new BadCredentialsException(jobId.getString("errortext"));
                }
            }
            if (jobId.has("jobid")) {
                String jobResponse = csipaddressService.associatedJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
            }
            ipAddress.setIsActive(false);
            ipAddress.setState(State.FREE);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return ipAddress;
        } else {
            errors.addGlobalError("Resource limit for department has not been set. Please update department quota");
            throw new ApplicationException(errors);
        }
    }

    @Override
    public IpAddress save(IpAddress ipAddress) throws Exception {
        return ipRepo.save(ipAddress);
    }

    @Override
    public IpAddress enableStaticNat(Long ipAddressId, Long vmId, String ipAddress) throws Exception {
        IpAddress ipaddress = ipRepo.findOne(ipAddressId);
        String vmid = null;
        if (convertEntityService.getVmInstanceById(vmId) != null) {
            ipaddress.setVmInstanceId(vmId);
            vmid = convertEntityService.getVmInstanceById(vmId).getUuid();
        }
        try {
            HashMap<String, String> ipMap = new HashMap<String, String>();
            configServer.setUserServer();
            ipMap.put("vmguestip", ipAddress);
            String enableResponse = csipaddressService.enableStaticNat(ipaddress.getUuid(), vmid, ipMap);
            JSONObject jobId = new JSONObject(enableResponse).getJSONObject("enablestaticnatresponse");
            if (jobId.has("errorcode")) {
                Errors errors = validator.sendGlobalError(jobId.getString("errortext"));
                if (errors.hasErrors()) {
                    throw new BadCredentialsException(jobId.getString("errortext"));
                }
            } else {
                ipaddress.setIsStaticnat(true);
            }
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return ipRepo.save(ipaddress);
    }

    @Override
    public IpAddress disableStaticNat(Long ipAddressId) throws Exception {
        IpAddress ipaddress = ipRepo.findOne(ipAddressId);
        try {
            configServer.setUserServer();
            String disableResponse = csipaddressService.disableStaticNat(ipaddress.getUuid());
            JSONObject jobId = new JSONObject(disableResponse).getJSONObject("disablestaticnatresponse");
            if (jobId.has("errorcode")) {
                Errors errors = validator.sendGlobalError(jobId.getString("errortext"));
                if (errors.hasErrors()) {
                    throw new BadCredentialsException(jobId.getString("errortext"));
                }
            }
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return ipaddress;
    }

    @Override
    public List<IpAddress> findByStateAndActive(State state, Boolean isActive) throws Exception {
        return ipRepo.findAllByIsActiveAndState(state, isActive);
    }

    /**
     * Check resouce capacity to create new Network.
     *
     * @param network Network.
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
                    // 4. Check whether resource is available to acquire new ip
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
                            errMessage = CloudStackConstants.RESOURCE_CHECK + " public.ip.available " + CloudStackConstants.CONTACT_CLOUD_ADMIN;
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
    public IpAddress enableRemoteAccessVpn(String uuid) throws Exception {
        Errors errors = null;
        IpAddress ipAddress = findbyUUID(uuid);
        try {
            configServer.setUserServer();
            Boolean routerStatus = virtualRoutersStatusCheck(ipAddress.getNetwork().getDomain().getUuid(),
                    ipAddress.getNetwork().getDepartment().getUserName(), ipAddress.getNetwork().getUuid());

            if (routerStatus) {
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put(CloudStackConstants.CS_DOMAIN_ID, ipAddress.getNetwork().getDomain().getUuid());
                optional.put(CloudStackConstants.CS_ACCOUNT, ipAddress.getNetwork().getDepartment().getUserName());

                String createRemoteAccess = csVPNService.createRemoteAccessVpn(ipAddress.getUuid(), optional, CloudStackConstants.JSON);
                JSONObject jobId = new JSONObject(createRemoteAccess).getJSONObject(CloudStackConstants.CS_CREATE_REMOTE_ACCESS_VPN);
                if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
                    errors = validator.sendGlobalError(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                    if (errors.hasErrors()) {
                        throw new BadCredentialsException(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                    }
                }
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    String jobResponse = csipaddressService.associatedJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);

                    if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS)
                            .equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                        JSONObject jobresultReponse = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE)
                            .getJSONObject(CloudStackConstants.CS_JOB_RESULT).getJSONObject(CloudStackConstants.CS_REMOTE_ACCESS_VPN);

                        ipAddress.setVpnUuid(jobresultReponse.getString(CloudStackConstants.CS_ID));
                        ipAddress.setVpnPresharedKey(convertEncryptedKey(jobresultReponse.getString(CloudStackConstants.CS_PRESHARED_KEY)));
                        ipAddress.setVpnState(VpnState.valueOf(jobresultReponse.getString(CloudStackConstants.CS_STATE).toUpperCase()));
                        ipAddress.setVpnForDisplay(jobresultReponse.getBoolean(CloudStackConstants.CS_FOR_DISPLAY));
                    } else if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                        errors = validator.sendGlobalError(CS_EVENT_RUNNING);
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(CS_EVENT_RUNNING);
                        }
                    } else if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                        if (jobresults.has(CloudStackConstants.CS_JOB_RESULT)) {
                            errors = validator.sendGlobalError(jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                            if (errors.hasErrors()) {
                                throw new BadCredentialsException(jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                            }
                        }
                    }
                }
            } else {
                throw new BadCredentialsException(CS_ROUTER_STOPPED_ERROR);
            }
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return ipRepo.save(ipAddress);
    }

    /**
     * Check the virtual router is running/stopped.
     *
     * @param domainId domain id of the router
     * @param accountName account name of the router
     * @param networkId network id of the router
     * @return encrypted value
     * @throws Exception unhandled errors.
     */
    private Boolean virtualRoutersStatusCheck(String domainId, String accountName, String networkId) throws Exception {
        Boolean routerState = false;
        JSONArray routerListJSON = null;
        HashMap<String, String> routerOptional = new HashMap<String, String>();
        routerOptional.put(CloudStackConstants.CS_DOMAIN_ID, domainId);
        routerOptional.put(CloudStackConstants.CS_ACCOUNT, accountName);
        routerOptional.put(CloudStackConstants.CS_NETWORK_ID, networkId);
        String listRouters = csVPNService.listRouters(routerOptional, CloudStackConstants.JSON);
        JSONObject responseObject = new JSONObject(listRouters).getJSONObject(CloudStackConstants.CS_LIST_ROUTER_RESPONSE);
        if (responseObject.has(CloudStackConstants.CS_ROUTER)) {
            routerListJSON = responseObject.getJSONArray(CloudStackConstants.CS_ROUTER);
            if (JsonUtil.getStringValue(routerListJSON.getJSONObject(0), CloudStackConstants.CS_STATE).equals(CS_RUNNING_STATE)) {
                routerState = true;
            }
        }
        return routerState;
    }

    @Override
    public IpAddress disableRemoteAccessVpn(String uuid) throws Exception {
        Errors errors = null;
        IpAddress ipAddress = findbyUUID(uuid);
        try {
            configServer.setUserServer();

            String createRemoteAccess = csVPNService.deleteRemoteAccessVpn(ipAddress.getUuid(), CloudStackConstants.JSON);
            JSONObject jobId = new JSONObject(createRemoteAccess).getJSONObject(CloudStackConstants.CS_DELETE_REMOTE_ACCESS_VPN);
            if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = validator.sendGlobalError(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                if (errors.hasErrors()) {
                    throw new BadCredentialsException(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                }
            }
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = csipaddressService.associatedJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);

                if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                    ipAddress.setVpnState(VpnState.DISABLED);
                } else if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                    errors = validator.sendGlobalError(CS_EVENT_REMOVE);
                    if (errors.hasErrors()) {
                        throw new BadCredentialsException(CS_EVENT_REMOVE);
                    }
                } else if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                    if (jobresults.has(CloudStackConstants.CS_JOB_RESULT)) {
                        errors = validator.sendGlobalError(jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                        if (errors.hasErrors()) {
                            throw new BadCredentialsException(jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                        }
                    }
                }
            }
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException(e.getMessage());
        }
        return ipRepo.save(ipAddress);
    }

    /**
     * Convert key value as encrypted format.
     *
     * @param value secret value.
     * @return encrypted value
     * @throws Exception unhandled errors.
     */
    private String convertEncryptedKey(String value) throws Exception {
        // Set password from CS for an instance with AES encryption.
        String encryptedValue = "";
        if (value != null) {
            String strEncoded = Base64.getEncoder().encodeToString(secretKey.getBytes(GenericConstants.CHARACTER_ENCODING));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, GenericConstants.ENCRYPT_ALGORITHM);
            encryptedValue = new String(EncryptionUtil.encrypt(value, originalKey));
        }
        return encryptedValue;
    }

    @Override
    public IpAddress findByVpnKey(Long id) throws Exception {
        IpAddress ipAddress = ipRepo.findOne(id);
        if (ipAddress.getVpnPresharedKey() != null) {
            String strEncoded = Base64.getEncoder()
                    .encodeToString(secretKey.getBytes(GenericConstants.CHARACTER_ENCODING));
            byte[] decodedKey = Base64.getDecoder().decode(strEncoded);
            SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length,
                    GenericConstants.ENCRYPT_ALGORITHM);
            String decryptPassword = new String(EncryptionUtil.decrypt(ipAddress.getVpnPresharedKey(), originalKey));
            ipAddress.setVpnPresharedKey(decryptPassword);
        }
        return ipAddress;
    }

}
