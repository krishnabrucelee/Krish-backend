package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Domain;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Domain. This service provides basic CRUD and essential api's for Domain related business actions.
 *
 */
@Service
public interface DomainService extends CRUDService<Domain> {

    /**
     * To get list of domains from cloudstack server.
     *
     * @return domain list from server
     * @throws Exception unhandled errors.
     */
    List<Domain> findAllFromCSServer() throws Exception;

    /**
     * Find domain by uuid.
     *
     * @param uuid uuid of domain.
     * @return domain object.
     * @throws Exception unhandled errors.
     */
    Domain findbyUUID(String uuid) throws Exception;

    /**
     * Find domain by uuid and isActive.
     *
     * @param uuid uuid of domain.
     * @return domain object.
     * @throws Exception unhandled errors.
     */
    Domain findByUUIDAndIsActive(String uuid) throws Exception;

    /**
     * Find domain by name.
     *
     * @param name of domain.
     * @return domain object.
     * @throws Exception unhandled errors.
     */
    Domain findByName(String name) throws Exception;

    /**
     * Soft delete for domain.
     *
     * @param domain object
     * @return domain
     * @throws Exception unhandled errors.
     */
    Domain softDelete(Domain domain) throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<Domain> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * To get list of domains.
     *
     * @return domain list
     * @throws Exception unhandled errors.
     */
    List<Domain> findAllDomain() throws Exception;

     /**
     * Retrieves domain entity by its id.
     *
     * @return domain
     * @throws Exception unhandled errors.
     */
    Domain findDomain() throws Exception;

    /**
     * Paging and Sorting for displaying more number of elements in list.
     *
     * @param pagingAndSorting sortable method.
     * @param searchText search text
     * @return sorted values.
     * @throws Exception unhandled errors.
     */
    Page<Domain> findDomainBySearchText(PagingAndSorting pagingAndSorting, String searchText) throws Exception;

    /**
     * Update the domain to suspended state.
     *
     * @return domain
     * @throws Exception unhandled errors.
     */
    Domain updateSuspended(Domain domain) throws Exception;
    
}
