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
import ck.panda.domain.entity.LbStickinessPolicy;
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.repository.jpa.LbStickinessPolicyRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackLoadBalancerService;
import ck.panda.util.CloudStackOptionalUtil;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * LbStickinessPolicy service implementation class.
 *
 */
@Service
public class LbStickinessPolicyServiceImpl implements LbStickinessPolicyService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private LbStickinessPolicyRepository policyRepo;

    /** Cloud stack firewall service. */
    @Autowired
    private CloudStackLoadBalancerService cloudStackLoadBalancerService;

    /** Virtual machine Service for listing vms. */
    @Autowired
    private LoadBalancerService loadBalancerService;

    /** Virtual machine Service for listing vms. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Constant for name. */
    private static final String CS_NAME = ".name";

    /** Constant for method name. */
    private static final String CS_METHOD_NAME = "methodname";

    /** Constant for table size. */
    private static final String CS_TABLE_SIZE = "tablesize";

    /** Constant for cookie name. */
    private static final String CS_COOKIE_NAME = "cookie-name";

    /** Constant for length. */
    private static final String CS_LENGTH = "length";

    /** Constant for expires. */
    private static final String CS_EXPIRES = "expire";

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

    /** Constant for algorithm. */
    private static final String CS_ALGORITHM = "algorithm";

    /** Constant for parameters. */
    private static final String CS_PARAMS = "params";

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

    /** Constant for create lb stickiness policy. */
    private static final String CS_CREATE_STICKY_POLICY = "createLBStickinessPolicy";

    /** Constant for create lb stickiness policy. */
    private static final String CS_LIST_STICKY_POLICY = "listlbstickinesspoliciesresponse";

    /** Constant for create lb stickiness policy. */
    private static final String CS_STICKY_POLICY = "stickinesspolicy";

    /** Constant for create lb stickiness policy. */
    private static final String CS_STICKY_POLICIES = "stickinesspolicies";

    @Override
    public LbStickinessPolicy save(LbStickinessPolicy lbStickinessPolicy, String loadbalancer) throws Exception {
        lbStickinessPolicy.setIsActive(true);
         Errors errors = validator.rejectIfNullEntity("lbStickinessPolicy", lbStickinessPolicy);
         errors = validator.validateEntity(lbStickinessPolicy, errors);
         String createStickiness = cloudStackLoadBalancerService.createLBStickinessPolicy(loadbalancer, CloudStackConstants.JSON, addOptionalValues(lbStickinessPolicy));
          JSONObject csloadBalancerResponseJSON = new JSONObject(createStickiness)
                  .getJSONObject(CS_CREATE_STICKY_POLICY);
          if (csloadBalancerResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
              errors = this.validateEvent(errors, csloadBalancerResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
              throw new ApplicationException(errors);
          }
          lbStickinessPolicy.setUuid((String) csloadBalancerResponseJSON.get("id"));
                  if (csloadBalancerResponseJSON.has(CloudStackConstants.CS_JOB_ID)) {
                      String jobResponse = cloudStackLoadBalancerService.loadBalancerJobResult(csloadBalancerResponseJSON.getString(CloudStackConstants.CS_JOB_ID),CloudStackConstants.JSON);
                      JSONObject jobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);

        }
        return policyRepo.save(lbStickinessPolicy);

    }

    @Override
    public LbStickinessPolicy update(LbStickinessPolicy lbStickinessPolicy) throws Exception {

        Errors errors = validator.rejectIfNullEntity("lbStickinessPolicy", lbStickinessPolicy);
        LoadBalancerRule listresponse = loadBalancerService.findByLbId(lbStickinessPolicy.getId());
        String createStickiness = cloudStackLoadBalancerService.createLBStickinessPolicy(listresponse.getUuid(), CloudStackConstants.JSON, addOptionalValues(lbStickinessPolicy));
        JSONObject csloadBalancerResponseJSON = new JSONObject(createStickiness)
                .getJSONObject(CS_CREATE_STICKY_POLICY);
        if (csloadBalancerResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors = this.validateEvent(errors, csloadBalancerResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        }
        lbStickinessPolicy.setUuid(csloadBalancerResponseJSON.getString("id"));

        if (policyRepo.findByUUID(lbStickinessPolicy.getUuid()) == null) {
            return policyRepo.save(lbStickinessPolicy);
        }
        return lbStickinessPolicy;
    }

    @Override
    public void delete(LbStickinessPolicy lbStickinessPolicy) throws Exception {
        policyRepo.delete(lbStickinessPolicy);
    }

    @Override
    public void delete(Long id) throws Exception {
        policyRepo.delete(id);
    }

    @Override
    public LbStickinessPolicy find(Long id) throws Exception {
        LbStickinessPolicy lbStickinessPolicy = policyRepo.findOne(id);
        return lbStickinessPolicy;
    }

    @Override
    public Page<LbStickinessPolicy> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return policyRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<LbStickinessPolicy> findAll() throws Exception {
        return (List<LbStickinessPolicy>) policyRepo.findAll();
    }

    @Override
    public List<LbStickinessPolicy> findAllFromCSServer() throws Exception {
         List<LoadBalancerRule> listresponse = loadBalancerService.findByIsActive(true);
         List<LbStickinessPolicy> loadBalancerList = new ArrayList<LbStickinessPolicy>();
         for (LoadBalancerRule lbRule: listresponse) {
             HashMap<String, String> lbMap = new HashMap<String, String>();
             lbMap.put("lbruleid", lbRule.getUuid());
             configServer.setServer(1L);
             String csStickyResponse = cloudStackLoadBalancerService.listLBStickinessPolicies(CloudStackConstants.JSON, lbMap);
             JSONObject listStickyJSON = new JSONObject(csStickyResponse).getJSONObject(CS_LIST_STICKY_POLICY);
             if (listStickyJSON != null) {
                 JSONArray stickinessJSON = listStickyJSON.getJSONArray(CS_STICKY_POLICIES);
                 JSONArray stickyPolicyJSON = stickinessJSON.getJSONObject(0).getJSONArray(CS_STICKY_POLICY);
                 for (int i = 0, size = stickyPolicyJSON.length(); i < size; i++) {
                     LbStickinessPolicy lbPolicy = LbStickinessPolicy.convert(stickyPolicyJSON.getJSONObject(i));
                     lbPolicy.setLbUuid(stickinessJSON.getJSONObject(0).getString("lbruleid"));
                     loadBalancerList.add(lbPolicy);
                 }
              }
             }
         return loadBalancerList;
    }

    @Override
    public LbStickinessPolicy findByUUID(String uuid) throws Exception {
        return policyRepo.findByUUID(uuid);
    }

    @Override
    public LbStickinessPolicy softDelete(LbStickinessPolicy lbStickinessPolicy) throws Exception {
        lbStickinessPolicy.setIsActive(false);
        return policyRepo.save(lbStickinessPolicy);
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

    /**
     * Optional values mapping to ACS.
     *
     * @param lbStickinessPolicy object
     * @return optional values.
     * @throws Exception if error occurs.
     */
    public HashMap<String, String> addOptionalValues(LbStickinessPolicy lbStickinessPolicy) throws Exception {
        HashMap<String, String> loadBalancerMap = new HashMap<String, String>();

            CloudStackOptionalUtil.updateOptionalStringValue(CloudStackConstants.CS_NAME, lbStickinessPolicy.getStickinessName().toString(),loadBalancerMap);
            CloudStackOptionalUtil.updateOptionalStringValue(CS_METHOD_NAME, lbStickinessPolicy.getStickinessMethod().toString(),loadBalancerMap);
            // Inorder to map values as same as ACS, checking not null condition for allowing only allocated values.
            if (lbStickinessPolicy.getStickyTableSize() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_0 + CS_NAME,CS_TABLE_SIZE,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_0 + CS_VALUE, lbStickinessPolicy.getStickyTableSize().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getCookieName() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_1 + CS_NAME, CS_COOKIE_NAME,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_1 + CS_VALUE, lbStickinessPolicy.getCookieName().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getStickyLength() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_2 + CS_NAME, CS_LENGTH,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_2 + CS_VALUE, lbStickinessPolicy.getStickyLength().toString(),loadBalancerMap);
           }
            if (lbStickinessPolicy.getStickyExpires() != null) {
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_3 + CS_NAME, CS_EXPIRES,loadBalancerMap);
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_3 + CS_VALUE, lbStickinessPolicy.getStickyExpires().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getStickyMode() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_4 + CS_NAME, CS_MODE,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_4 + CS_VALUE, lbStickinessPolicy.getStickyMode().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getStickyRequestLearn() != null && lbStickinessPolicy.getStickyRequestLearn()) {
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_5 + CS_NAME, CS_REQUEST_LEARN,loadBalancerMap);
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_5 + CS_VALUE, lbStickinessPolicy.getStickyRequestLearn().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getStickyPrefix() != null && lbStickinessPolicy.getStickyPrefix()) {
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_6 + CS_NAME, CS_PREFIX,loadBalancerMap);
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_6 + CS_VALUE, lbStickinessPolicy.getStickyPrefix().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getStickyIndirect() != null && lbStickinessPolicy.getStickyIndirect()) {
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_7 + CS_NAME, CS_INDIRECT,loadBalancerMap);
                 CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_7 + CS_VALUE, lbStickinessPolicy.getStickyIndirect().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getStickyNoCache() != null && lbStickinessPolicy.getStickyNoCache()) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_8 + CS_NAME, CS_NO_CACHE,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_8 + CS_VALUE, lbStickinessPolicy.getStickyNoCache().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getStickyPostOnly() != null && lbStickinessPolicy.getStickyPostOnly()) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_9 + CS_NAME, CS_POST_ONLY,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_9 + CS_VALUE, lbStickinessPolicy.getStickyPostOnly().toString(),loadBalancerMap);
            }
            if (lbStickinessPolicy.getStickyHoldTime() != null) {
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_10 + CS_NAME, CS_HOLD_TIME,loadBalancerMap);
                CloudStackOptionalUtil.updateOptionalStringValue(CS_PARAM_10 + CS_VALUE, lbStickinessPolicy.getStickyHoldTime().toString(),loadBalancerMap);
            }

            return loadBalancerMap;
    }

    @Override
    public LbStickinessPolicy save(LbStickinessPolicy stickyPolicy) throws Exception {
        stickyPolicy.setIsActive(true);
         if (!stickyPolicy.getSyncFlag()) {
             stickyPolicy = policyRepo.save(stickyPolicy);
             if (stickyPolicy.getLbUuid() != null) {
                 this.updateLoadBalancer(stickyPolicy);
             }
        }
         return stickyPolicy;
    }

    /**
     * Update loadbalancer stickiness policy in load balancer.
     *
     * @param stickyPolicy of the load balancer.
     * @throws Exception if error occurs.
     */
    private void updateLoadBalancer(LbStickinessPolicy stickyPolicy) throws Exception {
            LoadBalancerRule loadBalancer = loadBalancerService.findByUUID(stickyPolicy.getLbUuid());
            loadBalancer.setSyncFlag(false);
            loadBalancer.setLbPolicyId(stickyPolicy.getId());
            loadBalancerService.update(loadBalancer);
    }
}
