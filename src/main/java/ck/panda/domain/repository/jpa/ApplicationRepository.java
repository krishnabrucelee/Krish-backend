package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Application.Status;

/**
 * JPA Repository for Application entity.
 */
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {

    /**
     * Find the application for same domain with application type and is active status.
     *
     * @param type type of the application
     * @param domainId Domain reference
     * @param isActive get the application list based on active/inactive status
     * @param status of the application
     * @return application type
     */
    @Query(value = "SELECT app FROM Application app WHERE app.type=:type AND  app.domainId =:domainId AND "
        + "app.isActive =:isActive AND app.status = :status")
    Application findByTypeAndDomainAndIsActive(@Param("type") String type, @Param("domainId") Long domainId,
            @Param("isActive") Boolean isActive, @Param("status") Status status);

    /**
     * Find all the active or inactive applications with pagination.
     *
     * @param pageable to get the list with pagination
     * @param isActive get the application list based on active/inactive status
     * @return list of applications
     */
    @Query(value = "SELECT app FROM Application app WHERE app.isActive =:isActive")
    Page<Application> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Find all the application with active status.
     *
     * @param isActive get the application list based on active/inactive status
     * @return list of applications
     */
    @Query(value = "SELECT app FROM Application app WHERE app.isActive =:isActive")
    List<Application> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find all the application with active status.
     *
     * @param isActive get the application list based on active/inactive status
     * @param pageable to get the list with pagination
     * @param domainId of the application
     * @return list of applications
     */
    @Query(value = "SELECT app FROM Application app WHERE app.domainId=:domainId AND app.isActive =:isActive")
    Page<Application> findAllByDomainIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive,
            Pageable pageable);

    /**
     * Find all the application with active status.
     *
     * @param isActive get the application list based on active/inactive status
     * @param domainId of the application
     * @return list of applications
     */
    @Query(value = "SELECT app FROM Application app WHERE app.domainId=:domainId AND app.isActive =:isActive")
    List<Application> findAllByDomainIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

    /**
     * Find all the application with active status.
     *
     * @param domainId of the application
     * @param isActive get the application list based on active/inactive status
     * @param status of the application
     * @return list of applications
     */
    @Query(value = "SELECT app FROM Application app WHERE app.domainId=:domainId AND app.isActive =:isActive AND "
        + "app.status = :status")
    List<Application> findAllByDomainAndIsActiveAndStatus(@Param("domainId") Long domainId,
            @Param("isActive") Boolean isActive, @Param("status") Status status);

    /**
     * Find all the domain based active or inactive applications with pagination.
     *
     * @param domainId domain id of the application
     * @param isActive get the application list based on active/inactive status
     * @param pageable to get the list with pagination
     * @return list of applications
     */
    @Query(value = "SELECT app FROM Application app WHERE app.domainId =:domainId AND app.isActive =:isActive")
    Page<Application> findAllByDomainIdAndIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);
}
