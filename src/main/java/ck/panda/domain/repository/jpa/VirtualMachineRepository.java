package ck.panda.domain.repository.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ck.panda.domain.entity.Department;
import ck.panda.domain.entity.VmInstance;

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
    * @param id instance id.
    * @return instance.
    */
   @Query(value = "select vm from VmInstance vm where vm.name=:name AND vm.department=:department)")
   VmInstance findByNameAndDepartment(@Param("name") String name, @Param("department") Department department);
}
