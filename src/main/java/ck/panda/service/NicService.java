package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Nic;
import ck.panda.domain.entity.VmIpaddress;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for nic entity.
 */
@Service
public interface NicService extends CRUDService<Nic> {

    /**
     * Find nic by uuid.
     *
     * @param uuid of nic.
     * @return nic object.
     * @throws Exception unhandled errors.
     */
    Nic findbyUUID(String uuid) throws Exception;

    /**
     * Soft delete method for nic.
     *
     * @param nic for network
     * @return nic.
     * @throws Exception unhandled errors.
     */
    Nic softDelete(Nic nic) throws Exception;

    /**
     * List by instance attached to nic.
     *
     * @param nic Nic
     * @return nic Nics from instance.
     * @throws Exception exception
     */
    List<Nic> findByInstance(Long nic) throws Exception;

    /**
     * List by instance attached to nic.
     *
     * @param nic Nic
     * @return nic Nics from instance.
     * @throws Exception exception
     */
    List<VmIpaddress> findByVMInstance(Long instanceId, Long networkId) throws Exception;

    /**
     * Find all nics from CloudStack.
     *
     * @return nic
     * @throws Exception unhandled errors.
     */
    List<Nic> findAllFromCSServer() throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<Nic> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Secondary IP for a Network.
     *
     * @param nic sencondary ip.
     * @return nic
     * @throws Exception if error occurs.
     */
    Nic acquireSecondaryIP(Nic nic) throws Exception;

    /**
     * Method to update NIC resource state.
     *
     * @param nic entity
     * @return updated entity
     * @throws Exception error occurs
     */
    Nic updatebyResourceState(Nic nic) throws Exception;

    /**
     * Find nic by id.
     *
     * @param id of the nic.
     * @return nic.
     * @throws Exception if error occurs.
     */
    Nic findById(Long id) throws Exception;

    /**
     * Relase vm instance secondary Ip
     *
     * @param vmIpaddressId of nic.
     * @return
     * @throws Exception if error occurs.
     */
      Nic releaseSecondaryIP(Nic nic, Long vmIpaddressId) throws Exception;

      /**
       * Find all the nic by network and is Active status.
       *
       * @param networkId of the network.
       * @param isActive get the nic list based on active/inactive status.
       * @return list of nics.
       * @throws Exception if error occurs.
       */
    List<Nic> findAllByNetworkAndIsActive(Long networkId, Boolean isActive) throws Exception;

    /**
     * Find vmIpAddress by Vm instance Id.
     *
     * @param instanceId of the instance.
     * @return vmIpAddress.
     * @throws Exception if error occurs.
     */
    List<VmIpaddress> findByNicAndVmInstance(Long instanceId) throws Exception;

    /**
     * Find all Nic by Network id, instance id and isActive status.
     *
     * @param networkId of the network.
     * @param instanceId of the instance.
     * @param isActive status of the nic.
     * @return nic.
     * @throws Exception if error occurs.
     */
    Nic findAllNetworkAndVmInstanceByIsActive(Long networkId, Long instanceId, Boolean isActive) throws Exception;

    /**
     * Find Nic by vminstance.
     *
     * @param instanceId of the instance.
     * @return nic.
     * @throws Exception if error occurs.
     */
    Nic findByVmInstance(Long instanceId) throws Exception;

    /**
     * Find all vmipAdddres by instance id.
     *
     * @param instanceId of the vmipaddress.
     * @return vmipaddress.
     * @throws Exception if error occurs.
     */
    List<VmIpaddress> findByInstanceId(Long instanceId) throws Exception;

}
