package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Application;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service class for Application.
 *
 * This service provides basic CRUD and essential api's for Application related business actions.
 */
@Service
public interface ApplicationService extends CRUDService<Application> {

    /**
     * Delete the application.
     *
     * @param application Application entity
     * @return Application
     * @throws Exception error occurs
     */
    Application softDelete(Application application) throws Exception;

    /**
     * Find all the applications with respect to domain.
     *
     * @param domainId of the application
     * @return list of applications with respect to domain
     * @throws Exception if error occurs
     */
    List<Application> findAllByDomain(Long domainId) throws Exception;

    /**
     * To get list of application.
     *
     * @param pagingAndSorting parameters
     * @param id of the login user
     * @return application list with pagination
     * @throws Exception if error occurs
     */
    Page<Application> findAll(PagingAndSorting pagingAndSorting, Long id) throws Exception;

    /**
     * To get list of application.
     *
     * @param id of the login user
     * @return application list
     * @throws Exception if error occurs
     */
    List<Application> findAll(Long id) throws Exception;

    /**
     * To get list of application by domain.
     *
     * @param domainId domain id of the application
     * @param pagingAndSorting parameters
     * @return application list with pagination
     * @throws Exception if error occurs
     */
    Page<Application> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception;

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
    Page<Application> findAllByDomainIdAndSearchText(Long domainId, PagingAndSorting pagingAndSorting, String searchText, Long userId) throws Exception;

}
