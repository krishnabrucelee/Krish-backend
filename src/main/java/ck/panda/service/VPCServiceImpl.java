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
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.ResourceLimitDepartment;
import ck.panda.domain.entity.ResourceLimitProject;
import ck.panda.domain.entity.SupportedNetwork;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VPC;
import ck.panda.domain.entity.VPC.Status;
import ck.panda.domain.entity.VpcOffering;
import ck.panda.domain.entity.VpnUser;
import ck.panda.domain.entity.Zone;
import ck.panda.domain.repository.jpa.VPCRepository;
import ck.panda.domain.entity.Department.AccountType;
import ck.panda.domain.entity.IpAddress.State;
import ck.panda.domain.entity.Network.NetworkCreationType;
import ck.panda.domain.entity.ResourceLimitDepartment.ResourceType;
import ck.panda.util.AppValidator;
import ck.panda.util.CloudStackOptionalUtil;
import ck.panda.util.CloudStackResourceCapacity;
import ck.panda.util.CloudStackVPCService;
import ck.panda.util.ConfigUtil;
import ck.panda.util.TokenDetails;
import ck.panda.util.domain.vo.PagingAndSorting;
import ck.panda.util.error.Errors;
import ck.panda.util.error.exception.ApplicationException;
import ck.panda.util.error.exception.CustomGenericException;

/**
 * VPC service implementation.
 */
