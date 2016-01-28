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
     * @return vm ipaddress.
     */
    @Query(value = "select net from VmIpaddress net where net.uuid = :uuid")
    VmIpaddress findByUUID(@Param("uuid") String uuid);

    /**
     * Find all by VMInstance Id.
     *
     * @param isActive get the vmIpAddress list based on active/inactive status.
     * @param vmInstanceId from nic
     * @return Vm ipaddress.
     */
    @Query(value = "select vmIpaddress from VmIpaddress vmIpaddress where  vmIpaddress.vmInstanceId=:vmInstanceId AND vmIpaddress.isActive =:isActive")
    List<VmIpaddress> findByVMInstanceAndIsActive(@Param("vmInstanceId") Long vmInstanceId, @Param("isActive") Boolean isActive);

}
