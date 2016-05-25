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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Snapshot.Status;
import ck.panda.domain.repository.jpa.SnapshotRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackSnapshotService;
import ck.panda.util.CloudStackVolumeService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
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

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackVolumeService csVolumeService;

    /** Autowired TokenDetails. */
    @Autowired
    private VolumeService volumeService;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackSnapshotService snapshotService;

    /** Token details reference. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Constant for snapshot. */
    public static final String CS_SNAPSHOT = "snapshot";

    /** Constant for Cloud stack volume create response. */
    public static final String CS_CREATE_VOLUME_RESPONSE = "createvolumeresponse";

    @Override
    @PreAuthorize("hasPermission(#snapshot.getSyncFlag(), 'DISK_SNAPSHOT')")
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
                snapshotService.setServer(configServer.setUserServer());
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
     * @param snapshot reference of the Snapshot.
     * @throws Exception error occurs
     */
    private void validateSnapshot(Snapshot snapshot) throws Exception {
        Errors errors = validator.rejectIfNullEntity("snapshots", snapshot);
        errors = validator.validateEntity(snapshot, errors);
        Snapshot validateSnapshot = snapshotRepo.findByNameAndIsActive(snapshot.getName(), true);
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
     * @throws Exception application errors.
     * @param pagingAndSorting do pagination with sorting for departments.
     * @return list of departments.
     */
    public Page<Snapshot> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        if (!convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.ROOT_ADMIN)) {
            return snapshotRepo.findAllByDomainIdAndIsActiveSearchText(user.getDomainId(), true, pagingAndSorting.toPageRequest(), "");
        }
        return snapshotRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }
    @Override
    public List<Snapshot> findAllByActive(Boolean isActive) throws Exception {
        return snapshotRepo.findAllByIsActive(true);
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
                Snapshot snapshot = Snapshot.convert(snapshotListJSON.getJSONObject(i));
                snapshot.setDomainId(convertEntityService.getDomainId(snapshot.getTransDomainId()));
                snapshot.setZoneId(convertEntityService.getZoneId(snapshot.getTransZoneId()));
                snapshot.setVolumeId(convertEntityService.getVolumeId(snapshot.getTransVolumeId()));
                snapshotList.add(snapshot);
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
        if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
            errors.addGlobalError(jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
            throw new ApplicationException(errors);
        } else {
            if(jobId.has(CloudStackConstants.CS_ID)) {
            snapshot.setUuid((String) jobId.get(CloudStackConstants.CS_ID));
            }
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = snapshotService.snapshotJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),CloudStackConstants.JSON);

                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
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
            snapshotService.setServer(configServer.setUserServer());
            snapshotService.deleteSnapshot(CloudStackConstants.JSON, snapshot.getUuid());
        }
        return snapshotRepo.save(snapshot);
    }

    /**
     * Check the Snapshot CS error handling.
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
    public Snapshot createVolume(Snapshot snapshot, Long userId) throws Exception {
        this.validateVolumeUniqueness(snapshot, convertEntityService.getOwnerById(userId).getDomainId(), userId);
        Errors errors = validator.rejectIfNullEntity(CS_SNAPSHOT, snapshot);
        errors = validator.validateEntity(snapshot, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
        Volume volume = convertEntityService.getVolumeById(snapshot.getVolumeId());
        Snapshot snapshotObject = convertEntityService.getSnapshotById(snapshot.getId());
        HashMap<String,String> optional = new HashMap<String, String>();
        optional.put("snapshotid", snapshotObject.getUuid());
        configServer.setUserServer();
        String volumeResponse = csVolumeService.createVolume(snapshot.getTransVolumeName(), convertEntityService.getZoneUuidById(volume.getZoneId()), CloudStackConstants.JSON, optional);
        return snapshot;
    }

    @Override
    public Snapshot findById(Long id) {
        return snapshotRepo.findOne(id);
    }

    /**
     * Validate the Volume.
     *
     * @param volume
     *            reference of the Volume.
     * @param userId
     *            user details
     * @param domainId
     *            domain details
     * @throws Exception
     *             error occurs
     */
    private void validateVolumeUniqueness(Snapshot snapshot, Long domainId, Long userId) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CS_SNAPSHOT, snapshot);
        errors = validator.validateEntity(snapshot, errors);
        Volume validateVolume = volumeService.findByNameAndIsActive(snapshot.getTransVolumeName(), domainId, userId, true);
        if (validateVolume != null && snapshot.getId() != validateVolume.getId()) {
            errors.addGlobalError("error.volume.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Snapshot findByUUID(String uuid) throws Exception {
        return snapshotRepo.findByUUID(uuid);
    }

    @Override
    public Snapshot revertSnapshot(Snapshot snapshot) throws Exception {
         Errors errors = validator.rejectIfNullEntity(CS_SNAPSHOT, snapshot);
         errors = validator.validateEntity(snapshot, errors);
         if (errors.hasErrors()) {
             throw new ApplicationException(errors);
         } else {
             Snapshot snapshotObject = convertEntityService.getSnapshotById(snapshot.getId());
             configServer.setUserServer();
             String snapResponse = snapshotService.revertSnapshot(snapshotObject.getUuid(),CloudStackConstants.JSON);
             JSONObject jobId = new JSONObject(snapResponse).getJSONObject("revertsnapshotresponse");
             if (jobId.has(CloudStackConstants.CS_ERROR_CODE)) {
                 errors = this.validateEvent(errors, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                 throw new ApplicationException(errors);
             }
             snapshot = this.updateSnapshotByJobResponse(snapshot, jobId, errors);
         }
        return snapshot;
    }

    @Override
    public Page<Snapshot> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText) throws Exception {
          User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
          if (!convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.ROOT_ADMIN)) {
              domainId = user.getDomainId();
          }
        return snapshotRepo.findAllByDomainIdAndIsActiveSearchText(domainId, true, pagingAndSorting.toPageRequest(), searchText);
    }
}
