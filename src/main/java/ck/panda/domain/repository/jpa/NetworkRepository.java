package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Network;
import ck.panda.domain.entity.Project;

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
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE net.isActive =:isActive")
    Page<Network> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);
    
    /**
     * Find all the active or inactive snapshots with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @return list of snapshots.
     */
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE net.isActive =:isActive")
    List<Network> findAllByIsActiveWihtoutPaging(@Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive domain network.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @param domainId get the id of the domain
     * @return list of network.
     */
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE net.isActive =:isActive AND net.domainId =:domainId")
    Page<Network> findByDomainIsActive(Pageable pageable, @Param("isActive") Boolean isActive,
            @Param("domainId") Long domainId);
    
    /**
     * Find all the active or inactive domain network.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the snapshot list based on active/inactive status.
     * @param domainId get the id of the domain
     * @return list of network.
     */
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE net.isActive =:isActive AND net.domainId =:domainId")
    List<Network> findAllByDomainIsActive(@Param("isActive") Boolean isActive, @Param("domainId") Long domainId);

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
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE net.departmentId=:departmentId AND net.isActive =:isActive AND net.projectId = NULL ")
    Page<Network> findByDepartmentAndPagination(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive, Pageable pageable);
    
    /**
     * Find all active networks by department.
     *
     * @param departmentId of the network.
     * @param isActive status of the network.
     * @param pageable for pagination.
     * @return networks.
     */
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE net.departmentId=:departmentId AND net.isActive =:isActive AND net.projectId = NULL ")
    List<Network> findByDepartment(@Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Find all the domain based active or inactive network with pagination.
     *
     * @param domainId get the id of the domain
     * @param isActive get the network list based on active/inactive status.
     * @param pageable to get the list with pagination.
     * @return list of snapshots.
     */
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE net.domainId =:domainId AND net.isActive =:isActive")
    Page<Network> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all networks by project and deparment with pagination.
     *
     * @param allProjectList project list.
     * @param departmentId of the network.
     * @param isActive status of the network.
     * @param pageable to get the list with pagination.
     * @return list of networks.
     */
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE (net.project in :allProjectList OR net.departmentId=:departmentId ) AND net.isActive =:isActive")
    Page<Network> findByProjectDepartmentAndIsActive(@Param("allProjectList") List<Project> allProjectList,
            @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive, Pageable pageable);
    
    /**
     * Find all networks by project and deparment with pagination.
     *
     * @param allProjectList project list.
     * @param departmentId of the network.
     * @param isActive status of the network.
     * @param pageable to get the list with pagination.
     * @return list of networks.
     */
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE (net.project in :allProjectList OR net.departmentId=:departmentId ) AND net.isActive =:isActive")
    List<Network> findAByProjectDepartmentAndIsActiveWithoutPaging(@Param("allProjectList") List<Project> allProjectList,
            @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);
    
    
    /**
     * Find all the domain based active or inactive network.
     *
     * @param domainId get the id of the domain
     * @param isActive get the network list based on active/inactive status.
     * @return list of snapshots.
     */
    @Query(value = "SELECT net FROM Network net LEFT JOIN net.project WHERE net.domainId =:domainId AND net.isActive =:isActive")
    List<Network> findAllByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

}
