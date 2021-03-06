package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.User;
import ck.panda.domain.entity.VmInstance;
import ck.panda.domain.entity.VmInstance.Status;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Virtual Machine. This service provides basic CRUD and essential api's for Virtual Machine related
 * business actions.
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
     * Find vm instance by id with specified field.
     *
     * @param vmId instance id.
     * @return instance.
     */
    VmInstance findVMByIDWithSpecifiedField(Long vmId);

   /**
    * Create new instance from panda.
    *
    * @param vmInstance vmInstance to save.
    * @param userId created user.
    * @return vm Instance.
    * @throws Exception unhandled exception.
    */
   VmInstance saveVmInstance(VmInstance vmInstance, Long userId) throws Exception;

    /**
     * Find vm instance by id.
     *
     * @param id instance id.
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
    VmInstance handleAsyncJobByEventName(String vmId, String event) throws Exception;

    /**
     * To get list of instance from cloudstack server.
     *
     * @return vm list.
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findAllFromCSServer() throws Exception;

    /**
     * To get list of instance by except status.
     *
     * @param status of instance.
     * @return vm list.
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findAllByExceptStatus(Status status) throws Exception;

    /**
     * VM related events are handled.
     *
     * @param vmInstance Virtual machine.
     * @param event event message.
     * @param userId user id.
     * @return instance.
     * @throws Exception if error occurs.
     */
    VmInstance handleAsyncJobByVM(VmInstance vmInstance, String event, Long userId) throws Exception;

    /**
     * Upgrade/Downgrade vm offerings for created instance.
     *
     * @param vminstance virtual machine.
     * @return instance.
     * @throws Exception if error occurs.
     */
    VmInstance upgradeDowngradeVM(VmInstance vminstance) throws Exception;

    /**
     * Find all the instance based on the given status and page request.
     *
     * @param pagingAndSorting page request.
     * @param userId user id.
     * @return instances.
     * @throws Exception unhandled errors.
     */
    Page<VmInstance> findAllVM(PagingAndSorting pagingAndSorting, Long userId) throws Exception;

    /**
     * Find all the instance based on the given status.
     *
     * @param userId user id.
     * @return instances.
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findAllVMList(Long userId) throws Exception;

    /**
     * Find all the instance based on the given status for paginated list.
     *
     * @param pagingAndSorting page request.
     * @param status status of vm.
     * @param userId user id.
     * @return instances.
     * @throws Exception unhandled errors.
     */
    Page<VmInstance> findAllByStatus(PagingAndSorting pagingAndSorting, Status status, Long userId) throws Exception;

    /**
     * Get the count of the instance based on the status.
     *
     * @param status status of vm.
     * @param userId user id.
     * @return count.
     */
    Integer findCountByStatus(Status status, Long userId);

    /**
     * Get the count of the instance based on the status.
     *
     * @param vminstance instance to display
     * @return instance
     * @throws Exception unhandled errors.
     */
    VmInstance updateDisplayName(VmInstance vminstance) throws Exception;

    /**
     * Find vm instance associated with department.
     *
     * @param departmentId of the department.
     * @return department
     * @param status status of vm.
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findAllByDepartmentAndVmStatus(Long departmentId, Status status) throws Exception;

    /**
     * Find list of vm Instances with pagination.
     *
     * @param pagingAndSorting parameters.
     * @param userId user id.
     * @return page result of intances.
     * @throws Exception if error occurs.
     */
    Page<VmInstance> findAllByUser(PagingAndSorting pagingAndSorting, Long userId) throws Exception;

    /**
     * To get list of instance by status.
     *
     * @param pagingAndSorting parameters.
     * @param status status.
     * @return vm list.
     * @throws Exception unhandled errors.
     */
    Page<VmInstance> findAllBySort(PagingAndSorting pagingAndSorting, Status status) throws Exception;

    /**
     * Find list of vm Instances without pagination.
     *
     * @param userId user id.
     * @return result of instance.
     * @throws Exception if error occurs.
     */
    List<VmInstance> findAllByUser(Long userId) throws Exception;

    /**
     * Find all vm instances associated with project.
     *
     * @param projectId of the project.
     * @param statusCode status of instance
     * @return vm list.
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findAllByProjectAndStatus(Long projectId, List<Status> statusCode) throws Exception;

    /**
     * Find vm instance associated with department.
     *
     * @param departmentId of the department.
     * @param statusCode status of instance
     * @return vm list.
     * @throws Exception unhandled errors.
     */
    List<VmInstance> findAllByDepartmentAndStatus(Long departmentId, List<Status> statusCode) throws Exception;

    /**
     * Find all vm instances associated with compute offering.
     *
     * @param computeOfferingId of the compute offer.
     * @param status status of vm.
     * @return vm list.
     * @throws Exception error occurs.
     */
    List<VmInstance> findAllByComputeOfferingIdAndVmStatus(Long computeOfferingId, Status status) throws Exception;

    /**
     * Find all vm instances associated with network.
     *
     * @param networkId of the instance.
     * @param status of instance
     * @return vm list.
     * @throws Exception error occurs.
     */
    List<VmInstance> findAllByNetworkAndVmStatus(Long networkId, Status status) throws Exception;

    /**
     * Find all vm instances associated with network nic.
     *
     * @param networkId of network
     * @return vm list.
     * @throws Exception error occurs.
     */
    List<VmInstance> findAllByNic(Long networkId) throws Exception;

    /**
     * Find vm instance by status.
     *
     * @param status of the vm instance
     * @param userId of user id.
     * @return list of instances
     * @throws Exception if error occurs.
     */
    List<VmInstance> findByVmStatus(List<Status> status, Long userId) throws Exception;

    /**
     * Find all vm instances associated with storage offering.
     *
     * @param storageOfferingId of the storage offer.
     * @param expunging status of vm.
     * @return vm list.
     * @throws Exception error occurs.
     */
    List<VmInstance> findAllByStorageOfferingIdAndVmStatus(Long storageOfferingId, Status expunging) throws Exception;

    /**
     * Find vm instance with VNC password by id.
     *
     * @param id instance id.
     * @return instance.
     * @throws Exception error occurs.
     */
    VmInstance findByIdWithVncPassword(Long id) throws Exception;

    /**
     * Find domain based list of vm Instances with pagination.
     *
     * @param pagingAndSorting parameters.
     * @param domainId domain id.
     * @param searchText quick search text
     * @param userId user id.
     * @return page result of intances.
     * @throws Exception if error occurs.
     */
    Page<VmInstance> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting, String searchText, Long userId) throws Exception;

    /**
     * Find all the domain instance based on the given status for paginated list.
     *
     * @param pagingAndSorting page request.
     * @param status status of vm.
     * @param domainId domain id.
     * @param searchText quick search text
     * @param userId user id.
     * @return instances.
     * @throws Exception unhandled errors.
     */
    Page<VmInstance> findAllByStatusAndDomain(PagingAndSorting pagingAndSorting, Status status, Long domainId, String searchText, Long userId) throws Exception;

    /**
     * Get the count of the instance based on the status and domain.
     *
     * @param status status of vm.
     * @param userId user id.
     * @param searchText search text.
     * @param domainId user id.
     * @return count.
     */
    Integer findCountByStatusAndDomain(Status status, Long domainId, Long userId, String searchText);

    /**
     * Find list of vm Instances by domain without pagination.
     *
     * @param domainId user id.
     * @param userId user id.
     * @param searchText search text.
     * @return result of instance.
     * @throws Exception if error occurs.
     */
    List<VmInstance> findAllByDomain(Long domainId, Long userId, String searchText) throws Exception;

    /**
     * Reset SSH Key in created instance.
     *
     * @param vminstance virtual machine.
     * @return instance.
     * @throws Exception if error occurs.
     */
    VmInstance resetSSHKey(VmInstance vminstance) throws Exception;

    /**
     * Find All by Vm and status.
     *
     * @param status of the vm instance
     * @param user reference of the user.
     * @return list of instances
     * @throws Exception if error occurs.
     */
    List<VmInstance> findAllByUserAndStatus(User user, Status status) throws Exception;

    /**
     * Update vm to stopped status by user and status.
     *
     * @param user user reference.
     * @param status status of the instance.
     */
    void updateVmToStoppedByOwnerAndStatus(User user, Status status);

    /**
     * Update the affinity group for created instance.
     *
     * @param vminstance virtual machine.
     * @return instance.
     * @throws Exception if error occurs.
     */
    VmInstance affinityGroup(VmInstance vminstance) throws Exception;

    /**
     * Get instance list by affinity group id.
     *
     * @param id group id
     * @return list of instances
     * @throws Exception if error occurs.
     */
    List<VmInstance> findInstanceByGroup(Long id) throws Exception;
}
