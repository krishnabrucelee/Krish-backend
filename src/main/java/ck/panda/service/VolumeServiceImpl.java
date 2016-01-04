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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ck.panda.constants.EventTypes;
import ck.panda.domain.entity.Domain;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.StorageOffering;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.Volume.Status;
import ck.panda.domain.entity.Volume.VolumeType;
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

    /** Domain Service reference. */
    @Autowired
    private DomainService domainService;

    /** Department Service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Autowired Project Service. */
    @Autowired
    private ProjectService projectService;

    /** Autowired TokenDetails. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** Autowired Storage Offering Service. */
    @Autowired
    private StorageOfferingService storageService;

    @Override
    @PreAuthorize("hasPermission(#volume.getIsSyncFlag(), 'ADD_VOLUME')")
    public Volume save(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            this.validateVolumeUniqueness(volume);
            Errors errors = validator.rejectIfNullEntity("volumes", volume);
            errors = validator.validateEntity(volume, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                Volume volumeCS = createVolume(volume, errors);
                if (volumeRepo.findByUUID(volumeCS.getUuid()) != null) {
                    volume = volumeRepo.findByUUID(volumeCS.getUuid());
                }
            }
        }
        return volumeRepo.save(volume);
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
    @PreAuthorize("hasPermission(#volume.getIsSyncFlag(), 'ATTACH_DISK')")
    public Volume attachVolume(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("volumes", volume);
            errors = validator.validateEntity(volume, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                Volume volumeCS = attach(volume, errors);
                if (volumeRepo.findByUUID(volumeCS.getUuid()) != null) {
                    volume = volumeRepo.findByUUID(volumeCS.getUuid());
                    volume.setVmInstanceId(volumeCS.getVmInstanceId());
                    volume.setStatus(volumeCS.getStatus());
                }
            }
        }
        return volumeRepo.save(volume);
    }

    @Override
    @PreAuthorize("hasPermission(#volume.getIsSyncFlag(), 'DETACH_DISK')")
    public Volume detachVolume(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("volumes", volume);
            errors = validator.validateEntity(volume, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                Volume volumeCS = detach(volume, errors);
                if (volumeRepo.findByUUID(volumeCS.getUuid()) != null) {
                    volume = volumeRepo.findByUUID(volumeCS.getUuid());
                    volume.setVmInstanceId(volumeCS.getVmInstanceId());
                    volume.setStatus(volumeCS.getStatus());
                }
            }
        }
        return volumeRepo.save(volume);
    }

    @Override
    public Volume resizeVolume(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("volumes", volume);
            errors = validator.validateEntity(volume, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                Volume volumeCS = resize(volume, errors);
                if (volumeRepo.findByUUID(volumeCS.getUuid()) != null) {
                    volume = volumeRepo.findByUUID(volumeCS.getUuid());
                }
            }
        }
        return volumeRepo.save(volume);
    }

    @Override
    @PreAuthorize("hasPermission(#volume.getIsSyncFlag(), 'UPLOAD_VOLUME')")
    public Volume uploadVolume(Volume volume) throws Exception {
        if (volume.getIsSyncFlag()) {
            Errors errors = validator.rejectIfNullEntity("volumes", volume);
            errors = validator.validateEntity(volume, errors);
            this.validateVolumeUniqueness(volume);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                upload(volume, errors);
                return volume;
            }
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
    public Page<Volume> findAllByIsActive(PagingAndSorting pagingAndSorting) throws Exception {
        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
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
        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
        return volumeRepo.findByInstanceAndDomainIsActive(domain.getId(), volume, true);
        }
        return volumeRepo.findByInstanceAndIsActive(volume, true);
    }

    @Override
    public List<Volume> findByVolumeTypeAndIsActive() throws Exception {
        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
        return volumeRepo.findByVolumeTypeAndIsActive(domain.getId(), Volume.VolumeType.DATADISK, true);
        }
        return volumeRepo.findByVolumeTypeAndIsActive(Volume.VolumeType.DATADISK, true);
    }

    @Override
    public List<Volume> findByInstanceAndVolumeTypeAndIsActive(Long volume) throws Exception {
        return volumeRepo.findByInstanceAndVolumeTypeAndIsActive(volume, Volume.VolumeType.ROOT, true);
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

        if (volume.getDiskMaxIops() != null) {
            optional.put("maxiops", volume.getDiskMaxIops().toString());
        }

        if (volume.getDiskMinIops() != null) {
            optional.put("miniops", volume.getDiskMinIops().toString());
        }

        if (volume.getProjectId() != null) {
            optional.put("projectid", convertEntityService.getProjectUuidById(volume.getProjectId()));
        } else if (volume.getDepartmentId() != null) {
             optional.put("account", convertEntityService.getDepartmentUsernameById(volume.getDepartmentId()));

             optional.put("domainid", departmentService.find(volume.getDepartmentId()).getDomain().getUuid());
        } else {
            Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
            if (domain != null && !domain.getName().equals("ROOT")) {
                optional.put("domainid", departmentService
                        .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getDomain().getUuid());
                optional.put("account", departmentService
                        .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName());
            }
        }
        return optional;
    }

    @Override
    public List<Volume> findAllFromCSServer() throws Exception {

        List<Project> project = projectService.findAllByActive(true);
        List<Volume> volumeList = new ArrayList<Volume>();
        for (int j = 0; j <= project.size(); j++) {
            HashMap<String, String> volumeMap = new HashMap<String, String>();
            if (j == project.size()) {
                volumeMap.put("listall", "true");
            } else {
                volumeMap.put("projectid", project.get(j).getUuid());
            }

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
                    volume.setStorageOfferingId(
                            convertEntityService.getStorageOfferId(volume.getTransStorageOfferingId()));
                    volume.setVmInstanceId(convertEntityService.getVmInstanceId(volume.getTransvmInstanceId()));
                    if (volume.getTransProjectId() != null) {
                        volume.setProjectId(convertEntityService.getProjectId(volume.getTransProjectId()));
                        volume.setDepartmentId(projectService.find(volume.getProjectId()).getDepartmentId());
                    } else {
//                        departmentRepository.findByUuidAndIsActive(volume.getTransDepartmentId(), true);
                        Domain domain = domainService.find(volume.getDomainId());
                    volume.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(volume.getTransDepartmentId(), domain));
                    }
                    volumeList.add(volume);
                }
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
     * @return volume
     */
    private Volume createVolume(Volume volume, Errors errors) throws Exception {
        config.setUserServer();
        String volumeS = csVolumeService.createVolume(volume.getName(), convertEntityService.getStorageOfferingById(volume.getStorageOfferingId()),
                convertEntityService.getZoneUuidById(volume.getZoneId()), "json", optional(volume));
        LOGGER.info("Volume create response " + volumeS);
        try {
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
                    volume.setStorageOfferingId(volume.getStorageOfferingId());
                    if (volume.getDiskSize() != null) {
                        volume.setDiskSize(volume.getDiskSize());
                    } else {
                        StorageOffering store = storageService.find(volume.getStorageOfferingId());
                        volume.setDiskSize(store.getDiskSize());
                    }
                    volume.setDomainId(Long.parseLong(tokenDetails.getTokenDetails("domainid")));
//                    Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
//                    if (domain != null && !domain.getName().equals("ROOT")) {
//                        volume.setDepartmentId(Long.parseLong(tokenDetails.getTokenDetails("departmentid")));
//                    }
                    volume.setVolumeType(Volume.VolumeType.DATADISK);
                    if (volume.getProjectId() != null) {
                        volume.setProjectId(volume.getProjectId());
                        Project project = projectService.find(volume.getProjectId());
                        volume.setDepartmentId(project.getDepartmentId());
                    }
                    volume.setCreatedDateTime(volume.getCreatedDateTime());
                    volume.setDiskMaxIops(volume.getDiskMaxIops());
                    volume.setDiskMinIops(volume.getDiskMinIops());
                }

            }
        } catch (ApplicationException e) {
            LOGGER.error("ERROR AT VOLUME CREATION", e);
            throw new ApplicationException(e.getErrors());
        }
        return volume;
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
            errors.addGlobalError("volume.already.exist");
        }
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        }
    }

    @Override
    public Volume findByUUID(String uuid) throws Exception {
        return volumeRepo.findByUUID(uuid);
    }

    /**
     * Attach volume to an instance.
     *
     * @param volume volume
     * @param errors errors
     * @throws Exception error
     * @return volume
     */
    public Volume attach(Volume volume, Errors errors) throws Exception {
        config.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        if (volume.getVmInstanceId() != null) {
        VmInstance instance = virtualMachineService.find(volume.getVmInstanceId());
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
        }
        return volume;
    }

    /**
     * Detach volume to an instance.
     *
     * @param volume volume
     * @param errors errors
     * @throws Exception error
     * @return volume
     */
    public Volume detach(Volume volume, Errors errors) throws Exception {
        config.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();
        optional.put("id", volume.getUuid());
        //optional.put("virtualmachineid", volume.getVmInstance().getUuid());
        String volumeS = csVolumeService.detachVolume("json",  optional);
        JSONObject jobId = new JSONObject(volumeS).getJSONObject("detachvolumeresponse");

        if (jobId.has("errorcode")) {
            errors = this.validateEvent(errors, jobId.getString("errortext"));
            throw new ApplicationException(errors);
        } else {
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
        }
        return volume;
    }

    /**
     * Resize volume to an instance.
     *
     * @param volume volume
     * @param errors errors
     * @throws Exception error
     * @return volume
     */
    public Volume resize(Volume volume, Errors errors) throws Exception {
        config.setUserServer();
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
        }
        return volume;
    }

    @Override
    public Volume softDelete(Volume volume) throws Exception {
        volume.setIsActive(false);
        volume.setStatus(Volume.Status.DESTROY);
        if (volume.getIsSyncFlag()) {
            // set server for finding value in configuration
            config.setUserServer();
            csVolumeService.deleteVolume(volume.getUuid(), "json");
        }
        if (volumeRepo.findByUUID(volume.getUuid()).getIsActive()) {
            return volumeRepo.save(volume);
        }
        return volume;
    }

    /**
     * Upload volume to an instance.
     *
     * @param volume volume
     * @param errors errors
     * @throws Exception error
     */
    public void upload(Volume volume, Errors errors) throws Exception {
        config.setUserServer();
        HashMap<String, String> optional = new HashMap<String, String>();

        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            optional.put("domainid", departmentService
                    .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getDomain().getUuid());
            optional.put("account", departmentService
                    .find(Long.parseLong(tokenDetails.getTokenDetails("departmentid"))).getUserName());
        } else {
            optional.put("domainid", tokenDetails.getTokenDetails("domainid"));
            // optional.put("account",
            // departmentRepository.findOne(Long.parseLong(tokenDetails.getTokenDetails("domainid"))).getUserName());
        }
        if (volume.getStorageOfferingId() != null) {
            optional.put("diskofferingid", convertEntityService.getStorageOfferingById(volume.getStorageOfferingId()));
        }
        if (volume.getChecksum() != null) {
            optional.put("checksum", volume.getChecksum().toString());
        }
        String volumeS = csVolumeService.uploadVolume(volume.getName(), volume.getFormat().name(),
                convertEntityService.getZoneUuidById(volume.getZoneId()), volume.getUrl(), "json", optional);
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
                    setValue(volume);
                }
                if (jobresult.getString("jobstatus").equals("0")) {
                    setValue(volume);
                }
            }
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
        if (volume.getStorageOfferingId() != null) {
            volume.setStorageOfferingId(volume.getStorageOfferingId());
            StorageOffering storageOffer = storageService.find(volume.getStorageOfferingId());
            volume.setDiskSize(storageOffer.getDiskSize());
        }
        if (volume.getDiskSize() != null) {
            volume.setDiskSize(volume.getDiskSize());
        }
        volume.setZoneId(volume.getZoneId());
        volume.setDomainId(Long.parseLong(tokenDetails.getTokenDetails("domainid")));
        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
        if (domain != null && !domain.getName().equals("ROOT")) {
            volume.setDepartmentId(Long.parseLong(tokenDetails.getTokenDetails("departmentid")));
        }
        if (volume.getChecksum() != null) {
            volume.setChecksum(volume.getChecksum());
        }
    }

    @Override
    public List<Volume> findByDepartment(Long departmentId) {
        return volumeRepo.findByDepartment(departmentId);
    }

    @Override
    public Volume findByInstanceAndVolumeType(Long volume) throws Exception {
        return volumeRepo.findByInstanceAndVolumeType(volume, Volume.VolumeType.ROOT, true);
    }

    @Override
    public List<Volume> findByProjectAndVolumeType(Long projectId, List<VolumeType> volumeType) {
        return volumeRepo.findByProjectAndVolumeType(projectId, volumeType, true);
    }

    @Override
    public List<Volume> findByDepartmentAndVolumeType(Long departmentId, List<VolumeType> volumeType) {
        return volumeRepo.findByDepartmentAndVolumeType(departmentId, volumeType, true);
    }

    @Override
    public List<Volume> findByDepartmentAndProjectAndVolumeType(Long departmentId, Long projectId, List<VolumeType> volumeType) {
        return volumeRepo.findByDepartmentAndProjectAndVolumeType(departmentId, projectId, volumeType, true);
    }
//
//    @Override
//    public Integer findCountByStatus() throws NumberFormatException, Exception {
//        Domain domain = domainService.find(Long.valueOf(tokenDetails.getTokenDetails("domainid")));
//        if (domain != null && !domain.getName().equals("ROOT")) {
//            List<Volume> volumeCount = (List<Volume>) volumeRepo.findAllByActive(true);
//            for (Volume volume2 : volumeCount) {
//              return volumeRepo.findVolumeCountByDomainAndInstanceId(domain.getId(), volume2.getVmInstanceId(), Volume.VolumeType.DATADISK, true);
//            }
//            return null;
//        } else {
//        List<Volume> volumeCount = (List<Volume>) volumeRepo.findAllByActive(true);
//        for (Volume volume2 : volumeCount) {
//
//            List<Volume> ints = volumeRepo.findVolumeCountByInstanceId(volume2.getVmInstanceId(), Volume.VolumeType.DATADISK, true);
//
//            System.out.println(ints);
//          return ints.size();
//        }
//        }
//        return null;
//    }

}
