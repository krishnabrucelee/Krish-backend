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
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.LoadBalancerRule.SticknessMethod;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.repository.jpa.LoadBalancerRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackLoadBalancerService;
import ck.panda.util.CloudStackOptionalUtil;
import ck.panda.util.ConfigUtil;
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

    /** Constant for load balancer. */
    private static final String CS_LOADBALANCER = "loadbalancer";

    /** Constant for list load balancer. */
    private static final String CS_LB_LIST = "listloadbalancerrulesresponse";

    /** Constant for list load balancer rule instance response. */
    private static final String CS_LB_LIST_INSTANCE = "listloadbalancerruleinstancesresponse";

    /** Constant for load balancer rule. */
    private static final String CS_LB_RULE = "loadbalancerrule";

    /** Constant for create loadbalancer rule. */
    private static final String CS_LB_CREATE = "createloadbalancerruleresponse";

    /** Constant for delete load balancer rule. */
    private static final String CS_LB_DELETE = "deleteloadbalancerruleresponse";

    /** Constant for lb rule id. */
    private static final String CS_LB_INSTANCE_ID = "lbrulevmidip";

    /** Constant for load balancer rule instance. */
    private static final String CS_LB_INSTANCE = "loadbalancerruleinstance";

    /** Constant for method name. */
    private static final String CS_METHOD_NAME = "methodname";

    /** Constant for table size. */
    private static final String CS_TABLE_SIZE = "tablesize";

    /** Constant for cookie name. */
    private static final String CS_COOKIE_NAME = "cookie-name";

    /** Constant for length. */
    private static final String CS_LENGTH = "length";

    /** Constant for expires. */
    private static final String CS_EXPIRES = "expires";

    /** Constant for mode. */
    private static final String CS_MODE = "mode";

    /** Constant for request learn. */
    private static final String CS_REQUEST_LEARN = "request-learn";

    /** Constant for prefix. */
    private static final String CS_PREFIX = "prefix";

    /** Constant for indirect. */
    private static final String CS_INDIRECT = "indirect";

    /** Constant for nocache. */
    private static final String CS_NO_CACHE = "nocache";

    /** Constant for post only. */
    private static final String CS_POST_ONLY = "postonly";

    /** Constant for hold time. */
    private static final String CS_HOLD_TIME = "holdtime";

    /** Constant for domain. */
    private static final String CS_DOMAIN = "domain";

    /** Constant for algorithm. */
    private static final String CS_ALGORITHM = "algorithm";

    /** Constant for parameters. */
    private static final String CS_PARAMS = "params";

    /** Constant for load balancer instance ip address. */
    private static final String CS_LB_INSTANCE_IP = "lbvmips";

    /** Constant for name. */
    private static final String CS_NAME = ".name";

    /** Constant for value. */
    private static final String CS_VALUE = ".value";

    /** Constant for param of array value zero. */
    private static final String CS_PARAM_0 = "param[0]";

    /** Constant for param of array value one. */
    private static final String CS_PARAM_1 = "param[1]";

    /** Constant for param of array value two. */
    private static final String CS_PARAM_2 = "param[2]";

    /** Constant for param of array value three. */
    private static final String CS_PARAM_3 = "param[3]";

    /** Constant for param of array value four. */
    private static final String CS_PARAM_4 = "param[4]";

    /** Constant for param of array value five. */
    private static final String CS_PARAM_5 = "param[5]";

    /** Constant for param of array value six. */
    private static final String CS_PARAM_6 = "param[6]";

    /** Constant for param of array value seven. */
    private static final String CS_PARAM_7 = "param[7]";

    /** Constant for param of array value eight. */
    private static final String CS_PARAM_8 = "param[8]";

    /** Constant for param of array value nine. */
    private static final String CS_PARAM_9 = "param[9]";

    /** Constant for param of array value ten. */
    private static final String CS_PARAM_10 = "param[10]";

    /** Constant for param of array value eleven. */
    private static final String CS_PARAM_11 = "param[11]";

    /** Constant for create lb stickiness policy. */
    private static final String CS_CREATE_STICKY_POLICY = "createLBStickinessPolicy";

    /** Constant for update load balancer rule response. */
    private static final String CS_UPDATE_LB_RULE = "updateloadbalancerruleresponse";

    @Override
    public LoadBalancerRule save(LoadBalancerRule loadBalancer, Long userId) throws Exception {
        loadBalancer.setIsActive(true);

            Errors errors = validator.rejectIfNullEntity(CS_LOADBALANCER, loadBalancer);
            errors = validator.validateEntity(loadBalancer, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                LoadBalancerRule csLoadBalancer = csCreateLoadBalancerRule(loadBalancer, errors, userId);
                if (loadBalancerRepo.findByUUID(csLoadBalancer.getUuid(), true) == null) {
                    return loadBalancerRepo.save(csLoadBalancer);
                }
                    }
            return loadBalancer;

              }

    @Override
    public LoadBalancerRule createStickinessPolicy(LoadBalancerRule loadBalanceRule) throws Exception {
          if (loadBalanceRule.getStickinessMethod() != null) {
              Errors errors = validator.rejectIfNullEntity(CS_LOADBALANCER, loadBalanceRule);
              errors = validator.validateEntity(loadBalanceRule, errors);
              String createStickiness = cloudStackLoadBalancerService.createLBStickinessPolicy(loadBalanceRule.getUuid(), CloudStackConstants.JSON, addOptionalValues(loadBalanceRule));
               JSONObject csloadBalancerResponseJSON = new JSONObject(createStickiness)
                       .getJSONObject(CS_CREATE_STICKY_POLICY);
                   if (errors.hasErrors()) {
                  throw new ApplicationException(errors);
                   }
                       if (csloadBalancerResponseJSON.has(CloudStackConstants.CS_JOB_ID)) {
                           Thread.sleep(10000);
                           String jobResponse = cloudStackLoadBalancerService.loadBalancerJobResult(csloadBalancerResponseJSON.getString(CloudStackConstants.CS_JOB_ID),CloudStackConstants.JSON);
                           JSONObject jobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE).getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                           JSONArray stickyResult = jobresult.getJSONObject(CloudStackConstants.CS_STICKY_POLICIES).getJSONArray(CloudStackConstants.CS_STICKY_POLICY);
                          for (int j = 0, sizes = stickyResult.length(); j < sizes; j++) {
                              JSONObject json = (JSONObject)stickyResult.get(j);
                              loadBalanceRule.setStickinessName((String) json.getString(CloudStackConstants.CS_NAME));
                              loadBalanceRule.setStickinessMethod(SticknessMethod.valueOf(json.getString(CS_METHOD_NAME)));
                              loadBalanceRule.setStickyUuid((String) json.getString(CloudStackConstants.CS_ID));
                              if (json.has(CS_PARAMS)) {
                                  JSONObject paramsResponse = json.getJSONObject(CS_PARAMS);
                                  switch (CS_PARAMS) {
                                  case CS_TABLE_SIZE :
                                      loadBalanceRule.setStickyTableSize((String) paramsResponse.getString(CS_TABLE_SIZE));
                                      break;
                                  case CS_LENGTH :
                                      loadBalanceRule.setStickyLength((String) paramsResponse.getString(CS_LENGTH));
                                      break;
                                  case CS_EXPIRES :
                                      loadBalanceRule.setStickyExpires((String) paramsResponse.getString(CS_EXPIRES));
                                      break;
                                  case CS_MODE:
                                      loadBalanceRule.setStickyMode((String) paramsResponse.getString(CS_MODE));
                                      break;
                                  case CS_PREFIX :
                                      loadBalanceRule.setStickyPrefix((Boolean) paramsResponse.get(CS_PREFIX));
                                      break;
                                  case CS_REQUEST_LEARN :
                                      loadBalanceRule.setStickyRequestLearn((Boolean) paramsResponse.get(CS_REQUEST_LEARN));
                                      break;
                                  case  CS_INDIRECT :
                                      loadBalanceRule.setStickyIndirect((Boolean) paramsResponse.get(CS_INDIRECT));
                                      break;
                                  case CS_NO_CACHE :
                                      loadBalanceRule.setStickyNoCache((Boolean) paramsResponse.get(CS_NO_CACHE));
                                      break;
                                  case CS_POST_ONLY :
                                      loadBalanceRule.setStickyPostOnly((Boolean) paramsResponse.get(CS_POST_ONLY));
                                      break;
                                  case CS_HOLD_TIME :
                                      loadBalanceRule.setStickyHoldTime((String) paramsResponse.getString(CS_HOLD_TIME));
                                      break;
                                  case CS_DOMAIN :
                                      loadBalanceRule.setStickyCompany((String) paramsResponse.getString(CS_DOMAIN));
                                      break;
                                  default :
                                      break;
                                  }
                              }
                          }
                       }
              }
        return loadBalanceRule;
    }

    @Override
    public LoadBalancerRule update(LoadBalancerRule loadBalancer) throws Exception {
         if (loadBalancer.getSyncFlag()) {
             Errors errors = validator.rejectIfNullEntity(CS_LOADBALANCER, loadBalancer);
             errors = validator.validateEntity(loadBalancer, errors);
             HashMap<String,String> optional = new HashMap<String, String>();
             optional.put(CS_ALGORITHM, loadBalancer.getAlgorithm());
             optional.put(CloudStackConstants.CS_NAME, loadBalancer.getName());
             configUtil.setUserServer();
             String csEditloadBalancer = cloudStackLoadBalancerService.updateLoadBalancerRule(loadBalancer.getUuid(), CloudStackConstants.JSON, optional);
             JSONObject csloadBalancerResponseJSON = new JSONObject(csEditloadBalancer)
                         .getJSONObject(CS_UPDATE_LB_RULE);
             this.createStickinessPolicy(loadBalancer);
             if (errors.hasErrors()) {
                    throw new ApplicationException(errors);
             }
             Thread.sleep(10000);
             if (csloadBalancerResponseJSON.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = cloudStackLoadBalancerService.loadBalancerJobResult(csloadBalancerResponseJSON.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE).getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                     JSONObject loadBalancerResponse = jobresult.getJSONObject(CS_LOADBALANCER);
                     loadBalancer.setUuid((String) loadBalancerResponse.get(CloudStackConstants.CS_ID));
                     loadBalancer.setName((String) loadBalancerResponse.get(CloudStackConstants.CS_NAME));
                     loadBalancer.setAlgorithm((String) loadBalancerResponse.get(CS_ALGORITHM));

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
        loadBalancerMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        loadBalancerMap.put("fordisplay", "true");
        configUtil.setServer(1L);
        String response = cloudStackLoadBalancerService.listLoadBalancerRules(CloudStackConstants.JSON, loadBalancerMap);
        JSONArray loadBalancerListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LB_LIST);
        if (responseObject.has(CS_LB_RULE)) {
            loadBalancerListJSON = responseObject.getJSONArray(CS_LB_RULE);
            for (int i = 0, size = loadBalancerListJSON.length(); i < size; i++) {
                LoadBalancerRule loadBalancer = LoadBalancerRule.convert(loadBalancerListJSON.getJSONObject(i));
                if (loadBalancer != null) {
                    HashMap<String, String> loadBalancerInstanceMap = new HashMap<String, String>();
                    loadBalancerInstanceMap.put(CS_LB_INSTANCE_IP, CloudStackConstants.STATUS_ACTIVE);
                    loadBalancerInstanceMap.put(CloudStackConstants.CS_LIST_ALL,CloudStackConstants.STATUS_ACTIVE);
                    String responseCS = cloudStackLoadBalancerService.listLoadBalancerRuleInstances(loadBalancer.getUuid(), CloudStackConstants.JSON, loadBalancerInstanceMap);
                    JSONArray vmListJSON = null;
                    JSONObject responseObjectCS = new JSONObject(responseCS).getJSONObject(CS_LB_LIST_INSTANCE);
                    if (responseObjectCS.has(CS_LB_INSTANCE_ID)) {
                        vmListJSON = responseObjectCS.getJSONArray(CS_LB_INSTANCE_ID);
                        List<VmInstance> newVmInstance = new ArrayList<VmInstance>();
                        for (int k = 0; k < vmListJSON.length(); k++) {
                            VmInstance vmInstance = virtualMachineService.findByUUID(vmListJSON.getJSONObject(k).
                                    getJSONObject(CS_LB_INSTANCE).getString(CloudStackConstants.CS_ID));
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
            LoadBalancerRule csLoadBalancer = loadBalancerRepo.findOne(loadBalancer.getId());
            try {
                configUtil.setUserServer();
                String loadBalancerDeleteResponse = cloudStackLoadBalancerService.deleteLoadBalancerRule(csLoadBalancer.getUuid(), CloudStackConstants.JSON);
                JSONObject jobId = new JSONObject(loadBalancerDeleteResponse).getJSONObject(CS_LB_DELETE);
                if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                    String jobResponse = cloudStackLoadBalancerService.loadBalancerJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                    JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
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
     * @param userId of the user.
     * @throws Exception raise if error
     * @return loadBalancer details
     */
    public LoadBalancerRule csCreateLoadBalancerRule(LoadBalancerRule loadBalancer, Errors errors, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        configUtil.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        IpAddress ipAddress = convertEntityService.getIpAddress(loadBalancer.getIpAddressId());
        optional.put(CloudStackConstants.CS_DOMAIN_ID, domainService.find(user.getDomainId()).getUuid());
        optional.put("publicipid", ipAddress.getUuid());
        String csResponse = cloudStackLoadBalancerService.createLoadBalancerRule(loadBalancer.getAlgorithm(), loadBalancer.getName(),
        loadBalancer.getPrivatePort().toString(), loadBalancer.getPublicPort().toString(), CloudStackConstants.JSON, optional);
            JSONObject loadBalancerJSON = new JSONObject(csResponse).getJSONObject(CS_LB_CREATE);
            if (loadBalancerJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateEvent(errors, loadBalancerJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            }
                if (loadBalancerJSON.has(CloudStackConstants.CS_JOB_ID)) {
                   Thread.sleep(10000);
                   String eventObjectResult = cloudStackLoadBalancerService.loadBalancerRuleJobResult(loadBalancerJSON.getString(CloudStackConstants.CS_JOB_ID),
                           CloudStackConstants.JSON);
                   Thread.sleep(5000);
                   JSONObject jobresult = new JSONObject(eventObjectResult).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE)
                        .getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                   if (jobresult.has(CS_LOADBALANCER)) {
                       LoadBalancerRule loadBalancerRule = LoadBalancerRule.convert(jobresult.getJSONObject(CS_LOADBALANCER));
                       loadBalancerRule.setNetworkId(convertEntityService.getNetworkId(loadBalancerRule.getTransNetworkId()));
                       loadBalancerRule.setIpAddressId(convertEntityService.getIpAddressId(loadBalancerRule.getTransIpAddressId()));
                       loadBalancerRule.setZoneId(convertEntityService.getZoneId(loadBalancerRule.getTransZoneId()));
                       loadBalancerRule.setDomainId(convertEntityService.getDomainId(loadBalancerRule.getTransDomainId()));
                       loadBalancerRule.setStickinessName(loadBalancer.getStickinessName());
                       loadBalancerRule.setStickinessMethod(loadBalancer.getStickinessMethod());
                       loadBalancerRule.setAlgorithm(loadBalancer.getAlgorithm());
                       loadBalancerRule.setStickyTableSize(loadBalancer.getStickyTableSize());
                       loadBalancerRule.setCookieName(loadBalancer.getCookieName());
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

    /**
     * Optional values mapping to ACS.
     *
     * @param loadBalancer object
     * @return optional values.
     * @throws Exception if error occurs.
     */
    public HashMap<String, String> addOptionalValues(LoadBalancerRule loadBalancer) throws Exception {
        HashMap<String, String> loadBalancerMap = new HashMap<String, String>();

            CloudStackOptionalUtil.updateOptionalStringValue(CloudStackConstants.CS_NAME, loadBalancer.getStickinessName().toString(),loadBalancerMap);
            CloudStackOptionalUtil.updateOptionalStringValue(CS_METHOD_NAME, loadBalancer.getStickinessMethod().toString(),loadBalancerMap);
            // Inorder to map values as same as ACS, checking not null condition for allowing only allocated values.
            if (loadBalancer.getStickyTableSize() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_0 + CS_NAME,CS_TABLE_SIZE,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_0 + CS_VALUE, loadBalancer.getStickyTableSize().toString(),loadBalancerMap);
            }
            if (loadBalancer.getCookieName() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_1 + CS_NAME, CS_COOKIE_NAME,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_1 + CS_VALUE, loadBalancer.getCookieName().toString(),loadBalancerMap);
            }
            if (loadBalancer.getStickyLength() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_2 + CS_NAME, CS_LENGTH,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_2 + CS_VALUE, loadBalancer.getStickyLength().toString(),loadBalancerMap);
           }
            if (loadBalancer.getStickyExpires() != null) {
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_3 + CS_NAME, CS_EXPIRES,loadBalancerMap);
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_3 + CS_VALUE, loadBalancer.getStickyExpires().toString(),loadBalancerMap);
            }
            if (loadBalancer.getStickyMode() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_4 + CS_NAME, CS_MODE,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_4 + CS_VALUE, loadBalancer.getStickyMode().toString(),loadBalancerMap);
            }
            if (loadBalancer.getStickyRequestLearn() != null) {
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_5 + CS_NAME, CS_REQUEST_LEARN,loadBalancerMap);
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_5 + CS_VALUE, loadBalancer.getStickyRequestLearn().toString(),loadBalancerMap);
            }
            if (loadBalancer.getStickyPrefix() != null) {
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_6 + CS_NAME, CS_PREFIX,loadBalancerMap);
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_6 + CS_VALUE, loadBalancer.getStickyPrefix().toString(),loadBalancerMap);
            }
            if (loadBalancer.getStickyIndirect() != null) {
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_7 + CS_NAME, CS_INDIRECT,loadBalancerMap);
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_7 + CS_VALUE, loadBalancer.getStickyIndirect().toString(),loadBalancerMap);
            }
            if (loadBalancer.getStickyNoCache() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_8 + CS_NAME, CS_NO_CACHE,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_8 + CS_VALUE, loadBalancer.getStickyNoCache().toString(),loadBalancerMap);
            }
            if (loadBalancer.getStickyPostOnly() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_9 + CS_NAME, CS_POST_ONLY,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_9 + CS_VALUE, loadBalancer.getStickyPostOnly().toString(),loadBalancerMap);
            }
            if (loadBalancer.getStickyHoldTime() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_10 + CS_NAME, CS_HOLD_TIME,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_10 + CS_VALUE, loadBalancer.getStickyHoldTime().toString(),loadBalancerMap);
            }
           /* if (loadBalancer.getDomain() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_11 + CS_NAME, CS_DOMAIN,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_11 + CS_VALUE, loadBalancer.getDomain().toString(),loadBalancerMap);
            }*/
            return loadBalancerMap;
    }

    @Override
    public LoadBalancerRule save(LoadBalancerRule loadBalancer) throws Exception {
          if (!loadBalancer.getSyncFlag()) {
                return loadBalancerRepo.save(loadBalancer);
            }
            return loadBalancer;
        }

    @Override
    public List<LoadBalancerRule> findAllFromCSServerStickyPolicies() throws Exception {
        List<LoadBalancerRule> listresponse = loadBalancerRepo.findAllByIsActive(true);
        List<LoadBalancerRule> loadBalancerList = new ArrayList<LoadBalancerRule>();
        for (LoadBalancerRule lbRule: listresponse) {
            HashMap<String, String> lbMap = new HashMap<String, String>();
            lbMap.put("lbruleid", lbRule.getUuid());
            configUtil.setServer(1L);
            String csStickyResponse = cloudStackLoadBalancerService.listLBStickinessPolicies(CloudStackConstants.JSON, lbMap);
            JSONObject listStickyJSON = new JSONObject(csStickyResponse).getJSONObject("listlbstickinesspoliciesresponse");
            if (listStickyJSON != null) {
                JSONArray stickinessJSON = listStickyJSON.getJSONArray("stickinesspolicies");
                JSONArray stickyPolicyJSON = stickinessJSON.getJSONObject(0).getJSONArray("stickinesspolicy");
                for (int i = 0, size = stickyPolicyJSON.length(); i < size; i++) {
                    JSONObject lbRuleList = stickyPolicyJSON.getJSONObject(i);
                    lbRule.setStickyUuid(lbRuleList.getString("id"));
                    lbRule.setStickinessMethod(SticknessMethod.valueOf(lbRuleList.getString("methodname")));
                    lbRule.setStickinessName(lbRuleList.getString("name"));
                    loadBalancerList.add(lbRule);
                }
             }
            }
        return loadBalancerList;
    }
}
