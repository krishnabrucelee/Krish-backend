package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ck.panda.domain.entity.Department;
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
    * Find vm instance by name and department.
    *
    * @param name instance name.
    * @param department department object.
    * @return instance.
    */
   @Query(value = "select vm from VmInstance vm where vm.name=:name AND vm.department=:department")
   VmInstance findByNameAndDepartment(@Param("name") String name, @Param("department") Department department);

   /**
    * Get the list of VMs by domain and status.
    *
    * @param id of the domain
    * @param status of the domain
    * @param pageable page request
    * @return instance list
    */
   @Query(value = "select vm from VmInstance vm where vm.domainId=:id AND vm.status =:status")
   Page<VmInstance> findAllByDomainIsActive(@Param("id") Long id, @Param("status") Status status, Pageable pageable);

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
    * @param pageable page request
    * @return instance list
    */
   @Query(value = "select vm from VmInstance vm where vm.status <> :status")
   List<VmInstance> findAllByIsActive(@Param("status") Status status);
}
