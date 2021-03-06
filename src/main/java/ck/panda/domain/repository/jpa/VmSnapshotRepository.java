package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.VmSnapshot;
import ck.panda.domain.entity.VmSnapshot.Status;

/**
 * Jpa Repository for VmSnapshot entity.
 */
@Repository
public interface VmSnapshotRepository extends PagingAndSortingRepository<VmSnapshot, Long> {
    /**
     * Find vm snapshot by uuid.
     *
     * @param uuid snapshot uuid.
     * @return Vm snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot WHERE snapshot.uuid LIKE :uuid ")
    VmSnapshot findByUUID(@Param("uuid") String uuid);

    /**
     * Find vm snapshot by instance.
     *
     * @param vmId instance id.
     * @param isRemoved check whether removed or not.
     * @return Vm snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot WHERE snapshot.vmId=:vmId AND snapshot.isRemoved IS :isRemoved )")
    List<VmSnapshot> findByVmInstance(@Param("vmId") Long vmId, @Param("isRemoved") Boolean isRemoved);

    /**
     * Find all vm snapshot without expunging status by active.
     *
     * @param pageable paging and sorting.
     * @param status of the snapshot
     * @param isRemoved check whether removed or not.
     * @return Vm snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot LEFT JOIN snapshot.vm WHERE snapshot.isRemoved IS :isRemoved AND snapshot.status <>:status)")
    Page<VmSnapshot> findAllByActiveAndExpunging(Pageable pageable, @Param("isRemoved") Boolean isRemoved, @Param("status") Status status);

    /**
     * Find all domain based vm snapshot without expunging status by active.
     *
     * @param domainId domain id of the vm snapshot
     * @param pageable paging and sorting.
     * @param isRemoved check whether removed or not.
     * @param status of the snapshot
     * @return Vm snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot LEFT JOIN snapshot.vm WHERE snapshot.domainId =:domainId AND snapshot.isRemoved IS :isRemoved AND snapshot.status <>:status)")
    Page<VmSnapshot> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isRemoved") Boolean isRemoved, @Param("status") Status status, Pageable pageable);


    /**
     * Find all domain based vm snapshot without expunging status by active.
     *
     * @param domainId domain id of the vm snapshot
     * @param pageable paging and sorting.
     * @param isRemoved check whether removed or not.
     * @param status of the snapshot
     * @return Vm snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot LEFT JOIN snapshot.vm WHERE (snapshot.domainId =:domainId OR 0L = :domainId) AND snapshot.isRemoved IS :isRemoved AND snapshot.status <>:status AND (snapshot.name LIKE %:search% OR snapshot.description LIKE %:search% "
            + "OR snapshot.vm.name LIKE %:search% OR snapshot.createdDateTime LIKE %:search% )")
    Page<VmSnapshot> findAllByDomainIdAndIsActiveSearchText(@Param("domainId") Long domainId, @Param("isRemoved") Boolean isRemoved, @Param("status") Status status, Pageable pageable,@Param("search") String search);

    /**
     * Find all domain based vm snapshot without expunging status by active.
     *
     * @param domainId domain id of the vm snapshot
     * @param pageable paging and sorting.
     * @param isRemoved check whether removed or not.
     * @return Vm snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot LEFT JOIN snapshot.vm WHERE (snapshot.domainId =:domainId OR 0L = :domainId) AND snapshot.departmentId= :departmentId AND snapshot.isRemoved IS :isRemoved AND snapshot.status <>:status AND (snapshot.name LIKE %:search% OR snapshot.description LIKE %:search% "
            + "OR snapshot.vm.name LIKE %:search% OR snapshot.createdDateTime LIKE %:search% )")
    Page<VmSnapshot> findAllByDomainIdDepartmentIdAndIsActiveSearchText(@Param("domainId") Long domainId, @Param("isRemoved") Boolean isRemoved, Pageable pageable,@Param("search") String search,@Param("status") Status status,@Param("departmentId") Long departmentId);


    /**
     * Find all snapshots by projects, department and isRemoved status.
     *
     * @param allProjectList list project which consists of recently added user.
     * @param departmentId of the project
     * @param isRemoved status of the snapshot.
     * @param status of the snapshot
     * @param pageable for paging and sorting
     * @return snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot LEFT JOIN snapshot.project LEFT JOIN snapshot.vm WHERE (snapshot.project in :allProjectList OR snapshot.departmentId = :departmentId ) AND snapshot.isRemoved =:isRemoved AND snapshot.status <>:status")
    Page<VmSnapshot> findByProjectDepartmentAndIsActive(@Param("allProjectList") List<Project> allProjectList,
            @Param("departmentId") Long departmentId,
            @Param("isRemoved") Boolean isRemoved, Pageable pageable,@Param("status") Status status);

    /**
     * Find all snapshots by department with paging and sorting.
     *
     * @param status of the snapshot
     * @param departmentId of the project.
     * @param isRemoved status of the project.
     * @param pageable for paging and sorting.
     * @return snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot LEFT JOIN snapshot.project LEFT JOIN snapshot.vm WHERE snapshot.departmentId= :departmentId AND snapshot.isRemoved IS :isRemoved AND snapshot.projectId = NULL AND snapshot.status <>:status ")
    Page<VmSnapshot> findByDepartmentAndPagination(@Param("departmentId") Long departmentId,
            @Param("isRemoved") Boolean isRemoved, Pageable pageable,@Param("status") Status status);

    /**
     * Find all domain based vm snapshot without expunging status by active.
     *
     * @param domainId domain id of the vm snapshot
     * @param pageable paging and sorting.
     * @param isRemoved check whether removed or not.
     * @param status of the snapshot
     * @return Vm snapshot.
     */
    @Query(value = "SELECT snapshot FROM VmSnapshot snapshot LEFT JOIN snapshot.vm WHERE (snapshot.domainId =:domainId OR 0L = :domainId) AND snapshot.isRemoved IS :isRemoved AND snapshot.status <>:status AND (snapshot.name LIKE %:search% OR snapshot.description LIKE %:search% "
            + "OR snapshot.vm.name LIKE %:search% OR snapshot.createdDateTime LIKE %:search%) AND snapshot.ownerId= :ownerId")
    Page<VmSnapshot> findAllByDomainIdAndIsActiveSearchTextAndUserId(@Param("domainId") Long domainId, @Param("isRemoved") Boolean isRemoved, @Param("status") Status status, Pageable pageable,@Param("search") String search, @Param("ownerId") Long ownerId);

}
