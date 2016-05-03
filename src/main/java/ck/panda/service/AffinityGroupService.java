package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.AffinityGroup;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for affinity group.
 *
 * This service provides basic CRUD and essential api's for affinity group related business actions.
 */
@Service
public interface AffinityGroupService extends CRUDService<AffinityGroup> {

    /**
     * Get all affinity group from cloud stack server.
     *
     * @return list of active affinity group.
     * @throws Exception if error occurs.
     */
    List<AffinityGroup> findAllFromCSServer() throws Exception;

    /**
     * Save the affinity group.
     *
     * @param affinityGroup SSHKey entity
     * @param id of the login user
     * @return affinityGroup
     * @throws Exception error occurs
     */
    AffinityGroup save(AffinityGroup affinityGroup, Long id) throws Exception;

    /**
     * Get all affinity group by department.
     *
     * @param departmentId department id
     * @return list of active affinity group.
     * @throws Exception if error occurs.
     */
    List<AffinityGroup> findByDepartment(Long departmentId) throws Exception;

    /**
     * Get all affinity group with pagination.
     *
     * @param pagingAndSorting object
     * @param id of the login user
     * @return list of active affinity group.
     * @throws Exception if error occurs.
     */
    Page<AffinityGroup> findAll(PagingAndSorting pagingAndSorting, Long id) throws Exception;

    /**
     * Find all affinity group by domain.
     *
     * @param domainId domain id of the affinity group
     * @param searchText search text.
     * @param pagingAndSorting paging and sorting information.
     * @return list of affinity group.
     * @throws Exception if error occurs.
     */
    Page<AffinityGroup> findAllByDomainId(Long domainId, String searchText, PagingAndSorting pagingAndSorting) throws Exception;

}
