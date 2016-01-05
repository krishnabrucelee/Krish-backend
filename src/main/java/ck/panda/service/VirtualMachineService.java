package ck.panda.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.Project;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Virtual Machine. This service provides basic CRUD and essential api's for Virtual Machine
 * related business actions.
 */
@Service
public interface VirtualMachineService extends CRUDService<VmInstance> {

    /**
     * Find vm instance by uuid.
     *
     * @param uuid instance uuid.
     * @return instance.
     */
    VmInstance findByUUID(String uuid);
    
    /**
     * Find vm instance by id.
     *
     * @param uuid instance id.
     * @return instance.
     */
    VmInstance findById(Long id);
    
   
    /**
     * VM related events are handled.
     *
     * @param vmId Virtual machine Id.
     * @param event event message.
     * @return instance.
     * @throws Exception if error occurs.
     */
    VmInstance vmEventHandle(String vmId, String event) throws Exception;

    /**
     * To get list of instance from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findAllFromCSServer() throws Exception;

    /**
     * VM related events are handled.
     *
     * @param vmInstance Virtual machine.
     * @param event event message.
     * @return instance.
     * @throws Exception if error occurs.
     */
    VmInstance vmEventHandleWithVM(VmInstance vmInstance, String event) throws Exception;

    /**
     * Upgrade/Downgrade VM offerings for created instance.
     *
     * @param vminstance Virtual machine
     * @return instance
     * @throws Exception if error occurs
     */
    VmInstance upgradeDowngradeVM(VmInstance vminstance) throws Exception;

    /**
     * Find all the instance based on the given status for paginated list.
     *
     * @param pagingAndSorting
     * @param status
     * @return
     * @throws Exception
     */
    Page<VmInstance> findAllByStatus(PagingAndSorting pagingAndSorting, String status) throws Exception;

    /**
     * Get the count of the instance based on the status.
     *
     * @param status
     * @return
     */
    Integer findCountByStatus(Status status);

    /**
     * Get the count of the instance based on the status.
     *
     * @param vminstance instance to display
     * @return instance
     * @throws Exception unhandled errors.
     */
    VmInstance updateDisplayName(VmInstance vminstance) throws Exception;

    /**
     * Find vm Instance associated with department.
     *
     * @param deaprtmentId of the department.
     * @return department
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findByDepartmentAndVmStatus(Long departmentId, Status status) throws Exception;

    /**
     * Find vm Instance associated with project.
     *
     * @param projectId of the project.
     * @param statusCode status of instance
     * @return project
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findByProjectAndStatus(Long projectId, List<Status> statusCode) throws Exception;

    /**
     * Find vm Instance associated with department.
     *
     * @param departmentId of the department.
     * @param status status of instance
     * @return department
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findByDepartmentAndStatus(Long departmentId,List<Status> statusCode)throws Exception;

    /**
     * Find vm Instance assocaited with compute offering.
     * 
     * @param computeOfferingId of the compute offer
     * @return compute offering
     * @throws Exception error occurs.
     */
	List<VmInstance> findByComputeOfferingIdAndVmStatus(Long computeOfferingId, Status status) throws Exception;

}
