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
import ck.panda.domain.entity.FirewallRules;
import ck.panda.domain.entity.FirewallRules.TrafficType;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.IpAddress.State;
import ck.panda.domain.entity.Network;
import ck.panda.domain.repository.jpa.EgressRuleRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackFirewallService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Egress Firewall Rule service Implemetation.
 *
 */
@Service
public class EgressRuleServiceImpl implements EgressRuleService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(EgressRuleServiceImpl.class);

    /** Egress Rule repository reference. */
    @Autowired
    private EgressRuleRepository egressRepo;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Cloud stack firewall service. */
    @Autowired
    private CloudStackFirewallService cloudStackFirewallService;

    /** Reference for Network Service . */
    @Autowired
    private NetworkService networkService;

    /** Reference for Ipaddress Service . */
    @Autowired
    private IpaddressService ipaddressService;

    /**
     * CloudStack FirewallRules service for getting egressFirewallRule connectivity with cloudstack.
     */
    @Autowired
    private CloudStackFirewallService csEgressService;

    @Override
    public FirewallRules save(FirewallRules egressFirewallRule) throws Exception {
        if (egressFirewallRule.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("egressFirewallRule", egressFirewallRule);
            errors = validator.validateEntity(egressFirewallRule, errors);
            configServer.setUserServer();
            Network network = convertEntityService.getNetworkById(egressFirewallRule.getNetworkId());
            HashMap<String, String> egressMap = new HashMap<String, String>();
            if (egressFirewallRule.getStartPort() != null) {
                egressMap.put("startport", egressFirewallRule.getStartPort().toString());
            }
            if (egressFirewallRule.getEndPort() != null) {
                egressMap.put("endport", egressFirewallRule.getEndPort().toString());
            }
            if (egressFirewallRule.getSourceCIDR() != null) {
                egressMap.put("cidrlist", egressFirewallRule.getSourceCIDR().toString());
            }
            if (egressFirewallRule.getIcmpCode() != null) {
                egressMap.put("icmpcode", egressFirewallRule.getIcmpCode().toString());
            }
            if (egressFirewallRule.getIcmpMessage() != null) {
                egressMap.put("icmptype", egressFirewallRule.getIcmpMessage().toString());
            }
            String createEgressResponse = csEgressService.createEgressFirewallRule(network.getUuid(),
                    egressFirewallRule.getProtocol().toString(), "json", egressMap);
            JSONObject csegressResponseJSON = new JSONObject(createEgressResponse)
                    .getJSONObject("createegressfirewallruleresponse");
            if (csegressResponseJSON.has("errorcode")) {
                errors = this.validateEvent(errors, csegressResponseJSON.getString("errortext"));
                throw new ApplicationException(errors);
            } else if (csegressResponseJSON.has("jobid")) {
                String jobResponse = csEgressService.firewallJobResult(csegressResponseJSON.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                egressFirewallRule.setUuid((String) csegressResponseJSON.get("id"));
                if (jobresult.getString("jobstatus").equals("1")) {
                    egressFirewallRule.setSourceCIDR((String) csegressResponseJSON.get("cidrlist"));
                    egressFirewallRule.setStartPort((Integer) csegressResponseJSON.get("startport"));
                    egressFirewallRule.setEndPort((Integer) csegressResponseJSON.get("endport"));
                }
            }
            egressFirewallRule.setIsActive(true);
        }
        return egressRepo.save(egressFirewallRule);
    }

    @Override
    public FirewallRules update(FirewallRules egressFirewallRule) throws Exception {
        if (egressFirewallRule.getSyncFlag()) {
        return egressRepo.save(egressFirewallRule);
        }
        return egressRepo.save(egressFirewallRule);
    }

    @Override
    public void delete(FirewallRules egressFirewallRule) throws Exception {
        egressRepo.delete(egressFirewallRule);
    }

    @Override
    public void delete(Long id) throws Exception {
        egressRepo.delete(id);
    }

    @Override
    public FirewallRules find(Long id) throws Exception {
        return egressRepo.findOne(id);
    }

    @Override
    public Page<FirewallRules> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return egressRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<FirewallRules> findAll() throws Exception {
        return (List<FirewallRules>) egressRepo.findAll();
    }

    /**
     * Check the egress firewall CS error handling.
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
    public FirewallRules softDelete(FirewallRules egressFirewallRule) throws Exception {
        egressFirewallRule.setIsActive(false);
        if (egressFirewallRule.getSyncFlag()) {
            configServer.setUserServer();
            String deleteResponse = csEgressService.deleteEgressFirewallRule(egressFirewallRule.getUuid(), "json");
            JSONObject jobId = new JSONObject(deleteResponse).getJSONObject("deleteegressfirewallruleresponse");
            if (jobId.has("jobid")) {
                String jobResponse = csEgressService.firewallJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
            }
        }
        return egressRepo.save(egressFirewallRule);
    }

    @Override
    public FirewallRules findByUUID(String uuid) throws Exception {
        return egressRepo.findByUUID(uuid);
    }

    @Override
    public List<FirewallRules> findAllFromCSServer() throws Exception {
        List<Network> networkList = networkService.findAllByActive(true);
        List<FirewallRules> egressList = new ArrayList<FirewallRules>();
        LOGGER.debug("Network size" + networkList.size());
        for (Network net : networkList) {
            HashMap<String, String> egressMap = new HashMap<String, String>();
            egressMap.put("networkid", net.getUuid());
            egressMap.put("listall", "true");
            // 1. Get the list of nics from CS server using CS connector
            String response = csEgressService.listEgressFirewallRules("json", egressMap);
            JSONObject listJSON = new JSONObject(response).getJSONObject("listegressfirewallrulesresponse");
            if (response != null && listJSON.has("firewallrule")) {
                JSONArray egressListJSON = listJSON.getJSONArray("firewallrule");
                // 2. Iterate the json list, convert the single json entity to nic
                for (int i = 0, size = egressListJSON.length(); i < size; i++) {
                    // 2.1 Call convert by passing JSONObject to nic entity and Add
                    // the converted nic entity to list
                    FirewallRules egress = FirewallRules.convert(egressListJSON.getJSONObject(i), TrafficType.EGRESS, FirewallRules.Purpose.FIREWALL);
                    egress.setNetworkId(convertEntityService.getNetworkByUuid(egress.getTransNetworkId()));
                    egress.setDepartmentId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(egress.getTransNetworkId()))
                            .getDepartmentId());
                    egress.setProjectId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(egress.getTransNetworkId()))
                            .getProjectId());
                    egress.setDomainId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(egress.getTransNetworkId()))
                            .getDomainId());
                    egressList.add(egress);
                }
            }
        }
        return egressList;
    }

    @Override
    public Page<FirewallRules> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return egressRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<FirewallRules> findAllByTraffictypeAndNetwork(PagingAndSorting pagingAndSorting,  Long networkId, TrafficType trafficType) throws Exception {
        return egressRepo.findAllByTraffictypeAndNetworkAndIsActive(pagingAndSorting.toPageRequest(), trafficType, networkId, true);
    }

    @Override
    public Page<FirewallRules> findAllByTraffictypeAndIpAddress(PagingAndSorting pagingAndSorting,  Long ipaddressId, TrafficType trafficType) throws Exception {
        return egressRepo.findAllByTraffictypeAndIpaddressAndIsActive(pagingAndSorting.toPageRequest(), trafficType, ipaddressId, true);
    }


    @Override
    public List<FirewallRules> findAllFromCSServerForIngress() throws Exception {
        List<IpAddress> ipList = ipaddressService.findByStateAndActive(State.ALLOCATED, true);
        List<FirewallRules> ingressList = new ArrayList<FirewallRules>();
        LOGGER.debug("Ip size" + ipList.size());
        for (IpAddress net : ipList) {
            HashMap<String, String> ingressMap = new HashMap<String, String>();
            ingressMap.put("ipaddressid", net.getUuid());
            ingressMap.put("listall", "true");
            configServer.setServer(1L);
            // 1. Get the list of firewalls from CS server using CS connector
            String response = csEgressService.listFirewallRules("json", ingressMap);
            JSONObject listJSON = new JSONObject(response).getJSONObject("listfirewallrulesresponse");
            if (response != null && listJSON.has("firewallrule")) {
                JSONArray ingressListJSON = listJSON.getJSONArray("firewallrule");
                // 2. Iterate the json list, convert the single json entity to firewalls
                for (int i = 0, size = ingressListJSON.length(); i < size; i++) {
                    // 2.1 Call convert by passing JSONObject to firewalls entity and Add
                    // the converted firewalls entity to list
                    FirewallRules ingress = FirewallRules.convert(ingressListJSON.getJSONObject(i), TrafficType.INGRESS, FirewallRules.Purpose.FIREWALL);
                    ingress.setNetworkId(convertEntityService.getNetworkByUuid(ingress.getTransNetworkId()));
                    ingress.setDepartmentId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(ingress.getTransNetworkId()))
                            .getDepartmentId());
                    ingress.setProjectId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(ingress.getTransNetworkId()))
                            .getProjectId());
                    ingress.setIpAddressId(ipaddressService.findbyUUID(ingress.getTransIpaddressId()).getId());
                    ingress.setDomainId(convertEntityService
                            .getNetworkById(convertEntityService.getNetworkByUuid(ingress.getTransNetworkId()))
                            .getDomainId());
                    ingressList.add(ingress);
                }
            }
        }
        return ingressList;
    }

    @Override
    public List<FirewallRules> findAllByTrafficType(TrafficType trafficType) throws Exception {
        return egressRepo.findByTrafficType(trafficType);
    }

    @Override
    public FirewallRules createFirewallIngressRule(FirewallRules ingressFirewallRule) throws Exception {
        if (ingressFirewallRule.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("egressFirewallRule", ingressFirewallRule);
            errors = validator.validateEntity(ingressFirewallRule, errors);
            configServer.setUserServer();
            Network network = convertEntityService.getNetworkById(ingressFirewallRule.getNetworkId());
            HashMap<String, String> ingressMap = new HashMap<String, String>();
            if (ingressFirewallRule.getStartPort() != null) {
                ingressMap.put("startport", ingressFirewallRule.getStartPort().toString());
            }
            if (ingressFirewallRule.getIcmpCode() != null) {
                ingressMap.put("icmpcode", ingressFirewallRule.getIcmpCode().toString());
            }
            if (ingressFirewallRule.getIcmpMessage() != null) {
                ingressMap.put("icmptype", ingressFirewallRule.getIcmpMessage().toString());
            }
            if (ingressFirewallRule.getEndPort() != null) {
                ingressMap.put("endport", ingressFirewallRule.getEndPort().toString());
            }
            if (ingressFirewallRule.getSourceCIDR() != null) {
                ingressMap.put("cidrlist", ingressFirewallRule.getSourceCIDR().toString());
            }
            IpAddress ipaddress = ipaddressService.find(ingressFirewallRule.getIpAddressId());
            String createIngressResponse = cloudStackFirewallService.createFirewallRule(ipaddress.getUuid(), ingressFirewallRule.getProtocol().toString().toLowerCase(), "json", ingressMap);
            JSONObject csingressResponseJSON = new JSONObject(createIngressResponse)
                    .getJSONObject("createfirewallruleresponse");
            if (csingressResponseJSON.has("errorcode")) {
                errors = this.validateEvent(errors, csingressResponseJSON.getString("errortext"));
                throw new ApplicationException(errors);
            } else if (csingressResponseJSON.has("jobid")) {
                String jobResponse = cloudStackFirewallService.firewallJobResult(csingressResponseJSON.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                ingressFirewallRule.setUuid((String) csingressResponseJSON.get("id"));
                if (jobresult.getString("jobstatus").equals("1")) {
                    ingressFirewallRule.setSourceCIDR((String) csingressResponseJSON.get("cidrlist"));
                    ingressFirewallRule.setStartPort((Integer) csingressResponseJSON.get("startport"));
                    ingressFirewallRule.setEndPort((Integer) csingressResponseJSON.get("endport"));
                }
            }
            ingressFirewallRule.setIsActive(true);
        }
        return egressRepo.save(ingressFirewallRule);
    }

    @Override
    public FirewallRules deleteFirewallIngressRule(FirewallRules ingressFirewallRule) throws Exception {
        ingressFirewallRule.setIsActive(false);
        if (ingressFirewallRule.getSyncFlag()) {
            configServer.setUserServer();
            String deleteResponse = cloudStackFirewallService.deleteFirewallRule(ingressFirewallRule.getUuid(), "json");
            JSONObject jobId = new JSONObject(deleteResponse).getJSONObject("deletefirewallruleresponse");
            if (jobId.has("jobid")) {
                String jobResponse = csEgressService.firewallJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
            }
        }
        return egressRepo.save(ingressFirewallRule);
    }

    @Override
    public List<FirewallRules> findAllByIpAddressAndIsActive(Long id, Boolean isActive) throws Exception {
        return egressRepo.findAllByIpAddressAndIsActive(id, true);
    }
}
