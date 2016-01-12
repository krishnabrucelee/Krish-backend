package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.PortForwarding;

/**
 * Port Forwarding Repository.
 *
 */
public interface PortForwardingRepository extends PagingAndSortingRepository<PortForwarding, Long> {

    /**
     * Get the Port Forwarding based on the uuid.
     *
     * @param uuid of the Port Forwarding
     * @param isActive get the port forwarding list based on active/inactive status.
     * @return Port Forwarding
     */
    @Query(value = "select port from PortForwarding port where port.uuid = :uuid AND port.isActive =:isActive")
    PortForwarding findByUUID(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive Port Forwarding with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the port forwarding list based on active/inactive status.
     * @return list of Port Forwarding.
     */
    @Query(value = "select port from PortForwarding port where port.isActive =:isActive")
    Page<PortForwarding> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Get the Port Forwarding based on the Instance id.
     *
     * @param isActive get the PortForwarding list based on active/inactive status.
     * @param vmInstanceId from PortForwarding
     * @return PortForwarding.
     */
    @Query(value = "select port from PortForwarding port where port.vmInstanceId=:vmInstanceId AND port.isActive =:isActive")
    List<PortForwarding> findByInstanceAndIsActive(@Param("vmInstanceId") Long vmInstanceId, @Param("isActive") Boolean isActive);

    /**
     * Find all by network and status active or inactive with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param ipAddressId ip address id id.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return list of firewall rule.
     */
    @Query(value = "select port from PortForwarding port where port.isActive =:isActive and port.ipAddressId =:ipAddressId ")
    Page<PortForwarding> findAllByIpaddressAndIsActive(Pageable pageable, @Param("ipAddressId") Long ipAddressId, @Param("isActive") Boolean isActive);

}
