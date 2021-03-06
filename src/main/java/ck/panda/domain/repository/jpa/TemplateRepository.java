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
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture = :architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive = :isActive")
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
    @Query(value = "SELECT template FROM Template template WHERE ((template.architecture = :architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive = :isActive) AND (template.domainId = :domainId OR template.templateOwnerId = :userId)")
    List<Template> findByTemplateAndUserId(@Param("architecture") String architecture, @Param("type") TemplateType type,
        @Param("status") Status status, @Param("isActive") Boolean isActive, @Param("domainId") Long domainId, @Param("userId") Long userId);


    /**
     * Get the template by architecture, status and without system type.
     *
     * @param architecture of the template
     * @param type of the template
     * @param status of the template
     * @param isActive true/false
     * @return list of filtered template
     */
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture = :architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.featured IS TRUE OR ( template.share IS TRUE AND template.featured IS FALSE) AND template.isActive = :isActive")
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
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType WHERE template.type <>:type AND template.format <>:format AND template.isActive = :isActive")
    Page<Template> findAllByType(@Param("type") TemplateType type, @Param("format") Format format, Pageable pageable,
        @Param("isActive") Boolean isActive);


    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType LEFT JOIN template.zone WHERE template.type <>:type AND template.format <>:format AND template.isActive = :isActive AND (template.name LIKE %:search% OR template.osType.description LIKE %:search% "
            + "OR template.zone.name LIKE %:search% OR template.hypervisor.name LIKE %:search% OR template.status LIKE %:search% )")
    Page<Template> findAllByTypeAndSearchText(@Param("type") TemplateType type, @Param("format") Format format, Pageable pageable, @Param("isActive") Boolean isActive, @Param("search") String searchText);

    /**
     * Get the template based on the uuid and status.
     *
     * @param uuid of the template
     * @param isActive true/false
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.uuid = :uuid ")
    Template findByUUID(@Param("uuid") String uuid);

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
    @Query(value = "SELECT template FROM Template template WHERE template.osCategoryId = :osCategoryId AND "
            + "(template.architecture = :architecture OR 'ALL' = :architecture) AND template.type <>:type AND "
            + "template.status = :status AND template.share IS TRUE AND template.isActive = :isActive")
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
    @Query(value = "SELECT template FROM Template template WHERE (template.osCategoryId = :osCategoryId AND (template.architecture = :architecture OR 'ALL' = :architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive = :isActive) AND (template.domainId = :domainId OR template.templateOwnerId = :userId)")
    List<Template> findAllByOsCategoryAndArchitectureAndTypeAndStatus(@Param("osCategoryId") Long osCategoryId,
        @Param("architecture") String architecture, @Param("type") TemplateType type, @Param("status") Status status,
        @Param("isActive") Boolean isActive,@Param("domainId") Long domainId, @Param("userId") Long userId);

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
    @Query(value = "SELECT DISTINCT template FROM Template template WHERE template.type <>:type AND template.status = :status AND template.osCategory LIKE :osCategory AND template.format = :format")
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
     * Get the template based on format, status and without system type.
     *
     * @param type of the template
     * @param format of template
     * @param pageable of template
     * @param isActive true/false
     * @return user and routing template list
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType LEFT JOIN template.zone WHERE template.type <>:type AND template.format =:format AND template.isActive = :isActive AND (template.name LIKE %:search% OR template.osType.description LIKE %:search% "
            + "OR template.zone.name LIKE %:search% OR template.hypervisor.name LIKE %:search% OR template.status LIKE %:search%)")
    Page<Template> findAllByFormatAndSearchText(@Param("type") TemplateType type, @Param("format") Format format, Pageable pageable,
        @Param("isActive") Boolean isActive,@Param("search") String searchText);

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
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture =:architecture OR 'ALL' = :architecture) AND template.type <>:type AND template.status = :status AND (template.share IS TRUE OR template.featured IS TRUE) AND template.format = :format AND template.isActive = :isActive")
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
    @Query(value = "SELECT template FROM Template template WHERE (template.architecture =:architecture OR 'ALL' = :architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive =:isActive AND template.format = :format")
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
    @Query(value = "SELECT template FROM Template template WHERE template.osCategoryId = :osCategoryId AND (template.architecture = :architecture OR 'ALL' =:architecture) AND template.type <>:type AND template.status = :status AND (template.share IS TRUE OR template.featured IS TRUE) AND template.isActive = :isActive AND template.format = :format")
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
    @Query(value = "SELECT template FROM Template template WHERE template.osCategoryId = :osCategoryId AND (template.architecture = :architecture OR 'ALL' = :architecture) AND template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive =:isActive AND template.format =:format")
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
    @Query(value = "SELECT template FROM Template template WHERE template.type <>:type AND template.isActive = :isActive")
    List<Template> findTemplateCounts(@Param("type") TemplateType type, @Param("isActive") Boolean isActive);

    /**
     * Get the template count without system type and search text.
     *
     * @param type of template
     * @param isActive true/false
     * @return template list
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType LEFT JOIN template.zone WHERE template.type <>:type AND template.isActive = :isActive AND (template.name LIKE %:search% OR template.osType.description LIKE %:search% "
            + "OR template.zone.name LIKE %:search% OR template.hypervisor.name LIKE %:search% OR template.status LIKE %:search% )")
    List<Template> findTemplateCountsAndSearchText(@Param("type") TemplateType type, @Param("isActive") Boolean isActive,@Param("search") String search);


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
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE (template.type <>:type AND template.featured = :featured AND template.share = :share AND template.isActive = :isActive) AND (template.domainId = :domainId OR template.templateOwnerId = :rootUser)")
    Page<Template> findTemplateByFeatured(@Param("type") TemplateType type, Pageable pageable, @Param("featured") Boolean featured, @Param("share") Boolean share,@Param("isActive") Boolean isActive,@Param("domainId") Long domainId, @Param("rootUser") Long rootUser);

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
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE (template.type <>:type AND template.featured = :featured AND template.share = :share AND template.isActive = :isActive) AND (template.domainId = :domainId OR template.templateOwnerId = :rootUser) AND (template.name LIKE %:search% OR ROUND((template.size / POWER(2, 30)),2) LIKE %:search%  OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    Page<Template> findTemplateByFeaturedAndSearchText(@Param("type") TemplateType type, Pageable pageable, @Param("featured") Boolean featured, @Param("share") Boolean share,@Param("isActive") Boolean isActive,@Param("domainId") Long domainId, @Param("rootUser") Long rootUser,@Param("search") String search);


    /**
     * Get all the featured template.
     *
     * @param type of the template feature or shared.
     * @param pageable for paging or sorting.
     * @param featured type of the template.
     * @param share status of the template.
     * @param isActive status of the template.
     * @return template.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.featured =:featured AND template.share =:share AND template.isActive =:isActive)")
    Page<Template> findAllTemplateByFeatured(@Param("type") TemplateType type, Pageable pageable, @Param("featured") Boolean featured, @Param("share") Boolean share,@Param("isActive") Boolean isActive);

    /**
     * Get all the featured template.
     *
     * @param type of the template feature or shared.
     * @param pageable for paging or sorting.
     * @param featured type of the template.
     * @param share status of the template.
     * @param isActive status of the template.
     * @return template.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.featured = :featured AND template.share = :share AND template.isActive = :isActive AND (template.name LIKE %:search% OR ROUND((template.size / POWER(2, 30)),2) LIKE %:search% OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%))")
    Page<Template> findAllTemplateByFeaturedAndSearchText(@Param("type") TemplateType type, Pageable pageable, @Param("featured") Boolean featured, @Param("share") Boolean share,@Param("isActive") Boolean isActive,@Param("search") String search);

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
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE (template.type <>:type AND template.share =:share AND template.isActive =:isActive) AND (template.domainId =:domainId OR template.templateOwnerId =:rootUser)")
    Page<Template> findTemplateByCommunity(@Param("type") TemplateType type, Pageable pageable, @Param("share") Boolean share,@Param("isActive") Boolean isActive,@Param("domainId") Long domainId,@Param("rootUser") Long rootUser);


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
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE (template.type <>:type AND template.share =:share AND template.isActive =:isActive) AND (template.domainId =:domainId OR template.templateOwnerId =:rootUser) AND (template.name LIKE %:search% OR ROUND((template.size / POWER(2, 30)),2) LIKE %:search%  OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    Page<Template> findTemplateByCommunityAndSearchText(@Param("type") TemplateType type, Pageable pageable, @Param("share") Boolean share,@Param("isActive") Boolean isActive,@Param("domainId") Long domainId,@Param("rootUser") Long rootUser,@Param("search") String search);

    /**
     * Get all the templates in community.
     *
     * @param type of the templates.
     * @param pageable for pagination.
     * @param share type of the template.
     * @param isActive status of the template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.share = :share AND template.isActive = :isActive")
    Page<Template> findAllTemplateByCommunity(@Param("type") TemplateType type, Pageable pageable, @Param("share") Boolean share,@Param("isActive") Boolean isActive);

    /**
     * Get all the templates in community.
     *
     * @param type of the templates.
     * @param pageable for pagination.
     * @param share type of the template.
     * @param isActive status of the template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.share = :share AND template.isActive = :isActive AND (template.name LIKE %:search% OR  ROUND((template.size / POWER(2, 30)),2) LIKE %:search%  OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    Page<Template> findAllTemplateByCommunityAndSearchText(@Param("type") TemplateType type, Pageable pageable, @Param("share") Boolean share,@Param("isActive") Boolean isActive,@Param("search") String search);


    /**
     * Get the template by user id.
     *
     * @param type template type
     * @param pageable page
     * @param userId of the template
     * @param isActive status
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType WHERE template.type <>:type AND (template.templateOwnerId = NULL AND template.departmentId = :departmentId) OR (template.templateOwnerId != NULL AND template.templateOwnerId = :userId) AND template.isActive = :isActive")
    Page<Template> findTemplateByUserId(@Param("type") TemplateType type, Pageable pageable, @Param("userId") Long userId,@Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Get the template by user id.
     *
     * @param type template type
     * @param pageable page
     * @param userId of the template
     * @param isActive status
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType WHERE template.type <>:type AND (template.templateOwnerId = NULL AND template.departmentId =:departmentId) OR (template.templateOwnerId != NULL AND template.templateOwnerId =:userId) AND template.isActive =:isActive AND (template.name LIKE %:search% OR ROUND((template.size / POWER(2, 30)),2) LIKE %:search% OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    Page<Template> findTemplateByUserIdAndSearchText(@Param("type") TemplateType type, Pageable pageable, @Param("userId") Long userId,@Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive,@Param("search") String search);

    /**
     * Get the template by template type and isActive.
     *
     * @param type template type
     * @param isActive status
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.type <>:type AND template.isActive = :isActive")
    List<Template> findAllTemplatesByIsActiveAndType(@Param("type") TemplateType type, @Param("isActive") Boolean isActive);

    /**
     * Get the template by template type and isActive.
     *
     * @param type template type
     * @param isActive status
     * @return template
     */
    @Query(value = "SELECT template FROM Template template WHERE template.type <>:type AND template.isActive = :isActive AND (template.name LIKE %:search% OR template.size LIKE %:search% OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    List<Template> findAllTemplatesByIsActiveAndTypeAndSearchText(@Param("type") TemplateType type, @Param("isActive") Boolean isActive, @Param("search") String search);


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
    List<Template> findAllTemplateByDomainIdUserTypeAndIsActiveStatus(@Param("architecture") String architecture, @Param("type") TemplateType type,
        @Param("status") Status status, @Param("isActive") Boolean isActive,@Param("rootAdmin")UserType rootAdmin, @Param("domainId") Long domainId);


    /**
     * Find all by domain, status and shared.
     * @param type system, builtin etc.
     * @param status active, inactive
     * @param isActive true or false
     * @param domainId domain id.
     * @return list of templates.
     */
    @Query(value = "SELECT template FROM Template template WHERE template.type <>:type "
            + "AND template.share =:share "
            + "AND template.isActive =:isActive "
            + "AND template.domainId = :domainId")
    List<Template> findAllByDomainIdIsActiveAndShare(@Param("type") TemplateType type,
        @Param("share") Boolean share, @Param("isActive") Boolean isActive, @Param("domainId") Long domainId);

    /**
     * Get the template by the type community.
     *
     * @param type community
     * @param share public type
     * @param isActive status
     * @param status status of the template
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.share =:share AND template.status =:status AND template.isActive =:isActive")
    List<Template> findAllByCommunity(@Param("type") TemplateType type, @Param("share") Boolean share, @Param("status") Status status, @Param("isActive") Boolean isActive);

    /**
     * Find all the template by user id.
     *
     * @param type template type
     * @param userId of the template
     * @param isActive status
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType WHERE template.type <>:type AND (template.templateOwnerId = NULL AND template.departmentId =:departmentId) OR (template.templateOwnerId != NULL AND template.templateOwnerId =:userId) AND template.isActive =:isActive")
    List<Template> findAllByUserId(@Param("type") TemplateType type, @Param("userId") Long userId,@Param("departmentId") Long departmentId, @Param("isActive") Boolean isActive);

    /**
     * Get the template by featured type and domain id of the template.
     *
     * @param type of the template.
     * @param featured template.
     * @param share type of the template.
     * @param isActive status of the template.
     * @param domainId of the user template.
     * @param rootUser template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE (template.type <>:type AND template.featured =:featured AND template.share =:share AND template.isActive =:isActive) AND (template.domainId =:domainId OR template.templateOwnerId =:rootUser)")
    List<Template> listTemplateByFeaturedAndDomainId(@Param("type") TemplateType type,  @Param("featured") Boolean featured, @Param("share") Boolean share,@Param("isActive") Boolean isActive,@Param("domainId") Long domainId, @Param("rootUser") Long rootUser);

    /**
     * Get the template by featured type and domain id of the template.
     *
     * @param type of the template.
     * @param featured template.
     * @param share type of the template.
     * @param isActive status of the template.
     * @param domainId of the user template.
     * @param rootUser template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE (template.type <>:type AND template.featured =:featured AND template.share =:share AND template.isActive =:isActive) AND (template.domainId =:domainId OR template.templateOwnerId =:rootUser) AND (template.name LIKE %:search% OR template.size LIKE %:search% OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    List<Template> listTemplateByFeaturedAndDomainIdAndSearchText(@Param("type") TemplateType type,  @Param("featured") Boolean featured, @Param("share") Boolean share,@Param("isActive") Boolean isActive,@Param("domainId") Long domainId, @Param("rootUser") Long rootUser, @Param("search") String search);


    /**
     * Get all the templates by feature type.
     *
     * @param type of the template
     * @param featured type of the template.
     * @param share type of the template.
     * @param isActive status of the template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.featured = :featured AND template.share = :share AND template.isActive = :isActive)")
    List<Template> listAllTemplateByFeatured(@Param("type") TemplateType type, @Param("featured") Boolean featured, @Param("share") Boolean share, @Param("isActive") Boolean isActive);

    /**
     * Get all the templates by feature type.
     *
     * @param type of the template
     * @param featured type of the template.
     * @param share type of the template.
     * @param isActive status of the template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.featured = :featured AND template.share = :share AND template.isActive = :isActive AND (template.name LIKE %:search% OR template.size LIKE %:search% OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    List<Template> listAllTemplateByFeaturedAndSearchText(@Param("type") TemplateType type, @Param("featured") Boolean featured, @Param("share") Boolean share, @Param("isActive") Boolean isActive ,@Param("search") String search);


    /**
     * Get the template by the type community.
     *
     * @param type community
     * @param pageable page
     * @param share public type
     * @param isActive status
     * @param status status of the template
     * @param domainId of the user template.
     * @param rootUser user id of the user template.
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE (template.type <>:type AND template.share = :share AND template.isActive = :isActive) AND (template.domainId = :domainId OR template.templateOwnerId = :rootUser) ")
    List<Template> listTemplateByCommunity(@Param("type") TemplateType type, @Param("share") Boolean share,@Param("isActive") Boolean isActive, @Param("domainId") Long domainId, @Param("rootUser") Long rootUser);

    /**
     * Get the template by the type community.
     *
     * @param type community
     * @param pageable page
     * @param share public type
     * @param isActive status
     * @param status status of the template
     * @param domainId of the user template.
     * @param rootUser user id of the user template.
     * @return template
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE (template.type <>:type AND template.share = :share AND template.isActive = :isActive) AND (template.domainId = :domainId OR template.templateOwnerId = :rootUser) AND (template.name LIKE %:search% OR template.size LIKE %:search% OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    List<Template> listTemplateByCommunityAndSearchText(@Param("type") TemplateType type, @Param("share") Boolean share,@Param("isActive") Boolean isActive, @Param("domainId") Long domainId, @Param("rootUser") Long rootUser,@Param("search") String search);


    /**
     * Get all the template list in community.
     *
     * @param type of the template.
     * @param share template or not.
     * @param isActive status of the template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.share = :share AND template.isActive = :isActive")
    List<Template> listAllTemplateByCommunity(@Param("type") TemplateType type, @Param("share") Boolean share, @Param("isActive") Boolean isActive);

    /**
     * Get all the template list in community.
     *
     * @param type of the template.
     * @param share template or not.
     * @param isActive status of the template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template LEFT JOIN template.osCategory LEFT JOIN template.templateOwner LEFT JOIN template.osType  WHERE template.type <>:type AND template.share = :share AND template.isActive = :isActive AND (template.name LIKE %:search% OR template.size LIKE %:search% OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    List<Template> listAllTemplateByCommunityAndSearchText(@Param("type") TemplateType type, @Param("share") Boolean share, @Param("isActive") Boolean isActive, @Param("search") String search);

    /**
     * Find all the templates by owner id and isActive status.
     *
     * @param userId of the template.
     * @param isActive status.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template WHERE template.templateOwnerId = :userId AND template.isActive = :isActive")
    List<Template> findByTemplateOwnerIdAndIsActive(@Param("userId") Long userId, @Param("isActive") Boolean isActive);

    /**
     * Find all the templates by owner id and isActive status.
     *
     * @param userId of the template.
     * @param isActive status.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template WHERE template.templateOwnerId = :userId AND template.isActive = :isActive AND (template.name LIKE %:search% OR template.size LIKE %:search% OR template.templateOwner.userName LIKE %:search% "
            + "OR template.format LIKE %:search% OR template.createdDateTime LIKE %:search% OR template.status LIKE %:search%)")
    List<Template> findByTemplateOwnerIdAndIsActiveSearchText(@Param("userId") Long userId, @Param("isActive") Boolean isActive,@Param("search") String search);


    /**
     * Find all the templates by user id.
     *
     * @param type of the template.
     * @param status of the template.
     * @param isActive status of the template.
     * @param domainId of the template.
     * @param userId of the template.
     * @return templates.
     */
    @Query(value = "SELECT template FROM Template template WHERE (template.type <>:type AND template.status = :status AND template.share IS TRUE AND template.isActive = :isActive) AND (template.domainId = :domainId OR template.templateOwnerId = :userId)")
    List<Template> findByTemplateForUserId( @Param("type") TemplateType type,
        @Param("status") Status status, @Param("isActive") Boolean isActive, @Param("domainId") Long domainId, @Param("userId") Long userId);
}
