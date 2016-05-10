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
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.uuid LIKE :uuid ")
    VmInstance findByUUID(@Param("uuid") String uuid);

    /**
     * Find vm instance by name and department.
     *
     * @param name instance name.
     * @param status of the status of VM.
     * @param department department object.
     * @return instance.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.name = :name AND vm.department = :department AND vm.status <> :status")
    VmInstance findByNameAndDepartment(@Param("name") String name, @Param("department") Department department,
            @Param("status") Status status);

    /**
     * Get the list of VMs by domain and status with pagination.
     *
     * @param domainId of the domain.
     * @param status of the domain.
     * @param pageable page request.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, owner.userName as instanceOwner, vm.publicIpAddress as publicIpAddress, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.domainId= :domainId AND vm.status = :status "
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    Page<VmInstance> findAllByDomainAndStatusWithPageRequest(@Param("domainId") Long domainId,
            @Param("status") Status status, Pageable pageable, @Param("search") String search);

    /**
     * Get the list of VMs by domain and status.
     *
     * @param domainId of the domain.
     * @param status of the domain.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT vm.name FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status = :status AND (vm.domainId = :domainId OR 0 = :domainId) "
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    List<VmInstance> findAllByDomainAndStatus(@Param("domainId") Long domainId, @Param("status") Status status, @Param("search") String search);

    /**
     * Get the list of VMs by domain and except given status.
     *
     * @param id of the domain.
     * @param status of the domain.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status <> :status AND (vm.domainId = :id OR 0 = :id) "
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    List<VmInstance> findAllByDomainAndExceptStatus(@Param("id") Long id, @Param("status") Status status, @Param("search") String search);

    /**
     * Get the list of VMs by domain and except given status.
     *
     * @param id of the domain.
     * @param status list of status.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.status NOT IN :status AND vm.domainId = :id ")
    List<VmInstance> findAllByDomainAndExceptInStatus(@Param("id") Long id, @Param("status") List<Status> status);

    /**
     * Get the list of VMs by domain and except given status.
     *
     * @param id of the domain.
     * @param status of the domain.
     * @param pageable page request.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.publicIpAddress as publicIpAddress, vm.displayName as displayName, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.domainId = :id AND vm.status <> :status "
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    Page<VmInstance> findAllByDomainAndExceptStatus(@Param("id") Long id, @Param("status") Status status,
            Pageable pageable, @Param("search") String search);

    /**
     * Get the list of VMs by domain and except given status.
     *
     * @param status of the domain.
     * @param pageable page request.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.publicIpAddress as publicIpAddress, vm.displayName as displayName, vm.ipAddress as ipAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status <> :status ")
    Page<VmInstance> findAllByStatus(Pageable pageable, @Param("status") Status status);

    /**
     * Get the list of VMs by domain and except given status.
     *
     * @param id of the domain.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm LEFT JOIN vm.instanceOwner LEFT JOIN vm.template LEFT JOIN vm.computeOffering LEFT JOIN vm.department LEFT JOIN vm.project LEFT JOIN vm.domain LEFT JOIN vm.zone LEFT JOIN vm.storageOffering LEFT JOIN vm.networkOffering LEFT JOIN vm.network LEFT JOIN vm.host LEFT JOIN vm.hypervisor LEFT JOIN vm.hypervisor  WHERE vm.id = :id")
    VmInstance findVMByID(@Param("id") Long id);

    /**
     * Get the list of VMs by domain and except given status.
     *
     * @param id of the domain.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.id = :id")
    VmInstance findVMByIDWithSpecifiedField(@Param("id") Long id);

    /**
     * Get the list of VMs by except given status with pagination.
     *
     * @param status of the status of VM.
     * @param pageable page request.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner as owner WHERE vm.status <> :status ")
    Page<VmInstance> findAllByExceptStatusWithPageRequest(@Param("status") Status status, Pageable pageable);

    /**
     * Get the list of VMs by except given status.
     *
     * @param status of the status of VM.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.status <> :status")
    List<VmInstance> findAllByExceptStatus(@Param("status") Status status);

    /**
     * Get the list of VMs by status with pagination.
     *
     * @param status of the status of VM.
     * @param pageable page request.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status = :status ")
    Page<VmInstance> findAllByStatusWithPageRequest(@Param("status") Status status, Pageable pageable);

    /**
     * Get the list of VMs by status and user with pagination.
     *
     * @param status of the status of VM.
     * @param pageable page request.
     * @param instanceOwner belongs to VM.
     * @return instance list
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status = :status AND vm.instanceOwner = :user ")
    Page<VmInstance> findAllByUserAndStatusWithPageRequest(@Param("status") Status status, Pageable pageable,
            @Param("user") User instanceOwner);

    /**
     * Get the list of VMs by user and except given status with pagination.
     *
     * @param status of the status of VM.
     * @param pageable page request.
     * @param instanceOwner belongs to VM.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status <> :status AND vm.instanceOwner = :user ")
    Page<VmInstance> findAllByUserAndExceptStatusWithPageRequest(@Param("status") Status status, Pageable pageable,
            @Param("user") User instanceOwner);

    /**
     * Get the list of VMs by status and department.
     *
     * @param status of the status of VM.
     * @param department belongs to VM.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status = :status  AND vm.project is NULL AND vm.department = :department"
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    List<VmInstance> findAllByDepartmentAndStatus(@Param("status") Status status,
            @Param("department") Department department, @Param("search") String search);

    /**
     * Get the list of VMs by except given status and user.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.status <> :status AND vm.instanceOwner = :user")
    List<VmInstance> findAllByUserAndExceptStatus(@Param("status") Status status, @Param("user") User instanceOwner);

    /**
     * Get the list of VMs by except given status and department.
     *
     * @param status of the status of VM.
     * @param department belongs to VM.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status <> :status  AND vm.project is NULL AND vm.department = :department"
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    List<VmInstance> findAllByDepartmentAndExceptStatus(@Param("status") Status status,
            @Param("department") Department department, @Param("search") String search);

    /**
     * Get the list of VMs by status and user with pagination .
     *
     * @param status of the status of VM.
     * @param department belongs to VM.
     * @param pageable page request.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status = :status AND vm.project IS NULL AND vm.department = :department "
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    Page<VmInstance> findAllByDepartmentAndStatusWithPageRequest(@Param("status") Status status,
            @Param("department") Department department, Pageable pageable, @Param("search") String search);

    /**
     * Get the list of VMs by status and user belongs to department and project.
     *
     * @param status of the status of VM.
     * @param department belongs to VM.
     * @param project belongs to VM.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT vm.cpuCore as cpuCore, vm.memory as memory, vm.ipAddress as ipAddress, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize, vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status = :status  AND (vm.project = :project OR (vm.project IS NULL AND vm.department = :department))"
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    List<VmInstance> findAllByDepartmentAndProjectAndStatus(@Param("status") Status status,
            @Param("department") Department department, @Param("project") Project project, @Param("search") String search);

    /**
     * Get the list of VMs by except given status and user belongs to department
     * and project.
     *
     * @param status of the status of VM.
     * @param department belongs to VM.
     * @param project belongs to VM.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT vm.cpuCore as cpuCore, vm.memory as memory, vm.ipAddress as ipAddress, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize, vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status <> :status  AND (vm.project = :project OR (vm.project IS NULL AND vm.department = :department))"
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    List<VmInstance> findAllByDepartmentAndProjectAndExceptStatus(@Param("status") Status status,
            @Param("department") Department department, @Param("project") Project project, @Param("search") String search);

    /**
     * Get the list of VMs by status and user belongs to project.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @param project belongs to VM.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.status = :status AND (vm.instanceOwner = :user OR vm.project = :project)")
    List<VmInstance> findAllByUserAndProjectAndStatus(@Param("status") Status status, @Param("user") User instanceOwner,
            @Param("project") Project project);

    /**
     * Get the instance count by status.
     *
     * @param status instance status.
     * @return instance count.
     */
    @Query(value = "SELECT COUNT(vm.id) FROM VmInstance vm WHERE vm.status = :status")
    Integer findCountByStatus(@Param("status") Status status);

    /**
     * Find all instance by department and status.
     *
     * @param departmentId department id.
     * @param status get the instance list based on current state.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.departmentId = :departmentId AND vm.status <> :status")
    List<VmInstance> findByDepartmentAndStatus(@Param("departmentId") Long departmentId,
            @Param("status") Status status);

    /**
     * Find all instance by department and except given status with pagination.
     *
     * @param departmentId department id.
     * @param pageable page request.
     * @param status get the instance list based on current state.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.project IS NULL AND vm.departmentId = :departmentId AND vm.status <> :status "
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    Page<VmInstance> findAllByDepartmentAndExceptStatusWithPageRequest(@Param("departmentId") Long departmentId,
            @Param("status") Status status, Pageable pageable, @Param("search") String search);

    /**
     * Find all instance by status and associated with compute offering.
     *
     * @param computeOfferingId computeOffering id.
     * @param status list of status.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.computeOfferingId = :computeOfferingId AND vm.status <> :status")
    List<VmInstance> findByComputeOfferingAndStatus(@Param("computeOfferingId") Long computeOfferingId,
            @Param("status") Status status);

    /**
     * Find all instance by project and list of status.
     *
     * @param projectId project id.
     * @param statusCode get the project list based on status.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.projectId = :projectId AND vm.status IN :statusCode")
    List<VmInstance> findByProjectAndStatus(@Param("projectId") Long projectId,
            @Param("statusCode") List<Status> statusCode);

    /**
     * Find all instance by department and list of status.
     *
     * @param departmentId department id.
     * @param statusCode list of status.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.departmentId = :departmentId AND vm.projectId IS NULL AND vm.status IN :statusCode")
    List<VmInstance> findByDepartmentAndStatus(@Param("departmentId") Long departmentId,
            @Param("statusCode") List<Status> statusCode);

    /**
     * Find all instance by status and associated with network.
     *
     * @param networkId of the network.
     * @param status of the instance.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.networkId = :id AND vm.status <> :status")
    List<VmInstance> findByNetworkAndExceptStatus(@Param("id") Long networkId, @Param("status") Status status);

    /**
     * Find all instance by status and associated with storage offering.
     *
     * @param storageOfferingId storageOffering id.
     * @param status list of status.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.storageOfferingId = :storageOfferingId AND vm.status <> :status")
    List<VmInstance> findByStorageOfferingAndStatus(@Param("storageOfferingId") Long storageOfferingId,
            @Param("status") Status status);

    /**
     * Get the domain based list of VMs by except given status with pagination.
     *
     * @param status of the status of VM.
     * @param domainId of the domain.
     * @param pageable page request.
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status <> :status AND (vm.domainId = :domainId OR 0 = :domainId) "
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    Page<VmInstance> findAllByDomainAndExceptStatusWithPageRequest(@Param("status") Status status, @Param("domainId") Long domainId, Pageable pageable, @Param("search") String search);

    /**
     * Get the list of VMs by status and domain with pagination.
     *
     * @param status of the status of VM.
     * @param domainId of the domain.
     * @param pageable page request
     * @param search search text.
     * @return instance list
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize,vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status = :status AND (vm.domainId = :domainId OR 0 = :domainId) "
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    Page<VmInstance> findAllByStatusAndDomainWithPageRequest(@Param("status") Status status, @Param("domainId") Long domainId, Pageable pageable, @Param("search") String search);

    /**
     * Get the list of VMs by except given status and user belongs to department
     * and project list with pagination.
     *
     * @param status of the status of VM.
     * @param department belongs to VM.
     * @param projectList belongs to VM.
     * @param pageable page request
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.ipAddress as ipAddress, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize, vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status <> :status  AND (vm.project in :projectList OR (vm.project IS NULL AND vm.department = :department))"
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    Page<VmInstance> findAllByDepartmentAndProjectAndExceptStatusAndPage(@Param("status") Status status,
            @Param("department") Department department, @Param("projectList") List<Project> projectList, Pageable pageable, @Param("search") String search);

    /**
     * Get the list of VMs by status and user belongs to department and project list.
     *
     * @param status of the status of VM.
     * @param department belongs to VM.
     * @param projectList belongs to VM.
     * @param pageable page request
     * @param search search text.
     * @return instance list.
     */
    @Query(value = "SELECT new map(vm.cpuCore as cpuCore, vm.memory as memory, vm.ipAddress as ipAddress, vm.network as network, vm.displayName as displayName, vm.publicIpAddress as publicIpAddress, owner.userName as instanceOwner, vm.application as application, vm.osType as template, vm.volumeSize as volumeSize, vm.domainId as domainId, vm.ipAddress as ipAddress, vm.status as status, vm.id as id) FROM VmInstance vm LEFT JOIN vm.instanceOwner owner WHERE vm.status = :status  AND (vm.project in :projectList OR (vm.project IS NULL AND vm.department = :department))"
            + " AND (vm.displayName LIKE %:search% OR owner.userName LIKE %:search% OR vm.application LIKE %:search% OR vm.osType LIKE %:search% OR vm.cpuCore LIKE %:search%"
            + " OR vm.memory LIKE %:search% OR CONCAT(vm.volumeSize/POWER(2, 30), LOWER(' GB')) LIKE LOWER(%:search%) OR vm.publicIpAddress LIKE %:search% OR vm.ipAddress LIKE %:search%)")
    Page<VmInstance> findAllByDepartmentAndProjectAndStatusAndPage(@Param("status") Status status,
            @Param("department") Department department, @Param("projectList") List<Project> projectList, Pageable pageable, @Param("search") String search);


    /**
     * Get the list of VMs by given status and user.
     *
     * @param status of the status of VM.
     * @param instanceOwner belongs to VM.
     * @return instance list.
     */
    @Query(value = "SELECT vm FROM VmInstance vm WHERE vm.instanceOwner = :user AND vm.status = :status")
    List<VmInstance> findAllByUserAndStatus(@Param("user") User instanceOwner, @Param("status") Status status);

}
