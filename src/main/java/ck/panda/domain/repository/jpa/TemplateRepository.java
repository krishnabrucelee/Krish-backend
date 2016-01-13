package ck.panda.domain.repository.jpa;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.Format;
import ck.panda.domain.entity.Template.Status;
import ck.panda.domain.entity.Template.TemplateType;

/**
 * JPA repository for Template entity.
 */
public interface TemplateRepository extends PagingAndSortingRepository<Template, Long> {

    /**
     * Get the template without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param isActive true/false
     * @param status of the template
     * @return list of filtered template
     */
    @Query(value = "select template from Template template where (template.architecture =:architecture OR 'ALL' =:architecture) and template.type <>:type and template.status = :status and template.share IS TRUE AND template.isActive =:isActive")
    List<Template> findByTemplate(@Param("architecture") String architecture, @Param("type") TemplateType type,
            @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param isActive true/false
     * @param status of the template
     * @return list of filtered template
     */
    @Query(value = "select template from Template template where (template.architecture =:architecture OR 'ALL' =:architecture) and template.type <>:type and template.status = :status and (template.share IS TRUE OR template.featured IS TRUE) AND template.isActive =:isActive")
    List<Template> findByTemplateAndFeature(@Param("architecture") String architecture,
            @Param("type") TemplateType type, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template without system type.
     *
     * @param type of template
     * @param format of template
     * @param isActive true/false
     * @param pageable of template
     * @return user and routing template list
     */
    @Query(value = "select template from Template template where template.type <>:type AND template.format <>:format AND template.isActive =:isActive")
    Page<Template> findAllByType(@Param("type") TemplateType type, @Param("format") Format format, Pageable pageable, @Param("isActive") Boolean isActive);
    /**
     * Get the template based on the uuid.
     *
     * @param uuid of the template
     * @param isActive true/false
     * @return template
     */
    @Query(value = "select template from Template template where template.uuid = :uuid AND template.isActive =:isActive")
    Template findByUUID(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Get the template based on the osCategory,architecture and type.
     *
     * @param osCategoryId of the template
     * @param architecture of the template
     * @param isActive true/false
     * @param type of the template
     * @param status of the template
     * @return template
     */
    @Query(value = "select t from Template t where t.osCategoryId=:osCategoryId AND (t.architecture =:architecture OR 'ALL' =:architecture) and t.type <>:type and t.status = :status AND (t.share IS TRUE OR t.featured IS TRUE) AND t.isActive =:isActive")
    List<Template> findAllByOsCategoryAndArchitectureAndType(@Param("osCategoryId") Long osCategoryId,
            @Param("architecture") String architecture, @Param("type") TemplateType type,
            @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template based on the osCategory,architecture and type.
     *
     * @param osCategoryId of the template
     * @param architecture of the template
     * @param type of the template
     * @param isActive true/false
     * @param status of the template
     * @return template
     */
    @Query(value = "select t from Template t where t.osCategoryId=:osCategoryId AND (t.architecture =:architecture OR 'ALL' =:architecture) and t.type <>:type and t.status = :status and t.share IS TRUE AND t.isActive =:isActive")
    List<Template> findAllByOsCategoryAndArchitectureAndTypeAndStatus(@Param("osCategoryId") Long osCategoryId,
            @Param("architecture") String architecture, @Param("type") TemplateType type,
            @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template based on the osCategory,architecture and type.
     *
     * @param osCategory of the template
     * @param type of the template
     * @param status of the template
     * @return template
     */
    @Query(value = "select DISTINCT t from Template t where t.type <>:type AND t.status = :status AND t.osCategory LIKE :osCategory")
    List<Template> findByOsCategoryFilters(@Param("type") TemplateType type, @Param("status") Status status,
            @Param("osCategory") OsCategory osCategory);

    /**
     * Get the template with Format ISO.
     *
     * @param type of the template
     * @param format of template
     * @param pageable of template
     * @param isActive true/false
     * @return user and routing template list
     */
    @Query(value = "select template from Template template where template.type <>:type AND template.format =:format AND template.isActive =:isActive")
    Page<Template> findAllByFormat(@Param("type") TemplateType type, @Param("format") Format format, Pageable pageable, @Param("isActive") Boolean isActive);

    /**
     * Get the template without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param format of template
     * @param isActive true/false
     * @param status of the template
     * @return list of filtered template
     */
    @Query(value = "select template from Template template where (template.architecture =:architecture OR 'ALL' =:architecture) and template.type <>:type and template.status = :status and (template.share IS TRUE OR template.featured IS TRUE) and template.format =:format and template.isActive =:isActive")
    List<Template> findByIsoAndFeature(@Param("architecture") String architecture, @Param("type") TemplateType type, @Param("format") List<Format> format, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param format of template
     * @param isActive true/false
     * @param status of the template
     * @return list of filtered template
     */
    @Query(value = "select template from Template template where (template.architecture =:architecture OR 'ALL' =:architecture) and template.type <>:type and template.status = :status and template.share IS TRUE AND template.isActive =:isActive and template.format =:format")
    List<Template> findByIso(@Param("architecture") String architecture, @Param("type") TemplateType type, @Param("format") List<Format> format, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the iso based on the osCategory,architecture and type.
     *
     * @param osCategoryId of the template
     * @param architecture of the template
     * @param format of template
     * @param isActive true/false
     * @param type of the template
     * @param status of the template
     * @return template
     */
    @Query(value = "select t from Template t where t.osCategoryId=:osCategoryId AND (t.architecture =:architecture OR 'ALL' =:architecture) and t.type <>:type and t.status = :status and (t.share IS TRUE OR t.featured IS TRUE) and t.isActive =:isActive and t.format =:format")
    List<Template> findAllByOsCategoryAndArchitectureAndTypeAndIso(@Param("osCategoryId") Long osCategoryId,
            @Param("architecture") String architecture,
            @Param("type") TemplateType type, @Param("format") List<Format> format, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template based on the osCategory,architecture and type.
     *
     * @param osCategoryId of the template
     * @param architecture of the template
     * @param type of the template
     * @param format of template
     * @param isActive true/false
     * @param status of the template
     * @return template
     */
    @Query(value = "select t from Template t where t.osCategoryId=:osCategoryId AND (t.architecture =:architecture OR 'ALL' =:architecture) and t.type <>:type and t.status = :status and t.share IS TRUE and t.isActive =:isActive and t.format =:format")
    List<Template> findAllByOsCategoryAndArchitectureAndTypeAndStatusAndIso(@Param("osCategoryId") Long osCategoryId,
            @Param("architecture") String architecture,
            @Param("type") TemplateType type, @Param("format") List<Format> format, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template count without system type.
     *
     * @param type of template
     * @param isActive true/false
     * @return template list
     */
    @Query(value = "select template from Template template where template.type <>:type AND template.isActive =:isActive")
    List<Template> findTemplateCounts(@Param("type") TemplateType type, @Param("isActive") Boolean isActive);

}
