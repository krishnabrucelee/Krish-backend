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

    /** Reference for Network Service .*/
    @Autowired
    private NetworkService networkService;

    /**
     * CloudStack FirewallRules service for getting egressFirewallRule
     * connectivity with cloudstack.
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
        if (egressFirewallRule.getSourceCIDR()!= null) {
            egressMap.put("cidrlist", egressFirewallRule.getSourceCIDR().toString());
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
            if (jobresult.getString("jobstatus").equals("1")) {
                egressFirewallRule.setUuid((String) csegressResponseJSON.get("id"));
                egressFirewallRule.setSourceCIDR((String) csegressResponseJSON.get("cidrlist"));
                egressFirewallRule.setStartPort((Integer) csegressResponseJSON.get("startport"));
                egressFirewallRule.setEndPort((Integer) csegressResponseJSON.get("endport"));

            }
         }
      }
        return egressRepo.save(egressFirewallRule);
    }

    @Override
    public FirewallRules update(FirewallRules egressFirewallRule) throws Exception {
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
    public Page<FirewallRules> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return egressRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<FirewallRules> findAll() throws Exception {
        return (List<FirewallRules>) egressRepo.findAll();
    }

    /**
     * Check the egress firewall CS error handling.
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
             JSONArray EgressListJSON = listJSON.getJSONArray("firewallrule");
            // 2. Iterate the json list, convert the single json entity to nic
            for (int i = 0, size = EgressListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to nic entity and Add
                // the converted nic entity to list
                FirewallRules egress = FirewallRules.convert(EgressListJSON.getJSONObject(i));
                egress.setNetworkId(convertEntityService.getNetworkId(egress.getTransNetworkId()));
                egressList.add(egress);
            }
                 }
            }
        return egressList;
   }
}
