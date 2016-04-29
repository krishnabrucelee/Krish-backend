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
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.domain.repository.jpa.LoadBalancerRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackLoadBalancerService;
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

    /** Load balancer stickiness policy service for reference. */
    @Autowired
    private LbStickinessPolicyService stickyService;

    /** VmIpAddress service for reference. */
    @Autowired
    private VmIpaddressService vmIpService;

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** Project service reference. */
    @Autowired
    private ProjectService projectService;

    /** Constant for load balancer. */
    private static final String CS_LOADBALANCER = "loadbalancer";

    /** Constant for list load balancer. */
    private static final String CS_LB_LIST = "listloadbalancerrulesresponse";

    /** Constant for list load balancer rule instance response. */
    private static final String CS_LB_LIST_INSTANCE = "listloadbalancerruleinstancesresponse";

    /** Constant for load balancer rule. */
    private static final String CS_LB_RULE = "loadbalancerrule";

    /** Constant for load balancer rule. */
    private static final String CS_LB_IP = "lbvmipaddresses";

    /** Constant for create loadbalancer rule. */
    private static final String CS_LB_CREATE = "createloadbalancerruleresponse";

    /** Constant for delete load balancer rule. */
    private static final String CS_LB_DELETE = "deleteloadbalancerruleresponse";

    /** Constant for lb rule id. */
    private static final String CS_LB_INSTANCE_ID = "lbrulevmidip";

    /** Constant for load balancer rule instance. */
    private static final String CS_LB_INSTANCE = "loadbalancerruleinstance";

    /** Constant for algorithm. */
    private static final String CS_ALGORITHM = "algorithm";

    /** Constant for load balancer instance ip address. */
    private static final String CS_LB_INSTANCE_IP = "lbvmips";

    /** Constant for update load balancer rule response. */
    private static final String CS_UPDATE_LB_RULE = "updateloadbalancerruleresponse";

    private static final String CS_FORDISPLAY = "fordisplay";

    @Override
    public LoadBalancerRule save(LoadBalancerRule loadBalancer, Long userId) throws Exception {
        loadBalancer.setIsActive(true);
        if (loadBalancer.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(CS_LOADBALANCER, loadBalancer);
            errors = validator.validateEntity(loadBalancer, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                LoadBalancerRule csLoadBalancer = csCreateLoadBalancerRule(loadBalancer, errors, userId);
                if (loadBalancer.getVmIpAddress() != null) {
                   this.createLoadBalancerRule(loadBalancer, errors);
                }
                if (loadBalancerRepo.findByUUID(csLoadBalancer.getUuid(), true) == null) {
                   return loadBalancerRepo.save(csLoadBalancer);
                }
            }
            return loadBalancer;
        } else {
            return loadBalancerRepo.save(loadBalancer);
        }
    }

    @Override
    public LoadBalancerRule update(LoadBalancerRule loadBalancer) throws Exception {
        LoadBalancerRule lbRule = convertEntityService.getLoadBalancer(loadBalancer.getId());
         if (loadBalancer.getSyncFlag()) {
             Errors errors = validator.rejectIfNullEntity(CS_LOADBALANCER, loadBalancer);
             errors = validator.validateEntity(loadBalancer, errors);
             HashMap<String,String> optional = new HashMap<String, String>();
             optional.put(CS_ALGORITHM, loadBalancer.getAlgorithm());
             optional.put(CloudStackConstants.CS_NAME, loadBalancer.getName());
             configUtil.setUserServer();
             String csEditloadBalancer = cloudStackLoadBalancerService.updateLoadBalancerRule(lbRule.getUuid(), CloudStackConstants.JSON, optional);
             JSONObject csloadBalancerResponseJSON = new JSONObject(csEditloadBalancer)
                         .getJSONObject(CS_UPDATE_LB_RULE);
             if (loadBalancer.getLbPolicy() != null) {
                 stickyService.save(loadBalancer.getLbPolicy(),loadBalancer.getUuid());
             }
             if (loadBalancer.getVmIpAddress().size() != 0) {
                 this.assignLoadBalancerRule(loadBalancer, errors);
             }
             if (errors.hasErrors()) {
                    throw new ApplicationException(errors);
             }
             if (csloadBalancerResponseJSON.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = cloudStackLoadBalancerService.loadBalancerJobResult(csloadBalancerResponseJSON.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
             }
         }
         lbRule.setAlgorithm(loadBalancer.getAlgorithm());
         lbRule.setName(loadBalancer.getName());
        return loadBalancerRepo.save(lbRule);
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
        List<Project> projectList = projectService.findAllByActive(true);
        List<LoadBalancerRule> loadBalancerList = new ArrayList<LoadBalancerRule>();
        for (int j = 0; j <= projectList.size(); j++) {
        HashMap<String, String> loadBalancerMap = new HashMap<String, String>();
        if (j == projectList.size()) {
            loadBalancerMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
            loadBalancerMap.put(CS_FORDISPLAY, CloudStackConstants.STATUS_ACTIVE);
        }
        else {
            loadBalancerMap.put(CloudStackConstants.CS_PROJECT_ID, projectList.get(j).getUuid());
            loadBalancerMap.put(CS_FORDISPLAY, CloudStackConstants.STATUS_ACTIVE);
        }
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
                            JSONArray responseObjectIPCS = vmListJSON.getJSONObject(k).getJSONArray(CS_LB_IP);
                           List<VmIpaddress> ipaddresses =  new ArrayList<VmIpaddress>();
                            for (int  m = 0; m < responseObjectIPCS.length(); m++) {
                                String lbIpAddress = responseObjectIPCS.get(m).toString();
                                    VmIpaddress vmList = vmIpService.findByIPAddress(lbIpAddress, vmInstance.getId());
                                    vmList.setGuestIpAddress(lbIpAddress);
                                    vmList.setVmInstanceId(vmInstance.getId());
                                    ipaddresses.add(vmList);
                                }
                            loadBalancer.setVmIpAddress(ipaddresses);
                        }
                        loadBalancer.setVmInstanceList(newVmInstance);

                    }
                }
                loadBalancer.setNetworkId(convertEntityService.getNetworkId(loadBalancer.getTransNetworkId()));
                if(loadBalancer.getNetworkId() != null) {
                    loadBalancer.setDomainId(convertEntityService.getNetworkById(loadBalancer.getNetworkId()).getDomainId());
                }
                loadBalancer.setIpAddressId(convertEntityService.getIpAddressId(loadBalancer.getTransIpAddressId()));
                loadBalancer.setZoneId(convertEntityService.getZoneId(loadBalancer.getTransZoneId()));
                loadBalancerList.add(loadBalancer);
            }
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
            loadBalancer.setUuid((String) loadBalancerJSON.get("id"));
                if (loadBalancerJSON.has(CloudStackConstants.CS_JOB_ID)) {
                   String eventObjectResult = cloudStackLoadBalancerService.loadBalancerRuleJobResult(loadBalancerJSON.getString(CloudStackConstants.CS_JOB_ID),
                           CloudStackConstants.JSON);
                   JSONObject jobresult = new JSONObject(eventObjectResult).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                   if (loadBalancer.getLbPolicy().getStickinessMethod() != null) {
                   stickyService.save(loadBalancer.getLbPolicy(),loadBalancer.getUuid());
                   }
                       return loadBalancer;
                   }
                return loadBalancer;
    }

    /**
     * Assign rule for load balancer.
     *
     * @param loadbalancer object.
     * @param errors for unhandled errors.
     * @return load balancer.
     * @throws Exception if error occurs.
     */
    private LoadBalancerRule assignLoadBalancerRule(LoadBalancerRule loadbalancer, Errors errors) throws Exception {
        configUtil.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        LoadBalancerRule lbRule = convertEntityService.getLoadBalancer(loadbalancer.getId());
        List<VmIpaddress> vmlist = loadbalancer.getVmIpAddress();
        for(VmIpaddress lbRuleIp : lbRule.getVmIpAddress()) {
            for (int i = 0; i < vmlist.size(); i++) {
            if(!lbRuleIp.getGuestIpAddress().equals(loadbalancer.getVmIpAddress().get(i).getGuestIpAddress())){
                    VmInstance vmId = convertEntityService.getVmInstanceById(loadbalancer.getVmIpAddress().get(i).getVmInstanceId());
                    optional.put("vmidipmap[" + i + "].vmid", vmId.getUuid());
                    optional.put("vmidipmap[" + i + "].vmip", loadbalancer.getVmIpAddress().get(i).getGuestIpAddress());
                String assignResponse = cloudStackLoadBalancerService.assignToLoadBalancerRule(lbRule.getUuid(), "json", optional);
                for(VmIpaddress vmIp : lbRule.getVmIpAddress()) {
                    vmIp.getGuestIpAddress();
                    vmlist.add(vmIp);
                }
                lbRule.setVmIpAddress(vmlist);
                loadBalancerRepo.save(lbRule);
                }
            }
        }
        return loadbalancer;

    }

    @Override
    public LoadBalancerRule removeLoadBalancerRule(LoadBalancerRule loadBalancer) throws Exception {
        configUtil.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
       VmIpaddress vmIpAddress = loadBalancer.getVmIpAddress().get(0);
        VmInstance vmId = convertEntityService.getVmInstanceById(vmIpAddress.getVmInstanceId());
        optional.put("vmidipmap[0].vmid", vmId.getUuid());
        optional.put("vmidipmap[0].vmip", vmIpAddress.getGuestIpAddress());
        cloudStackLoadBalancerService.removeFromLoadBalancerRule(loadBalancer.getUuid(), "json", optional);
        LoadBalancerRule lbRule = convertEntityService.getLoadBalancer(loadBalancer.getId());
        List<VmIpaddress> vmList = new ArrayList<VmIpaddress>();
        for (VmIpaddress vmIp : lbRule.getVmIpAddress()) {
            if (vmIpAddress.getId() != vmIp.getId()) {
                vmList.add(vmIp);
                }
        }
        loadBalancer.setVmIpAddress(vmList);
        loadBalancerRepo.save(loadBalancer);
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

    @Override
    public LoadBalancerRule save(LoadBalancerRule loadBalancer) throws Exception {
          if (!loadBalancer.getSyncFlag()) {
                return loadBalancerRepo.save(loadBalancer);
            }
            return loadBalancer;
        }

    @Override
    public List<LoadBalancerRule> findByIsActive(Boolean isActive) {
        return loadBalancerRepo.findAllByIsActive(true);
    }

    /**
     *  Create load balancer rule for an IpAddress.
     *
     * @param loadbalancer object.
     * @param errors if error occurs.
     * @return loadbalancer rule.
     * @throws Exception if error occurs.
     */
    private LoadBalancerRule createLoadBalancerRule(LoadBalancerRule loadbalancer, Errors errors) throws Exception {
        configUtil.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        List<VmIpaddress> vmlist = loadbalancer.getVmIpAddress();
        for (int i = 0; i < vmlist.size(); i++) {
            VmInstance vmId = convertEntityService.getVmInstanceById(loadbalancer.getVmIpAddress().get(i).getVmInstanceId());
            optional.put("vmidipmap[" + i + "].vmid", vmId.getUuid());
            optional.put("vmidipmap[" + i + "].vmip", loadbalancer.getVmIpAddress().get(i).getGuestIpAddress());
        }
        cloudStackLoadBalancerService.assignToLoadBalancerRule(loadbalancer.getUuid(), "json", optional);
        loadbalancer.setVmIpAddress(vmlist);
         return loadbalancer;

    }

    @Override
    public LoadBalancerRule findByLbId(Long lbPolicyId) {
        return loadBalancerRepo.findByLbIdAndIsActive(lbPolicyId, true);
    }

    @Override
    public List<LoadBalancerRule> findAllByIpAddressAndIsActive(Long id, Boolean isActive) throws Exception {
        return loadBalancerRepo.findAllByIpAddressAndIsActive(id, true);
    }

   }
