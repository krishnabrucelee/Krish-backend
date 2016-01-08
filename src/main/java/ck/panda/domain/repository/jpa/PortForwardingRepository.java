/**
 *
 */
package ck.panda.domain.repository.jpa;

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
     * @return Port Forwarding
     */
    @Query(value = "select port from PortForwarding port where port.uuid = :uuid")
    PortForwarding findByUUID(@Param("uuid") String uuid);

    /**
     * Find all the active or inactive Port Forwarding with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the department list based on active/inactive status.
     * @return list of Port Forwarding.
     */
    @Query(value = "select port from PortForwarding port where port.isActive =:isActive")
    Page<PortForwarding> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);
}
