package ck.panda.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.IpAddress;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.ResourceLimitDomain;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.Volume;
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

    @Override
    public JSONObject getInfrastructure() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        List<VmInstance> vmList = virtualmachineService.findAllByUser(Long.valueOf(tokenDetails.getTokenDetails("id")));
        Integer runningVmCount = virtualmachineService.findCountByStatus(Status.RUNNING, Long.valueOf(tokenDetails.getTokenDetails("id")));
        Integer stoppedVmCount = virtualmachineService.findCountByStatus(Status.STOPPED, Long.valueOf(tokenDetails.getTokenDetails("id")));
        Integer vmCount = vmList.size();
        List<Network> networkList = networkService.findAllByUserId(user.getId());
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
                Template.Status.ACTIVE, true, Long.valueOf(tokenDetails.getTokenDetails("id"))).size();
        Long storageSize = 0L;
        List<Volume> volumeList = (List<Volume>) volumeService.findAllVolumeByUserId(Long.valueOf(tokenDetails.getTokenDetails("id")));
        for(Volume volume : volumeList) {
        	storageSize = (storageSize + (volume.getDiskSize() / (1024 * 1024 * 1024)));
        }

        Integer cpuCore = 0;
        Integer memory = 0;
        for(VmInstance vm : vmList) {
            cpuCore = cpuCore + vm.getCpuCore();
            memory = memory + (vm.getMemory() / 1024);
        }

    	JSONObject infra = new JSONObject();
        infra.put("runningVmCount", runningVmCount);
        infra.put("stoppedVmCount", stoppedVmCount);
        infra.put("totalCount", vmCount);
        infra.put("vcpu", runningVmCount);
        infra.put("ram", memory);
        infra.put("storage", storageSize);
        infra.put("publicIp", ipCount);
        infra.put("networks", networkCount);
        infra.put("template", templateCount);
        return infra;
    }

    @Override
    public List<ResourceLimitDomain> findByDomainQuota() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        return resourceLimitDomainService.findAllByDomainId(user.getDomainId());
    }

    @Override
    public List<Department> findAllDepartmentByDomain() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        List<Department> departmentList =  departmentService.findAllByDomainAndIsActive(user.getDomainId(), true);
        return departmentList;
    }

    @Override
    public List<Application> findAllApplicationByDomain() throws Exception {
        User user = convertEntityService.getOwnerById(Long.valueOf(tokenDetails.getTokenDetails("id")));
        return applicationService.findAllByDomain(user.getDomainId());
    }


}
