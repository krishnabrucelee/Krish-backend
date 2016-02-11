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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.CloudStackConfiguration;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.VmSnapshot.Status;
import ck.panda.domain.repository.jpa.VmSnapshotRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackServer;
import ck.panda.util.CloudStackSnapshotService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/**
 * VmSnapshot Service Implementation.
 */
@Service
public class VmSnapshotServiceImpl implements VmSnapshotService {
    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VmSnapshotServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Vm snapshot repository reference. */
    @Autowired
    private VmSnapshotRepository vmSnapshotRepository;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Virtual Machine service reference. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** CloudStack connector reference for snapshot. */
    @Autowired
    private CloudStackSnapshotService csSnapshotService;

    /** CloudStack connector. */
    @Autowired
    private CloudStackServer server;

    /** CloudStack configuration . */
    @Autowired
    private CloudStackConfigurationService cloudConfigService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    @Override
    public VmSnapshot save(VmSnapshot vmSnapshot) throws Exception {
        if (vmSnapshot.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("vmSnapshot", vmSnapshot);
            errors = validator.validateEntity(vmSnapshot, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optional = new HashMap<String, String>();
                CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
                LOGGER.debug("Cloud stack connectivity at Snapshot", cloudConfig.getApiKey());

                if (vmSnapshot.getSnapshotMemory()) {
                    optional.put("snapshotmemory", "true");
                }
                optional.put("name", vmSnapshot.getName());
                optional.put("description", vmSnapshot.getDescription());
                VmInstance vmInstance = virtualMachineService.find(vmSnapshot.getVmId());
                if (vmInstance == null) {
                    errors.addGlobalError("Virtual machine may not be null");
                    throw new ApplicationException(errors);
                }
                config.setUserServer();
                String csResponse = csSnapshotService.createVMSnapshot(vmInstance.getUuid(), optional);
                JSONObject cssnapshot = new JSONObject(csResponse).getJSONObject("createvmsnapshotresponse");
                if (cssnapshot.has("errorcode")) {
                    errors = this.validateEvent(errors, cssnapshot.getString("errortext"));
                    throw new ApplicationException(errors);
                } else {
                    vmSnapshot.setDomainId(vmInstance.getDomainId());
                    vmSnapshot.setZoneId(vmInstance.getZoneId());
                    vmSnapshot.setOwnerId(vmInstance.getInstanceOwnerId());
                    vmSnapshot.setIsRemoved(false);
                    config.setUserServer();
                    String snapshotResponse = csSnapshotService.vmSnapshotJobResult(cssnapshot.getString("jobid"));
                    JSONObject snapshot = new JSONObject(snapshotResponse).getJSONObject("queryasyncjobresultresponse");
                    if (snapshot.getString("jobstatus").equals("2")) {
                        vmSnapshot.setStatus(Status.valueOf(EventTypes.EVENT_ERROR));
                    } else if (snapshot.getString("jobstatus").equals("0")) {
                        vmSnapshot.setStatus(Status.valueOf(EventTypes.EVENT_CREATE));
                    } else {
                        vmSnapshot.setStatus(Status.valueOf(EventTypes.EVENT_READY));
                    }
                }
            }

            return vmSnapshotRepository.save(vmSnapshot);
        } else {
            return vmSnapshotRepository.save(vmSnapshot);
        }
    }

    @Override
    public VmSnapshot update(VmSnapshot vmSnapshot) throws Exception {
        Errors errors = validator.rejectIfNullEntity("vmSnapshot", vmSnapshot);
        errors = validator.validateEntity(vmSnapshot, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return vmSnapshotRepository.save(vmSnapshot);
        }
    }

    @Override
    public void delete(VmSnapshot vmSnapshot) throws Exception {
        vmSnapshotRepository.delete(vmSnapshot);
    }

    @Override
    public void delete(Long id) throws Exception {
        vmSnapshotRepository.delete(id);
    }

    @Override
    public VmSnapshot find(Long id) throws Exception {
        VmSnapshot vmSnapshot = vmSnapshotRepository.findOne(id);
        if (vmSnapshot == null) {
            throw new EntityNotFoundException("Vm snapshot not found");
        }
        return null;
    }

