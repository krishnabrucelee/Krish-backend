package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.VPC;

/**
 * JPA repository for VPC entity.
 */
public interface VPCRepository extends PagingAndSortingRepository<VPC, Long> {
	/**
     * Find Vpc by uuid.
     *
     * @param uuid Vpc uuid.
     * @return uuid
     */
    @Query(value = "SELECT vpc FROM VPC vpc WHERE vpc.uuid LIKE :uuid ")
    VPC findByUUID(@Param("uuid") String uuid);

    /**
     * Find Vpc list by department.
     *
     * @param departmentId department id.
     * @param isActive true/false.
     * @return vpc list.
     */
    @Query(value = "SELECT vpc FROM VPC vpc WHERE vpc.projectId is NULL AND vpc.departmentId=:departmentId AND vpc.isActive =:isActive ")
    List<VPC> findByDepartmentAndVpcIsActive(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find Vpc list by department.
     *
     * @param projectId project id.
     * @param isActive true/false.
     * @return vpc list.
     */
    @Query(value = "SELECT vpc FROM VPC vpc WHERE vpc.projectId=:projectId AND vpc.isActive =:isActive ")
    List<VPC> findByProjectAndVpcIsActive(@Param("projectId") Long projectId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive vpcs with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the vpc list based on active/inactive status.
     * @return list of vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE vpc.isActive =:isActive")
    Page<VPC> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive vpcs with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the vpc list based on active/inactive status.
     * @return list of vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE vpc.isActive =:isActive")
    List<VPC> findAllByIsActiveWihtoutPaging(@Param("isActive") Boolean isActive);

    /**
     * Find all the active or inactive domain vpc.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the vpc list based on active/inactive status.
     * @param domainId get the id of the domain
     * @return list of vpc.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE vpc.isActive =:isActive AND vpc.domainId =:domainId")
    Page<VPC> findByDomainIsActive(Pageable pageable, @Param("isActive") Boolean isActive,
            @Param("domainId") Long domainId);

    /**
     * Find all the active or inactive domain vpc.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the vpc list based on active/inactive status.
     * @param domainId get the id of the domain
     * @return list of vpc.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE vpc.isActive =:isActive AND vpc.domainId =:domainId")
    List<VPC> findAllByDomainIsActive(@Param("isActive") Boolean isActive, @Param("domainId") Long domainId);

    /**
     * Find by name of the vpc.
     *
     * @param name of vpc
     * @return vpc.
     */
    @Query(value = "SELECT vpc FROM VPC vpc WHERE vpc.name =:name")
    VPC findName(@Param("name") String name);

    /**
     * Find  all the active networks.
     *
     * @param isActive get the vpc list based on active/inactive status.
     * @return list of vpc.
     */
    @Query(value = "SELECT vpc FROM VPC vpc WHERE vpc.isActive =:isActive")
    List<VPC> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find all vpcs by project and deparment.
     *
     * @param projectId of the vpc.
     * @param departmentId of the vpc.
     * @param isActive status of the vpc.
     * @return list of vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc WHERE (vpc.projectId = :projectId OR vpc.departmentId=:departmentId ) AND vpc.isActive =:isActive")
    List<VPC> findByProjectDepartmentAndVpc(@Param("projectId") Long projectId,
            @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);

    /**
     * Find all active vpcs by department.
     *
     * @param departmentId of the vpc.
     * @param isActive status of the vpc.
     * @param pageable for pagination.
     * @return vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE vpc.departmentId=:departmentId AND vpc.isActive =:isActive AND vpc.projectId IS NULL ")
    Page<VPC> findByDepartmentAndPagination(@Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all active vpcs by department.
     *
     * @param departmentId of the vpc.
     * @param isActive status of the vpc.
     * @param pageable for pagination.
     * @return vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE vpc.departmentId=:departmentId AND vpc.isActive =:isActive AND vpc.projectId IS NULL ")
    List<VPC> findByDepartment(@Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Find all the domain based active or inactive vpc with pagination.
     *
     * @param domainId get the id of the domain
     * @param isActive get the vpc list based on active/inactive status.
     * @param pageable to get the list with pagination.
     * @return list of vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE vpc.domainId =:domainId AND vpc.isActive =:isActive")
    Page<VPC> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all vpcs by project and deparment with pagination.
     *
     * @param allProjectList project list.
     * @param departmentId of the vpc.
     * @param isActive status of the vpc.
     * @param pageable to get the list with pagination.
     * @return list of vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE (vpc.project in :allProjectList OR vpc.departmentId=:departmentId ) AND vpc.isActive =:isActive")
    Page<VPC> findByProjectDepartmentAndIsActive(@Param("allProjectList") List<Project> allProjectList,
            @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all vpcs by project and deparment with pagination.
     *
     * @param allProjectList project list.
     * @param departmentId of the vpc.
     * @param isActive status of the vpc.
     * @param pageable to get the list with pagination.
     * @return list of vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE (vpc.project in :allProjectList OR vpc.departmentId=:departmentId ) AND vpc.isActive =:isActive")
    List<VPC> findAByProjectDepartmentAndIsActiveWithoutPaging(@Param("allProjectList") List<Project> allProjectList,
            @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive);


    /**
     * Find all the domain based active or inactive vpc.
     *
     * @param domainId get the id of the domain
     * @param isActive get the vpc list based on active/inactive status.
     * @return list of vpcs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE vpc.domainId =:domainId AND vpc.isActive =:isActive")
    List<VPC> findAllByDomainAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

    /**
     * Find all the domain based active or inactive VPC with pagination.
     *
     * @param domainId domain id of the VPC
     * @param search search text.
     * @param isActive get the VPC list based on active/inactive status
     * @param pageable to get the list with pagination
     * @return list of VPCs
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE (vpc.domainId=:domainId OR 0 = :domainId) AND vpc.isActive = :isActive AND (vpc.name LIKE %:search% OR vpc.description LIKE %:search% OR vpc.cIDR LIKE %:search% OR vpc.department.userName LIKE %:search% OR vpc.domain.name LIKE %:search% OR vpc.zone.name LIKE %:search% OR vpc.project.name LIKE %:search% OR vpc.status LIKE %:search%)")
    Page<VPC> findDomainBySearchText(@Param("domainId") Long domainId, Pageable pageable, @Param("search") String search, @Param("isActive") Boolean isActive);

    /**
     * Find all VPCs by project and department with pagination.
     *
     * @param domainId domain id of the VPC
     * @param allProjectList project list.
     * @param departmentId of the VPC.
     * @param search search text.
     * @param isActive status of the VPC.
     * @param pageable to get the list with pagination.
     * @return list of VPCs.
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE (vpc.domainId=:domainId OR 0 = :domainId) AND (vpc.project in :allProjectList OR vpc.departmentId=:departmentId ) AND vpc.isActive =:isActive AND (vpc.name LIKE %:search% OR vpc.description LIKE %:search% OR vpc.cIDR LIKE %:search% OR vpc.department.userName LIKE %:search% OR vpc.domain.name LIKE %:search% OR vpc.zone.name LIKE %:search% OR vpc.project.name LIKE %:search% OR vpc.status LIKE %:search%)")
    Page<VPC> findByProjectDepartmentAndIsActiveAndSearchText(@Param("allProjectList") List<Project> allProjectList, @Param("departmentId") Long departmentId,
            @Param("isActive") Boolean isActive, Pageable pageable, @Param("search") String search, @Param("domainId") Long domainId);

    /**
     * Find all the domain based active or inactive VPC with pagination.
     *
     * @param domainId domain id of the VPC
     * @param departmentId department id of the VPC
     * @param search search text.
     * @param isActive get the VPC list based on active/inactive status
     * @param pageable to get the list with pagination
     * @return list of VPCs
     */
    @Query(value = "SELECT vpc FROM VPC vpc LEFT JOIN vpc.project WHERE (vpc.domainId=:domainId OR 0 = :domainId) AND vpc.departmentId = :departmentId AND vpc.projectId  = NULL AND vpc.isActive = :isActive AND (vpc.name LIKE %:search% OR vpc.description LIKE %:search% OR vpc.cIDR LIKE %:search% OR vpc.department.userName LIKE %:search% OR vpc.domain.name LIKE %:search% OR vpc.zone.name LIKE %:search% OR vpc.project.name LIKE %:search% OR vpc.status LIKE %:search%)")
    Page<VPC> findAllByDepartmentIsActiveAndSearchText(@Param("domainId") Long domainId, @Param("departmentId") Long departmentId, Pageable pageable, @Param("search") String search, @Param("isActive") Boolean isActive);
}
