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
     * Soft delete for project.
     *
     * @param project project object.
     * @return project.
     * @throws Exception if error occurs.
     */
    Project softDelete(Project project) throws Exception;

    /**
     * Method to remove user from project.
     *
     * @param project project object.
     * @return project.
     * @throws Exception if error occurs.
     */
    Project removeUser(Project project) throws Exception;

    /**
     * Find all project by active status.
     *
     * @param isActive true/false.
     * @param pagingAndSorting paging and sorting information.
     * @param userId user id.
     * @return list of project.
     * @throws Exception if error occurs.
     */
    Page<Project> findAllByActive(Boolean isActive, PagingAndSorting pagingAndSorting, Long userId) throws Exception;

    /**
     * Find all project by active status.
     *
     * @param isActive true/false.
     * @return list of active project.
     * @throws Exception if error occurs.
     */
    List<Project> findAllByActive(Boolean isActive) throws Exception;

    /**
     * Find all project by Domain.
     *
     * @param id domain id .
     * @return list of domains in project.
     * @throws Exception if error occurs.
     */
    List<Project> findAllByDomain(Long id);

    /**
     * Find project by Uuid.
     *
     * @param uuid of the project.
     * @return project.
     * @throws Exception if error occurs.
     */
    Project findByUuid(String uuid) throws Exception;

    /**
     * Get all project from cloud stack server.
     *
     * @return list of active project.
     * @throws Exception if error occurs.
     */
    List<Project> findAllFromCSServerByDomain() throws Exception;

    /**
     * Find all project by department.
     *
     * @param id department id .
     * @param isActive department status Active/Inactive
     * @return list of departments in project.
     * @throws Exception if error occurs.
     */
    List<Project> findAllByDepartmentAndIsActive(Long id, Boolean isActive) throws Exception;

    /**
     * Find all project by user.
     *
     * @param id department id .
     * @param isActive department status Active/Inactive
     * @return list of departments in project.
     * @throws Exception if error occurs.
     */
    List<Project> findAllByUserAndIsActive(Long id, Boolean isActive) throws Exception;

    /**
     * Find all project.
     *
     * @param userId user id.
     * @throws Exception if error occurs.
     * @return project.
     */
    List<Project> getAllProjects(Long userId) throws Exception;

    /**
     * Find all project by domain.
     *
     * @param domainId domain id of the project
     * @param pagingAndSorting paging and sorting information.
     * @return list of project.
     * @throws Exception if error occurs.
     */
    Page<Project> findAllByDomainId(Long domainId, PagingAndSorting pagingAndSorting) throws Exception;
}
