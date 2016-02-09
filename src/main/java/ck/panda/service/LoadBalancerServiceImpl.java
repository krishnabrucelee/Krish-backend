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
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.LoadBalancerRule.SticknessMethod;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.repository.jpa.LoadBalancerRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackLoadBalancerService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Load Balancer Service Implementation.
 *
 */
@Service
public class LoadBalancerServiceImpl implements LoadBalancerService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** VolumeRepository repository reference. */
    @Autowired
    private LoadBalancerRepository loadBalancerRepo;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Cloud stack firewall service. */
    @Autowired
    private CloudStackLoadBalancerService cloudStackLoadBalancerService;

    /** Virtual machine Service for listing vms. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** Token Detail Utilities. */
    @Autowired
    private TokenDetails tokenDetails;


    @Override
    public LoadBalancerRule save(LoadBalancerRule loadBalancer) throws Exception {
        loadBalancer.setIsActive(true);
        if (loadBalancer.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("loadBalancer", loadBalancer);
            errors = validator.validateEntity(loadBalancer, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                LoadBalancerRule csLoadBalancer = csCreateLoadBalancerRule(loadBalancer, errors);
                if (loadBalancerRepo.findByUUID(csLoadBalancer.getUuid(), true) == null) {
                       loadBalancerRepo.save(csLoadBalancer);
                }

            }
        }
            return loadBalancerRepo.save(loadBalancer);
        }

    private LoadBalancerRule createStickinessPolicy(LoadBalancerRule loadBalancer) throws Exception {
          if (loadBalancer.getStickinessMethod() != null) {
              Errors errors = validator.rejectIfNullEntity("loadBalancer", loadBalancer);
              errors = validator.validateEntity(loadBalancer, errors);
              String createStickiness = cloudStackLoadBalancerService.createLBStickinessPolicy(loadBalancer.getUuid(), "json", addOptionalValues(loadBalancer));
               JSONObject csloadBalancerResponseJSON = new JSONObject(createStickiness)
                       .getJSONObject("createLBStickinessPolicy");
                   if (errors.hasErrors()) {
                  throw new ApplicationException(errors);
                   }
                       if (csloadBalancerResponseJSON.has("jobid")) {
                           Thread.sleep(10000);
                           String jobResponse = cloudStackLoadBalancerService.loadBalancerJobResult(csloadBalancerResponseJSON.getString("jobid"), "json");
                           JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse").getJSONObject("jobresult");
                           JSONArray stickyResult = jobresult.getJSONObject("stickinesspolicies").getJSONArray("stickinesspolicy");
                          for (int j = 0, sizes = stickyResult.length(); j < sizes; j++) {
                              JSONObject json = (JSONObject)stickyResult.get(j);
                              loadBalancer.setStickinessName((String) json.getString("name"));
                              loadBalancer.setStickinessMethod(SticknessMethod.valueOf(json.getString("methodname")));
                              loadBalancer.setStickyUuid((String) json.getString("id"));
                              if (json.has("params")) {
                                  JSONObject paramsResponse = json.getJSONObject("params");
                                  if (paramsResponse.has("tablesize")) {
                                      loadBalancer.setStickyTableSize((String) paramsResponse.getString("tablesize"));
                                  }
                                  if (paramsResponse.has("length")) {
                                      loadBalancer.setStickyLength((String) paramsResponse.getString("length"));
                                  }
                                  if (paramsResponse.has("expires")) {
                                      loadBalancer.setStickyExpires((String) paramsResponse.getString("expires"));
                                  }
                                  if (paramsResponse.has("mode")) {
                                      loadBalancer.setStickyMode((String) paramsResponse.getString("mode"));
                                  }
                                  if (paramsResponse.has("prefix")) {
                                      loadBalancer.setStickyPrefix((Boolean) paramsResponse.get("prefix"));
                                  }
                                  if (paramsResponse.has("request-learn")) {
                                      loadBalancer.setStickyRequestLearn((Boolean) paramsResponse.get("request-learn"));
                                  }
                                  if (paramsResponse.has("indirect")) {
                                      loadBalancer.setStickyIndirect((Boolean) paramsResponse.get("indirect"));
                                  }
                                  if (paramsResponse.has("nocache")) {
                                      loadBalancer.setStickyNoCache((Boolean) paramsResponse.get("nocache"));
                                  }
                                  if (paramsResponse.has("postonly")) {
                                      loadBalancer.setStickyPostOnly((Boolean) paramsResponse.get("postonly"));
                                  }
                                  if (paramsResponse.has("holdtime")) {
                                      loadBalancer.setStickyHoldTime((String) paramsResponse.getString("holdtime"));
                                  }
                                 if (paramsResponse.has("domain")) {
                                     loadBalancer.setStickyCompany((String) paramsResponse.getString("domain"));
                                 }
                              }
                          }
                       }
              }

        return loadBalancer;

    }
    @Override
    public LoadBalancerRule update(LoadBalancerRule loadBalancer) throws Exception {
         if (loadBalancer.getSyncFlag()) {
             Errors errors = validator.rejectIfNullEntity("loadBalancer", loadBalancer);
             errors = validator.validateEntity(loadBalancer, errors);
             HashMap<String,String> optional = new HashMap<String, String>();
             optional.put("algorithm", loadBalancer.getAlgorithm());
             optional.put("name", loadBalancer.getName());
             String csEditloadBalancer = cloudStackLoadBalancerService.updateLoadBalancerRule(loadBalancer.getUuid(), "json", optional);
             JSONObject csloadBalancerResponseJSON = new JSONObject(csEditloadBalancer)
                         .getJSONObject("updateloadbalancerruleresponse");
             this.createStickinessPolicy(loadBalancer);
             if (errors.hasErrors()) {
                    throw new ApplicationException(errors);
             }
             if (csloadBalancerResponseJSON.has("jobid")) {
                Thread.sleep(10000);
                String jobResponse = cloudStackLoadBalancerService.loadBalancerJobResult(csloadBalancerResponseJSON.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString("jobstatus").equals("1")) {
                     JSONObject loadBalancerResponse = jobresult.getJSONObject("loadbalancer");
                     loadBalancer.setUuid((String) loadBalancerResponse.get("id"));
                     loadBalancer.setName((String) loadBalancerResponse.get("name"));
                     loadBalancer.setAlgorithm((String) loadBalancerResponse.get("algorithm"));
                }
            }
         }
        return loadBalancerRepo.save(loadBalancer);
    }

    @Override
    public void delete(LoadBalancerRule loadBalancer) throws Exception {
        loadBalancerRepo.delete(loadBalancer);
    }

    @Override
    public void delete(Long id) throws Exception {
        loadBalancerRepo.delete(id);
    }

    @Override
    public LoadBalancerRule find(Long id) throws Exception {
        return loadBalancerRepo.findOne(id);
    }

    @Override
    public Page<LoadBalancerRule> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return loadBalancerRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<LoadBalancerRule> findAll() throws Exception {
        return (List<LoadBalancerRule>) loadBalancerRepo.findAll();
    }

    @Override
    public List<LoadBalancerRule> findAllFromCSServer() throws Exception {
        List<LoadBalancerRule> loadBalancerList = new ArrayList<LoadBalancerRule>();
        HashMap<String, String> loadBalancerMap = new HashMap<String, String>();
        loadBalancerMap.put("listall", "true");
        String response = cloudStackLoadBalancerService.listLoadBalancerRules("json", loadBalancerMap);
        JSONArray loadBalancerListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listloadbalancerrulesresponse");
        if (responseObject.has("loadbalancerrule")) {
            loadBalancerListJSON = responseObject.getJSONArray("loadbalancerrule");
            for (int i = 0, size = loadBalancerListJSON.length(); i < size; i++) {
                LoadBalancerRule loadBalancer = LoadBalancerRule.convert(loadBalancerListJSON.getJSONObject(i));
                if (loadBalancer != null) {
                    HashMap<String, String> loadBalancerInstanceMap = new HashMap<String, String>();
                    loadBalancerInstanceMap.put("lbvmips", "true");
                    loadBalancerInstanceMap.put("listall", "true");
                    String responseCS = cloudStackLoadBalancerService.listLoadBalancerRuleInstances(loadBalancer.getUuid(), "json", loadBalancerInstanceMap);
                    JSONArray vmListJSON = null;
                    JSONObject responseObjectCS = new JSONObject(responseCS).getJSONObject("listloadbalancerruleinstancesresponse");
                    if (responseObjectCS.has("lbrulevmidip")) {
                        vmListJSON = responseObjectCS.getJSONArray("lbrulevmidip");
                        List<VmInstance> newVmInstance = new ArrayList<VmInstance>();
                        for (int k = 0; k < vmListJSON.length(); k++) {
                            VmInstance vmInstance = virtualMachineService.findByUUID(vmListJSON.getJSONObject(k).
                                    getJSONObject("loadbalancerruleinstance").getString("id"));
                            newVmInstance.add(vmInstance);
                        }
                        loadBalancer.setVmInstanceList(newVmInstance);
                    }
                }
                loadBalancer.setNetworkId(convertEntityService.getNetworkId(loadBalancer.getTransNetworkId()));
                loadBalancer.setIpAddressId(convertEntityService.getIpAddressId(loadBalancer.getTransIpAddressId()));
                loadBalancer.setZoneId(convertEntityService.getZoneId(loadBalancer.getTransZoneId()));
                loadBalancer.setDomainId(convertEntityService.getNetworkById(loadBalancer.getNetworkId())
                        .getDomainId());
                loadBalancerList.add(loadBalancer);
            }
        }
        return loadBalancerList;
    }

    @Override
    public LoadBalancerRule softDelete(LoadBalancerRule loadBalancer) throws Exception {
        if (loadBalancer.getSyncFlag()) {
            configUtil.setServer(1L);
            LoadBalancerRule csLoadBalancer = loadBalancerRepo.findOne(loadBalancer.getId());
            try {
                String loadBalancerDeleteResponse = cloudStackLoadBalancerService.deleteLoadBalancerRule(csLoadBalancer.getUuid(), "json");
                JSONObject jobId = new JSONObject(loadBalancerDeleteResponse).getJSONObject("deleteloadbalancerruleresponse");
                if (jobId.has("jobid")) {
                    String jobResponse = cloudStackLoadBalancerService.loadBalancerJobResult(jobId.getString("jobid"), "json");
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                }
            } catch (Exception e) {
                LOGGER.error("ERROR AT Load Balancer DELETE", e);
            }
        }
        loadBalancer.setIsActive(false);
        return loadBalancerRepo.save(loadBalancer);
    }

    @Override
    public LoadBalancerRule findByUUID(String uuid) {
        return loadBalancerRepo.findByUUID(uuid, true);
    }

    /**
     * @param loadBalancer entity object
     * @param errors object for validation
     * @throws Exception raise if error
     * @return loadBalancer details
     */
    public LoadBalancerRule csCreateLoadBalancerRule(LoadBalancerRule loadBalancer, Errors errors) throws Exception {
        configUtil.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        IpAddress vm = convertEntityService.getIpAddress(loadBalancer.getIpAddressId());
        optional.put("domainid",domainService.find(Long.parseLong(tokenDetails.getTokenDetails("domainid"))).getUuid());

        optional.put("publicipid", vm.getUuid());

        String csResponse = cloudStackLoadBalancerService.createLoadBalancerRule(loadBalancer.getAlgorithm(), loadBalancer.getName(),
        loadBalancer.getPrivatePort().toString(), loadBalancer.getPublicPort().toString(), "json", optional);
            JSONObject loadBalancerJSON = new JSONObject(csResponse).getJSONObject("createloadbalancerruleresponse");
            if (loadBalancerJSON.has("errorcode")) {
                errors = this.validateEvent(errors, loadBalancerJSON.getString("errortext"));
                throw new ApplicationException(errors);
            }
                if (loadBalancerJSON.has("jobid")) {
                   Thread.sleep(10000);
                   String eventObjectResult = cloudStackLoadBalancerService.loadBalancerRuleJobResult(loadBalancerJSON.getString("jobid"),
                        "json");
                   Thread.sleep(5000);
                   JSONObject jobresult = new JSONObject(eventObjectResult).getJSONObject("queryasyncjobresultresponse")
                        .getJSONObject("jobresult");
                   if (jobresult.has("loadbalancer")) {
                       LoadBalancerRule loadBalancerRule = LoadBalancerRule.convert(jobresult.getJSONObject("loadbalancer"));
                       loadBalancerRule.setNetworkId(convertEntityService.getNetworkId(loadBalancerRule.getTransNetworkId()));
                       loadBalancerRule.setIpAddressId(convertEntityService.getIpAddressId(loadBalancerRule.getTransIpAddressId()));
                       loadBalancerRule.setZoneId(convertEntityService.getZoneId(loadBalancerRule.getTransZoneId()));
                       loadBalancerRule.setDomainId(convertEntityService.getDomainId(loadBalancerRule.getTransDomainId()));
                       this.createStickinessPolicy(loadBalancerRule);
                       return loadBalancerRule;
                   }
                }
                return loadBalancer;
    }

    /**
     * Check the LoadBalancer CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception unhandled exceptions.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    @Override
    public List<LoadBalancerRule> findByIpaddress(Long ipAddressId, Boolean isActive) {
        return loadBalancerRepo.findAllByIpaddressAndIsActive(ipAddressId, true);
    }

    public HashMap<String, String> addOptionalValues(LoadBalancerRule loadBalancer) throws Exception {
        HashMap<String, String> loadBalancerMap = new HashMap<String, String>();

        if (loadBalancer.getStickinessName() != null) {
            loadBalancerMap.put("name", loadBalancer.getStickinessName().toString());
        }

        if (loadBalancer.getStickinessMethod() != null) {
            loadBalancerMap.put("methodname", loadBalancer.getStickinessMethod().toString());
        }

        if (loadBalancer.getStickyTableSize() != null) {
            loadBalancerMap.put("param[0].name", "tablesize");
            loadBalancerMap.put("param[0].value", loadBalancer.getStickyTableSize().toString());

        }

        if (loadBalancer.getCookieName() != null) {
            loadBalancerMap.put("param[1].name", "cookie-name");
            loadBalancerMap.put("param[1].value", loadBalancer.getCookieName().toString());
        }

        if (loadBalancer.getStickyLength() != null) {
            loadBalancerMap.put("param[2].name", "length");
            loadBalancerMap.put("param[2].value", loadBalancer.getStickyLength().toString());
        }

        if (loadBalancer.getStickyExpires() != null) {
            loadBalancerMap.put("param[3].name", "expires");
            loadBalancerMap.put("param[3].value", loadBalancer.getStickyExpires().toString());
        }

        if (loadBalancer.getStickyMode() != null) {
            loadBalancerMap.put("param[4].name", "mode");
            loadBalancerMap.put("param[4].value", loadBalancer.getStickyMode().toString());
        }

        if (loadBalancer.getStickyRequestLearn() != null) {
            loadBalancerMap.put("param[5].name", "request-learn");
            loadBalancerMap.put("param[5].value", loadBalancer.getStickyRequestLearn().toString());
        }

        if (loadBalancer.getStickyPrefix() != null) {
            loadBalancerMap.put("param[6].name", "prefix");
            loadBalancerMap.put("param[6].value", loadBalancer.getStickyPrefix().toString());
        }

        if (loadBalancer.getStickyIndirect() != null) {
            loadBalancerMap.put("param[7].name", "indirect");
            loadBalancerMap.put("param[7].value", loadBalancer.getStickyIndirect().toString());
        }

        if (loadBalancer.getStickyNoCache() != null) {
            loadBalancerMap.put("param[8].name", "nocache");
            loadBalancerMap.put("param[8].value", loadBalancer.getStickyNoCache().toString());
        }

        if (loadBalancer.getStickyPostOnly() != null) {
            loadBalancerMap.put("param[9].name", "postonly");
            loadBalancerMap.put("param[9].value", loadBalancer.getStickyPostOnly().toString());
        }

        if (loadBalancer.getStickyHoldTime() != null) {
            loadBalancerMap.put("param[10].name", "holdtime");
            loadBalancerMap.put("param[10].value", loadBalancer.getStickyHoldTime().toString());
        }

        if (loadBalancer.getStickyCompany() != null) {
            loadBalancerMap.put("param[11].name", "domain");
            loadBalancerMap.put("param[11].value", loadBalancer.getStickyCompany().toString());
        }

        return loadBalancerMap;
    }
}
