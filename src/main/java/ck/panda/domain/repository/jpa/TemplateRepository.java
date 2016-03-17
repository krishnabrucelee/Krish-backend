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
import ck.panda.domain.entity.User.UserType;

/**
 * JPA repository for Template entity.
 */
public interface TemplateRepository extends PagingAndSortingRepository<Template, Long> {

    /**
     * Get the template by architecture, status and without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param status of the template
     * @param isActive true/false
     * @return list of filtered template
     */
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive =:isActive AND template.domainId = :domainId")
    List<Template> findByTemplateAndDomainId(@Param("architecture") String architecture, @Param("type") TemplateType type,
        @Param("status") Status status, @Param("isActive") Boolean isActive, @Param("domainId") Long domainId);

    /**
     * Get the template by architecture, status and without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param status of the template
     * @param isActive true/false
     * @return list of filtered template
     */
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive =:isActive")
    List<Template> findByTemplate(@Param("architecture") String architecture, @Param("type") TemplateType type,
        @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template by architecture, status and without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param status of the template
     * @param isActive true/false
     * @return list of filtered template
     */
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND (template.share IS TRUE OR template.featured IS TRUE) AND template.isActive =:isActive")
    List<Template> findByTemplateAndFeature(@Param("architecture") String architecture, @Param("type") TemplateType type,
        @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template by format, status and without system type.
     *
     * @param type of template
     * @param format of template
     * @param pageable of template
     * @param isActive true/false
     * @return user and routing template list
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType LEFT JOIN template.templateCost WHERE template.type <>:type AND template.format <>:format AND template.isActive =:isActive")
    Page<Template> findAllByType(@Param("type") TemplateType type, @Param("format") Format format, Pageable pageable,
        @Param("isActive") Boolean isActive);
    /**
     * Get the template based on the uuid and status.
     *
     * @param uuid of the template
     * @param isActive true/false
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.uuid = :uuid AND template.isActive =:isActive")
    Template findByUUID(@Param("uuid") String uuid, @Param("isActive") Boolean isActive);

    /**
     * Get the template based on the osCategory, architecture, status and without system type.
     *
     * @param osCategoryId of the template
     * @param architecture of the template
     * @param type of the template
     * @param status of the template
     * @param isActive true/false
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.osCategoryId=:osCategoryId AND (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND (template.share IS TRUE OR template.featured IS TRUE) AND template.isActive =:isActive")
    List<Template> findAllByOsCategoryAndArchitectureAndType(@Param("osCategoryId") Long osCategoryId, @Param("architecture") String architecture,
        @Param("type") TemplateType type, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template based on the osCategory, architecture, status and without system type.
     *
     * @param osCategoryId of the template
     * @param architecture of the template
     * @param type of the template
     * @param status of the template
     * @param isActive true/false
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.osCategoryId=:osCategoryId AND (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive =:isActive")
    List<Template> findAllByOsCategoryAndArchitectureAndTypeAndStatus(@Param("osCategoryId") Long osCategoryId,
        @Param("architecture") String architecture, @Param("type") TemplateType type, @Param("status") Status status,
        @Param("isActive") Boolean isActive);

    /**
     * Get the template based on the status, OS category, format and without system type.
     *
     * @param type of the template
     * @param status of the template
     * @param osCategory of the template
     * @param format of the template
     * @return template
     */
    @Query(value = "SELECT DISTINCT template FROM Template template WHERE template.type <>:type AND template.status = :status AND template.osCategory LIKE :osCategory AND template.format <>:format")
    List<Template> findByTemplateWithIsoCategory(@Param("type") TemplateType type, @Param("status") Status status,
        @Param("osCategory") OsCategory osCategory, @Param("format") Format format);

    /**
     * Get the template based on the status, OS category, format and without system type.
     *
     * @param type of the template
     * @param status of the template
     * @param osCategory of the template
     * @param format of the template
     * @return template
     */
    @Query(value = "SELECT DISTINCT template FROM Template template WHERE template.type <>:type AND template.status = :status AND template.osCategory LIKE :osCategory AND template.format =:format")
    List<Template> findByTemplateWithoutIsoCategory(@Param("type") TemplateType type, @Param("status") Status status,
        @Param("osCategory") OsCategory osCategory, @Param("format") Format format);

    /**
     * Get the template based on format, status and without system type.
     *
     * @param type of the template
     * @param format of template
     * @param pageable of template
     * @param isActive true/false
     * @return user and routing template list
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType LEFT JOIN template.templateCost WHERE template.type <>:type AND template.format =:format AND template.isActive =:isActive")
    Page<Template> findAllByFormat(@Param("type") TemplateType type, @Param("format") Format format, Pageable pageable,
        @Param("isActive") Boolean isActive);

    /**
     * Get the template based on architecture, format, status and without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param format of template
     * @param status of the template
     * @param isActive true/false
     * @return list of filtered template
     */
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND (template.share IS TRUE OR template.featured IS TRUE) AND template.format =:format AND template.isActive =:isActive")
    List<Template> findByIsoAndFeature(@Param("architecture") String architecture, @Param("type") TemplateType type,
        @Param("format") List<Format> format, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template based on architecture, format, status and without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param format of template
     * @param status of the template
     * @param isActive true/false
     * @return list of filtered template
     */
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive =:isActive AND template.format =:format")
    List<Template> findByIso(@Param("architecture") String architecture, @Param("type") TemplateType type, @Param("format") List<Format> format,
        @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the ISO based on the osCategory, architecture, format, status and without system type.
     *
     * @param osCategoryId of the template
     * @param architecture of the template
     * @param type of the template
     * @param format of template
     * @param status of the template
     * @param isActive true/false
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.osCategoryId=:osCategoryId AND (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND (template.share IS TRUE OR template.featured IS TRUE) AND template.isActive =:isActive AND template.format =:format")
    List<Template> findAllByOsCategoryAndArchitectureAndTypeAndIso(@Param("osCategoryId") Long osCategoryId,
        @Param("architecture") String architecture, @Param("type") TemplateType type, @Param("format") List<Format> format,
        @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template based on the osCategory, architecture, format, status and without system type.
     *
     * @param osCategoryId of the template
     * @param architecture of the template
     * @param type of the template
     * @param format of template
     * @param status of the template
     * @param isActive true/false
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.osCategoryId=:osCategoryId AND (template.architecture =:architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive =:isActive AND template.format =:format")
    List<Template> findAllByOsCategoryAndArchitectureAndTypeAndStatusAndIso(@Param("osCategoryId") Long osCategoryId,
        @Param("architecture") String architecture, @Param("type") TemplateType type, @Param("format") List<Format> format,
        @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template count without system type.
     *
     * @param type of template
     * @param isActive true/false
     * @return template list
     */
    @Query(value = "SELECT template FROM Template template WHERE template.type <>:type AND template.isActive =:isActive")
    List<Template> findTemplateCounts(@Param("type") TemplateType type, @Param("isActive") Boolean isActive);

    /**
     * Get the template by the type featured.
     *
     * @param type featured
     * @param pageable page
     * @param featured template type
     * @param share public type
     * @param isActive status
     * @param status status of the template
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType LEFT JOIN template.templateCost WHERE template.type <>:type AND template.featured =:featured AND template.share =:share AND template.status =:status AND template.isActive =:isActive")
    Page<Template> findTemplateByFeatured(@Param("type") TemplateType type, Pageable pageable, @Param("featured") Boolean featured, @Param("share") Boolean share, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template by the type community.
     *
     * @param type community
     * @param pageable page
     * @param share public type
     * @param isActive status
     * @param status status of the template
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType LEFT JOIN template.templateCost WHERE template.type <>:type AND template.share =:share AND template.status =:status AND template.isActive =:isActive")
    Page<Template> findTemplateByCommunity(@Param("type") TemplateType type, Pageable pageable, @Param("share") Boolean share, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Get the template by user id.
     *
     * @param type template type
     * @param pageable page
     * @param userId of the template
     * @param isActive status
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType LEFT JOIN template.templateCost WHERE template.type <>:type AND template.templateOwnerId =:userId AND template.isActive =:isActive")
    Page<Template> findTemplateByUserId(@Param("type") TemplateType type, Pageable pageable, @Param("userId") Long userId, @Param("isActive") Boolean isActive);

    /**
     * Get the template by template type and isActive.
     *
     * @param type template type
     * @param isActive status
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.type <>:type AND template.isActive =:isActive")
    List<Template> findAllTemplatesByIsActiveAndType(@Param("type") TemplateType type, @Param("isActive") Boolean isActive);

    /**
     * Find all templates by user type, isActive status of the template.
     *
     * @param architecture 64 or 32 bit
     * @param type of the user
     * @param status of the template.
     * @param isActive status of th template
     * @param rootAdmin type filtering.
     * @param domainId of the user.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.templateOwner user WHERE user.type =:rootAdmin OR template.architecture =:architecture OR 'ALL' =:architecture AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive =:isActive AND template.domainId = :domainId")
    List<Template> findByTemplateAndUserType(@Param("architecture") String architecture, @Param("type") TemplateType type,
        @Param("status") Status status, @Param("isActive") Boolean isActive,@Param("rootAdmin")UserType rootAdmin, @Param("domainId") Long domainId);

}
