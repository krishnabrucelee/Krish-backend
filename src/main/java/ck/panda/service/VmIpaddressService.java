package ck.panda.service;

import java.util.List;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.util.domain.CRUDService;

/**
 * Service interface for VmIpaddress entity.
 *
 */
@Service
public interface VmIpaddressService extends CRUDService<VmIpaddress> {

    /**
     * Find vm instance secondary address by id.
     *
     * @param id of the vm instance.
     * @return vm Ipaddress.
     * @throws Exception if error occurs.
     */
    VmIpaddress findById(Long id) throws Exception;

    /**
     * To get VmIpaddress from cloudstack server.
     *
     * @param uuid uuid of VmIpaddress.
     * @return VmIpaddress from server
     * @throws Exception unhandled errors.
     */
    VmIpaddress findByUUID(String uuid) throws Exception;

    /**
     * Soft delete for vm Ip address.
     *
     * @param vmIpaddress object.
     * @return vm Ip address.
     * @throws Exception if error occurs.
     */
    VmIpaddress softDelete(VmIpaddress vmIpaddress) throws Exception;

    /**
     * List by vmInstanceId.
     *
     * @param nic Nic
     * @return vm Ipaddress.
     * @throws Exception if error occurs.
     */
    List<VmIpaddress> findByVMInstance(Long nic) throws Exception;
}
