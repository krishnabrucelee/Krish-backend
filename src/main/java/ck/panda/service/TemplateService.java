package ck.panda.service;

import java.util.HashMap;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ck.panda.domain.entity.OsCategory;
import ck.panda.domain.entity.Template;
import ck.panda.domain.entity.Template.Status;
import ck.panda.domain.entity.Template.TemplateType;
import ck.panda.util.domain.CRUDService;
import ck.panda.util.domain.vo.PagingAndSorting;

/**
 * Service interface for Template entity.
 *
 * This service provides basic CRUD and essential api's for Template related business actions.
 */
@Service
public interface TemplateService extends CRUDService<Template> {

    /**
     * To get list of template from cloud stack server.
     *
     * @return template list from server
     * @throws Exception unhandled errors.
     */
    List<Template> findAllFromCSServer() throws Exception;

    /**
     * To get list of templates without system type from cloud stack server.
     *
     * @param id login user id
     * @return template list from server
     * @throws Exception unhandled errors.
     */
    List<Template> findByTemplate(Long id) throws Exception;

    /**
     * To get list of templates by filters.
     *
     * @param template template object.
     * @param id login user id
     * @return template list from server
     * @throws Exception unhandled errors.
     */
    List<Template> findTemplateByFilters(Template template, Long id) throws Exception;

    /**
     * To get list of ISO template by filters.
     *
     * @param templateIso template iso object.
     * @param id login user id
     * @return ISO template list from server
     * @throws Exception unhandled errors.
     */
    List<Template> findIsoByFilters(Template templateIso, Long id) throws Exception;

    /**
     * To get template by UUID.
     *
     * @param uuid UUID of the template.
     * @return template.
     * @throws Exception unhandled errors.
     */
    Template findByUUID(String uuid);

    /**
     * Soft delete for template.
     *
     * @param template object
     * @return template
     * @throws Exception unhandled errors.
     */
    Template softDelete(Template template) throws Exception;

    /**
     * Get all ISO templates.
     *
     * @param pagingAndSorting page
     * @return ISO templates
     * @throws Exception error
     */
    Page<Template> findAllIso(PagingAndSorting pagingAndSorting) throws Exception;

    /**
     * To get count of templates.
     *
     * @return template counts from server
     * @throws Exception unhandled errors.
     */
    HashMap<String, Integer> findTemplateCounts() throws Exception;

    /**
     * To get list of ISO template by OS category and type.
     *
     * @param osCategory template OS category.
     * @param type of the template.
     * @return template list from server.
     * @throws Exception unhandled errors.
     */
    List<Template> findByTemplateCategory(OsCategory osCategory, String type) throws Exception;

    /**
     * To get list of template by Community and Featured Templates.
     *
     * @param pagingAndSorting page
     * @param type template type
     * @param featured template
     * @param shared template
     * @return type of the template
     */
    Page<Template> findAllByType(PagingAndSorting pagingAndSorting, String type, Boolean featured, Boolean shared,Long userId)throws Exception;

    /**
     * To get list of template by Community and Featured Templates.
     *
     * @param pagingAndSorting page
     * @param type template type
     * @param featured template
     * @param shared template
     * @return type of the template
     */
    Page<Template> findAllByTypeAndSearchText(PagingAndSorting pagingAndSorting, String type, Boolean featured, Boolean shared,Long userId, String searchText)throws Exception;


    /**
     * To save the created template based on user id.
     *
     * @param template to be created.
     * @param UserId of the user.
     * @return template.
     * @throws Exception if error occurs.
     */
    Template save(Template template, Long UserId) throws Exception;

     /**
      * To update the template based on user id.
      *
      * @param template to be created.
      * @param userId of the user.
      * @return template.
      * @throws Exception if error occurs.
      */
    Template update(Template template, Long userId) throws Exception;

    /**
     * Find all template by type and user id.
     *
     * @param pagingAndSorting for pagination.
     * @param type of the template.
     * @param userId of the user.
     * @return template.
     * @throws Exception if error occurs.
     */
    Page<Template> findAllByUserIdAndType(PagingAndSorting pagingAndSorting, String type, Long userId) throws Exception;

    /**
     * Find all templates by isActive status and type.
     *
     * @param isActive status of the template.
     * @return template.
     * @throws Exception if error occurs.
     */
    List<Template> findAllTemplatesByIsActiveAndType(Boolean isActive) throws Exception;

    /**
     * Find all by domain id, is active and share
     *
     * @param type template type.
     * @param status template status.
     * @param isActive true/false.
     * @param userId user id.
     * @return template list.
     * @throws Exception if errors.
     */
    List<Template> findAllByUserIdIsActiveAndShare(TemplateType type, Status status, Boolean isActive, Long userId) throws Exception;

    /**
     * Find all the templates by type, isActive status and user Id.
     *
     * @param type of the template.
     * @param featured type of the template.
     * @param shared type of the template.
     * @param userId of the template.
     * @return templates.
     * @throws Exception if error occurs.
     */
    List<Template> findAllTemplateByType(String type, Boolean featured, Boolean shared,Long userId) throws Exception;

    Page<Template> findAllBySearchText(PagingAndSorting pagingAndSorting, String searchText) throws Exception;

    Page<Template> findAllIsoAndSearchText(PagingAndSorting pagingAndSorting, String searchText) throws Exception;

    Page<Template> findAllByUserIdAndTypeSearchText(PagingAndSorting pagingAndSorting, String type, Long userId,
            String searchText) throws Exception;

    List<Template> findAllTemplateByTypeAndSearchText(String type, Boolean featured, Boolean shared, Long userId,
            String searchText) throws Exception;

    List<Template> findAllTemplatesByIsActiveAndTypeSearchText(Boolean isActive, String searchText) throws Exception;
}
