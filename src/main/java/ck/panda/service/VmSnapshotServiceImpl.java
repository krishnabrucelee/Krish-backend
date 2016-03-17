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
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.VmSnapshot.Status;
import ck.panda.domain.repository.jpa.VmSnapshotRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackSnapshotService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.JsonUtil;
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

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** VM snapshot object name. */
    public static final String VM_SNAPSHOT = "vmSnapshot";

    /** VM snapshot memory. */
    public static final String VM_SNAPSHOT_MEMORY = "snapshotmemory";

    @Override
    public VmSnapshot save(VmSnapshot vmSnapshot) throws Exception {
        if (vmSnapshot.getSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity(VM_SNAPSHOT, vmSnapshot);
            errors = validator.validateEntity(vmSnapshot, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                HashMap<String, String> optional = new HashMap<String, String>();
                if (vmSnapshot.getSnapshotMemory()) {
                    optional.put(VM_SNAPSHOT_MEMORY, CloudStackConstants.STATUS_ACTIVE);
                }
                optional.put(CloudStackConstants.CS_NAME, vmSnapshot.getName());
                optional.put(CloudStackConstants.CS_DESCRIPTION, vmSnapshot.getDescription());
                VmInstance vmInstance = virtualMachineService.find(vmSnapshot.getVmId());
                if (vmInstance == null) {
                    errors.addGlobalError("Virtual machine may not be null");
                    throw new ApplicationException(errors);
                }
                config.setUserServer();
                String csResponse = csSnapshotService.createVMSnapshot(vmInstance.getUuid(), optional);
                JSONObject cssnapshot = new JSONObject(csResponse).getJSONObject(CloudStackConstants.CS_CREATE_VM_SNAPSHOT_RESPONSE);
                if (cssnapshot.has(CloudStackConstants.CS_ERROR_CODE)) {
                   if (cssnapshot.has(CloudStackConstants.CS_ERROR_TEXT)) {
                       throw new BadCredentialsException(cssnapshot.getString(CloudStackConstants.CS_ERROR_TEXT));
                   } else {
                       throw new BadCredentialsException("Something went wrong in snapshot creation");
                   }
                } else {
                    vmSnapshot.setDomainId(vmInstance.getDomainId());
                    vmSnapshot.setZoneId(vmInstance.getZoneId());
                    vmSnapshot.setOwnerId(vmInstance.getInstanceOwnerId());
                    vmSnapshot.setIsRemoved(false);
                    config.setUserServer();
                    String snapshotResponse = csSnapshotService.vmSnapshotJobResult(cssnapshot.getString(CloudStackConstants.CS_JOB_ID));
                    JSONObject snapshot = new JSONObject(snapshotResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (snapshot.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                        throw new BadCredentialsException(snapshot.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                    } else if (snapshot.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                        List<VmSnapshot> vmSnapshotList = vmSnapshotRepository.findByVmInstance(vmSnapshot.getVmId(), false);
                        for (VmSnapshot vmSnap : vmSnapshotList) {
                            if (vmSnap.getIsCurrent()) {
                                vmSnap.setIsCurrent(false);
                                vmSnap.setSyncFlag(false);
                                vmSnapshotRepository.save(vmSnap);
                            }
                        }

                        JSONObject jobresultReponse = new JSONObject(snapshotResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE)
                                .getJSONObject(CloudStackConstants.CS_JOB_RESULT).getJSONObject(CloudStackConstants.CS_VM_SNAPSHOT);
                        vmSnapshot.setStatus(Status.valueOf(JsonUtil.getStringValue(jobresultReponse, CloudStackConstants.CS_STATE)));
                        vmSnapshot.setIsCurrent(JsonUtil.getBooleanValue(jobresultReponse, CloudStackConstants.CS_CURRENT));
                        return vmSnapshotRepository.save(vmSnapshot);
                    }
                }
            }
            return vmSnapshot;
        } else {
            return vmSnapshotRepository.save(vmSnapshot);
        }
    }

    @Override
    public VmSnapshot update(VmSnapshot vmSnapshot) throws Exception {
        Errors errors = validator.rejectIfNullEntity(VM_SNAPSHOT, vmSnapshot);
        errors = validator.validateEntity(vmSnapshot, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return vmSnapshotRepository.save(vmSnapshot);
        }
    }

    @Override
    public void delete(VmSnapshot vmSnapshot) throws Exception {
        vmSnapshot.setIsRemoved(true);
        vmSnapshot.setStatus(Status.Expunging);
        vmSnapshotRepository.save(vmSnapshot);
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
        return vmSnapshot;
    }

    @Override
    public Page<VmSnapshot> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return vmSnapshotRepository.findAllByActiveAndExpunging(pagingAndSorting.toPageRequest(), false, Status.Expunging);
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
        config.setUserServer();
        switch (event) {
        case EventTypes.EVENT_VM_SNAPSHOT_REVERT:
            try {
                String snapshotResponse = csSnapshotService.revertToVMSnapshot(vmSnapshot.getUuid());
                JSONObject snapshots = new JSONObject(snapshotResponse).getJSONObject(CloudStackConstants.CS_REVERT_VM_SNAPSHOT_RESPONSE);
                if (snapshots.has(CloudStackConstants.CS_JOB_ID)) {
                    String snapshot = csSnapshotService.vmSnapshotJobResult(snapshots.getString(CloudStackConstants.CS_JOB_ID));
                    JSONObject jobresult = new JSONObject(snapshot).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                        throw new BadCredentialsException(jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                    } else {
                        List<VmSnapshot> vmSnapshotList = vmSnapshotRepository.findByVmInstance(vmSnapshot.getVmId(), false);
                        for (VmSnapshot vmSnap : vmSnapshotList) {
                            if (vmSnap.getIsCurrent()) {
                                vmSnap.setIsCurrent(false);
                                vmSnap.setSyncFlag(false);
                                vmSnapshotRepository.save(vmSnap);
                            }
                        }
                        vmSnapshot.setStatus(Status.valueOf(EventTypes.EVENT_READY));
                        vmSnapshot.setIsCurrent(true);
                    }
                }
            } catch (BadCredentialsException e) {
                LOGGER.error("ERROR AT Restore VM Snapshot", e);
                throw new BadCredentialsException(e.getMessage());
            }
            break;
        case EventTypes.EVENT_VM_SNAPSHOT_DELETE:
            try {
                String snapshotResponse = csSnapshotService.deleteVMSnapshot(vmSnapshot.getUuid());
                JSONObject snapshots = new JSONObject(snapshotResponse).getJSONObject(CloudStackConstants.CS_DELETE_VM_SNAPSHOT_RESPONSE);
                if (snapshots.has(CloudStackConstants.CS_JOB_ID)) {
                    String snapshot = csSnapshotService.vmSnapshotJobResult(snapshots.getString(CloudStackConstants.CS_JOB_ID));
                    JSONObject jobresult = new JSONObject(snapshot).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                        throw new BadCredentialsException(jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT).getString(CloudStackConstants.CS_ERROR_TEXT));
                    } else {
                        vmSnapshot.setStatus(Status.Expunging);
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
        vmsnapshotMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.CS_ACTIVE_VM);
        config.setServer(1L);
        // 1. Get the list of vm snapshot from CS server using CS connector
        String response = csSnapshotService.listVMSnapshot(vmsnapshotMap);
        JSONArray vmSnapshotListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CloudStackConstants.CS_LIST_VM_SNAPSHOT_RESPONSE);
        if (responseObject.has(VM_SNAPSHOT)) {
            vmSnapshotListJSON = responseObject.getJSONArray(VM_SNAPSHOT);
            // 2. Iterate the json list, convert the single json entity to vm
            // snapshot.
            for (int i = 0, size = vmSnapshotListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to vm snapshot entity
                // and Add
                // the converted vm snapshot entity to list
                VmSnapshot vmSnapshot = VmSnapshot.convert(vmSnapshotListJSON.getJSONObject(i));
                if (vmSnapshot.getTransvmInstanceId() != null) {
                    vmSnapshot.setVmId(convertEntityService.getVmInstanceId(vmSnapshot.getTransvmInstanceId()));
                    vmSnapshot.setDomainId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getDomainId());
                    vmSnapshot.setOwnerId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getInstanceOwnerId());
                    vmSnapshot.setZoneId(convertEntityService.getVm(vmSnapshot.getTransvmInstanceId()).getZoneId());
                }
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
        VmSnapshot vmsnapshot = vmSnapshotRepository.findByUUID(vmId);
        return vmsnapshot;
    }

    @Override
    public Page<VmSnapshot> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception {
        return vmSnapshotRepository.findAllByDomainIdAndIsActive(domainId, false, Status.Expunging, pagingAndSorting.toPageRequest());
    }

}
