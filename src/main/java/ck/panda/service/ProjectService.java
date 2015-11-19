package ck.panda.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.Project;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for Project. This service provides basic crud functions of projects.
 */
@Service
public interface ProjectService extends CRUDService<Project> {
    /**
     * Method to soft delete project.
     *
     * @param project project object.
     * @return project.
     * @throws Exception if error occurs.
     */
    Project softDelete(Project project) throws Exception;

    /**
     * Find all the projects with active status.
     *
     * @param isActive true/false.
     * @param pagingAndSorting paging and sorting information.
     * @return list of project.
     * @throws Exception if error occurs.
     */
    Page<Project> findAllByActive(Boolean isActive, PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * Find all the projects with active status.
     *
     * @param isActive true/false.
     * @return list of active project.
     * @throws Exception if error occurs.
     */
    List<Project> findAllByActive(Boolean isActive) throws Exception;

    /**
     * Find all the projects with Domain.
     *
     * @param id domain id .
     * @return list of domains in project.
     * @throws Exception if error occurs.
     */
    List<Project> findbyDomain(Long id);

    /**
     * Find all the projects from cloud stack server.
     *
     * @return list of active project.
     * @throws Exception if error occurs.
     */
    List<Project> findAllFromCSServer() throws Exception;

}