@Service
public class VPCServiceImpl implements VPCService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VPCServiceImpl.class);

    /** Constant for vpc entity. */
    private static final String CS_VPC = "VPC";

    /** Constant for vpc response entity. */
    private static final String VPC_CREATE_RESPONSE = "createvpcresponse";

    /** Constant for delete vpc response entity. */
    private static final String CS_DELETE_VPC_RESPONSE = "deletevpcresponse";

    /** Constant for clean up. */
    private static final String CS_CLEAN_UP = "cleanup";

    /** Constant for make redundant. */
    private static final String CS_MAKE_REDUNDANT = "makeredundant";

    /** Constant for restart vpc response. */
    private static final String CS_RESTART_VPC_RESPONSE = "restartvpcresponse";

    /** Constant for update vpc response  */
    private static final String CS_UPDATE_VPC_RESPONSE = "updatevpcresponse";

    /** Virtual Machine service reference. */
    @Autowired
    private VirtualMachineService vmService;

    /** For listing VPN user list from cloudstack server. */
    @Autowired
    private VpnUserService vpnUserService;

    /**IP Address service reference.  */
    @Autowired
    private IpaddressService ipService;

    /** Project service reference. */
    @Autowired
    private ProjectService projectService;

    /** Convert Entity service references. */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Vpc repository references. */
    @Autowired
    private VPCRepository vpcRepository;

    /** Domain service reference. */
    @Autowired
    private DomainService domainService;

    /** Department service reference. */
    @Autowired
    private DepartmentService departmentService;

    /** Cloud VPC service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVpc;

    /** Validator attribute. */
    @Autowired
    private AppValidator validator;

    /** Quota limit validation reference. */
    @Autowired
    QuotaValidationService quotaLimitValidation;

    /** Configuration Utilities. */
    @Autowired
    private ConfigUtil config;

    /** Resource Limit Department service reference. */
    @Autowired
    private ResourceLimitDepartmentService resourceLimitDepartmentService;

    /** Resource Limit Project service reference. */
    @Autowired
    private ResourceLimitProjectService resourceLimitProjectService;

    /** CloudStack connector reference for resource capacity. */
    @Autowired
    private CloudStackResourceCapacity cloudStackResourceCapacity;

    /** Update Resource Count service reference. */
    @Autowired
    private UpdateResourceCountService updateResourceCountService;

    /** VPC cloudstack service reference. */
    @Autowired
    private CloudStackVPCService cloudStackVPCService;

    /** Token details reference. */
    @Autowired
    private TokenDetails tokenDetails;

    @Override
    public VPC save(VPC vpc) throws Exception {
        return vpcRepository.save(vpc);
    }

    @Override
    @PreAuthorize("hasPermission(#vpc.getSyncFlag(), 'EDIT_VPC')")
    public VPC update(VPC vpc) throws Exception {
         if (vpc.getSyncFlag()) {
                Errors errors = validator.rejectIfNullEntity(CS_VPC, vpc);
                errors = validator.validateEntity(vpc, errors);
                if (errors.hasErrors()) {
                    throw new ApplicationException(errors);
                } else {
                    HashMap<String, String> optional = new HashMap<String, String>();
                    if (vpc.getDescription() != null && vpc.getDescription().trim() != "") {
                        optional.put(CloudStackConstants.CS_DISPLAY_TEXT, vpc.getDescription());
                    }
                    config.setUserServer();
                    String updateVPCResponse = cloudStackVpc.updateVPC(vpc.getUuid(), vpc.getName(), optional, CloudStackConstants.JSON);
                    JSONObject jobId = new JSONObject(updateVPCResponse).getJSONObject(CS_UPDATE_VPC_RESPONSE);
                    if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                        String jobResponse = cloudStackVpc.vpcJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID), CloudStackConstants.JSON);
                        JSONObject jobresults = new JSONObject(jobResponse).getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                        if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.PROGRESS_JOB_STATUS)) {
                            vpc.setStatus(Status.ENABLED);
                            vpc.setIsActive(true);
                            vpc.setName(vpc.getName());
                            vpc.setDescription(vpc.getDescription());
                        } else {
                            JSONObject jobresponse = jobresults.getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                            if (jobresults.getString(CloudStackConstants.CS_JOB_STATUS).equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                                if (jobresponse.has(CloudStackConstants.CS_ERROR_CODE)) {
                                    throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, jobresponse.getString(CloudStackConstants.CS_ERROR_TEXT));
                                }
                            }
                        }
                    } else {
                        throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                    }
                }
            }
            return vpcRepository.save(vpc);
    }

    @Override
    public void delete(VPC vpc) throws Exception {
        vpc.setIsActive(false);
        vpc.setStatus(Status.INACTIVE);
        vpcRepository.save(vpc);
    }

    @Override
    public void delete(Long id) throws Exception {
        vpcRepository.delete(id);
    }

    @Override
    public VPC find(Long id) throws Exception {
        return vpcRepository.findOne(id);
    }

    @Override
    public Page<VPC> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return vpcRepository.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VPC> findAll() throws Exception {
        return (List<VPC>) vpcRepository.findAll();
    }

    @Override
    public List<VPC> findAllFromCSServer() throws Exception {
        List<Project> projectList = projectService.findAllByActive(true);
        List<VPC> vpcList = new ArrayList<VPC>();
        for (Project project: projectList) {
            HashMap<String, String> vpcMap = new HashMap<String, String>();
            vpcMap.put(CloudStackConstants.CS_PROJECT_ID, project.getUuid());
            vpcList = getNetworkList(vpcMap, vpcList);
        }

        HashMap<String, String> vpcMap = new HashMap<String, String>();
        vpcMap.put(CloudStackConstants.CS_LIST_ALL, CloudStackConstants.STATUS_ACTIVE);
        vpcList = getNetworkList(vpcMap, vpcList);
        return vpcList;
    }

    private List<VPC> getNetworkList(HashMap<String, String> networkMap, List<VPC> vpcList) throws Exception {
        config.setServer(1L);
        // 1. Get the list of domains from CS server using CS connector
        String response = cloudStackVPCService.listVPCs(networkMap, CloudStackConstants.JSON);
        JSONArray vpcListJSON = null;
        JSONObject responseObject = new JSONObject(response).getJSONObject(CloudStackConstants.CS_LIST_VPC_RESPONSE);
        if (responseObject.has(CloudStackConstants.CS_VPC)) {
            vpcListJSON = responseObject.getJSONArray(CloudStackConstants.CS_VPC);
            // 2. Iterate the json list, convert the single json entity to VPC
            for (int i = 0, size = vpcListJSON.length(); i < size; i++) {
                // 2.1 Call convert by passing JSONObject to VPC entity and Add
                // the converted VPC entity to list
                VPC vpc = VPC.convert(vpcListJSON.getJSONObject(i));
                vpc.setDomainId(convertEntityService.getDomainId(vpc.getTransDomainId()));
                vpc.setZoneId(convertEntityService.getZoneId(vpc.getTransZoneId()));
                vpc.setVpcofferingid(convertEntityService.getVpcOfferingId(vpc.getTransVpcOfferingId()));
                vpc.setDepartmentId(convertEntityService.getDepartmentByUsernameAndDomains(
                        vpc.getTransAccount(), convertEntityService.getDomain(vpc.getTransDomainId())));
                vpc.setProjectId(convertEntityService.getProjectId(vpc.getTransProjectId()));
                if (vpc.getTransProjectId() != null) {
                    Project project = projectService.findByUuid(vpc.getTransProjectId());
                    vpc.setDepartmentId(project.getDepartmentId());
                }
                vpcList.add(vpc);
            }
        }
        return vpcList;
    }

    @Override
    public VPC findByUUID(String uuid) throws Exception {
        return vpcRepository.findByUUID(uuid);
    }

    @Override
    public VPC findById(Long id) throws Exception {
        return vpcRepository.findOne(id);
    }

    @Override
    public List<VPC> findByDepartmentAndVpcIsActive(Long department, Boolean isActive) throws Exception {
        return vpcRepository.findByDepartmentAndVpcIsActive(department, isActive, Status.ENABLED);
    }

    @Override
    @PreAuthorize("hasPermission(#vpc.getSyncFlag(), 'DELETE_VPC')")
    public VPC softDelete(VPC vpc) throws Exception {
        vpc.setIsActive(false);
        if (vpc.getSyncFlag()) {
            /*List<VmInstance> vmResponse = vmService.findAllByNetworkAndVmStatus(network.getId(),
                    VmInstance.Status.EXPUNGING);
            List<Nic> nicResponse = nicService.findAllByNetworkAndIsActive(network.getId(), true);
            if (vmResponse.size() != 0 || nicResponse.size() != 0) {
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                        "VPC tiers is associated with Vm instances. You cannot delete this vpc");
            }*/
            Errors errors = validator.rejectIfNullEntity(CS_VPC, vpc);
            errors = validator.validateEntity(vpc, errors);
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            }
            // check department and project quota validation.
            ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                    .findByDepartmentAndResourceType(vpc.getDepartmentId(), ResourceType.Instance, true);
            if (departmentLimit != null) {
                if (vpc.getProjectId() != null) {
                    // syncService.syncResourceLimitProject(convertEntityService.getProjectById(network.getProjectId()));
                }
                vpc.setIsActive(false);
                vpc.setStatus(Status.INACTIVE);
                if (vpc.getSyncFlag()) {
                    config.setUserServer();
                    String networkResponse = cloudStackVpc.deleteVPC(vpc.getUuid(), CloudStackConstants.JSON);
                    JSONObject jobId = new JSONObject(networkResponse).getJSONObject(CS_DELETE_VPC_RESPONSE);
                    if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                        String jobResponse = cloudStackVpc.vpcJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                                CloudStackConstants.JSON);
                        JSONObject jobresult = new JSONObject(jobResponse)
                                .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                        if(jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                                .equals(GenericConstants.ERROR_JOB_STATUS)){
                            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, jobId.getString(CloudStackConstants.CS_ERROR_TEXT));
                        }
                    }
                }
            } else {
                throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                        "Resource limit for department has not been set. Please update department quota");
            }
        }
        return vpcRepository.save(vpc);
    }

    @Override
    public Page<VPC> findAllByActive(PagingAndSorting page, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        // Check the user is not a root and admin and set the domain value from login detail.
        if (user.getType().equals(User.UserType.ROOT_ADMIN)) {

            return vpcRepository.findAllByIsActive(page.toPageRequest(), true, Status.ENABLED);
        }
        if (user.getType().equals(User.UserType.DOMAIN_ADMIN)) {
            return vpcRepository.findByDomainIsActive(page.toPageRequest(), true, user.getDomainId(), Status.ENABLED);
        }
        Page<VPC> vpcs = this.getVPCListByUser(page, userId);
        return vpcs;
    }

    @Override
    public List<VPC> findByProjectAndVpcIsActive(Long projectId, Boolean isActive) throws Exception {
        return vpcRepository.findByProjectAndVpcIsActive(projectId, isActive, Status.ENABLED);
    }

    @Override
    public List<VPC> findAllByActive(Boolean isActive) throws Exception {
        return vpcRepository.findAllByIsActive(isActive, Status.ENABLED);
    }

    @Override
    @PreAuthorize("hasPermission(#vpc.getSyncFlag(), 'CREATE_VPC')")
    public VPC save(VPC vpc, Long userId) throws Exception {
        if (vpc.getSyncFlag()) {
            User user = convertEntityService.getOwnerById(userId);
            Errors errors = validator.rejectIfNullEntity(CS_VPC, vpc);
            errors = validator.validateEntity(vpc, errors);
            HashMap<String, String> optionalMap = new HashMap<String, String>();
            optionalMap.put(CloudStackConstants.CS_ZONE_ID,
                    convertEntityService.getZoneById(vpc.getZoneId()).getUuid());
            if (errors.hasErrors()) {
                throw new ApplicationException(errors);
            } else {
                // check department and project quota validation.
                ResourceLimitDepartment departmentLimit = resourceLimitDepartmentService
                        .findByDepartmentAndResourceType(vpc.getDepartmentId(), ResourceType.Instance, true);
                ResourceLimitProject projectLimit = resourceLimitProjectService
                        .findByProjectAndResourceType(vpc.getProjectId(), ResourceLimitProject.ResourceType.Instance, true);
                if (departmentLimit != null && convertEntityService.getDepartmentById(vpc.getDepartmentId()).getType()
                        .equals(AccountType.USER)) {
                    if (vpc.getProjectId() != null) {
                        if (projectLimit != null) {
                            quotaLimitValidation.QuotaLimitCheckByResourceObject(vpc, CS_VPC,
                                    vpc.getProjectId(), "Project");
                        } else {
                            errors.addGlobalError(
                                    "Resource limit for project has not been set. Please update project quota");
                            throw new ApplicationException(errors);
                        }
                    } else {
                        quotaLimitValidation.QuotaLimitCheckByResourceObject(vpc, CS_VPC,
                                vpc.getDepartmentId(), "Department");
                    }
                    try {
                        config.setUserServer();
                        Zone zoneObject = convertEntityService.getZoneById(vpc.getZoneId());
                        String vpcResponse = cloudStackVpc.createVPC(vpc.getcIDR(), zoneObject.getUuid(), optional(vpc, userId), CloudStackConstants.JSON);
                        JSONObject createVpcResponseJSON = new JSONObject(vpcResponse)
                                .getJSONObject(VPC_CREATE_RESPONSE);
                        if (createVpcResponseJSON.has(CloudStackConstants.CS_ERROR_CODE)) {
                            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED, createVpcResponseJSON.getString(CloudStackConstants.CS_ERROR_TEXT));
                        }
                        vpc.setUuid(createVpcResponseJSON.getString(CloudStackConstants.CS_ID));
                        vpc.setIsActive(true);
                        vpc.setForDisplay(true);
                        vpc.setStart(true);
                    } catch (ApplicationException e) {
                        LOGGER.error("ERROR AT VPC CREATION", e);
                        throw new ApplicationException(e.getErrors());
                    }
                    return vpcRepository.save(vpc);

                } else {
                    errors.addGlobalError(
                            "Resource limit for department has not been set. Please update department quota");
                    throw new ApplicationException(errors);
                }
            }
        } else {
            // To check VPC UUID while Syncing Network.
            LOGGER.debug("Sync-VPC UUID :" + vpc.getUuid());
            vpc.setIsActive(true);
            return vpcRepository.save(vpc);
        }
    }

    @Override
    @PreAuthorize("hasPermission(#vpc.getSyncFlag(), 'RESTART_VPC')")
    public VPC restartVPC(VPC vpc) throws Exception {
        Errors errors = validator.rejectIfNullEntity(CS_VPC, vpc);
        errors = validator.validateEntity(vpc, errors);
        if (vpc.getSyncFlag()) {
            HashMap<String, String> optionalParams = new HashMap<String, String>();
            // Mapping optional parameters.
            CloudStackOptionalUtil.updateOptionalBooleanValue(CS_CLEAN_UP, vpc.getCleanUpVPC(), optionalParams);
            // Mapping optional parameters.
            CloudStackOptionalUtil.updateOptionalBooleanValue(CS_MAKE_REDUNDANT, vpc.getRedundantVPC(), optionalParams);
            // Configuration value to ACS.
            config.setUserServer();
            // Restart vpc call to ACS
            String restartResponse = cloudStackVpc.restartVPC(vpc.getUuid(), CloudStackConstants.JSON, optionalParams);
            JSONObject jobId = new JSONObject(restartResponse).getJSONObject(CS_RESTART_VPC_RESPONSE);
            // Checking job id.
            if (jobId.has(CloudStackConstants.CS_JOB_ID)) {
                String jobResponse = cloudStackVpc.vpcJobResult(jobId.getString(CloudStackConstants.CS_JOB_ID),
                        CloudStackConstants.JSON);
                JSONObject jobresult = new JSONObject(jobResponse)
                        .getJSONObject(CloudStackConstants.QUERY_ASYNC_JOB_RESULT_RESPONSE);
                if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                        .equals(CloudStackConstants.PROGRESS_JOB_STATUS)
                        || (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                                .equals(CloudStackConstants.PROGRESS_JOB_STATUS))) {
                    vpc.setRestartRequired(true);
                } else {
                    JSONObject jobresponse = jobresult.getJSONObject(CloudStackConstants.CS_JOB_RESULT);
                    if (jobresult.getString(CloudStackConstants.CS_JOB_STATUS)
                            .equals(CloudStackConstants.ERROR_JOB_STATUS)) {
                        if (jobresponse.has(CloudStackConstants.CS_ERROR_CODE)) {
                            throw new CustomGenericException(GenericConstants.NOT_IMPLEMENTED,
                                    jobresponse.getString(CloudStackConstants.CS_ERROR_TEXT));
                        }
                    }
                }
            }
        }
        return vpcRepository.save(vpc);
    }

    @Override
    public VPC ipRelease(VPC vpc) throws Exception {
        List<IpAddress> ipList = ipService.findByNetwork(vpc.getId());
        for (IpAddress ip : ipList) {
            List<VpnUser> vpnUserList = vpnUserService.findAllByDepartmentAndDomainAndIsActive(vpc.getDepartmentId(),
                    vpc.getDomainId(), true);
            if (vpnUserList.size() != 0) {
                for (VpnUser vpnUser : vpnUserList) {
                    vpnUser.setIsActive(false);
                    vpnUser.setSyncFlag(false);
                    vpnUserService.softDelete(vpnUser);
                }
            }
            ipService.ruleDelete(ip);
            IpAddress ipAddress = new IpAddress();
            ipAddress.setId(ip.getId());
            ipAddress.setState(State.FREE);
            ipAddress.setIsStaticnat(false);
            ipAddress.setIsSourcenat(false);
            ipAddress.setDepartmentId(ip.getDepartmentId());
            ipAddress.setZoneId(ip.getZoneId());
            ipAddress.setDisplay(ip.getDisplay());
            ipAddress.setProjectId(ip.getProjectId());
            ipAddress.setUuid(ip.getUuid());
            ipAddress.setPublicIpAddress(ip.getPublicIpAddress());
            ipAddress.setVmInstanceId(ip.getVmInstanceId());
            ipAddress.setVlan(ip.getVlan());
            ipAddress.setCreatedBy(ip.getCreatedBy());
            ipAddress.setCreatedDateTime(ip.getCreatedDateTime());
            ipAddress = ipService.update(ipAddress);
        }
        return vpc;
    }

    @Override
    public Page<VPC> findAllByDomainId(Long domainId, PagingAndSorting page) throws Exception {
        return vpcRepository.findByDomainIsActive(page.toPageRequest(), true, domainId, Status.ENABLED);
    }

    @Override
    public List<VPC> findAllByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception {
        return vpcRepository.findAllByDomainAndIsActive(domainId, isActive, Status.ENABLED);
    }

    @Override
    public List<VPC> findAllByUserId(Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        // Check the user is not a root and admin and set the domain value from login detail.
        if (user.getType().equals(User.UserType.ROOT_ADMIN)) {
            return vpcRepository.findAllByIsActiveWihtoutPaging(true, Status.ENABLED);
        }
        if (user.getType().equals(User.UserType.DOMAIN_ADMIN)) {
            return vpcRepository.findAllByDomainIsActive(true, user.getDomainId(), Status.ENABLED);
        }
        List<VPC> vpcs = this.getVPCListByUserWihtoutPaging(userId);
        return vpcs;
    }

    @Override
    public List<VPC> findAllByDomainId(Long domainId) throws Exception {
        return vpcRepository.findAllByDomainAndIsActive(domainId, true, Status.ENABLED);
    }

    /**
     * Get the vpc list based on the active status.
     *
     * @param pagingAndSorting do pagination with sorting for vpc.
     * @param userId id of the user.
     * @return list of vpc.
     * @throws Exception exception
     */
    private List<VPC> getVPCListByUserWihtoutPaging(Long userId) throws  Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
            List<Project> allProjectList = projectService.findAllByUserAndIsActive(user.getId(), true);
            List<VPC> projectVPC = vpcRepository.findAByProjectDepartmentAndIsActiveWithoutPaging(allProjectList,
                    user.getDepartmentId(), true, Status.ENABLED);
            return projectVPC;
        } else {
            return vpcRepository.findByDepartment(user.getDepartmentId(), true, Status.ENABLED);
        }
    }

    /**
     * Get the vpc list based on the active status.
     *
     * @param pagingAndSorting do pagination with sorting for vpc.
     * @param userId id of the user.
     * @return vpc list with pagination.
     * @throws Exception exception
     */
    private Page<VPC> getVPCListByUser(PagingAndSorting pagingAndSorting, Long userId) throws  Exception {
        User user = convertEntityService.getOwnerById(userId);
        if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
            List<Project> allProjectList = new ArrayList<Project>();
            for (Project project : projectService.findAllByUserAndIsActive(user.getId(), true)) {
                allProjectList.add(project);
            }
            Page<VPC> projectVpc = vpcRepository.findByProjectDepartmentAndIsActive(allProjectList,
                    user.getDepartmentId(), true, pagingAndSorting.toPageRequest(), Status.ENABLED);
            return projectVpc;
        } else {
            return vpcRepository.findByDepartmentAndPagination(user.getDepartmentId(), true,
                    pagingAndSorting.toPageRequest(), Status.ENABLED);
        }
    }

    /**
     * Hash Map to map the optional values to cloudstack.
     *
     * @return optional
     * @param vpc vpc's object
     * @param userId idof the user
     * @throws Exception Exception
     */
    public HashMap<String, String> optional(VPC vpc, Long userId) throws Exception {
        User user = convertEntityService.getOwnerById(userId);
        HashMap<String, String> optional = new HashMap<String, String>();
        if (vpc.getNetworkDomain() != null && vpc.getNetworkDomain().trim() != "") {
            optional.put(CloudStackConstants.CS_NETWORK_DOMAIN, vpc.getNetworkDomain());
        }
        if (vpc.getDomainId() != null) {
            optional.put(CloudStackConstants.CS_DOMAIN_ID,
                    convertEntityService.getDomainById(vpc.getDomainId()).getUuid());
        } else {
            optional.put(CloudStackConstants.CS_DOMAIN_ID, domainService.find(user.getDomainId()).getUuid());
        }
        if (vpc.getName() != null && vpc.getName().trim() != "") {
            optional.put(CloudStackConstants.CS_NAME, vpc.getName());
        }
        if (vpc.getDescription() != null && vpc.getDescription().trim() != "") {
            optional.put(CloudStackConstants.CS_DISPLAY_TEXT, vpc.getDescription());
        }
        if (vpc.getVpcofferingid() != null) {
            optional.put(CloudStackConstants.CS_VPC_OFFERING_ID, convertEntityService.getVpcOfferingById(vpc.getVpcofferingid()).getUuid());
        }
        if (vpc.getProjectId() != null) {
            optional.put(CloudStackConstants.CS_PROJECT_ID,
                    convertEntityService.getProjectById(vpc.getProjectId()).getUuid());

        } else {
            if (vpc.getDepartmentId() != null) {
                optional.put(CloudStackConstants.CS_ACCOUNT,
                        departmentService.find(vpc.getDepartmentId()).getUserName());
            } else {
                optional.put(CloudStackConstants.CS_ACCOUNT,
                        departmentService.find(user.getDepartmentId()).getUserName());
            }
        }
        return optional;
    }

    @Override
    public Page<VPC> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText, Long userId) throws Exception {
        Page<VPC> vpcs = null;
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        if (convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.ROOT_ADMIN)) {
            vpcs = vpcRepository.findDomainBySearchText(domainId, pagingAndSorting.toPageRequest(), searchText, true, Status.ENABLED);
        } else if ((convertEntityService.getOwnerById(user.getId()).getType()).equals(User.UserType.DOMAIN_ADMIN)) {
            vpcs = vpcRepository.findDomainBySearchText((convertEntityService.getOwnerById(user.getId())
                    .getDomainId()), pagingAndSorting.toPageRequest(), searchText, true, Status.ENABLED);
        } else if (convertEntityService.getOwnerById(user.getId()).getType().equals(User.UserType.USER)) {
            if (projectService.findAllByUserAndIsActive(user.getId(), true).size() > 0) {
                List<Project> allProjectList = projectService.findAllByUserAndIsActive(user.getId(), true);
                Page<VPC> projectVPC = vpcRepository.findByProjectDepartmentAndIsActiveAndSearchText(allProjectList, user.getDepartmentId(),true, pagingAndSorting.toPageRequest(),searchText,user.getDomainId(), Status.ENABLED);
                vpcs = projectVPC;
            } else {
                vpcs = vpcRepository.findAllByDepartmentIsActiveAndSearchText((convertEntityService.getOwnerById(user.getId()).getDomainId()), (convertEntityService.getOwnerById(user.getId()).getDepartmentId()),
                     pagingAndSorting.toPageRequest(), searchText, true, Status.ENABLED);
            }
         }
        return vpcs;
    }

    @Override
    public VPC findVpcById(Long id) throws Exception {
        return vpcRepository.findOne(id);
    }
}
