package ck.panda.domain.repository.jpa;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.EmailTemplate;

/**
 * Jpa Repository for Email template entity.
 *
 */
@Service
public interface EmailTemplateRepository extends PagingAndSortingRepository<EmailTemplate, Long> {

}
