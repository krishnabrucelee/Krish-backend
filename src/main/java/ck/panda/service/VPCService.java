package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.VPC;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

@Service
public interface VPCService extends CRUDService<VPC> {
    /**
     * To get list of vpc for sync.
     *
     * @return vpc list from server.
     * @throws Exception unhandled errors.
     */
    List<VPC> findAllFromCSServer() throws Exception;

    /**
     * To get vpc from cloudstack server.
     *
     * @param uuid network uuid.
     * @return vpc from server
     * @throws Exception unhandled errors.
     */
    VPC findByUUID(String uuid) throws Exception;

    /**
     * To get vpc from cloudstack server.
     *
     * @param id network id.
     * @return network from server
     * @throws Exception unhandled errors.
     */
    VPC findById(Long id) throws Exception;

    /**
     * To get list of vpc from department.
     *
     * @param department department.
     * @param isActive true/false.
     * @return vpc list from server.
     * @throws Exception unhandled errors.
     */
    List<VPC> findByDepartmentAndVpcIsActive(Long department, Boolean isActive) throws Exception;

    /**
     * Soft delete for vpc.
     *
     * @param vpc get vpc id.
     * @return vpc.
     * @throws Exception exception
     */
    VPC softDelete(VPC vpc) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list which are active.
     *
     * @param page pagination
     * @param userId id of the user
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<VPC> findAllByActive(PagingAndSorting page, Long userId) throws Exception;

    /**
     * To get list of vpc from project.
     *
     * @param projectId project id.
     * @param isActive true/false.
     * @return vpc list from server.
     * @throws Exception unhandled errors.
     */
    List<VPC> findByProjectAndVpcIsActive(Long projectId, Boolean isActive) throws Exception;

    /**
     * To get active vpcs list.
     *
     * @param isActive status of the vpc
     * @return vpc
     * @throws Exception if error occurs.
     */
    List<VPC> findAllByActive(Boolean isActive) throws Exception;

    /**
     * Save method in which userId is passed for tokenDetails.
     *
     * @param vpc vpc
     * @param userId id of the user
     * @return vpc
     * @throws Exception unHandled errors
     */
    VPC save(VPC vpc, Long userId) throws Exception;

    /**
     * Restart vpc for reapplying rules and ip addresses.
     *
     * @param vpc to be restarted.
     * @return vpc.
     * @throws Exception if error occurs.
     */
    VPC restartVPC(VPC vpc) throws Exception;

    /**
     * Release ip from vpc
     *
     * @param vpc object
     * @return vpc
     * @throws Exception if error occurs.
     */
    VPC ipRelease(VPC vpc) throws Exception;

    /**
     * Find all the domain based vpc list.
     *
     * @param domainId domain id of the vpc
     * @param page pagination and sorting values.
     * @return list of vpc with pagination.
     * @throws Exception unhandled errors.
     */
    Page<VPC> findAllByDomainId(Long domainId, PagingAndSorting page) throws Exception;

    /**
     * Find all by domain and isactive.
     * @param domainId domain id.
     * @param isActive status.
     * @return vpc list.
     * @throws Exception if error.
     */
    List<VPC> findAllByDomainAndIsActive(Long domainId, Boolean isActive) throws Exception;

    /**
     * Find all the vpc by user id.
     *
     * @param userId user id.
     * @return vpc list.
     * @throws Exception if error.
     */
    List<VPC> findAllByUserId(Long userId) throws Exception;

    /**
     * Find all the vpcs by domain id.
     *
     * @param userId user id.
     * @return vpc list.
     * @throws Exception if error.
     */
    List<VPC> findAllByDomainId(Long domainId) throws Exception;

    /**
     * Find domain based list on VPC with pagination.
     *
     * @param pagingAndSorting parameters.
     * @param domainId domain id.
     * @param searchText quick search text
     * @param userId user id.
     * @return page result of VPC.
     * @throws Exception if error occurs.
     */
    Page<VPC> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText, Long userId) throws Exception;

    /**
     * To get vpc from cloudstack server.
     *
     * @param id VPC id.
     * @return vpc from server
     * @throws Exception unhandled errors.
     */
    VPC findVpcById(Long id) throws Exception;

}
