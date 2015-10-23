package ck.panda.domain.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Application;

/**
 * JPA Repository for Application entity.
 */
public interface ApplicationRepository extends PagingAndSortingRepository<Application, Long> {

    /**
     * Method to find list of entities having active status.
     *
     * @param pageable of the application
     * @return a page of entities
     */
    @Query(value = "select app from Application app where app.isActive IS TRUE")
    Page<Application> findAllByActive(Pageable pageable);

    /**
     * Method to find type of the application.
     *
     * @param type of the application
     * @return application type
     */
    @Query(value = "select app from Application app where app.isActive IS TRUE AND app.type=:type")
    Application findByType(@Param("type") String type);
}
