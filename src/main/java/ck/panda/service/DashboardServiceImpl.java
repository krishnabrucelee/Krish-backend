package ck.panda.service;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ck.panda.constants.CloudStackConstants;
import ck.panda.constants.GenericConstants;
import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
import ck.panda.domain.entity.User.UserType;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.util.TokenDetails;
import ck.panda.web.resource.DashboardController;


/**
 * Dashboard service implementation class.
 *
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

    /** Service reference to Virtual Machine. */
    @Autowired
    private VirtualMachineService virtualmachineService;

    /** Service reference to Network. */
    @Autowired
    private NetworkService networkService;

    /** Service reference to Conver entity . */
    @Autowired
    private ConvertEntityService convertEntityService;

    /** Service reference to Conver entity . */
    @Autowired
    private TemplateService templateService;

    /** Service reference to Volume . */
    @Autowired
    private VolumeService volumeService;

    /** Service reference to Department . */
    @Autowired
    private DepartmentService departmentService;

    /** Service reference to Application . */
    @Autowired
    private ApplicationService applicationService;

    /** Service reference to Ip . */
    @Autowired
    private IpaddressService ipService;

    /** Service reference to Project . */
    @Autowired
    private ProjectService projectService;

    /** Autowired TokenDetails. */
    @Autowired
    private TokenDetails tokenDetails;

    /** Service reference to resource. */
    @Autowired
    private ResourceLimitDomainService resourceLimitDomainService;

    /** Constant for VM count. */
    public static final String RUNNING_VM_COUNT = "runningVmCount", STOPPED_VM_COUNT = "stoppedVmCount";

    /** Constant for total count. */
    public static final String TOTAL_COUNT = "totalCount";

    /** Constant for vcpu count. */
    public static final String VCPU = "vcpu";

    /** Constant for ram count. */
    public static final String RAM = "ram";

    /** Constant for storage count. */
    public static final String STORAGE = "storage";

    /** Constant for publicIp count. */
    public static final String PUBLIC_IP = "publicIp";

    /** Constant for networks count. */
    public static final String NETWORKS = "networks";

    /** Constant for template count. */
    public static final String TEMPLATE = "template";

    @Override
    public JSONObject getInfrastructure() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        List<VmInstance> vmList = virtualmachineService.findAllByDomain(user.getDomainId(), user.getId(), "");
        Integer runningVmCount = virtualmachineService.findCountByStatusAndDomain(Status.RUNNING, user.getDomainId(), user.getId(), "");
        Integer stoppedVmCount = virtualmachineService.findCountByStatusAndDomain(Status.STOPPED, user.getDomainId(), user.getId(), "");
        Integer vmCount = vmList.size();
        List<Network> networkList = new ArrayList();
        if (user != null && !user.getType().equals(UserType.ROOT_ADMIN)) {
            networkList = networkService.findAllByDomainId(user.getDomainId());
        } else {
            networkList = networkService.findAllByUserId(user.getId());
        }
        Integer networkCount = networkList.size();
        List<IpAddress> ipAddressList = new ArrayList();
        if(networkCount > 0) {
            for(Network network : networkList) {
                List<IpAddress> ipList = ipService.findByNetwork(network.getId());
                ipAddressList.addAll(ipList);
            }
        }
        Integer ipCount = ipAddressList.size();
        Integer templateCount = templateService.findAllByUserIdIsActiveAndShare(Template.TemplateType.SYSTEM,
                Template.Status.ACTIVE, true, Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID))).size();
        Long storageSize = 0L;
        List<Volume> volumeList = (List<Volume>) volumeService.findAllVolumeByUserId(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        for(Volume volume : volumeList) {
            storageSize = (storageSize + (volume.getDiskSize() / (1024 * 1024 * 1024)));
        }

        Integer cpuCore = 0;
        float memory = 0;
        for(VmInstance vm : vmList) {
            cpuCore = cpuCore + vm.getCpuCore();
            memory = memory + (vm.getMemory());
        }
        memory = (float) memory / 1024;

        JSONObject infra = new JSONObject();
        infra.put(RUNNING_VM_COUNT, runningVmCount);
        infra.put(STOPPED_VM_COUNT, stoppedVmCount);
        infra.put(TOTAL_COUNT, vmCount);
        infra.put(VCPU, cpuCore);
        infra.put(RAM, memory);
        infra.put(STORAGE, storageSize);
        infra.put(PUBLIC_IP, ipCount);
        infra.put(NETWORKS, networkCount);
        infra.put(TEMPLATE, templateCount);
        return infra;
    }


    @Override
    public JSONObject getInfrastructureByDomainId(Long domainId) throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        List<VmInstance> vmList = virtualmachineService.findAllByDomain(domainId , user.getId(), "");
        Integer runningVmCount = virtualmachineService.findCountByStatusAndDomain(Status.RUNNING, domainId, user.getId(), "");
        Integer stoppedVmCount = virtualmachineService.findCountByStatusAndDomain(Status.STOPPED, domainId, user.getId(), "");
        Integer vmCount = vmList.size();
        List<Network> networkList = new ArrayList();
        networkList = networkService.findAllByDomainId(domainId);
        Integer networkCount = networkList.size();
        List<IpAddress> ipAddressList = new ArrayList();
        if(networkCount > 0) {
            for(Network network : networkList) {
                List<IpAddress> ipList = ipService.findByNetwork(network.getId());
                ipAddressList.addAll(ipList);
            }
        }
        Integer ipCount = ipAddressList.size();
        Integer templateCount = templateService.findAllByDomainIdIsActiveAndShare(Template.TemplateType.SYSTEM,
                Template.Status.ACTIVE, true, domainId).size();
        Long storageSize = 0L;
        List<Volume> volumeList = (List<Volume>) volumeService.findAllVolumeByDomainId(domainId);
        for(Volume volume : volumeList) {
            storageSize = (storageSize + (volume.getDiskSize() / (1024 * 1024 * 1024)));
        }

        Integer cpuCore = 0;
        float memory = 0;
        for(VmInstance vm : vmList) {
            cpuCore = cpuCore + vm.getCpuCore();
            memory = memory + (vm.getMemory());
        }
        memory = (float) memory / 1024;

        JSONObject infra = new JSONObject();
        infra.put(RUNNING_VM_COUNT, runningVmCount);
        infra.put(STOPPED_VM_COUNT, stoppedVmCount);
        infra.put(TOTAL_COUNT, vmCount);
        infra.put(VCPU, cpuCore);
        infra.put(RAM, memory);
        infra.put(STORAGE, storageSize);
        infra.put(PUBLIC_IP, ipCount);
        infra.put(NETWORKS, networkCount);
        infra.put(TEMPLATE, templateCount);
        return infra;
    }

    @Override
    public List<ResourceLimitDomain> findByDomainQuota() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        return resourceLimitDomainService.findAllByDomainId(user.getDomainId());
    }

    @Override
    public List<ResourceLimitDomain> findDomainQuotaById(Long domainId) throws Exception {
        return resourceLimitDomainService.findAllByDomainId(domainId);
    }

    @Override
    public List<Department> findAllDepartmentByDomain() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        List<Department> departmentList =  departmentService.findAllByDomainAndIsActive(user.getDomainId(), true);
        return departmentList;
    }

    @Override
    public List<Application> findAllApplicationByDomain() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails(CloudStackConstants.CS_ID)));
        return applicationService.findAllByDomain(user.getDomainId());
    }


}
