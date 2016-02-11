package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.IpAddress.State;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.repository.jpa.IpaddressRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAddressService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;

/**
 * IpAddress service implementation class.
 */
@Service
public class IpaddressServiceImpl implements IpaddressService {

    /** Department repository reference. */
    @Autowired
    private IpaddressRepository ipRepo;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackAddressService csipaddressService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Token Detail Utilities. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Domain service reference. */
    @Autowired
    private DomainService domainService;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    @Override
    public List<IpAddress> acquireIP(Long networkId) throws Exception {
        Errors errors = null;
        configServer.setUserServer();
        Network network = convertEntityService.getNetworkById(networkId);
        HashMap<String, String> ipMap = new HashMap<String, String>();
        ipMap.put("domainid", network.getDomain().getUuid());
        if (network.getProjectId() != null) {
            ipMap.put("projectid", convertEntityService.getProjectById(network.getProjectId()).getUuid());
        } else {
            ipMap.put("account", departmentService
                    .find(convertEntityService.getDepartmentById(network.getDepartmentId()).getId()).getUserName());
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
            String jobResponse = csipaddressService.associatedJobResult(csassociatedIPResponseJSON.getString("jobid"),
                    "json");
            JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
        }
        return (List<IpAddress>) ipRepo.findByNetwork(networkId, IpAddress.State.ALLOCATED);
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
        ipMap.put("listall", "true");
        configServer.setServer(1L);
        // 1. Get the list of ipAddress from CS server using CS connector
        String response = csipaddressService.listPublicIpAddresses("json", ipMap);
        JSONArray ipAddressListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listpublicipaddressesresponse");
        if (responseObject.has("publicipaddress")) {
            ipAddressListJSON = responseObject.getJSONArray("publicipaddress");
            // 2. Iterate the json list, convert the single json entity to pod
            for (int i = 0, size = ipAddressListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to ipAddress entity
                // the converted pod entity to list
                IpAddress ipAddress = IpAddress.convert(ipAddressListJSON.getJSONObject(i));
                ipAddress.setDomainId(convertEntityService.getDomainId(ipAddress.getTransDomainId()));
                ipAddress.setZoneId(convertEntityService.getZoneId(ipAddress.getTransZoneId()));
                ipAddress.setNetworkId(convertEntityService.getNetworkId(ipAddress.getTransNetworkId()));
                ipAddress.setProjectId(convertEntityService.getProjectId(ipAddress.getTransProjectId()));
                ipList.add(ipAddress);
            }
        }
        return ipList;
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
        IpAddress ipAddress = findbyUUID(ipUuid);
        try {
            configServer.setUserServer();
            String deleteResponse = csipaddressService.disassociateIpAddress(ipUuid, "json");
            JSONObject jobId = new JSONObject(deleteResponse).getJSONObject("disassociateipaddressresponse");
            if (jobId.has("errorcode")) {
                Errors errors = validator.sendGlobalError(jobId.getString("errortext"));
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
        return ipRepo.save(ipAddress);
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

}
