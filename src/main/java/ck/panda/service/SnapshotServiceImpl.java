package ck.panda.service;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.entity.Snapshot.Status;
import ck.panda.domain.repository.jpa.SnapshotRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackSnapshotService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.ConvertUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Snapshot service implementation class.
 *
 */
@Service
public class SnapshotServiceImpl implements SnapshotService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SnapshotServiceImpl.class);

    /** Snapshot repository reference. */
    @Autowired
    private SnapshotRepository snapshotRepo;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Convert entity repository reference. */
    @Autowired
    private ConvertUtil convertUtil;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackSnapshotService snapshotService;

    @Override
    public Snapshot save(Snapshot snapshot) throws Exception {
        if (snapshot.getSyncFlag()) {
            this.validateSnapshot(snapshot);
            Errors errors = validator.rejectIfNullEntity("snapshots", snapshot);
            errors = validator.validateEntity(snapshot, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                // set server for maintain session with configuration values
                snapshot.setVolumeId(snapshot.getVolume().getId());
                snapshot.setIsActive(true);
                snapshotService.setServer(configServer.setServer(1L));
                HashMap<String, String> snapMap = new HashMap<String, String>();
                snapMap.put("name", snapshot.getName());
                String snapresponse = snapshotService.createSnapshot(snapshot.getVolume().getUuid(), snapMap, "json");
                LOGGER.debug(snapshot.getUuid());
                JSONObject jobId = new JSONObject(snapresponse).getJSONObject("createsnapshotresponse");
                if (jobId.has("errorcode")) {
                    errors = this.validateEvent(errors, jobId.getString("errortext"));
                    throw new ApplicationException(errors);
                }
                snapshot = this.updateSnapshotByJobResponse(snapshot, jobId, errors);
            }

        }
        return snapshotRepo.save(snapshot);
    }

    /**
     * Validate the Snapshot.
     *
     * @param department reference of the Snapshot.
     * @throws Exception error occurs
     */
    private void validateSnapshot(Snapshot snapshot) throws Exception {
        Errors errors = validator.rejectIfNullEntity("snapshots", snapshot);
        errors = validator.validateEntity(snapshot, errors);
        Snapshot validateSnapshot = snapshotRepo.findByNameAndIsActive(snapshot.getName(),true);
        if (validateSnapshot != null && snapshot.getId() != validateSnapshot.getId()) {
            errors.addFieldError("name", "snapshot.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Snapshot update(Snapshot snapshot) throws Exception {
        LOGGER.debug(snapshot.getUuid());
        return snapshotRepo.save(snapshot);
    }

    @Override
    public void delete(Snapshot snapshot) throws Exception {
        snapshotRepo.delete(snapshot);
    }

    @Override
    public void delete(Long id) throws Exception {
        snapshotRepo.delete(id);
    }

    @Override
    public Snapshot find(Long id) throws Exception {
        return snapshotRepo.findOne(id);
    }

    @Override
    public Page<Snapshot> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return snapshotRepo.findAll(pagingAndSorting.toPageRequest());
    }

    /**
     * Find all the departments with pagination.
     *
     * @throws Exception
     *             application errors.
     * @param pagingAndSorting
     *            do pagination with sorting for departments.
     * @return list of departments.
     */
    public Page<Snapshot> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        return snapshotRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Snapshot> findAll() throws Exception {
        return (List<Snapshot>) snapshotRepo.findAll();
    }

    @Override
    public List<Snapshot> findAllFromCSServer() throws Exception {
        List<Snapshot> snapshotList = new ArrayList<Snapshot>();
        HashMap<String, String> snapshotMap = new HashMap<String, String>();
        snapshotMap.put("listall", "true");
        // 1. Get the list of domains from CS server using CS connector
        String response = snapshotService.listSnapshots(snapshotMap, "json");
        JSONArray snapshotListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listsnapshotsresponse");
        if (responseObject.has("snapshot")) {
            snapshotListJSON = responseObject.getJSONArray("snapshot");
            // 2. Iterate the json list, convert the single json entity to
            // domain
            for (int i = 0, size = snapshotListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to Domain entity and
                // Add
                // the converted snapshot entity to list
                snapshotList.add(Snapshot.convert(snapshotListJSON.getJSONObject(i), convertUtil));
            }
        }
        return snapshotList;
    }

    /**
     * Update status of the job by response from cloudstack.
     *
     * @param snapshot values from cloudstack.
     * @param jobId for snapshot
     * @param errors in job response
     * @return response from cloudstack.
     * @throws Exception unhandled errors.
     */
    private Snapshot updateSnapshotByJobResponse(Snapshot snapshot, JSONObject jobId, Errors errors) throws Exception {
        if (jobId.has("errorcode")) {
            errors.addGlobalError(jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
            snapshot.setUuid((String) jobId.get("id"));
            if (jobId.has("jobid")) {
                String jobResponse = snapshotService.snapshotJobResult(jobId.getString("jobid"), "json");

                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString("jobstatus").equals("0")) {
                    snapshot.setStatus(Status.BACKEDUP);
                }
            }
        }
        return snapshot;
    }

    @Override
    public Snapshot softDelete(Snapshot snapshot) throws Exception {
        snapshot.setIsActive(false);
        snapshot.setStatus(Snapshot.Status.DESTROYED);
        if (snapshot.getSyncFlag()) {
            // set server for finding value in configuration
            snapshotService.setServer(configServer.setServer(1L));
            snapshotService.deleteSnapshot("json", snapshot.getUuid());
        }
        return snapshotRepo.save(snapshot);
    }

    /**
     * Check the Snapshot CS error handling.
     *
     * @param errors
     *            error creating status.
     * @param errmessage
     *            error message.
     * @return errors.
     * @throws Exception
     *             unhandled exceptions.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }
}
