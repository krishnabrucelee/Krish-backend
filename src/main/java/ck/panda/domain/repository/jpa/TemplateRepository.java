package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.Status;
import ck.panda.domain.entity.Template.Type;

/**
 * JPA repository for Template entity.
 */
public interface TemplateRepository extends PagingAndSortingRepository<Template, Long> {

    /**
     * Get the template without system type.
     *
     * @param type of template
     * @return user and routing template list
     */
    @Query(value = "select template from Template template where (template.architecture =:architecture OR 'ALL' =:architecture) and template.type != :type and template.status = :status")
    List<Template> findByTemplate(@Param("architecture") String architecture, @Param("type") Type type, @Param("status") Status status);

    /**
     * Get the template without system type.
     *
     * @param type of template
     * @return user and routing template list
     */
    @Query(value = "select template from Template template where template.type != :type")
    Page<Template> findAllWithoutSystem(@Param("type") Type type, Pageable pageable);

    /**
     * Get the template based on the uuid.
     *
     * @param uuid of the template
     * @return template
     */
    @Query(value = "select template from Template template where template.uuid = :uuid")
    Template findByUUID(@Param("uuid") String uuid);

    /**
     * Get the template based on the osCategory,architecture and osVersion.
     *
     * @param osCategory of the template
     * @param architecture of the template
     * @param osVersion of the template
     * @return template
     */
    @Query(value = "select t from Template t where (t.osCategory=:osCategory OR 'ALL'=:osCategory) AND (t.architecture =:architecture OR 'ALL' =:architecture) and t.type != :type and t.status = :status")
    List<Template> findByFilters(@Param("osCategory") OsCategory osCategory, @Param("architecture") String architecture, @Param("type") Type type, @Param("status") Status status);

}
