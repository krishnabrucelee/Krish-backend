package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;

/**
 * Jpa Repository for VmInstance entity.
 */
@Repository
public interface VirtualMachineRepository extends PagingAndSortingRepository<VmInstance, Long> {

    /**
     * Find vm instance by uuid.
     *
     * @param uuid instance uuid.
     * @return instance.
     */
    @Query(value = "select vm from VmInstance vm where vm.uuid LIKE :uuid ")
    VmInstance findByUUID(@Param("uuid") String uuid);

    /**
     * Find vm instance by id.
     *
     * @param id instance id.
     * @return instance.
     */
    @Query(value = "select vm from VmInstance vm where vm.id LIKE :id ")
    VmInstance findById(@Param("id") Long id);

    /**
     * Find vm instance by name and department.
     *
     * @param name instance name.
     * @param status of the status of VM.
     * @param department department object.
     * @return instance.
     */
    @Query(value = "select vm from VmInstance vm where vm.name=:name AND vm.department=:department AND vm.status <> :status")
    VmInstance findByNameAndDepartment(@Param("name") String name, @Param("department") Department department,
            @Param("status") Status status);

    /**
     * Get the list of VMs by domain and status.
     *
     * @param id of the domain
     * @param status of the domain
     * @param pageable page request
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.domainId=:id AND vm.status <>:status")
    Page<VmInstance> findAllByDomainIsActive(@Param("id") Long id, @Param("status") Status status, Pageable pageable);

    /**
     * Get the list of VMs by domain and status.
     *
     * @param id of the domain
     * @param status of the domain
     * @param pageable page request
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.domainId=:id AND vm.status =:status")
    Page<VmInstance> findAllByDomainIsActiveAndStatus(@Param("id") Long id, @Param("status") Status status,
            Pageable pageable);

    /**
     * Get the list of VMs by domain and status.
     *
     * @param id of the domain
     * @param status of the domain
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.domainId=:id AND vm.status =:status")
    List<VmInstance> findAllByDomainIsActiveAndStatus(@Param("id") Long id, @Param("status") Status status);

    /**
     * Get the list of VMs by domain and status.
     *
     * @param id of the domain
     * @param status of the domain
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.domainId=:id AND vm.status <>:status")
    List<VmInstance> findAllByDomainIsActive(@Param("id") Long id, @Param("status") Status status);

    /**
     * Get the list of VMs by domain and status.
     *
     * @param id of the domain
     * @param status of the domain
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.domainId=:id AND vm.status <>:status")
    List<VmInstance> findAllByDomain(@Param("id") Long id, @Param("status") Status status);

    /**
     * Get the list of VMs by status.
     *
     * @param status of the status of VM.
     * @param pageable page request
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status <> :status")
    Page<VmInstance> findAllByIsActive(@Param("status") Status status, Pageable pageable);

    /**
     * Get the list of VMs by status.
     *
     * @param status of the status of VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status <> :status")
    List<VmInstance> findAllByIsActive(@Param("status") Status status);

    /**
     * Get the list of VMs by status.
     *
     * @param status of the status of VM.
     * @param pageable page request
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status = :status")
    Page<VmInstance> findAllByStatus(@Param("status") Status status, Pageable pageable);

    /**
     * Get the list of VMs by status and user.
     *
     * @param status of the status of VM.
     * @param pageable page request.
     * @param instanceOwner belongs to VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status = :status and vm.instanceOwner = :user")
    Page<VmInstance> findAllByUserIsActive(@Param("status") Status status, Pageable pageable,
            @Param("user") User instanceOwner);

    /**
     * VmInstance. Get the list of VMs by status and user.
     *
     * @param status of the status of VM.
     * @param pageable page request.
     * @param instanceOwner belongs to VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status <> :status and vm.instanceOwner = :user")
    Page<VmInstance> findAllByUserIsActiveAndStatus(@Param("status") Status status, Pageable pageable,
            @Param("user") User instanceOwner);

    /**
     * Get the list of VMs by status and user.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status = :status and vm.instanceOwner = :user")
    List<VmInstance> findAllByUserIsActive(@Param("status") Status status, @Param("user") User instanceOwner);

    /**
     * Get the list of VMs by status and user.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status<>:status and vm.instanceOwner = :user")
    List<VmInstance> findAllByUserIsActiveAndStatus(@Param("status") Status status, @Param("user") User instanceOwner);

    /**
     * Get the list of VMs by status and user.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status <> :status and vm.instanceOwner = :user")
    List<VmInstance> findAllByUser(@Param("status") Status status, @Param("user") User instanceOwner);

    /**
     * Get the list of VMs by status and user.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @param project belongs to VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status = :status and (vm.instanceOwner = :user or vm.project = :project)")
    List<VmInstance> findAllByUserAndProjectIsActive(@Param("status") Status status, @Param("user") User instanceOwner,
            @Param("project") Project project);

    /**
     * Get the list of VMs by status and user.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @param project belongs to VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status <>:status and (vm.project = :project or vm.department = :department)")
    List<VmInstance> findAllByDepartmentAndProjectIsActiveAndStatus(@Param("status") Status status,
            @Param("department") Department department, @Param("project") Project project);

    /**
     * Get the list of VMs by status and user.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @param project belongs to VM.
     * @return instance list
     */
    @Query(value = "select vm from VmInstance vm where vm.status <> :status and (vm.instanceOwner = :user or vm.project = :project)")
    List<VmInstance> findAllByUserAndProject(@Param("status") Status status, @Param("user") User instanceOwner,
            @Param("project") Project project);

