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
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.PortForwarding;
import ck.panda.domain.entity.Project;
import ck.panda.domain.repository.jpa.PortForwardingRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackFirewallService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Port Forwarding Service Implementation.
 *
 */
@Service
public class PortForwardingServiceImpl implements PortForwardingService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VirtualMachineServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** VolumeRepository repository reference. */
    @Autowired
    private PortForwardingRepository portForwardingRepo;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Cloud stack firewall service. */
    @Autowired
    private CloudStackFirewallService cloudStackFirewallService;

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    @Autowired
    private ProjectService projectService;

    @Override
    public PortForwarding save(PortForwarding portForwarding) throws Exception {
        portForwarding.setIsActive(true);
        if (portForwarding.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("portForwarding", portForwarding);
            errors = validator.validateEntity(portForwarding, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                PortForwarding csPortForwarding = csCreatePortForwardingRule(portForwarding, errors);
                if (csPortForwarding != null && portForwardingRepo.findByUUID(csPortForwarding.getUuid(), true) == null) {
                    portForwardingRepo.save(csPortForwarding);
                }
                return portForwarding;
            }
        } else {
            return portForwardingRepo.save(portForwarding);
        }
    }

    @Override
    public PortForwarding update(PortForwarding portForwarding) throws Exception {
        return portForwardingRepo.save(portForwarding);
    }

    @Override
    public void delete(PortForwarding portForwarding) throws Exception {
        portForwardingRepo.delete(portForwarding);
    }

    @Override
    public void delete(Long id) throws Exception {
        portForwardingRepo.delete(id);
    }

    @Override
    public PortForwarding find(Long id) throws Exception {
        return portForwardingRepo.findOne(id);
    }

    @Override
    public Page<PortForwarding> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return portForwardingRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<PortForwarding> findAll() throws Exception {
        return (List<PortForwarding>) portForwardingRepo.findAll();
    }

    @Override
    public List<PortForwarding> findAllFromCSServer() throws Exception {
        List<Project> projectList = projectService.findAllByActive(true);
        List<PortForwarding> portList = new ArrayList<PortForwarding>();
        for (Project project: projectList) {
            HashMap<String, String> portForwardingMap = new HashMap<String, String>();
            portForwardingMap.put(CloudStackConstants.CS_PROJECT_ID, project.getUuid());
            portList = getPortList(portForwardingMap, portList);
        }

        HashMap<String, String> portForwardingMap = new HashMap<String, String>();
        portForwardingMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        portList = getPortList(portForwardingMap, portList);
        return portList;
        }

    private List<PortForwarding> getPortList(HashMap<String, String> portForwardingMap, List<PortForwarding> portList) throws Exception {
        String response = cloudStackFirewallService.listPortForwardingRules(portForwardingMap, "json");
        JSONArray portForwardingListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listportforwardingrulesresponse");
        if (responseObject.has("portforwardingrule")) {
            portForwardingListJSON = responseObject.getJSONArray("portforwardingrule");
            for (int i = 0, size = portForwardingListJSON.length(); i < size; i++) {
                PortForwarding portForwarding = PortForwarding.convert(portForwardingListJSON.getJSONObject(i));
                portForwarding.setVmInstanceId(convertEntityService.getVmInstanceId(portForwarding.getTransvmInstanceId()));
                portForwarding.setNetworkId(convertEntityService.getNetworkId(portForwarding.getTransNetworkId()));
                portForwarding.setIpAddressId(convertEntityService.getIpAddressId(portForwarding.getTransIpAddressId()));
                portList.add(portForwarding);
            }
        }
        return portList;
    }

    @Override
    public PortForwarding softDelete(PortForwarding portForwarding) throws Exception {
        if (portForwarding.getSyncFlag()) {
            configUtil.setUserServer();
            PortForwarding csPortForwarding = portForwardingRepo.findOne(portForwarding.getId());
            try {
                cloudStackFirewallService.deletePortForwardingRule(csPortForwarding.getUuid(), "json");
            } catch (Exception e) {
                LOGGER.error("ERROR AT PORT FORWARDING DELETE", e);
            }
        }
        portForwarding.setIsActive(false);
        return portForwardingRepo.save(portForwarding);
    }

    @Override
    public PortForwarding findByUUID(String uuid) {
        return portForwardingRepo.findByUUID(uuid, true);
    }

    /**
     * @param portForwarding entity object
     * @param errors object for validation
     * @throws Exception raise if error
     * @return PortForwarding details
     */
    public PortForwarding csCreatePortForwardingRule(PortForwarding portForwarding, Errors errors) throws Exception {
        configUtil.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put("networkid", convertEntityService.getNetworkById(portForwarding.getNetworkId()).getUuid());
        optional.put("privateendport", portForwarding.getPrivateStartPort().toString());
        optional.put("publicendport", portForwarding.getPublicEndPort().toString());
        optional.put("vmguestip", portForwarding.getvmGuestIp());
        optional.put("openfirewall", CloudStackConstants.STATUS_INACTIVE);
        String csResponse = cloudStackFirewallService.createPortForwardingRule(convertEntityService.getIpAddress(portForwarding.getIpAddressId()).getUuid(),
                portForwarding.getPrivateStartPort().toString(), portForwarding.getProtocolType().toString(),
                portForwarding.getPublicStartPort().toString(), convertEntityService.getVmInstanceById(portForwarding.getVmInstanceId()).getUuid(),
                optional, "json");
        try {
            JSONObject portForwardingJSON = new JSONObject(csResponse).getJSONObject("createportforwardingruleresponse");
            if (portForwardingJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateEvent(errors, portForwardingJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            }
            if(portForwardingJSON.has(CloudStackConstants.CS_ID)) {
                portForwarding.setUuid((String) portForwardingJSON.get(CloudStackConstants.CS_ID));
            } else {
                String eventObjectResult = cloudStackFirewallService.firewallJobResult(portForwardingJSON.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(eventObjectResult).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                    errors = this.validateEvent(errors, jobresult.getString(CloudStackConstants.CS_JOB_STATUS));
                    throw new ApplicationException(errors);
                }
            }
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT PORT FORWARDING RULE CREATION", e);
            throw new ApplicationException(e.getErrors());
        }
        return portForwarding;
    }

    /**
     * Check the PortForwarding CS error handling.
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
    public List<PortForwarding> findByInstance(Long portForwarding) throws Exception {
        return portForwardingRepo.findByInstanceAndIsActive(portForwarding, true);
    }

    @Override
    public Page<PortForwarding> findAllByIpaddress(PagingAndSorting pagingAndSorting, Long ipaddressId)
            throws Exception {
        return portForwardingRepo.findAllByIpaddressAndIsActive(pagingAndSorting.toPageRequest(), ipaddressId, true);
    }

    @Override
    public List<PortForwarding> findAllByIpAddressAndIsActive(Long id, Boolean isActive) throws Exception {
        return portForwardingRepo.findAllByIpAddressAndIsActive(id, true);
    }
}
