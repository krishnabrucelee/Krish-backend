package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Application;
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
     * @return application type.
     */
    @Query(value = "select app from Application app where app.type=:type AND  app.domain =:domain AND app.isActive =:isActive")
    Application findByTypeAndDomainAndIsActive(@Param("type") String type, @Param("domain") Domain domain, @Param("isActive")  Boolean isActive);

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
     * @return list of applications.
     */
    @Query(value = "select app from Application app where app.isActive =:isActive")
    List<Application> findAllByIsActive(@Param("isActive") Boolean isActive);
}