    @Override
    public Page<VmSnapshot> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return vmSnapshotRepository.findAllByActive(pagingAndSorting.toPageRequest(), false);
    }

    @Override
    public List<VmSnapshot> findAll() throws Exception {
        return (List<VmSnapshot>) vmSnapshotRepository.findAll();
    }

    @Override
    public VmSnapshot findByUUID(String uuid) {
        return vmSnapshotRepository.findByUUID(uuid);
    }

    @Override
    public VmSnapshot vmSnapshotEventHandle(String snapshotId, String event) throws Exception {
        VmSnapshot vmSnapshot = getCSConnector(snapshotId);
        HashMap<String, String> optional = new HashMap<String, String>();
        switch (event) {
        case EventTypes.EVENT_VM_SNAPSHOT_REVERT:
            try {
            	config.setUserServer();
                String snapshotResponse = csSnapshotService.revertToVMSnapshot(vmSnapshot.getUuid());
                JSONObject snapshots = new JSONObject(snapshotResponse).getJSONObject("reverttovmsnapshotresponse");
                if (snapshots.has("jobid")) {
                	config.setUserServer();
                    String snapshot = csSnapshotService.vmSnapshotJobResult(snapshots.getString("jobid"));
                    JSONObject jobresult = new JSONObject(snapshot).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        throw new BadCredentialsException(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vmSnapshot.setStatus(Status.valueOf(EventTypes.EVENT_READY));
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT Restore VM Snapshot", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_SNAPSHOT_DELETE:
            try {
            	config.setUserServer();
                String snapshotResponse = csSnapshotService.deleteVMSnapshot(vmSnapshot.getUuid());
                JSONObject snapshots = new JSONObject(snapshotResponse).getJSONObject("deletevmsnapshotresponse");
                if (snapshots.has("jobid")) {
                	config.setUserServer();
                    String snapshot = csSnapshotService.vmSnapshotJobResult(snapshots.getString("jobid"));
                    JSONObject jobresult = new JSONObject(snapshot).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        throw new BadCredentialsException(jobresult.getJSONObject("jobresult").getString("errortext"));
                    } else {
                        vmSnapshot.setIsRemoved(true);
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT Delete VM Snapshot", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        default:
            LOGGER.debug("No VM Snapshot Action ", event);
        }
        return vmSnapshotRepository.save(vmSnapshot);
    }

    @Override
    public List<VmSnapshot> findAllFromCSServer() throws Exception {
        List<VmSnapshot> vmsnapshotList = new ArrayList<VmSnapshot>();
        HashMap<String, String> vmsnapshotMap = new HashMap<String, String>();
        vmsnapshotMap.put("listall", "true");
        config.setServer(1L);
        // 1. Get the list of vm snapshot from CS server using CS connector
        String response = csSnapshotService.listVMSnapshot(vmsnapshotMap);
        JSONArray vmSnapshotListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listvmsnapshotresponse");
        if (responseObject.has("vmSnapshot")) {
            vmSnapshotListJSON = responseObject.getJSONArray("vmSnapshot");
            // 2. Iterate the json list, convert the single json entity to vm
            // snapshot.
            for (int i = 0, size = vmSnapshotListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to vm snapshot entity
                // and Add
                // the converted vm snapshot entity to list
                VmSnapshot vmSnapshot = VmSnapshot.convert(vmSnapshotListJSON.getJSONObject(i));
                vmSnapshot.setVmId(convertEntityService.getVmInstanceId(vmSnapshot.getTransvmInstanceId()));
                vmSnapshot.setDomainId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getDomainId());
                vmSnapshot
                        .setOwnerId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getInstanceOwnerId());
                vmSnapshot.setZoneId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getZoneId());

                vmsnapshotList.add(vmSnapshot);
            }
        }
        return vmsnapshotList;
    }

    @Override
    public List<VmSnapshot> findByVmInstance(Long vmId, Boolean isRemoved) {
        return vmSnapshotRepository.findByVmInstance(vmId, false);
    }

    /**
     * Open connection for CS API call.
     *
     * @param vmId VM snapshot id.
     * @return VmSnapshot.
     * @throws Exception unhandled exceptions.
     */
    public VmSnapshot getCSConnector(String vmId) throws Exception {
        // instantiate Cloud Stack connector for an snapshot service.
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
        csSnapshotService.setServer(server);
        LOGGER.debug("Cloud stack connectivity at Snapshot", cloudConfig.getApiKey());
        VmSnapshot vmsnapshot = vmSnapshotRepository.findByUUID(vmId);
        return vmsnapshot;
    }

    /**
     * Check the snapshot CS error handling.
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
     * Open connection for CS API call.
     *
     * @throws Exception unhandled exceptions.
     */
    public void getCSConnector() throws Exception {
        // instantiate Cloud Stack connector for an snapshot service.
        CloudStackConfiguration cloudConfig = cloudConfigService.find(1L);
        server.setServer(cloudConfig.getApiURL(), cloudConfig.getSecretKey(), cloudConfig.getApiKey());
        csSnapshotService.setServer(server);
        LOGGER.debug("Cloud stack connectivity at Snapshot", cloudConfig.getApiKey());
    }

}
