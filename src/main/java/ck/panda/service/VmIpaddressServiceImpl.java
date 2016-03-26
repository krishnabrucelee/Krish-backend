package ck.panda.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.LoadBalancerRule;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.domain.repository.jpa.VmIpaddressRepository;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * VmIpaddress service implementation class.
 */
@Service
public class VmIpaddressServiceImpl implements VmIpaddressService {

    /** Logger attribute. */
    private static final Logger LOGGER = LoggerFactory.getLogger(VmIpaddressServiceImpl.class);

    /** Department repository reference. */
    @Autowired
    private VmIpaddressRepository ipaddressRepo;

    @Autowired
    private LoadBalancerService lbService;
    @Override
    public VmIpaddress save(VmIpaddress ipaddress) throws Exception {
        LOGGER.debug(ipaddress.getUuid());
        return ipaddressRepo.save(ipaddress);
    }

    @Override
    public VmIpaddress update(VmIpaddress ipaddress) throws Exception {
        LOGGER.debug(ipaddress.getUuid());
        return ipaddressRepo.save(ipaddress);
    }

    @Override
    public void delete(VmIpaddress ipaddress) throws Exception {
        ipaddressRepo.delete(ipaddress);
    }

    @Override
    public void delete(Long id) throws Exception {
        ipaddressRepo.delete(id);
    }

    @Override
    public VmIpaddress find(Long id) throws Exception {
        VmIpaddress ipaddress = ipaddressRepo.findOne(id);
        return ipaddress;
    }

    @Override
    public Page<VmIpaddress> findAll(PagingAndSorting pagingAndSorting) throws Exception {
        return ipaddressRepo.findAll(pagingAndSorting.toPageRequest());
    }

    @Override
    public List<VmIpaddress> findAll() throws Exception {
        return (List<VmIpaddress>) ipaddressRepo.findAll();
    }

    @Override
    public VmIpaddress findById(Long id) throws Exception {
        return ipaddressRepo.findById(id);
    }

    @Override
    public VmIpaddress findByUUID(String uuid) throws Exception {
        return ipaddressRepo.findByUUIDAndIsActive(uuid, true);
    }

    @Override
    public VmIpaddress softDelete(VmIpaddress vmIpaddress) throws Exception {
        vmIpaddress.setIsActive(false);
         return ipaddressRepo.save(vmIpaddress);
    }

    @Override
    public List<VmIpaddress> findByVMInstance(Long nic) throws Exception {
        return ipaddressRepo.findByVMInstanceAndIsActive(nic, true);
    }

    @Override
    public List<VmIpaddress> findAllLoadBalancer(Long lbId) throws Exception {
        LoadBalancerRule loadBalancer = lbService.find(lbId);
        List<VmIpaddress> vmIp = loadBalancer.getVmIpAddress();
        if (vmIp.size() > 0) {
            return ipaddressRepo.findAllByVmIpaddressAndIsActive(true, vmIp);
        }
        return vmIp;
    }

    @Override
    public VmIpaddress findByIPAddress(String guestIpAddress, Long vmInstanceId) throws Exception {
        return ipaddressRepo.findAllByVmIpaddressAndvmInstanceId(guestIpAddress, vmInstanceId);
    }
}
