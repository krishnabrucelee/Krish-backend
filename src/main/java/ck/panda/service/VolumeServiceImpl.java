/**
 *
 */
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
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.Status;
import ck.panda.domain.repository.jpa.VolumeRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackVolumeService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;

/**
 * Volume Service Implementation.
 */
@Service
public class VolumeServiceImpl implements VolumeService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VolumeServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** VolumeRepository repository reference. */
    @Autowired
    private VolumeRepository volumeRepo;

    /** Lists types of Volumes in cloudstack server. */
    @Autowired
    private CloudStackVolumeService csVolumeService;

    /** Cloud stack configuration utility class. */
    @Autowired
    private ConfigUtil config;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    @Override
    public Volume save(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            this.validateVolumeUniqueness(volume);
            Errors errors = validator.rejectIfNullEntity("volumes", volume);
            errors = validator.validateEntity(volume, errors);

            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                createVolume(volume, errors);
                return volumeRepo.save(volume);
            }
        } else {
            return volumeRepo.save(volume);
        }
    }

    @Override
    public Volume update(Volume volume) throws Exception {
        Errors errors = validator.rejectIfNullEntity("volumes", volume);
        errors = validator.validateEntity(volume, errors);

        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            return volumeRepo.save(volume);
        }
    }

    @Override
    public void delete(Volume volume) throws Exception {
        volumeRepo.delete(volume);
    }

    @Override
    public void delete(Long id) throws Exception {
        volumeRepo.delete(id);
    }

    @Override
    public Volume find(Long id) throws Exception {
        return null;
    }

    @Override
    public Page<Volume> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return volumeRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<Volume> findAll() throws Exception {
        return (List<Volume>) volumeRepo.findAll();
    }

    /**
     * To set optional values by validating null and empty parameters.
     *
     * @param volume optional storage offering values
     * @return optional values
     */
    public HashMap<String, String> optional(Volume volume) {
        HashMap<String, String> optional = new HashMap<String, String>();

        if (volume.getDiskSize() != null) {
            optional.put("size", volume.getDiskSize().toString());
        }

        if (volume.getDiskMaxIops() != null) {
            optional.put("maxiops", volume.getDiskMaxIops().toString());
        }

        if (volume.getDiskMinIops() != null) {
            optional.put("miniops", volume.getDiskMinIops().toString());
        }
        return optional;
    }

    @Override
    public List<Volume> findAllFromCSServer() throws Exception {
        List<Volume> volumeList = new ArrayList<Volume>();
        HashMap<String, String> volumeMap = new HashMap<String, String>();
        volumeMap.put("listall", "true");
        // 1. Get the list of Volume from CS server using CS connector
        String response = csVolumeService.listVolumes("json", volumeMap);
        JSONArray volumeListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject("listvolumesresponse");
        if (responseObject.has("volume")) {
            volumeListJSON = responseObject.getJSONArray("volume");
        // 2. Iterate the json list, convert the single json entity to
        // Volume
        for (int i = 0, size = volumeListJSON.length(); i < size; i++) {
            // 2.1 Call convert by passing JSONObject to Volume entity
            // and Add
            // the converted Volume entity to list
            Volume volume = Volume.convert(volumeListJSON.getJSONObject(i));
            volume.setZoneId(convertEntityService.getZoneId(volume.getTransvmInstanceId()));
            volume.setStorageOfferingId(convertEntityService.getStorageOfferId(volume.getTransStorageOfferingId()));
            volume.setVmInstanceId(convertEntityService.getVmInstanceId(volume.getTransvmInstanceId()));
            volumeList.add(volume);
        }
        }
        return volumeList;
    }

    /**
     * Cloud stack create Volume.
     *
     * @param volume Volume
     * @param errors global error and field errors
     * @throws Exception error
     */
    private void createVolume(Volume volume, Errors errors) throws Exception {
        config.setServer(1L);
        String volumeS = csVolumeService.createVolume(volume.getName(), volume.getStorageOffering().getUuid(),
                volume.getZone().getUuid(), "json", optional(volume));
        LOGGER.info("Volume create response " + volumeS);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject("createvolumeresponse");

        if (jobId.has("errorcode")) {
            errors = this.validateEvent(errors, jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
            volume.setUuid((String) jobId.get("id"));
            if (jobId.has("jobid")) {
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString("jobid"), "json");

                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.getString("jobstatus").equals("0")) {
                    volume.setStatus(Status.valueOf(EventTypes.Allocated));
                }
                volume.setIsActive(true);
                volume.setDiskSize(volume.getDiskSize());
                volume.setStorageOfferingId(volume.getStorageOffering().getId());
                volume.setZoneId(volume.getZone().getId());
                volume.setVolumeType(Volume.VolumeType.DATADISK);
                volume.setCreatedDateTime(volume.getCreatedDateTime());
            }

        }
    }

    /**
     * Check the Storage offering CS error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception error
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    /**
     * Validate the Volume.
     *
     * @param volume reference of the Volume.
     * @throws Exception error occurs
     */
    private void validateVolumeUniqueness(Volume volume) throws Exception {
        Errors errors = validator.rejectIfNullEntity("volumes", volume);
        errors = validator.validateEntity(volume, errors);
        Volume validateVolume = volumeRepo.findByNameAndIsActive(volume.getName(), true);
        if (validateVolume != null && volume.getId() != validateVolume.getId()) {
            errors.addFieldError("name", "volume.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Volume findByUUID(String uuid) throws Exception {
        return volumeRepo.findByUUID(uuid);
    }

    @Override
    public Volume attachVolume(Volume volume) throws Exception {
        Errors errors = validator.rejectIfNullEntity("volumes", volume);
        errors = validator.validateEntity(volume, errors);
        config.setServer(1L);
        String volumeS = csVolumeService.attachVolume(volume.getUuid(), volume.getVmInstance().getUuid(),"json", optional(volume));
        JSONObject jobId = new JSONObject(volumeS).getJSONObject("attachvolumeresponse");

        if (jobId.has("errorcode")) {
            errors = this.validateEvent(errors, jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
//            volume.setUuid((String) jobId.get("jobid"));
            volume.setVmInstanceId(volume.getVmInstance().getId());
            if (jobId.has("jobid")) {
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");

                if (jobresult.has("volume")) {
                    volume.setUuid((String) jobresult.get("id"));
                }
                if (jobresult.getString("jobstatus").equals("0")) {
                       volume.setStatus(Status.Ready);
                }
            }
            volumeRepo.save(volume);
        }
        return volume;
    }

    @Override
    public Volume detachVolume(Volume volume) throws Exception {
        Errors errors = validator.rejectIfNullEntity("volumes", volume);
        errors = validator.validateEntity(volume, errors);
        config.setServer(1L);
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put("id", volume.getUuid());
        //optional.put("virtualmachineid", volume.getVmInstance().getUuid());
        String volumeS = csVolumeService.detachVolume("json",  optional);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject("detachvolumeresponse");

        if (jobId.has("errorcode")) {
            errors = this.validateEvent(errors, jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
//            volume.setUuid((String) jobId.get("jobid"));
            volume.setVmInstanceId(null);
            if (jobId.has("jobid")) {
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                if (jobresult.has("volume")) {
                    volume.setUuid((String) jobresult.get("id"));
                }
                if (jobresult.getString("jobstatus").equals("0")) {
                       volume.setStatus(Status.Destroy);
                }
            }
            volumeRepo.save(volume);
        }
        return volume;
    }

    @Override
    public Volume resizeVolume(Volume volume) throws Exception {
        Errors errors = validator.rejectIfNullEntity("volumes", volume);
        errors = validator.validateEntity(volume, errors);
        config.setServer(1L);
        String volumeS = csVolumeService.resizeVolume("json", optional(volume));
        JSONObject jobId = new JSONObject(volumeS).getJSONObject("attachvolumeresponse");

        if (jobId.has("errorcode")) {
            errors = this.validateEvent(errors, jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
//            volume.setUuid((String) jobId.get("jobid"));
            volume.setVmInstanceId(volume.getVmInstance().getId());
            if (jobId.has("jobid")) {
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");

                if (jobresult.has("volume")) {
                    volume.setUuid((String) jobresult.get("id"));
                }
                if (jobresult.getString("jobstatus").equals("0")) {
                       volume.setStatus(Status.Ready);
                }
            }
            volumeRepo.save(volume);
        }
        return volume;
    }

    @Override
        public Volume softDelete(Volume volume) throws Exception {
             volume.setIsActive(false);
             volume.setStatus(Volume.Status.Destroy);
             return volumeRepo.save(volume);
        }

}
