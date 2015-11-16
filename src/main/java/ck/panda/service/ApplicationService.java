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
 * This service provides basic CRUD and essential api's for Application related
 * business actions.
 */
@Service
public interface ApplicationService extends CRUDService<Application> {

    /**
     * Delete the application.
     *
     * @param application Application entity.
     * @return Application.
     * @throws Exception error occurs
     */
    Application softDelete(Application application) throws Exception;

    /**
     * Find all the applications with active status.
     *
     * @param pagingAndSorting pagination and sorting values.
     * @return list of applications with pagination.
     * @throws Exception error occurs
     */
    Page<Application> findAllByActive(PagingAndSorting pagingAndSorting) throws Exception;

    /**
    * Find all the applications with active status.
    *
    * @param isActive application status Active/Inactive
    * @return list of applications with active status
    * @throws Exception error occurs.
    */
   List<Application> findAllByIsActive(Boolean isActive) throws Exception;

}
