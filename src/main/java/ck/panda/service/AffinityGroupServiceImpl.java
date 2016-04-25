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
import ck.panda.domain.entity.AffinityGroup;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.AffinityGroup.Status;
import ck.panda.domain.entity.User;
import ck.panda.domain.repository.jpa.AffinityGroupRepository;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackAffinityGroupService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.EntityNotFoundException;

/** Affinity group service implementation class. */
@Service
public class AffinityGroupServiceImpl implements AffinityGroupService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AffinityGroupServiceImpl.class);

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Affinity group repository reference. */
    @Autowired
    private AffinityGroupRepository affinityGroupRepository;

    /** Affinity group service reference. */
    @Autowired
    private AffinityGroupTypeService affinityGroupTypeService;

    /** CloudStack reference for affinity group. */
    @Autowired
    private CloudStackAffinityGroupService cloudStackAffinityGroupService;

    /** Reference of the convert entity service. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Cloud stack configuration reference. */
    @Autowired
    private ConfigUtil configUtil;

    /** Configuration Utilities. */
    @Autowired
    private ConfigUtil config;

    /** Virtual machine Service reference. */
    @Autowired
    private VirtualMachineService virtualMachineService;

    /** Constant for affinity group list response from cloudStack. */
    public static final String CS_LIST_AFFINIGY_GROUP_RESPONSE = "listaffinitygroupsresponse";

    /** Constant for affinity group response from cloudStack. */
    public static final String CS_AFFINITY_GROUP = "affinitygroup";

    /** Constant for affinity group. */
    public static final String AFFINITY_GROUP = "affinityGroup";

    /** Constant for virtural machine ids. */
    public static final String VIRTURAL_MACHINE_IDS = "virtualmachineIds";

    /** Constant for create affinity group response. */
    public static final String CREATE_AFFINITY_GROUP_RESPONSE = "createaffinitygroupresponse";

    /** Constant for create affinity group response. */
    public static final String DELETE_AFFINITY_GROUP_RESPONSE = "deleteaffinitygroupresponse";

    /** Constant for instance. */
    public static final String INSTANCE = "INSTANCE";

    @Override
    public AffinityGroup save(AffinityGroup affinityGroup, Long id) throws Exception {
        affinityGroup.setIsActive(true);
        affinityGroup.setStatus(Status.ENABLED);
        Errors errors = validator.rejectIfNullEntity(AFFINITY_GROUP, affinityGroup);
        errors = validator.validateEntity(affinityGroup, errors);
        if (errors.hasErrors()) {
            throw new ApplicationException(errors);
        } else {
            HashMap<String, String> optional = new HashMap<String, String>();
            if (affinityGroup.getTransAffinityGroupAccessFlag().equals(INSTANCE)) {
                optional.put(CloudStackConstants.CS_ACCOUNT, convertEntityService.getDepartmentById(affinityGroup.getDepartmentId()).getUserName());
                optional.put(CloudStackConstants.CS_DOMAIN_ID, convertEntityService.getDomainById(affinityGroup.getDomainId()).getUuid());
            } else {
                User user = convertEntityService.getOwnerById(id);
                affinityGroup.setDomainId(user.getDomainId());
                affinityGroup.setDepartmentId(user.getDepartmentId());
                optional.put(CloudStackConstants.CS_ACCOUNT, user.getDepartment().getUserName());
                optional.put(CloudStackConstants.CS_DOMAIN_ID, user.getDomain().getUuid());
            }
            optional.put(CloudStackConstants.CS_DESCRIPTION, affinityGroup.getDescription());
            config.setUserServer();
            String csResponse = cloudStackAffinityGroupService.createAffinityGroup(affinityGroup.getName(),
                    affinityGroup.getAffinityGroupType().getType(), CloudStackConstants.JSON, optional);
            JSONObject csAffinityGroup = new JSONObject(csResponse).getJSONObject(CREATE_AFFINITY_GROUP_RESPONSE);
            if (csAffinityGroup.has(CloudStackConstants.CS_ERROR_CODE)) {
                errors = this.validateEvent(errors, csAffinityGroup.getString(CloudStackConstants.CS_ERROR_TEXT));
                throw new ApplicationException(errors);
            } else {
                affinityGroup.setUuid(csAffinityGroup.getString(CloudStackConstants.CS_ID));
                return affinityGroupRepository.save(affinityGroup);
            }
        }
    }

    @Override
    public AffinityGroup save(AffinityGroup affinityGroup) throws Exception {
        affinityGroup.setIsActive(true);
        affinityGroup.setStatus(Status.ENABLED);
        affinityGroup = affinityGroupRepository.save(affinityGroup);
        for (int i = 0; i < affinityGroup.getTransInstanceList().size(); i++) {
            VmInstance vmInstance = virtualMachineService.findByUUID(affinityGroup.getTransInstanceList().get(i));
            if (vmInstance != null) {
                List<AffinityGroup> affinityGrupList = vmInstance.getAffinityGroupList();
                if (affinityGrupList != null) {
                    affinityGrupList.add(affinityGroup);
                    vmInstance.setAffinityGroupList(affinityGrupList);
                    virtualMachineService.save(vmInstance);
                }
            }
        }
        return affinityGroup;
    }

    @Override
    public AffinityGroup update(AffinityGroup affinityGroup) throws Exception {
        return affinityGroupRepository.save(affinityGroup);
    }

    @Override
    public void delete(AffinityGroup affinityGroup) throws Exception {
        affinityGroup.setIsActive(false);
        affinityGroup.setStatus(Status.DISABLED);
        if (affinityGroup.getIsSyncFlag()) {
            configUtil.setUserServer();
            Errors errors = null;
            try {
                HashMap<String, String> optional = new HashMap<String, String>();
                optional.put(CloudStackConstants.CS_ID, affinityGroup.getUuid());
                String csResponse = cloudStackAffinityGroupService.deleteAffinityGroup(CloudStackConstants.JSON, optional);
                JSONObject affinityJson = new JSONObject(csResponse).getJSONObject(DELETE_AFFINITY_GROUP_RESPONSE);
                Thread.sleep(5000);
                if (affinityJson.has(CloudStackConstants.CS_JOB_ID)) {
                    String templateJob = cloudStackAffinityGroupService.queryAsyncJobResult(affinityJson.getString(CloudStackConstants.CS_JOB_ID),
                           CloudStackConstants.JSON);
                    JSONObject jobresult = new JSONObject(templateJob).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                    if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.SUCCEEDED_JOB_STATUS)) {
                        for (VmInstance instance : virtualMachineService.findInstanceByGroup(affinityGroup.getId())) {
                            if (instance != null) {
                                List<AffinityGroup> affinityGrupList = instance.getAffinityGroupList();
                                affinityGrupList.remove(affinityGroup);
                                instance.setAffinityGroupList(affinityGrupList);
                                virtualMachineService.save(instance);
                            }
                        }
                        affinityGroupRepository.save(affinityGroup);
                    } else if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                        errors = validator.sendGlobalError(jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT)
                                 .getString(CloudStackConstants.CS_ERROR_TEXT));
                        throw new ApplicationException(errors);
                    }
                }
            } catch (ApplicationException e) {
                throw new ApplicationException(errors);
            }
        }
    }

    @Override
    public void delete(Long id) throws Exception {
        AffinityGroup affinityGroup = affinityGroupRepository.findOne(id);
        affinityGroup.setIsActive(false);
        affinityGroup.setStatus(Status.DISABLED);
        affinityGroupRepository.save(affinityGroup);
    }

    @Override
    public AffinityGroup find(Long id) throws Exception {
        AffinityGroup affinityGroup = affinityGroupRepository.findOne(id);
        // Entity validation.
        if (affinityGroup == null) {
            throw new EntityNotFoundException("affinity.group.not.found");
        }
        return affinityGroupRepository.findOne(id);
    }

    @Override
    public Page<AffinityGroup> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return affinityGroupRepository.findByStatus(true, pagingAndSorting.toPageRequest());
    }

    @Override
    public List<AffinityGroup> findAll() throws Exception {
        return (List<AffinityGroup>) affinityGroupRepository.findAll();
    }

    @Override
    public List<AffinityGroup> findAllFromCSServer() throws Exception {
        List<AffinityGroup> affinityGroupList = new ArrayList<AffinityGroup>();
        HashMap<String, String> affinityGroupListMap = new HashMap<String, String>();
        affinityGroupListMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        config.setServer(1L);
        // 1. Get the list of affinity group from CS server using CS connector
        String response = cloudStackAffinityGroupService.listAffinityGroups(CloudStackConstants.JSON, affinityGroupListMap);
        JSONArray affinityGroupListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CS_LIST_AFFINIGY_GROUP_RESPONSE);
        if (responseObject.has(CS_AFFINITY_GROUP)) {
            affinityGroupListJSON = responseObject.getJSONArray(CS_AFFINITY_GROUP);
            // 2. Iterate the json list, convert the single json entity
            for (int i = 0, size = affinityGroupListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to affinity group entity
                // and Add
                // the converted affinity group entity to list
                AffinityGroup affinityGroup = AffinityGroup.convert(affinityGroupListJSON.getJSONObject(i));
                affinityGroup.setDomainId(convertEntityService.getDomainId(affinityGroup.getTransDomainId()));
                affinityGroup.setAffinityGroupTypeId(affinityGroupTypeService.findByType(affinityGroup.getTransAffinityGroupType()).getId());
                affinityGroup.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                        affinityGroup.getTransDepartment(), convertEntityService.getDomain(affinityGroup.getTransDomainId())));
                List<String> instanceList = new ArrayList<String>();
                if (affinityGroupListJSON.getJSONObject(i).has(VIRTURAL_MACHINE_IDS)) {
                    for (int j = 0; j < affinityGroupListJSON.getJSONObject(i).getJSONArray(VIRTURAL_MACHINE_IDS).length(); j++) {
                        instanceList.add(affinityGroupListJSON.getJSONObject(i).getJSONArray(VIRTURAL_MACHINE_IDS).get(j).toString());
                    }
                }
                affinityGroup.setTransInstanceList(instanceList);
                affinityGroupList.add(affinityGroup);
            }
        }
        return affinityGroupList;
    }

    /**
     * Check the affinity group error handling.
     *
     * @param errors error creating status.
     * @param errmessage error message.
     * @return errors.
     * @throws Exception if error occurs.
     */
    private Errors validateEvent(Errors errors, String errmessage) throws Exception {
        errors.addGlobalError(errmessage);
        return errors;
    }

    @Override
    public List<AffinityGroup> findByDepartment(Long departmentId) throws Exception {
        return affinityGroupRepository.findByDepartment(departmentId, true);
    }

    @Override
    public Page<AffinityGroup> findAll(PagingAndSorting pagingAndSorting, Long id) throws Exception {
        User user = convertEntityService.getOwnerById(id);
        if (user.getType() == User.UserType.USER) {
            return affinityGroupRepository.findByDepartmentAndPageable(user.getDepartmentId(), true, pagingAndSorting.toPageRequest());
        } else if (user.getType() == User.UserType.DOMAIN_ADMIN) {
            return affinityGroupRepository.findByDomainAndPageable(user.getDomainId(), true, pagingAndSorting.toPageRequest());
        }
        return affinityGroupRepository.findByStatus(true, pagingAndSorting.toPageRequest());
    }

    @Override
    public Page<AffinityGroup> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting)
            throws Exception {
        return affinityGroupRepository.findByDomainAndPageable(domainId, true, pagingAndSorting.toPageRequest());
    }

}
