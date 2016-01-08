package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.IpAddress.State;
import ck.panda.domain.entity.Network;
import ck.panda.domain.repository.jpa.IpaddressRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAddressService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

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
    public IpAddress save(IpAddress ipAddress) throws Exception {
        if (ipAddress.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("egressFirewallRule", ipAddress);
            errors = validator.validateEntity(ipAddress, errors);
            configServer.setUserServer();
            Network network = convertEntityService.getNetworkById(ipAddress.getNetworkId());
            HashMap<String, String> ipMap = new HashMap<String, String>();
            ipMap.put("domainid",
                    domainService.find(Long.parseLong(tokenDetails.getTokenDetails("domainid"))).getUuid());
            if (network.getProjectId() != null) {
                ipMap.put("projectid", convertEntityService.getProjectById(network.getProjectId()).getUuid());

            } else {
                ipMap.put("account", departmentService
                        .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName());
            }
            String associatedResponse = csipaddressService.associateIpAddress("json", ipMap);
            JSONObject csassociatedIPResponseJSON = new JSONObject(associatedResponse)
                    .getJSONObject("associateipaddressresponse");
            if (csassociatedIPResponseJSON.has("errorcode")) {
                errors = this.validateEvent(errors, csassociatedIPResponseJSON.getString("errortext"));
                throw new ApplicationException(errors);
            } else if (csassociatedIPResponseJSON.has("jobid")) {
                String jobResponse = csipaddressService
                        .associatedJobResult(csassociatedIPResponseJSON.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString("jobstatus").equals("1")) {
                    ipAddress.setUuid((String) csassociatedIPResponseJSON.get("id"));
                }
            }
        }
        return ipRepo.save(ipAddress);
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
        ipaddress.setIsActive(false);
          return ipRepo.save(ipaddress);
    }

    @Override
    public List<IpAddress> findByNetwork(Long networkId) throws Exception {
        return null;
    }

    @Override
    public List<IpAddress> findAllFromCSServer() throws Exception {
        List<IpAddress> ipList = new ArrayList<IpAddress>();
        HashMap<String, String> ipMap = new HashMap<String, String>();
        ipMap.put("listall", "true");
        ipMap.put("allocatedonly", "false");
        // 1. Get the list of ipAddress from CS server using CS connector
        String response = csipaddressService.listPublicIpAddresses("json", ipMap);
        JSONArray ipAddressListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listpublicipaddressesresponse");
        if (responseObject.has("publicipaddress")) {
            ipAddressListJSON = responseObject.getJSONArray("publicipaddress");
            // 2. Iterate the json list, convert the single json entity to pod
            for (int i = 0, size = ipAddressListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to ipAddress entity
                // and
                // Add
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
        return ipRepo.findAllByIsActive(pagingAndSorting.toPageRequest(),true);
    }

    /**
     * Check the IP address CS error handling.
     *
     * @param errors
     *            error creating status.
     * @param errmessage
     *            error message.
     * @return errors.
     * @throws Exception
     *             if error occurs.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    @Override
    public IpAddress dissocitateIpAddress(IpAddress ipAddress) throws Exception {
        ipAddress.setIsActive(false);
        ipAddress.setState(State.FREE);
        configServer.setUserServer();
        String deleteResponse = csipaddressService.disassociateIpAddress(ipAddress.getUuid(), "json");
        JSONObject jobId = new JSONObject(deleteResponse).getJSONObject("disassociateipaddressresponse");
         if (jobId.has("jobid")) {
             String jobResponse = csipaddressService.associatedJobResult(jobId.getString("jobid"), "json");
             JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
         }
        return ipRepo.save(ipAddress);
    }

}
