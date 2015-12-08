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
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.Status;
import ck.panda.domain.repository.jpa.DepartmentReposiory;
import ck.panda.domain.repository.jpa.DomainRepository;
import ck.panda.domain.repository.jpa.VirtualMachineRepository;
import ck.panda.domain.repository.jpa.VolumeRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackVolumeService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
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

    /** Domain repository reference. */
    @Autowired
    private DomainRepository domainRepository;

    /** Department repository reference. */
    @Autowired
    private DepartmentReposiory departmentRepository;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Autowired TokenDetails. */
    @Autowired
    private VirtualMachineRepository virtualMachineRepo;

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
    public Page<Volume> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            return volumeRepo.findByDomainAndIsActive(domain.getId(), true, pagingAndSorting.toPageRequest());
        }
        return volumeRepo.findAllByIsActive(pagingAndSorting.toPageRequest(), true);
    }

    @Override
    public List<Volume> findAll() throws Exception {
        return (List<Volume>) volumeRepo.findAll();
    }

    @Override
    public List<Volume> findByInstanceAndIsActive(Long volume) throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
        return volumeRepo.findByInstanceAndDomainIsActive(domain.getId(), volume, true);
        }
        return volumeRepo.findByInstanceAndIsActive(volume, true);
    }

    @Override
    public List<Volume> findByVolumeTypeAndIsActive() throws Exception {
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
        return volumeRepo.findByVolumeTypeAndIsActive(domain.getId(), Volume.VolumeType.DATADISK, true);
        }
        return volumeRepo.findByVolumeTypeAndIsActive(Volume.VolumeType.DATADISK, true);
    }

    /**
     * To set optional values by validating null and empty parameters.
     *
     * @param volume optional storage offering values
     * @return optional values
     * @throws Exception error
     */
    public HashMap<String, String> optional(Volume volume) throws Exception {
        HashMap<String, String> optional = new HashMap<String, String>();

        if (volume.getDiskSize() != null) {
            optional.put("size", volume.getDiskSize().toString());
        }

        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            optional.put("domainid", departmentRepository.findOne(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getDomain().getUuid());
            optional.put("account", departmentRepository.findOne(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName());
        } else {
            optional.put("domainid", tokenDetails.getTokenDetails("domainid"));
//            optional.put("account", departmentRepository.findOne(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName());
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
            volume.setZoneId(convertEntityService.getZoneId(volume.getTransZoneId()));
            volume.setDomainId(convertEntityService.getDomainId(volume.getTransDomainId()));
            volume.setDepartmentId(departmentRepository.findByUsername(volume.getTransDepartmentName(), true).getId());
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
                    volume.setStatus(Status.valueOf(EventTypes.ALLOCATED));
                }
                volume.setIsActive(true);
                volume.setStorageOfferingId(volume.getStorageOffering().getId());
                if (volume.getDiskSize() != null) {
                    volume.setDiskSize(volume.getDiskSize());
                } else {
                    volume.setDiskSize(volume.getStorageOffering().getDiskSize());
                }
                volume.setZoneId(volume.getZone().getId());
                volume.setDomainId(Long.parseLong(tokenDetails.getTokenDetails("domainid")));
                Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
                if (domain != null && !domain.getName().equals("ROOT")) {
                volume.setDepartmentId(Long.parseLong(tokenDetails.getTokenDetails("departmentid")));
                }
                volume.setVolumeType(Volume.VolumeType.DATADISK);
                volume.setCreatedDateTime(volume.getCreatedDateTime());
                volume.setDiskMaxIops(volume.getDiskMaxIops());
                volume.setDiskMinIops(volume.getDiskMinIops());
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
        Volume validateVolume = volumeRepo.findByNameAndIsActive(volume.getName(), Long.parseLong(tokenDetails.getTokenDetails("domainid")), Long.parseLong(tokenDetails.getTokenDetails("departmentid")), true);
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
        HashMap<String, String> optional = new HashMap<String, String>();
        if (volume.getVmInstanceId() != null) {
        VmInstance instance = virtualMachineRepo.findOne(volume.getVmInstanceId());
        optional.put("virtualmachineid", instance.getUuid());
        } else {
            optional.put("virtualmachineid", volume.getVmInstance().getUuid());
        }
        String volumeS = csVolumeService.attachVolume(volume.getUuid(), "json", optional);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject("attachvolumeresponse");

        if (jobId.has("errorcode")) {
            errors = this.validateEvent(errors, jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
//            volume.setUuid((String) jobId.get("jobid"));
            if (volume.getVmInstanceId() != null) {
                volume.setVmInstanceId(volume.getVmInstanceId());
            } else {
                volume.setVmInstanceId(volume.getVmInstance().getId());
            }
            if (jobId.has("jobid")) {
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");

                if (jobresult.has("volume")) {
                    volume.setUuid((String) jobresult.get("id"));
                    volume.setVmInstanceId(volume.getVmInstance().getId());
                }
                if (jobresult.getString("jobstatus").equals("0")) {
                       volume.setStatus(Status.READY);
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
                       volume.setStatus(Status.READY);
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
        if (volume.getIsShrink() != null) {
            optional.put("shrinkok", volume.getIsShrink().toString());
        }
        String volumeS = csVolumeService.resizeVolume(volume.getUuid(), volume.getStorageOffering().getUuid(), "json",
                optional);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject("resizevolumeresponse");

        if (jobId.has("errorcode")) {
            errors = this.validateEvent(errors, jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
            if (jobId.has("jobid")) {
                String jobResponse = csVolumeService.volumeJobResult(jobId.getString("jobid"), "json");
                JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                Thread.sleep(5000);
                if (jobresult.getString("jobstatus").equals("2")) {
                    volume.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));
                }
                if (jobresult.has("volume")) {
                    volume.setUuid((String) jobresult.get("id"));
                }
                if (jobresult.getString("jobstatus").equals("0")) {
                    volume.setStatus(Status.READY);
                    volume.setDiskSize(jobresult.getLong("size"));
                }
            }
            volumeRepo.save(volume);
        }
        return volume;
    }

    @Override
    public Volume softDelete(Volume volume) throws Exception {
        volume.setIsActive(false);
        volume.setStatus(Volume.Status.DESTROY);
        // set server for finding value in configuration
        csVolumeService.setServer(config.setServer(1L));
        csVolumeService.deleteVolume(volume.getUuid(), "json");
        return volumeRepo.save(volume);
    }

    @Override
    public Volume uploadVolume(Volume volume) throws Exception {
        this.validateVolumeUniqueness(volume);
        Errors errors = validator.rejectIfNullEntity("volumes", volume);
        errors = validator.validateEntity(volume, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            config.setServer(1L);
            HashMap<String, String> optional = new HashMap<String, String>();

            Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
            if (domain != null && !domain.getName().equals("ROOT")) {
                optional.put("domainid", departmentRepository.findOne(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getDomain().getUuid());
                optional.put("account", departmentRepository.findOne(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName());
            } else {
                optional.put("domainid", tokenDetails.getTokenDetails("domainid"));
//                optional.put("account", departmentRepository.findOne(Long.parseLong(tokenDetails.getTokenDetails("domainid"))).getUserName());
            }
            if (volume.getStorageOffering() != null) {
                optional.put("diskofferingid", volume.getStorageOffering().getUuid());
            }
            if (volume.getChecksum() != null) {
                optional.put("checksum", volume.getChecksum().toString());
            }
            String volumeS = csVolumeService.uploadVolume(volume.getName(), volume.getFormat().name(),
                    volume.getZone().getUuid(), volume.getUrl(), "json", optional);
            JSONObject jobId = new JSONObject(volumeS).getJSONObject("uploadvolumeresponse");

            if (jobId.has("errorcode")) {
                errors = this.validateEvent(errors, jobId.getString("errortext"));
                throw new ApplicationException(errors);
            } else {
                if (jobId.has("jobid")) {
                    String jobResponse = csVolumeService.volumeJobResult(jobId.getString("jobid"), "json");

                    JSONObject jobresult = new JSONObject(jobResponse).getJSONObject("queryasyncjobresultresponse");
                    if (jobresult.getString("jobstatus").equals("2")) {
                        volume.setEventMessage(jobresult.getJSONObject("jobresult").getString("errortext"));

                    }
                    if (jobresult.getString("jobstatus").equals("1")) {
                        volume.setStatus(Status.valueOf(EventTypes.UPLOAD_NOT_STARTED));
                        volume.setEventMessage("Volume Not started");
                        setValue(volume);
                    }
                    if (jobresult.getString("jobstatus").equals("0")) {
                        volume.setStatus(Status.valueOf(EventTypes.UPLOADED));
                        volume.setEventMessage("Volume Uploaded");
                        setValue(volume);
                    }
                }
                volumeRepo.save(volume);
            }
            return volume;
        }
    }

    /**
     * To set response values from cloud stack.
     *
     * @param volume volume response.
     * @throws Exception error
     */
    private void setValue(Volume volume) throws Exception {
        volume.setVolumeType(Volume.VolumeType.DATADISK);
        volume.setIsActive(true);
        volume.setUuid(volume.getUuid());
        volume.setFormat(volume.getFormat());
        volume.setUrl(volume.getUrl());
        if (volume.getStorageOffering() != null) {
            volume.setStorageOfferingId(volume.getStorageOffering().getId());
            volume.setDiskSize(volume.getStorageOffering().getDiskSize());
        }
        if (volume.getDiskSize() != null) {
            volume.setDiskSize(volume.getDiskSize());
        }
        volume.setZoneId(volume.getZone().getId());
        volume.setDomainId(Long.parseLong(tokenDetails.getTokenDetails("domainid")));
        Domain domain = domainRepository.findOne(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            volume.setDepartmentId(Long.parseLong(tokenDetails.getTokenDetails("departmentid")));
        }
        if (volume.getChecksum() != null) {
            volume.setChecksum(volume.getChecksum());
        }
    }

}
