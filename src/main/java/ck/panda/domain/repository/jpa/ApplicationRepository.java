package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Application;
import ck.panda.domain.entity.Application.Status;
import ck.panda.domain.entity.Domain;

/**
 * JPA Repository for Application entity.
 */
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {

    /**
     * Find the application for same domain with application type and is active status.
     *
     * @param type type of the application.
     * @param domain Domain reference.
     * @param isActive get the application list based on active/inactive status.
     * @param status of the application.
     * @return application type.
     */
    @Query(value = "select app from Application app where app.type=:type AND  app.domain =:domain AND app.isActive =:isActive and app.status = :status")
    Application findByTypeAndDomainAndIsActive(@Param("type") String type, @Param("domain") Domain domain, @Param("isActive")  Boolean isActive, @Param("status") Status status);

    /**
     * Find all the active or inactive applications with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the application list based on active/inactive status.
     * @param status of the application.
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.isActive =:isActive and app.status = :status")
    Page<Application> findAllByIsActiveAndStatus(Pageable pageable, @Param("isActive") Boolean isActive, @Param("status") Status status);

    /**
     * Find all the active or inactive applications with pagination.
     *
     * @param pageable to get the list with pagination.
     * @param isActive get the application list based on active/inactive status.
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.isActive =:isActive")
    Page<Application> findAllByIsActive(Pageable pageable, @Param("isActive") Boolean isActive);


    /**
     * Find all the application with active status.
     *
     * @param isActive get the application list based on active/inactive status.
     * @param status of the application.
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.isActive =:isActive and app.status = :status")
    List<Application> findAllByIsActiveAndStatus(@Param("isActive") Boolean isActive, @Param("status") Status status);

    /**
     * Find all the application with active status.
     *
     * @param isActive get the application list based on active/inactive status.
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.isActive =:isActive")
    List<Application> findAllByIsActive(@Param("isActive") Boolean isActive);

    /**
     * Find all the application with active status.
     *
     * @param isActive get the application list based on active/inactive status.
     * @param pageable to get the list with pagination.
     * @param domainId of the application.
     * @param status of the application.
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.domainId=:domainId and app.isActive =:isActive and app.status = :status")
    Page<Application> findAllByDomainIsActiveAndStatus(@Param("domainId") Long domainId, Pageable pageable, @Param("isActive") Boolean isActive, @Param("status") Status status);

    /**
     * Find all the application with active status.
     *
     * @param isActive get the application list based on active/inactive status.
     * @param pageable to get the list with pagination.
     * @param domainId of the application.
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.domainId=:domainId and app.isActive =:isActive")
    Page<Application> findAllByDomainIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive, Pageable pageable);

    /**
     * Find all the application with active status.
     *
     * @param isActive get the application list based on active/inactive status.
     * @param domainId of the application.
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.domainId=:domainId and app.isActive =:isActive")
    List<Application> findAllByDomainIsActive(@Param("domainId") Long domainId, @Param("isActive") Boolean isActive);

    /**
     * Find all the application with active status.
     *
     * @param id domain id.
     * @param isActive get the application list based on active/inactive status.
     * @param status of the application.
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.domainId=:domainId and app.isActive =:isActive and app.status = :status")
    List<Application> findAllByIsActiveAndDomainAndStatus(@Param("domainId") Long id, @Param("isActive") Boolean isActive, @Param("status") Status status);

}
