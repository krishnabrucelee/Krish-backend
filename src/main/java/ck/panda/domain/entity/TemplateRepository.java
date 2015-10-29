package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import ck.panda.domain.entity.Template;

/**
 * JPA repository for Template entity.
 */
public interface TemplateRepository extends PagingAndSortingRepository<Template, Long> {

}
