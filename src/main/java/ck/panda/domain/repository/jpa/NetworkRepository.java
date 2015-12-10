package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Network;

/**
 * JPA repository for Network entity.
 */
public interface NetworkRepository extends PagingAndSortingRepository<Network, Long> {

    /**
     * Find Network by uuid.
     * @param uuid Network uuid.
     * @return uuid
     */
    @Query(value = "select net from Network net where net.uuid LIKE :uuid ")
    Network findByUUID(@Param("uuid") String uuid);

    /**
     * Find Network list by department.
     * @param department department name.
     * @return network list.
     */
    @Query(value = "select net from Network net where net.department=:department AND net.isActive =:isActive ")
       List<Network> findByDepartmentAndNetworkIsActive(@Param("department") Department department, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive snapshots with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return list of snapshots.
     */
    @Query(value = "select net from Network net where net.isActive =:isActive")
    Page<Network> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive domain network.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @param domainId get the id of the domain
     * @return list of network.
     */
    @Query(value = "select net from Network net where net.isActive =:isActive AND net.domainId =:domainId")
    Page<Network> findByDomainIsActive(Pageable pageable, @Param("isActive") Boolean isActive, @Param("domainId") Long domainId);

    /**
     * Find by name of the Network.
     *
     * @param name of Network
     * @return Network.
     */
    @Query(value = "select net from Network net where net.name =:name")
    Network findName(@Param("name") String name);
}