    /**
     * Get the instance count by status.
     *
     * @param status instnace status.
     * @return Instance count.
     */
    @Query(value = "select COUNT(vm.id) from VmInstance vm where vm.status = :status")
    Integer findCountByStatus(@Param("status") Status status);

    /**
     * Find all vmInstance from department.
     *
     * @param departmentId department id.
     * @param status get the instance list based on current state.
     * @return vmInstance list.
     */
    @Query(value = "select vm from VmInstance vm where vm.departmentId=:id and vm.status <> :status ")
    List<VmInstance> findByDepartment(@Param("id") Long departmentId, @Param("status") Status status);

    /**
     * Find all vmInstance from department with pagination.
     *
     * @param departmentId department id.
     * @param pageable page request.
     * @param status get the instance list based on current state.
     * @return vmInstance list.
     */
    @Query(value = "select vm from VmInstance vm where vm.departmentId = :id and vm.status <> :status ")
    Page<VmInstance> findAllByDepartment(@Param("id") Long departmentId, @Param("status") Status status, Pageable pageable);

    /**
     * Find all vmInstance associated with Compute offering.
     *
     * @param computeOfferingId computeOffering id.
     * @param status get the instance list based on current state.
     * @return vmInstance list.
     */
    @Query(value = "select vm from VmInstance vm where vm.computeOfferingId=:computeOfferingId and vm.status <> :status  ")
    List<VmInstance> findByComputeOffering(@Param("computeOfferingId") Long computeOfferingId,
            @Param("status") Status status);

    /**
     * Find all vmInstance from project.
     *
     * @param projectId project id.
     * @param statusCode get the project list based on status.
     * @return project list.
     */
    @Query(value = "select vm from VmInstance vm where vm.projectId=:projectId and vm.status in :statusCode")
    List<VmInstance> findByProjectAndStatus(@Param("projectId") Long projectId,
            @Param("statusCode") List<Status> statusCode);

    /**
     * Find all vmInstance from department.
     *
     * @param departmentId department id.
     * @param statusCode get the department list based on status.
     * @return department list.
     */
    @Query(value = "select vm from VmInstance vm where vm.departmentId=:departmentId and vm.status in :statusCode")
    List<VmInstance> findByDepartmentAndStatus(@Param("departmentId") Long departmentId,
            @Param("statusCode") List<Status> statusCode);

    /**
     * Find all instance associated with network.
     *
     * @param networkId of the network.
     * @param status of the instance
     * @return network.
     */
    @Query(value = "select vm from VmInstance vm where vm.networkId=:id and vm.status <> :status ")
    List<VmInstance> findByNetwork(@Param("id") Long networkId, @Param("status") Status status);
}
