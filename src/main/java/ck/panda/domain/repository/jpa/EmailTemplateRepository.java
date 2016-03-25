package ck.panda.domain.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import ck.panda.domain.entity.ComputeOffering;
import ck.panda.domain.entity.EmailTemplate;

/**
 * Jpa Repository for Email template entity.
 *
 */
@Service
public interface EmailTemplateRepository extends PagingAndSortingRepository<EmailTemplate, Long> {

     /**
     * Find all by is Active in Email Template.
     *
     * @param isActive status of the template..
     * @return email template.
     */
    @Query(value = "SELECT email FROM EmailTemplate email WHERE email.isActive = :isActive")
    List<EmailTemplate> findAllByActive(@Param("isActive") Boolean isActive);

    /**
     * Find template by isactive status and language.
     *
     * @param isActive status of the template.
     * @param language of the template.
     * @return email template.
     */
    @Query(value = "SELECT email FROM EmailTemplate email WHERE email.isActive = :isActive AND email.englishLanguage =:language OR email.chineseLanguage =:language")
    EmailTemplate findNameAndIsActive(@Param("isActive") Boolean isActive,@Param("language")String language);
}
