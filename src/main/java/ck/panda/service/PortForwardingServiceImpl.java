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
import ck.panda.domain.entity.PortForwarding;
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
        List<PortForwarding> portForwardingList = new ArrayList<PortForwarding>();
        HashMap<String, String> portForwardingMap = new HashMap<String, String>();
        portForwardingMap.put("listall", "true");
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
                portForwardingList.add(portForwarding);
            }
        }
        return portForwardingList;
    }

    @Override
    public PortForwarding softDelete(PortForwarding portForwarding) throws Exception {
        if (portForwarding.getSyncFlag()) {
            configUtil.setServer(1L);
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
        String csResponse = cloudStackFirewallService.createPortForwardingRule(convertEntityService.getIpAddress(portForwarding.getIpAddressId()).getUuid(),
                portForwarding.getPrivateStartPort().toString(), portForwarding.getProtocolType().toString(),
                portForwarding.getPublicStartPort().toString(), convertEntityService.getVmInstanceById(portForwarding.getVmInstanceId()).getUuid(),
                optional, "json");
        try {
            JSONObject portForwardingJSON = new JSONObject(csResponse).getJSONObject("createportforwardingruleresponse");
            if (portForwardingJSON.has("errorcode")) {
                errors = this.validateEvent(errors, portForwardingJSON.getString("errortext"));
                throw new ApplicationException(errors);
            } else {
                Thread.sleep(20000);
                String eventObjectResult = cloudStackFirewallService.firewallJobResult(portForwardingJSON.getString("jobid"),
                        "json");
                JSONObject jobresult = new JSONObject(eventObjectResult).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString("jobstatus").equals("0")) {
                    errors = this.validateEvent(errors, jobresult.getString("jobstatus"));
                    throw new ApplicationException(errors);
                }
                if (jobresult.getJSONObject("jobresult").has("portforwardingrule")) {
                    PortForwarding csPortForwarding = PortForwarding.convert(jobresult.getJSONObject("jobresult").getJSONObject("portforwardingrule"));
                    csPortForwarding.setVmInstanceId(convertEntityService.getVmInstanceId(csPortForwarding.getTransvmInstanceId()));
                    csPortForwarding.setNetworkId(convertEntityService.getNetworkId(csPortForwarding.getTransNetworkId()));
                    csPortForwarding.setIpAddressId(convertEntityService.getIpAddressId(csPortForwarding.getTransIpAddressId()));
                    return csPortForwarding;
                } else {
                    return null;
                }
            }
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT PORT FORWARDING RULE CREATION", e);
            throw new ApplicationException(e.getErrors());
        }
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
}
