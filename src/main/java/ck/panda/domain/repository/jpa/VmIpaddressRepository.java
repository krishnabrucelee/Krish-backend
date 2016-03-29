package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VmIpaddress;

/**
 * Jpa Repository for Pod entity.
 *
 */
@Service
public interface VmIpaddressRepository extends PagingAndSortingRepository<VmIpaddress, Long> {

    /**
     * Find VmIpaddress by id.
     *
     * @param id Network id.
     * @return id
     */
    @Query(value = "select net from VmIpaddress net where net.id LIKE :id ")
    VmIpaddress findById(@Param("id") Long id);

    /**
     * Get the vm ip address  based on the uuid.
     *
     * @param uuid of the vm ip address.
     * @param isActive get the vmIpAddress list based on active/inactive status.
     * @return vm ipaddress.
     */
    @Query(value = "select net from VmIpaddress net where net.uuid = :uuid AND net.isActive =:isActive")
    VmIpaddress findByUUIDAndIsActive(@Param("uuid") String uuid,@Param("isActive") Boolean isActive);

    /**
     * Find all by VMInstance Id.
     *
     * @param isActive get the vmIpAddress list based on active/inactive status.
     * @param vmInstanceId from nic
     * @return Vm ipaddress.
     */
    @Query(value = "select vmIpaddress from VmIpaddress vmIpaddress where  vmIpaddress.vmInstanceId=:vmInstanceId AND vmIpaddress.isActive =:isActive")
    List<VmIpaddress> findByVMInstanceAndIsActive(@Param("vmInstanceId") Long vmInstanceId, @Param("isActive") Boolean isActive);

    /**
     * Find all Secondary ip Address and is Active status.
     *
     * @param isActive status of the vm ip Address.
     * @param vmIpAddress of the load balancer.
     * @return vm ipaddress.
     */
    @Query(value = "SELECT vmIpaddress FROM VmIpaddress vmIpaddress WHERE vmIpaddress.isActive IS :isActive AND vmIpaddress NOT IN :vmIpAddress")
    List<VmIpaddress> findAllByVmIpaddressAndIsActive(@Param("isActive") Boolean isActive, @Param("vmIpAddress") List<VmIpaddress> vmIpAddress);

    /**
     * Find all Secondary ip Address and vm instance id.
     *
     * @param guestIpAddress of the Vmipaddress.
     * @param vmInstanceId of the virtual Machine.
     * @return vm ipaddress.
     */
    @Query(value = "SELECT vmIpaddress FROM VmIpaddress vmIpaddress WHERE vmIpaddress.guestIpAddress =:guestIpAddress AND vmIpaddress.vmInstanceId =:vmInstanceId")
    VmIpaddress findAllByVmIpaddressAndvmInstanceId(@Param("guestIpAddress") String guestIpAddress, @Param("vmInstanceId") Long vmInstanceId);

    /**
     * Find all by VMInstance Id.
     *
     * @param isActive get the vmIpAddress list based on active/inactive status.
     * @param vmInstanceId from nic
     * @return Vm ipaddress.
     */
    @Query(value = "select vmIpaddress from VmIpaddress vmIpaddress where vmIpaddress.vmInstanceId=:vmInstanceId AND vmIpaddress.nicId=:nicId AND vmIpaddress.isActive =:isActive")
    List<VmIpaddress> findByNicAndVmInstanceAndIsActive(@Param("vmInstanceId") Long vmInstanceId,@Param("nicId") Long nicId, @Param("isActive") Boolean isActive);
}
