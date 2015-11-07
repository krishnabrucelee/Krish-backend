package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.Template;
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
    @Query(value = "select template from Template template where template.type != :type")
    List<Template> findByTemplate(@Param("type") Type type);
}
