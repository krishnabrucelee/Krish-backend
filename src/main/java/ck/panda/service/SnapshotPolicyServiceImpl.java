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
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Snapshot;
import ck.panda.domain.entity.SnapshotPolicy;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.repository.jpa.SnapshotPolicyRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackSnapshotService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;

/**
 * SnapshotPolicy service implementation class.
 *
 */
@Service
public class SnapshotPolicyServiceImpl implements SnapshotPolicyService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DomainServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private SnapshotPolicyRepository policyRepo;


    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** CloudStack Domain service for connectivity with cloudstack. */
    @Autowired
    private CloudStackSnapshotService snapshotService;


    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Autowired TokenDetails. */
    @Autowired
    private VolumeService volumeService;

    /** Constant for snapshot. */
    public static final String CS_SNAPSHOT = "snapshot";


    /** object(server) created for CloudStackServer. */
    @Autowired
    private ConfigUtil configServer;

    @Override
    public SnapshotPolicy save(SnapshotPolicy snapshot) throws Exception {
        if(snapshot.getSyncFlag()) {
        Errors errors = validator.rejectIfNullEntity(CS_SNAPSHOT, snapshot);
        errors = validator.validateEntity(snapshot, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            Volume volume = convertEntityService.getVolumeById(snapshot.getVolumeId());
            configServer.setUserServer();
            HashMap<String, String> optional = new HashMap<String,String>();
            if(snapshot.getIntervalType() != null ) {
              switch(snapshot.getIntervalType()) {
              case HOURLY :
                  String scheduleHour = snapshot.getMinutes();
                  snapshot.setScheduleTime(scheduleHour);
                  break;
              case DAILY :
                  String scheduleTime = snapshot.getHours()+ ':' + snapshot.getMinutes();
                  snapshot.setScheduleTime(scheduleTime);
                  break;
              case MONTHLY :
                  String scheduleMonth = snapshot.getHours()+ ':' + snapshot.getMinutes() + ':' + snapshot.getDayOfMonth();
                  snapshot.setScheduleTime(scheduleMonth);
                  break;
              case WEEKLY :
                  String scheduleWeekly = snapshot.getHours()+ ':' + snapshot.getMinutes() + ':' + snapshot.getDayOfWeek();
                  snapshot.setScheduleTime(scheduleWeekly);
                  break;
              }
             }
            optional.put("schedule", snapshot.getScheduleTime());
            String snapResponse = snapshotService.createSnapshotPolicy(String.valueOf(snapshot.getIntervalType()).toLowerCase(), snapshot.getMaximumSnapshots().toString(), snapshot.getTimeZone(), volume.getUuid(),CloudStackConstants.JSON,optional);
            JSONObject createSnapolicyResponse = new JSONObject(snapResponse).getJSONObject("createsnapshotpolicyresponse");
            if (createSnapolicyResponse.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateEvent(errors, createSnapolicyResponse.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            }
            JSONObject snapPolicy = createSnapolicyResponse.getJSONObject("snapshotpolicy");
            snapshot.setUuid((String) snapPolicy.get(CloudStackConstants.CS_ID));
            snapshot.setIsActive(true);
        }
        }
        return policyRepo.save(snapshot);
    }

    @Override
    public SnapshotPolicy update(SnapshotPolicy snapshot) throws Exception {
        return policyRepo.save(snapshot);
    }

    @Override
    public void delete(SnapshotPolicy snapshot) throws Exception {

        policyRepo.delete(snapshot);
    }

    @Override
    public void delete(Long id) throws Exception {
        policyRepo.delete(id);
    }

    @Override
    public SnapshotPolicy find(Long id) throws Exception {
        SnapshotPolicy snapshot = policyRepo.findOne(id);
        return snapshot;
    }

    @Override
    public Page<SnapshotPolicy> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return policyRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<SnapshotPolicy> findAll() throws Exception {
        return (List<SnapshotPolicy>) policyRepo.findAll();
    }

    @Override
    public List<SnapshotPolicy> findAllFromCSServer() throws Exception {
         List<Volume> volumeList = volumeService.findAllByIsActive(true);
        List<SnapshotPolicy> policyList = new ArrayList<SnapshotPolicy>();
        for (Volume volume : volumeList) {
            HashMap<String, String> policyMap = new HashMap<String, String>();
            policyMap.put("volumeid", volume.getUuid());
            configServer.setServer(1L);
            // 1. Get the list of pods from CS server using CS connector
            String response = snapshotService.listSnapshotPolicies("json",policyMap);
            JSONObject responseObject = new JSONObject(response).getJSONObject("listsnapshotpoliciesresponse");
            if (response!= null && responseObject.has("snapshotpolicy")) {
            JSONArray policyListJSON = responseObject.getJSONArray("snapshotpolicy");
            // 2. Iterate the json list, convert the single json entity to snapshot
            for (int i = 0, size = policyListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to snapshot poicy entity and add
                // the converted snapshot entity to list
                SnapshotPolicy snapshot = SnapshotPolicy.convert(policyListJSON.getJSONObject(i));
                snapshot.setVolumeId(convertEntityService.getVolumeId(snapshot.getTransVolumeId()));
                policyList.add(snapshot);
            }
        }
        }
        return policyList;
    }

    @Override
    public SnapshotPolicy findByUUID(String uuid) throws Exception {
        return policyRepo.findByUUID(uuid);
    }

    @Override
    public SnapshotPolicy softDelete(Long id) throws Exception {
          SnapshotPolicy snapshotObject = convertEntityService.getSnapshotPolicyById(id);
          configServer.setUserServer();
          String deleteSnapResponse = snapshotService.deleteSnapshotPolicies(snapshotObject.getUuid(), CloudStackConstants.JSON);
          JSONObject deleteSnapolicyResponse = new JSONObject(deleteSnapResponse).getJSONObject("deletesnapshotpoliciesresponse");
          if (deleteSnapolicyResponse.has(CloudStackConstants.CS_ERROR_CODE)) {
              throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, deleteSnapolicyResponse.getString(CloudStackConstants.CS_ERROR_TEXT));
          }
          snapshotObject.setIsActive(false);
     return policyRepo.save(snapshotObject);
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
    public List<SnapshotPolicy> findAllByVolumeAndIsActive(Long volumeId, Boolean isActive) throws Exception {
        return policyRepo.findByVolumeAndIsActive(volumeId, true);
    }
}
