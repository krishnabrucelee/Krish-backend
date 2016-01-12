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
import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.VmInstance;
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

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

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
                    loadBalancerRepo.save(loadBalancer);
                }
                return csLoadBalancer;
            }
        } else {
            return loadBalancerRepo.save(loadBalancer);
        }
    }

    @Override
    public LoadBalancerRule update(LoadBalancerRule loadBalancer) throws Exception {
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
                cloudStackLoadBalancerService.deleteLoadBalancerRule(csLoadBalancer.getUuid(), "json");
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
        String csResponse = cloudStackLoadBalancerService.createLoadBalancerRule(loadBalancer.getAlgorithm(), loadBalancer.getName(),
                loadBalancer.getPrivatePort().toString(), loadBalancer.getPublicPort().toString(), "json", optional);
        try {
            JSONObject loadBalancerJSON = new JSONObject(csResponse).getJSONObject("createloadbalancerruleresponse");
            if (loadBalancerJSON.has("errorcode")) {
                errors = this.validateEvent(errors, loadBalancerJSON.getString("errortext"));
                throw new ApplicationException(errors);
            } else {
                String eventObjectResult = cloudStackLoadBalancerService.loadBalancerRuleJobResult(loadBalancerJSON.getString("jobid"),
                        "json");
                JSONObject jobresult = new JSONObject(eventObjectResult).getJSONObject("queryasyncjobresultresponse")
                        .getJSONObject("jobresult");

                LoadBalancerRule csLoadBalancer = LoadBalancerRule.convert(jobresult.getJSONObject("loadBalancerrule"));
                csLoadBalancer.setNetworkId(convertEntityService.getNetworkId(csLoadBalancer.getTransNetworkId()));
                csLoadBalancer.setIpAddressId(convertEntityService.getIpAddressId(csLoadBalancer.getTransIpAddressId()));
                csLoadBalancer.setZoneId(convertEntityService.getZoneId(csLoadBalancer.getTransZoneId()));
                return csLoadBalancer;
            }
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT TEMPLATE CREATION", e);
            throw new ApplicationException(e.getErrors());
        }
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

}
