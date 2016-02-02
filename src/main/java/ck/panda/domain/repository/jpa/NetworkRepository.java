package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Network;

/**
 * JPA repository for Network entity.
 */
public interface NetworkRepository extends PagingAndSortingRepository<Network, Long> {

    /**
     * Find Network by uuid.
     *
     * @param uuid Network uuid.
     * @return uuid
     */
    @Query(value = "SELECT net FROM Network net WHERE net.uuid LIKE :uuid ")
    Network findByUUID(@Param("uuid") String uuid);

    /**
     * Find Network by id.
     *
     * @param id Network id.
     * @return id
     */
    @Query(value = "SELECT net FROM Network net WHERE net.id LIKE :id ")
    Network findById(@Param("id") Long id);

    /**
     * Find Network list by department.
     *
     * @param departmentId department id.
     * @param isActive true/false.
     * @return network list.
     */
    @Query(value = "SELECT net FROM Network net WHERE net.projectId is NULL AND net.departmentId=:departmentId AND net.isActive =:isActive ")
    List<Network> findByDepartmentAndNetworkIsActive(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find Network list by department.
     *
     * @param projectId project id.
     * @param isActive true/false.
     * @return network list.
     */
    @Query(value = "SELECT net FROM Network net WHERE net.projectId=:projectId AND net.isActive =:isActive ")
    List<Network> findByProjectAndNetworkIsActive(@Param("projectId") Long projectId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive snapshots with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return list of snapshots.
     */
    @Query(value = "SELECT net FROM Network net WHERE net.isActive =:isActive")
    Page<Network> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive domain network.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @param domainId get the id of the domain
     * @return list of network.
     */
    @Query(value = "SELECT net FROM Network net WHERE net.isActive =:isActive AND net.domainId =:domainId")
    Page<Network> findByDomainIsActive(Pageable pageable, @Param("isActive") Boolean isActive,
            @Param("domainId") Long domainId);

    /**
     * Find by name of the Network.
     *
     * @param name of Network
     * @return Network.
     */
    @Query(value = "SELECT net FROM Network net WHERE net.name =:name")
    Network findName(@Param("name") String name);

    /**
     * Find  all the active networks.
     *
     * @param isActive get the network list based on active/inactive status.
     * @return list of network.
     */
    @Query(value = "SELECT net FROM Network net WHERE net.isActive =:isActive")
    List<Network> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find all networks by project and deparment.
     *
     * @param projectId of the network.
     * @param departmentId of the network.
     * @param isActive status of the network.
     * @return list of networks.
     */
    @Query(value = "SELECT net FROM Network net WHERE (net.projectId = :projectId OR net.departmentId=:departmentId ) AND net.isActive =:isActive")
    List<Network> findByProjectDepartmentAndNetwork(@Param("projectId") Long projectId,
            @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all active networks by department.
     *
     * @param departmentId of the network.
     * @param isActive status of the network.
     * @param pageable for pagination.
     * @return networks.
     */
    @Query(value = "SELECT net FROM Network net WHERE net.departmentId=:departmentId AND net.isActive =:isActive AND net.projectId = NULL ")
    Page<Network> findByDepartmentAndPagination(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive, Pageable pageable);

}
